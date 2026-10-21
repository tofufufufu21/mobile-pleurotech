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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.example.pleurotech.data.DailyYieldProjection
import com.example.pleurotech.data.HarvestActionTip
import com.example.pleurotech.data.HarvestForecast
import com.example.pleurotech.data.MushroomBatch
import com.example.pleurotech.data.PleuroTechRepository
import com.example.pleurotech.data.PreHarvestReflection
import com.example.pleurotech.data.ScanCounts
import com.example.pleurotech.data.ScanRecord
import com.example.pleurotech.data.ScanResult
import com.example.pleurotech.data.ScanReport
import com.example.pleurotech.data.ShelfSummary
import com.example.pleurotech.data.TrendInsights
import com.example.pleurotech.data.sync.SyncResult
import com.example.pleurotech.ml.MushroomDiagnosisGuide
import com.example.pleurotech.ml.ActionStatus
import com.example.pleurotech.ui.components.WeatherForecastPanel
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

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import com.example.pleurotech.ui.theme.*

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
    val infoBlue: Color,
    val emeraldPrimary: Color = Color(0xFF059669),
    val emeraldSoft: Color = Color(0xFFECFDF5),
    val isDark: Boolean = false
)

val LightPleuroColors = PleuroColors(
    appBackground = Color(0xFFF6F8F6),
    surface = Color(0xFFFFFFFF),
    surfaceAlt = Color(0xFFEEF4EF),
    border = Color(0xFFE5EAE5),
    textPrimary = Color(0xFF111827),
    textMuted = Color(0xFF64748B),
    infoBlue = Color(0xFF0284C7),
    emeraldPrimary = Color(0xFF16A34A),
    emeraldSoft = Color(0xFFEBF7EE),
    isDark = false
)

val DarkPleuroColors = PleuroColors(
    appBackground = Color(0xFF0B130E),
    surface = Color(0xFF122117),
    surfaceAlt = Color(0xFF1B3224),
    border = Color(0x334ADE80),
    textPrimary = Color(0xFFF0FDF4),
    textMuted = Color(0xFF86A792),
    infoBlue = Color(0xFF38BDF8),
    emeraldPrimary = Color(0xFF22C55E),
    emeraldSoft = Color(0xFF143820),
    isDark = true
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

val EmeraldPrimary: Color
    @Composable get() = LocalPleuroColors.current.emeraldPrimary

val EmeraldSoft: Color
    @Composable get() = LocalPleuroColors.current.emeraldSoft

val ScanResult.softBg: Color
    @Composable get() = if (LocalPleuroColors.current.isDark) color.copy(alpha = 0.15f) else softColor

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
    var showOnboarding by remember { mutableStateOf(!PleuroTechRepository.isOnboardingCompleted(context)) }
    var showGuidedTour by remember { mutableStateOf(false) }

    var showSplash by remember { mutableStateOf(true) }

    CompositionLocalProvider(LocalPleuroColors provides currentColors) {
        PleurotechTheme(darkTheme = isDark) {
            if (showSplash) {
                SplashScreen(onSplashFinished = { showSplash = false })
            } else if (!isAuthenticated) {
                AuthScreen(
                    repository = repository,
                    isDark = isDark,
                    onToggleDark = { isDarkChecked ->
                        themePreference = if (isDarkChecked) ThemePreference.Dark else ThemePreference.Light
                    },
                    onAuthSuccess = {
                        val user = repository.authManager?.getCurrentUser()
                        selectedTab = if (user?.isOwner == false) AppTab.Scan else AppTab.Dashboard
                        showOnboarding = !PleuroTechRepository.isOnboardingCompleted(context)
                        isAuthenticated = true
                    }
                )
            } else if (showOnboarding) {
                OnboardingScreen(
                    onComplete = {
                        PleuroTechRepository.setOnboardingCompleted(context, true)
                        showOnboarding = false
                        showGuidedTour = true
                        val user = repository.authManager?.getCurrentUser()
                        selectedTab = if (user?.isOwner == false) AppTab.Scan else AppTab.Dashboard
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

                androidx.compose.runtime.LaunchedEffect(Unit) {
                    repository.syncWithCloud()
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        containerColor = AppBackground,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                shape = RoundedCornerShape(32.dp),
                                color = Surface,
                                tonalElevation = 6.dp,
                                border = BorderStroke(1.dp, Border)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    navTabs.forEach { tab ->
                                        val isSelected = selectedTab == tab || (tab == AppTab.Records && (selectedTab == AppTab.History || selectedTab == AppTab.Analytics || selectedTab == AppTab.Labels))
                                        val isScanTab = tab == AppTab.Scan

                                        if (isScanTab) {
                                            val fabBg by animateColorAsState(
                                                targetValue = if (isSelected) EmeraldPrimary else Surface,
                                                label = "fabBg"
                                            )
                                            val iconColor by animateColorAsState(
                                                targetValue = if (isSelected) Color.White else EmeraldPrimary,
                                                label = "fabIconColor"
                                            )
                                            val borderColor by animateColorAsState(
                                                targetValue = if (isSelected) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.85f),
                                                label = "fabBorderColor"
                                            )

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                                modifier = Modifier.clickable { selectedTab = tab }
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(52.dp)
                                                        .clip(CircleShape)
                                                        .background(fabBg)
                                                        .border(2.5.dp, borderColor, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    QrScannerVectorIcon(color = iconColor, modifier = Modifier.size(26.dp))
                                                }
                                                Text(
                                                    text = "Scan",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) EmeraldPrimary else TextMuted
                                                )
                                            }
                                        } else {
                                            val iconColor by animateColorAsState(
                                                targetValue = if (isSelected) EmeraldPrimary else TextMuted,
                                                label = "navIconColor"
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable {
                                                        selectedTab = tab
                                                        if (tab == AppTab.Records) {
                                                            recordsSubTab = RecordsSubTab.History
                                                        }
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                AppTabIcon(tab = tab, isSelected = isSelected)
                                                Text(
                                                    text = tab.title,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = iconColor
                                                )
                                            }
                                        }
                                    }
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
                                onOpenTour = { showGuidedTour = true },
                                repository = repository
                            )

                            AnimatedContent(
                                targetState = selectedTab,
                                transitionSpec = {
                                    (fadeIn(animationSpec = tween(240, easing = FastOutSlowInEasing)) +
                                        scaleIn(initialScale = 0.98f, animationSpec = tween(240, easing = FastOutSlowInEasing)))
                                        .togetherWith(
                                            fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                                            scaleOut(targetScale = 1.01f, animationSpec = tween(180, easing = FastOutSlowInEasing))
                                        )
                                },
                                label = "ScreenTransition"
                            ) { targetTab ->
                                when (targetTab) {
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
                                            repository.shareCsv(context)
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
                                        onReplayDemo = {
                                            showGuidedTour = true
                                            selectedTab = AppTab.Dashboard
                                        },
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

                    if (showGuidedTour) {
                        GuidedTourOverlay(
                            onNavigate = { tab, subTab ->
                                selectedTab = tab
                                if (subTab != null) {
                                    recordsSubTab = subTab
                                }
                            },
                            onDismiss = {
                                showGuidedTour = false
                                PleuroTechRepository.setOnboardingCompleted(context, true)
                                val user = repository.authManager?.getCurrentUser()
                                selectedTab = if (user?.isOwner == false) AppTab.Scan else AppTab.Dashboard
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppTabIcon(tab: AppTab, isSelected: Boolean, overrideColor: Color? = null) {
    val color = overrideColor ?: if (isSelected) EmeraldPrimary else TextMuted
    val iconModifier = Modifier.size(20.dp)
    when (tab) {
        AppTab.Dashboard -> HomeVectorIcon(color = color, modifier = iconModifier)
        AppTab.Scan -> QrScannerVectorIcon(color = color, modifier = iconModifier)
        AppTab.Records, AppTab.History, AppTab.Analytics, AppTab.Labels -> DocumentVectorIcon(color = color, modifier = iconModifier)
        AppTab.Assistant -> SparklesVectorIcon(color = color, modifier = iconModifier)
        AppTab.Settings -> SettingsVectorIcon(color = color, modifier = iconModifier)
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
    onOpenTour: () -> Unit = {},
    repository: PleuroTechRepository
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sync = repository.syncManager
    val pending = sync?.getPendingCount() ?: 0
    val online = sync?.isNetworkAvailable ?: false
    val user = repository.authManager?.getCurrentUser()
    val isOwner = user?.isOwner ?: true

    val initials = remember(user?.name) {
        user?.name?.split(" ")?.filter { it.isNotEmpty() }?.take(2)?.map { it.first().uppercase() }?.joinToString("") ?: "PT"
    }

    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Welcome Back"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EmeraldSoft)
                    .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = greeting,
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = user?.name ?: "PleuroTech Farm",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Minimalist Sync Status Pill / Badge
            val isSyncing = repository.isSyncing
            val syncColor = if (isSyncing) EmeraldPrimary else if (pending > 0) ScanResult.ClassB.color else ScanResult.ClassA.color
            val syncLabel = if (isSyncing) "Syncing..." else if (pending > 0) "$pending Pending" else if (online) "Online" else "Offline"
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(syncColor.copy(alpha = 0.12f))
                    .border(1.dp, syncColor.copy(alpha = 0.25f), RoundedCornerShape(50))
                    .clickable(enabled = !isSyncing) {
                        scope.launch {
                            repository.syncWithCloud()
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(8.dp),
                        strokeWidth = 1.5.dp,
                        color = syncColor
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(syncColor)
                    )
                }
                Text(
                    text = syncLabel,
                    color = syncColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Minimalist Menu Action Button
            Box {
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
                        modifier = Modifier.size(15.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier
                        .background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(16.dp))
                        .widthIn(min = 200.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isOwner) {
                        DropdownMenuItem(
                            text = { Text("Dashboard", fontWeight = if (selectedTab == AppTab.Dashboard) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == AppTab.Dashboard) ScanResult.ClassA.color else TextPrimary) },
                            leadingIcon = { HomeVectorIcon(color = if (selectedTab == AppTab.Dashboard) ScanResult.ClassA.color else TextMuted, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                onSelectTab(AppTab.Dashboard)
                                menuExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(if (isOwner) "Scan" else "Mushroom Scan", fontWeight = if (selectedTab == AppTab.Scan) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == AppTab.Scan) ScanResult.ClassA.color else TextPrimary) },
                        leadingIcon = { QrScannerVectorIcon(color = if (selectedTab == AppTab.Scan) ScanResult.ClassA.color else TextMuted, modifier = Modifier.size(18.dp)) },
                        onClick = {
                            onSelectTab(AppTab.Scan)
                            menuExpanded = false
                        }
                    )
                    if (isOwner) {
                        DropdownMenuItem(
                            text = { Text("QR Labels", fontWeight = if (selectedTab == AppTab.Labels) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == AppTab.Labels) ScanResult.ClassA.color else TextPrimary) },
                            leadingIcon = { TagVectorIcon(color = if (selectedTab == AppTab.Labels) ScanResult.ClassA.color else TextMuted, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                onSelectTab(AppTab.Labels)
                                menuExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(if (isOwner) "Scan Logs" else "Recent Scans", fontWeight = if (selectedTab == AppTab.History) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == AppTab.History) ScanResult.ClassA.color else TextPrimary) },
                        leadingIcon = { DocumentVectorIcon(color = if (selectedTab == AppTab.History) ScanResult.ClassA.color else TextMuted, modifier = Modifier.size(18.dp)) },
                        onClick = {
                            onSelectTab(AppTab.History)
                            menuExpanded = false
                        }
                    )
                    if (isOwner) {
                        DropdownMenuItem(
                            text = { Text("Analytics", fontWeight = if (selectedTab == AppTab.Analytics) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == AppTab.Analytics) ScanResult.ClassA.color else TextPrimary) },
                            leadingIcon = { ChartVectorIcon(color = if (selectedTab == AppTab.Analytics) ScanResult.ClassA.color else TextMuted, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                onSelectTab(AppTab.Analytics)
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("AI Copilot", fontWeight = if (selectedTab == AppTab.Assistant) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == AppTab.Assistant) ScanResult.ClassA.color else TextPrimary) },
                            leadingIcon = { SparklesVectorIcon(color = if (selectedTab == AppTab.Assistant) ScanResult.ClassA.color else TextMuted, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                onSelectTab(AppTab.Assistant)
                                menuExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Interactive Tour", fontWeight = FontWeight.Medium, color = InfoBlue) },
                        leadingIcon = { SparklesVectorIcon(color = InfoBlue, modifier = Modifier.size(18.dp)) },
                        onClick = {
                            onOpenTour()
                            menuExpanded = false
                        }
                    )
                    HorizontalDivider(color = Border)
                    DropdownMenuItem(
                        text = { Text("Settings", fontWeight = if (selectedTab == AppTab.Settings) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == AppTab.Settings) ScanResult.ClassA.color else TextPrimary) },
                        leadingIcon = { SettingsVectorIcon(color = if (selectedTab == AppTab.Settings) ScanResult.ClassA.color else TextMuted, modifier = Modifier.size(18.dp)) },
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
    iconText: String = "",
    title: String,
    subtitle: String,
    icon: (@Composable () -> Unit)? = null,
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
            if (icon != null) {
                icon()
            } else {
                Text(iconText, fontSize = 20.sp)
            }
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
    onReplayDemo: () -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showFarmerManagerDialog by remember { mutableStateOf(false) }
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
    if (showFarmerManagerDialog) {
        FarmerManagementDialog(repository = repository, onDismiss = { showFarmerManagerDialog = false })
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
                icon = {
                    if (isDark) MoonVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    else SunVectorIcon(color = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                },
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
                icon = { BellVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp)) },
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
                icon = { CloudVectorIcon(color = InfoBlue, modifier = Modifier.size(20.dp)) },
                title = "Cloud Sync (Supabase)",
                subtitle = syncSubtitle,
                onClick = { showSyncDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
            // Privacy Controls
            SettingItemCard(
                icon = { LockVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp)) },
                title = "Privacy Controls",
                subtitle = "Manage your data",
                onClick = { showPrivacyDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
            // Permissions
            SettingItemCard(
                icon = { ShieldVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp)) },
                title = "Permissions",
                subtitle = "Review app permissions",
                onClick = { showPermissionsDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
        }

        // Section 3: AI Engine & Architecture
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "AI Engine & Architecture",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ScanResult.ClassA.softBg),
                            contentAlignment = Alignment.Center
                        ) {
                            SparklesVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("YOLOv8s Oyster Model", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("640×640 · 42.6 MB FlatBuffer", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                    StatusPill("TFLite Active", ScanResult.ClassA.color)
                }

                Text(
                    "Trained on 7 distinct classes: Ready Class A, Ready Class B, Potential Class A, Potential Class B, Bacterial Blotch, Black Mold, and Reject. Runs 100% offline via hardware-accelerated XNNPACK CPU inference.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceAlt)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Inference Acceleration", fontSize = 12.sp, color = TextMuted)
                    Text("XNNPACK · Multi-threaded", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ScanResult.ClassA.color)
                }
            }
        }

        // Section 4: Support
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Support & Tutorials",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            // Interactive Walkthrough Demo
            SettingItemCard(
                icon = { SparklesVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp)) },
                title = "Interactive Walkthrough Demo",
                subtitle = "Replay step-by-step onboarding tutorial",
                onClick = onReplayDemo
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
            // Help & FAQ
            SettingItemCard(
                icon = { HelpVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp)) },
                title = "Help & FAQ",
                subtitle = "Get answers & farm setup tips",
                onClick = { showHelpDialog = true }
            ) {
                ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
            }
        }

        // Section 5: Account & Profile
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
                    if (!user?.farmName.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            BuildingVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(12.dp))
                            Text(
                                text = user?.farmName ?: "",
                                fontSize = 11.sp,
                                color = ScanResult.ClassA.color,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    StatusPill(label = user?.role ?: "Inspector", color = ScanResult.ClassA.color)
                }
            }

            // Farm Owner: Manage & Create Farmers for this Farm
            if (user?.isOwner == true) {
                val farmerList = repository.authManager?.getFarmersForOwner(user.id) ?: emptyList()
                SettingItemCard(
                    icon = { WorkerVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(20.dp)) },
                    title = "Manage Farm Workers",
                    subtitle = if (farmerList.isEmpty()) "Add & create worker accounts for your farm" else "${farmerList.size} active farm workers",
                    onClick = { showFarmerManagerDialog = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "+ Add Farmer",
                            color = ScanResult.ClassA.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        ChevronRightIcon(color = TextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Sign Out Card
            SettingItemCard(
                icon = { LogoutVectorIcon(color = ScanResult.Reject.color, modifier = Modifier.size(20.dp)) },
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
private fun FarmerManagementDialog(repository: PleuroTechRepository, onDismiss: () -> Unit) {
    val auth = repository.authManager
    val user = auth?.getCurrentUser()
    val scope = rememberCoroutineScope()

    var farmerList by remember { mutableStateOf(if (user != null) auth.getFarmersForOwner(user.id) else emptyList()) }
    var isAdding by remember { mutableStateOf(false) }

    var farmerName by remember { mutableStateOf("") }
    var farmerEmail by remember { mutableStateOf("") }
    var farmerPassword by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ScanResult.ClassA.softBg)
                            .border(1.dp, ScanResult.ClassA.color.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        WorkerVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = if (isAdding) "New Worker Account" else "Farm Workers",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = user?.farmName ?: "PleuroTech Farm",
                            fontSize = 12.sp,
                            color = ScanResult.ClassA.color,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (isAdding) {
                            isAdding = false
                            statusMessage = null
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("✕", color = TextMuted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (!isAdding) {
                    Text(
                        text = "Farmers registered under your Farm Owner account will automatically upload inspection logs to your records.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 17.sp
                    )

                    // Farmer List Overview
                    if (farmerList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceAlt)
                                .border(1.dp, Border, RoundedCornerShape(14.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SproutVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(28.dp))
                                Text("No farm workers yet", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    "Add workers so they can log harvests from their mobile phones.",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            farmerList.forEach { f ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceAlt)
                                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(ScanResult.ClassA.softBg)
                                            .border(1.dp, ScanResult.ClassA.color.copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            f.name.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = ScanResult.ClassA.color,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(f.name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 13.sp)
                                        Text(f.email, color = TextMuted, fontSize = 11.sp)
                                    }
                                    StatusPill(label = "Active", color = ScanResult.ClassA.color)
                                }
                            }
                        }
                    }
                } else {
                    // Adding form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Enter login credentials for the worker. They will use this email and password to log in.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )

                        val workerFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ScanResult.ClassA.color,
                            unfocusedBorderColor = Border,
                            focusedLabelColor = ScanResult.ClassA.color,
                            unfocusedLabelColor = TextMuted,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedPlaceholderColor = TextMuted.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = TextMuted.copy(alpha = 0.6f),
                            cursorColor = ScanResult.ClassA.color,
                            focusedContainerColor = SurfaceAlt.copy(alpha = 0.35f),
                            unfocusedContainerColor = SurfaceAlt.copy(alpha = 0.2f)
                        )

                        OutlinedTextField(
                            value = farmerName,
                            onValueChange = { farmerName = it },
                            label = { Text("Worker Full Name") },
                            placeholder = { Text("e.g. Pedro Santos") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = workerFieldColors
                        )

                        OutlinedTextField(
                            value = farmerEmail,
                            onValueChange = { farmerEmail = it },
                            label = { Text("Worker Login Email") },
                            placeholder = { Text("pedro@farm.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            colors = workerFieldColors
                        )

                        OutlinedTextField(
                            value = farmerPassword,
                            onValueChange = { farmerPassword = it },
                            label = { Text("Temporary Password") },
                            placeholder = { Text("••••••••") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            colors = workerFieldColors
                        )

                        if (statusMessage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isError) ScanResult.Reject.color.copy(alpha = 0.12f) else ScanResult.ClassA.color.copy(alpha = 0.12f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isError) ScanResult.Reject.color.copy(alpha = 0.3f) else ScanResult.ClassA.color.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = statusMessage!!,
                                    color = if (isError) ScanResult.Reject.color else ScanResult.ClassA.color,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isAdding) {
                Button(
                    onClick = {
                        isAdding = true
                        statusMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Create Worker Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            isAdding = false
                            statusMessage = null
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = TextPrimary,
                            maxLines = 1,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (farmerName.isBlank() || farmerEmail.isBlank() || farmerPassword.isBlank()) {
                                statusMessage = "Please complete all fields."
                                isError = true
                                return@Button
                            }
                            if (user == null) {
                                statusMessage = "No active owner session."
                                isError = true
                                return@Button
                            }

                            isSubmitting = true
                            statusMessage = null
                            scope.launch {
                                val result = auth.createFarmerAccountByOwner(
                                    farmerEmail = farmerEmail,
                                    farmerPassword = farmerPassword,
                                    farmerName = farmerName,
                                    ownerProfile = user
                                )
                                isSubmitting = false
                                if (result.isSuccess) {
                                    isError = false
                                    statusMessage = "Farmer account created for ${farmerName.trim()}!"
                                    farmerList = auth.getFarmersForOwner(user.id)
                                    farmerName = ""
                                    farmerEmail = ""
                                    farmerPassword = ""
                                    isAdding = false
                                } else {
                                    isError = true
                                    statusMessage = result.exceptionOrNull()?.message ?: "Failed to create account"
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1.4f),
                        colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save Worker", color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        dismissButton = {
            if (!isAdding) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", color = TextPrimary)
                }
            }
        }
    )
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
                LockVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(22.dp))
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
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
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
                ShieldVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(24.dp))
                Text("App Permissions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple("Camera", "Granted", "Required for scanning QR labels & YOLOv8 grading"),
                    Triple("Printing Service", "Active", "Required for generating printable PDF QR sheets"),
                    Triple("Notifications", "Granted", "Used for real-time contamination & harvest alerts"),
                    Triple("Storage", "Active", "Used for CSV data export & temporary report files")
                ).forEach { (name, status, desc) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceAlt)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                when (name) {
                                    "Camera" -> CameraVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(18.dp))
                                    "Printing Service" -> PrinterVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(18.dp))
                                    "Notifications" -> BellVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(18.dp))
                                    else -> DocumentVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(18.dp))
                                }
                                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            }
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
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
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
                HelpVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(22.dp))
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
                Text("Understood", color = Color.White, fontWeight = FontWeight.Bold)
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
                CloudVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(24.dp))
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

    val isOwner = repository.authManager?.getCurrentUser()?.isOwner ?: true
    var inspectionModalScan by remember { mutableStateOf<ScanRecord?>(null) }
    var selectedShelfForModal by remember { mutableStateOf<ShelfSummary?>(null) }
    var showAddRackDialog by remember { mutableStateOf(false) }

    if (showAddRackDialog) {
        val nextRackLetter = ('A'.code + repository.activeBatch.rackCount).toChar()
        AlertDialog(
            onDismissRequest = { showAddRackDialog = false },
            containerColor = Surface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text("Add Rack $nextRackLetter", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Add Rack $nextRackLetter (Shelves ${nextRackLetter}1–${nextRackLetter}5) to batch \"${repository.activeBatch.name}\"?",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "This expands room capacity by 25 fruiting bags and creates scannable shelf codes immediately.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.addRackToActiveBatch()
                        showAddRackDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ Confirm Add Rack", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddRackDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

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

    var selectedRackFilter by remember { mutableStateOf<Char?>(null) }
    val filteredRecent = remember(recent, selectedRackFilter) {
        if (selectedRackFilter == null) recent
        else recent.filter { it.shelfCode.startsWith(selectedRackFilter.toString(), ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WeatherForecastPanel()
        }
        item {
            BatchStatusPanel(repository = repository)
        }
        item {
            TwinMetricCards(repository = repository)
        }
        item {
            RackCategoryRow(
                shelves = repository.shelfMap(),
                selectedRack = selectedRackFilter,
                onSelectRack = { selectedRackFilter = it },
                onSelectShelf = { selectedShelfForModal = it },
                isOwner = isOwner,
                onAddRack = { showAddRackDialog = true }
            )
        }
        item {
            TriAiAnalyticsPanel(repository = repository)
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
            ChartCard(title = "Premium Yield Trend", subtitle = "Last 7 days") {
                TrendStackedBars(trend = trend)
            }
        }
        item {
            SectionLabel(if (selectedRackFilter != null) "Recent Scans (Rack $selectedRackFilter)" else "Recent Scans")
            Spacer(Modifier.height(8.dp))
            DataCard {
                ScanRows(
                    scans = filteredRecent,
                    compact = true,
                    onSelectScan = { inspectionModalScan = it }
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenScan,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("📷 Start Real Scan", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onOpenHistory,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("View History", color = TextPrimary)
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
        val isConfirm = trimmed.contains("/confirm", ignoreCase = true) ||
            trimmed.contains(":confirm", ignoreCase = true) ||
            trimmed.contains("-confirm", ignoreCase = true) ||
            trimmed.contains("confirm", ignoreCase = true)

        // Check 1: Explicit Shelf Code (e.g. "A1", "B2", "Shelf A1", "Rack A Shelf 2", "A-1")
        val shelfMatch = Regex("""(?:RACK\s*([A-D])\s*SHELF\s*([1-5])|SHELF\s*([A-D])[-_ ]?([1-5])|^([A-D])[-_ ]?([1-5]))""", RegexOption.IGNORE_CASE).find(trimmed)
        val bagNumFromShelf = if (shelfMatch != null) {
            val rackChar = (shelfMatch.groupValues[1].ifEmpty { shelfMatch.groupValues[3].ifEmpty { shelfMatch.groupValues[5] } }).first().uppercaseChar()
            val shelfChar = (shelfMatch.groupValues[2].ifEmpty { shelfMatch.groupValues[4].ifEmpty { shelfMatch.groupValues[6] } }).toIntOrNull()
            if (rackChar in 'A'..'D' && shelfChar != null && shelfChar in 1..5) {
                val rackIndex = (rackChar - 'A').coerceIn(0, repository.activeBatch.rackCount - 1)
                (rackIndex * repository.activeBatch.shelvesPerRack) + shelfChar
            } else null
        } else null

        // Check 2: Bag number in URL / payload
        val bagNum = bagNumFromShelf ?: when {
            trimmed.contains("/bag/") -> trimmed.substringAfter("/bag/").substringBefore("/").substringBefore("?").toIntOrNull()
            trimmed.contains("/scan/") -> trimmed.substringAfter("/scan/").substringBefore("/").substringBefore("?").toIntOrNull()
            trimmed.startsWith("BAG-", ignoreCase = true) -> trimmed.substringAfter("-").substringBefore("-").substringBefore("/").toIntOrNull()
            trimmed.startsWith("PLEURO-", ignoreCase = true) -> trimmed.substringAfterLast(":").filter { it.isDigit() }.toIntOrNull()
            else -> trimmed.filter { it.isDigit() }.toIntOrNull()
        } ?: return null

        val batchId = if (trimmed.contains("://") && trimmed.contains("/bag/")) {
            trimmed.substringAfter("://").substringBefore("/bag/")
        } else {
            repository.activeBatch.id
        }

        val qrType = if (isConfirm) QrType.Confirmation else QrType.Identification

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

fun decodeAndOrientBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val orientation = try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = android.media.ExifInterface(stream)
                exif.getAttributeInt(
                    android.media.ExifInterface.TAG_ORIENTATION,
                    android.media.ExifInterface.ORIENTATION_NORMAL
                )
            } ?: android.media.ExifInterface.ORIENTATION_NORMAL
        } catch (_: Exception) {
            android.media.ExifInterface.ORIENTATION_NORMAL
        }

        val rawBmp = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: return null

        val matrix = android.graphics.Matrix()
        when (orientation) {
            android.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            android.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            android.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return rawBmp
        }

        val rotatedBmp = Bitmap.createBitmap(rawBmp, 0, 0, rawBmp.width, rawBmp.height, matrix, true)
        if (rotatedBmp != rawBmp) {
            rawBmp.recycle()
        }
        rotatedBmp
    } catch (e: Exception) {
        try {
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) {
            null
        }
    }
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
    ScanQr,              // Step 1: Scan First QR Code (Bag Identification)
    CaptureMushroom,     // Step 2: Photograph the Mushroom Flush
    ScanQr2Confirm,      // Step 3: Scan Confirmation QR 2 Code
    ReviewConfirmation   // Step 4: Final Review & Save Record
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
            ScanStage.ScanQr -> 0.25f
            ScanStage.CaptureMushroom -> 0.50f
            ScanStage.ScanQr2Confirm -> 0.75f
            ScanStage.ReviewConfirmation -> 1.0f
        },
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "pipeline_progress"
    )

    DataCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 14.dp)
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
                            if (scanStage == ScanStage.ReviewConfirmation && isQr2Confirmed) ScanResult.ClassA.color
                            else if (scanStage == ScanStage.ScanQr2Confirm) InfoBlue
                            else ScanResult.ClassA.color
                        )
                )
                Text(
                    text = when (scanStage) {
                        ScanStage.ScanQr -> "STEP 1 OF 3 · SCAN QR 1 (ID)"
                        ScanStage.CaptureMushroom -> "STEP 2 OF 3 · SPECIMEN CAPTURE"
                        ScanStage.ScanQr2Confirm -> "STEP 3 OF 3 · SCAN CONFIRMATION QR 2"
                        ScanStage.ReviewConfirmation -> "STEP 3 OF 3 · LOCATION CONFIRMED & VERIFIED"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (scanStage == ScanStage.ScanQr2Confirm)
                            InfoBlue.copy(alpha = 0.12f)
                        else
                            ScanResult.ClassA.softBg
                    )
                    .border(
                        1.dp,
                        if (scanStage == ScanStage.ScanQr2Confirm)
                            InfoBlue.copy(alpha = 0.35f)
                        else
                            ScanResult.ClassA.color.copy(alpha = 0.3f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = when (scanStage) {
                        ScanStage.ScanQr -> "STEP 1 · READY"
                        ScanStage.CaptureMushroom -> "STEP 2 · LOCKED"
                        ScanStage.ScanQr2Confirm -> "STEP 3 · SCAN QR 2"
                        ScanStage.ReviewConfirmation -> "100% · VERIFIED"
                    },
                    color = if (scanStage == ScanStage.ScanQr2Confirm) InfoBlue else ScanResult.ClassA.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        // Animated Linear Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(SurfaceAlt)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(ScanResult.ClassA.color, InfoBlue)
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
                    .background(if (scanStage != ScanStage.ScanQr) ScanResult.ClassA.color.copy(alpha = 0.6f) else Border)
            )

            MilestoneNode(
                step = "2",
                title = "Specimen",
                status = when (scanStage) {
                    ScanStage.ScanQr -> "Pending"
                    ScanStage.CaptureMushroom -> "Bag #$targetBagNumber"
                    else -> "Done"
                },
                isActive = scanStage == ScanStage.CaptureMushroom,
                isDone = scanStage == ScanStage.ScanQr2Confirm || scanStage == ScanStage.ReviewConfirmation
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(1.dp)
                    .background(
                        if (scanStage == ScanStage.ReviewConfirmation && isQr2Confirmed) ScanResult.ClassA.color.copy(alpha = 0.6f)
                        else if (scanStage == ScanStage.ScanQr2Confirm) InfoBlue.copy(alpha = 0.6f)
                        else Border
                    )
            )

            MilestoneNode(
                step = "3",
                title = "QR 2 Confirm",
                status = when (scanStage) {
                    ScanStage.ScanQr, ScanStage.CaptureMushroom -> "Pending"
                    ScanStage.ScanQr2Confirm -> "Scan 2"
                    ScanStage.ReviewConfirmation -> "Verified"
                },
                isActive = scanStage == ScanStage.ScanQr2Confirm,
                isDone = scanStage == ScanStage.ReviewConfirmation && isQr2Confirmed
            )
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
                        isDone -> ScanResult.ClassA.color
                        isActive -> ScanResult.ClassA.softBg
                        else -> SurfaceAlt
                    }
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isDone || isActive -> ScanResult.ClassA.color
                        else -> Border
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isDone) "✓" else step,
                color = if (isDone) Color.White else if (isActive) ScanResult.ClassA.color else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }

        Column {
            Text(
                text = title,
                color = if (isActive || isDone) TextPrimary else TextMuted,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            )
            Text(
                text = status,
                color = if (isActive || isDone) ScanResult.ClassA.color else TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
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
    onAutoQrDetected: (String) -> Unit,
    onAutoPhotoCaptured: (Bitmap) -> Unit,
    onRetakePhoto: () -> Unit,
    onConfirmScan: () -> Unit,
    onRescanQr: () -> Unit,
    onChangeBagClick: () -> Unit,
    onManualConfirmQr2: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Sleek Modern Process Tracker with Linear Progress Bar
        ScanProcessTracker(
            scanStage = scanStage,
            targetBagNumber = targetBagNumber,
            targetShelfCode = targetShelfCode,
            isQr2Confirmed = isQr2Confirmed
        )

        // 2. Active Stage View / Live Auto Scanner
        when (scanStage) {
            ScanStage.ScanQr -> {
                LiveAutoScannerView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(370.dp),
                    isQrMode = true,
                    instructionTitle = "Step 1: Point Camera at Collar QR 1",
                    instructionSubtitle = "⚡ Automatically identifies bag number and shelf location",
                    onQrDetected = onAutoQrDetected
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceAlt),
                    border = BorderStroke(1.dp, Border)
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
                            LocationVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Current Target: Bag #$targetBagNumber (Shelf $targetShelfCode)",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        TextButton(onClick = onChangeBagClick) {
                            Text(
                                "Manual Select",
                                color = ScanResult.ClassA.color,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            ScanStage.CaptureMushroom -> {
                LiveAutoScannerView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(410.dp),
                    isQrMode = false,
                    instructionTitle = "Step 2: Frame Mushroom Flush (Bag #$targetBagNumber)",
                    instructionSubtitle = "Shelf $targetShelfCode locked · Aim camera & tap button below",
                    onCaptureImage = onAutoPhotoCaptured
                )

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
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

            ScanStage.ScanQr2Confirm -> {
                // Dedicated full camera viewfinder for Step 3: Confirmation QR 2
                LiveAutoScannerView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(370.dp),
                    isQrMode = true,
                    instructionTitle = "Step 3: Point at Confirmation QR 2 (Bag #$targetBagNumber)",
                    instructionSubtitle = "⚡ Verifies physical placement on Shelf $targetShelfCode before saving",
                    onQrDetected = onAutoQrDetected
                )

                // Info card showing target bag and captured specimen summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceAlt),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LocationVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Target: Bag #$targetBagNumber (Shelf $targetShelfCode)",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            val grade = inferenceSummary?.primaryGrade ?: ScanResult.ClassA
                            StatusPill(label = grade.name, color = grade.color)
                        }

                        Text(
                            text = "Specimen analyzed (${inferenceSummary?.detections?.size ?: 1} mushrooms). Scan the Confirmation QR (QR 2) sticker on this bag to verify location and unlock saving.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Controls: Retake photo or manual override if sticker is torn
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 46.dp),
                        onClick = onRetakePhoto,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Border)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RefreshVectorIcon(color = TextPrimary, modifier = Modifier.size(15.dp))
                            Text("↺ Retake Photo", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 46.dp),
                        onClick = onManualConfirmQr2,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Border)
                    ) {
                        Text("Sticker Damaged?", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            ScanStage.ReviewConfirmation -> {
                // Viewport Box showing captured image & YOLOv8 bounding boxes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.30f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1318))
                        .border(
                            width = 1.5.dp,
                            color = if (inferenceSummary?.hasContamination == true) ScanResult.Reject.color else EmeraldPrimary,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    if (capturedBitmap != null) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val img = capturedBitmap.asImageBitmap()
                            drawImage(
                                image = img,
                                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                            )

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
                    }

                    // Bottom Detection Result Overlay
                    if (inferenceSummary != null) {
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
                                        "No mushroom detected"
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
                }

                // Detection Breakdown in ReviewConfirmation
                if (inferenceSummary != null && inferenceSummary.detections.isNotEmpty()) {
                    val reviewBreakdown = inferenceSummary.detections.groupBy { it.label.replace('_', ' ') }
                        .entries.joinToString(" · ") { "${it.value.size} ${it.key}" }
                    DetectionBreakdownView(breakdownText = reviewBreakdown)
                }

                // Location Verified Confirmation Banner (Both QR 1 and QR 2 validated)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            CheckVectorIcon(color = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Column {
                            Text(
                                text = "QR 2 Verified · Location Confirmed",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Bag #$targetBagNumber confirmed on Shelf $targetShelfCode. Ready to save.",
                                color = ScanResult.ClassA.color,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Bottom confirmation and retake buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 50.dp),
                        onClick = onRescanQr,
                        enabled = !isScanning,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Border)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RefreshVectorIcon(color = TextPrimary, modifier = Modifier.size(15.dp))
                            Text(
                                "↺ Rescan",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Button(
                        modifier = Modifier
                            .weight(1.4f)
                            .defaultMinSize(minHeight = 50.dp),
                        onClick = onConfirmScan,
                        enabled = !isScanning && isQr2Confirmed,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            disabledContainerColor = SurfaceAlt
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CheckVectorIcon(color = Color.White, modifier = Modifier.size(15.dp))
                            Text(
                                text = "CONFIRM & SAVE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Error and conflict warning banners
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
                    WarningVectorIcon(color = ScanResult.Reject.color, modifier = Modifier.size(20.dp))
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
                    WarningVectorIcon(color = ScanResult.Reject.color, modifier = Modifier.size(26.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "QR 2 MISMATCH CONFLICT!",
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Target is Bag #$targetBagNumber (Shelf $targetShelfCode), but scanned Confirmation QR 2 belongs to Bag #$conflictingBagNumber!\nPlease point camera at Bag #$targetBagNumber.",
                            color = Color.White,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
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
                        "Point the camera at any QR code for instant auto-scanning. In Step 2, capture a clear photo of the mushroom caps.",
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
    var isQr2Confirmed by remember { mutableStateOf(false) }

    var targetBagNumber by remember { mutableIntStateOf(1) }
    val targetShelfCode = repository.shelfCodeForBag(targetBagNumber)
    var qrConflictDetected by remember { mutableStateOf(false) }
    var conflictingBagNumber by remember { mutableStateOf<Int?>(null) }
    var qrErrorMessage by remember { mutableStateOf<String?>(null) }

    var showBagSelectorDialog by remember { mutableStateOf(false) }
    var showManualConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf(false) }
    var successToastMessage by remember { mutableStateOf("") }

    var showDiseaseDialog by remember { mutableStateOf(false) }
    var activeDiseaseLabel by remember { mutableStateOf("") }
    var activeDiseaseConfidence by remember { mutableStateOf(0f) }

    val onAutoQrDetected: (String) -> Unit = { rawText ->
        when (scanStage) {
            ScanStage.ScanQr -> {
                val parsed = parseQrPayload(rawText, repository)
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
                    qrErrorMessage = "Unrecognized QR ($rawText). Expected Bag QR 1."
                }
            }
            ScanStage.ScanQr2Confirm -> {
                val parsed = parseQrPayload(rawText, repository)
                if (parsed != null) {
                    if (parsed.bagNumber != targetBagNumber) {
                        qrConflictDetected = true
                        conflictingBagNumber = parsed.bagNumber
                        isQr2Confirmed = false
                        qrErrorMessage = "❌ QR Mismatch: Scanned Bag #${parsed.bagNumber}, but target is Bag #$targetBagNumber. Please scan Confirmation QR 2 on Bag #$targetBagNumber."
                    } else if (parsed.qrType != QrType.Confirmation) {
                        qrErrorMessage = "⚠️ That is QR 1 (Identification). Please scan the second code: Confirmation QR 2 on Bag #$targetBagNumber."
                        isQr2Confirmed = false
                    } else {
                        isQr2Confirmed = true
                        qrConflictDetected = false
                        conflictingBagNumber = null
                        qrErrorMessage = null
                        scanStage = ScanStage.ReviewConfirmation
                    }
                } else {
                    qrErrorMessage = "Unrecognized QR code ($rawText). Expected Confirmation QR 2 for Bag #$targetBagNumber."
                }
            }
            ScanStage.ReviewConfirmation -> {
                val parsed = parseQrPayload(rawText, repository)
                if (parsed != null) {
                    if (parsed.bagNumber == targetBagNumber && parsed.qrType == QrType.Confirmation) {
                        isQr2Confirmed = true
                        qrConflictDetected = false
                        conflictingBagNumber = null
                        qrErrorMessage = null
                    } else if (parsed.bagNumber != targetBagNumber) {
                        qrConflictDetected = true
                        conflictingBagNumber = parsed.bagNumber
                        isQr2Confirmed = false
                        qrErrorMessage = "QR 2 mismatch: Scanned Bag #${parsed.bagNumber}, but target is Bag #$targetBagNumber."
                    }
                }
            }
            else -> {}
        }
    }

    val onAutoPhotoCaptured: (Bitmap) -> Unit = { bmp ->
        capturedBitmap = bmp
        isQr2Confirmed = false
        isScanning = true
        scope.launch(Dispatchers.IO) {
            val secondQr = decodeQrFromBitmap(bmp)
            val secondParsed = if (secondQr != null) parseQrPayload(secondQr, repository) else null
            if (secondParsed != null && secondParsed.bagNumber != targetBagNumber) {
                qrConflictDetected = true
                conflictingBagNumber = secondParsed.bagNumber
            } else {
                qrConflictDetected = false
                conflictingBagNumber = null
            }

            val summary = detector.detect(bmp)
            withContext(Dispatchers.Main) {
                inferenceSummary = summary
                isScanning = false
                // Enforce Step 3: Transition to dedicated Confirmation QR 2 viewfinder
                scanStage = ScanStage.ScanQr2Confirm

                if (summary.hasContamination) {
                    activeDiseaseLabel = summary.primaryLabel
                    activeDiseaseConfidence = summary.primaryConfidence
                    showDiseaseDialog = true
                    alarmManager.triggerContaminationAlarm()
                }
            }
        }
    }

    val onRetakePhoto = {
        capturedBitmap = null
        inferenceSummary = null
        qrConflictDetected = false
        conflictingBagNumber = null
        isQr2Confirmed = false
        scanStage = ScanStage.CaptureMushroom
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

    val onConfirmScan: () -> Unit = {
        val summary = inferenceSummary
        val bmp = capturedBitmap
        if (summary != null && bmp != null && isQr2Confirmed) {
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

                try {
                    com.example.pleurotech.data.sync.SyncWorker.syncImmediately(context)
                } catch (_: Exception) {
                }

                withContext(Dispatchers.Main) {
                    successToastMessage = "✅ Bag #$targetBagNumber saved to Shelf $targetShelfCode (${summary.detections.size} mushrooms)!"
                    showSuccessToast = true
                    capturedBitmap = null
                    inferenceSummary = null
                    qrConflictDetected = false
                    conflictingBagNumber = null
                    isQr2Confirmed = false
                    scanStage = ScanStage.ScanQr
                    targetBagNumber = if (targetBagNumber < repository.activeBatch.targetBagCount) targetBagNumber + 1 else 1
                }
            }
        } else if (!isQr2Confirmed) {
            qrErrorMessage = "⚠️ Confirmation QR 2 must be scanned to unlock and save Bag #$targetBagNumber."
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
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Select Bag to Inspect", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Choose bag location to lock shelf coordinates:", color = TextMuted, fontSize = 12.sp)

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
                                    .background(if (isSelected) EmeraldPrimary else SurfaceAlt)
                                    .border(1.dp, if (isSelected) EmeraldPrimary else Border, RoundedCornerShape(8.dp))
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
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showBagSelectorDialog = false }) {
                            Text("Cancel", color = TextMuted)
                        }
                    }
                }
            }
        }
    }

    if (showManualConfirmDialog) {
        Dialog(onDismissRequest = { showManualConfirmDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⚠️", fontSize = 24.sp)
                        Column {
                            Text(
                                "Manual Bag Confirmation",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Bag #$targetBagNumber (Shelf $targetShelfCode)",
                                color = EmeraldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "Use this only if the physical Confirmation QR 2 sticker on Bag #$targetBagNumber is torn, smudged, or missing.\n\nAre you sure you are physically at Bag #$targetBagNumber on Shelf $targetShelfCode?",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { showManualConfirmDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Text("Cancel", color = TextMuted)
                        }
                        Button(
                            modifier = Modifier.weight(1.3f),
                            onClick = {
                                isQr2Confirmed = true
                                qrErrorMessage = null
                                qrConflictDetected = false
                                conflictingBagNumber = null
                                scanStage = ScanStage.ReviewConfirmation
                                showManualConfirmDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Confirm Bag #$targetBagNumber", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            onAutoQrDetected = onAutoQrDetected,
            onAutoPhotoCaptured = onAutoPhotoCaptured,
            onRetakePhoto = onRetakePhoto,
            onConfirmScan = onConfirmScan,
            onRescanQr = onRescanQr,
            onChangeBagClick = { showBagSelectorDialog = true },
            onManualConfirmQr2 = { showManualConfirmDialog = true }
        )

        AiCommandCenter(
            brief = PleuroAssistant().brief(repository),
            compact = false
        )

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
    var isSingleMode by remember { mutableStateOf(false) }
    var selectedSingleBag by remember { mutableIntStateOf(1) }
    var startBag by remember { mutableIntStateOf(1) }
    var labelCount by remember { mutableIntStateOf(24) }
    var customText by remember { mutableStateOf("24") }
    var isPrinting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val batch = repository.activeBatch

    val labels = remember(isSingleMode, selectedSingleBag, startBag, labelCount, batch) {
        if (isSingleMode) {
            repository.bagLabels(startBag = selectedSingleBag, limit = 1)
        } else {
            repository.bagLabels(startBag = startBag, limit = labelCount)
        }
    }

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
                        "Generate physical QR labels. Attach each sticker to its grow bag collar.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
                StatusPill("${labels.size} Selected", ScanResult.ClassA.color)
            }

            // Mode Switcher: Single Bag vs Batch Range
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceAlt)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isSingleMode) ScanResult.ClassA.color.copy(alpha = 0.18f) else Color.Transparent)
                        .border(1.dp, if (!isSingleMode) ScanResult.ClassA.color else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { isSingleMode = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "📦 Batch Range",
                        color = if (!isSingleMode) ScanResult.ClassA.color else TextMuted,
                        fontWeight = if (!isSingleMode) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSingleMode) ScanResult.ClassA.color.copy(alpha = 0.18f) else Color.Transparent)
                        .border(1.dp, if (isSingleMode) ScanResult.ClassA.color else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { isSingleMode = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "🏷️ Single Bag",
                        color = if (isSingleMode) ScanResult.ClassA.color else TextMuted,
                        fontWeight = if (isSingleMode) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }

            if (isSingleMode) {
                /* ── Single Bag Selection Mode ── */
                SectionLabel("Select Specific Grow Bag")

                val singleShelf = repository.shelfCodeForBag(selectedSingleBag)

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
                            .background(if (selectedSingleBag > 1) Surface else SurfaceAlt)
                            .border(1.dp, if (selectedSingleBag > 1) Border else Color.Transparent, CircleShape)
                            .clickable(enabled = selectedSingleBag > 1) {
                                selectedSingleBag = (selectedSingleBag - 1).coerceAtLeast(1)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "−",
                            color = if (selectedSingleBag > 1) TextPrimary else TextMuted.copy(alpha = 0.35f),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Middle Bag & Shelf Indicator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Bag #${selectedSingleBag}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Shelf Location: $singleShelf",
                            color = ScanResult.ClassA.color,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    // Increment button (+)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (selectedSingleBag < batch.targetBagCount) Surface else SurfaceAlt)
                            .border(1.dp, if (selectedSingleBag < batch.targetBagCount) Border else Color.Transparent, CircleShape)
                            .clickable(enabled = selectedSingleBag < batch.targetBagCount) {
                                selectedSingleBag = (selectedSingleBag + 1).coerceAtMost(batch.targetBagCount)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+",
                            color = if (selectedSingleBag < batch.targetBagCount) TextPrimary else TextMuted.copy(alpha = 0.35f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Quick jump to individual bags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 2, 3, 5, 6, 7, 12, 24).forEach { bagNum ->
                        val isSel = selectedSingleBag == bagNum
                        val shelf = repository.shelfCodeForBag(bagNum)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ScanResult.ClassA.color.copy(alpha = 0.2f) else SurfaceAlt)
                                .border(1.dp, if (isSel) ScanResult.ClassA.color else Border, RoundedCornerShape(8.dp))
                                .clickable { selectedSingleBag = bagNum }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "#$bagNum",
                                    color = if (isSel) ScanResult.ClassA.color else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    shelf,
                                    color = if (isSel) ScanResult.ClassA.color else TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            } else {
                /* ── Batch Range Selection Mode ── */
                SectionLabel("Select Quantity / Rack Presets")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickPresets = listOf(12, 24, 48).filter { it < batch.targetBagCount } + listOf(batch.targetBagCount)
                    quickPresets.distinct().forEach { count ->
                        val isAll = count == batch.targetBagCount
                        val chipTitle = if (isAll) "All $count" else "$count Bags"
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
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                // Dynamic Rack Presets (Scrollable, adapts to added racks)
                val totalShelves = (batch.rackCount * batch.shelvesPerRack).coerceAtLeast(1)
                val bagsPerShelf = (batch.targetBagCount / totalShelves).coerceAtLeast(1)
                val bagsPerRack = (batch.shelvesPerRack * bagsPerShelf).coerceAtLeast(1)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..batch.rackCount).forEach { rIdx ->
                        val rackLetter = ('A'.code + rIdx - 1).toChar()
                        val rStart = (rIdx - 1) * bagsPerRack + 1
                        val rEnd = (rStart + bagsPerRack - 1).coerceAtMost(batch.targetBagCount)
                        val rCount = (rEnd - rStart + 1).coerceAtLeast(1)
                        val isSel = startBag == rStart && labelCount == rCount

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ScanResult.ClassA.color.copy(alpha = 0.2f) else SurfaceAlt)
                                .border(1.dp, if (isSel) ScanResult.ClassA.color else Border, RoundedCornerShape(8.dp))
                                .clickable {
                                    startBag = rStart
                                    labelCount = rCount
                                    customText = rCount.toString()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Rack $rackLetter ($rStart–$rEnd)",
                                color = if (isSel) ScanResult.ClassA.color else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center
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
                            "labels (from #$startBag)",
                            color = TextMuted,
                            fontSize = 12.sp,
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
            }

            Spacer(Modifier.height(4.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 50.dp),
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
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            "Generating Print Document...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrinterVectorIcon(color = Color.White, modifier = Modifier.size(20.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            val btnTitle = if (isSingleMode) {
                                "Print Bag #$selectedSingleBag Label"
                            } else {
                                "Print ${labels.size} Labels"
                            }
                            Text(
                                text = btnTitle,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            val subtitle = if (isSingleMode) {
                                repository.shelfCodeForBag(selectedSingleBag)
                            } else {
                                val endBag = (startBag + labels.size - 1).coerceAtLeast(startBag)
                                "Bags #$startBag to #$endBag"
                            }
                            Text(
                                text = subtitle,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
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
private fun RecordsSubTabIcon(tab: RecordsSubTab, color: Color) {
    val iconModifier = Modifier.size(15.dp)
    when (tab) {
        RecordsSubTab.History -> DocumentVectorIcon(color = color, modifier = iconModifier)
        RecordsSubTab.Analytics -> ChartVectorIcon(color = color, modifier = iconModifier)
        RecordsSubTab.Labels -> TagVectorIcon(color = color, modifier = iconModifier)
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
            color = AppBackground
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RecordsSubTab.values().forEach { tab ->
                    val isSelected = subTab == tab
                    val tabBg by animateColorAsState(
                        targetValue = if (isSelected) ScanResult.ClassA.softBg else Color.Transparent,
                        label = "recordsTabBg"
                    )
                    val tabTextColor by animateColorAsState(
                        targetValue = if (isSelected) ScanResult.ClassA.color else TextMuted,
                        label = "recordsTabTextColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(tabBg)
                            .clickable { onSubTabChanged(tab) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RecordsSubTabIcon(tab = tab, color = tabTextColor)
                            Text(
                                text = tab.title,
                                color = tabTextColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        AnimatedContent(
            targetState = subTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(200, delayMillis = 30)) +
                    slideInHorizontally(animationSpec = tween(240)) { if (targetState.ordinal > initialState.ordinal) 60 else -60 })
                    .togetherWith(fadeOut(animationSpec = tween(120)))
            },
            label = "RecordsSubTabTransition"
        ) { targetSubTab ->
            when (targetSubTab) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PageTitle("Scan History", "${repository.scans.size} total scans recorded")
            val scope = rememberCoroutineScope()
            OutlinedButton(
                onClick = {
                    scope.launch {
                        repository.syncWithCloud()
                    }
                },
                enabled = !repository.isSyncing,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (repository.isSyncing) Border else EmeraldPrimary.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = EmeraldPrimary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                if (repository.isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(13.dp),
                        strokeWidth = 2.dp,
                        color = EmeraldPrimary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Syncing...", fontSize = 12.sp)
                } else {
                    Text("🔄 Sync Cloud", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
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
                    OutlinedButton(
                        onClick = onExport,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ShareVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(15.dp))
                            Text("Export CSV", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
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
    val displayScore = if (counts.total > 0) "${"%.1f".format(reflection.readinessScore)}%" else "0%"
    var showBatchManager by remember { mutableStateOf(false) }

    if (showBatchManager) {
        BatchManagementDialog(repository = repository, onDismiss = { showBatchManager = false })
    }

    DataCard(
        shape = RoundedCornerShape(24.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
    ) {
        // Location / Batch Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceAlt)
                    .clickable { showBatchManager = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LocationVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(13.dp))
                    Text(
                        text = "Grow Room 1 · ${batch.name}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    ChevronRightIcon(color = TextMuted, modifier = Modifier.size(11.dp))
                }
            }

            Spacer(Modifier.width(8.dp))

            val (statusLabel, statusColor) = if (counts.total == 0) {
                "Awaiting Scans" to InfoBlue
            } else {
                reflection.readinessLabel to readinessColor(reflection.readinessScore)
            }

            StatusPill(statusLabel, statusColor)
        }

        Spacer(Modifier.height(4.dp))

        // Big Readout + Vector Mushroom illustration badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = displayScore,
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "${counts.total}/${batch.targetBagCount} bags · Started ${batch.startedDate}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(EmeraldSoft),
                contentAlignment = Alignment.Center
            ) {
                MushroomVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(28.dp))
            }
        }

        Spacer(Modifier.height(4.dp))

        // Row of mini capsules
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(ScanResult.ClassA.softBg)
                    .padding(vertical = 7.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SproutVectorIcon(color = ScanResult.ClassA.color, modifier = Modifier.size(12.dp))
                    Text("${counts.classA} Class A", color = ScanResult.ClassA.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(ScanResult.ClassB.softBg)
                    .padding(vertical = 7.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LeafVectorIcon(color = ScanResult.ClassB.color, modifier = Modifier.size(12.dp))
                    Text("${counts.classB} Class B", color = ScanResult.ClassB.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(ScanResult.Reject.softBg)
                    .padding(vertical = 7.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WarningVectorIcon(color = ScanResult.Reject.color, modifier = Modifier.size(12.dp))
                    Text("${counts.reject} Reject", color = ScanResult.Reject.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



@Composable
private fun TwinMetricCards(repository: PleuroTechRepository) {
    val counts = repository.counts()
    val classAPercent = if (counts.total > 0) (counts.classA * 100 / counts.total) else 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card 1: Today / Total Scanned
        DataCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ClipboardVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(14.dp))
                Text("Scanned", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Text(
                text = "${counts.total} bags",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Card 2: Premium Yield Rate
        DataCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LeafVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(14.dp))
                Text("Class A Yield", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Text(
                text = "$classAPercent%",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RackCategoryRow(
    shelves: List<ShelfSummary>,
    selectedRack: Char?,
    onSelectRack: (Char?) -> Unit,
    onSelectShelf: (ShelfSummary) -> Unit = {},
    isOwner: Boolean = false,
    onAddRack: () -> Unit = {}
) {
    val racks = remember(shelves) {
        shelves.mapNotNull { it.shelfCode.firstOrNull()?.uppercaseChar() }.distinct().sorted()
    }

    val activeRackShelves = remember(shelves, selectedRack) {
        if (selectedRack == null) shelves
        else shelves.filter { it.shelfCode.startsWith(selectedRack.toString(), ignoreCase = true) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Racks & Shelves",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (selectedRack != null) "Rack $selectedRack Filter Active (${activeRackShelves.size} shelves)" else "All ${racks.size} Racks · ${shelves.size} Shelves Active",
                    color = if (selectedRack != null) EmeraldPrimary else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (selectedRack != null) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (isOwner) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { onAddRack() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlusVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(11.dp))
                        Text("Add Rack", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" Pill
            val isAllSelected = selectedRack == null
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { onSelectRack(null) }
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isAllSelected) EmeraldPrimary else SurfaceAlt)
                        .border(1.dp, if (isAllSelected) EmeraldPrimary else Border, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    MushroomVectorIcon(color = if (isAllSelected) Color.White else EmeraldPrimary, modifier = Modifier.size(24.dp))
                }
                Text(
                    text = "All",
                    fontSize = 12.sp,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isAllSelected) EmeraldPrimary else TextMuted
                )
            }

            racks.forEach { rack ->
                val isSelected = selectedRack == rack
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { onSelectRack(if (isSelected) null else rack) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) EmeraldPrimary else SurfaceAlt)
                            .border(1.dp, if (isSelected) EmeraldPrimary else Border, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$rack",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                    Text(
                        text = "Rack $rack",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) EmeraldPrimary else TextMuted
                    )
                }
            }

            // Optional Add Rack circular button for owner
            if (isOwner) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { onAddRack() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SurfaceAlt)
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("➕", fontSize = 18.sp)
                    }
                    Text(
                        text = "+ Add",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldPrimary
                    )
                }
            }
        }

        // Active Rack Interactive Shelf Cards (Shown for All or individual rack!)
        if (activeRackShelves.isNotEmpty()) {
            DataCard(
                shape = RoundedCornerShape(18.dp),
                backgroundColor = SurfaceAlt.copy(alpha = 0.6f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedRack != null) "Rack $selectedRack Shelves (${activeRackShelves.size})" else "All Shelves Overview (${activeRackShelves.size})",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Tap shelf for details",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activeRackShelves.forEach { shelf ->
                        val hasRejects = shelf.reject > 0
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Surface)
                                .border(
                                    1.dp,
                                    if (hasRejects) ScanResult.Reject.color.copy(alpha = 0.5f) else Border,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectShelf(shelf) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = shelf.shelfCode,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasRejects) ScanResult.Reject.color else TextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${shelf.total} bags",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                if (hasRejects) {
                                    Text(
                                        text = "⚠️ ${shelf.reject} rej",
                                        color = ScanResult.Reject.color,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "✅ Optimal",
                                        color = ScanResult.ClassA.color,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TriAiAnalyticsPanel(repository: PleuroTechRepository) {
    val counts = repository.counts()
    val reflection = repository.preHarvestReflection()
    val alerts = repository.contaminationAlerts()

    val qualityIndex = if (counts.total > 0) ((counts.classA * 100 + counts.classB * 70) / counts.total).coerceIn(0, 100) else 0
    val timing = if (counts.total > 0) reflection.readinessScore else 0f
    val risk = if (counts.total > 0) {
        if (alerts.isNotEmpty()) 20 else (counts.reject * 100 / counts.total).coerceIn(0, 100)
    } else 0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("AI Analytics")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Metric 1: Quality Index
            DataCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                backgroundColor = SurfaceAlt,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SparkleVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(13.dp))
                    Text("Quality", color = TextMuted, fontSize = 11.sp, maxLines = 1)
                }
                Text("$qualityIndex%", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            // Metric 2: Flush Health
            DataCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                backgroundColor = SurfaceAlt,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SproutVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(13.dp))
                    Text("Health", color = TextMuted, fontSize = 11.sp, maxLines = 1)
                }
                Text(
                    text = if (timing == 0f) "0%" else "${"%.1f".format(timing)}%",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Metric 3: Contamination Risk
            DataCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                backgroundColor = SurfaceAlt,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShieldVectorIcon(color = if (risk > 0) ScanResult.Reject.color else EmeraldPrimary, modifier = Modifier.size(13.dp))
                    Text("Risk", color = TextMuted, fontSize = 11.sp, maxLines = 1)
                }
                Text("$risk%", color = if (risk > 0) ScanResult.Reject.color else TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
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
fun DetectionBreakdownView(
    breakdownText: String,
    modifier: Modifier = Modifier
) {
    if (breakdownText.isBlank() || breakdownText.trim().equals("null", ignoreCase = true)) return

    val rawItems = breakdownText.split("·", ",").map { it.trim() }.filter { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
    if (rawItems.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceAlt.copy(alpha = 0.65f))
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🍄 AI DETECTION BREAKDOWN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${rawItems.size} class${if (rawItems.size > 1) "es" else ""} detected",
                fontSize = 10.sp,
                color = TextMuted
            )
        }

        // Exact text breakdown line
        Text(
            text = "Breakdown: " + rawItems.joinToString(" · ") { it.replace("_", " ").trim() },
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            rawItems.forEach { rawItem ->
                val item = rawItem.replace("_", " ").trim()
                val lower = item.lowercase()
                val (chipColor, icon, actionHint) = when {
                    lower.contains("ready") && (lower.contains("class a") || lower.contains("class_a")) ->
                        Triple(ScanResult.ClassA.color, "🌾", "Prime • Harvest")
                    lower.contains("potential") && (lower.contains("class a") || lower.contains("class_a")) ->
                        Triple(Color(0xFF00B4D8), "⏳", "Developing • Monitor")
                    lower.contains("ready") && (lower.contains("class b") || lower.contains("class_b")) ->
                        Triple(ScanResult.ClassB.color, "🌾", "Standard • Harvest")
                    lower.contains("potential") && (lower.contains("class b") || lower.contains("class_b")) ->
                        Triple(Color(0xFFF59E0B), "⏳", "Developing • Monitor")
                    lower.contains("bacterial") || lower.contains("blotch") ->
                        Triple(ScanResult.Reject.color, "⚠️", "Bacterial • Isolate")
                    lower.contains("mold") || lower.contains("black_mold") ->
                        Triple(ScanResult.Reject.color, "🚨", "Black Mold • Remove")
                    lower.contains("reject") ->
                        Triple(ScanResult.Reject.color, "🗑️", "Deformed • Reject")
                    else ->
                        Triple(ScanResult.ClassA.color, "🍄", "Detected")
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(chipColor.copy(alpha = 0.14f))
                        .border(1.dp, chipColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(icon, fontSize = 13.sp)
                        Column {
                            Text(
                                text = item,
                                color = chipColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = actionHint,
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
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
    var selectedPricingTier by remember { mutableIntStateOf(1) } // 0: Farmgate, 1: Local Market, 2: Restaurant

    val (priceA, priceB, tierName) = when (selectedPricingTier) {
        0 -> Triple(180f, 130f, "Farmgate Wholesale")
        2 -> Triple(300f, 200f, "Restaurant / DTC")
        else -> Triple(250f, 160f, "Local Market / Retail")
    }

    val dynamicRevenue = (forecast.classAWeightKg * priceA) + (forecast.classBWeightKg * priceB)

    DataCard {
        // ── 1. Header & Flush Phase ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SectionLabel("Yield & Harvest Forecasting")
                    StatusPill(
                        forecast.flushCycleStage,
                        EmeraldPrimary
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = forecast.status,
                    color = forecastColor(forecast),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Agronomic projections for oyster mushroom fruiting bags based on grading momentum",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            StatusPill("${"%.0f".format(forecast.confidence * 100)}% AI confidence", forecastColor(forecast))
        }

        Spacer(Modifier.height(14.dp))

        // ── 2. Primary Agronomic KPI Grid (Weight, Revenue, Window, B.E.) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Est Weight
            DataCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                backgroundColor = SurfaceAlt,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MushroomVectorIcon(modifier = Modifier.size(16.dp))
                    Text("Est. Harvest Weight", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (forecast.estimatedWeightKg > 0f) "${"%.1f".format(forecast.estimatedWeightKg)} kg" else "0.0 kg",
                    color = EmeraldPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "A: ${"%.1f".format(forecast.classAWeightKg)}kg · B: ${"%.1f".format(forecast.classBWeightKg)}kg",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            // Projected Revenue
            DataCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                backgroundColor = SurfaceAlt,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagVectorIcon(color = InfoBlue, modifier = Modifier.size(14.dp))
                    Text("Gross Market Value", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (dynamicRevenue > 0f) "₱${"%,.0f".format(dynamicRevenue)}" else "₱0",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$tierName rate",
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Harvest Window
            DataCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                backgroundColor = SurfaceAlt,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SunVectorIcon(color = ScanResult.ClassB.color, modifier = Modifier.size(14.dp))
                    Text("Optimal Window", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = forecast.optimalHarvestWindow,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 17.sp
                )
                Text(
                    text = "Peak firmness before spore drop",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            // Biological Efficiency
            DataCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                backgroundColor = SurfaceAlt,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SproutVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(14.dp))
                    Text("Biological Efficiency", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${"%.1f".format(forecast.biologicalEfficiencyPercent)}%",
                    color = if (forecast.biologicalEfficiencyPercent >= 15f) EmeraldPrimary else TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Benchmark: 15%–25% per flush",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── 3. Bag Counts & Grade Mix Progress Bar ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Harvest Bag Composition", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(
                "Total: ${forecast.expectedScansTomorrow} bags (~${"%.1f".format(forecast.estimatedWeightKg)} kg)",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(6.dp))
        ForecastMixBar(forecast = forecast)
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Class A: ${forecast.expectedClassA} bags (${"%.1f".format(forecast.classAWeightKg)} kg)",
                color = ScanResult.ClassA.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Class B: ${forecast.expectedClassB} bags (${"%.1f".format(forecast.classBWeightKg)} kg)",
                color = ScanResult.ClassB.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            if (forecast.expectedReject > 0) {
                Text(
                    "Reject: ${forecast.expectedReject} bags",
                    color = ScanResult.Reject.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = Border)
        Spacer(Modifier.height(12.dp))

        // ── 4. Interactive Market Valuation Calculator ──
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Market Valuation Simulator", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Select market channel to simulate harvest proceeds", color = TextMuted, fontSize = 11.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "Farmgate (₱180/130)",
                    1 to "Local Retail (₱250/160)",
                    2 to "Restaurant (₱300/200)"
                ).forEach { (tier, label) ->
                    val isSelected = selectedPricingTier == tier
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) EmeraldPrimary else SurfaceAlt)
                            .border(1.dp, if (isSelected) EmeraldPrimary else Border, RoundedCornerShape(10.dp))
                            .clickable { selectedPricingTier = tier }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // ── 5. Multi-Day Harvest Planning Horizon ──
        if (forecast.projections.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("3-Day Harvest Schedule", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    val cumulativeKg = forecast.projections.sumOf { it.estimatedKg.toDouble() }.toFloat()
                    val cumulativePhp = forecast.projections.sumOf { it.estimatedRevenuePhp.toDouble() }.toFloat()
                    Text(
                        "3-Day Total: ~${"%.1f".format(cumulativeKg)} kg · ₱${"%,.0f".format(cumulativePhp)}",
                        color = EmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    forecast.projections.forEach { proj ->
                        DataCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = SurfaceAlt,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(10.dp)
                        ) {
                            Text(proj.dayLabel, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${"%.1f".format(proj.estimatedKg)} kg",
                                color = EmeraldPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${proj.expectedBags} bags",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                "₱${"%,.0f".format(proj.estimatedRevenuePhp)}",
                                color = InfoBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // ── 6. Contamination Loss Alert ──
        if (forecast.expectedReject > 0 && forecast.potentialLossPhp > 0f) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ScanResult.Reject.softBg)
                    .border(1.dp, ScanResult.Reject.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WarningVectorIcon(color = ScanResult.Reject.color, modifier = Modifier.size(20.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Biosecurity Containment Alert",
                        color = ScanResult.Reject.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "Isolating ${forecast.expectedReject} diseased bags prevents up to ₱${"%,.0f".format(forecast.potentialLossPhp)} in unharvested crop losses on adjacent shelves.",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // ── 7. Farmer Agronomic Action Checklist ──
        if (forecast.actionTips.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Farmer Harvest & Post-Harvest Guide", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                forecast.actionTips.forEach { tip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceAlt)
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when (tip.iconType) {
                                        "warning" -> ScanResult.Reject.softBg
                                        "water" -> InfoBlue.copy(alpha = 0.15f)
                                        else -> EmeraldSoft
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when (tip.iconType) {
                                "warning" -> WarningVectorIcon(color = ScanResult.Reject.color, modifier = Modifier.size(14.dp))
                                "water" -> DropVectorIcon(color = InfoBlue, modifier = Modifier.size(14.dp))
                                else -> LeafVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(14.dp))
                            }
                        }

                        Column(Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tip.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                StatusPill(
                                    label = tip.category,
                                    color = when (tip.iconType) {
                                        "warning" -> ScanResult.Reject.color
                                        "water" -> InfoBlue
                                        else -> EmeraldPrimary
                                    }
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(tip.description, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }
            }
        }
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
            .clip(RoundedCornerShape(20.dp))
            .background(result.softBg)
            .border(1.dp, result.color.copy(alpha = 0.32f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(result.color)
            )
            Text(
                text = result.displayName,
                color = result.color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = count.toString(),
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = result.description,
            color = TextMuted,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActiveAlert(latest: ScanRecord?) {
    val danger = latest?.result == ScanResult.Reject
    val color = if (danger) ScanResult.Reject.color else ScanResult.ClassA.color
    val title = if (danger) "Reject detected · Bag #${latest.bagNumber}" else "No active alerts"
    val body = if (danger) "Flag this bag for removal from the growing room." else "All scanned bags are within optimal quality standards."
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel("Active Alert")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.08f))
                .border(1.dp, color.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = body,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
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
            MushroomVectorIcon(color = EmeraldPrimary, modifier = Modifier.size(36.dp))
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
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceAlt.copy(alpha = 0.5f))
                .clickable(enabled = onSelectScan != null) { onSelectScan?.invoke(scan) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Rounded Thumbnail Container
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scan.finalResult.softBg)
                    .border(1.dp, scan.finalResult.color.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (scan.finalResult == ScanResult.Reject) {
                    WarningVectorIcon(color = ScanResult.Reject.color, modifier = Modifier.size(20.dp))
                } else {
                    MushroomVectorIcon(color = scan.finalResult.color, modifier = Modifier.size(22.dp))
                }
            }

            // Info Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shelf ${scan.shelfCode} · Bag #${scan.bagNumber}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (compact) scan.timeLabel else "${scan.dateLabel} ${scan.timeLabel}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ResultBadge(result = scan.finalResult, text = if (scan.verified) "V-${scan.finalResult.shortName}" else scan.finalResult.displayName)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Surface)
                            .border(1.dp, Border, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${(scan.confidence * 100).toInt()}% conf",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
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
        if (index != scans.lastIndex) Spacer(Modifier.height(8.dp))
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
fun ResultBadge(result: ScanResult, text: String = result.displayName) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(result.softBg)
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
fun MiniMetric(
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
fun DataCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Surface,
    borderColor: Color = Border,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        content()
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun PageTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.4).sp
        )
        Text(
            text = subtitle,
            color = TextMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun StatusPill(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.22f), RoundedCornerShape(50))
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false
        )
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
    score <= 0f -> InfoBlue
    score >= 85f -> ScanResult.ClassA.color
    score >= 70f -> InfoBlue
    score >= 55f -> ScanResult.ClassB.color
    else -> ScanResult.Reject.color
}

private fun forecastWeight(value: Int, total: Int): Float {
    return max(0.0001f, value.coerceAtLeast(0).toFloat() / total)
}

@Composable
fun shelfStatusColor(shelf: ShelfSummary): Color = when (shelf.status) {
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