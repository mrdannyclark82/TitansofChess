package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = "player_main",
    @ColumnInfo(name = "username") val username: String = "Kasparov_99",
    @ColumnInfo(name = "title") val title: String = "Grandmaster",
    @ColumnInfo(name = "trophies") val trophies: Int = 3840,
    @ColumnInfo(name = "gold") val gold: Int = 12500,
    @ColumnInfo(name = "gems") val gems: Int = 240,
    @ColumnInfo(name = "level") val level: Int = 12,
    @ColumnInfo(name = "xp") val xp: Int = 4200,
    @ColumnInfo(name = "xp_next") val xpNext: Int = 5000,
    @ColumnInfo(name = "castle_level") val castleLevel: Int = 4,
    @ColumnInfo(name = "castle_xp") val castleXp: Int = 1200,
    @ColumnInfo(name = "castle_xp_next") val castleXpNext: Int = 2500,
    @ColumnInfo(name = "selected_hero_id") val selectedHeroId: String = "hero_kasparov",
    @ColumnInfo(name = "selected_arena_id") val selectedArenaId: String = "arena_titans_peak",
    @ColumnInfo(name = "clan_id") val clanId: String = "clan_grandmaster_order",
    @ColumnInfo(name = "clan_name") val clanName: String = "Grandmaster Order",
    @ColumnInfo(name = "win_streak") val winStreak: Int = 5,
    @ColumnInfo(name = "battle_pass_tier") val battlePassTier: Int = 8,
    @ColumnInfo(name = "battle_pass_xp") val battlePassXp: Int = 650,
    @ColumnInfo(name = "battle_pass_xp_next") val battlePassXpNext: Int = 1000
)

@Entity(tableName = "player_cards")
data class PlayerCardEntity(
    @PrimaryKey @ColumnInfo(name = "card_id") val cardId: String,
    @ColumnInfo(name = "level") val level: Int = 1,
    @ColumnInfo(name = "count") val count: Int = 0,
    @ColumnInfo(name = "is_equipped") val isEquipped: Boolean = false,
    @ColumnInfo(name = "deck_slot") val deckSlot: Int = -1 // 0..7 if equipped
)

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "progress") val progress: Int,
    @ColumnInfo(name = "target") val target: Int,
    @ColumnInfo(name = "reward_gold") val rewardGold: Int,
    @ColumnInfo(name = "reward_gems") val rewardGems: Int,
    @ColumnInfo(name = "is_claimed") val isClaimed: Boolean = false,
    @ColumnInfo(name = "is_daily") val isDaily: Boolean = true
)

@Entity(tableName = "chest_slots")
data class ChestSlotEntity(
    @PrimaryKey @ColumnInfo(name = "slot_index") val slotIndex: Int, // 0..3
    @ColumnInfo(name = "chest_type") val chestType: String = "GOLDEN", // "SILVER", "GOLDEN", "MAGICAL", "EMPTY"
    @ColumnInfo(name = "unlock_time_millis") val unlockTimeMillis: Long = 0L,
    @ColumnInfo(name = "is_unlocking") val isUnlocking: Boolean = false,
    @ColumnInfo(name = "is_ready") val isReady: Boolean = false,
    @ColumnInfo(name = "is_empty") val isEmpty: Boolean = false
)

@Entity(tableName = "clans")
data class ClanEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "badge") val badge: String,
    @ColumnInfo(name = "member_count") val memberCount: Int,
    @ColumnInfo(name = "trophies") val trophies: Int,
    @ColumnInfo(name = "required_trophies") val requiredTrophies: Int,
    @ColumnInfo(name = "is_player_member") val isPlayerMember: Boolean = false
)

@Entity(tableName = "clan_messages")
data class ClanMessageEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "clan_id") val clanId: String,
    @ColumnInfo(name = "sender_name") val senderName: String,
    @ColumnInfo(name = "message_text") val messageText: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "is_card_request") val isCardRequest: Boolean = false,
    @ColumnInfo(name = "requested_card_id") val requestedCardId: String? = null,
    @ColumnInfo(name = "donations_current") val donationsCurrent: Int = 0,
    @ColumnInfo(name = "donations_max") val donationsMax: Int = 4
)

