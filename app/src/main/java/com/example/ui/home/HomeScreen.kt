package com.example.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.ChestSlotEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.ChestReward
import com.example.data.model.GameCatalog
import com.example.data.model.MatchedOpponent
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: UserProfileEntity?,
    chestSlots: List<ChestSlotEntity>,
    onStartBattle: () -> Unit,
    onOpenChest: (Int) -> Unit,
    onNavigateTab: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Featured Arena Showcase Card (Exact Design HTML Structure)
        FeaturedArenaHeroCard(
            profile = profile,
            onArenaClick = { onNavigateTab("ARMOURY") }
        )

        // 2. Daily Quest Widget (Exact Design HTML Structure)
        DailyQuestBanner(
            onQuestClick = { onNavigateTab("QUESTS") }
        )

        // 3. Battle Now | Ranked Primary Action Button (Exact Design HTML)
        Button(
            onClick = onStartBattle,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElegantPrimaryLavender,
                contentColor = ElegantPrimaryDark
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .testTag("btn_battle_now"),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "BATTLE NOW",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = ElegantPrimaryDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "| Ranked (🏆 ${profile?.trophies ?: 3840})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElegantPrimaryDark.copy(alpha = 0.85f)
                )
            }
        }

        // 4. Chest Slots Row (Clash Royale Style Timed Progression)
        ChestSlotsSection(
            chestSlots = chestSlots,
            onOpenChest = onOpenChest
        )

        // 5. Quick Shortcuts / Trophy Road Progress
        TrophyRoadShortcutCard(
            profile = profile,
            onClick = { onNavigateTab("TOURNAMENTS") }
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun FeaturedArenaHeroCard(
    profile: UserProfileEntity?,
    onArenaClick: () -> Unit
) {
    val arena = GameCatalog.ARENA_SKINS.find { it.id == profile?.selectedArenaId }
        ?: GameCatalog.ARENA_SKINS.first()
    val foilColors = HolographicPalettes.MythicPrism

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(foilColors)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .holographicPopOnTouch(onClick = onArenaClick, scaleUp = 1.03f, elevationDp = 16.dp)
            .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = ElegantPrimaryLavender.copy(alpha = 0.3f), spotColor = ElegantPrimaryLavender.copy(alpha = 0.5f))
            .testTag("card_featured_arena")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Prismatic Badge & Arena Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrismaticRarityBadge(rarity = "LEGENDARY ARENA")

                // Arena Title
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ROYAL REALM",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = arena.name.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ElegantPrimaryLavender
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Center Holographic Piece Frame with 3D Pop Levitation & Sparkles
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(arena.darkTileHex), ElegantDarkBg)
                        )
                    )
                    .holographicFoil(rarity = "LEGENDARY", intensity = 0.85f)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(HolographicPalettes.LegendaryGold),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color(0xFFFFD700), spotColor = Color(0xFFFFD700)),
                contentAlignment = Alignment.Center
            ) {
                HolographicSparklesOverlay(sparkleCount = 4, color = Color.White)

                HolographicFloatingPiece(
                    symbolChar = "hero_kasparov",
                    fontSize = 68.sp,
                    rarity = "MYTHIC"
                )

                // Bottom label inside frame
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(ElegantPrimaryLavender.copy(alpha = 0.2f))
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "THE GRANDMASTER",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = ElegantPrimaryLavender
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats 3-Column Grid (Atk, Def, Lvl)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatColumn(label = "ATK", value = "1.2k", modifier = Modifier.weight(1f))
                StatColumn(label = "DEF", value = "850", modifier = Modifier.weight(1f))
                StatColumn(label = "LVL", value = "12", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ElegantDarkBg.copy(alpha = 0.6f))
            .border(1.dp, ElegantBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = ElegantTextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ElegantPrimaryLavender
        )
    }
}

@Composable
private fun DailyQuestBanner(
    onQuestClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ElegantCardBg)
            .border(1.dp, ElegantBorder, RoundedCornerShape(18.dp))
            .clickable { onQuestClick() }
            .padding(14.dp)
            .testTag("card_daily_quest"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DAILY QUEST",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = ElegantPrimaryLavender
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Capture 5 Knights in PvP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ElegantTextPrimary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mini Progress Bar
            Box(
                modifier = Modifier
                    .width(76.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(ElegantDarkBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.6f)
                        .background(ElegantPrimaryLavender)
                )
            }

            Text(
                text = "3/5",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantTextPrimary
            )
        }
    }
}

@Composable
private fun ChestSlotsSection(
    chestSlots: List<ChestSlotEntity>,
    onOpenChest: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CHEST REWARDS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = ElegantTextSecondary
            )
            Text(
                text = "Tap to unlock",
                fontSize = 10.sp,
                color = ElegantPrimaryLavender
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val displaySlots = if (chestSlots.isEmpty()) {
                listOf(
                    ChestSlotEntity(0, "GOLDEN", isReady = true),
                    ChestSlotEntity(1, "SILVER", isUnlocking = true),
                    ChestSlotEntity(2, "MAGICAL"),
                    ChestSlotEntity(3, "EMPTY", isEmpty = true)
                )
            } else {
                chestSlots
            }

            displaySlots.take(4).forEach { slot ->
                ChestSlotItem(
                    slot = slot,
                    onClick = { onOpenChest(slot.slotIndex) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ChestSlotItem(
    slot: ChestSlotEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chestIcon = when (slot.chestType) {
        "SILVER" -> "🥈"
        "GOLDEN" -> "🏆"
        "MAGICAL" -> "🔮"
        else -> if (slot.isEmpty) "➕" else "📦"
    }

    val statusText = when {
        slot.isReady -> "OPEN!"
        slot.isUnlocking -> "1h 42m"
        slot.isEmpty -> "Empty"
        else -> "Locked"
    }

    val statusColor = when {
        slot.isReady -> ElegantEmerald
        slot.isUnlocking -> ElegantPrimaryLavender
        slot.isEmpty -> ElegantTextMuted
        else -> ElegantTextSecondary
    }

    val foilColors = when (slot.chestType) {
        "MAGICAL" -> HolographicPalettes.MythicPrism
        "GOLDEN" -> HolographicPalettes.LegendaryGold
        "SILVER" -> HolographicPalettes.CommonSilver
        else -> listOf(ElegantBorder, ElegantCardBg)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .holographicPopOnTouch(onClick = onClick, scaleUp = 1.08f, elevationDp = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ElegantCardBg)
            .border(
                width = if (slot.isReady) 1.5.dp else 1.dp,
                brush = if (slot.isReady) Brush.linearGradient(foilColors) else Brush.linearGradient(listOf(ElegantBorder, ElegantBorder)),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("chest_slot_${slot.slotIndex}")
    ) {
        Text(text = chestIcon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (slot.isEmpty) "FREE" else slot.chestType.take(6),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = ElegantTextPrimary
        )
        Text(
            text = statusText,
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            color = statusColor
        )
    }
}

@Composable
private fun TrophyRoadShortcutCard(
    profile: UserProfileEntity?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ElegantCardBg)
            .border(1.dp, ElegantBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("card_trophy_road_shortcut"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ElegantPrimaryDark)
                    .border(1.dp, ElegantPrimaryLavender, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👑", fontSize = 18.sp)
            }

            Column {
                Text(
                    text = "TROPHY ROAD & ESPORTS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElegantTextPrimary
                )
                Text(
                    text = "Next milestone: 4,000 Trophies (Legendary Chest)",
                    fontSize = 10.sp,
                    color = ElegantTextSecondary
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "View",
            tint = ElegantPrimaryLavender
        )
    }
}

@Composable
fun MatchmakingRadarModal(
    timeSeconds: Int,
    matchedOpponent: MatchedOpponent?,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_pulse"
    )

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = ElegantCardBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElegantPrimaryLavender),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_matchmaking")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SEARCHING FOR OPPONENT",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    color = ElegantPrimaryLavender
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ElegantPrimaryDark)
                        .border(2.dp, ElegantPrimaryLavender, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚔️",
                        fontSize = (32 * radarPulse).sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (matchedOpponent != null) {
                    Text(
                        text = "OPPONENT FOUND!",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = ElegantEmerald
                    )
                    Text(
                        text = "${matchedOpponent.name} (🏆 ${matchedOpponent.trophies})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = "Loading Arena...",
                        fontSize = 11.sp,
                        color = ElegantTextSecondary
                    )
                } else {
                    Text(
                        text = "Time elapsed: ${timeSeconds}s",
                        fontSize = 12.sp,
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = "Ranked Matchmaking [Global Server]",
                        fontSize = 10.sp,
                        color = ElegantTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElegantCoral),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_cancel_matchmaking")
                ) {
                    Text(text = "CANCEL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ChestRewardModal(
    result: ChestReward,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = ElegantCardBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElegantPrimaryLavender),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_chest_rewards")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${result.chestName.uppercase()} UNLOCKED!",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = ElegantPrimaryLavender
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    Text(text = "🏆", fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🪙 +${result.goldReward}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ElegantAccentLight)
                        Text(text = "Gold", fontSize = 10.sp, color = ElegantTextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💎 +${result.gemsReward}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ElegantCoral)
                        Text(text = "Gems", fontSize = 10.sp, color = ElegantTextSecondary)
                    }
                }

                if (result.skinRewarded != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ElegantDarkBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(result.skinRewarded.accentColorHex)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = result.skinRewarded.symbolChar, fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "✨ UNLOCKED: ${result.skinRewarded.name}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color(result.skinRewarded.accentColorHex)
                                )
                                Text(
                                    text = "${result.skinRewarded.rarity} Cosmetic Skin",
                                    fontSize = 9.5.sp,
                                    color = ElegantTextSecondary
                                )
                            }
                        }
                    }
                }

                if (result.cardsRewarded.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkBg)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        result.cardsRewarded.forEach { (cardId, count) ->
                            val card = GameCatalog.getCard(cardId)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${card.symbolChar} ${card.name}", fontSize = 11.sp, color = ElegantTextPrimary)
                                Text(text = "+$count Cards", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPrimaryLavender),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_claim_chest_rewards")
                ) {
                    Text(text = "COLLECT ALL", fontWeight = FontWeight.Black, fontSize = 13.sp, color = ElegantPrimaryDark)
                }
            }
        }
    }
}
