package com.anakinyoo.qrscanners.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.anakinyoo.qrscanners.BuildConfig
import com.anakinyoo.qrscanners.R
import com.anakinyoo.qrscanners.camera.BarcodeScannerEngine
import com.anakinyoo.qrscanners.data.AppPreferences
import com.anakinyoo.qrscanners.data.HistoryStore
import com.anakinyoo.qrscanners.model.ContactData
import com.anakinyoo.qrscanners.model.EmailData
import com.anakinyoo.qrscanners.model.GeoData
import com.anakinyoo.qrscanners.model.HistoryRecord
import com.anakinyoo.qrscanners.model.QrType
import com.anakinyoo.qrscanners.model.ScanResultData
import com.anakinyoo.qrscanners.model.SmsData
import com.anakinyoo.qrscanners.model.WifiData
import com.anakinyoo.qrscanners.util.CurrentLocationProvider
import com.anakinyoo.qrscanners.util.LocationError
import com.anakinyoo.qrscanners.util.QrCodeGenerator
import com.anakinyoo.qrscanners.util.QrPayloadBuilder
import com.anakinyoo.qrscanners.util.ScanActionResolver
import com.anakinyoo.qrscanners.util.ShareUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab {
    SCAN, CREATE, HISTORY, SETTINGS
}

@Composable
fun MainApp(
    historyStore: HistoryStore,
    preferences: AppPreferences,
    onLocaleChange: (String) -> Unit
) {
    var showSplash by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(AppTab.SCAN) }
    var currentResult by remember { mutableStateOf<ScanResultData?>(null) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        showSplash = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp

        if (isWide) {
            // Adaptive wide layout: NavigationRail + Main Content
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Spacer(Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = selectedTab == AppTab.SCAN,
                        onClick = { selectedTab = AppTab.SCAN },
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(R.string.nav_scan)) },
                        label = { Text(stringResource(R.string.nav_scan)) },
                        modifier = Modifier.testTag("nav_rail_scan")
                    )
                    NavigationRailItem(
                        selected = selectedTab == AppTab.CREATE,
                        onClick = { selectedTab = AppTab.CREATE },
                        icon = { Icon(Icons.Default.AddBox, contentDescription = stringResource(R.string.nav_create)) },
                        label = { Text(stringResource(R.string.nav_create)) },
                        modifier = Modifier.testTag("nav_rail_create")
                    )
                    NavigationRailItem(
                        selected = selectedTab == AppTab.HISTORY,
                        onClick = { selectedTab = AppTab.HISTORY },
                        icon = { Icon(Icons.Default.History, contentDescription = stringResource(R.string.nav_history)) },
                        label = { Text(stringResource(R.string.nav_history)) },
                        modifier = Modifier.testTag("nav_rail_history")
                    )
                    NavigationRailItem(
                        selected = selectedTab == AppTab.SETTINGS,
                        onClick = { selectedTab = AppTab.SETTINGS },
                        icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings)) },
                        label = { Text(stringResource(R.string.nav_settings)) },
                        modifier = Modifier.testTag("nav_rail_settings")
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    TabContent(
                        tab = selectedTab,
                        historyStore = historyStore,
                        preferences = preferences,
                        onLocaleChange = onLocaleChange,
                        onResultFound = { currentResult = it }
                    )
                }
            }
        } else {
            // Compact mobile layout: Scaffold + Bottom NavigationBar
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == AppTab.SCAN,
                            onClick = { selectedTab = AppTab.SCAN },
                            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(R.string.nav_scan)) },
                            label = { Text(stringResource(R.string.nav_scan)) },
                            modifier = Modifier.testTag("nav_bottom_scan")
                        )
                        NavigationBarItem(
                            selected = selectedTab == AppTab.CREATE,
                            onClick = { selectedTab = AppTab.CREATE },
                            icon = { Icon(Icons.Default.AddBox, contentDescription = stringResource(R.string.nav_create)) },
                            label = { Text(stringResource(R.string.nav_create)) },
                            modifier = Modifier.testTag("nav_bottom_create")
                        )
                        NavigationBarItem(
                            selected = selectedTab == AppTab.HISTORY,
                            onClick = { selectedTab = AppTab.HISTORY },
                            icon = { Icon(Icons.Default.History, contentDescription = stringResource(R.string.nav_history)) },
                            label = { Text(stringResource(R.string.nav_history)) },
                            modifier = Modifier.testTag("nav_bottom_history")
                        )
                        NavigationBarItem(
                            selected = selectedTab == AppTab.SETTINGS,
                            onClick = { selectedTab = AppTab.SETTINGS },
                            icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings)) },
                            label = { Text(stringResource(R.string.nav_settings)) },
                            modifier = Modifier.testTag("nav_bottom_settings")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TabContent(
                        tab = selectedTab,
                        historyStore = historyStore,
                        preferences = preferences,
                        onLocaleChange = onLocaleChange,
                        onResultFound = { currentResult = it }
                    )
                }
            }
        }

        // Scan Result Bottom Sheet
        currentResult?.let { result ->
            ScanResultSheet(
                result = result,
                onDismiss = { currentResult = null },
                onToggleFavorite = {
                    val rec = HistoryRecord(
                        content = result.rawValue,
                        displayTitle = result.title,
                        qrType = result.type,
                        barcodeFormat = result.format,
                        isFavorite = true,
                        isGenerated = false
                    )
                    historyStore.addRecord(rec)
                }
            )
        }
    }

    // Animated Splash Screen Overlay
    AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(tween(150)),
        exit = fadeOut(tween(350)),
        modifier = Modifier.fillMaxSize()
    ) {
        SplashScreenView()
    }
}
}

@Composable
fun SplashScreenView() {
    val alphaAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .graphicsLayer {
                    alpha = alphaAnim
                }
                .padding(24.dp)
        ) {
            // App Icon Container
            Card(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .size(108.dp)
                    .testTag("splash_app_icon")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.qr_launcher_icon_1790218665405),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier
                            .size(92.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Name
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_app_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Author subtitle
            Text(
                text = stringResource(R.string.author_by),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_author_subtitle")
            )
        }
    }
}

@Composable
private fun TabContent(
    tab: AppTab,
    historyStore: HistoryStore,
    preferences: AppPreferences,
    onLocaleChange: (String) -> Unit,
    onResultFound: (ScanResultData) -> Unit
) {
    when (tab) {
        AppTab.SCAN -> ScannerScreen(
            historyStore = historyStore,
            preferences = preferences,
            onResultDetected = onResultFound
        )
        AppTab.CREATE -> GeneratorScreen(
            historyStore = historyStore
        )
        AppTab.HISTORY -> HistoryScreen(
            historyStore = historyStore,
            onSelectRecord = { record ->
                onResultFound(
                    ScanResultData(
                        rawValue = record.content,
                        format = record.barcodeFormat,
                        type = record.qrType,
                        title = record.displayTitle,
                        subtitle = record.content,
                        timestamp = record.timestamp
                    )
                )
            }
        )
        AppTab.SETTINGS -> SettingsScreen(
            preferences = preferences,
            historyStore = historyStore,
            onLocaleChange = onLocaleChange
        )
    }
}

/* ========================================================================
   SCANNER SCREEN
   ======================================================================== */
@Composable
fun ScannerScreen(
    historyStore: HistoryStore,
    preferences: AppPreferences,
    onResultDetected: (ScanResultData) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val defaultCamPref by preferences.defaultCamera.collectAsState()
    val vibratePref by preferences.vibrateOnScan.collectAsState()
    val soundPref by preferences.soundOnScan.collectAsState()
    val autoCopyPref by preferences.autoCopy.collectAsState()
    val autoOpenUrlPref by preferences.autoOpenUrl.collectAsState()

    var isFrontCamera by remember { mutableStateOf(defaultCamPref == "front") }
    var isFlashOn by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }
    var cameraInfo by remember { mutableStateOf<androidx.camera.core.CameraInfo?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProvider?.unbindAll()
            } catch (_: Exception) {}
        }
    }

    fun playFeedback() {
        if (vibratePref) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator?.vibrate(
                        VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(80)
                }
            } catch (_: Exception) {}
        }
        if (soundPref) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            } catch (_: Exception) {}
        }
    }

    val scannerEngine = remember {
        BarcodeScannerEngine { result ->
            playFeedback()
            if (autoCopyPref) {
                ScanActionResolver.copyToClipboard(context, result.rawValue)
            }
            // Add to history
            historyStore.addRecord(
                HistoryRecord(
                    content = result.rawValue,
                    displayTitle = result.title,
                    qrType = result.type,
                    barcodeFormat = result.format,
                    isGenerated = false
                )
            )
            when (result.type) {
                QrType.GEO -> {
                    val geo = ScanActionResolver.parseGeoOrNull(result.rawValue)
                    if (geo != null) {
                        // Location QR opens Google Maps immediately after a successful scan.
                        ScanActionResolver.openMap(context, geo)
                    } else {
                        onResultDetected(result)
                    }
                }
                QrType.URL -> {
                    if (ScanActionResolver.isGoogleMapsLink(result.rawValue)) {
                        ScanActionResolver.openMapLink(context, result.rawValue)
                    } else if (autoOpenUrlPref) {
                        ScanActionResolver.openBrowser(context, result.rawValue)
                    }
                    onResultDetected(result)
                }
                else -> onResultDetected(result)
            }
        }
    }

    LaunchedEffect(isFrontCamera, hasCameraPermission) {
        if (!hasCameraPermission) return@LaunchedEffect
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            val provider = withContext(Dispatchers.IO) {
                cameraProviderFuture.get()
            }
            cameraProvider = provider
            val preview = CameraPreview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(Dispatchers.Default.asExecutor(), scannerEngine)
                }
            val selector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            provider.unbindAll()
            val camera = provider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageAnalysis
            )
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
            if (isFlashOn) {
                try {
                    cameraControl?.enableTorch(true)
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scannerEngine.scanUri(
                context = context,
                uri = uri,
                onSuccess = { result ->
                    playFeedback()
                    if (autoCopyPref) {
                        ScanActionResolver.copyToClipboard(context, result.rawValue)
                    }
                    historyStore.addRecord(
                        HistoryRecord(
                            content = result.rawValue,
                            displayTitle = result.title,
                            qrType = result.type,
                            barcodeFormat = result.format,
                            isGenerated = false
                        )
                    )
                    if (result.type == QrType.GEO) {
                        val geo = ScanActionResolver.parseGeoOrNull(result.rawValue)
                        if (geo != null) {
                            // Gallery location QR follows the same direct-to-Google-Maps flow.
                            ScanActionResolver.openMap(context, geo)
                        } else {
                            onResultDetected(result)
                        }
                    } else if (result.type == QrType.URL &&
                        ScanActionResolver.isGoogleMapsLink(result.rawValue)
                    ) {
                        ScanActionResolver.openMapLink(context, result.rawValue)
                        onResultDetected(result)
                    } else {
                        onResultDetected(result)
                    }
                },
                onNotFound = {
                    errorMessage = context.getString(R.string.no_code_found_in_image)
                },
                onError = {
                    errorMessage = context.getString(R.string.image_scan_error)
                }
            )
        }
    }

    if (!hasCameraPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.camera_permission_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.camera_permission_desc),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.testTag("btn_grant_camera_permission")
            ) {
                Text(stringResource(R.string.btn_grant_permission))
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    galleryPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.testTag("btn_scan_gallery_fallback")
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.gallery_pick))
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // CameraX Preview View
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )

        // Viewfinder overlay & animated scanning laser
        ViewfinderOverlay()

        // Brand header and scan guidance over the live camera preview.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xCC0B1120),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.28f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Surface(
                color = Color(0xCC0B1120),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Text(
                    text = stringResource(R.string.point_camera_hint),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }

        // Bottom Controls: Flash, Zoom, Flip Camera, Gallery
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .background(Color(0xE60B1120), RoundedCornerShape(28.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFF38BDF8).copy(alpha = 0.24f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Zoom Slider & Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        val newZoom = (zoomRatio - 0.5f).coerceAtLeast(1f)
                        zoomRatio = newZoom
                        cameraControl?.setLinearZoom((newZoom - 1f) / 4f)
                    }
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out", tint = Color.White)
                }

                Slider(
                    value = zoomRatio,
                    onValueChange = { value ->
                        zoomRatio = value
                        cameraControl?.setLinearZoom((value - 1f) / 4f)
                    },
                    valueRange = 1f..5f,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("zoom_slider")
                )

                IconButton(
                    onClick = {
                        val newZoom = (zoomRatio + 0.5f).coerceAtMost(5f)
                        zoomRatio = newZoom
                        cameraControl?.setLinearZoom((newZoom - 1f) / 4f)
                    }
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in", tint = Color.White)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flash Toggle
                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        cameraControl?.enableTorch(isFlashOn)
                    },
                    modifier = Modifier
                        .background(Color(0xCC172338), CircleShape)
                        .size(52.dp)
                        .testTag("btn_flash_toggle")
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = stringResource(if (isFlashOn) R.string.flash_off else R.string.flash_on),
                        tint = if (isFlashOn) Color(0xFFFBBF24) else Color.White
                    )
                }

                // Gallery Picker
                IconButton(
                    onClick = {
                        galleryPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .background(Color(0xFF38BDF8), CircleShape)
                        .size(60.dp)
                        .testTag("btn_scan_gallery")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = stringResource(R.string.gallery_pick),
                        tint = Color(0xFF06202C),
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Switch Camera (Back / Front)
                IconButton(
                    onClick = {
                        isFrontCamera = !isFrontCamera
                        isFlashOn = false
                        try {
                            cameraControl?.enableTorch(false)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(52.dp)
                        .testTag("btn_switch_camera")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = stringResource(R.string.switch_camera),
                        tint = Color.White
                    )
                }
            }
        }

        // Error message snack/dialog
        errorMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text(stringResource(R.string.scan_title)) },
                text = { Text(msg) },
                confirmButton = {
                    TextButton(onClick = { errorMessage = null }) {
                        Text(stringResource(R.string.btn_clear))
                    }
                }
            )
        }
    }
}

@Composable
fun ViewfinderOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_progress"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val boxSize = minOf(canvasWidth * 0.72f, canvasHeight * 0.38f, 280.dp.toPx())
        val left = (canvasWidth - boxSize) / 2f
        val top = (canvasHeight - boxSize) / 2.6f
        val right = left + boxSize
        val bottom = top + boxSize
        val cornerRadius = 24.dp.toPx()
        val cornerLength = 36.dp.toPx()
        val strokeWidth = 4.dp.toPx()

        // Outside dark scrim
        drawRect(
            color = Color.Black.copy(alpha = 0.5f)
        )

        // Clear the viewfinder cut-out
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            blendMode = BlendMode.Clear
        )

        // Viewfinder borders / corner brackets
        val cornerColor = Color(0xFF38BDF8)

        // Top-left corner
        drawLine(cornerColor, Offset(left, top + cornerLength), Offset(left, top + cornerRadius), strokeWidth)
        drawLine(cornerColor, Offset(left + cornerRadius, top), Offset(left + cornerLength, top), strokeWidth)

        // Top-right corner
        drawLine(cornerColor, Offset(right - cornerLength, top), Offset(right - cornerRadius, top), strokeWidth)
        drawLine(cornerColor, Offset(right, top + cornerRadius), Offset(right, top + cornerLength), strokeWidth)

        // Bottom-left corner
        drawLine(cornerColor, Offset(left, bottom - cornerLength), Offset(left, bottom - cornerRadius), strokeWidth)
        drawLine(cornerColor, Offset(left + cornerRadius, bottom), Offset(left + cornerLength, bottom), strokeWidth)

        // Bottom-right corner
        drawLine(cornerColor, Offset(right - cornerLength, bottom), Offset(right - cornerRadius, bottom), strokeWidth)
        drawLine(cornerColor, Offset(right, bottom - cornerRadius), Offset(right, bottom - cornerLength), strokeWidth)

        // Animated red/cyan laser scanning line
        val laserY = top + (bottom - top) * laserProgress
        drawLine(
            color = Color(0xFF38BDF8),
            start = Offset(left + 12.dp.toPx(), laserY),
            end = Offset(right - 12.dp.toPx(), laserY),
            strokeWidth = 3.dp.toPx()
        )
    }
}

/* ========================================================================
   SCAN RESULT BOTTOM SHEET
   ======================================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultSheet(
    result: ScanResultData,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isFavorite by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type & Format Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${result.format} • ${result.type.name}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        onToggleFavorite()
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Display Title
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Raw Content Box
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = result.rawValue,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(20.dp))

            // Context Action Buttons based on type
            when (result.type) {
                QrType.URL -> {
                    Button(
                        onClick = { ScanActionResolver.openBrowser(context, result.rawValue) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_open_browser")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_open_browser))
                    }
                }
                QrType.WIFI -> {
                    val wifi = remember(result.rawValue) { ScanActionResolver.parseWifi(result.rawValue) }
                    Button(
                        onClick = { ScanActionResolver.connectWifi(context, wifi) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_connect_wifi")
                    ) {
                        Icon(Icons.Default.Wifi, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_connect_wifi))
                    }
                    if (wifi.password.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { ScanActionResolver.copyToClipboard(context, wifi.password) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Copy Wi-Fi Password: ${wifi.password}")
                        }
                    }
                }
                QrType.CONTACT -> {
                    val contact = remember(result.rawValue) { ScanActionResolver.parseContact(result.rawValue) }
                    Button(
                        onClick = { ScanActionResolver.addContact(context, contact) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_add_contact")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_add_contact))
                    }
                    if (contact.phone.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { ScanActionResolver.dialPhone(context, contact.phone) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("${stringResource(R.string.action_dial)} (${contact.phone})")
                        }
                    }
                }
                QrType.PHONE -> {
                    val phone = remember(result.rawValue) { ScanActionResolver.parsePhone(result.rawValue) }
                    Button(
                        onClick = { ScanActionResolver.dialPhone(context, phone) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_dial")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_dial))
                    }
                }
                QrType.EMAIL -> {
                    val email = remember(result.rawValue) { ScanActionResolver.parseEmail(result.rawValue) }
                    Button(
                        onClick = { ScanActionResolver.sendEmail(context, email) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_send_email")
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_send_email))
                    }
                }
                QrType.SMS -> {
                    val sms = remember(result.rawValue) { ScanActionResolver.parseSms(result.rawValue) }
                    Button(
                        onClick = { ScanActionResolver.sendSms(context, sms.number, sms.message) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_send_sms")
                    ) {
                        Icon(Icons.Default.Sms, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_send_sms))
                    }
                }
                QrType.GEO -> {
                    val geo = remember(result.rawValue) { ScanActionResolver.parseGeoOrNull(result.rawValue) }
                    if (geo != null) {
                        Button(
                            onClick = { ScanActionResolver.openMap(context, geo) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("action_open_map")
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.action_open_map))
                        }
                    }
                }
                QrType.TEXT -> {
                    Button(
                        onClick = { ScanActionResolver.searchWeb(context, result.rawValue) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_search_web")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_search_web))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Common actions: Copy & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { ScanActionResolver.copyToClipboard(context, result.rawValue) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_copy")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_copy))
                }

                OutlinedButton(
                    onClick = { ShareUtils.shareText(context, result.rawValue) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_share")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_share))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* ========================================================================
   GENERATOR SCREEN
   ======================================================================== */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    historyStore: HistoryStore
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(QrType.TEXT) }

    // Input States
    var textInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var wifiSecurity by remember { mutableStateOf("WPA") }
    var wifiHidden by remember { mutableStateOf(false) }

    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactOrg by remember { mutableStateOf("") }
    var contactTitle by remember { mutableStateOf("") }

    var emailAddress by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }

    var phoneNumber by remember { mutableStateOf("") }

    var smsNumber by remember { mutableStateOf("") }
    var smsMessage by remember { mutableStateOf("") }

    var geoLat by remember { mutableStateOf("") }
    var geoLng by remember { mutableStateOf("") }
    var isLocating by remember { mutableStateOf(false) }

    val loadCurrentLocation: () -> Unit = {
        isLocating = true
        CurrentLocationProvider.requestCurrentLocation(
            context = context,
            onResult = { location ->
                isLocating = false
                if (location != null) {
                    geoLat = String.format(Locale.US, "%.6f", location.latitude)
                    geoLng = String.format(Locale.US, "%.6f", location.longitude)
                    android.widget.Toast.makeText(
                        context,
                        context.getString(R.string.location_loaded),
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    android.widget.Toast.makeText(
                        context,
                        context.getString(R.string.location_unavailable),
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onError = { error ->
                isLocating = false
                val messageRes = when (error) {
                    LocationError.PERMISSION_REQUIRED -> R.string.location_permission_denied
                    LocationError.SERVICES_DISABLED -> R.string.location_services_disabled
                    LocationError.UNAVAILABLE -> R.string.location_unavailable
                }
                android.widget.Toast.makeText(
                    context,
                    context.getString(messageRes),
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted =
            grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            loadCurrentLocation()
        } else {
            android.widget.Toast.makeText(
                context,
                context.getString(R.string.location_permission_denied),
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    val requestCurrentLocation: () -> Unit = {
        if (CurrentLocationProvider.hasPermission(context)) {
            loadCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Color options
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    val colorPalettes = listOf(
        Pair(android.graphics.Color.BLACK, android.graphics.Color.WHITE),
        Pair(android.graphics.Color.rgb(2, 132, 199), android.graphics.Color.WHITE), // Ocean Blue
        Pair(android.graphics.Color.rgb(124, 58, 237), android.graphics.Color.WHITE), // Deep Purple
        Pair(android.graphics.Color.rgb(5, 150, 105), android.graphics.Color.WHITE)  // Emerald
    )

    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentPayload by remember { mutableStateOf("") }

    fun generateCode() {
        val payload = when (selectedType) {
            QrType.TEXT -> QrPayloadBuilder.buildText(textInput)
            QrType.URL -> QrPayloadBuilder.buildUrl(urlInput)
            QrType.WIFI -> QrPayloadBuilder.buildWifi(
                WifiData(ssid = wifiSsid, password = wifiPassword, securityType = wifiSecurity, isHidden = wifiHidden)
            )
            QrType.CONTACT -> QrPayloadBuilder.buildContact(
                ContactData(name = contactName, phone = contactPhone, email = contactEmail, organization = contactOrg, title = contactTitle)
            )
            QrType.EMAIL -> QrPayloadBuilder.buildEmail(
                EmailData(address = emailAddress, subject = emailSubject, body = emailBody)
            )
            QrType.PHONE -> QrPayloadBuilder.buildPhone(phoneNumber)
            QrType.SMS -> QrPayloadBuilder.buildSms(
                SmsData(number = smsNumber, message = smsMessage)
            )
            QrType.GEO -> QrPayloadBuilder.buildGeo(geoLat, geoLng)
        }

        if (payload.isNotBlank()) {
            currentPayload = payload
            val palette = colorPalettes[selectedColorIndex]
            val bmp = QrCodeGenerator.generateQrBitmap(
                content = payload,
                size = 512,
                foregroundColor = palette.first,
                backgroundColor = palette.second
            )
            generatedBitmap = bmp
            // Save to history
            val displayTitle = when (selectedType) {
                QrType.TEXT -> payload.take(30)
                QrType.URL -> payload
                QrType.WIFI -> "Wi-Fi: $wifiSsid"
                QrType.CONTACT -> if (contactName.isNotBlank()) contactName else "Contact"
                QrType.EMAIL -> "Email: $emailAddress"
                QrType.PHONE -> "Phone: $phoneNumber"
                QrType.SMS -> "SMS: $smsNumber"
                QrType.GEO -> "Location: $geoLat, $geoLng"
            }
            historyStore.addRecord(
                HistoryRecord(
                    content = payload,
                    displayTitle = displayTitle,
                    qrType = selectedType,
                    barcodeFormat = "QR Code",
                    isGenerated = true
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_title), fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type Selector Chips (FlowRow / Wrap Adaptive Layout)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QrType.values().forEach { type ->
                    val label = when (type) {
                        QrType.TEXT -> stringResource(R.string.type_text)
                        QrType.URL -> stringResource(R.string.type_url)
                        QrType.WIFI -> stringResource(R.string.type_wifi)
                        QrType.CONTACT -> stringResource(R.string.type_contact)
                        QrType.EMAIL -> stringResource(R.string.type_email)
                        QrType.PHONE -> stringResource(R.string.type_phone)
                        QrType.SMS -> stringResource(R.string.type_sms)
                        QrType.GEO -> stringResource(R.string.type_geo)
                    }
                    FilterChip(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            if (type == QrType.GEO) {
                                requestCurrentLocation()
                            }
                        },
                        label = { Text(label) },
                        modifier = Modifier.testTag("chip_type_${type.name.lowercase()}")
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Dynamic Form Fields according to selectedType
            when (selectedType) {
                QrType.TEXT -> {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text(stringResource(R.string.label_text)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_text"),
                        minLines = 3
                    )
                }
                QrType.URL -> {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text(stringResource(R.string.label_url)) },
                        placeholder = { Text("https://example.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_url")
                    )
                }
                QrType.WIFI -> {
                    OutlinedTextField(
                        value = wifiSsid,
                        onValueChange = { wifiSsid = it },
                        label = { Text(stringResource(R.string.label_ssid)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_wifi_ssid")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = wifiPassword,
                        onValueChange = { wifiPassword = it },
                        label = { Text(stringResource(R.string.label_password)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_wifi_password")
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.label_security))
                        Row {
                            listOf("WPA", "WEP", "None").forEach { sec ->
                                FilterChip(
                                    selected = wifiSecurity == sec,
                                    onClick = { wifiSecurity = sec },
                                    label = { Text(sec) },
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
                QrType.CONTACT -> {
                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text(stringResource(R.string.label_name)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_contact_name")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text(stringResource(R.string.label_phone)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_contact_phone")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text(stringResource(R.string.label_email)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_contact_email")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contactOrg,
                        onValueChange = { contactOrg = it },
                        label = { Text(stringResource(R.string.label_org)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                QrType.EMAIL -> {
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text(stringResource(R.string.label_email)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_email_address")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailSubject,
                        onValueChange = { emailSubject = it },
                        label = { Text(stringResource(R.string.label_subject)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailBody,
                        onValueChange = { emailBody = it },
                        label = { Text(stringResource(R.string.label_message)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                QrType.PHONE -> {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(stringResource(R.string.label_phone)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_phone_number")
                    )
                }
                QrType.SMS -> {
                    OutlinedTextField(
                        value = smsNumber,
                        onValueChange = { smsNumber = it },
                        label = { Text(stringResource(R.string.label_phone)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_sms_number")
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = smsMessage,
                        onValueChange = { smsMessage = it },
                        label = { Text(stringResource(R.string.label_message)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                QrType.GEO -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = geoLat,
                            onValueChange = { geoLat = it },
                            label = { Text(stringResource(R.string.label_lat)) },
                            modifier = Modifier.weight(1f).testTag("input_geo_lat")
                        )
                        OutlinedTextField(
                            value = geoLng,
                            onValueChange = { geoLng = it },
                            label = { Text(stringResource(R.string.label_lng)) },
                            modifier = Modifier.weight(1f).testTag("input_geo_lng")
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = requestCurrentLocation,
                        enabled = !isLocating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_current_location")
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isLocating) {
                                stringResource(R.string.location_finding)
                            } else {
                                stringResource(R.string.use_current_location)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Color Customization Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.color_customization),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0284C7),
                        Color(0xFF7C3AED),
                        Color(0xFF059669)
                    ).forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColorIndex == index) 3.dp else 1.dp,
                                    color = if (selectedColorIndex == index) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColorIndex = index
                                    if (currentPayload.isNotBlank()) generateCode()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColorIndex == index) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Generate Button
            Button(
                onClick = { generateCode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_generate_qr")
            ) {
                Icon(Icons.Default.QrCode, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.btn_generate))
            }

            // QR Preview Area
            generatedBitmap?.let { bmp ->
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.qr_preview),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Generated QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { ShareUtils.shareQrBitmap(context, bmp) },
                                modifier = Modifier.weight(1f).testTag("btn_share_qr_image")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.btn_share_qr))
                            }
                            OutlinedButton(
                                onClick = { ScanActionResolver.copyToClipboard(context, currentPayload) },
                                modifier = Modifier.weight(1f).testTag("btn_copy_generated_payload")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.action_copy))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/* ========================================================================
   HISTORY SCREEN
   ======================================================================== */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    historyStore: HistoryStore,
    onSelectRecord: (HistoryRecord) -> Unit
) {
    val context = LocalContext.current
    val allRecords by historyStore.records.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Scanned, 2: Created, 3: Favorites
    var showClearDialog by remember { mutableStateOf(false) }

    val filteredRecords = remember(allRecords, searchQuery, selectedFilter) {
        allRecords.filter { record ->
            val matchesFilter = when (selectedFilter) {
                1 -> !record.isGenerated
                2 -> record.isGenerated
                3 -> record.isFavorite
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                record.displayTitle.contains(searchQuery, ignoreCase = true) ||
                record.content.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title), fontWeight = FontWeight.Bold) },
                actions = {
                    if (allRecords.isNotEmpty()) {
                        IconButton(
                            onClick = { ShareUtils.exportHistoryCsv(context, allRecords) },
                            modifier = Modifier.testTag("btn_export_history")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = stringResource(R.string.btn_export))
                        }
                        IconButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.testTag("btn_clear_history")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.clear_history))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("history_search_input"),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Chips (FlowRow / Adaptive Wrap)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    stringResource(R.string.filter_all),
                    stringResource(R.string.filter_scanned),
                    stringResource(R.string.filter_created),
                    stringResource(R.string.filter_favorites)
                ).forEachIndexed { index, title ->
                    FilterChip(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        label = { Text(title) },
                        modifier = Modifier.testTag("history_filter_$index")
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = if (selectedFilter == 3) stringResource(R.string.empty_favorites) else stringResource(R.string.empty_history),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredRecords, key = { it.id }) { record ->
                        HistoryItemCard(
                            record = record,
                            dateStr = dateFormat.format(Date(record.timestamp)),
                            onSelect = { onSelectRecord(record) },
                            onToggleFavorite = { historyStore.toggleFavorite(record.id) },
                            onDelete = { historyStore.deleteRecord(record.id) },
                            onShare = { ShareUtils.shareText(context, record.content) },
                            onCopy = { ScanActionResolver.copyToClipboard(context, record.content) }
                        )
                    }
                }
            }
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text(stringResource(R.string.clear_history_confirm_title)) },
                text = { Text(stringResource(R.string.clear_history_confirm_msg)) },
                confirmButton = {
                    Button(
                        onClick = {
                            historyStore.clearAll()
                            showClearDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.btn_clear))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    record: HistoryRecord,
    dateStr: String,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("history_item_${record.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon based on type
            val icon = when (record.qrType) {
                QrType.URL -> Icons.Default.OpenInBrowser
                QrType.WIFI -> Icons.Default.Wifi
                QrType.CONTACT -> Icons.Default.Person
                QrType.EMAIL -> Icons.Default.Email
                QrType.PHONE -> Icons.Default.Phone
                QrType.SMS -> Icons.Default.Sms
                QrType.GEO -> Icons.Default.LocationOn
                QrType.TEXT -> Icons.Default.QrCode
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.displayTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (record.isGenerated) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = "Created",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = record.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$dateStr • ${record.barcodeFormat}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Quick actions: Favorite, Share, Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (record.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (record.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/* ========================================================================
   SETTINGS SCREEN
   ======================================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: AppPreferences,
    historyStore: HistoryStore,
    onLocaleChange: (String) -> Unit
) {
    val themeMode by preferences.themeMode.collectAsState()
    val appLanguage by preferences.appLanguage.collectAsState()
    val vibrateOnScan by preferences.vibrateOnScan.collectAsState()
    val soundOnScan by preferences.soundOnScan.collectAsState()
    val autoCopy by preferences.autoCopy.collectAsState()
    val autoOpenUrl by preferences.autoOpenUrl.collectAsState()
    val defaultCamera by preferences.defaultCamera.collectAsState()

    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // SECTION: APPEARANCE
            Text(
                text = stringResource(R.string.section_appearance),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Theme selector
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.theme_mode),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("system", stringResource(R.string.theme_system)),
                            Pair("light", stringResource(R.string.theme_light)),
                            Pair("dark", stringResource(R.string.theme_dark))
                        ).forEach { (mode, label) ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { preferences.setThemeMode(mode) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("theme_chip_$mode")
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Language selector
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.app_language),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("system", stringResource(R.string.lang_system)),
                            Pair("en", stringResource(R.string.lang_en)),
                            Pair("th", stringResource(R.string.lang_th))
                        ).forEach { (lang, label) ->
                            FilterChip(
                                selected = appLanguage == lang,
                                onClick = {
                                    preferences.setAppLanguage(lang)
                                    onLocaleChange(lang)
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag("lang_chip_$lang")
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // SECTION: SCAN BEHAVIOR
            Text(
                text = stringResource(R.string.section_behavior),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Vibrate setting
                    SettingToggleRow(
                        icon = Icons.Default.Vibration,
                        title = stringResource(R.string.setting_vibrate),
                        subtitle = stringResource(R.string.setting_vibrate_desc),
                        checked = vibrateOnScan,
                        onCheckedChange = { preferences.setVibrateOnScan(it) },
                        testTag = "switch_vibrate"
                    )

                    // Sound setting
                    SettingToggleRow(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = stringResource(R.string.setting_sound),
                        subtitle = stringResource(R.string.setting_sound_desc),
                        checked = soundOnScan,
                        onCheckedChange = { preferences.setSoundOnScan(it) },
                        testTag = "switch_sound"
                    )

                    // Auto Copy setting
                    SettingToggleRow(
                        icon = Icons.Default.ContentCopy,
                        title = stringResource(R.string.setting_auto_copy),
                        subtitle = stringResource(R.string.setting_auto_copy_desc),
                        checked = autoCopy,
                        onCheckedChange = { preferences.setAutoCopy(it) },
                        testTag = "switch_auto_copy"
                    )

                    // Auto Open URL setting
                    SettingToggleRow(
                        icon = Icons.Default.OpenInBrowser,
                        title = stringResource(R.string.setting_auto_open_url),
                        subtitle = stringResource(R.string.setting_auto_open_url_desc),
                        checked = autoOpenUrl,
                        onCheckedChange = { preferences.setAutoOpenUrl(it) },
                        testTag = "switch_auto_open_url"
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Default Camera
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.setting_default_camera),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("back", stringResource(R.string.cam_back)),
                            Pair("front", stringResource(R.string.cam_front))
                        ).forEach { (cam, label) ->
                            FilterChip(
                                selected = defaultCamera == cam,
                                onClick = { preferences.setDefaultCamera(cam) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("camera_chip_$cam")
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // SECTION: ABOUT & PRIVACY
            Text(
                text = stringResource(R.string.section_about),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.about_privacy),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.about_privacy_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.app_version_label, BuildConfig.VERSION_NAME),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Clear All History Button
            OutlinedButton(
                onClick = { showClearConfirm = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_settings_clear_history")
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.clear_history))
            }

            Spacer(Modifier.height(36.dp))
        }

        if (showClearConfirm) {
            AlertDialog(
                onDismissRequest = { showClearConfirm = false },
                title = { Text(stringResource(R.string.clear_history_confirm_title)) },
                text = { Text(stringResource(R.string.clear_history_confirm_msg)) },
                confirmButton = {
                    Button(
                        onClick = {
                            historyStore.clearAll()
                            showClearConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.btn_clear))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirm = false }) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}
