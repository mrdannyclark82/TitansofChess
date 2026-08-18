package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavTabItem(
    val id: String,
    val label: String,
    val iconEmoji: String
)

@Composable
fun BottomNavigationMenu(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val navItems = listOf(
        NavTabItem("HOME", "Home", "🏠"),
        NavTabItem("DECK", "Cards", "🎴"),
        NavTabItem("TOURNAMENTS", "Leagues", "🏆"),
        NavTabItem("CLAN", "Clan", "👥"),
        NavTabItem("ARMOURY", "Store", "🛍️")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ElegantCardBg)
    ) {
        // Top border line #49454F
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ElegantBorder)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = (currentTab == item.id) ||
                        (currentTab == "QUESTS" && item.id == "HOME") ||
                        (currentTab == "LEADERBOARD" && item.id == "TOURNAMENTS")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTabSelected(item.id) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("nav_tab_${item.id.lowercase()}")
                ) {
                    if (isSelected) {
                        // Active Pill Indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(ElegantBorder)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.iconEmoji,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.iconEmoji,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) ElegantPrimaryLavender else ElegantTextSecondary.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
