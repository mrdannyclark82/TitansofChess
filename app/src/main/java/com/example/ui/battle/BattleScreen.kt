package com.example.ui.battle

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CardModel
import com.example.data.model.GameCatalog
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BattleViewModel
import java.util.Locale
import com.example.ui.viewmodel.BoardCell

@Composable
fun BattleScreen(
    battleViewModel: BattleViewModel,
    opponentName: String,
    onExitBattle: () -> Unit
) {
    val battleState by battleViewModel.battleState.collectAsStateWithLifecycle()
    val elixir by battleViewModel.currentElixir.collectAsStateWithLifecycle()
    val heroEnergy by battleViewModel.heroEnergy.collectAsStateWithLifecycle()
    val playerTowers by battleViewModel.playerTowers.collectAsStateWithLifecycle()
    val enemyTowers by battleViewModel.enemyTowers.collectAsStateWithLifecycle()
    val boardGrid by battleViewModel.boardGrid.collectAsStateWithLifecycle()
    val battleDeck by battleViewModel.battleDeck.collectAsStateWithLifecycle()
    val selectedHandCardId by battleViewModel.selectedHandCardId.collectAsStateWithLifecycle()
    val matchTimerSeconds by battleViewModel.matchTimerSeconds.collectAsStateWithLifecycle()

    val arena = GameCatalog.ARENA_SKINS.find { it.id == battleState.arenaId }
        ?: GameCatalog.ARENA_SKINS.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar: Opponent Info, Match Timer, Surrender
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ElegantCardBg)
                .border(1.dp, ElegantBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Opponent details
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ElegantCoral)
                        .border(1.dp, ElegantBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 16.sp)
                }
                Column {
                    Text(text = opponentName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElegantTextPrimary)
                    Text(text = "🏆 ${battleState.opponentTrophies} • AI Grandmaster", fontSize = 9.sp, color = ElegantTextSecondary)
                }
            }

            // Match Timer (e.g. 2:45)
            val minutes = matchTimerSeconds / 60
            val seconds = matchTimerSeconds % 60
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ElegantDarkBg)
                    .border(1.dp, ElegantBorder, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = if (matchTimerSeconds <= 30) ElegantCoral else ElegantPrimaryLavender
                )
            }

            // Exit / Surrender
            IconButton(
                onClick = {
                    battleViewModel.endBattle(isVictory = false)
                    onExitBattle()
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Surrender", tint = ElegantTextSecondary)
            }
        }

        // 2. Enemy Towers Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TowerStatusBadge(name = "Left Turret", hp = enemyTowers.leftTowerHp, maxHp = 1800, isEnemy = true, isHit = enemyTowers.isLeftHit)
            TowerStatusBadge(name = "Enemy King", hp = enemyTowers.kingTowerHp, maxHp = 3200, isEnemy = true, isKing = true, isHit = enemyTowers.isKingHit)
            TowerStatusBadge(name = "Right Turret", hp = enemyTowers.rightTowerHp, maxHp = 1800, isEnemy = true, isHit = enemyTowers.isRightHit)
        }

        // 3. Tactical 8x8 Chess Royale Grid Arena
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(arena.darkTileHex))
                .border(2.dp, Color(arena.borderHex), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0..7) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        for (col in 0..7) {
                            val cell = boardGrid.find { it.row == row && it.col == col }
                            val isLight = (row + col) % 2 == 0
                            val tileColor = if (isLight) Color(arena.lightTileHex) else Color(arena.darkTileHex)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(tileColor)
                                    .border(0.5.dp, ElegantBorder.copy(alpha = 0.3f))
                                    .clickable {
                                        battleViewModel.onCellClicked(row, col)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (cell?.pieceSymbol != null) {
                                    TacticalBattlePieceToken(
                                        pieceId = cell.pieceId,
                                        pieceSymbol = cell.pieceSymbol,
                                        isPlayerPiece = cell.isPlayerPiece,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                if (cell?.isHighlight == true) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(ElegantPrimaryLavender.copy(alpha = 0.7f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Player Towers Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TowerStatusBadge(name = "Left Guard", hp = playerTowers.leftTowerHp, maxHp = 2000, isEnemy = false, isHit = playerTowers.isLeftHit)
            TowerStatusBadge(name = "Your King", hp = playerTowers.kingTowerHp, maxHp = 3500, isEnemy = false, isKing = true, isHit = playerTowers.isKingHit)
            TowerStatusBadge(name = "Right Guard", hp = playerTowers.rightTowerHp, maxHp = 2000, isEnemy = false, isHit = playerTowers.isRightHit)
        }

        // 5. Elixir Bar & Hero Super Ability
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Elixir Bar Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ElegantPrimaryDark)
                            .border(1.dp, ElegantPrimaryLavender, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 12.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Elixir Gauge", fontSize = 9.sp, color = ElegantTextSecondary)
                            Text(text = "${elixir.toInt()}/10", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ElegantAccentLight)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { (elixir / 10f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = ElegantPrimaryLavender,
                            trackColor = ElegantCardBg
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Hero Super Ability Button
                Button(
                    onClick = { battleViewModel.triggerHeroAbility() },
                    enabled = heroEnergy >= 100,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (heroEnergy >= 100) ElegantPrimaryLavender else ElegantBorder,
                        contentColor = if (heroEnergy >= 100) ElegantPrimaryDark else ElegantTextSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_hero_ability")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Hero Power", modifier = Modifier.size(16.dp))
                        Text(
                            text = if (heroEnergy >= 100) "HERO SUPER" else "$heroEnergy%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 6. Bottom Battle Hand Deck (4 ready cards)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ElegantCardBg)
                .border(1.dp, ElegantBorder, RoundedCornerShape(16.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            battleDeck.forEach { cardId ->
                val card = GameCatalog.getCard(cardId)
                val isSelected = selectedHandCardId == cardId
                val canAfford = elixir >= card.elixirCost

                BattleCardHandItem(
                    card = card,
                    isSelected = isSelected,
                    canAfford = canAfford,
                    onClick = { battleViewModel.selectCardToPlay(cardId) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Match Conclusion Modal
    if (battleState.isGameOver) {
        BattleResultDialog(
            isVictory = battleState.isVictory,
            trophiesDelta = if (battleState.isVictory) +30 else -15,
            goldReward = if (battleState.isVictory) 250 else 50,
            onContinue = {
                battleViewModel.dismissGameOver()
                onExitBattle()
            }
        )
    }
}

@Composable
private fun TowerStatusBadge(
    name: String,
    hp: Int,
    maxHp: Int,
    isEnemy: Boolean,
    isKing: Boolean = false,
    isHit: Boolean = false
) {
    val shakeOffset by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isHit) 4.dp else 0.dp,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioHighBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessHigh
        ),
        label = "tower_shake"
    )

    val hpFrac = (hp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = shakeOffset)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isHit) ElegantCoral.copy(alpha = 0.3f) else ElegantCardBg)
            .border(1.dp, if (isKing) ElegantPrimaryLavender.copy(alpha = 0.5f) else ElegantBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = (if (isKing) "👑 " else "🏰 ") + name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isEnemy) ElegantCoral else ElegantTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { hpFrac },
            modifier = Modifier
                .width(60.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50)),
            color = if (hpFrac > 0.3f) (if (isEnemy) ElegantCoral else ElegantEmerald) else ElegantCoral,
            trackColor = ElegantDarkBg
        )
        Text(text = "$hp HP", fontSize = 8.sp, color = ElegantTextSecondary)
    }
}

@Composable
private fun BattleCardHandItem(
    card: CardModel,
    isSelected: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColor = HolographicPalettes.getGlowColor(card.rarity)
    val foilColors = HolographicPalettes.getFoilColors(card.rarity)

    val animatedYOffset by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isSelected) (-10).dp else 0.dp,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "hand_card_pop"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .offset(y = animatedYOffset)
            .holographicPopOnTouch(
                onClick = if (canAfford) onClick else null,
                scaleUp = 1.08f,
                elevationDp = if (isSelected) 14.dp else 4.dp
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ElegantPrimaryDark else ElegantDarkBg)
            .then(
                if (canAfford && (isSelected || card.rarity in listOf("MYTHIC", "LEGENDARY"))) {
                    Modifier.holographicFoil(rarity = card.rarity, intensity = if (isSelected) 0.9f else 0.5f)
                } else Modifier
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = if (isSelected) Brush.linearGradient(foilColors) else if (canAfford) Brush.linearGradient(listOf(rarityColor, ElegantBorder)) else Brush.linearGradient(listOf(ElegantBorder.copy(alpha = 0.4f), ElegantBorder.copy(alpha = 0.4f))),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp)
            .testTag("battle_hand_card_${card.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (canAfford) ElegantPrimaryLavender else ElegantBorder),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${card.elixirCost}",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    color = if (canAfford) ElegantPrimaryDark else ElegantTextPrimary
                )
            }

            if (isSelected) {
                Text(text = "▲", fontSize = 8.sp, color = ElegantEmerald, fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        if (canAfford) {
            HolographicFloatingPiece(
                symbolChar = card.symbolChar,
                fontSize = 24.sp,
                rarity = card.rarity
            )
        } else {
            Text(
                text = card.symbolChar,
                fontSize = 24.sp,
                color = ElegantTextSecondary.copy(alpha = 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = card.name.take(7),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = if (canAfford) ElegantTextPrimary else ElegantTextSecondary.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun BattleResultDialog(
    isVictory: Boolean,
    trophiesDelta: Int,
    goldReward: Int,
    onContinue: () -> Unit
) {
    Dialog(onDismissRequest = onContinue) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ElegantCardBg,
            border = androidx.compose.foundation.BorderStroke(2.dp, if (isVictory) ElegantPrimaryLavender else ElegantCoral),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_battle_result")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isVictory) "👑 CHECKMATE! VICTORY" else "DEFEAT",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = if (isVictory) ElegantPrimaryLavender else ElegantCoral
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (isVictory) listOf(ElegantPrimaryLavender, ElegantPrimaryDark)
                                else listOf(ElegantCoral, Color(0xFF680016))
                            )
                        )
                        .border(2.dp, ElegantBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isVictory) "🏆" else "💀", fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${if (trophiesDelta > 0) "+" else ""}$trophiesDelta 🏆",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = ElegantPrimaryLavender
                        )
                        Text(text = "Trophies", fontSize = 10.sp, color = ElegantTextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+$goldReward 🪙",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = ElegantAccentLight
                        )
                        Text(text = "Gold Spoils", fontSize = 10.sp, color = ElegantTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVictory) ElegantPrimaryLavender else ElegantCardBgElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_continue_post_battle")
                ) {
                    Text(
                        text = "CONTINUE",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (isVictory) ElegantPrimaryDark else ElegantTextPrimary
                    )
                }
            }
        }
    }
}
