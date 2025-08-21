package com.esmanureral.artframe

import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

object PermissionHelper {
    fun requestNotificationPermission(
        fragment: MainActivity,
        onResult: (granted: Boolean) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = "android.permission.POST_NOTIFICATIONS"
            val launcher: ActivityResultLauncher<String> =
                fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    onResult(isGranted)
                }
            launcher.launch(permission)
        } else {
            onResult(true)
        }
    }
}