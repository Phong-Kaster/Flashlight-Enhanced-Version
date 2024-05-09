package com.example.flashlightenhancedversion.backgroundwork

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.flashlightenhancedversion.FlashlightApplication
import com.example.flashlightenhancedversion.ui.activity.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [TileService] that controls a cabin's "Door Lock" state.
 */
@Singleton
class FlashlightTileService
@Inject
constructor(
    private val context: FlashlightApplication
) : TileService() {

    /**
     * The cabin's door lock state. In the real app, you might want to use a service to
     * retrieve/update this state.
     */
    var isDoorLocked = false

    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()
    }

    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
    }

    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        super.onStopListening()
    }

    // Called when the user taps on your tile in an active or inactive state.
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onClick() {
        super.onClick()

        val destinationIntent = Intent(context, MainActivity::class.java)
        destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1896,
            destinationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        startActivityAndCollapse(pendingIntent)
    }

    // Called when the user removes your tile.
    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}