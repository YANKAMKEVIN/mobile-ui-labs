package com.kevin.offlinefirstnotes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Green,
            secondary = Blue,
            background = Paper,
            surface = Color.White
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Paper
        ) {
            OfflineFirstNotes()
        }
    }
}

private enum class SyncMode {
    Online,
    Offline,
    Queue,
    Syncing,
    Conflict
}

private data class SyncCopy(
    val mode: SyncMode,
    val tab: String,
    val title: String,
    val body: String,
    val rule: String,
    val accent: Color
)

private val Paper = Color(0xFFF4F0E8)
private val Ink = Color(0xFF101615)
private val Muted = Color(0xFF697169)
private val Line = Color(0xFFE1D8CA)
private val Green = Color(0xFF0D6B57)
private val Blue = Color(0xFF2563A8)
private val Gold = Color(0xFFB76F16)
private val Coral = Color(0xFFC8523A)
private val Violet = Color(0xFF6D56B3)

private val modes = listOf(
    SyncCopy(
        mode = SyncMode.Online,
        tab = "Online",
        title = "Save to local first",
        body = "Even online, the user should feel the note is safe immediately.",
        rule = "Offline-first starts before the device goes offline.",
        accent = Green
    ),
    SyncCopy(
        mode = SyncMode.Offline,
        tab = "Offline",
        title = "Keep editing available",
        body = "The UI confirms local safety and avoids making network loss feel fatal.",
        rule = "Offline mode should reduce anxiety, not remove core actions.",
        accent = Gold
    ),
    SyncCopy(
        mode = SyncMode.Queue,
        tab = "Queue",
        title = "Expose pending work",
        body = "Queued operations give users confidence that their changes are remembered.",
        rule = "A sync queue is part of the interface, not only infrastructure.",
        accent = Blue
    ),
    SyncCopy(
        mode = SyncMode.Syncing,
        tab = "Syncing",
        title = "Sync without blocking",
        body = "Progress appears near the data while editing remains available.",
        rule = "Background sync should be visible, calm and non-modal.",
        accent = Coral
    ),
    SyncCopy(
        mode = SyncMode.Conflict,
        tab = "Conflict",
        title = "Resolve with context",
        body = "When two edits compete, show the difference and keep both versions legible.",
        rule = "Conflict resolution is a writing experience, not an error screen.",
        accent = Violet
    )
)

@Composable
private fun OfflineFirstNotes() {
    var selectedIndex by rememberSaveable { mutableStateOf(2) }
    val selected = modes[selectedIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Header(Modifier.padding(horizontal = 20.dp)) }
        item {
            ModeTabs(
                selectedIndex = selectedIndex,
                onSelected = { selectedIndex = it },
                modifier = Modifier.padding(start = 20.dp)
            )
        }
        item {
            AnimatedContent(
                targetState = selected,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "sync-mode"
            ) { copy ->
                NotesPanel(
                    copy = copy,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
        item {
            RuleCard(
                copy = selected,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(2.dp)) }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Offline First Notes",
            color = Ink,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "A Compose lab for local-first writing, sync queues, background progress and conflict recovery.",
            color = Muted,
            lineHeight = 23.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ModeTabs(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        modes.forEachIndexed { index, mode ->
            val selected = selectedIndex == index
            Surface(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelected(index) }),
                shape = RoundedCornerShape(50),
                color = if (selected) mode.accent else Color.White,
                border = BorderStroke(1.dp, if (selected) mode.accent else Line)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    text = mode.tab,
                    color = if (selected) Color.White else Ink,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    lineHeight = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
private fun NotesPanel(copy: SyncCopy, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NoteEditor(copy)
            SyncTimeline(copy)
            Text(
                text = copy.title,
                color = Ink,
                fontWeight = FontWeight.Black,
                fontSize = 25.sp,
                lineHeight = 30.sp
            )
            Text(
                text = copy.body,
                color = Muted,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun NoteEditor(copy: SyncCopy) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatusDot(copy)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Trip notes",
                        color = Ink,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = statusText(copy.mode),
                        color = Muted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (copy.mode == SyncMode.Offline) Gold else Color.White,
                    border = BorderStroke(1.dp, if (copy.mode == SyncMode.Offline) Gold else Line)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        text = if (copy.mode == SyncMode.Offline) "Local" else "Saved",
                        color = if (copy.mode == SyncMode.Offline) Color.White else Green,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
            Text(
                text = "Museum list, quiet cafe, evening walk near the river. Add receipts later.",
                color = Ink,
                lineHeight = 22.sp
            )
            if (copy.mode == SyncMode.Syncing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = copy.accent,
                    trackColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun SyncTimeline(copy: SyncCopy) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(234.dp),
        shape = RoundedCornerShape(22.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TimelineRow("Local write", "instant", Green, active = true)
            TimelineRow("Queue update", "2 pending", copy.accent, active = copy.mode != SyncMode.Online)
            TimelineRow("Push remote", syncLabel(copy.mode), copy.accent, active = copy.mode == SyncMode.Syncing)
            if (copy.mode == SyncMode.Conflict) {
                ConflictBox(copy.accent)
            } else {
                TimelineRow("Confirm", confirmLabel(copy.mode), Line, active = copy.mode == SyncMode.Online)
            }
        }
    }
}

@Composable
private fun TimelineRow(label: String, meta: String, color: Color, active: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(17.dp)
                .clip(CircleShape)
                .background(if (active) color else Line)
        )
        Text(
            text = label,
            color = Ink,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = meta,
            color = Muted,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ConflictBox(accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VersionCard("Local", "Cafe note", accent, Modifier.weight(1f))
        VersionCard("Remote", "Train note", Line, Modifier.weight(1f))
    }
}

@Composable
private fun VersionCard(label: String, detail: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = if (color == Line) 0.45f else 0.16f))
            .padding(9.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Ink, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Text(detail, color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun StatusDot(copy: SyncCopy) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(copy.accent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (copy.mode) {
                SyncMode.Online -> "ON"
                SyncMode.Offline -> "OFF"
                SyncMode.Queue -> "Q"
                SyncMode.Syncing -> "S"
                SyncMode.Conflict -> "!"
            },
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun RuleCard(copy: SyncCopy, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Ink
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(copy.accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "Sync rule",
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = copy.rule,
                color = Color(0xFFECE7DC),
                lineHeight = 22.sp
            )
        }
    }
}

private fun statusText(mode: SyncMode): String =
    when (mode) {
        SyncMode.Online -> "Online, saved locally first"
        SyncMode.Offline -> "Offline, safe on device"
        SyncMode.Queue -> "Queued for background sync"
        SyncMode.Syncing -> "Syncing in background"
        SyncMode.Conflict -> "Conflict needs review"
    }

private fun syncLabel(mode: SyncMode): String =
    when (mode) {
        SyncMode.Syncing -> "48%"
        SyncMode.Conflict -> "blocked"
        SyncMode.Offline -> "waiting"
        SyncMode.Queue -> "ready"
        SyncMode.Online -> "done"
    }

private fun confirmLabel(mode: SyncMode): String =
    when (mode) {
        SyncMode.Online -> "done"
        SyncMode.Offline -> "later"
        SyncMode.Queue -> "pending"
        SyncMode.Syncing -> "soon"
        SyncMode.Conflict -> "review"
    }
