package com.example.pleurotech.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

private val AppBackground = Color(0xFFF4F7F1)
private val Surface = Color(0xFFFFFFFF)
private val SurfaceAlt = Color(0xFFEAF1E6)
private val Border = Color(0x1F1F3527)
private val TextPrimary = Color(0xFF1D2D22)
private val TextMuted = Color(0xFF607067)
private val InfoBlue = Color(0xFF2F80ED)

enum class AppTab(val title: String) {
    Dashboard("Dashboard"),
    Scan("Scan"),
    Labels("Labels"),
    History("History"),
    Analytics("Analytics"),
    Assistant("AI")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PleuroTechApp(repository: PleuroTechRepository) {
    var selectedTab by remember { mutableStateOf(AppTab.Dashboard) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = AppBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                AppTab.entries.forEach { tab ->
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
            AppHeader()
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
                    onPrint = {
                        val total = repository.bagLabels().size
                        scope.launch { snackbarHostState.showSnackbar("$total printable QR labels ready") }
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
            }
        }
    }
}

@Composable
private fun AppHeader() {
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
            Text(
                text = "Oyster mushroom farm intelligence",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
        StatusPill("Farm Ready", ScanResult.ClassA.color)
    }
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
private fun LabelsScreen(repository: PleuroTechRepository, onPrint: () -> Unit) {
    var labelCount by remember { mutableIntStateOf(24) }
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(12, 24, 48, batch.targetBagCount).forEach { count ->
                    AssistantChip(
                        title = if (count == batch.targetBagCount) "All" else count.toString(),
                        onClick = { labelCount = count }
                    )
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPrint,
                colors = ButtonDefaults.buttonColors(containerColor = ScanResult.ClassA.color)
            ) {
                Text("Prepare Printable QR Sheet")
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

    fun sendPrompt(prompt: String) {
        val cleaned = prompt.trim()
        if (cleaned.isEmpty()) return
        messages.add(AssistantMessage(MessageSender.User, cleaned))
        messages.add(AssistantMessage(MessageSender.Assistant, assistant.answer(cleaned, repository)))
        input = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PageTitle("AI Assistant", "Scan insights and quality recommendations")
        AssistantSummary(repository = repository)
        AiCommandCenter(brief = assistant.brief(repository), compact = false)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            assistant.suggestions().forEach { suggestion ->
                AssistantChip(title = suggestion.title, onClick = { sendPrompt(suggestion.prompt) })
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages.size) { index ->
                AssistantBubble(message = messages[index])
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Ask about rejects, trends, or latest scan") },
                singleLine = true
            )
            Button(
                onClick = { sendPrompt(input) },
                colors = ButtonDefaults.buttonColors(containerColor = InfoBlue)
            ) {
                Text("Ask")
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
private fun AssistantChip(title: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(SurfaceAlt)
            .border(1.dp, Border, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = title,
        color = TextPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun AssistantBubble(message: AssistantMessage) {
    val isUser = message.sender == MessageSender.User
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .clip(
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = if (isUser) 8.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 8.dp
                    )
                )
                .background(if (isUser) InfoBlue.copy(alpha = 0.22f) else Surface)
                .border(
                    1.dp,
                    if (isUser) InfoBlue.copy(alpha = 0.35f) else Border,
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = if (isUser) "You" else "Pleuro AI",
                color = if (isUser) InfoBlue else ScanResult.ClassA.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(message.text, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
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
        Text("PleuroTech", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        QrCode(payload = label.qrPayload, modifier = Modifier.size(104.dp))
        Text(label.batchId, color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 1)
        Text(
            "Bag ${label.bagNumber.toString().padStart(3, '0')} | Shelf ${label.shelfCode}",
            color = TextPrimary,
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
    Box(
        modifier = Modifier.size(98.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            drawArc(
                color = SurfaceAlt,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = qualityColor(score),
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
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        val maxTotal = max(1, trend.maxOfOrNull { it.total } ?: 1)
        val gap = 10.dp.toPx()
        val barWidth = (size.width - gap * (trend.size - 1)) / trend.size
        trend.forEachIndexed { index, day ->
            val h = size.height * day.total / maxTotal
            drawRoundRect(
                color = InfoBlue.copy(alpha = 0.45f),
                topLeft = Offset(index * (barWidth + gap), size.height - h),
                size = Size(barWidth, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}

@Composable
private fun RejectRateLineChart(trend: List<DailyTrend>) {
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
            drawCircle(color = Surface, radius = 6.dp.toPx(), center = point)
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

private fun forecastColor(forecast: HarvestForecast): Color = when {
    forecast.expectedScansTomorrow == 0 -> TextMuted
    forecast.projectedRejectRate >= 18f -> ScanResult.Reject.color
    forecast.projectedRejectRate >= 10f -> ScanResult.ClassB.color
    else -> ScanResult.ClassA.color
}

private fun readinessColor(score: Float): Color = when {
    score >= 85f -> ScanResult.ClassA.color
    score >= 70f -> InfoBlue
    score >= 55f -> ScanResult.ClassB.color
    else -> ScanResult.Reject.color
}

private fun forecastWeight(value: Int, total: Int): Float {
    return max(0.0001f, value.coerceAtLeast(0).toFloat() / total)
}

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
