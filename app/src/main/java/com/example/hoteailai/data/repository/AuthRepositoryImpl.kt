package com.example.hoteailai.data.repository

import com.example.hoteailai.domain.model.User
import com.example.hoteailai.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        var userValueListener: ValueEventListener? = null
        var currentUserRefPath: String? = null

        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
            val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
            
            // Remove previous listener if exists
            userValueListener?.let { listener ->
                currentUserRefPath?.let { path ->
                    firebaseDatabase.getReference(path).removeEventListener(listener)
                }
            }

            if (firebaseUser != null) {
                val path = "users/${firebaseUser.uid}"
                currentUserRefPath = path
                val userRef = firebaseDatabase.getReference(path)
                
                userValueListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        trySend(user)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Don't close flow on single user data error unless it's fatal
                    }
                }
                userRef.addValueEventListener(userValueListener!!)
            } else {
                trySend(null)
            }
        }
        
        auth.addAuthStateListener(authListener)
        
        awaitClose { 
            auth.removeAuthStateListener(authListener)
            userValueListener?.let { listener ->
                currentUserRefPath?.let { path ->
                    firebaseDatabase.getReference(path).removeEventListener(listener)
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Login failed")
            val userSnapshot = firebaseDatabase.getReference("users").child(firebaseUser.uid).get().await()
            val user = userSnapshot.getValue(User::class.java) ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Registration failed")
            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                membershipTier = "Regular"
            )
            firebaseDatabase.getReference("users").child(firebaseUser.uid).setValue(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Google Sign-In failed")
            
            val userRef = firebaseDatabase.getReference("users").child(firebaseUser.uid)
            val snapshot = userRef.get().await()
            
            val user = if (snapshot.exists()) {
                snapshot.getValue(User::class.java)!!
            } else {
                val newUser = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    profileImageUrl = firebaseUser.photoUrl?.toString() ?: "",
                    membershipTier = "Regular"
                )
                userRef.setValue(newUser).await()
                newUser
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
