package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.ChestReward
import com.example.data.model.GameCatalog
import com.example.data.model.MatchedOpponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainGameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.gameDao()

    val currentTab = MutableStateFlow("HOME")

    val userProfile: StateFlow<UserProfileEntity?> = dao.getProfileFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCards: StateFlow<List<PlayerCardEntity>> = dao.getAllCardsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deckCards: StateFlow<List<PlayerCardEntity>> = dao.getDeckCardsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quests: StateFlow<List<QuestEntity>> = dao.getQuestsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chestSlots: StateFlow<List<ChestSlotEntity>> = dao.getChestSlotsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clans: StateFlow<List<ClanEntity>> = dao.getClansFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isMatchmaking = MutableStateFlow(false)
    val matchmakingTime = MutableStateFlow(0)
    val matchedOpponent = MutableStateFlow<MatchedOpponent?>(null)
    private var matchmakingJob: Job? = null

    val openedChestResult = MutableStateFlow<ChestReward?>(null)
    val toastMessage = MutableStateFlow<String?>(null)

    init {
        bootstrapDefaultData()
    }

    private fun bootstrapDefaultData() {
        viewModelScope.launch {
            if (dao.getProfile() == null) {
                // Initialize default profile
                dao.insertProfile(
                    UserProfileEntity(
                        id = "player_main",
                        username = "Kasparov_99",
                        title = "Grandmaster",
                        trophies = 3840,
                        gold = 12500,
                        gems = 240,
                        level = 12,
                        xp = 4200,
                        xpNext = 5000,
                        castleLevel = 4,
                        castleXp = 1200,
                        castleXpNext = 2500,
                        selectedHeroId = "hero_kasparov",
                        selectedArenaId = "arena_titans_peak",
                        clanId = "clan_grandmaster_order",
                        clanName = "Grandmaster Order"
                    )
                )

                // Initialize starting cards
                val startingCards = GameCatalog.CARDS.mapIndexed { index, card ->
                    PlayerCardEntity(
                        cardId = card.id,
                        level = if (index < 4) 8 else 4,
                        count = (index + 1) * 6,
                        isEquipped = index < 8,
                        deckSlot = if (index < 8) index else -1
                    )
                }
                dao.insertCards(startingCards)

                // Initialize starting chests
                val defaultChests = listOf(
                    ChestSlotEntity(0, "GOLDEN", isReady = true),
                    ChestSlotEntity(1, "SILVER", isUnlocking = true),
                    ChestSlotEntity(2, "MAGICAL"),
                    ChestSlotEntity(3, "EMPTY", isEmpty = true)
                )
                dao.insertChestSlots(defaultChests)

                // Initialize starting quests
                val defaultQuests = listOf(
                    QuestEntity("q1", "Capture 5 Knights in PvP", "Deploy pieces to neutralize enemy Knight Paladins", 3, 5, 500, 20),
                    QuestEntity("q2", "Play 3 Ranked Matches", "Complete 3 full tactical matches", 2, 3, 350, 10),
                    QuestEntity("q3", "Deal 10,000 Tower Damage", "Destroy guard towers or deliver Checkmate", 10000, 10000, 1200, 50, isClaimed = false),
                    QuestEntity("q4", "Donate 2 Cards to Clan", "Assist clan comrades with piece shards", 1, 2, 200, 5)
                )
                dao.insertQuests(defaultQuests)

                // Initialize Clans
                val defaultClans = listOf(
                    ClanEntity("clan_grandmaster_order", "Grandmaster Order", "#99GMD", "👑", 48, 28400, 3000, isPlayerMember = true),
                    ClanEntity("clan_royal_knights", "Royal Knights", "#88Q9Y", "🛡️", 47, 24800, 2000),
                    ClanEntity("clan_shadow_syndicate", "Shadow Syndicate", "#44SHD", "🔮", 45, 21900, 1500),
                    ClanEntity("clan_iron_vanguard", "Iron Vanguard", "#11IRN", "🏰", 39, 18500, 1000)
                )
                dao.insertClans(defaultClans)
            }
        }
    }

    fun navigateTo(tab: String) {
        currentTab.value = tab
    }

    fun startRankedMatchmaking(onMatchFound: (MatchedOpponent) -> Unit) {
        isMatchmaking.value = true
        matchmakingTime.value = 0
        matchedOpponent.value = null

        matchmakingJob?.cancel()
        matchmakingJob = viewModelScope.launch {
            for (sec in 1..3) {
                delay(1000)
                matchmakingTime.value = sec
            }

            val opponents = listOf(
                MatchedOpponent("ValkyrieQueen_X", "Elite Master", 3890, "Royal Knights", "Vespera Nyx", "V"),
                MatchedOpponent("MagnusPro_99", "Grandmaster Tactician", 3920, "Iron Vanguard", "Magnus Ironclad", "M"),
                MatchedOpponent("DragonSlayer", "Colosseum Master", 3810, "Shadow Syndicate", "Kasparov_99", "D")
            )
            val opp = opponents.random()
            matchedOpponent.value = opp
            delay(1200)
            isMatchmaking.value = false
            onMatchFound(opp)
        }
    }

    fun unlockChest(slotIndex: Int) {
        viewModelScope.launch {
            val slots = chestSlots.value
            val slot = slots.find { it.slotIndex == slotIndex } ?: return@launch

            if (slot.isReady || slot.isUnlocking) {
                // Open and give reward
                val reward = ChestReward(
                    chestName = slot.chestType + " Chest",
                    goldReward = if (slot.chestType == "MAGICAL") 2400 else 850,
                    gemsReward = if (slot.chestType == "MAGICAL") 50 else 15,
                    cardsRewarded = listOf(
                        Pair("card_hierophant", 6),
                        Pair("card_knight_paladin", 12)
                    )
                )

                // Update profile with gold & gems
                val profile = dao.getProfile()
                if (profile != null) {
                    dao.updateProfile(
                        profile.copy(
                            gold = profile.gold + reward.goldReward,
                            gems = profile.gems + reward.gemsReward
                        )
                    )
                }

                // Reset slot
                dao.updateChestSlot(
                    slot.copy(
                        chestType = "SILVER",
                        isReady = false,
                        isUnlocking = false,
                        isEmpty = false
                    )
                )

                openedChestResult.value = reward
            } else if (!slot.isEmpty) {
                // Start unlocking
                dao.updateChestSlot(slot.copy(isUnlocking = true))
                toastMessage.value = "Unlocking started! ⏳"
            }
        }
    }

    fun dismissOpenedChest() {
        openedChestResult.value = null
    }

    fun upgradeCard(cardId: String) {
        viewModelScope.launch {
            val card = dao.getAllCardsFlow().first().find { it.cardId == cardId } ?: return@launch
            val model = GameCatalog.getCard(cardId)
            val profile = dao.getProfile() ?: return@launch

            if (profile.gold >= model.upgradeGoldCost && card.count >= model.upgradeCardsNeeded) {
                dao.updateProfile(profile.copy(gold = profile.gold - model.upgradeGoldCost))
                dao.updateCard(card.copy(level = card.level + 1, count = card.count - model.upgradeCardsNeeded))
                toastMessage.value = "${model.name} upgraded to Level ${card.level + 1}! ✨"
            } else {
                toastMessage.value = "Not enough Gold or Cards!"
            }
        }
    }

    fun swapDeckCard(cardId: String, slotIdx: Int) {
        viewModelScope.launch {
            val cards = dao.getAllCardsFlow().first().toMutableList()
            val existingInSlot = cards.find { it.deckSlot == slotIdx }
            val newCard = cards.find { it.cardId == cardId }

            if (newCard != null) {
                if (existingInSlot != null) {
                    dao.updateCard(existingInSlot.copy(isEquipped = false, deckSlot = -1))
                }
                dao.updateCard(newCard.copy(isEquipped = true, deckSlot = slotIdx))
            }
        }
    }

    fun selectHero(heroId: String) {
        viewModelScope.launch {
            val profile = dao.getProfile() ?: return@launch
            dao.updateProfile(profile.copy(selectedHeroId = heroId))
            toastMessage.value = "Hero Commander updated! 👑"
        }
    }

    fun selectArena(arenaId: String) {
        viewModelScope.launch {
            val profile = dao.getProfile() ?: return@launch
            dao.updateProfile(profile.copy(selectedArenaId = arenaId))
            toastMessage.value = "Battle Arena equipped! 🎨"
        }
    }

    fun upgradeCastleHall() {
        viewModelScope.launch {
            val profile = dao.getProfile() ?: return@launch
            val cost = profile.castleLevel * 2500
            if (profile.gold >= cost) {
                dao.updateProfile(
                    profile.copy(
                        gold = profile.gold - cost,
                        castleLevel = profile.castleLevel + 1,
                        castleXp = 0,
                        castleXpNext = (profile.castleLevel + 1) * 3000
                    )
                )
                toastMessage.value = "Castle Hall upgraded to Level ${profile.castleLevel + 1}! 🏰"
            }
        }
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            val questsList = dao.getQuestsFlow().first()
            val quest = questsList.find { it.id == questId } ?: return@launch
            val profile = dao.getProfile() ?: return@launch

            if (quest.progress >= quest.target && !quest.isClaimed) {
                dao.updateQuest(quest.copy(isClaimed = true))
                dao.updateProfile(
                    profile.copy(
                        gold = profile.gold + quest.rewardGold,
                        gems = profile.gems + quest.rewardGems,
                        battlePassXp = profile.battlePassXp + 200
                    )
                )
                toastMessage.value = "Quest rewards claimed! +${quest.rewardGold}🪙 +${quest.rewardGems}💎"
            }
        }
    }

    fun sendClanChat(clanId: String, text: String, isCardReq: Boolean, reqCardId: String?) {
        viewModelScope.launch {
            val profile = dao.getProfile()
            dao.insertClanMessage(
                ClanMessageEntity(
                    id = System.currentTimeMillis(),
                    clanId = clanId,
                    senderName = profile?.username ?: "Kasparov_99",
                    messageText = text,
                    timestamp = System.currentTimeMillis(),
                    isCardRequest = isCardReq,
                    requestedCardId = reqCardId
                )
            )
        }
    }

    fun donateClanCard(msgId: Long, cardId: String) {
        viewModelScope.launch {
            val profile = dao.getProfile() ?: return@launch
            dao.updateProfile(profile.copy(gold = profile.gold + 50, xp = profile.xp + 10))
            toastMessage.value = "Donated to clanmate! +50🪙 +10XP"
        }
    }

    fun joinClan(clanId: String) {
        viewModelScope.launch {
            val profile = dao.getProfile() ?: return@launch
            val clanList = dao.getClansFlow().first()
            val clan = clanList.find { it.id == clanId } ?: return@launch

            val updatedClans = clanList.map {
                if (it.id == clanId) it.copy(isPlayerMember = true, memberCount = it.memberCount + 1)
                else it.copy(isPlayerMember = false)
            }
            dao.insertClans(updatedClans)
            dao.updateProfile(profile.copy(clanId = clanId, clanName = clan.name))
            toastMessage.value = "Joined ${clan.name}! 🛡️"
        }
    }

    fun clearToast() {
        toastMessage.value = null
    }
}
