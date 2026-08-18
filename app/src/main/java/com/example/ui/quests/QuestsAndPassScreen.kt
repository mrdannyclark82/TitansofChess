package com.example.ui.quests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun QuestsAndPassScreen(
    profile: UserProfileEntity?,
    quests: List<QuestEntity>,
    onClaimQuest: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("QUESTS") } // "QUESTS", "PASS"
    val tier = profile?.battlePassTier ?: 8
    val bpXp = profile?.battlePassXp ?: 650
    val bpNext = profile?.battlePassXpNext ?: 1000

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        // Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ElegantCardBg)
                .border(1.dp, ElegantBorder, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == "QUESTS") ElegantPrimaryLavender else Color.Transparent)
                    .clickable { selectedTab = "QUESTS" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TACTICAL QUESTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "QUESTS") ElegantPrimaryDark else ElegantTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == "PASS") ElegantPrimaryLavender else Color.Transparent)
                    .clickable { selectedTab = "PASS" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BATTLE PASS TIER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "PASS") ElegantPrimaryDark else ElegantTextSecondary
                )
            }
        }

        if (selectedTab == "QUESTS") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                item {
                    Text(
                        text = "DAILY REFRESH (Resets in 6h 14m)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = ElegantPrimaryLavender
                    )
                }

                val displayQuests = if (quests.isEmpty()) {
                    listOf(
                        QuestEntity("q1", "Capture 5 Knights in PvP", "Deploy pieces to neutralize enemy Knight Paladins", 3, 5, 500, 20),
                        QuestEntity("q2", "Play 3 Ranked Matches", "Complete 3 full tactical matches", 2, 3, 350, 10),
                        QuestEntity("q3", "Deal 10,000 Tower Damage", "Destroy guard towers or deliver Checkmate", 10000, 10000, 1200, 50, isClaimed = false),
                        QuestEntity("q4", "Donate 2 Cards to Clan", "Assist clan comrades with piece shards", 1, 2, 200, 5)
                    )
                } else quests

                items(displayQuests) { quest ->
                    QuestItemCard(quest = quest, onClaim = { onClaimQuest(quest.id) })
                }
            }
        } else {
            BattlePassView(tier = tier, currentXp = bpXp, nextXp = bpNext)
        }
    }
}

@Composable
private fun QuestItemCard(
    quest: QuestEntity,
    onClaim: () -> Unit
) {
    val isComplete = quest.progress >= quest.target
    val progressFrac = (quest.progress.toFloat() / quest.target.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isComplete && !quest.isClaimed) ElegantEmerald else ElegantBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quest_item_${quest.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quest.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = quest.description,
                        fontSize = 10.sp,
                        color = ElegantTextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🪙 ${quest.rewardGold}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantAccentLight)
                    Text(text = "💎 ${quest.rewardGems}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantCoral)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Progress bar
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Progress", fontSize = 9.sp, color = ElegantTextSecondary)
                        Text(text = "${quest.progress}/${quest.target}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { progressFrac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = if (isComplete) ElegantEmerald else ElegantPrimaryLavender,
                        trackColor = ElegantDarkBg
                    )
                }

                // Claim Button
                Button(
                    onClick = onClaim,
                    enabled = isComplete && !quest.isClaimed,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (quest.isClaimed) Color.Transparent else if (isComplete) ElegantEmerald else ElegantBorder,
                        contentColor = if (isComplete && !quest.isClaimed) ElegantPrimaryDark else ElegantTextSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_claim_quest_${quest.id}")
                ) {
                    Text(
                        text = if (quest.isClaimed) "CLAIMED" else if (isComplete) "CLAIM" else "LOCKED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun BattlePassView(
    tier: Int,
    currentXp: Int,
    nextXp: Int
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Battle Pass Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimaryLavender),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "👑 ROYAL BATTLE PASS: SEASON 4",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = ElegantPrimaryLavender
                            )
                            Text(
                                text = "Current Tier: $tier / 20",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(ElegantPrimaryDark)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = "PREMIUM ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val xpFrac = (currentXp.toFloat() / nextXp.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { xpFrac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = ElegantPrimaryLavender,
                        trackColor = ElegantDarkBg
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currentXp / $nextXp Season XP",
                        fontSize = 10.sp,
                        color = ElegantTextSecondary
                    )
                }
            }
        }

        // Tiers list
        val tiersList = listOf(
            Triple(1, "500 Gold", "🥈 Silver Chest + 50 Gems"),
            Triple(2, "50 Gems", "🎴 Valkyrie Queen x2"),
            Triple(3, "1,000 Gold", "🔮 Magical Mystery Chest"),
            Triple(4, "100 Gems", "🐎 Astral Dread Knight Skin (Cosmetic)"),
            Triple(5, "2,000 Gold", "🏆 Royal Crown Chest (Skin Shards)"),
            Triple(8, "3,000 Gold + 150 Gems", "🌌 Celestial Colosseum Arena (Current Tier)"),
            Triple(10, "5,000 Gold", "🛡️ Molten Magma Magnus Hero Skin (Legendary)"),
            Triple(15, "7,500 Gold + 250 Gems", "⚔️ Solar Seraph Queen Mythic Crate"),
            Triple(20, "10,000 Gold + 500 Gems", "👑 Glacial Frost King Kasparov Mythic Hero Skin")
        )

        items(tiersList) { (tNum, freeReward, premiumReward) ->
            val isUnlocked = tier >= tNum
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isUnlocked) ElegantCardBg else ElegantDarkBg)
                    .border(
                        1.dp,
                        if (isUnlocked) ElegantPrimaryLavender.copy(alpha = 0.6f) else ElegantBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) ElegantPrimaryLavender else ElegantBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$tNum",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = if (isUnlocked) ElegantPrimaryDark else ElegantTextPrimary
                        )
                    }

                    Column {
                        Text(text = "Free: $freeReward", fontSize = 11.sp, color = ElegantTextPrimary)
                        Text(text = "Premium: $premiumReward", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
                    }
                }

                Text(
                    text = if (isUnlocked) "UNLOCKED ✓" else "LOCKED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) ElegantEmerald else ElegantTextSecondary
                )
            }
        }
    }
}
