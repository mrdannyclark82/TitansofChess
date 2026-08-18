package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun TopResourceHeader(
    profile: UserProfileEntity?,
    onCastleClick: () -> Unit
) {
    val username = profile?.username ?: "Kasparov_99"
    val title = profile?.title ?: "Grandmaster"
    val gems = profile?.gems ?: 240
    val gold = profile?.gold ?: 12500
    val goldFormatted = if (gold >= 1000) String.format("%.1fk", gold / 1000f) else "$gold"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ElegantDarkBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Avatar + Name (Matching Design HTML)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCastleClick() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                    .testTag("btn_profile_header")
            ) {
                // Circular Avatar with gradient from #D0BCFF to #381E72 and border #49454F
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElegantPrimaryLavender, ElegantPrimaryDark)
                            )
                        )
                        .border(2.dp, ElegantBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = username.take(1).uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = ElegantDarkBg
                    )
                }

                Column {
                    Text(
                        text = title.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ElegantTextPrimary
                    )
                }
            }

            // Resources Capsule (Matching Design HTML)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ElegantCardBg)
                    .border(1.dp, ElegantBorder, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gems
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElegantCoral)
                    )
                    Text(
                        text = "$gems",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(14.dp)
                        .background(ElegantBorder)
                )

                // Gold
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElegantAccentLight)
                    )
                    Text(
                        text = goldFormatted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary
                    )
                }
            }
        }
    }
}
