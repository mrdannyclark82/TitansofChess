package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.GameCatalog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BoardCell(
    val row: Int,
    val col: Int,
    val pieceSymbol: String? = null,
    val pieceId: String? = null,
    val isPlayerPiece: Boolean = true,
    val isHighlight: Boolean = false
)

data class TowersState(
    val kingTowerHp: Int = 3500,
    val leftTowerHp: Int = 2000,
    val rightTowerHp: Int = 2000
)

data class BattleRuntimeState(
    val heroId: String = "hero_kasparov",
    val arenaId: String = "arena_titans_peak",
    val opponentName: String = "ShadowMaster99",
    val opponentTrophies: Int = 3800,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false
)

class BattleViewModel : ViewModel() {

    private val _isBattleActive = MutableStateFlow(false)
    val isBattleActive = _isBattleActive.asStateFlow()

    private val _battleState = MutableStateFlow(BattleRuntimeState())
    val battleState = _battleState.asStateFlow()

    private val _currentElixir = MutableStateFlow(5f)
    val currentElixir = _currentElixir.asStateFlow()

    private val _heroEnergy = MutableStateFlow(40)
    val heroEnergy = _heroEnergy.asStateFlow()

    private val _playerTowers = MutableStateFlow(TowersState(3500, 2000, 2000))
    val playerTowers = _playerTowers.asStateFlow()

    private val _enemyTowers = MutableStateFlow(TowersState(3200, 1800, 1800))
    val enemyTowers = _enemyTowers.asStateFlow()

    private val _boardGrid = MutableStateFlow<List<BoardCell>>(emptyList())
    val boardGrid = _boardGrid.asStateFlow()

    private val _battleDeck = MutableStateFlow<List<String>>(emptyList())
    val battleDeck = _battleDeck.asStateFlow()

    private val _selectedHandCardId = MutableStateFlow<String?>(null)
    val selectedHandCardId = _selectedHandCardId.asStateFlow()

    private val _matchTimerSeconds = MutableStateFlow(180)
    val matchTimerSeconds = _matchTimerSeconds.asStateFlow()

    private var matchLoopJob: Job? = null

    fun startBattle(
        heroId: String,
        arenaId: String,
        deckCardIds: List<String>,
        opponentName: String,
        opponentTrophies: Int
    ) {
        _isBattleActive.value = true
        _battleState.value = BattleRuntimeState(
            heroId = heroId,
            arenaId = arenaId,
            opponentName = opponentName,
            opponentTrophies = opponentTrophies,
            isGameOver = false,
            isVictory = false
        )
        _currentElixir.value = 5f
        _heroEnergy.value = 50
        _playerTowers.value = TowersState(3500, 2000, 2000)
        _enemyTowers.value = TowersState(3200, 1800, 1800)
        _matchTimerSeconds.value = 180
        _selectedHandCardId.value = null

        val safeDeck = if (deckCardIds.isEmpty()) {
            listOf("card_hierophant", "card_knight_paladin", "card_siege_rook", "card_pawn_sentinels")
        } else {
            deckCardIds.take(4)
        }
        _battleDeck.value = safeDeck

        // Initialize Chess Board with classic starting tactical setups
        val cells = mutableListOf<BoardCell>()
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = when {
                    // Enemy starting pieces (Row 0, 1)
                    r == 0 && (c == 0 || c == 7) -> BoardCell(r, c, "🏰", "card_siege_rook", isPlayerPiece = false)
                    r == 0 && (c == 1 || c == 6) -> BoardCell(r, c, "🐎", "card_knight_paladin", isPlayerPiece = false)
                    r == 0 && (c == 2 || c == 5) -> BoardCell(r, c, "🔮", "card_hierophant", isPlayerPiece = false)
                    r == 0 && c == 3 -> BoardCell(r, c, "⚔️", "card_queen_valkyrie", isPlayerPiece = false)
                    r == 0 && c == 4 -> BoardCell(r, c, "🤴", "enemy_king", isPlayerPiece = false)
                    r == 1 -> BoardCell(r, c, "🛡️", "card_pawn_sentinels", isPlayerPiece = false)

                    // Player starting pieces (Row 6, 7)
                    r == 6 -> BoardCell(r, c, "🛡️", "card_pawn_sentinels", isPlayerPiece = true)
                    r == 7 && (c == 0 || c == 7) -> BoardCell(r, c, "🏰", "card_siege_rook", isPlayerPiece = true)
                    r == 7 && (c == 1 || c == 6) -> BoardCell(r, c, "🐎", "card_knight_paladin", isPlayerPiece = true)
                    r == 7 && (c == 2 || c == 5) -> BoardCell(r, c, "🔮", "card_hierophant", isPlayerPiece = true)
                    r == 7 && c == 3 -> BoardCell(r, c, "⚔️", "card_queen_valkyrie", isPlayerPiece = true)
                    r == 7 && c == 4 -> BoardCell(r, c, "🤴", "player_king", isPlayerPiece = true)

                    else -> BoardCell(r, c)
                }
                cells.add(piece)
            }
        }
        _boardGrid.value = cells

        startBattleLoop()
    }

    private fun startBattleLoop() {
        matchLoopJob?.cancel()
        matchLoopJob = viewModelScope.launch {
            while (_isBattleActive.value && !_battleState.value.isGameOver) {
                delay(1000)

                // Regenerate Elixir (up to 10)
                if (_currentElixir.value < 10f) {
                    _currentElixir.value = (_currentElixir.value + 0.35f).coerceAtMost(10f)
                }

                // Charge hero energy
                if (_heroEnergy.value < 100) {
                    _heroEnergy.value = (_heroEnergy.value + 4).coerceAtMost(100)
                }

                // Tick timer
                if (_matchTimerSeconds.value > 0) {
                    _matchTimerSeconds.value -= 1
                } else {
                    // Match timeout decision
                    val playerTotalHp = _playerTowers.value.kingTowerHp + _playerTowers.value.leftTowerHp + _playerTowers.value.rightTowerHp
                    val enemyTotalHp = _enemyTowers.value.kingTowerHp + _enemyTowers.value.leftTowerHp + _enemyTowers.value.rightTowerHp
                    endBattle(isVictory = playerTotalHp >= enemyTotalHp)
                }

                // AI Opponent attacks tower periodically
                if ((1..5).random() == 1) {
                    val dmg = (80..150).random()
                    _playerTowers.value = _playerTowers.value.copy(
                        leftTowerHp = (_playerTowers.value.leftTowerHp - dmg).coerceAtLeast(0)
                    )
                }
            }
        }
    }

    fun selectCardToPlay(cardId: String) {
        if (_selectedHandCardId.value == cardId) {
            _selectedHandCardId.value = null
        } else {
            _selectedHandCardId.value = cardId
        }
    }

    fun onCellClicked(row: Int, col: Int) {
        val selectedCard = _selectedHandCardId.value
        if (selectedCard != null) {
            val card = GameCatalog.getCard(selectedCard)
            if (_currentElixir.value >= card.elixirCost) {
                _currentElixir.value -= card.elixirCost

                // Place player piece on board
                val currentCells = _boardGrid.value.toMutableList()
                val idx = currentCells.indexOfFirst { it.row == row && it.col == col }
                if (idx != -1) {
                    currentCells[idx] = BoardCell(
                        row = row,
                        col = col,
                        pieceSymbol = card.symbolChar,
                        pieceId = card.id,
                        isPlayerPiece = true
                    )
                    _boardGrid.value = currentCells
                }

                // Damage enemy tower on successful tactical summon
                val dmg = card.attack / 2
                val currentEnemy = _enemyTowers.value
                val newEnemy = if (currentEnemy.leftTowerHp > 0) {
                    currentEnemy.copy(leftTowerHp = (currentEnemy.leftTowerHp - dmg).coerceAtLeast(0))
                } else {
                    currentEnemy.copy(kingTowerHp = (currentEnemy.kingTowerHp - dmg).coerceAtLeast(0))
                }
                _enemyTowers.value = newEnemy

                // Check victory condition
                if (newEnemy.kingTowerHp <= 0) {
                    endBattle(isVictory = true)
                }

                _selectedHandCardId.value = null
            }
        }
    }

    fun triggerHeroAbility() {
        if (_heroEnergy.value >= 100) {
            _heroEnergy.value = 0
            // Hero strike damages enemy towers & buffs allies
            val currentEnemy = _enemyTowers.value
            _enemyTowers.value = currentEnemy.copy(
                kingTowerHp = (currentEnemy.kingTowerHp - 450).coerceAtLeast(0)
            )
            if (_enemyTowers.value.kingTowerHp <= 0) {
                endBattle(isVictory = true)
            }
        }
    }

    fun endBattle(isVictory: Boolean) {
        _battleState.value = _battleState.value.copy(
            isGameOver = true,
            isVictory = isVictory
        )
        matchLoopJob?.cancel()
    }

    fun dismissGameOver() {
        _isBattleActive.value = false
        _battleState.value = _battleState.value.copy(isGameOver = false)
    }
}
