package com.example.hoteailai.presentation.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.presentation.components.GoldButton
import com.example.hoteailai.presentation.components.HotelioTextField
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.bookingId != null) {
        onConfirmClick(state.bookingId!!)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Checkout", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }
                    AsyncImage(
                        model = null,
                        contentDescription = "User",
                        modifier = Modifier.size(32.dp).background(Color.Gray, shape = RoundedCornerShape(16.dp))
                    )
                }
            )
        },
        bottomBar = {
            ConfirmPayBottomBar(
                onClick = viewModel::confirmBooking,
                isLoading = state.isBookingInProgress
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            StepProgress()

            Spacer(modifier = Modifier.height(24.dp))

            state.hotel?.let { hotel ->
                HotelSummaryCard(
                    hotel = hotel,
                    checkIn = state.checkInDate,
                    duration = state.durationDays,
                    guests = state.guestCount
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Payment Method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            PaymentMethodSelector(
                selectedMethod = state.paymentMethod,
                onSelect = viewModel::onPaymentMethodChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            CardDetailsForm(
                name = state.cardholderName,
                number = state.cardNumber,
                expiry = state.expiryDate,
                cvv = state.cvv,
                onValueChange = viewModel::onCardDetailsChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            PriceBreakdown(
                pricePerNight = state.hotel?.pricePerNight ?: 0.0,
                nights = state.durationDays
            )

            Spacer(modifier = Modifier.height(24.dp))

            TrustBadges()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StepProgress() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepItem(number = "1", label = "Details", isCompleted = true)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = SecondaryGold)
        StepItem(number = "2", label = "Payment", isCurrent = true)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = Color.LightGray)
        StepItem(number = "3", label = "Confirm", isFuture = true)
    }
}

@Composable
fun StepItem(number: String, label: String, isCompleted: Boolean = false, isCurrent: Boolean = false, isFuture: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isCompleted || isCurrent) SecondaryGold else Color.White,
            border = if (isFuture) BorderStroke(1.dp, Color.LightGray) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                } else {
                    Text(text = number, color = if (isCurrent) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = if (isCurrent) PrimaryBlue else Color.Gray)
    }
}

@Composable
fun HotelSummaryCard(hotel: Hotel, checkIn: Long, duration: Int, guests: Int) {
    val sdf = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
    val checkInStr = sdf.format(java.util.Date(checkIn))
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = checkIn
    cal.add(java.util.Calendar.DAY_OF_YEAR, duration)
    val checkOutStr = sdf.format(cal.time)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = hotel.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "LUXURY STAY", color = SecondaryGold, style = MaterialTheme.typography.labelSmall)
                    Text(text = "\u2B50 ${hotel.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text(text = hotel.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoIconItem(Icons.Default.DateRange, "Dates", "$checkInStr - $checkOutStr")
                    InfoIconItem(Icons.Default.Group, "Guests", "$guests Guests")
                }
            }
        }
    }
}

@Composable
fun InfoIconItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 8.sp)
            Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 10.sp)
        }
    }
}

@Composable
fun PaymentMethodSelector(selectedMethod: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PaymentOption(name = "Credit or Debit Card", subtitle = "Visa, Mastercard, Amex", icon = Icons.Default.CreditCard, selected = selectedMethod == "Credit Card", onClick = { onSelect("Credit Card") })
        PaymentOption(name = "Apple Pay", subtitle = "Fast & Secure Payment", icon = Icons.Default.Apps, selected = selectedMethod == "Apple Pay", onClick = { onSelect("Apple Pay") })
        PaymentOption(name = "Luxury Points", subtitle = "Available: 12,450 pts", icon = Icons.Default.Stars, selected = selectedMethod == "Points", onClick = { onSelect("Points") })
    }
}

@Composable
fun PaymentOption(name: String, subtitle: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = if (selected) BorderStroke(2.dp, SecondaryGold) else null,
        shadowElevation = if (selected) 4.dp else 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(8.dp), color = BackgroundLight) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = SecondaryGold))
        }
    }
}

@Composable
fun CardDetailsForm(name: String, number: String, expiry: String, cvv: String, onValueChange: (String, String, String, String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Cardholder Name", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        HotelioTextField(value = name, onValueChange = { onValueChange(it, number, expiry, cvv) }, label = "Johnathan Sterling")
        Text(text = "Card Number", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        HotelioTextField(value = number, onValueChange = { onValueChange(name, it, expiry, cvv) }, label = "**** **** **** 4455", trailingIcon = { Icon(Icons.Default.CreditCard, null) })
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Expiry Date", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                HotelioTextField(value = expiry, onValueChange = { onValueChange(name, number, it, cvv) }, label = "MM / YY")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "CVV", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                HotelioTextField(value = cvv, onValueChange = { onValueChange(name, number, expiry, it) }, label = "***")
            }
        }
    }
}

@Composable
fun PriceBreakdown(pricePerNight: Double, nights: Int) {
    val subtotal = pricePerNight * nights
    val serviceFee = 45.0
    val taxes = subtotal * 0.1
    val total = subtotal + serviceFee + taxes

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = BackgroundLight
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PriceRow("$nights Nights Stay", "$${String.format("%,.2f", subtotal)}")
            PriceRow("Service Fee", "$$serviceFee")
            PriceRow("Taxes & Duties", "$${String.format("%,.2f", taxes)}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Price", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text(text = "$${String.format("%,.2f", total)}", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium, color = PrimaryBlue)
    }
}

@Composable
fun TrustBadges() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        BadgeItem(Icons.Default.Lock, "SSL ENCRYPTED")
        BadgeItem(Icons.Default.Security, "PCI COMPLIANT")
        BadgeItem(Icons.Default.VerifiedUser, "SAFE CHECKOUT")
    }
}

@Composable
fun BadgeItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 8.sp, color = Color.Gray)
    }
}

@Composable
fun ConfirmPayBottomBar(onClick: () -> Unit, isLoading: Boolean) {
    Surface(shadowElevation = 8.dp, color = PrimaryBlue) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            GoldButton(
                text = if (isLoading) "Processing..." else "Confirm & Pay \u2192",
                onClick = onClick,
                enabled = !isLoading
            )
        }
    }
}
