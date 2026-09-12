package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.ZaMoversRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SignInScreen(
    viewModel: MainViewModel,
    onSignInSuccess: () -> Unit
) {
    var selectedAccountEmail by remember { mutableStateOf(ZaMoversRepository.OWNER_EMAIL) }
    var selectedAccountName by remember { mutableStateOf("Haq Baloch") }
    var isSigningIn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ZaNavyDark, Color(0xFF0F172A))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_google_sign_in"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step 7: Place ZA MOVERS logo at the top of the sign-in screen
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(ZaNavyDark)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.za_movers_logo_1789208975541),
                        contentDescription = "ZA MOVERS Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ZA MOVERS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = ZaNavyDark,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Travel • Connect • Explore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZaBluePrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Sign in to Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaNavyDark
                )
                Text(
                    text = "Fast, secure bus reservations across Balochistan & Pakistan.",
                    fontSize = 12.sp,
                    color = ZaTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Select Account Option
                Text(
                    text = "Choose Google Account:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZaTextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Owner Account Chip
                AccountSelectionRow(
                    email = ZaMoversRepository.OWNER_EMAIL,
                    name = "Haq Baloch (Owner)",
                    badge = "AUTHORIZED OWNER",
                    isSelected = selectedAccountEmail == ZaMoversRepository.OWNER_EMAIL,
                    onSelect = {
                        selectedAccountEmail = ZaMoversRepository.OWNER_EMAIL
                        selectedAccountName = "Haq Baloch"
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Customer Account Chip
                AccountSelectionRow(
                    email = "customer.traveler@gmail.com",
                    name = "Customer Traveler",
                    badge = "CUSTOMER",
                    isSelected = selectedAccountEmail == "customer.traveler@gmail.com",
                    onSelect = {
                        selectedAccountEmail = "customer.traveler@gmail.com"
                        selectedAccountName = "Customer Traveler"
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Step 7: "Continue with Google" as the only sign-in method
                Button(
                    onClick = {
                        isSigningIn = true
                        viewModel.signInWithGoogle(selectedAccountEmail, selectedAccountName)
                        onSignInSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("button_continue_with_google"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZaNavyDark)
                ) {
                    // Google icon simulation
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = ZaBluePrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Step 8: Security Notice
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ZaBlueLight.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Alert",
                            tint = ZaBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Automatic Login Security Alert email with device name, IP, and PKT timestamp is instantly sent to your Gmail address upon sign-in.",
                            fontSize = 11.sp,
                            color = ZaNavyDark,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSelectionRow(
    email: String,
    name: String,
    badge: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) ZaBlueLight.copy(alpha = 0.35f) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) ZaBluePrimary else ZaCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(selectedColor = ZaBluePrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaNavyDark
                    )
                    Text(
                        text = email,
                        fontSize = 11.sp,
                        color = ZaTextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = if (badge.contains("OWNER")) ZaNavyDark else Color(0xFFE2E8F0)
            ) {
                Text(
                    text = badge,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (badge.contains("OWNER")) ZaYellowAccent else ZaTextSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
