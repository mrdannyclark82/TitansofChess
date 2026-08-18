package com.example.data.model

data class CardModel(
    val id: String,
    val name: String,
    val pieceType: String, // "KING", "QUEEN", "ROOK", "BISHOP", "KNIGHT", "PAWN", "SPELL"
    val role: String, // "Omni Striker", "Diagonal Sniper", "Leaping Assassin", "Siege Fortress", "Disciplined Swarm", "Area Lightning Spell", "Shadow Infiltrator", "Commanding Aura"
    val movePattern: String, // e.g. "8-Direction Omnidirectional", "Diagonal Beam Piercing", "L-Shape Jump (ignores blocks)", "Straight-Line Artillery", "1-Step Forward (Promotes)"
    val targetType: String, // "Ground & Air", "Ground Melee", "Area Cells", "Direct Target"
    val elixirCost: Int,
    val rarity: String, // "COMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC"
    val attack: Int,
    val health: Int,
    val speed: Float,
    val range: Int,
    val symbolChar: String,
    val description: String,
    val abilityName: String,
    val abilityDescription: String,
    val upgradeCardsNeeded: Int = 10,
    val upgradeGoldCost: Int = 200,
    val cardArtGradientColors: List<Long> = listOf(0xFF2D2F33, 0xFF1A1C1E)
)

data class HeroModel(
    val id: String,
    val name: String,
    val title: String,
    val avatarEmoji: String,
    val themeColorHex: Long = 0xFFD0BCFF,
    val maxEnergy: Int = 100,
    val abilityName: String,
    val abilityDescription: String,
    val passiveName: String,
    val passiveDescription: String,
    val lore: String = ""
)

data class ArenaSkin(
    val id: String,
    val name: String,
    val description: String,
    val lightTileHex: Long,
    val darkTileHex: Long,
    val borderHex: Long,
    val requiredTrophies: Int
)

data class CosmeticSkin(
    val id: String,
    val name: String,
    val targetType: String, // "CARD" or "HERO"
    val targetId: String,
    val targetName: String,
    val rarity: String, // "COMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC"
    val priceGems: Int,
    val acquisitionMethod: String, // "SHOP", "BATTLE_PASS", "CHEST_DROP", "TOURNAMENT", "RANKED"
    val acquisitionDetail: String,
    val themeName: String,
    val symbolChar: String,
    val accentColorHex: Long,
    val bgGradientColors: List<Long>,
    val description: String,
    val visualEffects: String
)

data class TournamentEvent(
    val id: String,
    val title: String,
    val format: String,
    val prizePool: String,
    val entryFeeGold: Int,
    val participantCount: Int,
    val maxParticipants: Int,
    val status: String // "LIVE NOW", "UPCOMING", "REGISTRATION"
)

data class ChestReward(
    val chestName: String,
    val goldReward: Int,
    val gemsReward: Int,
    val cardsRewarded: List<Pair<String, Int>>, // cardId, count
    val skinRewarded: CosmeticSkin? = null
)

data class MatchedOpponent(
    val name: String,
    val title: String,
    val trophies: Int,
    val clanName: String,
    val heroName: String,
    val avatarLetter: String
)

object GameCatalog {
    val HEROES = listOf(
        HeroModel(
            id = "hero_kasparov",
            name = "Kasparov_99",
            title = "Grandmaster Tactician",
            avatarEmoji = "👑",
            themeColorHex = 0xFFD0BCFF,
            abilityName = "King's Vanguard",
            abilityDescription = "Summons 2 Royal Knights adjacent to the King with +30% attack speed.",
            passiveName = "Master Gambit",
            passiveDescription = "Generates +1 Elixir whenever an opponent piece is captured.",
            lore = "The legendary tactician who unified the grand chessboard dynasties. Master of tempo and tactical sacrifices."
        ),
        HeroModel(
            id = "hero_vespera",
            name = "Vespera Nyx",
            title = "Shadow Sovereign",
            avatarEmoji = "🔮",
            themeColorHex = 0xFFBB86FC,
            abilityName = "Eclipse Portal",
            abilityDescription = "Teleports any friendly piece instantly behind the enemy line.",
            passiveName = "Shadow Veil",
            passiveDescription = "Allied units take 20% reduced ranged damage.",
            lore = "An enigmatic enchantress from the cosmic void who bends diagonal leylines and shadows to her command."
        ),
        HeroModel(
            id = "hero_magnus",
            name = "Magnus Ironclad",
            title = "Colossus of the Fortress",
            avatarEmoji = "🛡️",
            themeColorHex = 0xFFFFB4A9,
            abilityName = "Fortress Bulwark",
            abilityDescription = "Grants +500 HP shield to all allied towers and pieces for 6 seconds.",
            passiveName = "Unyielding Armor",
            passiveDescription = "Rooks deal 30% splash damage on impact.",
            lore = "A juggernaut smith who forged unbreakable fortress armor out of fallen meteorite steel."
        )
    )

    val CARDS = listOf(
        CardModel(
            id = "card_queen_valkyrie",
            name = "Valkyrie Queen",
            pieceType = "QUEEN",
            role = "Omni Striker",
            movePattern = "8-Direction Omnidirectional Strike",
            targetType = "Ground & Air",
            elixirCost = 7,
            rarity = "LEGENDARY",
            attack = 1650,
            health = 1400,
            speed = 1.4f,
            range = 5,
            symbolChar = "⚔️",
            description = "Supreme ruler of the board who sweeps in all 8 directions with blade hurricanes.",
            abilityName = "Royal Tempest",
            abilityDescription = "Unleashes a circular blade storm dealing 400 area damage to all surrounding enemies.",
            cardArtGradientColors = listOf(0xFF4A148C, 0xFF1A1C1E)
        ),
        CardModel(
            id = "card_hierophant",
            name = "The Hierophant",
            pieceType = "BISHOP",
            role = "Diagonal Sniper",
            movePattern = "Continuous Diagonal Leyline",
            targetType = "Ground & Air",
            elixirCost = 4,
            rarity = "LEGENDARY",
            attack = 1200,
            health = 850,
            speed = 1.2f,
            range = 4,
            symbolChar = "🔮",
            description = "Master of diagonal devastation who pierces through multiple enemies with chained arcane rays.",
            abilityName = "Arcane Ray",
            abilityDescription = "Pierces through all target cells along the diagonal axis simultaneously.",
            cardArtGradientColors = listOf(0xFF283593, 0xFF1A1C1E)
        ),
        CardModel(
            id = "card_knight_paladin",
            name = "Knight Paladin",
            pieceType = "KNIGHT",
            role = "Leaping Flanker Assassin",
            movePattern = "L-Shape Jump (Ignores Obstacles)",
            targetType = "Ground Melee",
            elixirCost = 3,
            rarity = "RARE",
            attack = 820,
            health = 950,
            speed = 1.8f,
            range = 2,
            symbolChar = "🐎",
            description = "Agile mounted paladin who leaps over enemy frontlines directly onto high-value squishy targets.",
            abilityName = "L-Leap Charge",
            abilityDescription = "First strike immediately after landing deals 200% critical damage and stuns.",
            cardArtGradientColors = listOf(0xFF00695C, 0xFF1A1C1E)
        ),
        CardModel(
            id = "card_siege_rook",
            name = "Siege Rook",
            pieceType = "ROOK",
            role = "Siege Fortress Artillery",
            movePattern = "Orthogonal Straight Line",
            targetType = "Ground & Buildings",
            elixirCost = 5,
            rarity = "EPIC",
            attack = 1100,
            health = 1800,
            speed = 0.9f,
            range = 6,
            symbolChar = "🏰",
            description = "Heavy armored artillery tower on reinforced stone treads that rains down long-range bombardment.",
            abilityName = "Castle Barrage",
            abilityDescription = "Deploys stabilization anchors and fires continuous high-damage mortar volleys.",
            cardArtGradientColors = listOf(0xFFBF360C, 0xFF1A1C1E)
        ),
        CardModel(
            id = "card_pawn_sentinels",
            name = "Pawn Sentinels",
            pieceType = "PAWN",
            role = "Disciplined Swarm",
            movePattern = "Forward 1-Step (Promotes at end)",
            targetType = "Ground Melee",
            elixirCost = 2,
            rarity = "COMMON",
            attack = 450,
            health = 500,
            speed = 1.0f,
            range = 1,
            symbolChar = "🛡️",
            description = "Trio of disciplined foot soldiers. If any pawn reaches the opposing baseline, it immediately promotes!",
            abilityName = "Promotion Rush",
            abilityDescription = "Promotes into an autonomous Minor Queen upon reaching enemy home row.",
            cardArtGradientColors = listOf(0xFF37474F, 0xFF1A1C1E)
        ),
        CardModel(
            id = "card_thunder_spell",
            name = "Checkmate Strike",
            pieceType = "SPELL",
            role = "Area Lightning Spell",
            movePattern = "Targeted 3x3 Grid Cast",
            targetType = "Area Spell",
            elixirCost = 4,
            rarity = "EPIC",
            attack = 900,
            health = 0,
            speed = 0f,
            range = 3,
            symbolChar = "⚡",
            description = "Calls down celestial thunderbolts from the heavens onto a 3x3 board zone, disabling all enemy pieces.",
            abilityName = "Electrocute",
            abilityDescription = "Stuns enemy units caught inside the blast zone for 2.0 seconds.",
            cardArtGradientColors = listOf(0xFFF57F17, 0xFF1A1C1E)
        ),
        CardModel(
            id = "card_shadow_assassin",
            name = "Shadow Bishop",
            pieceType = "BISHOP",
            role = "Shadow Infiltrator",
            movePattern = "Diagonal Darkstep",
            targetType = "Ground & Air",
            elixirCost = 4,
            rarity = "RARE",
            attack = 780,
            health = 720,
            speed = 1.3f,
            range = 4,
            symbolChar = "🗡️",
            description = "Stealth bishop shrouded in void mist. Infiltrates enemy ranks unseen until delivering a lethal poison strike.",
            abilityName = "Backstab Poison",
            abilityDescription = "Inflicts deadly neurotoxin dealing 120 damage per second over 4 seconds.",
            cardArtGradientColors = listOf(0xFF311B92, 0xFF1A1C1E)
        ),
        CardModel(
            id = "card_royal_guardian",
            name = "King's Vanguard",
            pieceType = "KING",
            role = "Commanding Aura Tank",
            movePattern = "1-Step 8-Direction Step",
            targetType = "Ground Melee",
            elixirCost = 6,
            rarity = "LEGENDARY",
            attack = 1350,
            health = 2200,
            speed = 0.8f,
            range = 2,
            symbolChar = "🤴",
            description = "Majestic royal champion whose commanding battle aura inspires nearby allies with +25% Attack Speed.",
            abilityName = "Sovereign Command",
            abilityDescription = "Emits a golden seismic shockwave knocking back all surrounding enemies by 2 tiles.",
            cardArtGradientColors = listOf(0xFFFF6F00, 0xFF1A1C1E)
        )
    )

    val ARENA_SKINS = listOf(
        ArenaSkin(
            id = "arena_titans_peak",
            name = "Titan's Peak",
            description = "Obsidian summit shrouded in twilight mist & violet ley lines.",
            lightTileHex = 0xFF2D2F33,
            darkTileHex = 0xFF1A1C1E,
            borderHex = 0xFFD0BCFF,
            requiredTrophies = 0
        ),
        ArenaSkin(
            id = "arena_royal_marble",
            name = "Royal Grand Hall",
            description = "Polished obsidian and gold-inlaid grand chessboard arena.",
            lightTileHex = 0xFF36393E,
            darkTileHex = 0xFF232529,
            borderHex = 0xFFEADDFF,
            requiredTrophies = 400
        ),
        ArenaSkin(
            id = "arena_celestial_void",
            name = "Celestial Colosseum",
            description = "Nebula stars and glowing celestial constellation grids.",
            lightTileHex = 0xFF381E72,
            darkTileHex = 0xFF1A1C1E,
            borderHex = 0xFFD0BCFF,
            requiredTrophies = 1200
        )
    )

    // Complete cosmetic skin collection with clear acquisition methods
    val PIECE_SKINS = listOf(
        // Hero Skins
        CosmeticSkin(
            id = "skin_hero_kasparov_gilded",
            name = "Gilded Grandmaster Kasparov",
            targetType = "HERO",
            targetId = "hero_kasparov",
            targetName = "Kasparov_99",
            rarity = "LEGENDARY",
            priceGems = 400,
            acquisitionMethod = "RANKED",
            acquisitionDetail = "Reach Grandmaster League (3,000+ Trophies) or 400 💎 in Shop",
            themeName = "Gilded Emperor",
            symbolChar = "👑",
            accentColorHex = 0xFFFFD700,
            bgGradientColors = listOf(0xFF795548, 0xFF1A1C1E),
            description = "Ornate 24-karat gold filigree armor and royal velvet mantle. Pure prestige.",
            visualEffects = "Golden stardust trails on movement, royal fanfare on skill activation"
        ),
        CosmeticSkin(
            id = "skin_hero_kasparov_cyber",
            name = "Cyber Overlord Kasparov",
            targetType = "HERO",
            targetId = "hero_kasparov",
            targetName = "Kasparov_99",
            rarity = "EPIC",
            priceGems = 250,
            acquisitionMethod = "SHOP",
            acquisitionDetail = "Featured Armoury Shop Daily Rotation (250 💎)",
            themeName = "Cyberpunk Neo",
            symbolChar = "👑",
            accentColorHex = 0xFF00E5FF,
            bgGradientColors = listOf(0xFF006064, 0xFF1A1C1E),
            description = "Futuristic neon cyan visor, holographic scepter, and glowing tactical circuits.",
            visualEffects = "Holographic grid summon flares and electronic synth voice lines"
        ),
        CosmeticSkin(
            id = "skin_hero_kasparov_frost",
            name = "Glacial Frost King Kasparov",
            targetType = "HERO",
            targetId = "hero_kasparov",
            targetName = "Kasparov_99",
            rarity = "MYTHIC",
            priceGems = 500,
            acquisitionMethod = "BATTLE_PASS",
            acquisitionDetail = "Unlock Season 4 Royal Battle Pass Tier 20 Milestone",
            themeName = "Glacial Frost",
            symbolChar = "👑",
            accentColorHex = 0xFF80D8FF,
            bgGradientColors = listOf(0xFF01579B, 0xFF1A1C1E),
            description = "Permafrost crystalline crown, frozen scepter, and sub-zero blizzard cape.",
            visualEffects = "Sub-zero frost footsteps and crystalline shatter impact effects"
        ),
        CosmeticSkin(
            id = "skin_hero_vespera_astral",
            name = "Star Sovereign Vespera",
            targetType = "HERO",
            targetId = "hero_vespera",
            targetName = "Vespera Nyx",
            rarity = "LEGENDARY",
            priceGems = 350,
            acquisitionMethod = "SHOP",
            acquisitionDetail = "Armoury Shop Mythic Rotation (350 💎)",
            themeName = "Cosmic Astral",
            symbolChar = "🔮",
            accentColorHex = 0xFFD0BCFF,
            bgGradientColors = listOf(0xFF4A148C, 0xFF1A1C1E),
            description = "Robes woven from cosmic nebulae, orbiting micro-stars, and twilight veil.",
            visualEffects = "Constellation leylines glow when casting Eclipse Portal"
        ),
        CosmeticSkin(
            id = "skin_hero_vespera_bloodmoon",
            name = "Blood Moon Empress Vespera",
            targetType = "HERO",
            targetId = "hero_vespera",
            targetName = "Vespera Nyx",
            rarity = "EPIC",
            priceGems = 220,
            acquisitionMethod = "CHEST_DROP",
            acquisitionDetail = "Rare drop from Magical and Mythic Royal Chests (2.5% chance)",
            themeName = "Blood Moon",
            symbolChar = "🔮",
            accentColorHex = 0xFFFF5252,
            bgGradientColors = listOf(0xFF880E4F, 0xFF1A1C1E),
            description = "Crimson lunar robes, blood-crystal talisman, and crimson eclipse mist.",
            visualEffects = "Dark crimson shadow mist surrounds allied units"
        ),
        CosmeticSkin(
            id = "skin_hero_magnus_infernal",
            name = "Molten Magma Magnus",
            targetType = "HERO",
            targetId = "hero_magnus",
            targetName = "Magnus Ironclad",
            rarity = "LEGENDARY",
            priceGems = 350,
            acquisitionMethod = "BATTLE_PASS",
            acquisitionDetail = "Unlock Season 4 Royal Battle Pass Tier 10 Milestone",
            themeName = "Infernal Ember",
            symbolChar = "🛡️",
            accentColorHex = 0xFFFF6E40,
            bgGradientColors = listOf(0xFFBF360C, 0xFF1A1C1E),
            description = "Forged in volcanic bedrock with flowing magma veins and a smoking obsidian shield.",
            visualEffects = "Lava embers burst when Bulwark Shield is activated"
        ),

        // Unit / Piece Skins
        CosmeticSkin(
            id = "skin_queen_solar",
            name = "Solar Seraph Queen",
            targetType = "CARD",
            targetId = "card_queen_valkyrie",
            targetName = "Valkyrie Queen",
            rarity = "MYTHIC",
            priceGems = 500,
            acquisitionMethod = "CHEST_DROP",
            acquisitionDetail = "Exclusive drop from Mythic Royal Chests (1% chance) or 500 💎",
            themeName = "Solar Seraph",
            symbolChar = "⚔️",
            accentColorHex = 0xFFFFD700,
            bgGradientColors = listOf(0xFFE65100, 0xFF1A1C1E),
            description = "Radiant golden angelic wings, solar halo crown, and twin sunblade broadswords.",
            visualEffects = "Blinding solar light beams on 8-direction sweeping blade attacks"
        ),
        CosmeticSkin(
            id = "skin_queen_void",
            name = "Void Sovereign Queen",
            targetType = "CARD",
            targetId = "card_queen_valkyrie",
            targetName = "Valkyrie Queen",
            rarity = "LEGENDARY",
            priceGems = 300,
            acquisitionMethod = "SHOP",
            acquisitionDetail = "Armoury Shop Featured Weekly Deal (300 💎)",
            themeName = "Void Sovereign",
            symbolChar = "⚔️",
            accentColorHex = 0xFFBB86FC,
            bgGradientColors = listOf(0xFF4A148C, 0xFF1A1C1E),
            description = "Dark matter armor, ethereal shadow blades, and swirling twilight petals.",
            visualEffects = "Dark violet blade storm with floating dimensional rift particles"
        ),
        CosmeticSkin(
            id = "skin_hierophant_cosmic",
            name = "Cosmic Hierophant",
            targetType = "CARD",
            targetId = "card_hierophant",
            targetName = "The Hierophant",
            rarity = "LEGENDARY",
            priceGems = 250,
            acquisitionMethod = "TOURNAMENT",
            acquisitionDetail = "Titan's Grandmaster Invitational 1st Place or 250 💎",
            themeName = "Cosmic Astral",
            symbolChar = "🔮",
            accentColorHex = 0xFF80D8FF,
            bgGradientColors = listOf(0xFF0D47A1, 0xFF1A1C1E),
            description = "Starlight celestial staff topped with a miniature rotating spiral galaxy.",
            visualEffects = "Diagonal attacks fire brilliant starlight constellation beams"
        ),
        CosmeticSkin(
            id = "skin_hierophant_chronomancer",
            name = "Clockwork Chronomancer",
            targetType = "CARD",
            targetId = "card_hierophant",
            targetName = "The Hierophant",
            rarity = "EPIC",
            priceGems = 200,
            acquisitionMethod = "SHOP",
            acquisitionDetail = "Armoury Shop Daily Flash Sale (200 💎)",
            themeName = "Steampunk Clockwork",
            symbolChar = "🔮",
            accentColorHex = 0xFFFFAB00,
            bgGradientColors = listOf(0xFF5D4037, 0xFF1A1C1E),
            description = "Brass gears, glass hourglass staff, and ticking pendulum robes.",
            visualEffects = "Clock gear dials appear on the board along diagonal strike paths"
        ),
        CosmeticSkin(
            id = "skin_knight_pegasus",
            name = "Winged Pegasus Knight",
            targetType = "CARD",
            targetId = "card_knight_paladin",
            targetName = "Knight Paladin",
            rarity = "EPIC",
            priceGems = 220,
            acquisitionMethod = "SHOP",
            acquisitionDetail = "Armoury Shop Rotation (220 💎)",
            themeName = "Divine Pegasus",
            symbolChar = "🐎",
            accentColorHex = 0xFF80CBC4,
            bgGradientColors = listOf(0xFF004D40, 0xFF1A1C1E),
            description = "Divine feathered pegasus steed with radiant lance and silver armor.",
            visualEffects = "Spreads angelic wings on L-shape leaps with cloud burst landing impacts"
        ),
        CosmeticSkin(
            id = "skin_knight_nightmare",
            name = "Astral Dread Knight",
            targetType = "CARD",
            targetId = "card_knight_paladin",
            targetName = "Knight Paladin",
            rarity = "RARE",
            priceGems = 120,
            acquisitionMethod = "BATTLE_PASS",
            acquisitionDetail = "Season 4 Royal Battle Pass Tier 4 Reward",
            themeName = "Dread Nightmare",
            symbolChar = "🐎",
            accentColorHex = 0xFFFF5252,
            bgGradientColors = listOf(0xFF37474F, 0xFF1A1C1E),
            description = "Dark spectral steed leaving glowing ember trails behind every leap.",
            visualEffects = "Fiery hoof prints on jump takeoff and landing tiles"
        ),
        CosmeticSkin(
            id = "skin_rook_dragon",
            name = "Dragon Bastion Rook",
            targetType = "CARD",
            targetId = "card_siege_rook",
            targetName = "Siege Rook",
            rarity = "LEGENDARY",
            priceGems = 300,
            acquisitionMethod = "CHEST_DROP",
            acquisitionDetail = "Drop from Gold & Magical Chests (3% chance) or 300 💎",
            themeName = "Dragon Bastion",
            symbolChar = "🏰",
            accentColorHex = 0xFFFF6E40,
            bgGradientColors = listOf(0xFFD84315, 0xFF1A1C1E),
            description = "Carved dragon gargoyle stone spire with a molten flame mortar port.",
            visualEffects = "Fires roaring fireball projectiles with burning impact ground scorch"
        ),
        CosmeticSkin(
            id = "skin_rook_tesla",
            name = "Tesla Ion Spire Rook",
            targetType = "CARD",
            targetId = "card_siege_rook",
            targetName = "Siege Rook",
            rarity = "EPIC",
            priceGems = 200,
            acquisitionMethod = "SHOP",
            acquisitionDetail = "Armoury Shop (200 💎)",
            themeName = "Tesla Coil",
            symbolChar = "🏰",
            accentColorHex = 0xFF00E5FF,
            bgGradientColors = listOf(0xFF006064, 0xFF1A1C1E),
            description = "High-voltage tesla arc coils enclosed in titanium Faraday framing.",
            visualEffects = "Shoots high-frequency electric lightning arcs along straight lines"
        ),
        CosmeticSkin(
            id = "skin_pawn_cyber",
            name = "Cyber Drone Phalanx",
            targetType = "CARD",
            targetId = "card_pawn_sentinels",
            targetName = "Pawn Sentinels",
            rarity = "RARE",
            priceGems = 100,
            acquisitionMethod = "SHOP",
            acquisitionDetail = "Armoury Shop (100 💎)",
            themeName = "Cyberpunk Neo",
            symbolChar = "🛡️",
            accentColorHex = 0xFF00E676,
            bgGradientColors = listOf(0xFF1B5E20, 0xFF1A1C1E),
            description = "Autonomous combat androids with energy tower shields and plasma blades.",
            visualEffects = "Neon green holographic promotion barrier when reaching back row"
        ),
        CosmeticSkin(
            id = "skin_pawn_spartan",
            name = "Spartan Bronze Hoplites",
            targetType = "CARD",
            targetId = "card_pawn_sentinels",
            targetName = "Pawn Sentinels",
            rarity = "COMMON",
            priceGems = 50,
            acquisitionMethod = "CHEST_DROP",
            acquisitionDetail = "Common drop from Clan Donation Chests and Silver Chests",
            themeName = "Spartan Bronze",
            symbolChar = "🛡️",
            accentColorHex = 0xFFFFD54F,
            bgGradientColors = listOf(0xFF4E342E, 0xFF1A1C1E),
            description = "Corinthian bronze helmets, hoplon shields with lambda insignia, and red crests.",
            visualEffects = "Shield wall clanking audio and golden sparks on block"
        ),
        CosmeticSkin(
            id = "skin_spell_meteor",
            name = "Cataclysm Meteor Strike",
            targetType = "CARD",
            targetId = "card_thunder_spell",
            targetName = "Checkmate Strike",
            rarity = "EPIC",
            priceGems = 200,
            acquisitionMethod = "CHEST_DROP",
            acquisitionDetail = "Drop from Magical Chests (5% chance) or 200 💎",
            themeName = "Meteor Cataclysm",
            symbolChar = "☄️",
            accentColorHex = 0xFFFF5722,
            bgGradientColors = listOf(0xFFBF360C, 0xFF1A1C1E),
            description = "Summons three flaming apocalyptic meteors plunging from orbit onto the 3x3 grid.",
            visualEffects = "Molten meteor impact craters with burning ash smoke"
        )
    )

    val TOURNAMENTS = listOf(
        TournamentEvent(
            id = "tourney_grandmaster_cup",
            title = "Titan's Grandmaster Invitational",
            format = "Single Elimination (Best of 3)",
            prizePool = "50,000 Gold + Cosmic Hierophant Skin",
            entryFeeGold = 100,
            participantCount = 62,
            maxParticipants = 64,
            status = "LIVE NOW"
        ),
        TournamentEvent(
            id = "tourney_speed_chess",
            title = "Blitz Knights Rumble (2x Elixir)",
            format = "Double Elimination",
            prizePool = "20,000 Gold + 250 Gems",
            entryFeeGold = 50,
            participantCount = 28,
            maxParticipants = 32,
            status = "REGISTRATION"
        )
    )

    fun getCard(id: String): CardModel {
        return CARDS.find { it.id == id } ?: CARDS.first()
    }

    fun getHero(id: String): HeroModel {
        return HEROES.find { it.id == id } ?: HEROES.first()
    }

    fun getSkinsForTarget(targetId: String): List<CosmeticSkin> {
        return PIECE_SKINS.filter { it.targetId == targetId }
    }
}

