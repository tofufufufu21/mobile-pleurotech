package com.example.pleurotech.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.example.pleurotech.ai.AiBrief
import com.example.pleurotech.ai.AiRecommendation
import com.example.pleurotech.ai.AssistantPriority
import com.example.pleurotech.ai.AssistantMessage
import com.example.pleurotech.ai.MessageSender
import com.example.pleurotech.ai.PleuroAssistant
import com.example.pleurotech.data.BagLabel
import com.example.pleurotech.data.ContaminationAlert
import com.example.pleurotech.data.DailyTrend
import com.example.pleurotech.data.HarvestForecast
import com.example.pleurotech.data.PleuroTechRepository
import com.example.pleurotech.data.PreHarvestReflection
import com.example.pleurotech.data.ScanCounts
import com.example.pleurotech.data.ScanRecord
import com.example.pleurotech.data.ScanResult
import com.example.pleurotech.data.ScanReport
import com.example.pleurotech.data.ShelfSummary
import com.example.pleurotech.data.TrendInsights
import com.example.pleurotech.data.sync.SyncResult
import com.example.pleurotech.ui.theme.PleurotechTheme
import com.example.pleurotech.util.QrLabelPrinter
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

enum class ThemePreference(val title: String, val icon: String) {
    Light("Light", "☀️"),
    Dark("Dark", "🌙"),
    System("Auto", "📱")
}

data class PleuroColors(
    val appBackground: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val border: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val infoBlue: Color
)

val LightPleuroColors = PleuroColors(
    appBackground = Color(0xFFF4F7F1),
    surface = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFEAF1E6),
    border = Color(0x1F1F3527),
    textPrimary = Color(0xFF1D2D22),
    textMuted = Color(0xFF607067),
    infoBlue = Color(0xFF2F80ED)
)

val DarkPleuroColors = PleuroColors(
    appBackground = Color(0xFF0E1410),
    surface = Color(0xFF17201A),
    surfaceAlt = Color(0xFF212C24),
    border = Color(0x33FFFFFF),
    textPrimary = Color(0xFFF1F6F0),
    textMuted = Color(0xFF98ACA0),
    infoBlue = Color(0xFF4B96FF)
)

val LocalPleuroColors = compositionLocalOf { LightPleuroColors }

val AppBackground: Color
    @Composable get() = LocalPleuroColors.current.appBackground

val Surface: Color
    @Composable get() = LocalPleuroColors.current.surface

val SurfaceAlt: Color
    @Composable get() = LocalPleuroColors.current.surfaceAlt

val Border: Color
    @Composable get() = LocalPleuroColors.current.border

val TextPrimary: Color
    @Composable get() = LocalPleuroColors.current.textPrimary

val TextMuted: Color
    @Composable get() = LocalPleuroColors.current.textMuted

val InfoBlue: Color
    @Composable get() = LocalPleuroColors.current.infoBlue

enum class AppTab(val title: String) {
    Dashboard("Dashboard"),
    Scan("Scan"),
    Labels("Labels"),
    History("History"),
    Analytics("Analytics"),
    Assistant("AI"),
    Settings("Settings")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PleuroTechApp(repository: PleuroTechRepository) {
    var selectedTab by remember { mutableStateOf(AppTab.Dashboard) }
    var themePreference by remember { mutableStateOf(ThemePreference.System) }
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themePreference) {
        ThemePreference.System -> isSystemDark
        ThemePreference.Light -> false
        ThemePreference.Dark -> true
    }
    val currentColors = if (isDark) DarkPleuroColors else LightPleuroColors

    val auth = repository.authManager
    var isAuthenticated by remember { mutableStateOf(auth?.isLoggedIn() ?: false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    CompositionLocalProvider(LocalPleuroColors provides currentColors) {
        PleurotechTheme(darkTheme = isDark) {
            if (!isAuthenticated) {
                AuthScreen(
                    repository = repository,
                    onAuthSuccess = {
                        val user = repository.authManager?.getCurrentUser()
                        selectedTab = if (user?.isOwner == false) AppTab.Scan else AppTab.Dashboard
                        isAuthenticated = true
                    }
                )
            } else {
                val currentUser = repository.authManager?.getCurrentUser()
                val isOwner = currentUser?.isOwner ?: true

                val navTabs = remember(isOwner) {
                    if (isOwner) {
                        listOf(AppTab.Dashboard, AppTab.Scan, AppTab.Labels, AppTab.History, AppTab.Analytics, AppTab.Assistant)
                    } else {
                        listOf(AppTab.Scan, AppTab.History)
                    }
                }

                androidx.compose.runtime.LaunchedEffect(currentUser) {
                    if (!isOwner && selectedTab != AppTab.Scan && selectedTab != AppTab.History) {
                        selectedTab = AppTab.Scan
                    }
                }

                Scaffold(
                    containerColor = AppBackground,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar(containerColor = Surface) {
                            navTabs.forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = { Text(tab.iconLabel(), fontWeight = FontWeight.Bold) },
                                    label = { Text(tab.title) },
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppBackground)
                            .padding(innerPadding)
                    ) {
                        AppHeader(
                            selectedTab = selectedTab,
                            onSelectTab = { selectedTab = it },
                            onOpenSettings = { selectedTab = AppTab.Settings },
                            repository = repository
                        )
                        when (selectedTab) {
                            AppTab.Dashboard -> DashboardScreen(
                                repository = repository,
                                onOpenHistory = { selectedTab = AppTab.History },
                                onOpenScan = { selectedTab = AppTab.Scan }
                            ) { repository.addMockScan() }
                            AppTab.Scan -> ScanScreen(
                                repository = repository,
                                onMockScan = { repository.addMockScan() }
                            )
                            AppTab.Labels -> LabelsScreen(
                                repository = repository,
                                onPrint = { labels, batchName ->
                                    QrLabelPrinter(context).print(labels, batchName)
                                    scope.launch { snackbarHostState.showSnackbar("Printing ${labels.size} QR labels…") }
                                }
                            )
                            AppTab.History -> HistoryScreen(
                                repository = repository,
                                onExport = {
                                    val lines = repository.exportCsv().lineSequence().count()
                                    scope.launch { snackbarHostState.showSnackbar("CSV ready with $lines lines") }
                                },
                                onClear = { repository.clear() }
                            )
                            AppTab.Analytics -> AnalyticsScreen(repository = repository)
                            AppTab.Assistant -> AssistantScreen(repository = repository)
                            AppTab.Settings -> SettingsScreen(
                                isDark = isDark,
                                onToggleDark = { enabled ->
                                    themePreference = if (enabled) ThemePreference.Dark else ThemePreference.Light
                                },
                                repository = repository,
                                onBack = {
                                    val user = repository.authManager?.getCurrentUser()
                                    selectedTab = if (user?.isOwner == false) AppTab.Scan else AppTab.Dashboard
                                },
                                onSignOut = {
                                    selectedTab = AppTab.Dashboard
                                    isAuthenticated = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HamburgerIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        val w = size.width
        val h = size.height
        val lineSpacing = h * 0.32f
        val startY = h * 0.18f

        for (i in 0..2) {
            val y = startY + i * lineSpacing
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun AppHeader(
    selectedTab: AppTab,
    onSelectTab: (AppTab) -> Unit,
    onOpenSettings: () -> Unit,
    repository: PleuroTechRepository
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val sync = repository.syncManager
    val pending = sync?.getPendingCount() ?: 0
    val online = sync?.isOnline() ?: false

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "PleuroTech",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            val user = repository.authManager?.getCurrentUser()
            Text(
                text = if (user != null) "${user.name} · ${user.role}" else "Oyster mushroom farm intelligence",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (pending > 0) {
                StatusPill("☁️ $pending Pending", ScanResult.ClassB.color)
            } else if (online) {
                StatusPill("☁️ Synced", ScanResult.ClassA.color)
            } else {
                StatusPill("Farm Ready", ScanResult.ClassA.color)
            }

            Box {
                // 3-line Hamburger button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Surface)
                        .border(1.dp, Border, CircleShape)
                        .clickable { menuExpanded = !menuExpanded },
                    contentAlignment = Alignment.Center
                ) {
                    HamburgerIcon(
                        color = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Dropdown Menu showing all features
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(14.dp))
                        .widthIn(min = 190.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    val user = repository.authManager?.getCurrentUser()
                    val isOwner = user?.isOwner ?: true

                    if (isOwner) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Dashboard",
                                    fontWeight = if (selectedTab == AppTab.Dashboard) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == AppTab.Dashboard) ScanResult.ClassA.color else TextPrimary
                                )
                            },
                            leadingIcon = { Text("📊", fontSize = 16.sp) },
                            onClick = {
                                onSelectTab(AppTab.Dashboard)
                                menuExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isOwner) "Scan" else "Mushroom Scan",
                                fontWeight = if (selectedTab == AppTab.Scan) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == AppTab.Scan) ScanResult.ClassA.color else TextPrimary
                            )
                        },
                        leadingIcon = { Text("📷", fontSize = 16.sp) },
                        onClick = {
                            onSelectTab(AppTab.Scan)
                            menuExpanded = false
                        }
                    )
                    if (isOwner) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Labels",
                                    fontWeight = if (selectedTab == AppTab.Labels) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == AppTab.Labels) ScanResult.ClassA.color else TextPrimary
                                )
                            },
                            leadingIcon = { Text("🏷️", fontSize = 16.sp) },
                            onClick = {
                                onSelectTab(AppTab.Labels)
                                menuExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isOwner) "History" else "Recent Scans",
                                fontWeight = if (selectedTab == AppTab.History) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == AppTab.History) ScanResult.ClassA.color else TextPrimary
                            )
                        },
                        leadingIcon = { Text("📜", fontSize = 16.sp) },
                        onClick = {
                            onSelectTab(AppTab.History)
                            menuExpanded = false
                        }
                    )
                    if (isOwner) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Analytics",
                                    fontWeight = if (selectedTab == AppTab.Analytics) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == AppTab.Analytics) ScanResult.ClassA.color else TextPrimary
                                )
                            },
                            leadingIcon = { Text("📈", fontSize = 16.sp) },
                            onClick = {
                                onSelectTab(AppTab.Analytics)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "AI Assistant",
                                    fontWeight = if (selectedTab == AppTab.Assistant) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == AppTab.Assistant) ScanResult.ClassA.color else TextPrimary
                                )
                            },
                            leadingIcon = { Text("🤖", fontSize = 16.sp) },
                            onClick = {
                                onSelectTab(AppTab.Assistant)
                                menuExpanded = false
                            }
                        )
                    }
                    HorizontalDivider(color = Border)
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Settings",
                                fontWeight = if (selectedTab == AppTab.Settings) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == AppTab.Settings) ScanResult.ClassA.color else TextPrimary
                            )
                        },
                        leadingIcon = { Text("⚙️", fontSize = 16.sp) },
                        onClick = {
                            onOpenSettings()
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChevronRightIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        val path = Path().apply {
            moveTo(size.width * 0.35f, size.height * 0.25f)
            lineTo(size.width * 0.65f, size.height * 0.5f)
            lineTo(size.width * 0.35f, size.height * 0.75f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun BackArrowIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.2.dp.toPx()
        val w = size.width
        val h = size.height
        drawLine(
            color = color,
            start = Offset(w * 0.25f, h * 0.5f),
            end = Offset(w * 0.75f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        val head = Path().apply {
            moveTo(w * 0.45f, h * 0.3f)
            lineTo(w * 0.25f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.7f)
        }
        drawPath(
            path = head,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun SettingItemCard(
    iconText: String,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    val baseModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(Surface)
        .border(1.dp, Border, RoundedCornerShape(18.dp))
        .padding(horizontal = 16.dp, vertical = 14.dp)

    val finalModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    } else {
        baseModifier
    }

    Row(
        modifier = finalModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SurfaceAlt),
            contentAlignment = Alignment.Center
        ) {
            Text(iconText, fontSize = 20.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        trailing()
    }
}

@Composable
private fun SettingsScreen(
    isDark: Boolean,
    onToggleDark: (Boolean) -> Unit,
    repository: PleuroTechRepository,
    onBack: () -> Unit,
    onSignOut: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showSignOutConfirm by remember { mutableStateOf(false) }

    if (showPrivacyDialog) {
        PrivacyControlsDialog(repository = repository, onDismiss = { showPrivacyDialog = false })
    }
    if (showPermissionsDialog) {
        PermissionsDialog(onDismiss = { showPermissionsDialog = false })
    }
    if (showHelpDialog) {
        HelpFaqDialog(onDismiss = { showHelpDialog = false })
    }
    if (showSyncDialog) {
        SupabaseSyncDialog(repository = repository, onDismiss = { showSyncDialog = false })
    }
    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            containerColor = Surface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text("Sign Out of PleuroTech?", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            },
            text = {
                Text(
                    "You will need to sign in again to access farm records. Your local scans remain safely saved in offline SQLite.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.authManager?.signOut()
                        showSignOutConfirm = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ScanResult.Reject.color),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSignOutConfirm = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Page Title: "Setting" with Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Surface)
                    .border(1.dp, Border, CircleShape)
            ) {
                BackArrowIcon(color = TextPrimary, modifier = Modifier.size(16.dp))
            }
            Text(
                text = "Setting",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Section 1: Protection
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Protection",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            // Dark Mode item
            SettingItemCard(
                iconText = if (isDark) "🌙" else "☀️",
                title = "Dark Mode",
                subtitle = if (isDark) "Enabled" else "Disabled"
            ) {
                Switch(
                    checked = isDark,
                    onCheckedChange = onToggleDark,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ScanResult.ClassA.color,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD4DBD4),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }
            // Notifications item
            SettingItemCard(
                iconText = "🔔",
                title = "Notifications",
                subtitle = if (notificationsEnabled) "Manage alert preferences" else "Muted"
            ) {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ScanResult.ClassA.color,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD4DBD4),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }
        }

        // Section 2: Privacy & Security
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Privacy & Security",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            // Cloud Sync (Supabase)
            val sync = repository.syncManager
            val pendingCount = sync?.getPendingCount() ?: 0
            val syncSubtitle = if (pendingCount > 0) "$pendingCount offline scans pending sync" else "Multi-device cloud synchronization"
            SettingItemCard(
                iconText = "☁️",
                title = "Cloud Sync (Supabase)",
                subtitle = syncSubtitle,
                onClick = { showSyncDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
            // Privacy Controls
            SettingItemCard(
                iconText = "🔒",
                title = "Privacy Controls",
                subtitle = "Manage your data",
                onClick = { showPrivacyDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
            // Permissions
            SettingItemCard(
                iconText = "🛡️",
                title = "Permissions",
                subtitle = "Review app permissions",
                onClick = { showPermissionsDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
        }

        // Section 3: Support
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Support",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            // Help & FAQ
            SettingItemCard(
                iconText = "❓",
                title = "Help & FAQ",
                subtitle = "Get answers",
                onClick = { showHelpDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
        }

        // Section 4: Account & Profile
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Account & Profile",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            val user = repository.authManager?.getCurrentUser()
            // Profile Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ScanResult.ClassA.color.copy(alpha = 0.2f))
                        .border(1.5.dp, ScanResult.ClassA.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.name?.take(1)?.uppercase() ?: "W",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScanResult.ClassA.color
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.name ?: "Farm Inspector",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = user?.email ?: "worker@pleurotech.com",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(Modifier.height(4.dp))
                    StatusPill(label = user?.role ?: "Inspector", color = ScanResult.ClassA.color)
                }
            }

            // Sign Out Card
            SettingItemCard(
                iconText = "🚪",
                title = "Sign Out",
                subtitle = "Log out from this mobile device",
                onClick = { showSignOutConfirm = true }
            ) {
                Text("Log Out", color = ScanResult.Reject.color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PrivacyControlsDialog(repository: PleuroTechRepository, onDismiss: () -> Unit) {
    val counts = repository.counts()
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🔒", fontSize = 20.sp)
                Text("Privacy & Data Controls", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "PleuroTech stores all mushroom inspection records and scan telemetry strictly on your local device for data privacy.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceAlt)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("LOCAL STORAGE METRICS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text("Total Scans: ${counts.total} records", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("Active Batch: ${repository.activeBatch.name}", fontSize = 12.sp, color = TextMuted)
                    Text("Data Encryption: Local sandbox protected", fontSize = 12.sp, color = ScanResult.ClassA.color)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color)
            ) {
                Text("Done", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun PermissionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🛡️", fontSize = 20.sp)
                Text("App Permissions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple("📷 Camera", "Granted", "Required for scanning QR labels & YOLOv8 grading"),
                    Triple("🖨️ Printing Service", "Active", "Required for generating printable PDF QR sheets"),
                    Triple("🔔 Notifications", "Granted", "Used for real-time contamination & harvest alerts"),
                    Triple("📁 Storage", "Active", "Used for CSV data export & temporary report files")
                ).forEach { (name, status, desc) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceAlt)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text(status, color = ScanResult.ClassA.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(desc, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color)
            ) {
                Text("Close", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun HelpFaqDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("❓", fontSize = 20.sp)
                Text("Help & FAQ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "How does YOLOv8 classify mushrooms?" to "The deep learning model analyzes oyster mushroom cap shape, color saturation, and pinhead maturity to assign Class A, Class B, or Reject.",
                    "What makes a mushroom Class A?" to "Smooth margins, convex/flat cap curvature, uniform oyster-white hue, and absence of bacterial blotch or green mold.",
                    "How do I print QR bag tags?" to "Navigate to the Labels tab, select the number of labels or input a custom count, and tap 'Print QR Sheet' to open the Android print preview."
                ).forEach { (q, a) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceAlt)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(q, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text(a, fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color)
            ) {
                Text("Understood", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun SupabaseSyncDialog(
    repository: PleuroTechRepository,
    onDismiss: () -> Unit
) {
    val sync = repository.syncManager
    val scope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf<String?>(null) }
    var testMessage by remember { mutableStateOf<String?>(null) }

    val pendingCount = sync?.getPendingCount() ?: 0
    val isOnline = sync?.isOnline() ?: false
    val lastSync = sync?.getLastSyncTime() ?: "Never"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("☁️", fontSize = 20.sp)
                Text(
                    text = "Cloud Synchronization",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status overview card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceAlt)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("NETWORK CONNECTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        StatusPill(
                            label = if (isOnline) "Connected" else "Offline",
                            color = if (isOnline) ScanResult.ClassA.color else ScanResult.Reject.color
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("MULTI-DEVICE SYNC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("Active (Private)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ScanResult.ClassA.color)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PENDING UPLOADS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = if (pendingCount > 0) "$pendingCount offline scans" else "0 pending",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (pendingCount > 0) ScanResult.ClassB.color else ScanResult.ClassA.color
                        )
                    }

                    Text("Last synced: $lastSync", fontSize = 12.sp, color = TextMuted)
                }

                // Security Note Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceAlt.copy(alpha = 0.5f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔒", fontSize = 16.sp)
                    Text(
                        text = "Cloud credentials are encrypted and embedded directly in the app code to protect farm database security.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }

                // Action 1: Sync Now
                Button(
                    onClick = {
                        if (sync != null) {
                            isSyncing = true
                            syncMessage = null
                            testMessage = null
                            scope.launch {
                                val result = sync.syncNow()
                                isSyncing = false
                                when (result) {
                                    is SyncResult.Success -> {
                                        repository.refreshFromDb()
                                        syncMessage = "✅ Successfully synchronized! Pushed ${result.pushedCount} scans, pulled ${result.pulledCount} from cloud."
                                    }
                                    is SyncResult.Offline -> {
                                        syncMessage = "⚠️ Device is currently offline. Scans remain safely stored in local SQLite."
                                    }
                                    is SyncResult.NotConfigured -> {
                                        syncMessage = "ℹ️ Cloud sync is ready."
                                    }
                                    is SyncResult.Error -> {
                                        syncMessage = "❌ Sync error: ${result.message}"
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isSyncing && !isTesting,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color)
                ) {
                    Text(
                        if (isSyncing) "Syncing with Cloud..." else "Sync Now (Push & Pull)",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Action 2: Test Connection
                OutlinedButton(
                    onClick = {
                        if (sync != null) {
                            isTesting = true
                            testMessage = null
                            syncMessage = null
                            scope.launch {
                                val testRes = sync.testConnection()
                                isTesting = false
                                testMessage = if (testRes.isSuccess) {
                                    "✅ Fully connected to Supabase cloud! Database is active and ready."
                                } else {
                                    "❌ Could not reach Supabase endpoint (${testRes.exceptionOrNull()?.message}). Check internet connection or project status."
                                }
                            }
                        }
                    },
                    enabled = !isSyncing && !isTesting,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isTesting) "Testing Connection..." else "Test Cloud Connection", color = TextPrimary)
                }

                if (syncMessage != null) {
                    Text(syncMessage!!, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
                if (testMessage != null) {
                    Text(testMessage!!, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceAlt)
            ) {
                Text("Close", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun DashboardScreen(
    repository: PleuroTechRepository,
    onOpenHistory: () -> Unit,
    onOpenScan: () -> Unit,
    onMockScan: () -> Unit
) {
    val counts = repository.counts()
    val recent = repository.recentScans()
    val latest = repository.latestScan
    val trend = repository.trend()
    val alerts = repository.contaminationAlerts()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BatchStatusPanel(repository = repository)
        }
        item {
            SectionLabel("Mobile Scan")
            Spacer(Modifier.height(8.dp))
            MobileScanPanel(
                primaryLabel = "Open Scanner",
                onPrimary = onOpenScan,
                onSecondary = onMockScan
            )
        }
        item {
            SectionLabel("Today's Grading Results")
            Spacer(Modifier.height(8.dp))
            GradeSummary(counts = counts)
        }
        item {
            AiCommandCenter(
                brief = PleuroAssistant().brief(repository),
                compact = true
            )
        }
        item {
            ActiveAlert(latest = latest)
        }
        item {
            ContaminationAlertPanel(alerts = alerts)
        }
        item {
            ShelfMapPanel(shelves = repository.shelfMap())
        }
        item {
            ChartCard(title = "Premium Yield Trend", subtitle = "Last 7 days") {
                TrendStackedBars(trend = trend)
            }
        }
        item {
            SectionLabel("Recent Scans")
            Spacer(Modifier.height(8.dp))
            DataCard {
                ScanRows(scans = recent, compact = true)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onMockScan,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceAlt)
                ) {
                    Text("Simulate Scan")
                }
                OutlinedButton(onClick = onOpenHistory) {
                    Text("View All")
                }
            }
        }
    }
}

@Composable
private fun MobileScanPanel(
    primaryLabel: String,
    secondaryLabel: String = "Demo Result",
    onPrimary: () -> Unit,
    onSecondary: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.58f)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF171A24), Color(0xFF22283A), Color(0xFF151821))
                    )
                )
                .border(1.dp, Border, RoundedCornerShape(8.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val laneColor = Color.White.copy(alpha = 0.07f)
                for (i in 1..5) {
                    val y = (size.height * i) / 6f
                    drawLine(laneColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
                }
                for (i in 1..7) {
                    val x = size.width * i / 8f
                    drawLine(laneColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.dp.toPx())
                }
            }
            StatusPill(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                label = "Phone camera",
                color = ScanResult.ClassA.color
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Capture a mushroom bag, then run YOLOv8 classification",
                color = TextMuted,
                fontSize = 13.sp
            )
            Text(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                text = "model pending",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onPrimary,
                colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color)
            ) {
                Text(primaryLabel)
            }
            OutlinedButton(onClick = onSecondary) {
                Text(secondaryLabel)
            }
        }
    }
}

@Composable
private fun ScanScreen(repository: PleuroTechRepository, onMockScan: () -> Unit) {
    val latest = repository.latestScan

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageTitle("Scan", "Phone-only YOLOv8 grading workflow")
        MobileScanPanel(
            primaryLabel = "Scan Current Frame",
            onPrimary = onMockScan,
            onSecondary = onMockScan
        )
        AiCommandCenter(
            brief = PleuroAssistant().brief(repository),
            compact = false
        )
        DataCard {
            Text("Classifier status", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(
                "The trained YOLOv8 model can plug into YoloV8Classifier when it is ready. For now, this screen records demo classification results locally.",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        DataCard {
            Text("Latest result", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            if (latest == null) {
                Text("No mobile scans yet.", color = TextMuted, fontSize = 12.sp)
            } else {
                LatestScanDetail(scan = latest, onVerify = { repository.verifyScan(latest.id, it) })
            }
        }
    }
}

@Composable
private fun LabelsScreen(repository: PleuroTechRepository, onPrint: (List<BagLabel>, String) -> Unit) {
    var labelCount by remember { mutableIntStateOf(24) }
    var customText by remember { mutableStateOf("24") }
    val labels = repository.bagLabels(labelCount)
    val batch = repository.activeBatch

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageTitle("QR Labels", "Printable labels for oyster mushroom bag tracking")
        DataCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(batch.name, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Generate physical labels, attach one to each bag, then scan it to open the exact bag record.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
                StatusPill("${labels.size}/${batch.targetBagCount}", ScanResult.ClassA.color)
            }
            SectionLabel("Print Quantity")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(12, 24, 48, batch.targetBagCount).forEach { count ->
                    val isAll = count == batch.targetBagCount
                    val chipTitle = if (isAll) "All" else count.toString()
                    val isSelected = labelCount == count
                    val bg by animateColorAsState(
                        targetValue = if (isSelected) ScanResult.ClassA.color.copy(alpha = 0.2f) else SurfaceAlt,
                        label = "chipBg"
                    )
                    val border by animateColorAsState(
                        targetValue = if (isSelected) ScanResult.ClassA.color else Border,
                        label = "chipBorder"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) ScanResult.ClassA.color else TextPrimary,
                        label = "chipText"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(10.dp))
                            .clickable {
                                labelCount = count
                                customText = count.toString()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chipTitle,
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            /* ── Stable Stepper & Numeric Input Row ── */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceAlt.copy(alpha = 0.5f))
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Decrement button (−)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (labelCount > 1) Surface else SurfaceAlt)
                        .border(1.dp, if (labelCount > 1) Border else Color.Transparent, CircleShape)
                        .clickable(enabled = labelCount > 1) {
                            val next = (labelCount - 1).coerceAtLeast(1)
                            labelCount = next
                            customText = next.toString()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "−",
                        color = if (labelCount > 1) TextPrimary else TextMuted.copy(alpha = 0.35f),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Middle: Clean numeric input display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface)
                        .border(1.dp, ScanResult.ClassA.color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    BasicTextField(
                        value = customText,
                        onValueChange = { text ->
                            val digits = text.filter { it.isDigit() }.take(3)
                            customText = digits
                            digits.toIntOrNull()?.let { n ->
                                labelCount = n.coerceIn(1, batch.targetBagCount)
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.widthIn(min = 36.dp, max = 64.dp),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.Center) {
                                if (customText.isEmpty()) {
                                    Text(
                                        "0",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted.copy(alpha = 0.35f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "labels",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Increment button (+)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (labelCount < batch.targetBagCount) Surface else SurfaceAlt)
                        .border(1.dp, if (labelCount < batch.targetBagCount) Border else Color.Transparent, CircleShape)
                        .clickable(enabled = labelCount < batch.targetBagCount) {
                            val next = (labelCount + 1).coerceAtMost(batch.targetBagCount)
                            labelCount = next
                            customText = next.toString()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+",
                        color = if (labelCount < batch.targetBagCount) TextPrimary else TextMuted.copy(alpha = 0.35f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick increment helpers row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Range: 1 to ${batch.targetBagCount} bags",
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(5, 10, 20).forEach { step ->
                        Text(
                            text = "+$step",
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceAlt)
                                .border(1.dp, Border, RoundedCornerShape(6.dp))
                                .clickable {
                                    val next = (labelCount + step).coerceAtMost(batch.targetBagCount)
                                    labelCount = next
                                    customText = next.toString()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                onClick = { onPrint(labels, batch.name) },
                enabled = labels.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScanResult.ClassA.color,
                    disabledContainerColor = ScanResult.ClassA.color.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🖨️", fontSize = 16.sp)
                    Text(
                        "Print ${labels.size} QR Labels",
                        color = Color(0xFF155E2B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
        DataCard {
            SectionLabel("Label Sheet Preview")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                labels.forEach { label ->
                    PrintableQrLabel(label = label)
                }
            }
        }
    }
}

@Composable
private fun HistoryScreen(
    repository: PleuroTechRepository,
    onExport: () -> Unit,
    onClear: () -> Unit
) {
    var page by remember { mutableIntStateOf(1) }
    val perPage = 20
    val totalPages = max(1, (repository.scans.size + perPage - 1) / perPage)
    val pageRows = repository.history(page.coerceAtMost(totalPages), perPage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageTitle("Scan History", "${repository.scans.size} total scans recorded")
        GradeSummary(counts = repository.counts())
        ReportPreviewPanel(report = repository.report())
        DataCard {
            ScanRows(
                scans = pageRows,
                compact = false,
                onVerify = { scan, result -> repository.verifyScan(scan.id, result) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                enabled = page > 1,
                onClick = { page-- }
            ) {
                Text("Previous")
            }
            Text("Page $page of $totalPages", color = TextMuted, fontSize = 12.sp)
            OutlinedButton(
                enabled = page < totalPages,
                onClick = { page++ }
            ) {
                Text("Next")
            }
        }
        val isOwner = repository.authManager?.getCurrentUser()?.isOwner ?: true
        if (isOwner) {
            DataCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Export data", color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text("Create CSV with batch, shelf, QR, AI result, final result, and verification state", color = TextMuted, fontSize = 12.sp)
                    }
                    OutlinedButton(onClick = onExport) {
                        Text("Export")
                    }
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onClear()
                    page = 1
                }
            ) {
                Text("Clear Demo Data")
            }
        }
    }
}

@Composable
private fun AnalyticsScreen(repository: PleuroTechRepository) {
    val counts = repository.counts()
    var selectedDays by remember { mutableIntStateOf(7) }
    val trend = repository.trend(selectedDays)
    val insights = repository.insights(selectedDays)
    val forecast = repository.harvestForecast(selectedDays)
    val reflection = repository.preHarvestReflection(selectedDays)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageTitle("Analytics", "$selectedDays-day performance overview")
        TrendWindowSelector(selectedDays = selectedDays, onSelect = { selectedDays = it })
        AdvancedInsightHero(insights = insights)
        ForecastPanel(forecast = forecast)
        PreHarvestReflectionPanel(reflection = reflection)
        InsightGrid(insights = insights)
        GradeSummary(counts = counts)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RateCard(
                modifier = Modifier.weight(1f),
                label = "Premium Rate",
                value = counts.premiumRate,
                color = ScanResult.ClassA.color,
                subtitle = "Class A bags"
            )
            RateCard(
                modifier = Modifier.weight(1f),
                label = "Reject Rate",
                value = counts.rejectRate,
                color = ScanResult.Reject.color,
                subtitle = "Diseased bags"
            )
        }
        ChartCard(title = "Grade Distribution", subtitle = "All time") {
            DistributionDonut(counts = counts)
        }
        ChartCard(title = "Daily Totals", subtitle = "Last $selectedDays days", showLegend = false) {
            DailyTotalsBars(trend = trend)
        }
        ChartCard(title = "Reject Risk", subtitle = "Rate by day", showLegend = false) {
            RejectRateLineChart(trend = trend)
        }
        ChartCard(title = "Grade Breakdown Per Day", subtitle = "Last $selectedDays days") {
            TrendStackedBars(trend = trend)
        }
        SectionLabel("Daily Breakdown Table")
        DataCard {
            trend.forEachIndexed { index, day ->
                DailyTrendRow(day = day)
                if (index != trend.lastIndex) HorizontalDivider(color = Border)
            }
        }
    }
}

@Composable
private fun AssistantScreen(repository: PleuroTechRepository) {
    val assistant = remember { PleuroAssistant() }
    var input by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            AssistantMessage(
                sender = MessageSender.Assistant,
                text = assistant.welcome(repository)
            )
        )
    }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    fun sendPrompt(prompt: String) {
        val cleaned = prompt.trim()
        if (cleaned.isEmpty()) return
        messages.add(AssistantMessage(MessageSender.User, cleaned))
        messages.add(AssistantMessage(MessageSender.Assistant, assistant.answer(cleaned, repository)))
        input = ""
        scope.launch {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        /* ── Header ── */
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PageTitle("AI Assistant", "Scan insights and quality recommendations")
            val insights = repository.insights(7)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Quality Copilot", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        "Risk ${insights.riskLevel.lowercase()} · ${"%,.1f".format(insights.rejectRate)}% rejects · ${insights.scanCount} scans",
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                StatusPill(insights.trendDirection, riskColor(insights.riskLevel))
            }
        }

        /* ── Chat messages ── */
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 4.dp, bottom = 12.dp)
        ) {
            items(messages.size) { index ->
                AssistantBubble(message = messages[index])
            }
        }

        /* ── Bottom Input & AI Buttons Container ── */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .imePadding()
                .padding(top = 10.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            /* ── AI Suggestion Chips (Horizontal Smooth Scroll) ── */
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val icons = listOf("📊", "⚠️", "🔍", "⚡", "💡")
                val suggestions = assistant.suggestions()
                items(suggestions.size) { index ->
                    val suggestion = suggestions[index]
                    AiSuggestionChip(
                        title = suggestion.title,
                        icon = icons.getOrElse(index) { "•" },
                        onClick = {
                            focusManager.clearFocus()
                            sendPrompt(suggestion.prompt)
                        }
                    )
                }
            }

            /* ── Typing Bar Row ── */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = input,
                    onValueChange = { input = it },
                    placeholder = {
                        Text(
                            "Ask AI about rejects, batch risk, advice…",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceAlt.copy(alpha = 0.35f),
                        unfocusedContainerColor = SurfaceAlt.copy(alpha = 0.25f),
                        focusedBorderColor = InfoBlue,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = InfoBlue
                    ),
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = input.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(TextMuted.copy(alpha = 0.15f))
                                    .clickable { input = "" },
                                contentAlignment = Alignment.Center
                            ) {
                                ClearIcon(color = TextMuted, modifier = Modifier.size(12.dp))
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (input.isNotBlank()) {
                                focusManager.clearFocus()
                                sendPrompt(input)
                            }
                        }
                    )
                )

                val isReady = input.isNotBlank()
                val sendBg by animateColorAsState(
                    targetValue = if (isReady) InfoBlue else SurfaceAlt,
                    label = "sendBg"
                )
                val sendContentColor by animateColorAsState(
                    targetValue = if (isReady) Color.White else TextMuted.copy(alpha = 0.45f),
                    label = "sendContentColor"
                )
                val sendScale by animateFloatAsState(
                    targetValue = if (isReady) 1.0f else 0.92f,
                    label = "sendScale"
                )

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .scale(sendScale)
                        .clip(CircleShape)
                        .background(sendBg)
                        .border(
                            1.dp,
                            if (isReady) InfoBlue.copy(alpha = 0.3f) else Border,
                            CircleShape
                        )
                        .clickable(enabled = isReady) {
                            focusManager.clearFocus()
                            sendPrompt(input)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    SendIcon(
                        color = sendContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AssistantSummary(repository: PleuroTechRepository) {
    val insights = repository.insights(7)
    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Assistant brief", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    "Risk ${insights.riskLevel.lowercase()} with ${"%.1f".format(insights.rejectRate)}% rejects over 7 days.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            StatusPill(insights.trendDirection, riskColor(insights.riskLevel))
        }
    }
}

@Composable
private fun BatchStatusPanel(repository: PleuroTechRepository) {
    val batch = repository.activeBatch
    val reflection = repository.preHarvestReflection()
    val counts = repository.counts()
    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                SectionLabel("Active Oyster Batch")
                Text(batch.name, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "${counts.total}/${batch.targetBagCount} bags scanned since ${batch.startedDate}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            StatusPill(reflection.readinessLabel, readinessColor(reflection.readinessScore))
        }
        ConfidenceBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp),
            result = ScanResult.ClassA,
            confidence = (counts.total.toFloat() / batch.targetBagCount).coerceIn(0f, 1f)
        )
    }
}

@Composable
private fun ContaminationAlertPanel(alerts: List<ContaminationAlert>) {
    Column {
        SectionLabel("Disease And Contamination Alerts")
        Spacer(Modifier.height(8.dp))
        DataCard {
            if (alerts.isEmpty()) {
                Text("No contamination cluster detected in the active oyster batch.", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("Shelf rejection patterns are inside the normal monitoring range.", color = TextMuted, fontSize = 12.sp)
            } else {
                alerts.forEachIndexed { index, alert ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        StatusPill(alert.severity, if (alert.severity == "High") ScanResult.Reject.color else ScanResult.ClassB.color)
                        Column(Modifier.weight(1f)) {
                            Text(alert.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(alert.detail, color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }
                    if (index != alerts.lastIndex) HorizontalDivider(color = Border)
                }
            }
        }
    }
}

@Composable
private fun ShelfMapPanel(shelves: List<ShelfSummary>) {
    Column {
        SectionLabel("Shelf Map")
        Spacer(Modifier.height(8.dp))
        DataCard {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                shelves.forEach { shelf ->
                    ShelfTile(shelf = shelf)
                }
            }
        }
    }
}

@Composable
private fun ShelfTile(shelf: ShelfSummary) {
    val color = shelfStatusColor(shelf)
    Column(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.13f))
            .border(1.dp, color.copy(alpha = 0.32f), RoundedCornerShape(8.dp))
            .padding(9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(shelf.shelfCode, color = color, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text("${shelf.total} scans", color = TextMuted, fontSize = 10.sp, maxLines = 1)
        Text("${shelf.reject} R", color = ScanResult.Reject.color, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun AiCommandCenter(brief: AiBrief, compact: Boolean) {
    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                SectionLabel("AI Quality Copilot")
                Text(
                    text = brief.headline,
                    color = priorityColor(brief.priority),
                    fontSize = if (compact) 20.sp else 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = brief.summary,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            StatusPill(brief.confidenceLabel, priorityColor(brief.priority))
        }
        Spacer(Modifier.height(8.dp))
        AiSignalStrip(signals = brief.anomalySignals)
        if (!compact) {
            Spacer(Modifier.height(8.dp))
            brief.recommendations.forEachIndexed { index, recommendation ->
                RecommendationRow(recommendation = recommendation)
                if (index != brief.recommendations.lastIndex) HorizontalDivider(color = Border)
            }
        } else {
            brief.recommendations.firstOrNull()?.let { recommendation ->
                Spacer(Modifier.height(6.dp))
                RecommendationRow(recommendation = recommendation)
            }
        }
    }
}

@Composable
private fun AiSignalStrip(signals: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        signals.forEach { signal ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceAlt)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(InfoBlue)
                )
                Spacer(Modifier.width(8.dp))
                Text(signal, color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RecommendationRow(recommendation: AiRecommendation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(priorityColor(recommendation.priority).copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = priorityLabel(recommendation.priority),
                color = priorityColor(recommendation.priority),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(Modifier.weight(1f)) {
            Text(recommendation.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(recommendation.detail, color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun AssistantChip(title: String, selected: Boolean = false, onClick: () -> Unit) {
    val bg by animateColorAsState(
        targetValue = if (selected) ScanResult.ClassA.color.copy(alpha = 0.18f) else SurfaceAlt,
        label = "chipBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) ScanResult.ClassA.color.copy(alpha = 0.45f) else Border,
        label = "chipBorder"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) ScanResult.ClassA.color else TextPrimary,
        label = "chipText"
    )
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = title,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        maxLines = 1,
        softWrap = false
    )
}

@Composable
private fun AiSuggestionChip(
    title: String,
    icon: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(InfoBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 11.sp)
        }
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SendIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 2.2.dp.toPx()
        val centerX = size.width / 2f
        val topY = size.height * 0.2f
        val bottomY = size.height * 0.78f
        val wing = size.width * 0.22f

        // Stem
        drawLine(
            color = color,
            start = Offset(centerX, bottomY),
            end = Offset(centerX, topY),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Left diagonal
        drawLine(
            color = color,
            start = Offset(centerX - wing, topY + wing),
            end = Offset(centerX, topY),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Right diagonal
        drawLine(
            color = color,
            start = Offset(centerX + wing, topY + wing),
            end = Offset(centerX, topY),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ClearIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 1.8.dp.toPx()
        val inset = size.width * 0.26f
        drawLine(
            color = color,
            start = Offset(inset, inset),
            end = Offset(size.width - inset, size.height - inset),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width - inset, inset),
            end = Offset(inset, size.height - inset),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun AssistantBubble(message: AssistantMessage) {
    val isUser = message.sender == MessageSender.User
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .clip(bubbleShape)
                .background(if (isUser) InfoBlue.copy(alpha = 0.12f) else Surface)
                .border(1.dp, if (isUser) InfoBlue.copy(alpha = 0.25f) else Border, bubbleShape)
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isUser) InfoBlue else ScanResult.ClassA.color)
                )
                Text(
                    text = if (isUser) "You" else "Pleuro AI",
                    color = if (isUser) InfoBlue else ScanResult.ClassA.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(message.text, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun TrendWindowSelector(selectedDays: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(7, 14).forEach { days ->
            val selected = selectedDays == days
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onSelect(days) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) SurfaceAlt else Surface,
                    contentColor = if (selected) TextPrimary else TextMuted
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("${days}D")
            }
        }
    }
}

@Composable
private fun AdvancedInsightHero(insights: TrendInsights) {
    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                SectionLabel("Quality Score")
                Text(
                    text = insights.riskLevel,
                    color = riskColor(insights.riskLevel),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Reject trend is ${insights.trendDirection.lowercase()} by ${signedPercent(insights.rejectRateChange)}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            QualityScoreRing(score = insights.qualityScore)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniMetric(
                modifier = Modifier.weight(1f),
                label = "Scans",
                value = insights.scanCount.toString(),
                accent = InfoBlue
            )
            MiniMetric(
                modifier = Modifier.weight(1f),
                label = "Confidence",
                value = "${(insights.averageConfidence * 100).roundToInt()}%",
                accent = ScanResult.ClassA.color
            )
            MiniMetric(
                modifier = Modifier.weight(1f),
                label = "Reject",
                value = "%.1f%%".format(insights.rejectRate),
                accent = ScanResult.Reject.color
            )
        }
    }
}

@Composable
private fun ForecastPanel(forecast: HarvestForecast) {
    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                SectionLabel("Harvest Forecast")
                Text(
                    forecast.status,
                    color = forecastColor(forecast),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Tomorrow estimate based on recent scan volume and grade mix",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            StatusPill("${"%.0f".format(forecast.confidence * 100)}% confidence", forecastColor(forecast))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniMetric(
                modifier = Modifier.weight(1f),
                label = "Expected",
                value = forecast.expectedScansTomorrow.toString(),
                accent = InfoBlue
            )
            MiniMetric(
                modifier = Modifier.weight(1f),
                label = "Class A",
                value = forecast.expectedClassA.toString(),
                accent = ScanResult.ClassA.color
            )
            MiniMetric(
                modifier = Modifier.weight(1f),
                label = "Reject",
                value = forecast.expectedReject.toString(),
                accent = ScanResult.Reject.color
            )
        }
        Spacer(Modifier.height(6.dp))
        ForecastMixBar(forecast = forecast)
    }
}

@Composable
private fun PreHarvestReflectionPanel(reflection: PreHarvestReflection) {
    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                SectionLabel("Pre-Harvest Reflection")
                Text(
                    reflection.readinessLabel,
                    color = readinessColor(reflection.readinessScore),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(reflection.observation, color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
            }
            QualityScoreRing(score = reflection.readinessScore)
        }
        Spacer(Modifier.height(8.dp))
        Text("Recommended action", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(reflection.action, color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(6.dp))
        reflection.drivers.forEach { driver ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(readinessColor(reflection.readinessScore))
                )
                Spacer(Modifier.width(8.dp))
                Text(driver, color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ForecastMixBar(forecast: HarvestForecast) {
    val total = max(1, forecast.expectedClassA + forecast.expectedClassB + forecast.expectedReject)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(SurfaceAlt)
    ) {
        Box(
            modifier = Modifier
                .weight(forecastWeight(forecast.expectedClassA, total))
                .fillMaxSize()
                .background(ScanResult.ClassA.color)
        )
        Box(
            modifier = Modifier
                .weight(forecastWeight(forecast.expectedClassB, total))
                .fillMaxSize()
                .background(ScanResult.ClassB.color)
        )
        Box(
            modifier = Modifier
                .weight(forecastWeight(forecast.expectedReject, total))
                .fillMaxSize()
                .background(ScanResult.Reject.color)
        )
    }
}

@Composable
private fun InsightGrid(insights: TrendInsights) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InsightCard(
                modifier = Modifier.weight(1f),
                title = "Best Day",
                value = insights.bestDay?.label ?: "--",
                detail = insights.bestDay?.let { "${it.premiumPercent}% Class A" } ?: "No data",
                color = ScanResult.ClassA.color
            )
            InsightCard(
                modifier = Modifier.weight(1f),
                title = "Busiest",
                value = insights.busiestDay?.label ?: "--",
                detail = insights.busiestDay?.let { "${it.total} scans" } ?: "No data",
                color = InfoBlue
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InsightCard(
                modifier = Modifier.weight(1f),
                title = "Today",
                value = (insights.today?.total ?: 0).toString(),
                detail = "mobile scans",
                color = TextPrimary
            )
            InsightCard(
                modifier = Modifier.weight(1f),
                title = "Momentum",
                value = insights.trendDirection,
                detail = signedPercent(insights.rejectRateChange),
                color = riskColor(insights.riskLevel)
            )
        }
    }
}

@Composable
private fun GradeSummary(counts: ScanCounts) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ScanResult.entries.forEach { result ->
            GradeCard(
                modifier = Modifier.weight(1f),
                result = result,
                count = counts.countFor(result)
            )
        }
    }
}

@Composable
private fun GradeCard(modifier: Modifier, result: ScanResult, count: Int) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(result.softColor)
            .border(1.dp, result.color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(result.displayName, color = result.color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(
            text = count.toString(),
            color = result.color,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
        Text("bags - ${result.description}", color = TextMuted, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun ActiveAlert(latest: ScanRecord?) {
    val danger = latest?.result == ScanResult.Reject
    val color = if (danger) ScanResult.Reject.color else ScanResult.ClassA.color
    val title = if (danger) "Reject detected - Bag #${latest.bagNumber}" else "No active alerts"
    val body = if (danger) "Flag this bag for removal from the batch." else "All scanned bags are within acceptable quality range."
    Column {
        SectionLabel("Active Alert")
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.08f))
                .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (danger) "!" else "OK", color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(body, color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun LatestScanDetail(scan: ScanRecord, onVerify: (ScanResult) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        QrCode(payload = scan.qrPayload, modifier = Modifier.size(116.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bag ${scan.bagNumber}", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                    Text("${scan.batchId} | Shelf ${scan.shelfCode} | ${scan.timeLabel}", color = TextMuted, fontSize = 11.sp)
                }
                ResultBadge(scan.finalResult, if (scan.verified) "Verified ${scan.finalResult.shortName}" else scan.finalResult.displayName)
            }
            ConfidenceBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                result = scan.finalResult,
                confidence = scan.confidence
            )
            Text("AI scan explanation", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(scan.aiExplanation, color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScanResult.entries.forEach { result ->
                    AssistantChip(
                        title = "Verify ${result.shortName}",
                        onClick = { onVerify(result) }
                    )
                }
            }
        }
    }
    Text(scan.qrPayload, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun QrCode(payload: String, modifier: Modifier = Modifier) {
    val matrix = remember(payload) {
        QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 29, 29)
    }
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        val cell = size.minDimension / matrix.width
        for (x in 0 until matrix.width) {
            for (y in 0 until matrix.height) {
                if (matrix[x, y]) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x * cell, y * cell),
                        size = Size(cell, cell)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrintableQrLabel(label: BagLabel) {
    Column(
        modifier = Modifier
            .width(142.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("PleuroTech", color = Color(0xFF1D2D22), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        QrCode(payload = label.qrPayload, modifier = Modifier.size(104.dp))
        Text(label.batchId, color = Color(0xFF607067), fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 1)
        Text(
            "Bag ${label.bagNumber.toString().padStart(3, '0')} | Shelf ${label.shelfCode}",
            color = Color(0xFF1D2D22),
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun ReportPreviewPanel(report: ScanReport) {
    DataCard {
        SectionLabel("Offline Quality Report")
        Text(report.title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(report.summary, color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
        report.rows.forEach { row ->
            Text(row, color = TextPrimary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ScanRows(
    scans: List<ScanRecord>,
    compact: Boolean,
    onVerify: ((ScanRecord, ScanResult) -> Unit)? = null
) {
    if (scans.isEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            text = "No scans yet. Add a mock scan or seed demo data.",
            color = TextMuted,
            fontSize = 13.sp
        )
        return
    }
    scans.forEachIndexed { index, scan ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!compact) Text("#${scan.id}", modifier = Modifier.width(42.dp), color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            Text(
                "Bag ${scan.bagNumber}\n${scan.shelfCode}",
                modifier = Modifier.widthIn(min = 58.dp),
                color = if (compact) TextMuted else TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            ResultBadge(result = scan.finalResult, text = if (scan.verified) "V-${scan.finalResult.shortName}" else scan.finalResult.displayName)
            ConfidenceBar(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp),
                result = scan.finalResult,
                confidence = scan.confidence
            )
            Text(
                text = if (compact) scan.timeLabel else "${scan.dateLabel}\n${scan.timeLabel}",
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                maxLines = if (compact) 1 else 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (!compact) {
            Text(scan.aiExplanation, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QrCode(payload = scan.qrPayload, modifier = Modifier.size(72.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(scan.qrPayload, color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (onVerify != null) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ScanResult.entries.forEach { result ->
                                AssistantChip(title = result.displayName, onClick = { onVerify(scan, result) })
                            }
                        }
                    }
                }
            }
        }
        if (index != scans.lastIndex) HorizontalDivider(color = Border)
    }
}

@Composable
private fun DailyTrendRow(day: DailyTrend) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(day.label, modifier = Modifier.width(44.dp), color = TextPrimary, fontWeight = FontWeight.Medium)
        Text(day.classA.toString(), modifier = Modifier.weight(1f), color = ScanResult.ClassA.color, fontFamily = FontFamily.Monospace)
        Text(day.classB.toString(), modifier = Modifier.weight(1f), color = ScanResult.ClassB.color, fontFamily = FontFamily.Monospace)
        Text(day.reject.toString(), modifier = Modifier.weight(1f), color = ScanResult.Reject.color, fontFamily = FontFamily.Monospace)
        ResultBadge(result = if (day.rejectPercent > 10) ScanResult.Reject else ScanResult.ClassA, text = "${day.rejectPercent}%")
    }
}

@Composable
private fun ResultBadge(result: ScanResult, text: String = result.displayName) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(result.softColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        text = text,
        color = result.color,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1
    )
}

@Composable
private fun ConfidenceBar(modifier: Modifier, result: ScanResult, confidence: Float) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(SurfaceAlt)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(confidence.coerceIn(0f, 1f))
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(result.color)
        )
    }
}

@Composable
private fun RateCard(
    modifier: Modifier,
    label: String,
    value: Float,
    color: Color,
    subtitle: String
) {
    DataCard(modifier = modifier) {
        Text(label.uppercase(), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("%.1f%%".format(value), color = color, fontSize = 30.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
        ConfidenceBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            result = if (color == ScanResult.Reject.color) ScanResult.Reject else ScanResult.ClassA,
            confidence = value / 100f
        )
        Text(subtitle, color = TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun MiniMetric(
    modifier: Modifier,
    label: String,
    value: String,
    accent: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceAlt)
            .padding(10.dp)
    ) {
        Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = accent, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun InsightCard(
    modifier: Modifier,
    title: String,
    value: String,
    detail: String,
    color: Color
) {
    DataCard(modifier = modifier) {
        Text(title.uppercase(), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Text(detail, color = TextMuted, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun QualityScoreRing(score: Float) {
    val trackColor = SurfaceAlt
    val progressColor = qualityColor(score)
    Box(
        modifier = Modifier.size(98.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * (score / 100f).coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(score.roundToInt().toString(), color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("score", color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    subtitle: String,
    showLegend: Boolean = true,
    content: @Composable () -> Unit
) {
    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }
        Spacer(Modifier.height(12.dp))
        if (showLegend) {
            ChartLegend()
            Spacer(Modifier.height(8.dp))
        }
        content()
    }
}

@Composable
private fun ChartLegend() {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        ScanResult.entries.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(it.color)
                )
                Spacer(Modifier.width(6.dp))
                Text(it.displayName, color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun TrendStackedBars(trend: List<DailyTrend>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val maxTotal = max(1, trend.maxOfOrNull { it.total } ?: 1)
        val gap = 10.dp.toPx()
        val barWidth = (size.width - gap * (trend.size - 1)) / trend.size
        trend.forEachIndexed { index, day ->
            var bottom = size.height
            val x = index * (barWidth + gap)
            listOf(
                ScanResult.ClassA to day.classA,
                ScanResult.ClassB to day.classB,
                ScanResult.Reject to day.reject
            ).forEach { (result, count) ->
                val h = size.height * count / maxTotal
                bottom -= h
                drawRoundRect(
                    color = result.color,
                    topLeft = Offset(x, bottom),
                    size = Size(barWidth, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun DailyTotalsBars(trend: List<DailyTrend>) {
    val barColor = InfoBlue.copy(alpha = 0.45f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val maxTotal = max(1, trend.maxOfOrNull { it.total } ?: 1)
        val gap = 10.dp.toPx()
        val barWidth = (size.width - gap * (trend.size - 1)) / trend.size
        trend.forEachIndexed { index, day ->
            val h = size.height * day.total / maxTotal
            drawRoundRect(
                color = barColor,
                topLeft = Offset(index * (barWidth + gap), size.height - h),
                size = Size(barWidth, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}

@Composable
private fun RejectRateLineChart(trend: List<DailyTrend>) {
    val pointSurface = Surface
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val values = trend.map { it.rejectPercent.toFloat() }
        if (values.isEmpty()) return@Canvas
        val maxValue = max(20f, values.maxOrNull() ?: 20f)
        val points = values.mapIndexed { index, value ->
            val x = if (values.size == 1) size.width / 2f else index * size.width / values.lastIndex
            val y = size.height - (value / maxValue).coerceIn(0f, 1f) * size.height
            Offset(x, y)
        }
        val warningY = size.height - (10f / maxValue).coerceIn(0f, 1f) * size.height
        drawLine(
            color = ScanResult.ClassB.color.copy(alpha = 0.45f),
            start = Offset(0f, warningY),
            end = Offset(size.width, warningY),
            strokeWidth = 1.dp.toPx()
        )
        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(
                path = path,
                color = ScanResult.Reject.color,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        points.forEach { point ->
            drawCircle(color = pointSurface, radius = 6.dp.toPx(), center = point)
            drawCircle(color = ScanResult.Reject.color, radius = 4.dp.toPx(), center = point)
        }
    }
}

@Composable
private fun DistributionDonut(counts: ScanCounts) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val total = max(1, counts.total)
        val stroke = 28.dp.toPx()
        val diameter = size.minDimension - stroke
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        var start = -90f
        ScanResult.entries.forEach { result ->
            val sweep = 360f * counts.countFor(result) / total
            drawArc(
                color = result.color,
                startAngle = start,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
            start += sweep
        }
    }
}

@Composable
private fun DataCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        content()
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = TextMuted,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun PageTitle(title: String, subtitle: String) {
    Column {
        Text(title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = TextMuted, fontSize = 12.sp)
    }
}

@Composable
private fun StatusPill(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

private fun signedPercent(value: Float): String {
    val sign = if (value > 0f) "+" else ""
    return "$sign${"%.1f".format(value)}%"
}

private fun AppTab.iconLabel(): String = when (this) {
    AppTab.Dashboard -> "H"
    AppTab.Scan -> "C"
    AppTab.Labels -> "B"
    AppTab.History -> "L"
    AppTab.Analytics -> "A"
    AppTab.Assistant -> "AI"
    AppTab.Settings -> "S"
}

private fun riskColor(level: String): Color = when (level) {
    "High" -> ScanResult.Reject.color
    "Watch" -> ScanResult.ClassB.color
    else -> ScanResult.ClassA.color
}

private fun priorityColor(priority: AssistantPriority): Color = when (priority) {
    AssistantPriority.Critical -> ScanResult.Reject.color
    AssistantPriority.Watch -> ScanResult.ClassB.color
    AssistantPriority.Stable -> ScanResult.ClassA.color
}

private fun priorityLabel(priority: AssistantPriority): String = when (priority) {
    AssistantPriority.Critical -> "!!"
    AssistantPriority.Watch -> "!"
    AssistantPriority.Stable -> "OK"
}

@Composable
private fun forecastColor(forecast: HarvestForecast): Color = when {
    forecast.expectedScansTomorrow == 0 -> TextMuted
    forecast.projectedRejectRate >= 18f -> ScanResult.Reject.color
    forecast.projectedRejectRate >= 10f -> ScanResult.ClassB.color
    else -> ScanResult.ClassA.color
}

@Composable
private fun readinessColor(score: Float): Color = when {
    score >= 85f -> ScanResult.ClassA.color
    score >= 70f -> InfoBlue
    score >= 55f -> ScanResult.ClassB.color
    else -> ScanResult.Reject.color
}

private fun forecastWeight(value: Int, total: Int): Float {
    return max(0.0001f, value.coerceAtLeast(0).toFloat() / total)
}

@Composable
private fun shelfStatusColor(shelf: ShelfSummary): Color = when (shelf.status) {
    "Isolate" -> ScanResult.Reject.color
    "Watch" -> ScanResult.ClassB.color
    "Unscanned" -> TextMuted
    else -> ScanResult.ClassA.color
}

private fun qualityColor(score: Float): Color = when {
    score >= 85f -> ScanResult.ClassA.color
    score >= 70f -> ScanResult.ClassB.color
    else -> ScanResult.Reject.color
}
//pleurotech team copyright original code don't steal