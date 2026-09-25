package com.anter.plusmessenger.ui.screens.conversations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.anter.plusmessenger.util.AvatarUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    onLogout: () -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFindFriends: () -> Unit,
    vm: ConversationsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المحادثات", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { vm.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "تحديث")
                    }
                    IconButton(onClick = onOpenFindFriends) {
                        Icon(Icons.Filled.PersonSearch, contentDescription = "أشخاص قد تعرفهم")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "الإعدادات")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "تسجيل الخروج")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { vm.refresh() }) { Text("إعادة المحاولة") }
                    }
                }
                state.items.isEmpty() -> {
                    Text(
                        "لا توجد محادثات بعد.",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    val onlineContacts = state.contacts.filter { it.isOnline == true }
                    LazyColumn(Modifier.fillMaxSize()) {
                        if (onlineContacts.isNotEmpty()) {
                            item(key = "online_section") {
                                OnlineNowSection(
                                    contacts = onlineContacts,
                                    onOpenChat = onOpenChat,
                                    onOpenProfile = onOpenProfile
                                )
                            }
                            item(key = "online_divider") {
                                HorizontalDivider(
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                        items(state.items, key = { it.user.id }) { item ->
                            ConversationRow(item, onOpenChat = { onOpenChat(item.user.username) }, onOpenProfile = { onOpenProfile(item.user.username) })
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 72.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(
    item: com.anter.plusmessenger.data.api.models.ConversationDto,
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenChat)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clickable(onClick = onOpenProfile)
        ) {
            AsyncImage(
                model = AvatarUtil.url(item.user),
                contentDescription = item.user.name ?: item.user.username,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            if (item.user.isOnline == true) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF26A649))
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = item.user.name ?: item.user.username,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.latestMessage?.content ?: "—",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (item.unreadCount > 0) {
            Spacer(Modifier.width(8.dp))
            Badge { Text(item.unreadCount.toString()) }
        }
    }
}

@Composable
private fun OnlineNowSection(
    contacts: List<com.anter.plusmessenger.data.api.models.UserDto>,
    onOpenChat: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "المتصلون الآن",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "(${contacts.size})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(contacts, key = { it.id }) { contact ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(72.dp)
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { onOpenProfile(contact.username) }
                    ) {
                        AsyncImage(
                            model = AvatarUtil.url(contact),
                            contentDescription = contact.name ?: contact.username,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF26A649))
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = (contact.name ?: contact.username).split(" ").first(),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProfile(contact.username) }
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}
