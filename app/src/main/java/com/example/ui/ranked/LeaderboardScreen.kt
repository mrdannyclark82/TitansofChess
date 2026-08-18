package com.example.ui.ranked

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.*

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val clanName: String,
    val trophies: Int,
    val winRate: String,
    val avatarLetter: String
)

@Composable
fun LeaderboardScreen(
    profile: UserProfileEntity?,
    onBack: () -> Unit
) {
    val topPlayers = listOf(
        LeaderboardEntry(1, "Hikaru_Speed", "Grandmaster Order", 4250, "78%", "H"),
        LeaderboardEntry(2, "Magnus_King", "Iron Vanguard", 4190, "76%", "M"),
        LeaderboardEntry(3, "Kasparov_99", "Grandmaster Order", 3840, "72%", "K"),
        LeaderboardEntry(4, "Gotham_Tactics", "Royal Knights", 3780, "69%", "G"),
        LeaderboardEntry(5, "Anna_Queen", "Shadow Syndicate", 3690, "68%", "A"),
        LeaderboardEntry(6, "Vishy_Master", "Grandmaster Order", 3550, "66%", "V"),
        LeaderboardEntry(7, "Nodirbek_Pro", "Iron Vanguard", 3420, "65%", "N"),
        LeaderboardEntry(8, "Judit_Legend", "Royal Knights", 3390, "64%", "J")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GLOBAL RANKED LEADERBOARD",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp,
                    color = ElegantPrimaryLavender
                )
                Text(
                    text = "Season 4: Titan's Arena Championship",
                    fontSize = 10.sp,
                    color = ElegantTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ElegantPrimaryDark)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "TOP 100", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElegantPrimaryLavender)
            }
        }

        // Leaderboard List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            itemsIndexed(topPlayers) { _, entry ->
                val isCurrentPlayer = entry.username == (profile?.username ?: "Kasparov_99")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isCurrentPlayer) ElegantPrimaryDark else ElegantCardBg)
                        .border(
                            1.dp,
                            if (isCurrentPlayer) ElegantPrimaryLavender else ElegantBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .padding(12.dp)
                        .testTag("leaderboard_row_${entry.rank}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Rank badge
                        val rankColor = when (entry.rank) {
                            1 -> ElegantPrimaryLavender
                            2 -> ElegantAccentLight
                            3 -> ElegantCoral
                            else -> ElegantTextSecondary
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(ElegantDarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${entry.rank}",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = rankColor
                            )
                        }

                        Column {
                            Text(
                                text = entry.username,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ElegantTextPrimary
                            )
                            Text(
                                text = "${entry.clanName} • Winrate ${entry.winRate}",
                                fontSize = 10.sp,
                                color = ElegantTextSecondary
                            )
                        }
                    }

                    Text(
                        text = "🏆 ${entry.trophies}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = ElegantPrimaryLavender
                    )
                }
            }
        }
    }
}
