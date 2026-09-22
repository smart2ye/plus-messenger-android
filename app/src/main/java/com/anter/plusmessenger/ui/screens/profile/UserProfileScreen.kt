package com.anter.plusmessenger.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.anter.plusmessenger.util.AvatarUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    vm: UserProfileViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.reportSuccess) {
        if (state.reportSuccess) {
            snackbarHostState.showSnackbar("تم إرسال البلاغ، شكرًا لك.")
            vm.clearReportResult()
        }
    }
    LaunchedEffect(state.reportError) {
        state.reportError?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearReportResult()
        }
    }
    LaunchedEffect(state.blockMessage) {
        state.blockMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearBlockMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("البروفايل", fontWeight = FontWeight.Bold) },
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

                state.error != null -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.load() }) { Text("إعادة المحاولة") }
                }

                state.user != null -> {
                    val u = state.user!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(120.dp)) {
                            AsyncImage(
                                model = AvatarUtil.url(
                                    com.anter.plusmessenger.data.api.models.UserDto(
                                        id = u.id,
                                        username = u.username,
                                        name = u.name,
                                        avatar = u.avatar,
                                        isOnline = u.isOnline
                                    )
                                ),
                                contentDescription = u.name ?: u.username,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )
                            if (u.isOnline == true) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(Color(0xFF26A649))
                                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            u.name ?: u.username,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "@${u.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (u.isOnline == true) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "متصل الآن",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF26A649)
                            )
                        } else {
                            u.lastSeen?.let { ls ->
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "آخر ظهور: ${formatLastSeen(ls)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (!u.bio.isNullOrBlank()) {
                            Spacer(Modifier.height(20.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    u.bio!!,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        if (!u.isSelf) {
                            Button(
                                onClick = { onOpenChat(u.username) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = u.isMutual
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (u.isMutual) "إرسال رسالة"
                                    else "يتطلب متابعة متبادلة"
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { showReportDialog = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = !state.reportInFlight
                            ) {
                                Icon(Icons.Filled.Flag, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("الإبلاغ عن المستخدم")
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { vm.toggleBlock() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = !state.blockInFlight,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Filled.Block, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (u.isBlocked) "إلغاء الحظر" else "حظر المستخدم")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { reason, details ->
                showReportDialog = false
                vm.report(reason, details)
            }
        )
    }
}

@Composable
private fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String?) -> Unit
) {
    val reasons = listOf(
        "spam" to "محتوى مزعج أو ترويجي",
        "harassment" to "تنمر أو مضايقة",
        "hate" to "كراهية أو تحريض",
        "impersonation" to "انتحال شخصية",
        "other" to "سبب آخر"
    )
    var selected by remember { mutableStateOf(reasons.first().first) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("الإبلاغ عن المستخدم") },
        text = {
            Column {
                reasons.forEach { (value, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selected == value,
                            onClick = { selected = value }
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = details,
                    onValueChange = { if (it.length <= 500) details = it },
                    label = { Text("تفاصيل إضافية (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(selected, details.ifBlank { null }) }) {
                Text("إرسال")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

private fun formatLastSeen(iso: String): String {
    return try {
        val t = iso.substringAfter("T", "")
        val d = iso.substringBefore("T", "")
        if (t.length >= 5) "$d $t".substring(0, 16) else iso
    } catch (_: Throwable) {
        iso
    }
}
