package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animation states for arena chess pieces.
 */
enum class PieceAnimState {
    IDLE,
    SPAWNING,
    ATTACKING,
    CAPTURED
}

/**
 * Piece on the 8x8 arena. Level drives upgrade stars and visual polish.
 */
data class ArenaPiece(
    val id: String,
    val type: ChessPieceType,
    val team: PieceTeam,
    val row: Int,
    val col: Int,
    val hp: Float = 1f,
    val level: Int = 1,
    val animState: PieceAnimState = PieceAnimState.IDLE
)

data class SquareEffect(
    val id: Long,
    val row: Int,
    val col: Int,
    val isCapture: Boolean,
    val team: PieceTeam
)

enum class ChessPieceType(val displayName: String, val defaultElixir: Int) {
    PAWN("Royal Recruit", 1),
    KNIGHT("Hog Prince", 3),
    BISHOP("Grand Wizard", 3),
    ROOK("Mega Cannon", 5),
    QUEEN("Archer Queen", 9),
    KING("King", 10)
}

enum class PieceTeam(val primary: Color, val secondary: Color, val highlight: Color) {
    BLUE(
        primary = Color(0xFF1E88E5),
        secondary = Color(0xFF0D47A1),
        highlight = Color(0xFF64B5F6)
    ),
    RED(
        primary = Color(0xFFE53935),
        secondary = Color(0xFFB71C1C),
        highlight = Color(0xFFEF5350)
    )
}

enum class CardRarity(
    val borderBrush: Brush,
    val badgeColor: Color,
    val label: String
) {
    COMMON(
        borderBrush = Brush.verticalGradient(listOf(Color(0xFFB0BEC5), Color(0xFF607D8B))),
        badgeColor = Color(0xFF78909C),
        label = "Common"
    ),
    RARE(
        borderBrush = Brush.verticalGradient(listOf(Color(0xFFFFB74D), Color(0xFFF57C00))),
        badgeColor = Color(0xFFFF9800),
        label = "Rare"
    ),
    EPIC(
        borderBrush = Brush.verticalGradient(listOf(Color(0xFFCE93D8), Color(0xFF8E24AA))),
        badgeColor = Color(0xFF9C27B0),
        label = "Epic"
    ),
    LEGENDARY(
        borderBrush = Brush.sweepGradient(
            listOf(
                Color(0xFF00E5FF),
                Color(0xFFEA80FC),
                Color(0xFFFFD54F),
                Color(0xFF69F0AE),
                Color(0xFF00E5FF)
            )
        ),
        badgeColor = Color(0xFF00BCD4),
        label = "Legendary"
    )
}

@Composable
fun ClashChessCard(
    pieceType: ChessPieceType,
    team: PieceTeam = PieceTeam.BLUE,
    rarity: CardRarity = CardRarity.EPIC,
    level: Int = 11,
    elixirCost: Int = pieceType.defaultElixir,
    isSelected: Boolean = false,
    canAfford: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val cardOffsetY by animateDpAsState(
        targetValue = if (isSelected) (-10).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_lift"
    )

    Box(
        modifier = modifier
            .offset(y = cardOffsetY)
            .width(86.dp)
            .height(114.dp)
            .padding(3.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(if (isSelected) 12.dp else 5.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.radialGradient(
                        colors = if (canAfford) {
                            listOf(Color(0xFF2C3E50), Color(0xFF1A252F))
                        } else {
                            listOf(Color(0xFF262A2E), Color(0xFF14171A))
                        }
                    )
                )
                .border(
                    width = if (isSelected) 3.5.dp else 2.5.dp,
                    brush = if (isSelected) {
                        Brush.verticalGradient(listOf(Color(0xFFFFEE58), Color(0xFFFF9800)))
                    } else {
                        rarity.borderBrush
                    },
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            team.primary.copy(alpha = if (canAfford) 0.4f else 0.15f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension * 0.65f,
                    center = Offset(size.width / 2f, size.height * 0.42f)
                )
            }

            CharacterVisualView(
                pieceType = pieceType,
                team = team,
                level = level,
                isAnimated = canAfford,
                size = 68.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xDD0D131A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lvl $level",
                    color = if (canAfford) Color(0xFFFFD54F) else Color(0xFF78909C),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        ElixirBadge(
            cost = elixirCost,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-3).dp, y = (-3).dp)
        )
    }
}

@Composable
fun ElixirBadge(cost: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val dropPath = Path().apply {
                moveTo(w * 0.5f, h * 0.05f)
                cubicTo(w * 0.9f, h * 0.45f, w * 0.95f, h * 0.85f, w * 0.5f, h * 0.95f)
                cubicTo(w * 0.05f, h * 0.85f, w * 0.1f, h * 0.45f, w * 0.5f, h * 0.05f)
                close()
            }
            drawPath(path = dropPath, color = Color(0xFF4A0072), style = Stroke(width = 5f))
            drawPath(
                path = dropPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFEA80FC), Color(0xFFAA00FF), Color(0xFF6A1B9A))
                )
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = w * 0.1f,
                center = Offset(w * 0.38f, h * 0.45f)
            )
        }
        Text(
            text = cost.toString(),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Clash Royale style piece renderer.
 * level (1-14) controls upgrade stars and slight visual polish.
 */
@Composable
fun CharacterVisualView(
    pieceType: ChessPieceType,
    team: PieceTeam = PieceTeam.BLUE,
    level: Int = 1,
    isAnimated: Boolean = true,
    animState: PieceAnimState = PieceAnimState.IDLE,
    showHpBar: Boolean = false,
    hpProgress: Float = 1f,
    size: Dp = 84.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "clash_piece_idle")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isAnimated) -3.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAnimated) 1.035f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    val spawnProgress = remember(animState) { Animatable(if (animState == PieceAnimState.SPAWNING) 0f else 1f) }
    LaunchedEffect(animState) {
        if (animState == PieceAnimState.SPAWNING) {
            spawnProgress.snapTo(0f)
            spawnProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    val dropOffsetY = (1f - spawnProgress.value) * -70f
    val dynamicScale = when (animState) {
        PieceAnimState.SPAWNING -> 0.45f + 0.55f * spawnProgress.value
        PieceAnimState.ATTACKING -> 1.15f
        else -> breathScale
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val shadowScale = spawnProgress.value.coerceIn(0.35f, 1f)
        drawOval(
            color = Color.Black.copy(alpha = 0.38f * shadowScale),
            topLeft = Offset(w * (0.5f - 0.32f * shadowScale), h * 0.82f),
            size = Size(w * 0.64f * shadowScale, h * 0.14f * shadowScale)
        )

        translate(top = bounceOffset + dropOffsetY) {
            scale(scaleX = dynamicScale, scaleY = dynamicScale, pivot = Offset(w / 2f, h * 0.78f)) {
                drawPedestalBase(team)
                when (pieceType) {
                    ChessPieceType.PAWN -> drawRoyalRecruit(team, level)
                    ChessPieceType.KNIGHT -> drawHogPrince(team, level)
                    ChessPieceType.BISHOP -> drawGrandWizard(team, level)
                    ChessPieceType.ROOK -> drawMegaCannon(team, level)
                    ChessPieceType.QUEEN -> drawArcherQueen(team, level)
                    ChessPieceType.KING -> drawClashKing(team, level)
                }
                drawUpgradeStars(level, w, h)
            }
        }

        if (showHpBar && spawnProgress.value > 0.85f) {
            val barWidth = w * 0.7f
            val barHeight = h * 0.08f
            val barLeft = (w - barWidth) / 2f
            val barTop = h * 0.01f
            drawRoundRect(
                color = Color(0xDD111722),
                topLeft = Offset(barLeft, barTop),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = if (team == PieceTeam.BLUE) Color(0xFF42A5F5) else Color(0xFFEF5350),
                topLeft = Offset(barLeft + 1.5f, barTop + 1.5f),
                size = Size((barWidth - 3f) * hpProgress.coerceIn(0f, 1f), barHeight - 3f),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}

private fun DrawScope.drawUpgradeStars(level: Int, w: Float, h: Float) {
    val starCount = when {
        level >= 11 -> 3
        level >= 7 -> 2
        level >= 4 -> 1
        else -> 0
    }
    if (starCount == 0) return
    val starColor = Color(0xFFFFD54F)
    val outline = Color(0xFF1A1A1A)
    val startX = w * 0.5f - (starCount - 1) * w * 0.08f
    for (i in 0 until starCount) {
        val cx = startX + i * w * 0.16f
        val cy = h * 0.08f
        drawCircle(color = outline, radius = w * 0.045f, center = Offset(cx, cy))
        drawCircle(color = starColor, radius = w * 0.032f, center = Offset(cx, cy))
    }
}

private fun DrawScope.drawPedestalBase(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFFEE58), Color(0xFFF57F17))),
        topLeft = Offset(w * 0.18f, h * 0.74f),
        size = Size(w * 0.64f, h * 0.11f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.18f, h * 0.74f),
        size = Size(w * 0.64f, h * 0.11f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = 3.5f)
    )

    drawRoundRect(
        brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary)),
        topLeft = Offset(w * 0.24f, h * 0.66f),
        size = Size(w * 0.52f, h * 0.1f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.24f, h * 0.66f),
        size = Size(w * 0.52f, h * 0.1f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 3.5f)
    )
}

/** Royal Recruit – helmet, shield, short sword. */
private fun DrawScope.drawRoyalRecruit(team: PieceTeam, level: Int) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)
    val metal = if (level >= 7) Color(0xFFCFD8DC) else Color(0xFFB0BEC5)

    // Body
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(team.highlight, team.primary)),
        topLeft = Offset(w * 0.34f, h * 0.42f),
        size = Size(w * 0.32f, h * 0.26f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.34f, h * 0.42f),
        size = Size(w * 0.32f, h * 0.26f),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 4f)
    )

    // Helmet
    drawCircle(
        brush = Brush.radialGradient(listOf(metal, Color(0xFF78909C)), center = Offset(w * 0.46f, h * 0.28f)),
        radius = w * 0.16f,
        center = Offset(w * 0.5f, h * 0.32f)
    )
    drawCircle(color = outline, radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(width = 4f))

    // Visor
    drawLine(
        color = outline,
        start = Offset(w * 0.38f, h * 0.32f),
        end = Offset(w * 0.62f, h * 0.32f),
        strokeWidth = 4.5f,
        cap = StrokeCap.Round
    )

    // Plume
    drawCircle(color = team.highlight, radius = w * 0.055f, center = Offset(w * 0.5f, h * 0.14f))
    drawCircle(color = outline, radius = w * 0.055f, center = Offset(w * 0.5f, h * 0.14f), style = Stroke(width = 3f))

    // Shield
    drawCircle(color = Color(0xFF8D6E63), radius = w * 0.11f, center = Offset(w * 0.68f, h * 0.52f))
    drawCircle(color = Color(0xFFFFCA28), radius = w * 0.11f, center = Offset(w * 0.68f, h * 0.52f), style = Stroke(width = 3.5f))

    // Sword tip
    if (level >= 4) {
        drawRoundRect(
            color = Color(0xFFECEFF1),
            topLeft = Offset(w * 0.26f, h * 0.38f),
            size = Size(w * 0.06f, h * 0.18f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(w * 0.26f, h * 0.38f),
            size = Size(w * 0.06f, h * 0.18f),
            cornerRadius = CornerRadius(2f, 2f),
            style = Stroke(width = 2.5f)
        )
    }
}

/** Hog Prince – armored hog rider with lance. */
private fun DrawScope.drawHogPrince(team: PieceTeam, level: Int) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    val body = Path().apply {
        moveTo(w * 0.28f, h * 0.68f)
        quadraticBezierTo(w * 0.22f, h * 0.45f, w * 0.38f, h * 0.28f)
        lineTo(w * 0.62f, h * 0.26f)
        quadraticBezierTo(w * 0.78f, h * 0.38f, w * 0.72f, h * 0.68f)
        close()
    }
    drawPath(body, brush = Brush.linearGradient(listOf(Color(0xFFFFF176), Color(0xFFFFB300))))
    drawPath(body, color = outline, style = Stroke(width = 4f))

    // Mane
    val mane = Path().apply {
        moveTo(w * 0.32f, h * 0.5f)
        quadraticBezierTo(w * 0.12f, h * 0.32f, w * 0.42f, h * 0.12f)
        lineTo(w * 0.48f, h * 0.22f)
        close()
    }
    drawPath(mane, color = team.primary)
    drawPath(mane, color = outline, style = Stroke(width = 3.5f))

    // Eye
    drawCircle(color = team.highlight, radius = w * 0.03f, center = Offset(w * 0.52f, h * 0.36f))

    // Lance
    if (level >= 4) {
        val lance = Path().apply {
            moveTo(w * 0.74f, h * 0.66f)
            lineTo(w * 0.8f, h * 0.1f)
            lineTo(w * 0.86f, h * 0.66f)
            close()
        }
        drawPath(lance, brush = Brush.horizontalGradient(listOf(Color(0xFFECEFF1), Color(0xFF90A4AE))))
        drawPath(lance, color = outline, style = Stroke(width = 3f))
    }
}

/** Grand Wizard – pointed hat, glowing eyes, orb. */
private fun DrawScope.drawGrandWizard(team: PieceTeam, level: Int) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    val cloak = Path().apply {
        moveTo(w * 0.26f, h * 0.68f)
        quadraticBezierTo(w * 0.2f, h * 0.35f, w * 0.5f, h * 0.1f)
        quadraticBezierTo(w * 0.8f, h * 0.35f, w * 0.74f, h * 0.68f)
        close()
    }
    drawPath(cloak, brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary)))
    drawPath(cloak, color = outline, style = Stroke(width = 4f))

    // Face shadow
    drawCircle(color = Color(0xFF121824), radius = w * 0.1f, center = Offset(w * 0.5f, h * 0.36f))
    drawCircle(color = Color(0xFF00E5FF), radius = w * 0.025f, center = Offset(w * 0.45f, h * 0.35f))
    drawCircle(color = Color(0xFF00E5FF), radius = w * 0.025f, center = Offset(w * 0.55f, h * 0.35f))

    // Orb
    val orbColor = if (level >= 7) Color(0xFFFF4081) else Color(0xFFEA80FC)
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White, orbColor, Color(0xFFAA00FF))),
        radius = w * 0.06f,
        center = Offset(w * 0.5f, h * 0.1f)
    )
    drawCircle(color = outline, radius = w * 0.06f, center = Offset(w * 0.5f, h * 0.1f), style = Stroke(width = 3f))
}

/** Mega Cannon – stone tower with banner and cannon barrel. */
private fun DrawScope.drawMegaCannon(team: PieceTeam, level: Int) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(Color(0xFFB0BEC5), Color(0xFFCFD8DC), Color(0xFF78909C))),
        topLeft = Offset(w * 0.28f, h * 0.26f),
        size = Size(w * 0.44f, h * 0.42f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.28f, h * 0.26f),
        size = Size(w * 0.44f, h * 0.42f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 4f)
    )

    // Battlements
    listOf(w * 0.22f, w * 0.42f, w * 0.62f).forEach { x ->
        drawRoundRect(
            color = Color(0xFFCFD8DC),
            topLeft = Offset(x, h * 0.14f),
            size = Size(w * 0.14f, h * 0.14f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(x, h * 0.14f),
            size = Size(w * 0.14f, h * 0.14f),
            cornerRadius = CornerRadius(3f, 3f),
            style = Stroke(width = 3.5f)
        )
    }

    // Banner
    val banner = Path().apply {
        moveTo(w * 0.4f, h * 0.32f)
        lineTo(w * 0.6f, h * 0.32f)
        lineTo(w * 0.6f, h * 0.52f)
        lineTo(w * 0.5f, h * 0.46f)
        lineTo(w * 0.4f, h * 0.52f)
        close()
    }
    drawPath(banner, color = team.primary)
    drawPath(banner, color = Color(0xFFFFD54F), style = Stroke(width = 2.5f))

    // Cannon barrel (higher level)
    if (level >= 7) {
        drawRoundRect(
            color = Color(0xFF455A64),
            topLeft = Offset(w * 0.62f, h * 0.4f),
            size = Size(w * 0.2f, h * 0.1f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(w * 0.62f, h * 0.4f),
            size = Size(w * 0.2f, h * 0.1f),
            cornerRadius = CornerRadius(4f, 4f),
            style = Stroke(width = 2.5f)
        )
    }
}

/** Archer Queen – crown, bow, flowing gown. */
private fun DrawScope.drawArcherQueen(team: PieceTeam, level: Int) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    val gown = Path().apply {
        moveTo(w * 0.26f, h * 0.68f)
        cubicTo(w * 0.3f, h * 0.48f, w * 0.34f, h * 0.34f, w * 0.34f, h * 0.3f)
        lineTo(w * 0.66f, h * 0.3f)
        cubicTo(w * 0.66f, h * 0.34f, w * 0.7f, h * 0.48f, w * 0.74f, h * 0.68f)
        close()
    }
    drawPath(gown, brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary)))
    drawPath(gown, color = outline, style = Stroke(width = 4f))

    // Crown
    val crown = Path().apply {
        moveTo(w * 0.28f, h * 0.3f)
        lineTo(w * 0.22f, h * 0.14f)
        lineTo(w * 0.4f, h * 0.22f)
        lineTo(w * 0.5f, h * 0.08f)
        lineTo(w * 0.6f, h * 0.22f)
        lineTo(w * 0.78f, h * 0.14f)
        lineTo(w * 0.72f, h * 0.3f)
        close()
    }
    drawPath(crown, brush = Brush.linearGradient(listOf(Color(0xFFFFF59D), Color(0xFFFFCA28))))
    drawPath(crown, color = outline, style = Stroke(width = 3.5f))

    // Pearls
    listOf(Offset(w * 0.22f, h * 0.13f), Offset(w * 0.5f, h * 0.07f), Offset(w * 0.78f, h * 0.13f)).forEach { c ->
        drawCircle(color = Color.White, radius = w * 0.03f, center = c)
        drawCircle(color = outline, radius = w * 0.03f, center = c, style = Stroke(width = 2f))
    }

    // Bow (higher level)
    if (level >= 4) {
        drawArc(
            color = Color(0xFF8D6E63),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(w * 0.62f, h * 0.32f),
            size = Size(w * 0.22f, h * 0.28f),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }

    // Gem
    val gem = Path().apply {
        moveTo(w * 0.5f, h * 0.4f)
        lineTo(w * 0.56f, h * 0.48f)
        lineTo(w * 0.5f, h * 0.56f)
        lineTo(w * 0.44f, h * 0.48f)
        close()
    }
    drawPath(gem, color = if (level >= 11) Color(0xFF00E676) else Color(0xFF69F0AE))
    drawPath(gem, color = outline, style = Stroke(width = 2f))
}

/** Classic Clash King – mustache, big crown, ruby. */
private fun DrawScope.drawClashKing(team: PieceTeam, level: Int) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    drawRoundRect(
        brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary)),
        topLeft = Offset(w * 0.24f, h * 0.34f),
        size = Size(w * 0.52f, h * 0.34f),
        cornerRadius = CornerRadius(16f, 16f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.24f, h * 0.34f),
        size = Size(w * 0.52f, h * 0.34f),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 4f)
    )

    // Mustache / beard
    val beard = Path().apply {
        moveTo(w * 0.32f, h * 0.44f)
        quadraticBezierTo(w * 0.5f, h * 0.38f, w * 0.68f, h * 0.44f)
        quadraticBezierTo(w * 0.64f, h * 0.6f, w * 0.5f, h * 0.6f)
        quadraticBezierTo(w * 0.36f, h * 0.6f, w * 0.32f, h * 0.44f)
        close()
    }
    drawPath(beard, color = Color(0xFF263238))
    drawPath(beard, color = outline, style = Stroke(width = 3f))

    // Crown
    val crown = Path().apply {
        moveTo(w * 0.26f, h * 0.34f)
        lineTo(w * 0.2f, h * 0.16f)
        lineTo(w * 0.36f, h * 0.24f)
        lineTo(w * 0.5f, h * 0.12f)
        lineTo(w * 0.64f, h * 0.24f)
        lineTo(w * 0.8f, h * 0.16f)
        lineTo(w * 0.74f, h * 0.34f)
        close()
    }
    drawPath(crown, brush = Brush.verticalGradient(listOf(Color(0xFFFFEE58), Color(0xFFFFB300))))
    drawPath(crown, color = outline, style = Stroke(width = 4f))

    // Cross
    val cw = w * 0.05f
    drawRoundRect(
        color = Color(0xFFFFD54F),
        topLeft = Offset(w * 0.5f - cw / 2f, h * 0.02f),
        size = Size(cw, h * 0.14f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.5f - cw / 2f, h * 0.02f),
        size = Size(cw, h * 0.14f),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 2.5f)
    )
    drawRoundRect(
        color = Color(0xFFFFD54F),
        topLeft = Offset(w * 0.4f, h * 0.06f),
        size = Size(w * 0.2f, cw),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.4f, h * 0.06f),
        size = Size(w * 0.2f, cw),
        cornerRadius = CornerRadius(3f, 3f),
        style = Stroke(width = 2.5f)
    )

    // Ruby
    drawCircle(color = Color(0xFFFF1744), radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.26f))
    drawCircle(color = outline, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.26f), style = Stroke(width = 2f))
}

@Composable
fun ArenaParticleEffectOverlay(
    isCapture: Boolean,
    team: PieceTeam,
    onEffectComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(if (isCapture) 600 else 500, easing = FastOutSlowInEasing))
        onEffectComplete()
    }
    val p = progress.value
    val fade = (1f - p).coerceIn(0f, 1f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxR = size.minDimension * 0.8f
        if (isCapture) {
            drawCircle(
                color = Color(0xFFFF9100).copy(alpha = fade * 0.8f),
                radius = maxR * (0.2f + p * 0.85f),
                center = center,
                style = Stroke(width = 7f * fade)
            )
            if (p < 0.4f) {
                drawCircle(color = Color(0xFFFFF59D).copy(alpha = 1f - p * 2.5f), radius = maxR * 0.4f, center = center)
            }
        } else {
            drawCircle(
                color = Color(0xFFEA80FC).copy(alpha = fade * 0.85f),
                radius = maxR * (0.15f + p * 0.7f),
                center = center,
                style = Stroke(width = 5f * fade)
            )
            drawCircle(
                color = team.highlight.copy(alpha = fade * 0.65f),
                radius = maxR * (0.05f + p * 0.5f),
                center = center,
                style = Stroke(width = 3.5f * fade)
            )
        }
    }
}

@Composable
fun ClashBattleArenaScreen(modifier: Modifier = Modifier) {
    var pieces by remember { mutableStateOf(createInitialArenaPieces()) }
    var selectedPieceId by remember { mutableStateOf<String?>(null) }
    var selectedDeployCard by remember { mutableStateOf<ChessPieceType?>(null) }
    var activeEffects by remember { mutableStateOf<List<SquareEffect>>(emptyList()) }
    var elixir by remember { mutableStateOf(7) }
    var blueCrowns by remember { mutableStateOf(1) }
    var redCrowns by remember { mutableStateOf(0) }
    var battleBannerText by remember { mutableStateOf("TAP A PIECE TO MOVE OR DEPLOY") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1800)
            if (elixir < 10) elixir += 1
        }
    }

    val selectedPiece = pieces.find { it.id == selectedPieceId }
    val validTargets = remember(selectedPiece, pieces) {
        selectedPiece?.let { calculateValidMoves(it, pieces) } ?: emptySet()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D1520), Color(0xFF172334), Color(0xFF0E1621))))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ClashBattleTopHud(
            redCrowns = redCrowns,
            blueCrowns = blueCrowns,
            statusBanner = battleBannerText,
            onResetBoard = {
                pieces = createInitialArenaPieces()
                selectedPieceId = null
                selectedDeployCard = null
                elixir = 8
                blueCrowns = 0
                redCrowns = 0
                battleBannerText = "ARENA RESET"
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(14.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF37474F))
                .border(5.dp, Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFF455A64), Color(0xFF263238))), RoundedCornerShape(18.dp))
                .padding(8.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                for (row in 0 until 8) {
                    Row(Modifier.weight(1f).fillMaxWidth()) {
                        for (col in 0 until 8) {
                            val isLight = (row + col) % 2 == 0
                            val piece = pieces.find { it.row == row && it.col == col }
                            val isSelected = piece?.id == selectedPieceId
                            val isValid = validTargets.contains(row to col)
                            val isDeploy = selectedDeployCard != null && row in 4..7 && piece == null

                            ArenaBoardTile(
                                row = row,
                                col = col,
                                isLightTile = isLight,
                                isSelected = isSelected,
                                isValidMove = isValid && piece == null,
                                isValidCapture = isValid && piece != null,
                                isDeployHighlight = isDeploy,
                                piece = piece,
                                effects = activeEffects.filter { it.row == row && it.col == col },
                                onEffectDone = { id -> activeEffects = activeEffects.filterNot { it.id == id } },
                                onClick = {
                                    val card = selectedDeployCard
                                    if (card != null && row in 4..7 && piece == null) {
                                        if (elixir >= card.defaultElixir) {
                                            elixir -= card.defaultElixir
                                            pieces = pieces + ArenaPiece(
                                                id = "d_${System.currentTimeMillis()}",
                                                type = card,
                                                team = PieceTeam.BLUE,
                                                row = row,
                                                col = col,
                                                level = 11,
                                                animState = PieceAnimState.SPAWNING
                                            )
                                            activeEffects = activeEffects + SquareEffect(System.nanoTime(), row, col, false, PieceTeam.BLUE)
                                            battleBannerText = "DEPLOYED ${card.displayName}"
                                            selectedDeployCard = null
                                        } else {
                                            battleBannerText = "NEED ${card.defaultElixir} ELIXIR"
                                        }
                                        return@ArenaBoardTile
                                    }
                                    if (selectedPiece != null && isValid) {
                                        val captured = piece
                                        pieces = pieces
                                            .filterNot { it.id == captured?.id }
                                            .map {
                                                if (it.id == selectedPiece.id) {
                                                    val promo = if (it.type == ChessPieceType.PAWN && (row == 0 || row == 7)) ChessPieceType.QUEEN else it.type
                                                    it.copy(
                                                        row = row,
                                                        col = col,
                                                        type = promo,
                                                        animState = if (captured != null) PieceAnimState.ATTACKING else PieceAnimState.SPAWNING
                                                    )
                                                } else it
                                            }
                                        if (captured != null) {
                                            activeEffects = activeEffects + SquareEffect(System.nanoTime(), row, col, true, selectedPiece.team)
                                            if (selectedPiece.team == PieceTeam.BLUE) blueCrowns = (blueCrowns + 1).coerceAtMost(3)
                                            else redCrowns = (redCrowns + 1).coerceAtMost(3)
                                            battleBannerText = "${selectedPiece.type.name} CAPTURED ${captured.type.name}"
                                        }
                                        selectedPieceId = null
                                        return@ArenaBoardTile
                                    }
                                    if (piece != null) {
                                        selectedDeployCard = null
                                        selectedPieceId = if (selectedPieceId == piece.id) null else piece.id
                                        battleBannerText = "SELECTED ${piece.type.displayName} LVL ${piece.level}"
                                    } else selectedPieceId = null
                                },
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                        }
                    }
                }
            }
            ClashRiverDividerOverlay(Modifier.fillMaxWidth().height(12.dp).align(Alignment.Center))
        }

        ClashElixirDeckDock(
            elixir = elixir,
            selectedCard = selectedDeployCard,
            onSelectCard = { type ->
                selectedPieceId = null
                selectedDeployCard = if (selectedDeployCard == type) null else type
                battleBannerText = if (selectedDeployCard != null) "TAP BLUE HALF TO SPAWN" else "DESELECTED"
            }
        )
    }
}

@Composable
private fun ArenaBoardTile(
    row: Int,
    col: Int,
    isLightTile: Boolean,
    isSelected: Boolean,
    isValidMove: Boolean,
    isValidCapture: Boolean,
    isDeployHighlight: Boolean,
    piece: ArenaPiece?,
    effects: List<SquareEffect>,
    onEffectDone: (Long) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val grass = if (row < 4) {
        if (isLightTile) Color(0xFF7CB342) else Color(0xFF689F38)
    } else {
        if (isLightTile) Color(0xFF8BC34A) else Color(0xFF7CB342)
    }

    Box(
        modifier = modifier
            .background(grass)
            .then(
                when {
                    isSelected -> Modifier.background(Color(0x66FFEE58)).border(2.dp, Color(0xFFFFEE58))
                    isValidCapture -> Modifier.background(Color(0x66FF1744)).border(2.dp, Color(0xFFFF5252))
                    isDeployHighlight -> Modifier.background(Color(0x4400E5FF)).border(1.dp, Color(0x8800E5FF))
                    else -> Modifier
                }
            )
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isValidMove) {
            Canvas(Modifier.size(14.dp)) {
                drawCircle(Color(0xAA111722), size.minDimension / 2f)
                drawCircle(Color(0xFFFFEE58), size.minDimension / 3f)
            }
        }
        if (isValidCapture) {
            Canvas(Modifier.fillMaxSize().padding(3.dp)) {
                drawCircle(Color(0xFFFF1744), style = Stroke(width = 4f))
            }
        }
        piece?.let {
            key(it.id) {
                CharacterVisualView(
                    pieceType = it.type,
                    team = it.team,
                    level = it.level,
                    isAnimated = true,
                    animState = it.animState,
                    showHpBar = it.type == ChessPieceType.KING || it.type == ChessPieceType.ROOK,
                    hpProgress = it.hp,
                    size = 42.dp
                )
            }
        }
        effects.forEach { e ->
            key(e.id) {
                ArenaParticleEffectOverlay(e.isCapture, e.team) { onEffectDone(e.id) }
            }
        }
    }
}

@Composable
private fun ClashRiverDividerOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xCC0288D1), Color(0xEE29B6F6), Color(0xCC0288D1))),
            size = size
        )
        val bw = size.width * 0.18f
        listOf(size.width * 0.15f, size.width * 0.67f).forEach { x ->
            drawRoundRect(Color(0xFF8D6E63), Offset(x, -1f), Size(bw, size.height + 2f), CornerRadius(3f, 3f))
            drawRoundRect(Color(0xFF4E342E), Offset(x, -1f), Size(bw, size.height + 2f), CornerRadius(3f, 3f), style = Stroke(2f))
        }
    }
}

@Composable
private fun ClashBattleTopHud(
    redCrowns: Int,
    blueCrowns: Int,
    statusBanner: String,
    onResetBoard: () -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                .border(1.5.dp, Color(0xFF475569), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("RED $redCrowns", color = Color(0xFFEF5350), fontSize = 12.sp, fontWeight = FontWeight.Black)
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFB300))
                    .clickable { onResetBoard() }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("RESET", color = Color(0xFF111722), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
            Text("$blueCrowns BLUE", color = Color(0xFF64B5F6), fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        Text(statusBanner, color = Color(0xFFFFD54F), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
    }
}

@Composable
private fun ClashElixirDeckDock(
    elixir: Int,
    selectedCard: ChessPieceType?,
    onSelectCard: (ChessPieceType) -> Unit
) {
    val hand = remember {
        listOf(
            Triple(ChessPieceType.PAWN, CardRarity.COMMON, 11),
            Triple(ChessPieceType.KNIGHT, CardRarity.RARE, 12),
            Triple(ChessPieceType.BISHOP, CardRarity.EPIC, 13),
            Triple(ChessPieceType.QUEEN, CardRarity.LEGENDARY, 14)
        )
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A2433), RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            hand.forEach { (type, rarity, lvl) ->
                ClashChessCard(
                    pieceType = type,
                    team = PieceTeam.BLUE,
                    rarity = rarity,
                    level = lvl,
                    isSelected = selectedCard == type,
                    canAfford = elixir >= type.defaultElixir,
                    onClick = { onSelectCard(type) }
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ElixirBadge(elixir, Modifier.size(24.dp))
            Row(
                Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF4A0072), RoundedCornerShape(6.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                repeat(10) { i ->
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (i < elixir) Brush.verticalGradient(listOf(Color(0xFFEA80FC), Color(0xFFAA00FF)))
                                else Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                            )
                    )
                }
            }
        }
    }
}

private fun createInitialArenaPieces(): List<ArenaPiece> {
    val back = listOf(
        ChessPieceType.ROOK, ChessPieceType.KNIGHT, ChessPieceType.BISHOP, ChessPieceType.QUEEN,
        ChessPieceType.KING, ChessPieceType.BISHOP, ChessPieceType.KNIGHT, ChessPieceType.ROOK
    )
    val list = mutableListOf<ArenaPiece>()
    back.forEachIndexed { c, t -> list.add(ArenaPiece("r_b_$c", t, PieceTeam.RED, 0, c, level = 9)) }
    for (c in 0 until 8) {
        val r = if (c == 3 || c == 4) 3 else 1
        list.add(ArenaPiece("r_p_$c", ChessPieceType.PAWN, PieceTeam.RED, r, c, level = 7))
    }
    for (c in 0 until 8) {
        val r = if (c == 2 || c == 4) 4 else 6
        list.add(ArenaPiece("b_p_$c", ChessPieceType.PAWN, PieceTeam.BLUE, r, c, level = 11))
    }
    back.forEachIndexed { c, t -> list.add(ArenaPiece("b_b_$c", t, PieceTeam.BLUE, 7, c, level = 13)) }
    return list
}

private fun calculateValidMoves(piece: ArenaPiece, all: List<ArenaPiece>): Set<Pair<Int, Int>> {
    val moves = mutableSetOf<Pair<Int, Int>>()
    val board = all.associateBy { it.row to it.col }
    fun step(r: Int, c: Int): Boolean {
        if (r !in 0..7 || c !in 0..7) return false
        val occ = board[r to c]
        return if (occ == null) { moves.add(r to c); true }
        else { if (occ.team != piece.team) moves.add(r to c); false }
    }
    fun ray(dr: Int, dc: Int) {
        var r = piece.row + dr
        var c = piece.col + dc
        while (step(r, c)) { r += dr; c += dc }
    }
    when (piece.type) {
        ChessPieceType.PAWN -> {
            val dir = if (piece.team == PieceTeam.BLUE) -1 else 1
            val start = if (piece.team == PieceTeam.BLUE) 6 else 1
            val next = piece.row + dir
            if (next in 0..7 && board[next to piece.col] == null) {
                moves.add(next to piece.col)
                if (piece.row == start && board[piece.row + dir * 2 to piece.col] == null)
                    moves.add(piece.row + dir * 2 to piece.col)
            }
            listOf(piece.col - 1, piece.col + 1).forEach { dc ->
                val t = board[next to dc]
                if (t != null && t.team != piece.team) moves.add(next to dc)
            }
        }
        ChessPieceType.KNIGHT -> listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1)
            .forEach { (dr, dc) -> step(piece.row + dr, piece.col + dc) }
        ChessPieceType.BISHOP -> { ray(-1, -1); ray(-1, 1); ray(1, -1); ray(1, 1) }
        ChessPieceType.ROOK -> { ray(-1, 0); ray(1, 0); ray(0, -1); ray(0, 1) }
        ChessPieceType.QUEEN -> {
            ray(-1, -1); ray(-1, 1); ray(1, -1); ray(1, 1)
            ray(-1, 0); ray(1, 0); ray(0, -1); ray(0, 1)
        }
        ChessPieceType.KING -> for (dr in -1..1) for (dc in -1..1) if (dr != 0 || dc != 0) step(piece.row + dr, piece.col + dc)
    }
    return moves
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1520, widthDp = 420, heightDp = 760)
@Composable
fun ClashRoyaleChessPiecesPreview() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Color(0xFF0D1520)) {
            ClashBattleArenaScreen()
        }
    }
}
