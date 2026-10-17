package com.example.ui.battle

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ElegantCoral)
                )
                Column {
                    Text(opponentName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${battleState.opponentTrophies} 🏆", color = ElegantPrimaryLavender, fontSize = 10.sp)
                }
            }

            val mins = matchTimerSeconds / 60
            val secs = matchTimerSeconds % 60
            Text(
                text = String.format(Locale.US, "%d:%02d", mins, secs),
                color = if (matchTimerSeconds < 30) ElegantCoral else Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )

            TextButton(onClick = { battleViewModel.exitBattle(); onExitBattle() }) {
                Text("SURRENDER", color = ElegantCoral, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TowerStatusBadge(name = "Left Guard", hp = enemyTowers.leftTowerHp, maxHp = 2000, isEnemy = true, isHit = enemyTowers.isLeftHit)
            TowerStatusBadge(name = "Enemy King", hp = enemyTowers.kingTowerHp, maxHp = 3500, isEnemy = true, isKing = true, isHit = enemyTowers.isKingHit)
            TowerStatusBadge(name = "Right Guard", hp = enemyTowers.rightTowerHp, maxHp = 2000, isEnemy = true, isHit = enemyTowers.isRightHit)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(arena.borderHex))
                .border(3.dp, Color(arena.borderHex), RoundedCornerShape(12.dp))
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
                                if (cell?.pieceSymbol != null || cell?.pieceId != null) {
                                    val pieceType = mapToChessPieceType(cell.pieceId, cell.pieceSymbol)
                                    val team = if (cell.isPlayerPiece) PieceTeam.BLUE else PieceTeam.RED
                                    CharacterVisualView(
                                        pieceType = pieceType,
                                        team = team,
                                        level = cell.level,
                                        isAnimated = true,
                                        showHpBar = pieceType == ChessPieceType.KING || pieceType == ChessPieceType.ROOK,
                                        hpProgress = 1f,
                                        size = 36.dp,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TowerStatusBadge(name = "Left Guard", hp = playerTowers.leftTowerHp, maxHp = 2000, isEnemy = false, isHit = playerTowers.isLeftHit)
            TowerStatusBadge(name = "Your King", hp = playerTowers.kingTowerHp, maxHp = 3500, isEnemy = false, isKing = true, isHit = playerTowers.isKingHit)
            TowerStatusBadge(name = "Right Guard", hp = playerTowers.rightTowerHp, maxHp = 2000, isEnemy = false, isHit = playerTowers.isRightHit)
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(ElegantBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((elixir / 10f).coerceIn(0f, 1f))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFEA80FC), Color(0xFFAA00FF))
                                    )
                                )
                        )
                    }
                    Text("${elixir.toInt()}/10", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (heroEnergy >= 100) ElegantPrimaryLavender else ElegantCardBg)
                        .border(2.dp, if (heroEnergy >= 100) Color.White else ElegantBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${heroEnergy}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                battleDeck.forEach { cardId ->
                    val card = GameCatalog.getCard(cardId)
                    val isSelected = selectedHandCardId == cardId
                    val canAfford = elixir >= card.elixirCost
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .clickable { battleViewModel.selectCardToPlay(cardId) },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ElegantPrimaryLavender.copy(alpha = 0.3f) else ElegantCardBg
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ElegantPrimaryLavender) else null
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(card.symbolChar, fontSize = 18.sp)
                            Text(card.name.take(8), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text("${card.elixirCost}", color = if (canAfford) Color(0xFFEA80FC) else Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }

    if (battleState.isGameOver) {
        Dialog(onDismissRequest = { battleViewModel.exitBattle(); onExitBattle() }) {
            Surface(shape = RoundedCornerShape(16.dp), color = ElegantCardBg) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        if (battleState.isVictory) "VICTORY" else "DEFEAT",
                        color = if (battleState.isVictory) ElegantEmerald else ElegantCoral,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Button(onClick = { battleViewModel.exitBattle(); onExitBattle() }) {
                        Text("CONTINUE")
                    }
                }
            }
        }
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
    val color = if (isEnemy) ElegantCoral else ElegantPrimaryLavender
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, color = Color.White.copy(alpha = 0.8f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(ElegantBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((hp.toFloat() / maxHp).coerceIn(0f, 1f))
                    .background(if (isHit) Color.White else color)
            )
        }
        Text("$hp", color = color, fontSize = 8.sp, fontWeight = FontWeight.Black)
    }
}

private fun mapToChessPieceType(pieceId: String?, symbol: String?): ChessPieceType {
    val id = (pieceId ?: "").lowercase()
    val sym = symbol ?: ""
    return when {
        id.contains("queen") || id.contains("valkyrie") || sym == "⚔️" -> ChessPieceType.QUEEN
        id.contains("hierophant") || id.contains("bishop") || sym == "🔮" -> ChessPieceType.BISHOP
        id.contains("knight") || id.contains("paladin") || sym == "🐎" -> ChessPieceType.KNIGHT
        id.contains("rook") || id.contains("siege") || sym == "🏰" -> ChessPieceType.ROOK
        id.contains("king") || sym == "🤴" -> ChessPieceType.KING
        id.contains("pawn") || id.contains("sentinel") || sym == "🛡️" -> ChessPieceType.PAWN
        else -> ChessPieceType.PAWN
    }
}
