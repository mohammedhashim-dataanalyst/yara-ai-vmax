package com.yara.ai.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yara.ai.data.MemoryStore
import com.yara.ai.features.FeatureCatalog
import com.yara.ai.features.YaraFeature
import kotlinx.coroutines.launch

/**
 * Settings — English-only UI, data-driven from FeatureCatalog (120+ toggles).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(nav: NavController, store: MemoryStore) {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var features by remember { mutableStateOf(FeatureCatalog.all) }

    val filtered = remember(query) {
        if (query.isBlank()) features
        else features.filter {
            it.title.contains(query, true) || it.description.contains(query, true)
        }
    }
    val grouped = filtered.groupBy { it.group }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { nav.navigate("api") }) { Icon(Icons.Default.VpnKey, null) }
                    IconButton(onClick = { nav.navigate("dashboard") }) { Icon(Icons.Default.Dashboard, null) }
                }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                placeholder = { Text("Search 120+ features…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )

            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // quick links
                item {
                    QuickLinksRow(nav)
                    Divider(Modifier.padding(vertical = 8.dp))
                }

                grouped.forEach { (group, items) ->
                    item {
                        Text(
                            group,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    items(items) { f ->
                        FeatureRow(f, store)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickLinksRow(nav: NavController) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        QuickLink("Personalities", Icons.Default.Face) { nav.navigate("traits") }
        QuickLink("API Keys", Icons.Default.VpnKey) { nav.navigate("api") }
        QuickLink("Memories", Icons.Default.Psychology) { nav.navigate("memories") }
        QuickLink("Voice", Icons.Default.RecordVoiceOver) { nav.navigate("voice") }
        QuickLink("About", Icons.Default.Info) { nav.navigate("about") }
    }
}

@Composable
fun QuickLink(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 4.dp) {
            Icon(icon, label, modifier = Modifier.padding(12.dp))
        }
        Text(label, fontSize = 10.sp)
    }
}

@Composable
fun FeatureRow(f: YaraFeature, store: MemoryStore) {
    val scope = rememberCoroutineScope()
    var on by remember { mutableStateOf(store.isFeatureOn(f.key, f.defaultOn)) }

    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(f.title, fontWeight = FontWeight.Medium)
                Text(f.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = on,
                onCheckedChange = {
                    on = it
                    scope.launch { store.setFeature(f.key, it) }
                }
            )
        }
    }
}
