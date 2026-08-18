package com.example.ui.tournaments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.UserProfileEntity
import com.example.data.model.GameCatalog
import com.example.data.model.TournamentEvent
import com.example.ui.theme.*

@Composable
fun TournamentsScreen(
    profile: UserProfileEntity?,
    onOpenLeaderboard: () -> Unit
) {
    var selectedTournamentForBracket by remember { mutableStateOf<TournamentEvent?>(null) }
    var selectedTab by remember { mutableStateOf("TOURNAMENTS") } // "TOURNAMENTS", "BRACKETS"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        // Tab Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ElegantCardBg)
                .border(1.dp, ElegantBorder, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == "TOURNAMENTS") ElegantPrimaryLavender else Color.Transparent)
                    .clickable { selectedTab = "TOURNAMENTS" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GLOBAL TOURNAMENTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "TOURNAMENTS") ElegantPrimaryDark else ElegantTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == "LEADERBOARD") ElegantPrimaryLavender else Color.Transparent)
                    .clickable { onOpenLeaderboard() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RANKED LEADERBOARD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "LEADERBOARD") ElegantPrimaryDark else ElegantTextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Text(
                    text = "ESPORTS CHAMPIONSHIPS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = ElegantPrimaryLavender
                )
            }

            items(GameCatalog.TOURNAMENTS) { tourney ->
                TournamentCard(
                    tourney = tourney,
                    onViewBracket = { selectedTournamentForBracket = tourney }
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "👑 ESPORTS QUALIFIER SCHEDULE",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "Top 16 players on the global leaderboard at the end of each season automatically qualify for the Grandmaster World Finals with live streaming broadcast.",
                            fontSize = 11.sp,
                            color = ElegantTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }

    selectedTournamentForBracket?.let { tourney ->
        TournamentBracketDialog(
            tourney = tourney,
            onDismiss = { selectedTournamentForBracket = null }
        )
    }
}

@Composable
private fun TournamentCard(
    tourney: TournamentEvent,
    onViewBracket: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (tourney.status == "LIVE NOW") ElegantPrimaryLavender else ElegantBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_tournament_${tourney.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (tourney.status == "LIVE NOW") ElegantPrimaryDark else ElegantBorder)
                        .border(
                            1.dp,
                            if (tourney.status == "LIVE NOW") ElegantPrimaryLavender else ElegantBorder,
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tourney.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tourney.status == "LIVE NOW") ElegantPrimaryLavender else ElegantTextSecondary
                    )
                }

                Text(
                    text = "Entry: ${tourney.entryFeeGold} 🪙",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantAccentLight
                )
            }

            Text(
                text = tourney.title,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = ElegantTextPrimary
            )

            Text(
                text = "Format: ${tourney.format}",
                fontSize = 11.sp,
                color = ElegantTextSecondary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantDarkBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Prize Pool", fontSize = 9.sp, color = ElegantTextSecondary)
                    Text(text = tourney.prizePool, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
                }

                Text(
                    text = "${tourney.participantCount}/${tourney.maxParticipants} Registered",
                    fontSize = 10.sp,
                    color = ElegantTextSecondary
                )
            }

            Button(
                onClick = onViewBracket,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElegantPrimaryLavender),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_view_bracket_${tourney.id}")
            ) {
                Text(
                    text = "VIEW LIVE BRACKET",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = ElegantPrimaryDark
                )
            }
        }
    }
}

@Composable
fun TournamentBracketDialog(
    tourney: TournamentEvent,
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
                .testTag("dialog_tournament_bracket")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🏆 TOURNAMENT BRACKET: SEMI-FINALS",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = ElegantPrimaryLavender
                )

                // Matchup 1
                BracketMatchupItem(player1 = "Kasparov_99", score1 = "2", player2 = "DragonSlayer", score2 = "1", isLive = true)

                // Matchup 2
                BracketMatchupItem(player1 = "MagnusPro_99", score1 = "2", player2 = "ValkyrieQueen_X", score2 = "0", isLive = false)

                // Championship Match
                Text(
                    text = "FINALS: Kasparov_99 vs MagnusPro_99",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ElegantAccentLight
                )

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPrimaryLavender),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "CLOSE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ElegantPrimaryDark)
                }
            }
        }
    }
}

@Composable
private fun BracketMatchupItem(
    player1: String,
    score1: String,
    player2: String,
    score2: String,
    isLive: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ElegantDarkBg)
            .border(1.dp, if (isLive) ElegantEmerald else ElegantBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = player1, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantTextPrimary)
            Text(text = score1, fontSize = 11.sp, fontWeight = FontWeight.Black, color = ElegantPrimaryLavender)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = player2, fontSize = 11.sp, color = ElegantTextSecondary)
            Text(text = score2, fontSize = 11.sp, fontWeight = FontWeight.Black, color = ElegantTextSecondary)
        }
    }
}
