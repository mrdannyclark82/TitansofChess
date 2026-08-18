package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Character Graphic System: Replaces traditional generic unicode chess glyphs
 * with vibrant, living fantasy character artwork, animated dynamic combat auras,
 * detailed warrior tokens, and glowing magical elements.
 */

enum class CharacterIdentity(
    val id: String,
    val displayName: String,
    val characterEmoji: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val weaponGlowColor: Color,
    val subtitle: String
) {
    VALKYRIE_QUEEN(
        id = "card_queen_valkyrie",
        displayName = "Valkyrie Queen",
        characterEmoji = "⚔️",
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFF9C27B0),
        weaponGlowColor = Color(0xFFFFEB3B),
        subtitle = "Solar Blade Master"
    ),
    HIEROPHANT(
        id = "card_hierophant",
        displayName = "The Hierophant",
        characterEmoji = "🔮",
        primaryColor = Color(0xFF7C4DFF),
        secondaryColor = Color(0xFF00E5FF),
        weaponGlowColor = Color(0xFF651FFF),
        subtitle = "Arcane Leyline Mage"
    ),
    KNIGHT_PALADIN(
        id = "card_knight_paladin",
        displayName = "Knight Paladin",
        characterEmoji = "🐎",
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF00B0FF),
        weaponGlowColor = Color(0xFF1DE9B6),
        subtitle = "Valiant Winged Charger"
    ),
    SIEGE_ROOK(
        id = "card_siege_rook",
        displayName = "Siege Golem Rook",
        characterEmoji = "🏰",
        primaryColor = Color(0xFFFF5722),
        secondaryColor = Color(0xFF795548),
        weaponGlowColor = Color(0xFFFFAB00),
        subtitle = "Molten Fortress Colossus"
    ),
    PAWN_SENTINELS(
        id = "card_pawn_sentinels",
        displayName = "Pawn Sentinels",
        characterEmoji = "🛡️",
        primaryColor = Color(0xFF90A4AE),
        secondaryColor = Color(0xFF37474F),
        weaponGlowColor = Color(0xFF64B5F6),
        subtitle = "Spartan Vanguard Swarm"
    ),
    THUNDER_SPELL(
        id = "card_thunder_spell",
        displayName = "Thunder Titan Strike",
        characterEmoji = "⚡",
        primaryColor = Color(0xFFFFEA00),
        secondaryColor = Color(0xFFFF6D00),
        weaponGlowColor = Color(0xFFFFF176),
        subtitle = "Celestial Lightning"
    ),
    SHADOW_ASSASSIN(
        id = "card_shadow_assassin",
        displayName = "Shadow Bishop",
        characterEmoji = "🗡️",
        primaryColor = Color(0xFFE040FB),
        secondaryColor = Color(0xFF311B92),
        weaponGlowColor = Color(0xFF7C4DFF),
        subtitle = "Void Strike Infiltrator"
    ),
    ROYAL_GUARDIAN(
        id = "card_royal_guardian",
        displayName = "King's Vanguard",
        characterEmoji = "🤴",
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFD50000),
        weaponGlowColor = Color(0xFFFFAB00),
        subtitle = "Sovereign Warlord"
    ),
    HERO_KASPAROV(
        id = "hero_kasparov",
        displayName = "Kasparov_99",
        characterEmoji = "👑",
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFF3F51B5),
        weaponGlowColor = Color(0xFFFFE082),
        subtitle = "Grandmaster Tactician"
    ),
    HERO_VESPERA(
        id = "hero_vespera",
        displayName = "Vespera Nyx",
        characterEmoji = "🌙",
        primaryColor = Color(0xFFBB86FC),
        secondaryColor = Color(0xFF4A148C),
        weaponGlowColor = Color(0xFFE1BEE7),
        subtitle = "Shadow Sovereign"
    ),
    HERO_MAGNUS(
        id = "hero_magnus",
        displayName = "Magnus Ironclad",
        characterEmoji = "🌋",
        primaryColor = Color(0xFFFF6E40),
        secondaryColor = Color(0xFF263238),
        weaponGlowColor = Color(0xFFFFAB91),
        subtitle = "Colossus Smith"
    );

    companion object {
        fun fromIdOrSymbol(idOrSymbol: String?): CharacterIdentity {
            if (idOrSymbol == null) return PAWN_SENTINELS
            val lower = idOrSymbol.lowercase()
            return when {
                lower.contains("queen") || lower.contains("valkyrie") || idOrSymbol == "♕" || idOrSymbol == "♛" -> VALKYRIE_QUEEN
                lower.contains("hierophant") || (lower.contains("bishop") && !lower.contains("shadow")) || idOrSymbol == "♗" || idOrSymbol == "♝" -> HIEROPHANT
                lower.contains("knight") || lower.contains("paladin") || idOrSymbol == "♘" || idOrSymbol == "♞" -> KNIGHT_PALADIN
                lower.contains("rook") || lower.contains("siege") || idOrSymbol == "♖" || idOrSymbol == "♜" -> SIEGE_ROOK
                lower.contains("pawn") || lower.contains("sentinel") || idOrSymbol == "♙" || idOrSymbol == "♟" -> PAWN_SENTINELS
                lower.contains("thunder") || lower.contains("lightning") || lower.contains("spell") || idOrSymbol == "⚡" -> THUNDER_SPELL
                lower.contains("shadow") || lower.contains("assassin") || lower.contains("void") -> SHADOW_ASSASSIN
                lower.contains("king") || lower.contains("vanguard") || idOrSymbol == "♔" || idOrSymbol == "♚" -> ROYAL_GUARDIAN
                lower.contains("kasparov") -> HERO_KASPAROV
                lower.contains("vespera") -> HERO_VESPERA
                lower.contains("magnus") -> HERO_MAGNUS
                else -> PAWN_SENTINELS
            }
        }
    }
}

/**
 * Living Character Card Portrait with animated magical aura, combat emblems,
 * weapon sparkles, and layered fantasy styling.
 */
@Composable
fun LivingCharacterPortrait(
    cardId: String?,
    rarity: String,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    showAura: Boolean = true
) {
    val charInfo = remember(cardId) { CharacterIdentity.fromIdOrSymbol(cardId) }
    val infiniteTransition = rememberInfiniteTransition(label = "char_portrait_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "char_pulse"
    )

    val auraRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aura_rot"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
            .background(
                Brush.radialGradient(
                    listOf(
                        charInfo.primaryColor.copy(alpha = 0.35f),
                        charInfo.secondaryColor.copy(alpha = 0.6f),
                        Color(0xFF0F1115)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Rotating Magical Aura Ring
        if (showAura) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(this.size.width / 2, this.size.height / 2)
                val radius = this.size.minDimension * 0.42f
                val color = charInfo.primaryColor.copy(alpha = 0.4f)

                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.Transparent,
                            color,
                            charInfo.weaponGlowColor.copy(alpha = 0.7f),
                            Color.Transparent
                        ),
                        center = center
                    ),
                    radius = radius,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Central Character Graphic Composition
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(pulseScale)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Character Weapon / Element Embellishment
                Text(
                    text = charInfo.characterEmoji,
                    fontSize = (size.value * 0.48f).sp,
                    modifier = Modifier.shadow(8.dp, CircleShape, ambientColor = charInfo.primaryColor, spotColor = charInfo.weaponGlowColor)
                )
            }
        }
    }
}

/**
 * Tactical Living Battle Token for pieces positioned on the 8x8 Tactical Grid.
 * Replaces cold text chess glyphs with animated 3D warrior tokens, team banners,
 * and high-visibility status glows.
 */
@Composable
fun TacticalBattlePieceToken(
    pieceId: String?,
    pieceSymbol: String?,
    isPlayerPiece: Boolean,
    modifier: Modifier = Modifier
) {
    val charInfo = remember(pieceId ?: pieceSymbol) {
        CharacterIdentity.fromIdOrSymbol(pieceId ?: pieceSymbol)
    }

    val teamGlowColor = if (isPlayerPiece) Color(0xFF64B5F6) else Color(0xFFFF5252)
    val teamBadgeColor = if (isPlayerPiece) Color(0xFF1E88E5) else Color(0xFFD32F2F)

    val infiniteTransition = rememberInfiniteTransition(label = "piece_token_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "piece_float"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(1.5.dp),
        contentAlignment = Alignment.Center
    ) {
        // Base Tactical Disc with Metallic Team Ring
        Box(
            modifier = Modifier
                .fillMaxSize(0.92f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            charInfo.primaryColor.copy(alpha = 0.25f),
                            if (isPlayerPiece) Color(0xFF1A2733) else Color(0xFF331A1A),
                            Color(0xFF101216)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            teamBadgeColor,
                            charInfo.primaryColor,
                            teamBadgeColor.copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = teamGlowColor,
                    spotColor = teamGlowColor
                ),
            contentAlignment = Alignment.Center
        ) {
            // Team Crown / Indicator Dot at Top
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 1.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(teamGlowColor)
            )

            // Character Living Emoji / Avatar
            Text(
                text = charInfo.characterEmoji,
                fontSize = 16.sp,
                modifier = Modifier
                    .offset(y = floatOffset.dp)
                    .shadow(4.dp, CircleShape, ambientColor = charInfo.weaponGlowColor, spotColor = charInfo.primaryColor)
            )
        }
    }
}

/**
 * Living Character Badge showing name, animated portrait, archetype role, and power aura.
 */
@Composable
fun LivingCharacterBadge(
    cardId: String?,
    rarity: String,
    modifier: Modifier = Modifier
) {
    val charInfo = remember(cardId) { CharacterIdentity.fromIdOrSymbol(cardId) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E2128))
            .border(1.dp, charInfo.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        LivingCharacterPortrait(cardId = cardId, rarity = rarity, size = 26.dp, showAura = false)

        Column {
            Text(
                text = charInfo.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantTextPrimary
            )
            Text(
                text = charInfo.subtitle,
                fontSize = 8.5.sp,
                color = charInfo.primaryColor
            )
        }
    }
}
