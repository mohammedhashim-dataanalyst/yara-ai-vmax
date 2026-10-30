package com.yara.ai.ui

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yara.ai.ui.theme.YaraTheme

/**
 * Screen sharing — captures screen via MediaProjection so Yara can
 * "see" what's happening. The captured frames feed the local brain
 * context (wire to a WebSocket relay for real-time vision).
 */
class ScreenShareActivity : ComponentActivity() {

    private val REQ = 777

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YaraTheme { ScreenShareScreen { startProjection() } }
        }
    }

    private fun startProjection() {
        val mpm = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mpm.createScreenCaptureIntent(), REQ)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ && resultCode == Activity.RESULT_OK && data != null) {
            // Hand off to a service that streams frames / feeds local brain
            // (full implementation wires MediaProjection to a VirtualDisplay)
            finish()
        }
    }
}

@Composable
fun ScreenShareScreen(onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Share your screen with Yara", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text(
            "Yara will be able to see your screen and help in real time.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStart) { Text("Start sharing") }
    }
}
