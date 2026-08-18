package com.example.ui.clan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.example.data.local.ClanEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.GameCatalog
import com.example.ui.theme.*

data class ClanChatMessage(
    val id: Long,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isCardRequest: Boolean = false,
    val cardId: String? = null,
    val donationsDone: Int = 0,
    val donationsTarget: Int = 4
)

@Composable
fun ClanScreen(
    profile: UserProfileEntity?,
    clans: List<ClanEntity>,
    onJoinClan: (String) -> Unit,
    onDonate: (Long, String) -> Unit,
    onSendChat: (String, Boolean, String?) -> Unit
) {
    var selectedTab by remember { mutableStateOf("CHAT") } // "CHAT", "WAR", "MEMBERS", "EXPLORE"
    var chatInputText by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            ClanChatMessage(1, "Hikaru_Speed", "Ready for Clan War weekend! Need 2 more attackers.", "10:14 AM"),
            ClanChatMessage(2, "Vishy_Master", "Can anyone spare Hierophant cards for upgrade?", "10:32 AM", isCardRequest = true, cardId = "card_hierophant", donationsDone = 2, donationsTarget = 4),
            ClanChatMessage(3, "Kasparov_99", "I've placed my Hierophant diagonal defense setup.", "11:05 AM")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        // Clan Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimaryLavender.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ElegantPrimaryDark)
                            .border(1.dp, ElegantPrimaryLavender, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 20.sp)
                    }

                    Column {
                        Text(
                            text = profile?.clanName ?: "Grandmaster Order",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "#99GMD • 48/50 Members • 🏆 28,400",
                            fontSize = 10.sp,
                            color = ElegantTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ElegantPrimaryDark)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "WAR: LIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElegantEmerald)
                }
            }
        }

        // Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ElegantCardBg)
                .border(1.dp, ElegantBorder, RoundedCornerShape(12.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val tabs = listOf("CHAT", "WAR", "MEMBERS", "EXPLORE")
            tabs.forEach { tabName ->
                val isSelected = selectedTab == tabName
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElegantPrimaryLavender else Color.Transparent)
                        .clickable { selectedTab = tabName }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) ElegantPrimaryDark else ElegantTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        when (selectedTab) {
            "CHAT" -> {
                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(messages) { msg ->
                        ClanMessageBubble(
                            message = msg,
                            onDonate = {
                                onDonate(msg.id, msg.cardId ?: "card_hierophant")
                            }
                        )
                    }
                }

                // Chat Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatInputText,
                        onValueChange = { chatInputText = it },
                        placeholder = { Text("Message clan comrades...", fontSize = 11.sp, color = ElegantTextSecondary) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ElegantCardBg,
                            unfocusedContainerColor = ElegantCardBg,
                            focusedBorderColor = ElegantPrimaryLavender,
                            unfocusedBorderColor = ElegantBorder,
                            focusedTextColor = ElegantTextPrimary,
                            unfocusedTextColor = ElegantTextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_clan_chat")
                    )

                    Button(
                        onClick = {
                            if (chatInputText.isNotBlank()) {
                                messages.add(
                                    ClanChatMessage(
                                        id = System.currentTimeMillis(),
                                        senderName = profile?.username ?: "Kasparov_99",
                                        text = chatInputText,
                                        timestamp = "Now"
                                    )
                                )
                                onSendChat(chatInputText, false, null)
                                chatInputText = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantPrimaryLavender),
                        contentPadding = PaddingValues(10.dp),
                        modifier = Modifier.testTag("btn_send_clan_chat")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = ElegantPrimaryDark)
                    }
                }
            }

            "WAR" -> {
                ClanWarView()
            }

            "MEMBERS" -> {
                ClanMembersView()
            }

            "EXPLORE" -> {
                ClanExploreView(clans = clans, onJoinClan = onJoinClan)
            }
        }
    }
}

@Composable
private fun ClanMessageBubble(
    message: ClanChatMessage,
    onDonate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = message.senderName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ElegantPrimaryLavender
                )
                Text(
                    text = message.timestamp,
                    fontSize = 9.sp,
                    color = ElegantTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message.text,
                fontSize = 12.sp,
                color = ElegantTextPrimary
            )

            if (message.isCardRequest && message.cardId != null) {
                val card = GameCatalog.getCard(message.cardId)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElegantDarkBg)
                        .border(1.dp, ElegantPrimaryLavender.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = card.symbolChar, fontSize = 20.sp, color = ElegantTextPrimary)
                        Column {
                            Text(text = card.name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ElegantTextPrimary)
                            Text(text = "${message.donationsDone}/${message.donationsTarget} Donated", fontSize = 9.sp, color = ElegantTextSecondary)
                        }
                    }

                    Button(
                        onClick = onDonate,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantEmerald),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "DONATE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ElegantPrimaryDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClanWarView() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantPrimaryLavender),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "⚔️ CLAN WAR COLOSSEUM",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = ElegantPrimaryLavender
                )
                Text(
                    text = "War Day 2/3: Grandmaster Order (12,400 pts) vs Shadow Syndicate (11,800 pts)",
                    fontSize = 11.sp,
                    color = ElegantTextPrimary
                )
                LinearProgressIndicator(
                    progress = { 0.65f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = ElegantEmerald,
                    trackColor = ElegantDarkBg
                )
                Text(text = "War Chest: 15,000 Gold + Legendary Wild Card", fontSize = 10.sp, color = ElegantAccentLight)
            }
        }
    }
}

@Composable
private fun ClanMembersView() {
    val members = listOf(
        Pair("Hikaru_Speed", "Leader • 4,250 🏆"),
        Pair("Kasparov_99", "Co-Leader • 3,840 🏆"),
        Pair("Vishy_Master", "Elder • 3,550 🏆"),
        Pair("Judit_Legend", "Member • 3,390 🏆")
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(members) { (name, role) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantCardBg)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElegantTextPrimary)
                Text(text = role, fontSize = 10.sp, color = ElegantTextSecondary)
            }
        }
    }
}

@Composable
private fun ClanExploreView(
    clans: List<ClanEntity>,
    onJoinClan: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clans) { clan ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ElegantCardBg)
                    .border(1.dp, ElegantBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = clan.badge, fontSize = 22.sp)
                    Column {
                        Text(text = clan.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElegantTextPrimary)
                        Text(text = "${clan.memberCount}/50 • Req: ${clan.requiredTrophies} 🏆", fontSize = 10.sp, color = ElegantTextSecondary)
                    }
                }

                Button(
                    onClick = { onJoinClan(clan.id) },
                    enabled = !clan.isPlayerMember,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantPrimaryLavender),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (clan.isPlayerMember) "JOINED" else "JOIN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantPrimaryDark
                    )
                }
            }
        }
    }
}
