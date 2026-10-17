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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animation states for arena chess pieces (Idle, Spawn drop-in, Attack lunge, and Capture burst).
 */
enum class PieceAnimState {
    IDLE,
    SPAWNING,
    ATTACKING,
    CAPTURED
}

/**
 * Represents a piece placed on the 8x8 Clash Royale Chess Arena.
 */
data class ArenaPiece(
    val id: String,
    val type: ChessPieceType,
    val team: PieceTeam,
    val row: Int,
    val col: Int,
    val hp: Float = 1f,
    val animState: PieceAnimState = PieceAnimState.IDLE
)

/**
 * Represents a transient particle effect triggered on a specific board square.
 */
data class SquareEffect(
    val id: Long,
    val row: Int,
    val col: Int,
    val isCapture: Boolean, // true = Crown/Fire explosion, false = Elixir Spawn shockwave
    val team: PieceTeam
)

/**
 * Core enums for Clash Royale style Chess Pieces.
 * If you already have these in com.example.data.model.Models, you can remove these
 * and import your existing models directly.
 */
enum class ChessPieceType(val displayName: String, val defaultElixir: Int) {
    PAWN("Royal Recruit Pawn", 1),
    KNIGHT("Hog Prince Knight", 3),
    BISHOP("Grand Wizard Bishop", 3),
    ROOK("Mega Cannon Tower", 5),
    QUEEN("Archer Queen", 9),
    KING("Blue King", 10)
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

/**
 * Full Clash Royale style Deck Card featuring the Chess Piece, Elixir Drop, Level Banner, and Rarity Border.
 */
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
        // Card Frame
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
            // Subtle sunburst/arena glow inside card
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

            // Animated Piece Visual
            CharacterVisualView(
                pieceType = pieceType,
                team = team,
                isAnimated = canAfford,
                size = 68.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
            )

            // Bottom Level Banner
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

        // Top-Left Elixir Drop Badge
        ElixirBadge(
            cost = elixirCost,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-3).dp, y = (-3).dp)
        )
    }
}

/**
 * Purple Elixir Drop icon used in Clash Royale cards.
 */
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
            // Drop shadow/outline
            drawPath(
                path = dropPath,
                color = Color(0xFF4A0072),
                style = Stroke(width = 5f)
            )
            // Main Elixir Fill
            drawPath(
                path = dropPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFEA80FC), Color(0xFFAA00FF), Color(0xFF6A1B9A))
                )
            )
            // Glossy specular highlight
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
 * Standalone Board/Arena Piece Renderer.
 * Renders chunky, stylized Clash Royale characters mapped to each Chess Piece role,
 * complete with Spawn drop-in bounce, Attack lunge, and HP bar support.
 */
@Composable
fun CharacterVisualView(
    pieceType: ChessPieceType,
    team: PieceTeam = PieceTeam.BLUE,
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
        targetValue = if (isAnimated) -4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAnimated) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    // Spawn drop-in animation (sky drop + squash & stretch)
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

    val dropOffsetY = (1f - spawnProgress.value) * -65f
    val dynamicScale = when (animState) {
        PieceAnimState.SPAWNING -> 0.5f + 0.5f * spawnProgress.value
        PieceAnimState.ATTACKING -> 1.18f
        else -> breathScale
    }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 1. Arena Ground Shadow (shrinks when piece is high in the air during spawn)
        val shadowScale = spawnProgress.value.coerceIn(0.3f, 1f)
        drawOval(
            color = Color.Black.copy(alpha = 0.42f * shadowScale),
            topLeft = Offset(w * (0.5f - 0.35f * shadowScale), h * 0.80f),
            size = Size(w * 0.7f * shadowScale, h * 0.16f * shadowScale)
        )

        translate(top = bounceOffset + dropOffsetY) {
            scale(scaleX = dynamicScale, scaleY = dynamicScale, pivot = Offset(w / 2f, h * 0.8f)) {
                // 2. Chunky Pedestal Base (Gold Rim + Team Tunic Base)
                drawPedestalBase(team)

                // 3. Piece-Specific Clash Royale Character Silhouette & Details
                when (pieceType) {
                    ChessPieceType.PAWN -> drawRecruitPawn(team)
                    ChessPieceType.KNIGHT -> drawPrinceKnight(team)
                    ChessPieceType.BISHOP -> drawWizardBishop(team)
                    ChessPieceType.ROOK -> drawCannonRook(team)
                    ChessPieceType.QUEEN -> drawArcherQueen(team)
                    ChessPieceType.KING -> drawClashKing(team)
                }
            }
        }

        // 4. Optional Clash Royale Floating HP Bar above piece
        if (showHpBar && spawnProgress.value > 0.8f) {
            val barWidth = w * 0.68f
            val barHeight = h * 0.09f
            val barLeft = (w - barWidth) / 2f
            val barTop = h * 0.02f

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

/**
 * Renders Clash Royale style Spawn Elixir Shockwaves & Capture Explosion Particles on a board tile.
 */
@Composable
fun ArenaParticleEffectOverlay(
    isCapture: Boolean,
    team: PieceTeam,
    onEffectComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = if (isCapture) 650 else 550,
                easing = FastOutSlowInEasing
            )
        )
        onEffectComplete()
    }

    val p = progress.value
    val fadeAlpha = (1f - p).coerceIn(0f, 1f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension * 0.85f

        if (isCapture) {
            // 1. Capture Shockwave Ring (Fiery Orange/Gold)
            drawCircle(
                color = Color(0xFFFF9100).copy(alpha = fadeAlpha * 0.8f),
                radius = maxRadius * (0.2f + p * 0.9f),
                center = center,
                style = Stroke(width = 8f * fadeAlpha)
            )

            // 2. Inner Flash Core
            if (p < 0.4f) {
                drawCircle(
                    color = Color(0xFFFFF59D).copy(alpha = (1f - p * 2.5f)),
                    radius = maxRadius * 0.45f,
                    center = center
                )
            }

            // 3. Radial Debris Sparks & Elixir Droplets (8 directions)
            val angles = 8
            for (i in 0 until angles) {
                val angleRad = (i * (360f / angles)) * (PI.toFloat() / 180f)
                val dist = maxRadius * (0.15f + p * 0.95f)
                val px = center.x + cos(angleRad) * dist
                val py = center.y + sin(angleRad) * dist

                // Smoke puff
                drawCircle(
                    color = Color(0xFFCFD8DC).copy(alpha = fadeAlpha * 0.7f),
                    radius = (size.minDimension * 0.14f) * (1f - p * 0.5f),
                    center = Offset(px, py)
                )

                // Golden Star / Ember Spark
                val sparkDist = dist * 1.15f
                val sx = center.x + cos(angleRad + 0.25f) * sparkDist
                val sy = center.y + sin(angleRad + 0.25f) * sparkDist
                drawCircle(
                    color = if (i % 2 == 0) Color(0xFFFFD54F).copy(alpha = fadeAlpha)
                    else Color(0xFFEA80FC).copy(alpha = fadeAlpha),
                    radius = (size.minDimension * 0.08f) * fadeAlpha,
                    center = Offset(sx, sy)
                )
            }
        } else {
            // SPAWN EFFECT: Purple Elixir Ripple + Dust Clouds + Vertical Light Pillar
            // 1. Expanding Elixir Ring
            drawCircle(
                color = Color(0xFFEA80FC).copy(alpha = fadeAlpha * 0.85f),
                radius = maxRadius * (0.15f + p * 0.75f),
                center = center,
                style = Stroke(width = 6f * fadeAlpha)
            )

            // 2. Secondary Team Color Ring
            drawCircle(
                color = team.highlight.copy(alpha = fadeAlpha * 0.7f),
                radius = maxRadius * (0.05f + p * 0.55f),
                center = center,
                style = Stroke(width = 4f * fadeAlpha)
            )

            // 3. Dust Cloud Puffs around Pedestal
            val puffCount = 6
            for (i in 0 until puffCount) {
                val angleRad = (i * (360f / puffCount)) * (PI.toFloat() / 180f)
                val dist = maxRadius * (0.2f + p * 0.55f)
                val px = center.x + cos(angleRad) * dist
                val py = center.y + sin(angleRad) * dist * 0.7f + size.height * 0.15f

                drawCircle(
                    color = Color.White.copy(alpha = fadeAlpha * 0.65f),
                    radius = size.minDimension * 0.12f * (0.5f + p * 0.5f),
                    center = Offset(px, py)
                )
            }
        }
    }
}

private fun DrawScope.drawPedestalBase(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outlineColor = Color(0xFF1A1A1A)

    // Gold Lower Rim
    drawRoundRect(
        brush = Brush.horizontalGradient(
            listOf(Color(0xFFFFB300), Color(0xFFFFEE58), Color(0xFFF57F17))
        ),
        topLeft = Offset(w * 0.2f, h * 0.72f),
        size = Size(w * 0.6f, h * 0.12f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(w * 0.2f, h * 0.72f),
        size = Size(w * 0.6f, h * 0.12f),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 4f)
    )

    // Team Colored Sub-Base
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary)),
        topLeft = Offset(w * 0.25f, h * 0.64f),
        size = Size(w * 0.5f, h * 0.1f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(w * 0.25f, h * 0.64f),
        size = Size(w * 0.5f, h * 0.1f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 4f)
    )
}

/**
 * PAWN: Royal Recruit / Barbarian style helmet with wooden shield and short sword.
 */
private fun DrawScope.drawRecruitPawn(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    // Torso
    drawRoundRect(
        color = team.primary,
        topLeft = Offset(w * 0.32f, h * 0.44f),
        size = Size(w * 0.36f, h * 0.22f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.32f, h * 0.44f),
        size = Size(w * 0.36f, h * 0.22f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = 4f)
    )

    // Iron Bucket Helmet / Head
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFCFD8DC), Color(0xFF78909C), Color(0xFF455A64)),
            center = Offset(w * 0.45f, h * 0.3f)
        ),
        radius = w * 0.18f,
        center = Offset(w * 0.5f, h * 0.34f)
    )
    drawCircle(
        color = outline,
        radius = w * 0.18f,
        center = Offset(w * 0.5f, h * 0.34f),
        style = Stroke(width = 4f)
    )

    // Helmet Visor Slit
    drawLine(
        color = outline,
        start = Offset(w * 0.38f, h * 0.34f),
        end = Offset(w * 0.62f, h * 0.34f),
        strokeWidth = 5f,
        cap = StrokeCap.Round
    )

    // Team Plume on top
    drawCircle(
        color = team.highlight,
        radius = w * 0.06f,
        center = Offset(w * 0.5f, h * 0.14f)
    )
    drawCircle(
        color = outline,
        radius = w * 0.06f,
        center = Offset(w * 0.5f, h * 0.14f),
        style = Stroke(width = 3f)
    )

    // Mini Wooden Shield on Front-Right
    drawCircle(
        color = Color(0xFF8D6E63),
        radius = w * 0.12f,
        center = Offset(w * 0.65f, h * 0.54f)
    )
    drawCircle(
        color = Color(0xFFFFCA28),
        radius = w * 0.12f,
        center = Offset(w * 0.65f, h * 0.54f),
        style = Stroke(width = 4f)
    )
}

/**
 * KNIGHT: Golden Prince / Pony Crest with Lance and Plumed Mane.
 */
private fun DrawScope.drawPrinceKnight(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    // Armored Horse/Knight Bust Path
    val knightPath = Path().apply {
        moveTo(w * 0.32f, h * 0.65f)
        lineTo(w * 0.28f, h * 0.35f)
        quadraticBezierTo(w * 0.30f, h * 0.15f, w * 0.52f, h * 0.16f)
        // Snout / Visor
        lineTo(w * 0.72f, h * 0.28f)
        lineTo(w * 0.66f, h * 0.38f)
        lineTo(w * 0.52f, h * 0.36f)
        // Chest
        quadraticBezierTo(w * 0.65f, h * 0.48f, w * 0.65f, h * 0.65f)
        close()
    }

    // Team Mane behind neck
    val manePath = Path().apply {
        moveTo(w * 0.32f, h * 0.58f)
        quadraticBezierTo(w * 0.16f, h * 0.35f, w * 0.38f, h * 0.14f)
        lineTo(w * 0.44f, h * 0.22f)
        close()
    }
    drawPath(manePath, color = team.primary)
    drawPath(manePath, color = outline, style = Stroke(width = 4f))

    // Golden Armor Fill
    drawPath(
        path = knightPath,
        brush = Brush.linearGradient(
            listOf(Color(0xFFFFF176), Color(0xFFFFB300), Color(0xFFF57F17))
        )
    )
    drawPath(path = knightPath, color = outline, style = Stroke(width = 4f))

    // Fierce Glowing Eye
    drawCircle(
        color = team.highlight,
        radius = w * 0.035f,
        center = Offset(w * 0.50f, h * 0.25f)
    )

    // Jousting Lance Tip on side
    val lancePath = Path().apply {
        moveTo(w * 0.72f, h * 0.64f)
        lineTo(w * 0.78f, h * 0.12f)
        lineTo(w * 0.84f, h * 0.64f)
        close()
    }
    drawPath(
        lancePath,
        brush = Brush.horizontalGradient(listOf(Color(0xFFECEFF1), Color(0xFF90A4AE)))
    )
    drawPath(lancePath, color = outline, style = Stroke(width = 3.5f))
}

/**
 * BISHOP: Grand Wizard / Magic Archer Hood with Glowing Arcane Gem & Fireball Orb.
 */
private fun DrawScope.drawWizardBishop(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    // Wizard Cloak & Pointed Mitre Hood
    val cloakPath = Path().apply {
        moveTo(w * 0.28f, h * 0.65f)
        quadraticBezierTo(w * 0.24f, h * 0.32f, w * 0.5f, h * 0.12f)
        quadraticBezierTo(w * 0.76f, h * 0.32f, w * 0.72f, h * 0.65f)
        close()
    }

    drawPath(
        path = cloakPath,
        brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary))
    )
    drawPath(path = cloakPath, color = outline, style = Stroke(width = 4f))

    // Golden Trim Collar
    drawArc(
        color = Color(0xFFFFD54F),
        startAngle = 15f,
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(w * 0.32f, h * 0.36f),
        size = Size(w * 0.36f, h * 0.18f),
        style = Stroke(width = 6f, cap = StrokeCap.Round)
    )

    // Shadowed Hood Face + Glowing Wizard Eyes
    drawCircle(
        color = Color(0xFF121824),
        radius = w * 0.11f,
        center = Offset(w * 0.5f, h * 0.35f)
    )
    drawCircle(
        color = Color(0xFF00E5FF),
        radius = w * 0.028f,
        center = Offset(w * 0.45f, h * 0.34f)
    )
    drawCircle(
        color = Color(0xFF00E5FF),
        radius = w * 0.028f,
        center = Offset(w * 0.55f, h * 0.34f)
    )

    // Arcane Elixir/Fire Orb atop the Mitre
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color.White, Color(0xFFFF4081), Color(0xFFAA00FF))
        ),
        radius = w * 0.065f,
        center = Offset(w * 0.5f, h * 0.11f)
    )
    drawCircle(
        color = outline,
        radius = w * 0.065f,
        center = Offset(w * 0.5f, h * 0.11f),
        style = Stroke(width = 3f)
    )
}

/**
 * ROOK: Clash Royale King's Stone Battlement Tower with Team Banner.
 */
private fun DrawScope.drawCannonRook(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    // Main Stone Tower Body
    drawRoundRect(
        brush = Brush.horizontalGradient(
            listOf(Color(0xFFB0BEC5), Color(0xFFCFD8DC), Color(0xFF78909C))
        ),
        topLeft = Offset(w * 0.30f, h * 0.28f),
        size = Size(w * 0.40f, h * 0.38f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.30f, h * 0.28f),
        size = Size(w * 0.40f, h * 0.38f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 4f)
    )

    // 3 Top Battlement Crenellations
    val merlonWidth = w * 0.14f
    val merlonPositions = listOf(w * 0.24f, w * 0.43f, w * 0.62f)
    merlonPositions.forEach { xPos ->
        drawRoundRect(
            color = Color(0xFFCFD8DC),
            topLeft = Offset(xPos, h * 0.16f),
            size = Size(merlonWidth, h * 0.14f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = outline,
            topLeft = Offset(xPos, h * 0.16f),
            size = Size(merlonWidth, h * 0.14f),
            cornerRadius = CornerRadius(4f, 4f),
            style = Stroke(width = 4f)
        )
    }

    // Hanging Team Banner Flag in the Center
    val bannerPath = Path().apply {
        moveTo(w * 0.41f, h * 0.33f)
        lineTo(w * 0.59f, h * 0.33f)
        lineTo(w * 0.59f, h * 0.54f)
        lineTo(w * 0.50f, h * 0.48f)
        lineTo(w * 0.41f, h * 0.54f)
        close()
    }
    drawPath(bannerPath, color = team.primary)
    drawPath(bannerPath, color = Color(0xFFFFD54F), style = Stroke(width = 3f))
}

/**
 * QUEEN: Archer Queen Tiara, Flowing Cape, and Emerald/Ruby Crown Jewels.
 */
private fun DrawScope.drawArcherQueen(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    // Regal Gown / Armor Bodice
    val gownPath = Path().apply {
        moveTo(w * 0.28f, h * 0.65f)
        cubicTo(w * 0.32f, h * 0.45f, w * 0.36f, h * 0.32f, w * 0.35f, h * 0.28f)
        lineTo(w * 0.65f, h * 0.28f)
        cubicTo(w * 0.64f, h * 0.32f, w * 0.68f, h * 0.45f, w * 0.72f, h * 0.65f)
        close()
    }
    drawPath(
        path = gownPath,
        brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary))
    )
    drawPath(path = gownPath, color = outline, style = Stroke(width = 4f))

    // Multi-peaked Golden Queen Tiara
    val crownPath = Path().apply {
        moveTo(w * 0.30f, h * 0.30f)
        lineTo(w * 0.24f, h * 0.14f)
        lineTo(w * 0.40f, h * 0.23f)
        lineTo(w * 0.50f, h * 0.10f)
        lineTo(w * 0.60f, h * 0.23f)
        lineTo(w * 0.76f, h * 0.14f)
        lineTo(w * 0.70f, h * 0.30f)
        close()
    }
    drawPath(
        path = crownPath,
        brush = Brush.linearGradient(
            listOf(Color(0xFFFFF59D), Color(0xFFFFCA28), Color(0xFFFF8F00))
        )
    )
    drawPath(path = crownPath, color = outline, style = Stroke(width = 4f))

    // Crown Pearls
    val pearlCenters = listOf(
        Offset(w * 0.24f, h * 0.13f),
        Offset(w * 0.50f, h * 0.09f),
        Offset(w * 0.76f, h * 0.13f)
    )
    pearlCenters.forEach { center ->
        drawCircle(color = Color.White, radius = w * 0.035f, center = center)
        drawCircle(color = outline, radius = w * 0.035f, center = center, style = Stroke(width = 2.5f))
    }

    // Center Diamond Brooch
    val gemPath = Path().apply {
        moveTo(w * 0.50f, h * 0.38f)
        lineTo(w * 0.56f, h * 0.46f)
        lineTo(w * 0.50f, h * 0.54f)
        lineTo(w * 0.44f, h * 0.46f)
        close()
    }
    drawPath(gemPath, color = Color(0xFF00E676))
    drawPath(gemPath, color = outline, style = Stroke(width = 2.5f))
}

/**
 * KING: Iconic Clash Royale King with bushy mustache, massive gold crown, Ruby gem, and Royal Cross.
 */
private fun DrawScope.drawClashKing(team: PieceTeam) {
    val w = size.width
    val h = size.height
    val outline = Color(0xFF1A1A1A)

    // Royal Mantle Robe
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(team.highlight, team.primary, team.secondary)),
        topLeft = Offset(w * 0.26f, h * 0.34f),
        size = Size(w * 0.48f, h * 0.32f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.26f, h * 0.34f),
        size = Size(w * 0.48f, h * 0.32f),
        cornerRadius = CornerRadius(14f, 14f),
        style = Stroke(width = 4f)
    )

    // Iconic Clash King Mustache & Beard Emblem
    val beardPath = Path().apply {
        moveTo(w * 0.34f, h * 0.42f)
        quadraticBezierTo(w * 0.50f, h * 0.36f, w * 0.66f, h * 0.42f)
        quadraticBezierTo(w * 0.62f, h * 0.58f, w * 0.50f, h * 0.58f)
        quadraticBezierTo(w * 0.38f, h * 0.58f, w * 0.34f, h * 0.42f)
        close()
    }
    drawPath(beardPath, color = Color(0xFF263238))
    drawPath(beardPath, color = outline, style = Stroke(width = 3.5f))

    // Chunky Golden Crown
    val crownPath = Path().apply {
        moveTo(w * 0.28f, h * 0.35f)
        lineTo(w * 0.24f, h * 0.18f)
        lineTo(w * 0.38f, h * 0.25f)
        lineTo(w * 0.50f, h * 0.16f)
        lineTo(w * 0.62f, h * 0.25f)
        lineTo(w * 0.76f, h * 0.18f)
        lineTo(w * 0.72f, h * 0.35f)
        close()
    }
    drawPath(
        path = crownPath,
        brush = Brush.verticalGradient(listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFF57F17)))
    )
    drawPath(path = crownPath, color = outline, style = Stroke(width = 4f))

    // Royal Cross Finial at the Very Top
    val crossWidth = w * 0.055f
    // Vertical bar
    drawRoundRect(
        color = Color(0xFFFFD54F),
        topLeft = Offset(w * 0.5f - crossWidth / 2f, h * 0.02f),
        size = Size(crossWidth, h * 0.15f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.5f - crossWidth / 2f, h * 0.02f),
        size = Size(crossWidth, h * 0.15f),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(width = 3f)
    )
    // Horizontal bar
    drawRoundRect(
        color = Color(0xFFFFD54F),
        topLeft = Offset(w * 0.40f, h * 0.06f),
        size = Size(w * 0.20f, crossWidth),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.40f, h * 0.06f),
        size = Size(w * 0.20f, crossWidth),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(width = 3f)
    )

    // Center Red Ruby in Crown
    drawCircle(
        color = Color(0xFFFF1744),
        radius = w * 0.045f,
        center = Offset(w * 0.50f, h * 0.27f)
    )
    drawCircle(
        color = outline,
        radius = w * 0.045f,
        center = Offset(w * 0.50f, h * 0.27f),
        style = Stroke(width = 2.5f)
    )
}

/**
 * Complete Clash Royale Battle Arena Chessboard with:
 * - Stone arena walls, 8x8 emerald/lime grass tiles, and a central river with bridges
 * - Interactive tap-to-move, tap-to-capture, and card deployment from the bottom Elixir dock
 * - Real-time Spawn & Capture particle bursts
 */
@Composable
fun ClashBattleArenaScreen(
    modifier: Modifier = Modifier
) {
    var pieces by remember { mutableStateOf(createInitialArenaPieces()) }
    var selectedPieceId by remember { mutableStateOf<String?>(null) }
    var selectedDeployCard by remember { mutableStateOf<ChessPieceType?>(null) }
    var activeEffects by remember { mutableStateOf<List<SquareEffect>>(emptyList()) }
    var elixir by remember { mutableStateOf(7) }
    var blueCrowns by remember { mutableStateOf(1) }
    var redCrowns by remember { mutableStateOf(0) }
    var battleBannerText by remember { mutableStateOf("TAP A PIECE TO MOVE OR DEPLOY A CARD!") }

    // Regenerate Elixir smoothly over time like Clash Royale
    LaunchedEffect(Unit) {
        while (true) {
            delay(1800)
            if (elixir < 10) {
                elixir += 1
            }
        }
    }

    val selectedPiece = pieces.find { it.id == selectedPieceId }
    val validTargetSquares = remember(selectedPiece, pieces) {
        if (selectedPiece != null) {
            calculateValidMoves(selectedPiece, pieces)
        } else {
            emptySet()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1520), Color(0xFF172334), Color(0xFF0E1621))
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Opponent HUD & Crown Scoreboard
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
                battleBannerText = "ARENA RESET! FIGHT!"
            }
        )

        // 2. 8x8 Clash Royale Arena Chessboard
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(14.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF37474F))
                .border(
                    width = 5.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF90A4AE), Color(0xFF455A64), Color(0xFF263238))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0 until 8) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        for (col in 0 until 8) {
                            val isLightTile = (row + col) % 2 == 0
                            val pieceAtSquare = pieces.find { it.row == row && it.col == col }
                            val isSelected = pieceAtSquare?.id == selectedPieceId
                            val isValidTarget = validTargetSquares.contains(row to col)
                            val isDeployTarget = selectedDeployCard != null &&
                                    row in 4..7 && pieceAtSquare == null

                            ArenaBoardTile(
                                row = row,
                                col = col,
                                isLightTile = isLightTile,
                                isSelected = isSelected,
                                isValidMove = isValidTarget && pieceAtSquare == null,
                                isValidCapture = isValidTarget && pieceAtSquare != null,
                                isDeployHighlight = isDeployTarget,
                                piece = pieceAtSquare,
                                effects = activeEffects.filter { it.row == row && it.col == col },
                                onEffectDone = { effectId ->
                                    activeEffects = activeEffects.filterNot { it.id == effectId }
                                },
                                onClick = {
                                    // Case A: Deploying a selected card from the dock onto an empty friendly square
                                    val cardToDeploy = selectedDeployCard
                                    if (cardToDeploy != null && row in 4..7 && pieceAtSquare == null) {
                                        if (elixir >= cardToDeploy.defaultElixir) {
                                            elixir -= cardToDeploy.defaultElixir
                                            val newPiece = ArenaPiece(
                                                id = "deploy_${System.currentTimeMillis()}",
                                                type = cardToDeploy,
                                                team = PieceTeam.BLUE,
                                                row = row,
                                                col = col,
                                                animState = PieceAnimState.SPAWNING
                                            )
                                            pieces = pieces + newPiece
                                            activeEffects = activeEffects + SquareEffect(
                                                id = System.nanoTime(),
                                                row = row,
                                                col = col,
                                                isCapture = false,
                                                team = PieceTeam.BLUE
                                            )
                                            battleBannerText = "DEPLOYED ${cardToDeploy.displayName.uppercase()}!"
                                            selectedDeployCard = null
                                        } else {
                                            battleBannerText = "NOT ENOUGH ELIXIR (${cardToDeploy.defaultElixir} NEEDED)!"
                                        }
                                        return@ArenaBoardTile
                                    }

                                    // Case B: Moving or Capturing with an already-selected board piece
                                    if (selectedPiece != null && isValidTarget) {
                                        val capturedPiece = pieceAtSquare
                                        pieces = pieces
                                            .filterNot { it.id == capturedPiece?.id }
                                            .map {
                                                if (it.id == selectedPiece.id) {
                                                    // Pawn promotion to Archer Queen at row 0 or 7
                                                    val promotedType = if (
                                                        it.type == ChessPieceType.PAWN &&
                                                        (row == 0 || row == 7)
                                                    ) ChessPieceType.QUEEN else it.type

                                                    it.copy(
                                                        row = row,
                                                        col = col,
                                                        type = promotedType,
                                                        animState = if (capturedPiece != null) {
                                                            PieceAnimState.ATTACKING
                                                        } else {
                                                            PieceAnimState.SPAWNING
                                                        }
                                                    )
                                                } else it
                                            }

                                        if (capturedPiece != null) {
                                            activeEffects = activeEffects + SquareEffect(
                                                id = System.nanoTime(),
                                                row = row,
                                                col = col,
                                                isCapture = true,
                                                team = selectedPiece.team
                                            )
                                            if (selectedPiece.team == PieceTeam.BLUE) {
                                                blueCrowns = (blueCrowns + 1).coerceAtMost(3)
                                            } else {
                                                redCrowns = (redCrowns + 1).coerceAtMost(3)
                                            }
                                            battleBannerText =
                                                "${selectedPiece.type.name} CAPTURED ${capturedPiece.type.name}!"
                                        } else {
                                            activeEffects = activeEffects + SquareEffect(
                                                id = System.nanoTime(),
                                                row = row,
                                                col = col,
                                                isCapture = false,
                                                team = selectedPiece.team
                                            )
                                            battleBannerText =
                                                "${selectedPiece.type.displayName} -> (${row + 1}, ${col + 1})"
                                        }
                                        selectedPieceId = null
                                        return@ArenaBoardTile
                                    }

                                    // Case C: Selecting a piece on the board
                                    if (pieceAtSquare != null) {
                                        selectedDeployCard = null
                                        selectedPieceId =
                                            if (selectedPieceId == pieceAtSquare.id) null else pieceAtSquare.id
                                        battleBannerText =
                                            "SELECTED ${pieceAtSquare.team.name} ${pieceAtSquare.type.displayName.uppercase()}"
                                    } else {
                                        selectedPieceId = null
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }

            // Central Clash Royale River & Wooden Bridges Overlay (between rows 3 and 4)
            ClashRiverDividerOverlay(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .align(Alignment.Center)
            )
        }

        // 3. Bottom Clash Royale Elixir Bar & Card Deployment Hand
        ClashElixirDeckDock(
            elixir = elixir,
            selectedCard = selectedDeployCard,
            onSelectCard = { cardType ->
                selectedPieceId = null
                selectedDeployCard = if (selectedDeployCard == cardType) null else cardType
                battleBannerText = if (selectedDeployCard != null) {
                    "TAP ANY EMPTY BLUE TILE (BOTTOM HALF) TO SPAWN ${cardType.name}!"
                } else {
                    "CARD DESELECTED"
                }
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
    // Clash Royale lush grass colors (Red territory top half slightly warmer, Blue territory bottom half vibrant green)
    val baseGrassColor = if (row < 4) {
        if (isLightTile) Color(0xFF7CB342) else Color(0xFF689F38)
    } else {
        if (isLightTile) Color(0xFF8BC34A) else Color(0xFF7CB342)
    }

    Box(
        modifier = modifier
            .background(baseGrassColor)
            .then(
                if (isSelected) {
                    Modifier
                        .background(Color(0x66FFEE58))
                        .border(2.dp, Color(0xFFFFEE58))
                } else if (isValidCapture) {
                    Modifier
                        .background(Color(0x66FF1744))
                        .border(2.dp, Color(0xFFFF5252))
                } else if (isDeployHighlight) {
                    Modifier
                        .background(Color(0x4400E5FF))
                        .border(1.dp, Color(0x8800E5FF))
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Valid Move Indicator Dot
        if (isValidMove) {
            Canvas(modifier = Modifier.size(16.dp)) {
                drawCircle(
                    color = Color(0xAA111722),
                    radius = size.minDimension / 2f
                )
                drawCircle(
                    color = Color(0xFFFFEE58),
                    radius = size.minDimension / 3f
                )
            }
        }

        // Valid Capture Crosshair Ring
        if (isValidCapture) {
            Canvas(modifier = Modifier.fillMaxSize().padding(3.dp)) {
                drawCircle(
                    color = Color(0xFFFF1744),
                    style = Stroke(width = 5f)
                )
            }
        }

        // Render Chess Piece if present on this square
        if (piece != null) {
            key(piece.id) {
                CharacterVisualView(
                    pieceType = piece.type,
                    team = piece.team,
                    isAnimated = true,
                    animState = piece.animState,
                    showHpBar = piece.type == ChessPieceType.KING || piece.type == ChessPieceType.ROOK,
                    hpProgress = piece.hp,
                    size = 44.dp
                )
            }
        }

        // Render Spawn / Capture Particle Effects
        effects.forEach { effect ->
            key(effect.id) {
                ArenaParticleEffectOverlay(
                    isCapture = effect.isCapture,
                    team = effect.team,
                    onEffectComplete = { onEffectDone(effect.id) }
                )
            }
        }
    }
}

/**
 * Decorative Clash Royale River & Dual Bridges across the middle of the 8x8 chessboard.
 */
@Composable
private fun ClashRiverDividerOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // River Water Strip
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xCC0288D1), Color(0xEE29B6F6), Color(0xCC0288D1))
            ),
            topLeft = Offset(0f, 0f),
            size = Size(w, h)
        )

        // Left & Right Wooden Arena Bridges (aligned with columns 1-2 and 5-6)
        val bridgeWidth = w * 0.20f
        val leftBridgeX = w * 0.14f
        val rightBridgeX = w * 0.66f

        listOf(leftBridgeX, rightBridgeX).forEach { bx ->
            drawRoundRect(
                color = Color(0xFF8D6E63),
                topLeft = Offset(bx, -2f),
                size = Size(bridgeWidth, h + 4f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = Color(0xFF4E342E),
                topLeft = Offset(bx, -2f),
                size = Size(bridgeWidth, h + 4f),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 2.5f)
            )
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                .border(1.5.dp, Color(0xFF475569), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Red Team Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(PieceTeam.RED.primary, CircleShape)
                )
                Text(
                    text = "RED KING: $redCrowns 👑",
                    color = Color(0xFFEF5350),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Reset / Demo FX Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFB300))
                    .clickable { onResetBoard() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "RESET ARENA",
                    color = Color(0xFF111722),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Blue Team Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "👑 $blueCrowns :BLUE KING",
                    color = Color(0xFF64B5F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(PieceTeam.BLUE.primary, CircleShape)
                )
            }
        }

        // Action Banner
        Text(
            text = statusBanner,
            color = Color(0xFFFFD54F),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun ClashElixirDeckDock(
    elixir: Int,
    selectedCard: ChessPieceType?,
    onSelectCard: (ChessPieceType) -> Unit
) {
    val handCards = remember {
        listOf(
            Triple(ChessPieceType.PAWN, CardRarity.COMMON, 11),
            Triple(ChessPieceType.KNIGHT, CardRarity.RARE, 12),
            Triple(ChessPieceType.BISHOP, CardRarity.EPIC, 13),
            Triple(ChessPieceType.QUEEN, CardRarity.LEGENDARY, 14)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A2433), RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 4-Card Clash Royale Battle Hand
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            handCards.forEach { (type, rarity, lvl) ->
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

        // 10-Slot Segmented Purple Elixir Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElixirBadge(cost = elixir, modifier = Modifier.size(26.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF4A0072), RoundedCornerShape(7.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (i in 1..10) {
                    val filled = i <= elixir
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (filled) {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFEA80FC), Color(0xFFAA00FF))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                    )
                                }
                            )
                    )
                }
            }
        }
    }
}

/**
 * Creates a balanced Clash Royale Chess formation so the user can immediately test moves & captures.
 */
private fun createInitialArenaPieces(): List<ArenaPiece> {
    val backRowOrder = listOf(
        ChessPieceType.ROOK,
        ChessPieceType.KNIGHT,
        ChessPieceType.BISHOP,
        ChessPieceType.QUEEN,
        ChessPieceType.KING,
        ChessPieceType.BISHOP,
        ChessPieceType.KNIGHT,
        ChessPieceType.ROOK
    )

    val list = mutableListOf<ArenaPiece>()

    // Red Team (Top rows 0 and 1, with two advanced pawns for quick capture testing)
    backRowOrder.forEachIndexed { col, type ->
        list.add(ArenaPiece("red_back_$col", type, PieceTeam.RED, row = 0, col = col))
    }
    for (col in 0 until 8) {
        val startRow = if (col == 3 || col == 4) 3 else 1
        list.add(ArenaPiece("red_pawn_$col", ChessPieceType.PAWN, PieceTeam.RED, row = startRow, col = col))
    }

    // Blue Team (Bottom rows 6 and 7, with two advanced center pawns ready to clash)
    for (col in 0 until 8) {
        val startRow = if (col == 2 || col == 4) 4 else 6
        list.add(ArenaPiece("blue_pawn_$col", ChessPieceType.PAWN, PieceTeam.BLUE, row = startRow, col = col))
    }
    backRowOrder.forEachIndexed { col, type ->
        list.add(ArenaPiece("blue_back_$col", type, PieceTeam.BLUE, row = 7, col = col))
    }

    return list
}

/**
 * Calculates valid chess moves and capture squares for the selected piece on the 8x8 board.
 */
private fun calculateValidMoves(
    piece: ArenaPiece,
    allPieces: List<ArenaPiece>
): Set<Pair<Int, Int>> {
    val moves = mutableSetOf<Pair<Int, Int>>()
    val boardMap = allPieces.associateBy { it.row to it.col }

    fun addStepIfValid(r: Int, c: Int): Boolean {
        if (r !in 0..7 || c !in 0..7) return false
        val occupant = boardMap[r to c]
        return if (occupant == null) {
            moves.add(r to c)
            true
        } else {
            if (occupant.team != piece.team) {
                moves.add(r to c)
            }
            false
        }
    }

    fun traceRay(dr: Int, dc: Int) {
        var r = piece.row + dr
        var c = piece.col + dc
        while (addStepIfValid(r, c)) {
            r += dr
            c += dc
        }
    }

    when (piece.type) {
        ChessPieceType.PAWN -> {
            val dir = if (piece.team == PieceTeam.BLUE) -1 else 1
            val startRow = if (piece.team == PieceTeam.BLUE) 6 else 1
            val nextRow = piece.row + dir
            // 1 step forward
            if (nextRow in 0..7 && boardMap[nextRow to piece.col] == null) {
                moves.add(nextRow to piece.col)
                // 2 steps from starting rank
                val twoStepRow = piece.row + dir * 2
                if (piece.row == startRow && boardMap[twoStepRow to piece.col] == null) {
                    moves.add(twoStepRow to piece.col)
                }
            }
            // Diagonal captures
            listOf(piece.col - 1, piece.col + 1).forEach { diagCol ->
                val target = boardMap[nextRow to diagCol]
                if (target != null && target.team != piece.team) {
                    moves.add(nextRow to diagCol)
                }
            }
        }

        ChessPieceType.KNIGHT -> {
            val offsets = listOf(
                -2 to -1, -2 to 1,
                -1 to -2, -1 to 2,
                1 to -2, 1 to 2,
                2 to -1, 2 to 1
            )
            offsets.forEach { (dr, dc) ->
                addStepIfValid(piece.row + dr, piece.col + dc)
            }
        }

        ChessPieceType.BISHOP -> {
            traceRay(-1, -1)
            traceRay(-1, 1)
            traceRay(1, -1)
            traceRay(1, 1)
        }

        ChessPieceType.ROOK -> {
            traceRay(-1, 0)
            traceRay(1, 0)
            traceRay(0, -1)
            traceRay(0, 1)
        }

        ChessPieceType.QUEEN -> {
            traceRay(-1, -1); traceRay(-1, 1); traceRay(1, -1); traceRay(1, 1)
            traceRay(-1, 0); traceRay(1, 0); traceRay(0, -1); traceRay(0, 1)
        }

        ChessPieceType.KING -> {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr != 0 || dc != 0) {
                        addStepIfValid(piece.row + dr, piece.col + dc)
                    }
                }
            }
        }
    }

    return moves
}

/**
 * Interactive Preview showcasing the Clash Royale Battle Arena Chessboard, Particle Effects, and Deck Dock.
 */
@Preview(showBackground = true, backgroundColor = 0xFF0D1520, widthDp = 420, heightDp = 760)
@Composable
fun ClashRoyaleChessPiecesPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0D1520)
        ) {
            ClashBattleArenaScreen()
        }
    }
}