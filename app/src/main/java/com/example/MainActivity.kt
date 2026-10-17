package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.armoury.ArmouryShopScreen
import com.example.ui.battle.BattleScreen
import com.example.ui.castle.CastleProgressionScreen
import com.example.ui.clan.ClanScreen
import com.example.ui.components.BottomNavigationMenu
import com.example.ui.deck.DeckBuilderScreen
import com.example.ui.home.HomeScreen
import com.example.ui.quests.QuestsAndPassScreen
import com.example.ui.ranked.LeaderboardScreen
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.TitansOfChessTheme
import com.example.ui.tournaments.TournamentsScreen
import com.example.ui.viewmodel.BattleViewModel
import com.example.ui.viewmodel.MainGameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TitansOfChessTheme {
                val mainViewModel: MainGameViewModel = viewModel()
                val battleViewModel: BattleViewModel = viewModel()
                val currentTab by mainViewModel.currentTab.collectAsStateWithLifecycle()
                val profile by mainViewModel.userProfile.collectAsStateWithLifecycle()
                val allCards by mainViewModel.allCards.collectAsStateWithLifecycle()
                val deckCards by mainViewModel.deckCards.collectAsStateWithLifecycle()
                val chestSlots by mainViewModel.chestSlots.collectAsStateWithLifecycle()
                val quests by mainViewModel.quests.collectAsStateWithLifecycle()
                val clans by mainViewModel.clans.collectAsStateWithLifecycle()
                val isBattleActive by battleViewModel.isBattleActive.collectAsStateWithLifecycle()
                val matchedOpponent by mainViewModel.matchedOpponent.collectAsStateWithLifecycle()

                if (isBattleActive && matchedOpponent != null) {
                    BattleScreen(
                        battleViewModel = battleViewModel,
                        opponentName = matchedOpponent!!.name,
                        onExitBattle = { battleViewModel.exitBattle() }
                    )
                    return@TitansOfChessTheme
                }

                Scaffold(
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
                                            val levelMap = allCards.associate { it.cardId to it.level }
                                            battleViewModel.startBattle(
                                                heroId = profile?.selectedHeroId ?: "hero_kasparov",
                                                arenaId = profile?.selectedArenaId ?: "arena_titans_peak",
                                                deckCardIds = safeDeck,
                                                opponentName = opp.name,
                                                opponentTrophies = opp.trophies,
                                                cardLevels = levelMap
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
                                TournamentsScreen()
                            }

                            "RANKED" -> {
                                LeaderboardScreen()
                            }

                            "CLAN" -> {
                                ClanScreen(clans = clans)
                            }

                            "CASTLE" -> {
                                CastleProgressionScreen(
                                    profile = profile,
                                    onUpgradeCastle = { mainViewModel.upgradeCastleHall() }
                                )
                            }

                            "SHOP" -> {
                                ArmouryShopScreen()
                            }

                            else -> {
                                HomeScreen(
                                    profile = profile,
                                    chestSlots = chestSlots,
                                    onStartBattle = {},
                                    onOpenChest = {},
                                    onNavigateTab = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
