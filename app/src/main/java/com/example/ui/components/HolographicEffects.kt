package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Iridescent Color Palettes for Holographic Foil Effects by Rarity
 */
object HolographicPalettes {
    val MythicPrism = listOf(
        Color(0xFFFF0055).copy(alpha = 0.45f),
        Color(0xFFFFB700).copy(alpha = 0.50f),
        Color(0xFF00FFCC).copy(alpha = 0.55f),
        Color(0xFF0099FF).copy(alpha = 0.50f),
        Color(0xFFCC00FF).copy(alpha = 0.45f),
        Color(0xFFFF0055).copy(alpha = 0.45f)
    )

    val LegendaryGold = listOf(
        Color(0xFFFFD700).copy(alpha = 0.40f),
        Color(0xFFFFF176).copy(alpha = 0.55f),
        Color(0xFFFF80AB).copy(alpha = 0.35f),
        Color(0xFF80D8FF).copy(alpha = 0.45f),
        Color(0xFFFFD700).copy(alpha = 0.40f)
    )

    val EpicViolet = listOf(
        Color(0xFFBB86FC).copy(alpha = 0.35f),
        Color(0xFF00E5FF).copy(alpha = 0.45f),
        Color(0xFFFF4081).copy(alpha = 0.40f),
        Color(0xFF7C4DFF).copy(alpha = 0.35f)
    )

    val RareCyan = listOf(
        Color(0xFF00E5FF).copy(alpha = 0.30f),
        Color(0xFF69F0AE).copy(alpha = 0.35f),
        Color(0xFF40C4FF).copy(alpha = 0.30f)
    )

    val CommonSilver = listOf(
        Color(0xFFE0E0E0).copy(alpha = 0.18f),
        Color(0xFFFFFFFF).copy(alpha = 0.30f),
        Color(0xFF9E9E9E).copy(alpha = 0.18f)
    )

    fun getFoilColors(rarity: String): List<Color> = when (rarity.uppercase()) {
        "MYTHIC" -> MythicPrism
        "LEGENDARY" -> LegendaryGold
        "EPIC" -> EpicViolet
        "RARE" -> RareCyan
        else -> CommonSilver
    }

    fun getGlowColor(rarity: String): Color = when (rarity.uppercase()) {
        "MYTHIC" -> Color(0xFFFFD700)
        "LEGENDARY" -> ElegantPrimaryLavender
        "EPIC" -> Color(0xFFBB86FC)
        "RARE" -> Color(0xFF00E5FF)
        else -> ElegantBorder
    }
}

/**
 * Modifier that renders an animated iridescent holographic foil gleam and diagonal specular shine.
 */
fun Modifier.holographicFoil(
    rarity: String = "LEGENDARY",
    intensity: Float = 1.0f
): Modifier = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "hologram_transition")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "foil_shimmer"
    )

    val rainbowShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbow_rotation"
    )

    val foilColors = remember(rarity) { HolographicPalettes.getFoilColors(rarity) }

    this.drawWithContent {
        drawContent()

        // 1. Prismatic Foil Color Shift Layer
        val angleRad = Math.toRadians(rainbowShift.toDouble())
        val startX = (size.width / 2) + (cos(angleRad) * size.width).toFloat()
        val startY = (size.height / 2) + (sin(angleRad) * size.height).toFloat()
        val endX = (size.width / 2) - (cos(angleRad) * size.width).toFloat()
        val endY = (size.height / 2) - (sin(angleRad) * size.height).toFloat()

        drawRect(
            brush = Brush.linearGradient(
                colors = foilColors.map { it.copy(alpha = it.alpha * intensity) },
                start = Offset(startX, startY),
                end = Offset(endX, endY)
            ),
            blendMode = BlendMode.Screen
        )

        // 2. Specular Sweeping Diagonal Glint Beam
        val glintWidth = size.width * 0.45f
        val glintCenter = shimmerProgress * (size.width + glintWidth * 2) - glintWidth
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.15f * intensity),
                    Color.White.copy(alpha = 0.45f * intensity),
                    Color.White.copy(alpha = 0.15f * intensity),
                    Color.Transparent
                ),
                start = Offset(glintCenter - glintWidth / 2, 0f),
                end = Offset(glintCenter + glintWidth / 2, size.height)
            ),
            blendMode = BlendMode.Plus
        )
    }
}

/**
 * 3D Pop & Hover interaction modifier with tactile spring physics and elevation depth.
 */
fun Modifier.holographicPopOnTouch(
    onClick: (() -> Unit)? = null,
    scaleUp: Float = 1.06f,
    elevationDp: Dp = 12.dp
): Modifier = this.composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scaleUp else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_pop_scale"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) elevationDp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_pop_elevation"
    )

    this
        .scale(animatedScale)
        .shadow(
            elevation = animatedElevation,
            shape = RoundedCornerShape(16.dp),
            ambientColor = ElegantPrimaryLavender.copy(alpha = 0.35f),
            spotColor = ElegantPrimaryLavender.copy(alpha = 0.5f)
        )
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )
}

/**
 * Holographic 3D Floating Piece Emblem:
 * Levitation offset and drop shadow that makes the chess piece symbol literally POP OFF the card!
 */
@Composable
fun HolographicFloatingPiece(
    symbolChar: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 48.sp,
    rarity: String = "LEGENDARY",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "piece_levitate")

    // Vertical floating hover bobbing
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "piece_float_offset"
    )

    // Pulse scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "piece_pulse"
    )

    val charInfo = remember(symbolChar) { CharacterIdentity.fromIdOrSymbol(symbolChar) }
    val glowColor = HolographicPalettes.getGlowColor(rarity)

    Box(
        modifier = modifier
            .offset(y = floatOffset.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Ambient 3D Shadow underneath floating piece
        Canvas(
            modifier = Modifier
                .size(width = (fontSize.value * 1.1).dp, height = (fontSize.value * 0.35).dp)
                .offset(y = (fontSize.value * 0.5).dp)
        ) {
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.65f),
                        Color.Transparent
                    )
                )
            )
        }

        // Glowing Backing Aura
        Text(
            text = charInfo.characterEmoji,
            fontSize = fontSize,
            modifier = Modifier
                .offset(x = 1.dp, y = 1.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = charInfo.weaponGlowColor,
                    spotColor = glowColor
                )
        )

        // Main Vivid Living Character Avatar
        Text(
            text = charInfo.characterEmoji,
            fontSize = fontSize
        )
    }
}

/**
 * Twinkling Holographic Star Sparkles overlay
 */
@Composable
fun HolographicSparklesOverlay(
    modifier: Modifier = Modifier,
    sparkleCount: Int = 4,
    color: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha"
    )

    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val sparkles = listOf(
            Offset(size.width * 0.18f, size.height * 0.22f) to 5.dp.toPx(),
            Offset(size.width * 0.82f, size.height * 0.28f) to 7.dp.toPx(),
            Offset(size.width * 0.25f, size.height * 0.75f) to 6.dp.toPx(),
            Offset(size.width * 0.78f, size.height * 0.80f) to 4.5.dp.toPx()
        ).take(sparkleCount)

        sparkles.forEachIndexed { idx, (pos, sSize) ->
            val curAlpha = if (idx % 2 == 0) alphaAnim else (1.2f - alphaAnim).coerceIn(0.1f, 1f)
            val rot = rotationAnim + (idx * 45f)
            drawSparkleStar(pos, sSize, color.copy(alpha = curAlpha * 0.8f), rot)
        }
    }
}

private fun DrawScope.drawSparkleStar(center: Offset, sizePx: Float, color: Color, rotationDeg: Float) {
    rotate(rotationDeg, pivot = center) {
        // Vertical ray
        drawLine(
            color = color,
            start = Offset(center.x, center.y - sizePx),
            end = Offset(center.x, center.y + sizePx),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
        // Horizontal ray
        drawLine(
            color = color,
            start = Offset(center.x - sizePx, center.y),
            end = Offset(center.x + sizePx, center.y),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
        // Inner diamond core
        drawCircle(
            color = Color.White.copy(alpha = color.alpha),
            radius = sizePx * 0.25f,
            center = center
        )
    }
}

/**
 * Prismatic Badge with shimmering animated border and holographic text styling.
 */
@Composable
fun PrismaticRarityBadge(
    rarity: String,
    modifier: Modifier = Modifier
) {
    val glowColor = HolographicPalettes.getGlowColor(rarity)
    val foilColors = HolographicPalettes.getFoilColors(rarity)

    val infiniteTransition = rememberInfiniteTransition(label = "badge_shimmer")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "badge_grad"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(ElegantDarkBg, ElegantCardBgElevated),
                    start = Offset(gradientOffset, 0f),
                    end = Offset(gradientOffset + 100f, 50f)
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(foilColors),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✦ $rarity ✦",
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = glowColor,
            letterSpacing = 0.5.sp
        )
    }
}
