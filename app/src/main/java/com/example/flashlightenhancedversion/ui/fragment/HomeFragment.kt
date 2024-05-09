package com.example.flashlightenhancedversion.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.flashlightenhancedversion.R
import com.example.flashlightenhancedversion.backgroundwork.FlashlightTileService
import com.example.flashlightenhancedversion.configuration.Constant
import com.example.flashlightenhancedversion.core.CoreFragment
import com.example.flashlightenhancedversion.core.CoreLayout
import com.example.flashlightenhancedversion.lifecycleobserver.CameraPermissionLifecycleObserver
import com.example.flashlightenhancedversion.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Build a Camera Android App in Jetpack Compose Using CameraX
 * https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
 * https://developer.android.com/codelabs/camerax-getting-started#0
 */
@AndroidEntryPoint
class HomeFragment : CoreFragment() {

    private lateinit var cameraPermissionObserver: CameraPermissionLifecycleObserver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserverCameraPermission()
        requestCameraPermission()
    }

    /*************************************************
     * for request camera permission
     */
    private var showPopupOnePermission: Boolean by mutableStateOf(false)
    private val callbackOnePermission = object : CameraPermissionLifecycleObserver.Callback {
        override fun openRationaleDialog() {
            showPopupOnePermission = true
        }
    }

    private fun setupObserverCameraPermission() {
        cameraPermissionObserver = CameraPermissionLifecycleObserver(
            registry = requireActivity().activityResultRegistry,
            activity = requireActivity(),
            callback = callbackOnePermission
        )
        lifecycle.addObserver(cameraPermissionObserver)
    }


    /*************************************************
     * for using Camera X
     */
    private fun requestCameraPermission() {
        val isAccessed: Boolean = PermissionUtil.isCameraAccessible(context = requireContext())
        if (isAccessed) return
        cameraPermissionObserver.launcher.launch(android.Manifest.permission.CAMERA)
    }

    // In your Activity or Fragment
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(Constant.APPLICATION_TAG, "onReceive - isDoorLocked")
            if (intent.action == "Flashlight") {
                val isDoorLocked = intent.getBooleanExtra("isDoorLocked", false)
                // Now you can use isDoorLocked in your Activity or Fragment
                Log.d(Constant.APPLICATION_TAG, "onReceive - isDoorLocked: $isDoorLocked")
            }
        }
    }

    // Don't forget to register and unregister your receiver
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(
                receiver,
                IntentFilter("TileService"),
                Context.RECEIVER_NOT_EXPORTED
            )
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(receiver)
    }

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        HomeLayout()
    }
}

@Composable
fun HomeLayout() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    var enableFlashlight by remember { mutableStateOf(false) }
    val cameraSelector = remember {
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }
    var camera: Camera? by remember { mutableStateOf(null) }


    LaunchedEffect(
        key1 = enableFlashlight,
        block = { camera?.cameraControl?.enableTorch(enableFlashlight) }
    )

    LaunchedEffect(Unit) {
        try {
            val cameraProvider = context.getCameraProvider()
            val preview = androidx.camera.core.Preview.Builder().build()


            cameraProvider.unbindAll()
            val bindingCamera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )

            camera = bindingCamera
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    CoreLayout(
        backgroundColor = Color.DarkGray,
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .size(0.5.dp)
                        .alpha(0F)
                )

                IconButton(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .size(200.dp),
                    onClick = { enableFlashlight = !enableFlashlight },
                    content = {
                        Image(
                            contentDescription = null,
                            painter =
                                if (enableFlashlight)
                                    painterResource(id = R.drawable.ic_turn_on)
                                else
                                    painterResource(id = R.drawable.ic_turn_off),
                        )
                    }
                )
            }
        }
    )
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

@Preview
@Composable
private fun PreviewHome() {
    HomeLayout()
}