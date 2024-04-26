package com.example.flashlightenhancedversion.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.flashlightenhancedversion.configuration.Constant
import com.example.flashlightenhancedversion.core.CoreFragment
import com.example.flashlightenhancedversion.core.CoreLayout
import com.example.flashlightenhancedversion.lifecycleobserver.CameraPermissionLifecycleObserver
import com.example.flashlightenhancedversion.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

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

    /*private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e(Constant.APPLICATION_TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }*/

    @Composable
    override fun ComposeView() {
        super.ComposeView()
        HomeLayout()
    }
}

@Composable
fun HomeLayout() {
    CoreLayout(
        backgroundColor = Color.DarkGray,
        content = {

        }
    )
}

@Preview
@Composable
private fun PreviewHome() {
    HomeLayout()
}