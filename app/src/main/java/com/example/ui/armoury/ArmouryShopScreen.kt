package com.example.ui.armoury

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserProfileEntity
import com.example.data.model.ArenaSkin
import com.example.data.model.CosmeticSkin
import com.example.data.model.GameCatalog
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ArmouryShopScreen(
    profile: UserProfileEntity?,
    onSelectArena: (String) -> Unit
) {
    var activeFilter by remember { mutableStateOf("ALL") } // "ALL", "HERO", "UNIT", "ARENA", "VAULT"
    var inspectingSkin by remember { mutableStateOf<CosmeticSkin?>(null) }
    var equippedSkinIds by remember { mutableStateOf(setOf("skin_hierophant_cosmic")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        // Filter Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                Pair("ALL", "✨ All Cosmetics"),
                Pair("HERO", "👑 Hero Skins"),
                Pair("UNIT", "⚔️ Unit Skins"),
                Pair("ARENA", "🏟️ Battle Arenas"),
                Pair("VAULT", "💎 Gem Vault")
            )
            items(filters) { (key, label) ->
                val isSelected = activeFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) ElegantPrimaryLavender else ElegantCardBg)
                        .border(
                            1.dp,
                            if (isSelected) ElegantPrimaryLavender else ElegantBorder,
                            RoundedCornerShape(50)
                        )
                        .clickable { activeFilter = key }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) ElegantPrimaryDark else ElegantTextPrimary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Fairness Notice Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimaryLavender.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🛡️", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "100% PURELY COSMETIC",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = ElegantPrimaryLavender
                            )
                            Text(
                                text = "All unit & hero skins provide zero gameplay advantage, stat boosts, or speed buffs. Unlocked via Shop, Pass Tiers, Chests & Tournaments.",
                                fontSize = 10.sp,
                                color = ElegantTextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Hero Skins Section
            if (activeFilter == "ALL" || activeFilter == "HERO") {
                item {
                    Text(
                        text = "GRANDMASTER HERO SKINS",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        color = ElegantPrimaryLavender
                    )
                }

                val heroSkins = GameCatalog.PIECE_SKINS.filter { it.targetType == "HERO" }
                items(heroSkins) { skin ->
                    CosmeticSkinCard(
                        skin = skin,
                        isEquipped = equippedSkinIds.contains(skin.id),
                        onInspect = { inspectingSkin = skin },
                        onToggleEquip = {
                            equippedSkinIds = if (equippedSkinIds.contains(skin.id)) {
                                equippedSkinIds - skin.id
                            } else {
                                equippedSkinIds + skin.id
                            }
                        }
                    )
                }
            }

            // Unit Skins Section
            if (activeFilter == "ALL" || activeFilter == "UNIT") {
                item {
                    Text(
                        text = "MYTHIC CHESS PIECE SKINS",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        color = ElegantPrimaryLavender
                    )
                }

                val unitSkins = GameCatalog.PIECE_SKINS.filter { it.targetType == "CARD" }
                items(unitSkins) { skin ->
                    CosmeticSkinCard(
                        skin = skin,
                        isEquipped = equippedSkinIds.contains(skin.id),
                        onInspect = { inspectingSkin = skin },
                        onToggleEquip = {
                            equippedSkinIds = if (equippedSkinIds.contains(skin.id)) {
                                equippedSkinIds - skin.id
                            } else {
                                equippedSkinIds + skin.id
                            }
                        }
                    )
                }
            }

            // Arena Skins Section
            if (activeFilter == "ALL" || activeFilter == "ARENA") {
                item {
                    Text(
                        text = "BATTLEFIELD ARENA SKINS",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        color = ElegantPrimaryLavender
                    )
                }

                items(GameCatalog.ARENA_SKINS) { arena ->
                    val isSelected = arena.id == (profile?.selectedArenaId ?: "arena_titans_peak")
                    ArenaSkinCard(
                        arena = arena,
                        isSelected = isSelected,
                        onSelect = { onSelectArena(arena.id) }
                    )
                }
            }

            // Vault Section
            if (activeFilter == "ALL" || activeFilter == "VAULT") {
                item {
                    Text(
                        text = "VAULT & COSMETIC PACKAGES",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        color = ElegantPrimaryLavender
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "💎 GEM & GOLD TREASURY",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = ElegantPrimaryLavender
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                VaultPackItem(name = "Daily Shard Pouch", amount = "500 💎", price = "CLAIM FREE", modifier = Modifier.weight(1f))
                                VaultPackItem(name = "Grandmaster Chest", amount = "25,000 🪙", price = "100 💎", modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Inspect Skin Dialog Modal
    inspectingSkin?.let { skin ->
        SkinInspectDialog(
            skin = skin,
            isEquipped = equippedSkinIds.contains(skin.id),
            onToggleEquip = {
                equippedSkinIds = if (equippedSkinIds.contains(skin.id)) {
                    equippedSkinIds - skin.id
                } else {
                    equippedSkinIds + skin.id
                }
            },
            onDismiss = { inspectingSkin = null }
        )
    }
}

@Composable
private fun CosmeticSkinCard(
    skin: CosmeticSkin,
    isEquipped: Boolean,
    onInspect: () -> Unit,
    onToggleEquip: () -> Unit
) {
    val rarityColor = when (skin.rarity) {
        "MYTHIC" -> Color(0xFFFFD700)
        "LEGENDARY" -> ElegantPrimaryLavender
        "EPIC" -> Color(0xFFBB86FC)
        "RARE" -> ElegantCoral
        else -> ElegantTextSecondary
    }

    val methodBadge = when (skin.acquisitionMethod) {
        "SHOP" -> Pair("🛒 Shop Purchase", ElegantCoral)
        "BATTLE_PASS" -> Pair("👑 Battle Pass Tier", ElegantPrimaryLavender)
        "CHEST_DROP" -> Pair("🎁 Mystery Chest Drop", Color(0xFFFFB74D))
        "TOURNAMENT" -> Pair("🏆 Tournament Victory", Color(0xFF64B5F6))
        "RANKED" -> Pair("🎖️ Ranked Grandmaster", Color(0xFFFFD54F))
        else -> Pair("✨ Cosmetic", ElegantTextSecondary)
    }

    val foilColors = HolographicPalettes.getFoilColors(skin.rarity)
    val isHighRarity = skin.rarity in listOf("MYTHIC", "LEGENDARY", "EPIC")

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isHighRarity || isEquipped) 1.5.dp else 1.dp,
            brush = if (isHighRarity || isEquipped) Brush.linearGradient(foilColors) else Brush.linearGradient(listOf(ElegantBorder, ElegantBorder))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .holographicPopOnTouch(onClick = onInspect, scaleUp = 1.03f, elevationDp = 8.dp)
            .shadow(
                elevation = if (isEquipped) 8.dp else 2.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = rarityColor,
                spotColor = rarityColor
            )
            .testTag("card_cosmetic_skin_${skin.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Acquisition & Rarity Tag
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    PrismaticRarityBadge(rarity = skin.rarity)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElegantDarkBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = methodBadge.first, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = methodBadge.second)
                    }
                }

                if (isEquipped) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(ElegantEmerald.copy(alpha = 0.2f))
                            .border(1.dp, ElegantEmerald, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "EQUIPPED ✓", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ElegantEmerald)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Skin Visual Box with Holographic Foil & 3D Floating Pop Piece
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    skin.bgGradientColors.map { Color(it) }
                                )
                            )
                            .holographicFoil(rarity = skin.rarity, intensity = if (isHighRarity) 0.85f else 0.4f)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(foilColors),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isHighRarity) {
                            HolographicSparklesOverlay(sparkleCount = 2, color = rarityColor)
                        }
                        HolographicFloatingPiece(
                            symbolChar = skin.symbolChar,
                            fontSize = 26.sp,
                            rarity = skin.rarity
                        )
                    }

                    Column {
                        Text(
                            text = skin.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "For: ${skin.targetName} • ${skin.themeName}",
                            fontSize = 10.sp,
                            color = ElegantTextSecondary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onToggleEquip,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEquipped) ElegantPrimaryDark else ElegantPrimaryLavender
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isEquipped) "UNEQUIP" else "EQUIP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isEquipped) ElegantPrimaryLavender else ElegantPrimaryDark
                        )
                    }
                }
            }

            // Visual Effects brief
            Text(
                text = "✨ FX: ${skin.visualEffects}",
                fontSize = 9.sp,
                color = ElegantTextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SkinInspectDialog(
    skin: CosmeticSkin,
    isEquipped: Boolean,
    onToggleEquip: () -> Unit,
    onDismiss: () -> Unit
) {
    val rarityColor = HolographicPalettes.getGlowColor(skin.rarity)
    val foilColors = HolographicPalettes.getFoilColors(skin.rarity)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ElegantCardBg,
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(foilColors)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = rarityColor, spotColor = rarityColor)
                .testTag("dialog_skin_inspect")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrismaticRarityBadge(rarity = "${skin.rarity} COSMETIC")

                    Text(
                        text = "100% Visual Only",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantEmerald
                    )
                }

                // Big Hero Preview Box with Holographic Foil & 3D Floating Pop Piece
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                skin.bgGradientColors.map { Color(it) }
                            )
                        )
                        .holographicFoil(rarity = skin.rarity, intensity = 1.0f)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(foilColors),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .shadow(14.dp, RoundedCornerShape(22.dp), ambientColor = rarityColor, spotColor = rarityColor),
                    contentAlignment = Alignment.Center
                ) {
                    HolographicSparklesOverlay(sparkleCount = 4, color = Color.White)
                    HolographicFloatingPiece(
                        symbolChar = skin.symbolChar,
                        fontSize = 54.sp,
                        rarity = skin.rarity
                    )
                }

                Text(
                    text = skin.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = ElegantTextPrimary
                )

                Text(
                    text = "Applied to: ${skin.targetName} (${skin.themeName})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantPrimaryLavender
                )

                Text(
                    text = skin.description,
                    fontSize = 11.sp,
                    color = ElegantTextSecondary,
                    lineHeight = 16.sp
                )

                // FX Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkBg)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🌟 VISUAL PARTICLES & SOUND FX",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ElegantPrimaryLavender
                        )
                        Text(
                            text = skin.visualEffects,
                            fontSize = 10.sp,
                            color = ElegantTextPrimary
                        )
                    }
                }

                // How to Acquire Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantPrimaryDark.copy(alpha = 0.5f))
                        .border(1.dp, ElegantPrimaryLavender.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🎁 HOW TO ACQUIRE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ElegantAccentLight
                        )
                        Text(
                            text = skin.acquisitionDetail,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElegantTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CLOSE", fontSize = 11.sp, color = ElegantTextSecondary)
                    }

                    Button(
                        onClick = {
                            onToggleEquip()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEquipped) ElegantPrimaryDark else ElegantPrimaryLavender
                        )
                    ) {
                        Text(
                            text = if (isEquipped) "UNEQUIP" else "EQUIP SKIN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isEquipped) ElegantPrimaryLavender else ElegantPrimaryDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArenaSkinCard(
    arena: ArenaSkin,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ElegantPrimaryLavender else ElegantBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_arena_skin_${arena.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(arena.darkTileHex))
                        .border(1.5.dp, Color(arena.borderHex), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏟️", fontSize = 18.sp)
                }

                Column {
                    Text(text = arena.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ElegantTextPrimary)
                    Text(text = arena.description, fontSize = 10.sp, color = ElegantTextSecondary, maxLines = 1)
                }
            }

            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) ElegantPrimaryDark else ElegantPrimaryLavender
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isSelected) "EQUIPPED" else "EQUIP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) ElegantPrimaryLavender else ElegantPrimaryDark
                )
            }
        }
    }
}

@Composable
private fun VaultPackItem(
    name: String,
    amount: String,
    price: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ElegantDarkBg)
            .border(1.dp, ElegantBorder, RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = amount, fontWeight = FontWeight.Black, fontSize = 13.sp, color = ElegantAccentLight)
        Text(text = name, fontSize = 9.sp, color = ElegantTextSecondary)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(ElegantPrimaryDark)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(text = price, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
        }
    }
}
