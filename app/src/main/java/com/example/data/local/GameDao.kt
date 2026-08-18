package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 'player_main' LIMIT 1")
    fun getProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'player_main' LIMIT 1")
    suspend fun getProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    // Player Cards
    @Query("SELECT * FROM player_cards")
    fun getAllCardsFlow(): Flow<List<PlayerCardEntity>>

    @Query("SELECT * FROM player_cards WHERE is_equipped = 1 ORDER BY deck_slot ASC")
    fun getDeckCardsFlow(): Flow<List<PlayerCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<PlayerCardEntity>)

    @Update
    suspend fun updateCard(card: PlayerCardEntity)

    // Quests
    @Query("SELECT * FROM quests")
    fun getQuestsFlow(): Flow<List<QuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    // Chest Slots
    @Query("SELECT * FROM chest_slots ORDER BY slot_index ASC")
    fun getChestSlotsFlow(): Flow<List<ChestSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChestSlots(slots: List<ChestSlotEntity>)

    @Update
    suspend fun updateChestSlot(slot: ChestSlotEntity)

    // Clans
    @Query("SELECT * FROM clans")
    fun getClansFlow(): Flow<List<ClanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClans(clans: List<ClanEntity>)

    @Update
    suspend fun updateClan(clan: ClanEntity)

    // Clan Messages
    @Query("SELECT * FROM clan_messages WHERE clan_id = :clanId ORDER BY timestamp ASC")
    fun getClanMessagesFlow(clanId: String): Flow<List<ClanMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClanMessage(msg: ClanMessageEntity)

    @Update
    suspend fun updateClanMessage(msg: ClanMessageEntity)
}

