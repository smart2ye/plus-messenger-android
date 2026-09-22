package com.anter.plusmessenger.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.anter.plusmessenger.data.api.models.UpdateSettingsRequest
import com.anter.plusmessenger.data.api.models.UserDto
import com.anter.plusmessenger.util.AvatarUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.savedMessage) {
        state.savedMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearSavedMessage()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }
    LaunchedEffect(Unit) { vm.loadBlockedUsers() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
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
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null && state.settings == null -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.load() }) { Text("إعادة المحاولة") }
                }

                state.settings != null -> {
                    val s = state.settings!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // ----- الخصوصية -----
                        SectionTitle("الخصوصية")

                        DropdownSetting(
                            label = "من يمكنه رؤية ملفي",
                            currentValue = s.profileVisibility,
                            options = listOf(
                                "public" to "الجميع",
                                "followers" to "المتابعون فقط"
                            ),
                            enabled = !state.saving,
                            onSelect = { v ->
                                vm.save(UpdateSettingsRequest(profileVisibility = v))
                            }
                        )

                        DropdownSetting(
                            label = "من يمكنه الكتابة على حائطي",
                            currentValue = s.wallVisibility,
                            options = listOf(
                                "public" to "أي مستخدم مسجّل",
                                "followers" to "المتابعون فقط",
                                "mutual" to "المتابعة المتبادلة",
                                "none" to "مغلق (لا أحد)"
                            ),
                            enabled = !state.saving,
                            onSelect = { v ->
                                vm.save(UpdateSettingsRequest(wallVisibility = v))
                            }
                        )

                        DropdownSetting(
                            label = "من يمكنه إرسال رسائل إليّ",
                            currentValue = s.messagePrivacy,
                            options = listOf(
                                "everyone" to "الجميع",
                                "followers" to "المتابعون فقط",
                                "none" to "لا أحد"
                            ),
                            enabled = !state.saving,
                            onSelect = { v ->
                                vm.save(UpdateSettingsRequest(messagePrivacy = v))
                            }
                        )

                        SwitchSetting(
                            label = "إظهار حالة اتصالي",
                            description = "عند الإيقاف، لن يظهر متى كنت متصلًا للآخرين.",
                            checked = s.showOnlineStatus,
                            enabled = !state.saving,
                            onChange = { v ->
                                vm.save(UpdateSettingsRequest(showOnlineStatus = v))
                            }
                        )

                        SwitchSetting(
                            label = "إخفاء قوائم المتابعين",
                            description = "إخفاء من أتابع ومن يتابعني عن الآخرين.",
                            checked = s.hideFollowers,
                            enabled = !state.saving,
                            onChange = { v ->
                                vm.save(UpdateSettingsRequest(hideFollowers = v))
                            }
                        )

                        Spacer(Modifier.height(24.dp))

                        // ----- المحظورون -----
                        SectionTitle("المستخدمون المحظورون (${state.blockedUsers.size})")

                        if (state.blockedLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(Modifier.size(28.dp))
                            }
                        } else if (state.blockedUsers.isEmpty()) {
                            Text(
                                "لا يوجد مستخدمون محظورون.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            state.blockedUsers.forEach { user ->
                                BlockedUserRow(
                                    user = user,
                                    onUnblock = { vm.unblock(user.username) }
                                )
                            }
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)
    )
}

@Composable
private fun DropdownSetting(
    label: String,
    currentValue: String,
    options: List<Pair<String, String>>,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.firstOrNull { it.first == currentValue }?.second ?: currentValue

    Column(Modifier.padding(vertical = 6.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(currentLabel, modifier = Modifier.weight(1f))
                Text("▾")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (value, lbl) ->
                    DropdownMenuItem(
                        text = { Text(lbl) },
                        onClick = {
                            expanded = false
                            if (value != currentValue) onSelect(value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SwitchSetting(
    label: String,
    description: String?,
    checked: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            description?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            enabled = enabled
        )
    }
}

@Composable
private fun BlockedUserRow(
    user: com.anter.plusmessenger.data.api.models.BlockedUserDto,
    onUnblock: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = AvatarUtil.url(
                UserDto(
                    id = user.id,
                    username = user.username,
                    name = user.name,
                    avatar = user.avatar,
                    isOnline = user.isOnline
                )
            ),
            contentDescription = user.name ?: user.username,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                user.name ?: user.username,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "@${user.username}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(onClick = onUnblock) {
            Text("رفع الحظر")
        }
    }
}
