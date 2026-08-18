package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.armoury.ArmouryShopScreen
import com.example.ui.battle.BattleScreen
import com.example.ui.castle.CastleProgressionScreen
import com.example.ui.clan.ClanScreen
import com.example.ui.components.BottomNavigationMenu
import com.example.ui.components.TopResourceHeader
import com.example.ui.deck.DeckBuilderScreen
import com.example.ui.home.ChestRewardModal
import com.example.ui.home.HomeScreen
import com.example.ui.home.MatchmakingRadarModal
import com.example.ui.quests.QuestsAndPassScreen
import com.example.ui.ranked.LeaderboardScreen
import com.example.ui.theme.ChessRoyaleTheme
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.tournaments.TournamentsScreen
import com.example.ui.viewmodel.BattleViewModel
import com.example.ui.viewmodel.MainGameViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainGameViewModel by viewModels()
    private val battleViewModel: BattleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChessRoyaleTheme {
                MainAppScreen(
                    mainViewModel = mainViewModel,
                    battleViewModel = battleViewModel
                )
            }
        }
    }
}

@Composable
fun MainAppScreen(
    mainViewModel: MainGameViewModel,
    battleViewModel: BattleViewModel
) {
    val currentTab by mainViewModel.currentTab.collectAsStateWithLifecycle()
    val profile by mainViewModel.userProfile.collectAsStateWithLifecycle()
    val allCards by mainViewModel.allCards.collectAsStateWithLifecycle()
    val deckCards by mainViewModel.deckCards.collectAsStateWithLifecycle()
    val quests by mainViewModel.quests.collectAsStateWithLifecycle()
    val chestSlots by mainViewModel.chestSlots.collectAsStateWithLifecycle()
    val clans by mainViewModel.clans.collectAsStateWithLifecycle()

    val isBattleActive by battleViewModel.isBattleActive.collectAsStateWithLifecycle()
    val isMatchmaking by mainViewModel.isMatchmaking.collectAsStateWithLifecycle()
    val matchmakingTime by mainViewModel.matchmakingTime.collectAsStateWithLifecycle()
    val matchedOpponent by mainViewModel.matchedOpponent.collectAsStateWithLifecycle()
    val openedChestResult by mainViewModel.openedChestResult.collectAsStateWithLifecycle()
    val toastMessage by mainViewModel.toastMessage.collectAsStateWithLifecycle()

    // Show toast if present
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            // Can be handled or cleared
            mainViewModel.clearToast()
        }
    }

    if (isBattleActive) {
        BattleScreen(
            battleViewModel = battleViewModel,
            opponentName = matchedOpponent?.name ?: "ShadowMaster99",
            onExitBattle = {
                battleViewModel.dismissGameOver()
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(ElegantDarkBg),
            topBar = {
                TopResourceHeader(
                    profile = profile,
                    onCastleClick = { mainViewModel.navigateTo("CASTLE") }
                )
            },
            bottomBar = {
                BottomNavigationMenu(
                    currentTab = currentTab,
                    onTabSelected = { tabId ->
                        mainViewModel.navigateTo(tabId)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ElegantDarkBg)
            ) {
                when (currentTab) {
                    "HOME" -> {
                        HomeScreen(
                            profile = profile,
                            chestSlots = chestSlots,
                            onStartBattle = {
                                mainViewModel.startRankedMatchmaking { opp ->
                                    val safeDeck = deckCards.map { it.cardId }
                                    battleViewModel.startBattle(
                                        heroId = profile?.selectedHeroId ?: "hero_kasparov",
                                        arenaId = profile?.selectedArenaId ?: "arena_titans_peak",
                                        deckCardIds = safeDeck,
                                        opponentName = opp.name,
                                        opponentTrophies = opp.trophies
                                    )
                                }
                            },
                            onOpenChest = { slotIdx ->
                                mainViewModel.unlockChest(slotIdx)
                            },
                            onNavigateTab = { tab ->
                                mainViewModel.navigateTo(tab)
                            }
                        )
                    }

                    "DECK" -> {
                        DeckBuilderScreen(
                            profile = profile,
                            allPlayerCards = allCards,
                            deckPlayerCards = deckCards,
                            onUpgradeCard = { cardId -> mainViewModel.upgradeCard(cardId) },
                            onSwapCard = { cardId, slot -> mainViewModel.swapDeckCard(cardId, slot) },
                            onSelectHero = { heroId -> mainViewModel.selectHero(heroId) }
                        )
                    }

                    "QUESTS" -> {
                        QuestsAndPassScreen(
                            profile = profile,
                            quests = quests,
                            onClaimQuest = { questId -> mainViewModel.claimQuest(questId) }
                        )
                    }

                    "TOURNAMENTS" -> {
                        TournamentsScreen(
                            profile = profile,
                            onOpenLeaderboard = { mainViewModel.navigateTo("LEADERBOARD") }
                        )
                    }

                    "LEADERBOARD" -> {
                        LeaderboardScreen(
                            profile = profile,
                            onBack = { mainViewModel.navigateTo("TOURNAMENTS") }
                        )
                    }

                    "CLAN" -> {
                        ClanScreen(
                            profile = profile,
                            clans = clans,
                            onJoinClan = { clanId -> mainViewModel.joinClan(clanId) },
                            onDonate = { msgId, cardId -> mainViewModel.donateClanCard(msgId, cardId) },
                            onSendChat = { text, isReq, cardId -> mainViewModel.sendClanChat(profile?.clanId ?: "clan_grandmaster_order", text, isReq, cardId) }
                        )
                    }

                    "ARMOURY" -> {
                        ArmouryShopScreen(
                            profile = profile,
                            onSelectArena = { arenaId -> mainViewModel.selectArena(arenaId) }
                        )
                    }

                    "CASTLE" -> {
                        CastleProgressionScreen(
                            profile = profile,
                            onUpgradeCastle = { mainViewModel.upgradeCastleHall() },
                            onBack = { mainViewModel.navigateTo("HOME") }
                        )
                    }
                }
            }
        }
    }

    // Matchmaking Radar Dialog
    if (isMatchmaking) {
        MatchmakingRadarModal(
            timeSeconds = matchmakingTime,
            matchedOpponent = matchedOpponent,
            onCancel = { mainViewModel.isMatchmaking.value = false }
        )
    }

    // Chest Opened Reward Modal
    openedChestResult?.let { chestResult ->
        ChestRewardModal(
            result = chestResult,
            onDismiss = { mainViewModel.dismissOpenedChest() }
        )
    }
}
