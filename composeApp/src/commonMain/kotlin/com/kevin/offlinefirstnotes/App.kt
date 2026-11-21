package com.kevin.offlinefirstnotes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.CardColor
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.LabFooter
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTabs
import com.kevin.labs.ui.LabTheme
import com.kevin.labs.ui.Line
import com.kevin.labs.ui.Muted
import com.kevin.labs.ui.Paper
import com.kevin.labs.ui.RuleCard
import com.kevin.labs.ui.swipeBetweenTabs
import kotlinx.coroutines.delay

/** The UX question this lab asks; also its summary on the catalog. */
internal const val PITCH =
    "How does writing stay safe without a network? Compare online, offline, sync queue, background sync and conflict."

@Composable
fun App() {
    LabTheme(primary = Green, secondary = Blue) {
        OfflineFirstNotes()
    }
}

private data class SyncCopy(
    val mode: SyncMode,
    val tab: String,
    val title: String,
    val body: String,
    val rule: String,
    val accent: Color
)

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

/** How long the simulated server takes to answer a push. */
private const val PUSH_MILLIS = 1200L

@Composable
private fun OfflineFirstNotes() {
    // The tab follows the state of the note: writing, the network switch and sync move it.
    var notes by remember { mutableStateOf(preset(SyncMode.Queue)) }
    val onAction: (NotesAction) -> Unit = { notes = reduce(notes, it) }
    val selectedIndex = modes.indexOfFirst { it.mode == notes.mode }
    val selected = modes[selectedIndex]
    val selectMode: (Int) -> Unit = { onAction(NotesAction.JumpTo(modes[it].mode)) }

    LaunchedEffect(notes.syncing) {
        if (notes.syncing) {
            delay(PUSH_MILLIS)
            onAction(NotesAction.PushFinished)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LabHeader(
                title = "Offline First Notes",
                subtitle = PITCH,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        item {
            LabTabs(
                items = modes,
                label = { it.tab },
                accent = { it.accent },
                selectedIndex = selectedIndex,
                onSelected = selectMode,
                modifier = Modifier.padding(start = 20.dp),
                compact = true
            )
        }
        item {
            AnimatedContent(
                targetState = selected,
                modifier = Modifier.swipeBetweenTabs(selectedIndex, modes.size, selectMode),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "sync-mode"
            ) { copy ->
                NotesPanel(
                    copy = copy,
                    notes = notes,
                    onAction = onAction,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
        item {
            RuleCard(
                rule = selected.rule,
                modifier = Modifier.padding(horizontal = 20.dp),
                accent = selected.accent
            )
        }
        item { LabFooter(Modifier.padding(horizontal = 20.dp)) }
        item { Spacer(modifier = Modifier.height(2.dp)) }
    }
}

@Composable
private fun NotesPanel(
    copy: SyncCopy,
    notes: NotesState,
    onAction: (NotesAction) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = CardColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NoteEditor(copy, notes)
            NotesActions(copy, notes, onAction)
            SyncTimeline(copy, notes)
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
private fun NoteEditor(copy: SyncCopy, notes: NotesState) {
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
                SyncBadge(copy)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Trip notes",
                        color = Ink,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = statusText(notes),
                        color = Muted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (copy.mode == SyncMode.Offline) Gold else CardColor,
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
                text = notes.local,
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
                    trackColor = CardColor
                )
            }
        }
    }
}

/** What the user can do to the note in this state; every button feeds the reducer. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NotesActions(copy: SyncCopy, notes: NotesState, onAction: (NotesAction) -> Unit) {
    val buttonColors = ButtonDefaults.buttonColors(containerColor = copy.accent, contentColor = Color.White)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (notes.conflict != null) {
            Button(onClick = { onAction(NotesAction.Resolve(Resolution.KeepBoth)) }, colors = buttonColors) {
                Text("Keep both", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = { onAction(NotesAction.Resolve(Resolution.KeepMine)) }) {
                Text("Keep mine", color = Ink)
            }
            OutlinedButton(onClick = { onAction(NotesAction.Resolve(Resolution.KeepTheirs)) }) {
                Text("Keep theirs", color = Ink)
            }
        } else {
            Button(
                onClick = { onAction(NotesAction.Write(LOCAL_LINES[notes.local.lines().size % LOCAL_LINES.size])) },
                colors = buttonColors
            ) {
                Text("Write a line", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = { onAction(if (notes.online) NotesAction.GoOffline else NotesAction.GoOnline) }) {
                Text(if (notes.online) "Go offline" else "Go online", color = Ink)
            }
            if (notes.canSync) {
                OutlinedButton(onClick = { onAction(NotesAction.SyncNow) }) {
                    Text("Sync now", color = Ink)
                }
            }
            OutlinedButton(
                onClick = { onAction(NotesAction.RemoteWrite(REMOTE_LINES[notes.remote.lines().size % REMOTE_LINES.size])) }
            ) {
                Text("Edit on another device", color = Ink)
            }
        }
    }
}

@Composable
private fun SyncTimeline(copy: SyncCopy, notes: NotesState) {
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
            TimelineRow("Queue update", "${notes.pending.size} pending", copy.accent, active = notes.pending.isNotEmpty())
            TimelineRow("Push remote", syncLabel(copy.mode), copy.accent, active = copy.mode == SyncMode.Syncing)
            val conflict = notes.conflict
            if (conflict != null) {
                ConflictBox(conflict, copy.accent)
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
            .background(CardColor)
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
private fun ConflictBox(conflict: Conflict, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardColor)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        VersionCard("Mine", onlyIn(conflict.local, conflict.remote), accent, Modifier.weight(1f))
        VersionCard("Theirs", onlyIn(conflict.remote, conflict.local), Line, Modifier.weight(1f))
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
        Text(detail, color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun SyncBadge(copy: SyncCopy) {
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

private fun statusText(notes: NotesState): String =
    when (notes.mode) {
        SyncMode.Online -> "Online, saved locally first"
        SyncMode.Offline -> "Offline, safe on device"
        SyncMode.Queue -> "${changes(notes.pending.size)} queued for background sync"
        SyncMode.Syncing -> "Syncing ${changes(notes.pending.size)} in background"
        SyncMode.Conflict -> "Conflict needs review"
    }

private fun changes(count: Int): String = if (count == 1) "1 change" else "$count changes"

/** The first line of [version] that [other] does not have, to tell the two versions apart. */
private fun onlyIn(version: String, other: String): String {
    val otherLines = other.lines()
    return version.lines().firstOrNull { it !in otherLines } ?: "No new line"
}

private fun syncLabel(mode: SyncMode): String =
    when (mode) {
        SyncMode.Syncing -> "sending"
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
