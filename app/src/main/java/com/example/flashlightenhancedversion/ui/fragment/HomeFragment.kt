package com.example.flashlightenhancedversion.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.flashlightenhancedversion.configuration.Constant
import com.example.flashlightenhancedversion.core.CoreFragment
import com.example.flashlightenhancedversion.core.CoreLayout
import com.example.flashlightenhancedversion.lifecycleobserver.CameraPermissionLifecycleObserver
import com.example.flashlightenhancedversion.ui.component.SolidButton
import com.example.flashlightenhancedversion.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Build a Camera Android App in Jetpack Compose Using CameraX
 * https://betterprogramming.pub/build-a-camera-android-app-in-jetpack-compose-using-camerax-4d5dfbfbe8ec
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


    @Composable
    override fun ComposeView() {
        super.ComposeView()
        HomeLayout()
    }
}

@Composable
fun HomeLayout(
    enableTorch: Boolean = false,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    var enableTorch by remember { mutableStateOf(false) }
    val cameraSelector = remember {
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }
    var camera: Camera? by remember { mutableStateOf(null) }

    val imageAnalysis: ImageAnalysis = remember {
        ImageAnalysis.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(1280, 720),
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER
                        )
                    )
                    .build()
            )
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    LaunchedEffect(
        key1 = enableTorch,
        block = { camera?.cameraControl?.enableTorch(enableTorch) }
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
            Log.d(Constant.APPLICATION_TAG, "HomeLayout: ${ex.printStackTrace()}")
        }
    }

    CoreLayout(
        backgroundColor = Color.DarkGray,
        content = {
            Column {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5F)
                )

                SolidButton(
                    onClick = {
                        enableTorch = !enableTorch
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