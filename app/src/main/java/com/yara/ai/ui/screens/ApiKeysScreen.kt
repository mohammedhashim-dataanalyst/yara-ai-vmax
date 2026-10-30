package com.yara.ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yara.ai.data.ApiManager
import com.yara.ai.data.MemoryStore
import kotlinx.coroutines.launch

/**
 * API keys + provider management.
 * Adaptive auto-switch is on by default; user can set a manual primary.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeysScreen(nav: NavController, store: MemoryStore, api: ApiManager) {
    val scope = rememberCoroutineScope()
    var providers by remember { mutableStateOf(store.providers.toList()) }
    var primary by remember { mutableStateOf(api.manualPrimary.ifBlank { "Auto" }) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("AI Providers") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
        )
    }) { pad ->
        LazyColumn(Modifier.padding(pad).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Adaptive Auto-Switch", fontWeight = FontWeight.Bold)
                        Text(
                            "Yara automatically routes each message to the fastest working provider and skips any that hit rate limits. Set a manual primary below to override.",
                            fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            OutlinedTextField(
                                value = primary, onValueChange = {}, readOnly = true,
                                label = { Text("Primary provider") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("Auto (smart)") }, onClick = {
                                    primary = "Auto"; api.manualPrimary = ""; expanded = false
                                })
                                providers.forEach { p ->
                                    DropdownMenuItem(text = { Text(p.name) }, onClick = {
                                        primary = p.name; api.manualPrimary = p.name; expanded = false
                                    })
                                }
                            }
                        }
                    }
                }
            }

            items(providers) { p ->
                var key by remember { mutableStateOf(p.apiKey) }
                var enabled by remember { mutableStateOf(p.enabled) }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(p.name, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null,
                                        tint = if (p.apiKey.isNotBlank()) Color(0xFF4CAF50) else Color(0xFF666666),
                                        modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        if (p.apiKey.isNotBlank()) "key set · ${p.lastLatencyMs}ms" else "no key",
                                        fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(checked = enabled, onCheckedChange = {
                                enabled = it
                                p.enabled = it
                                scope.launch { store.saveProviders() }
                            })
                        }
                        OutlinedTextField(
                            value = key, onValueChange = { key = it; p.apiKey = it; scope.launch { store.saveProviders() } },
                            label = { Text("API key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}
