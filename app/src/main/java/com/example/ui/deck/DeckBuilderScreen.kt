package com.example.ui.deck

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.PlayerCardEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.CardModel
import com.example.data.model.CosmeticSkin
import com.example.data.model.GameCatalog
import com.example.data.model.HeroModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DeckBuilderScreen(
    profile: UserProfileEntity?,
    allPlayerCards: List<PlayerCardEntity>,
    deckPlayerCards: List<PlayerCardEntity>,
    onUpgradeCard: (String) -> Unit,
    onSwapCard: (String, Int) -> Unit,
    onSelectHero: (String) -> Unit
) {
    var selectedCardForDetail by remember { mutableStateOf<Pair<CardModel, PlayerCardEntity>?>(null) }
    var showHeroPicker by remember { mutableStateOf(false) }

    val activeHero = GameCatalog.getHero(profile?.selectedHeroId ?: "hero_kasparov")
    val avgElixir = remember(deckPlayerCards) {
        if (deckPlayerCards.isEmpty()) 4.1f
        else {
            val total = deckPlayerCards.sumOf {
                GameCatalog.getCard(it.cardId).elixirCost
            }
            total.toFloat() / deckPlayerCards.size.toFloat()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp)
        ) {
            // 1. Active Hero Banner
            item {
                ActiveHeroCard(
                    hero = activeHero,
                    onChangeHero = { showHeroPicker = true }
                )
            }

            // 2. Battle Deck Header & Avg Elixir
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BATTLE DECK (8 CARDS)",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp,
                            color = ElegantPrimaryLavender
                        )
                        Text(
                            text = "Avg Elixir: ${String.format("%.1f", avgElixir)} ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantAccentLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(ElegantPrimaryDark)
                            .border(1.dp, ElegantPrimaryLavender.copy(alpha = 0.4f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DECK 1 (ACTIVE)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantPrimaryLavender
                        )
                    }
                }
            }

            // 3. Active 8-Card Grid
            item {
                val deckSlots = (0..7).map { slotIdx ->
                    deckPlayerCards.find { it.deckSlot == slotIdx } ?: deckPlayerCards.getOrNull(slotIdx)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ElegantCardBg)
                        .border(1.dp, ElegantBorder, RoundedCornerShape(20.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 2 rows of 4 cards
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (col in 0..3) {
                                val slotIdx = row * 4 + col
                                val playerCard = deckSlots.getOrNull(slotIdx)
                                val cardModel = playerCard?.let { GameCatalog.getCard(it.cardId) }
                                    ?: GameCatalog.CARDS.getOrNull(slotIdx)
                                    ?: GameCatalog.CARDS.first()
                                val entity = playerCard ?: PlayerCardEntity(cardModel.id, level = 1, count = 5, isEquipped = true, deckSlot = slotIdx)

                                DeckCardSlotItem(
                                    card = cardModel,
                                    playerCard = entity,
                                    onClick = { selectedCardForDetail = Pair(cardModel, entity) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Card Collection Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CARD COLLECTION (${allPlayerCards.size}/${GameCatalog.CARDS.size} FOUND)",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = "Tap to inspect stats & skins",
                        fontSize = 10.sp,
                        color = ElegantPrimaryLavender
                    )
                }
            }

            // 5. Card Collection Grid
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GameCatalog.CARDS.chunked(4).forEach { rowCards ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowCards.forEach { card ->
                                val entity = allPlayerCards.find { it.cardId == card.id }
                                    ?: PlayerCardEntity(card.id, level = 1, count = 8, isEquipped = false)

                                DeckCardSlotItem(
                                    card = card,
                                    playerCard = entity,
                                    onClick = { selectedCardForDetail = Pair(card, entity) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowCards.size < 4) {
                                repeat(4 - rowCards.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Card Detail & Upgrade Dialog
    selectedCardForDetail?.let { (card, entity) ->
        CardDetailDialog(
            card = card,
            playerCard = entity,
            canUpgrade = (profile?.gold ?: 0) >= card.upgradeGoldCost && entity.count >= card.upgradeCardsNeeded,
            onUpgrade = {
                onUpgradeCard(card.id)
                selectedCardForDetail = null
            },
            onDismiss = { selectedCardForDetail = null }
        )
    }

    // Hero Picker Dialog
    if (showHeroPicker) {
        HeroPickerDialog(
            currentHeroId = activeHero.id,
            onSelect = { heroId ->
                onSelectHero(heroId)
                showHeroPicker = false
            },
            onDismiss = { showHeroPicker = false }
        )
    }
}

@Composable
private fun ActiveHeroCard(
    hero: HeroModel,
    onChangeHero: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimaryLavender.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChangeHero() }
            .testTag("card_active_hero")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElegantPrimaryLavender, ElegantPrimaryDark)
                            )
                        )
                        .border(2.dp, ElegantBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = hero.avatarEmoji, fontSize = 24.sp)
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = hero.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = ElegantTextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElegantPrimaryDark)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(text = hero.title, fontSize = 8.sp, color = ElegantPrimaryLavender, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Active: ${hero.abilityName} • ${hero.passiveName}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElegantPrimaryLavender
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElegantPrimaryDark)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "CHANGE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantPrimaryLavender
                )
            }
        }
    }
}

@Composable
private fun DeckCardSlotItem(
    card: CardModel,
    playerCard: PlayerCardEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColor = HolographicPalettes.getGlowColor(card.rarity)
    val foilColors = HolographicPalettes.getFoilColors(card.rarity)
    val isHighRarity = card.rarity in listOf("MYTHIC", "LEGENDARY", "EPIC")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .holographicPopOnTouch(onClick = onClick, scaleUp = 1.06f, elevationDp = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(ElegantCardBg)
            .border(
                width = if (isHighRarity) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(foilColors),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(6.dp)
            .testTag("card_slot_${card.id}")
    ) {
        // Top Row: Elixir Badge & Piece Type Mini
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6750A4)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${card.elixirCost}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFEADDFF)
                )
            }

            Text(
                text = "L${playerCard.level}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Center Artwork Frame with Holographic Foil & 3D Floating Pop Piece
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        card.cardArtGradientColors.map { Color(it) }
                    )
                )
                .holographicFoil(rarity = card.rarity, intensity = if (isHighRarity) 1.0f else 0.4f)
                .border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isHighRarity) {
                HolographicSparklesOverlay(sparkleCount = 2, color = rarityColor)
            }
            HolographicFloatingPiece(
                symbolChar = card.symbolChar,
                fontSize = 28.sp,
                rarity = card.rarity
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Name
        Text(
            text = card.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = ElegantTextPrimary,
            textAlign = TextAlign.Center
        )

        // Role chip
        Text(
            text = card.role.take(12),
            fontSize = 7.5.sp,
            color = rarityColor,
            maxLines = 1
        )

        // Upgrade progress
        val progressFrac = (playerCard.count.toFloat() / card.upgradeCardsNeeded.toFloat()).coerceIn(0f, 1f)
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progressFrac },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(50)),
            color = if (playerCard.count >= card.upgradeCardsNeeded) ElegantEmerald else ElegantPrimaryLavender,
            trackColor = ElegantDarkBg
        )
    }
}

@Composable
fun CardDetailDialog(
    card: CardModel,
    playerCard: PlayerCardEntity,
    canUpgrade: Boolean,
    onUpgrade: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDetailTab by remember { mutableStateOf("STATS") } // "STATS", "SKINS"
    val skins = remember(card.id) { GameCatalog.getSkinsForTarget(card.id) }
    val rarityColor = HolographicPalettes.getGlowColor(card.rarity)
    val foilColors = HolographicPalettes.getFoilColors(card.rarity)

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
                .padding(12.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = rarityColor, spotColor = rarityColor)
                .testTag("dialog_card_detail")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrismaticRarityBadge(rarity = "${card.rarity} • ${card.pieceType}")

                    Text(text = "⚡ ${card.elixirCost} Elixir", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantAccentLight)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card Art Display Box with Full Holographic Foil & 3D Floating Pop Piece
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                card.cardArtGradientColors.map { Color(it) }
                            )
                        )
                        .holographicFoil(rarity = card.rarity, intensity = 1.0f)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(foilColors),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = rarityColor, spotColor = rarityColor),
                    contentAlignment = Alignment.Center
                ) {
                    HolographicSparklesOverlay(sparkleCount = 4, color = Color.White)
                    HolographicFloatingPiece(
                        symbolChar = card.symbolChar,
                        fontSize = 48.sp,
                        rarity = card.rarity
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = card.name, fontWeight = FontWeight.Black, fontSize = 17.sp, color = ElegantTextPrimary)
                Text(text = "Level ${playerCard.level} • ${card.role}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = rarityColor)

                // Dialog Tabs (STATS vs COSMETIC SKINS)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkBg)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedDetailTab == "STATS") ElegantPrimaryLavender else Color.Transparent)
                            .clickable { selectedDetailTab = "STATS" }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TACTICAL STATS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDetailTab == "STATS") ElegantPrimaryDark else ElegantTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedDetailTab == "SKINS") ElegantPrimaryLavender else Color.Transparent)
                            .clickable { selectedDetailTab = "SKINS" }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "COSMETIC SKINS (${skins.size})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDetailTab == "SKINS") ElegantPrimaryDark else ElegantTextSecondary
                        )
                    }
                }

                if (selectedDetailTab == "STATS") {
                    // Tactical Description & Movement
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = card.description,
                            fontSize = 10.5.sp,
                            color = ElegantTextSecondary,
                            lineHeight = 14.sp
                        )

                        // Tactical Movement Guide Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElegantDarkBg)
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "⚔️ TACTICAL COMBAT MOVEMENT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ElegantPrimaryLavender
                                )
                                Text(
                                    text = "${card.movePattern} (${card.targetType})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantTextPrimary
                                )
                            }
                        }

                        // Special Ability Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElegantPrimaryDark.copy(alpha = 0.5f))
                                .border(1.dp, ElegantPrimaryLavender.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "✨ ABILITY: ${card.abilityName}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ElegantAccentLight
                                )
                                Text(
                                    text = card.abilityDescription,
                                    fontSize = 9.5.sp,
                                    color = ElegantTextPrimary
                                )
                            }
                        }

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatBox(label = "ATK", value = "${card.attack}", modifier = Modifier.weight(1f))
                            StatBox(label = "HP", value = "${card.health}", modifier = Modifier.weight(1f))
                            StatBox(label = "SPD", value = "${card.speed}x", modifier = Modifier.weight(1f))
                            StatBox(label = "RNG", value = "${card.range}", modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onUpgrade,
                        enabled = canUpgrade,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canUpgrade) ElegantPrimaryLavender else ElegantBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_upgrade_card_confirm")
                    ) {
                        Text(
                            text = if (canUpgrade) "UPGRADE (${card.upgradeGoldCost}🪙)" else "NEED ${card.upgradeCardsNeeded} CARDS",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = if (canUpgrade) ElegantPrimaryDark else ElegantTextSecondary
                        )
                    }
                } else {
                    // Cosmetic Skins Wardrobe View
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Purely visual variations. Zero stat changes.",
                            fontSize = 9.5.sp,
                            color = ElegantTextSecondary
                        )

                        if (skins.isEmpty()) {
                            Text(
                                text = "Standard Classic Skin active. New skins coming next season!",
                                fontSize = 11.sp,
                                color = ElegantTextSecondary,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            skins.forEach { skin ->
                                SkinPreviewMiniCard(skin = skin)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkinPreviewMiniCard(skin: CosmeticSkin) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantDarkBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(skin.accentColorHex)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                skin.bgGradientColors.map { Color(it) }
                            )
                        )
                        .border(1.dp, Color(skin.accentColorHex), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = skin.symbolChar, fontSize = 20.sp)
                }

                Column {
                    Text(
                        text = skin.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = "How to unlock: ${skin.acquisitionDetail}",
                        fontSize = 9.sp,
                        color = ElegantPrimaryLavender,
                        maxLines = 1
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ElegantPrimaryDark)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = skin.rarity,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(skin.accentColorHex)
                )
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ElegantDarkBg)
            .border(1.dp, ElegantBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 8.5.sp, color = ElegantTextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
    }
}

@Composable
fun HeroPickerDialog(
    currentHeroId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ElegantCardBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElegantPrimaryLavender),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SELECT HERO COMMANDER",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = ElegantPrimaryLavender
                )
                Text(
                    text = "Each Commander possesses a unique tactical board passive and an active hero power.",
                    fontSize = 10.sp,
                    color = ElegantTextSecondary
                )

                GameCatalog.HEROES.forEach { hero ->
                    val isSelected = hero.id == currentHeroId
                    val heroSkins = GameCatalog.getSkinsForTarget(hero.id)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) ElegantPrimaryDark else ElegantDarkBg)
                            .border(1.dp, if (isSelected) ElegantPrimaryLavender else ElegantBorder, RoundedCornerShape(14.dp))
                            .clickable { onSelect(hero.id) }
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(text = hero.avatarEmoji, fontSize = 24.sp)
                                Column {
                                    Text(text = hero.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ElegantTextPrimary)
                                    Text(text = hero.title, fontSize = 9.sp, color = ElegantTextSecondary)
                                }
                            }

                            if (isSelected) {
                                Text(text = "EQUIPPED ✓", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ElegantEmerald)
                            }
                        }

                        Text(
                            text = "⚡ Ability: ${hero.abilityName} - ${hero.abilityDescription}",
                            fontSize = 9.5.sp,
                            color = ElegantPrimaryLavender
                        )
                        Text(
                            text = "🛡️ Passive: ${hero.passiveName} - ${hero.passiveDescription}",
                            fontSize = 9.sp,
                            color = ElegantTextSecondary
                        )
                        Text(
                            text = "✨ ${heroSkins.size} Cosmetic Skins Available in Armoury",
                            fontSize = 8.5.sp,
                            color = ElegantAccentLight
                        )
                    }
                }
            }
        }
    }
}
