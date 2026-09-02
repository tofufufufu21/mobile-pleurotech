package com.example.pleurotech.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
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
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.layout.ContentScale
import java.util.Calendar
import com.example.pleurotech.data.auth.UserProfile
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.pleurotech.ml.DiseaseAlarmManager
import com.example.pleurotech.ml.YoloV8Detector
import com.example.pleurotech.ml.InferenceSummary
import com.example.pleurotech.ml.MushroomDetection
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
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
    Records("Records"),
    Assistant("AI"),
    Labels("Labels"),
    History("History"),
    Analytics("Analytics"),
    Settings("Settings")
}

enum class RecordsSubTab(val title: String, val icon: String) {
    History("Scan Logs", "📋"),
    Analytics("Analytics", "📊"),
    Labels("QR Labels", "🏷️")
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
                var recordsSubTab by remember { mutableStateOf(RecordsSubTab.History) }

                val navTabs = remember(isOwner) {
                    if (isOwner) {
                        listOf(AppTab.Dashboard, AppTab.Scan, AppTab.Records, AppTab.Assistant)
                    } else {
                        listOf(AppTab.Scan, AppTab.Records)
                    }
                }

                androidx.compose.runtime.LaunchedEffect(currentUser) {
                    if (!isOwner && selectedTab != AppTab.Scan && selectedTab != AppTab.Records) {
                        selectedTab = AppTab.Scan
                    }
                }

                Scaffold(
                    containerColor = AppBackground,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar(containerColor = Surface) {
                            navTabs.forEach { tab ->
                                val isSelected = selectedTab == tab || (tab == AppTab.Records && (selectedTab == AppTab.History || selectedTab == AppTab.Analytics || selectedTab == AppTab.Labels))
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTab = tab
                                        if (tab == AppTab.Records) {
                                            recordsSubTab = RecordsSubTab.History
                                        }
                                    },
                                    icon = { Text(tab.iconLabel(), fontSize = 18.sp) },
                                    label = { Text(tab.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, maxLines = 1, softWrap = false) },
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
                            onSelectTab = { tab ->
                                when (tab) {
                                    AppTab.History -> {
                                        selectedTab = AppTab.Records
                                        recordsSubTab = RecordsSubTab.History
                                    }
                                    AppTab.Analytics -> {
                                        selectedTab = AppTab.Records
                                        recordsSubTab = RecordsSubTab.Analytics
                                    }
                                    AppTab.Labels -> {
                                        selectedTab = AppTab.Records
                                        recordsSubTab = RecordsSubTab.Labels
                                    }
                                    else -> selectedTab = tab
                                }
                            },
                            onOpenSettings = { selectedTab = AppTab.Settings },
                            repository = repository
                        )
                        when (selectedTab) {
                            AppTab.Dashboard -> DashboardScreen(
                                repository = repository,
                                onOpenHistory = {
                                    selectedTab = AppTab.Records
                                    recordsSubTab = RecordsSubTab.History
                                },
                                onOpenScan = { selectedTab = AppTab.Scan }
                            )
                            AppTab.Scan -> ScanScreen(
                                repository = repository,
                                onOpenAssistant = { selectedTab = AppTab.Assistant }
                            )
                            AppTab.Records, AppTab.History, AppTab.Analytics, AppTab.Labels -> RecordsScreen(
                                repository = repository,
                                subTab = recordsSubTab,
                                onSubTabChanged = { recordsSubTab = it },
                                onExport = {
                                    val lines = repository.exportCsv().lineSequence().count()
                                    scope.launch { snackbarHostState.showSnackbar("CSV ready with $lines lines") }
                                },
                                onClear = {
                                    repository.clear(context)
                                    scope.launch { snackbarHostState.showSnackbar("All scans and batch data reset fresh.") }
                                },
                                onPrintLabels = { labels, batchName ->
                                    QrLabelPrinter(context).print(labels, batchName)
                                    scope.launch { snackbarHostState.showSnackbar("Printing ${labels.size} QR labels…") }
                                }
                            )
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
                    if (isSyncing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Text("Syncing with Cloud...", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("Sync Now (Push & Pull)", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
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
                    if (isTesting) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = TextPrimary,
                                strokeWidth = 2.dp
                            )
                            Text("Testing Connection...", color = TextPrimary)
                        }
                    } else {
                        Text("Test Cloud Connection", color = TextPrimary)
                    }
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
    onOpenScan: () -> Unit
) {
    val counts = repository.counts()
    val recent = repository.recentScans()
    val latest = repository.latestScan
    val trend = repository.trend()
    val alerts = repository.contaminationAlerts()

    var inspectionModalScan by remember { mutableStateOf<ScanRecord?>(null) }
    var selectedShelfForModal by remember { mutableStateOf<ShelfSummary?>(null) }

    if (inspectionModalScan != null) {
        InspectionDetailModal(
            scan = inspectionModalScan!!,
            onDismiss = { inspectionModalScan = null }
        )
    }

    if (selectedShelfForModal != null) {
        ShelfDetailModal(
            shelf = selectedShelfForModal!!,
            scans = repository.scansForShelf(selectedShelfForModal!!.shelfCode),
            onDismiss = { selectedShelfForModal = null }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WelcomeUserBanner(
                user = repository.authManager?.getCurrentUser(),
                batchName = repository.activeBatch.name
            )
        }
        item {
            BatchStatusPanel(repository = repository)
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
            ShelfMapPanel(
                shelves = repository.shelfMap(),
                onSelectShelf = { selectedShelfForModal = it }
            )
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
                ScanRows(
                    scans = recent,
                    compact = true,
                    onSelectScan = { inspectionModalScan = it }
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenScan,
                    colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color)
                ) {
                    Text("📷 Start Real Scan")
                }
                OutlinedButton(onClick = onOpenHistory) {
                    Text("View History")
                }
            }
        }
    }
}

@Composable
private fun WelcomeUserBanner(
    user: UserProfile?,
    batchName: String
) {
    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Welcome back"
        }
    }
    val displayName = user?.name ?: "Farm Operator"
    val role = user?.role ?: "Farm Owner"
    val initials = remember(displayName) {
        displayName.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifEmpty { "PT" }
    }

    DataCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ScanResult.ClassA.color.copy(alpha = 0.15f))
                        .border(1.5.dp, ScanResult.ClassA.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = ScanResult.ClassA.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "$greeting, $displayName 👋",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "$role · Active Batch: $batchName",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

enum class QrType {
    Identification, // QR 1 (Step 1)
    Confirmation   // QR 2 (Step 3)
}

data class ParsedQrBag(
    val batchId: String,
    val bagNumber: Int,
    val shelfCode: String,
    val qrType: QrType = QrType.Identification
)

fun parseQrPayload(payload: String, repository: PleuroTechRepository): ParsedQrBag? {
    return try {
        val trimmed = payload.trim()
        val bagNum = when {
            trimmed.contains("/bag/") -> trimmed.substringAfter("/bag/").substringBefore("/").toIntOrNull()
            trimmed.contains("/scan/") -> trimmed.substringAfter("/scan/").substringBefore("/").toIntOrNull()
            trimmed.startsWith("BAG-", ignoreCase = true) -> trimmed.substringAfter("-").substringBefore("-").substringBefore("/").toIntOrNull()
            else -> trimmed.filter { it.isDigit() }.toIntOrNull()
        } ?: return null

        val batchId = if (trimmed.contains("://") && trimmed.contains("/bag/")) {
            trimmed.substringAfter("://").substringBefore("/bag/")
        } else {
            repository.activeBatch.id
        }

        val qrType = if (trimmed.contains("/confirm", ignoreCase = true) ||
            trimmed.contains(":confirm", ignoreCase = true) ||
            trimmed.contains("-confirm", ignoreCase = true) ||
            trimmed.contains("confirm", ignoreCase = true)
        ) {
            QrType.Confirmation
        } else {
            QrType.Identification
        }

        ParsedQrBag(
            batchId = batchId,
            bagNumber = bagNum,
            shelfCode = repository.shelfCodeForBag(bagNum),
            qrType = qrType
        )
    } catch (_: Exception) {
        null
    }
}

fun decodeQrFromBitmap(bitmap: Bitmap): String? {
    val decodeHints = mapOf(
        DecodeHintType.TRY_HARDER to true,
        DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)
    )
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val source = RGBLuminanceSource(width, height, pixels)

    // Pass 1: HybridBinarizer (standard high-contrast and normal lighting)
    try {
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        return QRCodeReader().decode(binaryBitmap, decodeHints).text
    } catch (_: Exception) {}

    // Pass 2: GlobalHistogramBinarizer (far superior for dark shadows, reflections, and greenhouse lighting)
    try {
        val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
        return QRCodeReader().decode(binaryBitmap, decodeHints).text
    } catch (_: Exception) {}

    return null
}

fun saveScanPhoto(context: Context, bitmap: Bitmap, bagNumber: Int): String? {
    return try {
        val dir = File(context.filesDir, "scans")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "scan_bag_${bagNumber}_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

enum class ScanStage {
    ScanQr,              // Step 1: Scan Bag QR Code first
    CaptureMushroom,     // Step 2: Photograph the Mushroom
    ReviewConfirmation   // Step 3: Retake or Confirm
}

enum class StepState {
    Active,
    Completed,
    Upcoming
}

enum class CameraIntent {
    ScanQr1,
    CaptureMushroom,
    ScanQr2Confirm
}

@Composable
private fun ScanProcessTracker(
    scanStage: ScanStage,
    targetBagNumber: Int,
    targetShelfCode: String,
    isQr2Confirmed: Boolean = false
) {
    val animatedProgress by animateFloatAsState(
        targetValue = when (scanStage) {
            ScanStage.ScanQr -> 0.33f
            ScanStage.CaptureMushroom -> 0.66f
            ScanStage.ReviewConfirmation -> if (isQr2Confirmed) 1.0f else 0.85f
        },
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "pipeline_progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131920)),
        border = BorderStroke(1.dp, Color(0xFF222C38))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Telemetry Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (scanStage == ScanStage.ReviewConfirmation && isQr2Confirmed) Color(0xFF00E676)
                                else if (scanStage == ScanStage.ReviewConfirmation) Color(0xFF00B0FF)
                                else Color(0xFF00E676)
                            )
                    )
                    Text(
                        text = when (scanStage) {
                            ScanStage.ScanQr -> "STEP 1 OF 3 · SCAN QR 1 (ID)"
                            ScanStage.CaptureMushroom -> "STEP 2 OF 3 · SPECIMEN CAPTURE"
                            ScanStage.ReviewConfirmation -> if (isQr2Confirmed) "STEP 3 OF 3 · LOCATION CONFIRMED" else "STEP 3 OF 3 · SCAN QR 2 TO CONFIRM"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (scanStage == ScanStage.ReviewConfirmation && !isQr2Confirmed)
                                Color(0xFF00B0FF).copy(alpha = 0.15f)
                            else
                                Color(0xFF00E676).copy(alpha = 0.12f)
                        )
                        .border(
                            1.dp,
                            if (scanStage == ScanStage.ReviewConfirmation && !isQr2Confirmed)
                                Color(0xFF00B0FF).copy(alpha = 0.4f)
                            else
                                Color(0xFF00E676).copy(alpha = 0.3f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when (scanStage) {
                            ScanStage.ScanQr -> "33% · READY"
                            ScanStage.CaptureMushroom -> "66% · LOCKED"
                            ScanStage.ReviewConfirmation -> if (isQr2Confirmed) "100% · VERIFIED" else "85% · SCAN QR 2"
                        },
                        color = if (scanStage == ScanStage.ReviewConfirmation && !isQr2Confirmed) Color(0xFF00B0FF) else Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // High-Tech Animated Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1E2833))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00E676), Color(0xFF00B0FF))
                            )
                        )
                )
            }

            // Milestone Nodes Connected Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MilestoneNode(
                    step = "1",
                    title = "QR 1 (ID)",
                    status = if (scanStage == ScanStage.ScanQr) "Active" else "Done",
                    isActive = scanStage == ScanStage.ScanQr,
                    isDone = scanStage != ScanStage.ScanQr
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(1.dp)
                        .background(if (scanStage != ScanStage.ScanQr) Color(0xFF00E676).copy(alpha = 0.6f) else Color(0xFF263238))
                )

                MilestoneNode(
                    step = "2",
                    title = "Specimen",
                    status = when (scanStage) {
                        ScanStage.ScanQr -> "Pending"
                        ScanStage.CaptureMushroom -> "Bag #$targetBagNumber"
                        ScanStage.ReviewConfirmation -> "Done"
                    },
                    isActive = scanStage == ScanStage.CaptureMushroom,
                    isDone = scanStage == ScanStage.ReviewConfirmation
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(1.dp)
                        .background(
                            if (scanStage == ScanStage.ReviewConfirmation && isQr2Confirmed) Color(0xFF00E676).copy(alpha = 0.6f)
                            else if (scanStage == ScanStage.ReviewConfirmation) Color(0xFF00B0FF).copy(alpha = 0.6f)
                            else Color(0xFF263238)
                        )
                )

                MilestoneNode(
                    step = "3",
                    title = "QR 2 Confirm",
                    status = when {
                        scanStage != ScanStage.ReviewConfirmation -> "Pending"
                        isQr2Confirmed -> "Verified"
                        else -> "Scan 2"
                    },
                    isActive = scanStage == ScanStage.ReviewConfirmation && !isQr2Confirmed,
                    isDone = scanStage == ScanStage.ReviewConfirmation && isQr2Confirmed
                )
            }
        }
    }
}

@Composable
private fun MilestoneNode(
    step: String,
    title: String,
    status: String,
    isActive: Boolean,
    isDone: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> Color(0xFF00E676)
                        isActive -> Color(0xFF00E676).copy(alpha = 0.2f)
                        else -> Color(0xFF1E2833)
                    }
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isDone || isActive -> Color(0xFF00E676)
                        else -> Color(0xFF37474F)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isDone) "✓" else step,
                color = if (isDone) Color.Black else if (isActive) Color(0xFF00E676) else Color(0xFF90A4AE),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }

        Column {
            Text(
                text = title,
                color = if (isActive || isDone) Color.White else Color(0xFF78909C),
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            )
            Text(
                text = status,
                color = if (isActive || isDone) Color(0xFF00E676) else Color(0xFF546E7A),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun MobileScanPanel(
    detector: YoloV8Detector,
    scanStage: ScanStage,
    inferenceSummary: InferenceSummary?,
    capturedBitmap: Bitmap?,
    isScanning: Boolean,
    targetBagNumber: Int,
    targetShelfCode: String,
    isQr2Confirmed: Boolean,
    qrConflictDetected: Boolean,
    conflictingBagNumber: Int?,
    qrErrorMessage: String?,
    onScanQrClick: () -> Unit,
    onTakeMushroomPhotoClick: () -> Unit,
    onScanQr2Click: () -> Unit,
    onRetakePhoto: () -> Unit,
    onConfirmScan: () -> Unit,
    onRescanQr: () -> Unit,
    onChangeBagClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Sleek Modern Process Tracker with Linear Progress Bar
        ScanProcessTracker(
            scanStage = scanStage,
            targetBagNumber = targetBagNumber,
            targetShelfCode = targetShelfCode,
            isQr2Confirmed = isQr2Confirmed
        )

        // 2. High-Tech Precision Viewfinder Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.30f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F1318), Color(0xFF151C24), Color(0xFF0B0E12))
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = if (qrConflictDetected || inferenceSummary?.hasContamination == true) ScanResult.Reject.color else Color(0xFF263342),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (capturedBitmap != null) {
                    val img = capturedBitmap.asImageBitmap()
                    drawImage(
                        image = img,
                        dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                    )
                } else {
                    // Precision grid lines
                    val gridColor = Color.White.copy(alpha = 0.04f)
                    for (i in 1..5) {
                        val y = (size.height * i) / 6f
                        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
                    }
                    for (i in 1..7) {
                        val x = size.width * i / 8f
                        drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.dp.toPx())
                    }
                }

                // High-Tech Viewfinder Corner Brackets
                val bracketColor = when {
                    qrConflictDetected || inferenceSummary?.hasContamination == true -> ScanResult.Reject.color
                    scanStage != ScanStage.ScanQr -> Color(0xFF00E676)
                    else -> Color(0xFF00B0FF)
                }
                val bLen = 22.dp.toPx()
                val bStroke = 3.dp.toPx()
                val pad = 16.dp.toPx()

                // Top-Left
                drawLine(bracketColor, Offset(pad, pad), Offset(pad + bLen, pad), strokeWidth = bStroke, cap = StrokeCap.Round)
                drawLine(bracketColor, Offset(pad, pad), Offset(pad, pad + bLen), strokeWidth = bStroke, cap = StrokeCap.Round)

                // Top-Right
                drawLine(bracketColor, Offset(size.width - pad, pad), Offset(size.width - pad - bLen, pad), strokeWidth = bStroke, cap = StrokeCap.Round)
                drawLine(bracketColor, Offset(size.width - pad, pad), Offset(size.width - pad, pad + bLen), strokeWidth = bStroke, cap = StrokeCap.Round)

                // Bottom-Left
                drawLine(bracketColor, Offset(pad, size.height - pad), Offset(pad + bLen, size.height - pad), strokeWidth = bStroke, cap = StrokeCap.Round)
                drawLine(bracketColor, Offset(pad, size.height - pad), Offset(pad, size.height - pad - bLen), strokeWidth = bStroke, cap = StrokeCap.Round)

                // Bottom-Right
                drawLine(bracketColor, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - bLen, size.height - pad), strokeWidth = bStroke, cap = StrokeCap.Round)
                drawLine(bracketColor, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - bLen), strokeWidth = bStroke, cap = StrokeCap.Round)

                // Detections: Bounding boxes and tags
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 10.dp.toPx()
                    isAntiAlias = true
                    isFakeBoldText = true
                }
                inferenceSummary?.detections?.forEach { det ->
                    val strokeColor = Color(det.category.colorHex)
                    val left = det.box.left * size.width
                    val top = det.box.top * size.height
                    val right = det.box.right * size.width
                    val bottom = det.box.bottom * size.height
                    val boxWidth = max(20f, right - left)
                    val boxHeight = max(20f, bottom - top)

                    drawRoundRect(
                        color = strokeColor,
                        topLeft = Offset(left, top),
                        size = Size(boxWidth, boxHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    drawRoundRect(
                        color = strokeColor.copy(alpha = 0.18f),
                        topLeft = Offset(left, top),
                        size = Size(boxWidth, boxHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                    )

                    val tagText = "${det.label.replace('_', ' ')} ${(det.confidence * 100).toInt()}%"
                    val tagWidth = textPaint.measureText(tagText) + 12.dp.toPx()
                    val tagHeight = 16.dp.toPx()
                    val tagTop = max(0f, top - tagHeight - 2.dp.toPx())

                    drawRoundRect(
                        color = strokeColor,
                        topLeft = Offset(left, tagTop),
                        size = Size(tagWidth, tagHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        tagText,
                        left + 6.dp.toPx(),
                        tagTop + tagHeight - 4.dp.toPx(),
                        textPaint
                    )
                }
            }

            // Viewport HUD Overlay Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.82f))
                        .border(
                            1.dp,
                            if (scanStage != ScanStage.ScanQr) Color(0xFF00E676) else Color(0xFF00B0FF),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = when {
                                qrConflictDetected -> "⚠️ MISMATCH · BAG #$conflictingBagNumber"
                                scanStage == ScanStage.ScanQr -> "🏷️ STEP 1: SCAN QR 1 (ID)"
                                scanStage == ScanStage.CaptureMushroom -> "🔒 LOCKED · BAG #$targetBagNumber · SHELF $targetShelfCode"
                                scanStage == ScanStage.ReviewConfirmation && isQr2Confirmed -> "🔒 LOCATION VERIFIED · BAG #$targetBagNumber"
                                else -> "🔒 AWAITING QR 2 · BAG #$targetBagNumber"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        if (scanStage != ScanStage.ScanQr) {
                            Text(
                                text = if (scanStage == ScanStage.ReviewConfirmation && !isQr2Confirmed) "AWAITING" else "VERIFIED",
                                color = if (scanStage == ScanStage.ReviewConfirmation && !isQr2Confirmed) Color(0xFF00B0FF) else Color(0xFF00E676),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                if (inferenceSummary != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.82f))
                            .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "⚡ ${inferenceSummary.inferenceTimeMs}ms · YOLOv8s",
                            color = Color(0xFF00E676),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Viewport Center Guidance
            if (capturedBitmap == null && !isScanning) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (scanStage) {
                        ScanStage.ScanQr -> {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF00B0FF).copy(alpha = 0.12f))
                                    .border(1.dp, Color(0xFF00B0FF).copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏷️", fontSize = 28.sp)
                            }
                            Text(
                                text = "ALIGN BAG COLLAR QR CODE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Scan collar barcode first to confirm grow bag & shelf placement.",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                        ScanStage.CaptureMushroom -> {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF00E676).copy(alpha = 0.12f))
                                    .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🍄", fontSize = 28.sp)
                            }
                            Text(
                                text = "FRAME OYSTER MUSHROOM CLUSTER",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Target locked to Bag #$targetBagNumber on Shelf $targetShelfCode.\nAnti-conflict QR cross-validation active.",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                        ScanStage.ReviewConfirmation -> {}
                    }
                }
            }

            // Bottom Detection Result Overlay (during review)
            if (scanStage == ScanStage.ReviewConfirmation && inferenceSummary != null) {
                val count = inferenceSummary.detections.size
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.82f))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = if (count > 1) "$count Mushrooms Detected" else inferenceSummary.primaryLabel.replace("_", " "),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            val subtitle = if (count > 1) {
                                inferenceSummary.detections.groupBy { it.label.replace("_", " ") }
                                    .entries.joinToString(" · ") { "${it.value.size} ${it.key}" }
                            } else if (count == 1) {
                                "Grade: ${inferenceSummary.primaryGrade.name} · Confidence: ${(inferenceSummary.primaryConfidence * 100).toInt()}%"
                            } else {
                                "No mushroom detected — try adjusting angle or distance"
                            }
                            Text(
                                text = subtitle,
                                color = if (inferenceSummary.hasContamination) ScanResult.Reject.color else if (count > 0) ScanResult.ClassA.color else TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        StatusPill(
                            label = if (inferenceSummary.hasContamination) "⚠️ Contaminated" else if (count > 0) inferenceSummary.primaryGrade.name else "No Match",
                            color = if (inferenceSummary.hasContamination) ScanResult.Reject.color else if (count > 0) inferenceSummary.primaryGrade.color else TextMuted
                        )
                    }
                }
            }

            // Frosted Loading Buffer Overlay
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.82f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(46.dp),
                            color = Color(0xFF00E676),
                            strokeWidth = 3.5.dp
                        )
                        Text(
                            text = if (scanStage == ScanStage.ScanQr) "DECODING BAG QR CODE..." else "RUNNING YOLOV8 INFERENCE...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (scanStage == ScanStage.ScanQr) "Validating collar telemetry & shelf placement..." else "Executing multi-scale detection & anti-conflict check...",
                            color = Color(0xFF90A4AE),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Anti-conflict & Status Alerts
        if (qrErrorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1414)),
                border = BorderStroke(1.dp, ScanResult.Reject.color.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", fontSize = 18.sp)
                    Text(qrErrorMessage, color = Color(0xFFFF8A80), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        if (qrConflictDetected && conflictingBagNumber != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF281113)),
                border = BorderStroke(1.5.dp, Color(0xFFFF5252))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", fontSize = 26.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (scanStage == ScanStage.ReviewConfirmation) "QR 2 MISMATCH CONFLICT!" else "TELEMETRY CONFLICT DETECTED",
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (scanStage == ScanStage.ReviewConfirmation) {
                                "Target is Bag #$targetBagNumber (Shelf $targetShelfCode), but scanned Confirmation QR 2 belongs to Bag #$conflictingBagNumber!\nPlease scan the 2nd QR on Bag #$targetBagNumber to verify this mushroom."
                            } else {
                                "Locked target is Bag #$targetBagNumber (Shelf $targetShelfCode), but photo contains collar QR for Bag #$conflictingBagNumber!\nPlease Retake Photo to ensure you capture the correct specimen."
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        } else if (scanStage == ScanStage.CaptureMushroom) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F17)),
                border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Column {
                        Text(
                            text = "Bag #$targetBagNumber (Shelf $targetShelfCode) Verified",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "QR 1 locked. Please photograph the oyster mushroom cluster.",
                            color = Color(0xFF81C784),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else if (scanStage == ScanStage.ReviewConfirmation) {
            if (isQr2Confirmed) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F17)),
                    border = BorderStroke(1.dp, Color(0xFF00E676))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column {
                            Text(
                                text = "QR 2 Verified · Location Confirmed",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Physical match verified for Bag #$targetBagNumber (Shelf $targetShelfCode). Ready to save.",
                                color = Color(0xFF81C784),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101C24)),
                    border = BorderStroke(1.dp, Color(0xFF00B0FF).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00B0FF).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFF00B0FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2", color = Color(0xFF00B0FF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Column {
                            Text(
                                text = "Step 3: Scan QR 2 to Verify Bag #$targetBagNumber",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Scan QR 2 (Confirmation) on Bag #$targetBagNumber (Shelf $targetShelfCode) to verify this mushroom.",
                                color = Color(0xFF90CAF9),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Controls by Stage
        when (scanStage) {
            ScanStage.ScanQr -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        onClick = onScanQrClick,
                        enabled = !isScanning,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📷", fontSize = 16.sp)
                            Text(
                                "SCAN QR 1 (BAG ID)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.8.sp,
                                color = Color.White
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141920)),
                        border = BorderStroke(1.dp, Color(0xFF222C38))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🎯", fontSize = 14.sp)
                                Text(
                                    text = "Target: Bag #$targetBagNumber (Shelf $targetShelfCode)",
                                    color = Color(0xFF90A4AE),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            TextButton(onClick = onChangeBagClick) {
                                Text(
                                    "Manual Select",
                                    color = Color(0xFF00E676),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            ScanStage.CaptureMushroom -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        onClick = onTakeMushroomPhotoClick,
                        enabled = !isScanning,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📸", fontSize = 16.sp)
                            Text(
                                "CAPTURE SPECIMEN (BAG #$targetBagNumber)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.8.sp,
                                color = Color.White
                            )
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = onRescanQr,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Border)
                    ) {
                        Text(
                            "↺  SWITCH TO DIFFERENT QR CODE",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
            ScanStage.ReviewConfirmation -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            onClick = onRetakePhoto,
                            enabled = !isScanning,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Border)
                        ) {
                            Text(
                                "↺  Retake",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        if (isQr2Confirmed) {
                            Button(
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(50.dp),
                                onClick = onConfirmScan,
                                enabled = !isScanning,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("✓", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "CONFIRM & SAVE",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        } else {
                            Button(
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(50.dp),
                                onClick = onScanQr2Click,
                                enabled = !isScanning,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("📷", fontSize = 15.sp)
                                    Text(
                                        "SCAN QR 2 CONFIRM",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Contamination Warning Alert Box
        if (inferenceSummary?.hasContamination == true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ScanResult.Reject.color.copy(alpha = 0.15f))
                    .border(1.5.dp, ScanResult.Reject.color, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("☣️", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "CONTAMINATION ALERT TRIGGERED",
                            color = ScanResult.Reject.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Bacterial Blotch or Black Mold detected in this bag. Audible alarm and vibration active. Tap to review immediate cure actions.",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Live Inspection Field Guidelines
        DataCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("💡", fontSize = 20.sp)
                Column {
                    Text("Inspection Guidance", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(
                        "Align the camera squarely with the oyster mushroom cluster. Ensure even lighting and keep the grow bag in full frame for highest grading accuracy.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanScreen(
    repository: PleuroTechRepository,
    onOpenAssistant: () -> Unit = {}
) {
    val context = LocalContext.current
    val detector = remember { YoloV8Detector(context) }
    val alarmManager = remember { DiseaseAlarmManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            detector.close()
            alarmManager.stopAlarm()
        }
    }

    val scope = rememberCoroutineScope()
    val latest = repository.latestScan
    var inferenceSummary by remember { mutableStateOf<InferenceSummary?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var scanStage by remember { mutableStateOf(ScanStage.ScanQr) }
    var cameraIntent by remember { mutableStateOf(CameraIntent.ScanQr1) }
    var isQr2Confirmed by remember { mutableStateOf(false) }

    var targetBagNumber by remember { mutableIntStateOf(1) }
    val targetShelfCode = repository.shelfCodeForBag(targetBagNumber)
    var qrConflictDetected by remember { mutableStateOf(false) }
    var conflictingBagNumber by remember { mutableStateOf<Int?>(null) }
    var qrErrorMessage by remember { mutableStateOf<String?>(null) }

    var showBagSelectorDialog by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf(false) }
    var successToastMessage by remember { mutableStateOf("") }

    var showDiseaseDialog by remember { mutableStateOf(false) }
    var activeDiseaseLabel by remember { mutableStateOf("") }
    var activeDiseaseConfidence by remember { mutableStateOf(0f) }

    // High-Resolution Camera Launcher
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            isScanning = true
            scope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(photoUri!!)
                    val bmp = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    withContext(Dispatchers.Main) {
                        if (bmp != null) {
                            when (cameraIntent) {
                                CameraIntent.ScanQr1 -> {
                                    // STEP 1: Decode Collar QR 1 (ID)
                                    val qrText = decodeQrFromBitmap(bmp)
                                    val parsed = if (qrText != null) parseQrPayload(qrText, repository) else null
                                    if (parsed != null) {
                                        if (parsed.qrType == QrType.Confirmation) {
                                            qrErrorMessage = "⚠️ This is QR 2 (Confirmation). Please scan QR 1 first to identify the bag."
                                        } else {
                                            targetBagNumber = parsed.bagNumber
                                            qrErrorMessage = null
                                            isQr2Confirmed = false
                                            qrConflictDetected = false
                                            conflictingBagNumber = null
                                            scanStage = ScanStage.CaptureMushroom
                                        }
                                    } else {
                                        qrErrorMessage = if (qrText != null) {
                                            "Unrecognized QR payload ($qrText). Expected Bag QR 1."
                                        } else {
                                            "No QR code found in photo. Please frame the bag collar QR 1 closer."
                                        }
                                    }
                                    isScanning = false
                                }
                                CameraIntent.CaptureMushroom -> {
                                    // STEP 2: Process Mushroom Photo & Anti-Conflict Cross-Check
                                    capturedBitmap = bmp
                                    isQr2Confirmed = false

                                    // Anti-conflict: If collar QR is visible in the mushroom photo, verify it matches
                                    val secondQr = decodeQrFromBitmap(bmp)
                                    val secondParsed = if (secondQr != null) parseQrPayload(secondQr, repository) else null
                                    if (secondParsed != null && secondParsed.bagNumber != targetBagNumber) {
                                        qrConflictDetected = true
                                        conflictingBagNumber = secondParsed.bagNumber
                                    } else {
                                        qrConflictDetected = false
                                        conflictingBagNumber = null
                                    }

                                    // Run YOLOv8 multi-scale object detection
                                    val summary = detector.detect(bmp)
                                    inferenceSummary = summary
                                    isScanning = false
                                    scanStage = ScanStage.ReviewConfirmation

                                    if (summary.hasContamination) {
                                        activeDiseaseLabel = summary.primaryLabel
                                        activeDiseaseConfidence = summary.primaryConfidence
                                        showDiseaseDialog = true
                                        alarmManager.triggerContaminationAlarm()
                                    }
                                }
                                CameraIntent.ScanQr2Confirm -> {
                                    // STEP 3: Decode Confirmation QR 2 (Location Verification)
                                    val qrText = decodeQrFromBitmap(bmp)
                                    val parsed = if (qrText != null) parseQrPayload(qrText, repository) else null
                                    if (parsed != null) {
                                        if (parsed.bagNumber == targetBagNumber) {
                                            // Matches locked bag!
                                            isQr2Confirmed = true
                                            qrConflictDetected = false
                                            conflictingBagNumber = null
                                            qrErrorMessage = null
                                        } else {
                                            // Belongs to a different bag!
                                            qrConflictDetected = true
                                            conflictingBagNumber = parsed.bagNumber
                                            isQr2Confirmed = false
                                            qrErrorMessage = "QR 2 mismatch: Scanned Bag #${parsed.bagNumber}, but target is Bag #$targetBagNumber."
                                        }
                                    } else {
                                        qrErrorMessage = if (qrText != null) {
                                            "Unrecognized QR payload ($qrText). Expected Confirmation QR 2 for Bag #$targetBagNumber."
                                        } else {
                                            "No QR code found. Please align camera squarely with QR 2 on Bag #$targetBagNumber."
                                        }
                                    }
                                    isScanning = false
                                }
                            }
                        } else {
                            isScanning = false
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { isScanning = false }
                }
            }
        }
    }

    val launchHighResCamera = {
        try {
            val file = File(context.cacheDir, "camera_scan_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            photoUri = uri
            cameraCaptureLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchHighResCamera()
        }
    }

    val requestCameraAndLaunch = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            launchHighResCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val onScanQrClick = {
        cameraIntent = CameraIntent.ScanQr1
        qrErrorMessage = null
        requestCameraAndLaunch()
    }

    val onTakeMushroomPhotoClick = {
        cameraIntent = CameraIntent.CaptureMushroom
        requestCameraAndLaunch()
    }

    val onScanQr2Click = {
        cameraIntent = CameraIntent.ScanQr2Confirm
        qrErrorMessage = null
        requestCameraAndLaunch()
    }

    val onRetakePhoto = {
        capturedBitmap = null
        inferenceSummary = null
        qrConflictDetected = false
        conflictingBagNumber = null
        isQr2Confirmed = false
        scanStage = ScanStage.CaptureMushroom
        onTakeMushroomPhotoClick()
    }

    val onRescanQr = {
        capturedBitmap = null
        inferenceSummary = null
        qrConflictDetected = false
        conflictingBagNumber = null
        qrErrorMessage = null
        isQr2Confirmed = false
        scanStage = ScanStage.ScanQr
    }

    val onConfirmScan = {
        val summary = inferenceSummary
        val bmp = capturedBitmap
        if (summary != null && bmp != null) {
            scope.launch(Dispatchers.IO) {
                val photoPath = saveScanPhoto(context, bmp, targetBagNumber)
                val breakdown = if (summary.detections.size > 1) {
                    summary.detections.groupBy { it.label.replace('_', ' ') }
                        .entries.joinToString(" · ") { "${it.value.size} ${it.key}" }
                } else {
                    summary.primaryLabel.replace('_', ' ')
                }
                val conf = if (summary.primaryConfidence > 0f) summary.primaryConfidence else 0.85f

                repository.addScan(
                    bagNumber = targetBagNumber,
                    result = summary.primaryGrade,
                    confidence = conf,
                    shelfCode = targetShelfCode,
                    customExplanation = "${summary.primaryLabel.replace('_', ' ')} (${(conf * 100).toInt()}%) · ${summary.detections.size} mushrooms",
                    photoPath = photoPath,
                    mushroomCount = max(1, summary.detections.size),
                    classBreakdown = breakdown
                )

                withContext(Dispatchers.Main) {
                    successToastMessage = "✅ Bag #$targetBagNumber saved to Shelf $targetShelfCode (${summary.detections.size} mushrooms)!"
                    showSuccessToast = true
                    capturedBitmap = null
                    inferenceSummary = null
                    qrConflictDetected = false
                    conflictingBagNumber = null
                    isQr2Confirmed = false
                    scanStage = ScanStage.ScanQr
                    // Advance to next bag
                    targetBagNumber = if (targetBagNumber < repository.activeBatch.targetBagCount) targetBagNumber + 1 else 1
                }
            }
        }
    }

    if (showDiseaseDialog) {
        DiseaseAlertModal(
            diseaseLabel = activeDiseaseLabel,
            confidence = activeDiseaseConfidence,
            onSilenceAndDismiss = {
                alarmManager.stopAlarm()
                showDiseaseDialog = false
            },
            onConsultAi = {
                alarmManager.stopAlarm()
                showDiseaseDialog = false
                onOpenAssistant()
            }
        )
    }

    if (showBagSelectorDialog) {
        Dialog(onDismissRequest = { showBagSelectorDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141920)),
                border = BorderStroke(1.dp, Color(0xFF263342))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Select Bag to Inspect", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Choose bag location to lock shelf coordinates:", color = Color(0xFF90A4AE), fontSize = 12.sp)

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..repository.activeBatch.targetBagCount).forEach { bNum ->
                            val isSelected = bNum == targetBagNumber
                            val sCode = repository.shelfCodeForBag(bNum)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF00C853) else Color(0xFF1E2833))
                                    .border(1.dp, if (isSelected) Color(0xFF00E676) else Color(0xFF2E3D4D), RoundedCornerShape(8.dp))
                                    .clickable {
                                        targetBagNumber = bNum
                                        showBagSelectorDialog = false
                                        qrErrorMessage = null
                                        scanStage = ScanStage.CaptureMushroom
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "#$bNum ($sCode)",
                                    color = if (isSelected) Color.White else Color(0xFFECEFF1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showBagSelectorDialog = false }) {
                            Text("Cancel", color = Color(0xFF90A4AE))
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageTitle("Scan", "On-device YOLOv8 object detection & grading")

        if (showSuccessToast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ScanResult.ClassA.color.copy(alpha = 0.18f))
                    .border(1.dp, ScanResult.ClassA.color, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(successToastMessage, color = ScanResult.ClassA.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    TextButton(onClick = { showSuccessToast = false }) {
                        Text("Dismiss", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        MobileScanPanel(
            detector = detector,
            scanStage = scanStage,
            inferenceSummary = inferenceSummary,
            capturedBitmap = capturedBitmap,
            isScanning = isScanning,
            targetBagNumber = targetBagNumber,
            targetShelfCode = targetShelfCode,
            isQr2Confirmed = isQr2Confirmed,
            qrConflictDetected = qrConflictDetected,
            conflictingBagNumber = conflictingBagNumber,
            qrErrorMessage = qrErrorMessage,
            onScanQrClick = onScanQrClick,
            onTakeMushroomPhotoClick = onTakeMushroomPhotoClick,
            onScanQr2Click = onScanQr2Click,
            onRetakePhoto = onRetakePhoto,
            onConfirmScan = onConfirmScan,
            onRescanQr = onRescanQr,
            onChangeBagClick = { showBagSelectorDialog = true }
        )

        AiCommandCenter(
            brief = PleuroAssistant().brief(repository),
            compact = false
        )

        DataCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Model Architecture", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("YOLOv8s · 640x640 · 42.6 MB FlatBuffer", color = TextMuted, fontSize = 12.sp)
                }
                StatusPill("TFLite Active", ScanResult.ClassA.color)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Trained on 7 distinct classes: Ready Class A, Ready Class B, Potential Class A, Potential Class B, Bacterial Blotch, Black Mold, and Reject. Runs 100% offline via hardware-accelerated XNNPACK.",
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }

        DataCard {
            Text("Latest scan record", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            if (latest == null) {
                Text("No scans recorded yet.", color = TextMuted, fontSize = 12.sp)
            } else {
                LatestScanDetail(scan = latest, onVerify = { repository.verifyScan(latest.id, it) })
            }
        }

        Spacer(Modifier.height(88.dp))
    }
}

@Composable
private fun LabelsScreen(repository: PleuroTechRepository, onPrint: (List<BagLabel>, String) -> Unit) {
    var labelCount by remember { mutableIntStateOf(24) }
    var customText by remember { mutableStateOf("24") }
    var isPrinting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
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
                onClick = {
                    isPrinting = true
                    scope.launch {
                        onPrint(labels, batch.name)
                        delay(1200)
                        isPrinting = false
                    }
                },
                enabled = labels.isNotEmpty() && !isPrinting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScanResult.ClassA.color,
                    disabledContainerColor = ScanResult.ClassA.color.copy(alpha = 0.4f)
                )
            ) {
                if (isPrinting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color(0xFF155E2B),
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            "Generating Print Document...",
                            color = Color(0xFF155E2B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
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
private fun RecordsScreen(
    repository: PleuroTechRepository,
    subTab: RecordsSubTab,
    onSubTabChanged: (RecordsSubTab) -> Unit,
    onExport: () -> Unit,
    onClear: () -> Unit,
    onPrintLabels: (List<BagLabel>, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecordsSubTab.values().forEach { tab ->
                    val isSelected = subTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ScanResult.ClassA.color.copy(alpha = 0.16f) else SurfaceAlt)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ScanResult.ClassA.color else Border,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSubTabChanged(tab) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tab.icon, fontSize = 14.sp)
                            Text(
                                text = tab.title,
                                color = if (isSelected) ScanResult.ClassA.color else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        when (subTab) {
            RecordsSubTab.History -> HistoryScreen(
                repository = repository,
                onExport = onExport,
                onClear = onClear
            )
            RecordsSubTab.Analytics -> AnalyticsScreen(
                repository = repository
            )
            RecordsSubTab.Labels -> LabelsScreen(
                repository = repository,
                onPrint = onPrintLabels
            )
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

    var selectedScanForModal by remember { mutableStateOf<ScanRecord?>(null) }

    if (selectedScanForModal != null) {
        InspectionDetailModal(
            scan = selectedScanForModal!!,
            onDismiss = { selectedScanForModal = null },
            onVerify = { scan, result -> repository.verifyScan(scan.id, result) }
        )
    }

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
                onSelectScan = { selectedScanForModal = it },
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
    var isThinking by remember { mutableStateOf(false) }
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
        if (cleaned.isEmpty() || isThinking) return
        messages.add(AssistantMessage(MessageSender.User, cleaned))
        input = ""
        isThinking = true
        scope.launch {
            listState.animateScrollToItem(messages.lastIndex)
            delay(500)
            val answer = assistant.answer(cleaned, repository)
            messages.add(AssistantMessage(MessageSender.Assistant, answer))
            isThinking = false
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
            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ScanResult.ClassA.color,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "AI Farm Assistant is reviewing records & answering...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
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
                        .clickable(enabled = isReady && !isThinking) {
                            focusManager.clearFocus()
                            sendPrompt(input)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isThinking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        SendIcon(
                            color = sendContentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
private fun ShelfMapPanel(
    shelves: List<ShelfSummary>,
    onSelectShelf: (ShelfSummary) -> Unit = {}
) {
    Column {
        SectionLabel("Shelf Map")
        Spacer(Modifier.height(8.dp))
        DataCard {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                shelves.forEach { shelf ->
                    ShelfTile(shelf = shelf, onClick = { onSelectShelf(shelf) })
                }
            }
        }
    }
}

@Composable
private fun ShelfTile(shelf: ShelfSummary, onClick: () -> Unit = {}) {
    val color = shelfStatusColor(shelf)
    Column(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.13f))
            .border(1.dp, color.copy(alpha = 0.32f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(shelf.shelfCode, color = color, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text("${shelf.total} scans", color = TextMuted, fontSize = 10.sp, maxLines = 1)
        Text("${shelf.reject} R", color = ScanResult.Reject.color, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun ShelfDetailModal(
    shelf: ShelfSummary,
    scans: List<ScanRecord>,
    onDismiss: () -> Unit
) {
    var expandedBagScan by remember { mutableStateOf<ScanRecord?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp)),
            color = Surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (expandedBagScan != null) {
                    val scan = expandedBagScan!!
                    val photoBitmap = remember(scan.photoPath) {
                        if (!scan.photoPath.isNullOrEmpty()) {
                            try {
                                BitmapFactory.decodeFile(scan.photoPath)
                            } catch (_: Exception) {
                                null
                            }
                        } else {
                            null
                        }
                    }

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bag #${scan.bagNumber}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Shelf ${scan.shelfCode} · Batch ${scan.batchId}",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        ResultBadge(
                            result = scan.finalResult,
                            text = if (scan.verified) "V-${scan.finalResult.displayName}" else scan.finalResult.displayName
                        )
                    }

                    // Captured Mushroom Photo
                    if (photoBitmap != null) {
                        Image(
                            bitmap = photoBitmap.asImageBitmap(),
                            contentDescription = "Captured Mushroom Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.30f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Border, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceAlt)
                                .border(1.dp, Border, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🍄", fontSize = 32.sp)
                                Text("No Photo Attached", color = TextMuted, fontSize = 12.sp)
                                Text("Captured prior to live photo persistence", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }

                    // Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniMetric(
                            modifier = Modifier.weight(1f),
                            label = "COUNT",
                            value = "${scan.mushroomCount}",
                            accent = ScanResult.ClassA.color
                        )
                        MiniMetric(
                            modifier = Modifier.weight(1f),
                            label = "CONFIDENCE",
                            value = "${(scan.confidence * 100).toInt()}%",
                            accent = ScanResult.ClassB.color
                        )
                        MiniMetric(
                            modifier = Modifier.weight(1f),
                            label = "GRADE",
                            value = scan.finalResult.shortName,
                            accent = scan.finalResult.color
                        )
                    }

                    if (scan.classBreakdown.isNotEmpty()) {
                        Text(
                            text = "Breakdown: ${scan.classBreakdown}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (scan.aiExplanation.isNotEmpty()) {
                        Text(
                            text = scan.aiExplanation,
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Text(
                        text = "Scanned on ${scan.dateLabel} at ${scan.timeLabel}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { expandedBagScan = null }
                        ) {
                            Text("← Back to Bags", color = TextPrimary)
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceAlt)
                        ) {
                            Text("Close", color = TextPrimary)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Shelf ${shelf.shelfCode}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${shelf.total} Total Scans · ${shelf.reject} Rejects",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        StatusPill(label = shelf.status, color = shelfStatusColor(shelf))
                    }

                    Text(
                        text = "Tap any bag below to view its captured mushroom photo and details:",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    if (scans.isEmpty()) {
                        Text(
                            text = "No inspection records found for this shelf yet.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            scans.forEach { scan ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceAlt)
                                        .clickable { expandedBagScan = scan }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "Bag #${scan.bagNumber}",
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            if (!scan.photoPath.isNullOrEmpty()) {
                                                Text("📷", fontSize = 11.sp)
                                            }
                                        }
                                        Text(
                                            text = "${scan.mushroomCount} mushrooms · ${scan.timeLabel}",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                    ResultBadge(result = scan.finalResult)
                                }
                            }
                        }
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceAlt)
                    ) {
                        Text("Close", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectionDetailModal(
    scan: ScanRecord,
    onDismiss: () -> Unit,
    onVerify: ((ScanRecord, ScanResult) -> Unit)? = null
) {
    val photoBitmap = remember(scan.photoPath) {
        if (!scan.photoPath.isNullOrEmpty()) {
            try {
                BitmapFactory.decodeFile(scan.photoPath)
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp)),
            color = Surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bag #${scan.bagNumber}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Shelf ${scan.shelfCode} · Batch ${scan.batchId}",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    ResultBadge(
                        result = scan.finalResult,
                        text = if (scan.verified) "V-${scan.finalResult.displayName}" else scan.finalResult.displayName
                    )
                }

                // Captured Mushroom Photo
                if (photoBitmap != null) {
                    Image(
                        bitmap = photoBitmap.asImageBitmap(),
                        contentDescription = "Captured Mushroom Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.30f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Border, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceAlt)
                            .border(1.dp, Border, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🍄", fontSize = 32.sp)
                            Text("No Photo Available", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }

                // Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniMetric(
                        modifier = Modifier.weight(1f),
                        label = "COUNT",
                        value = "${scan.mushroomCount}",
                        accent = ScanResult.ClassA.color
                    )
                    MiniMetric(
                        modifier = Modifier.weight(1f),
                        label = "CONFIDENCE",
                        value = "${(scan.confidence * 100).toInt()}%",
                        accent = ScanResult.ClassB.color
                    )
                    MiniMetric(
                        modifier = Modifier.weight(1f),
                        label = "GRADE",
                        value = scan.finalResult.shortName,
                        accent = scan.finalResult.color
                    )
                }

                if (scan.classBreakdown.isNotEmpty()) {
                    Text(
                        text = "Breakdown: ${scan.classBreakdown}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (scan.aiExplanation.isNotEmpty()) {
                    Text(
                        text = scan.aiExplanation,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Text(
                    text = "Scanned on ${scan.dateLabel} at ${scan.timeLabel}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                // Close Button
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceAlt)
                ) {
                    Text("Close", color = TextPrimary)
                }
            }
        }
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
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M
        )
        QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 33, 33, hints)
    }
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(6.dp)
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("PleuroTech Dual Tag", color = Color(0xFF1D2D22), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(label.batchId, color = Color(0xFF607067), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QR 1 Compartment
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F8F5))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF00796B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("1. SCAN FIRST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
                QrCode(payload = label.qrPayload, modifier = Modifier.size(90.dp))
                Text("Step 1 ID", color = Color(0xFF00796B), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }

            // QR 2 Compartment
            val confirmPayload = if (label.confirmQrPayload.isNotEmpty()) label.confirmQrPayload else "${label.qrPayload}/confirm"
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F7FB))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0288D1))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("2. CONFIRM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
                QrCode(payload = confirmPayload, modifier = Modifier.size(90.dp))
                Text("Step 3 Verify", color = Color(0xFF0288D1), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Text(
            "Bag ${label.bagNumber.toString().padStart(3, '0')}  |  Shelf ${label.shelfCode}",
            color = Color(0xFF1D2D22),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
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
    onSelectScan: ((ScanRecord) -> Unit)? = null,
    onVerify: ((ScanRecord, ScanResult) -> Unit)? = null
) {
    if (scans.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🍄", fontSize = 32.sp)
            Text(
                text = "No Scans Recorded Yet",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "Inspection history will appear here as you scan oyster mushroom bags with the camera.",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
        return
    }
    scans.forEachIndexed { index, scan ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = onSelectScan != null) { onSelectScan?.invoke(scan) }
                .padding(vertical = 10.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!compact) Text("#${scan.id}", modifier = Modifier.width(42.dp), color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            Column(modifier = Modifier.widthIn(min = 58.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Bag ${scan.bagNumber}",
                        color = if (compact) TextMuted else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    if (!scan.photoPath.isNullOrEmpty()) {
                        Text("📷", fontSize = 10.sp)
                    }
                }
                Text(
                    scan.shelfCode,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
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
    if (trend.all { it.total == 0 }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No yield trend data yet. New daily scans will populate this chart.",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }
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
    if (trend.all { it.total == 0 }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Daily totals will appear here as bags are scanned.",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }
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
    AppTab.Dashboard -> "🏠"
    AppTab.Scan -> "📷"
    AppTab.Records -> "📊"
    AppTab.Assistant -> "🤖"
    AppTab.Labels -> "🏷️"
    AppTab.History -> "📋"
    AppTab.Analytics -> "📈"
    AppTab.Settings -> "⚙️"
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