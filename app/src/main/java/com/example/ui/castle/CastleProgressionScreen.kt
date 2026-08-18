package com.example.ui.castle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun CastleProgressionScreen(
    profile: UserProfileEntity?,
    onUpgradeCastle: () -> Unit,
    onBack: () -> Unit
) {
    val castleLevel = profile?.castleLevel ?: 4
    val gold = profile?.gold ?: 12500
    val upgradeCost = castleLevel * 2500
    val canUpgrade = gold >= upgradeCost

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        // Top Back Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = ElegantTextPrimary)
            }
            Text(
                text = "CASTLE FORTRESS PROGRESSION",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp,
                color = ElegantPrimaryLavender
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Castle Hall Hero Banner
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ElegantPrimaryLavender),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(ElegantPrimaryLavender, ElegantPrimaryDark)
                                    )
                                )
                                .border(2.dp, ElegantBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏰", fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "CASTLE HALL LEVEL $castleLevel",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = ElegantTextPrimary
                        )

                        Text(
                            text = "Fortress HP +${castleLevel * 500} • Tower Damage +${castleLevel * 15}%",
                            fontSize = 11.sp,
                            color = ElegantPrimaryLavender
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onUpgradeCastle,
                            enabled = canUpgrade,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canUpgrade) ElegantPrimaryLavender else ElegantBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_upgrade_castle")
                        ) {
                            Text(
                                text = if (canUpgrade) "UPGRADE CASTLE ($upgradeCost 🪙)" else "NEED $upgradeCost GOLD",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (canUpgrade) ElegantPrimaryDark else ElegantTextSecondary
                            )
                        }
                    }
                }
            }

            // Defenses & Productivity Modules
            item {
                Text(
                    text = "FORTRESS DEFENSIVE STRUCTURES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ElegantTextSecondary
                )
            }

            val defenses = listOf(
                Triple("🏹 Royal Guard Archer Tower", "Level $castleLevel", "+${castleLevel * 45} DPS against diagonal air/ground pieces"),
                Triple("🛡️ Obsidian King Moat", "Level $castleLevel", "Slows enemy pieces leaping across the perimeter by 20%"),
                Triple("⚡ Ley-Line Elixir Pump", "Level ${castleLevel + 1}", "Produces +1 Elixir every 25 seconds in combat")
            )

            items(defenses.size) { idx ->
                val (title, lvl, desc) = defenses[idx]
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElegantTextPrimary)
                            Text(text = desc, fontSize = 10.sp, color = ElegantTextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElegantDarkBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = lvl, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
                        }
                    }
                }
            }
        }
    }
}
