package com.yara.ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yara.ai.data.MemoryStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsScreen(nav: NavController, store: MemoryStore) {
    val scope = rememberCoroutineScope()
    var pitch by remember { mutableStateOf(1.0f) }
    var rate by remember { mutableStateOf(1.0f) }
    var language by remember { mutableStateOf("auto") }
    var langMenu by remember { mutableStateOf(false) }

    val langs = mapOf(
        "auto" to "Auto (Malayalam + English)",
        "ml-IN" to "Malayalam",
        "en-IN" to "English (India)",
        "hi-IN" to "Hindi",
        "ta-IN" to "Tamil",
        "ar-SA" to "Arabic"
    )

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Voice & Language") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
        )
    }) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Language", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    ExposedDropdownMenuBox(expanded = langMenu, onExpandedChange = { langMenu = it }) {
                        OutlinedTextField(
                            value = langs[language] ?: language, onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(langMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = langMenu, onDismissRequest = { langMenu = false }) {
                            langs.forEach { (k, v) ->
                                DropdownMenuItem(text = { Text(v) }, onClick = {
                                    language = k; langMenu = false
                                    scope.launch { store.setFeature("lang_$k", true) }
                                })
                            }
                        }
                    }
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Voice tuning", fontWeight = FontWeight.Bold)
                    Text("Pitch: ${"%.2f".format(pitch)}", fontSize = 12.sp)
                    Slider(value = pitch, onValueChange = { pitch = it }, valueRange = 0.7f..1.4f)
                    Text("Speed: ${"%.2f".format(rate)}", fontSize = 12.sp)
                    Slider(value = rate, onValueChange = { rate = it }, valueRange = 0.6f..1.4f)
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Sound & emotion packs", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Built-in emote sounds (hehe, aww, hmm, sigh), voice modulation and emotion-based pacing are always on.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
