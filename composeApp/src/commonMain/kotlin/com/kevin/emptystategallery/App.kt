package com.kevin.emptystategallery

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTheme
import kotlinx.coroutines.delay

@Composable
fun App() {
    LabTheme(primary = Color(0xFF1B5E4A), secondary = Color(0xFF6750A4)) {
        EmptyStateGallery()
    }
}

/** What the secondary button does in the demo. */
private sealed interface SecondaryOutcome {
    /** Jump straight to content, with a note explaining where it comes from. */
    data class ShowContent(val note: String) : SecondaryOutcome
    /** Stay on the empty state and acknowledge the choice. */
    data class Acknowledge(val message: String) : SecondaryOutcome
}

private data class EmptyStatePattern(
    val title: String,
    val context: String,
    /** User-facing headline, as it would ship in the product. */
    val headline: String,
    /** User-facing body copy. */
    val message: String,
    val primaryAction: String,
    val secondaryAction: String,
    val secondaryOutcome: SecondaryOutcome,
    /** Items shown once the screen has content again. */
    val filledItems: List<String>,
    /** Design guidance for this pattern (shown in the UX note, not in the product copy). */
    val designTip: String,
    val uxNote: String,
    val icon: ImageVector,
    val accent: Color
)

private val patterns = listOf(
    EmptyStatePattern(
        title = "First run",
        context = "No content yet",
        headline = "No projects yet",
        message = "Create your first project to start collecting ideas. It only takes a minute.",
        primaryAction = "Create item",
        secondaryAction = "View sample",
        secondaryOutcome = SecondaryOutcome.ShowContent("Sample project. Edit or delete it anytime."),
        filledItems = listOf("Weekend app idea", "Portfolio refresh", "Reading list"),
        designTip = "Invite the user to create the first item. Keep the copy optimistic and action-oriented.",
        uxNote = "Best when the user has not done anything wrong.",
        icon = Icons.Filled.Add,
        accent = Color(0xFF1B5E4A)
    ),
    EmptyStatePattern(
        title = "No results",
        context = "Search returned nothing",
        headline = "No results for \"kotlin crash\"",
        message = "Try fewer words or remove the \"Last 7 days\" filter.",
        primaryAction = "Clear search",
        secondaryAction = "Adjust filters",
        secondaryOutcome = SecondaryOutcome.ShowContent("Filter \"Last 7 days\" removed: 3 older matches."),
        filledItems = listOf("Kotlin crash on startup", "Crash report template", "Kotlin coroutines notes"),
        designTip = "Repeat the query context and offer a softer next step instead of blaming the search.",
        uxNote = "Best after user input, filters, or sorting.",
        icon = Icons.Filled.Search,
        accent = Color(0xFF2F5DA8)
    ),
    EmptyStatePattern(
        title = "Offline",
        context = "Data cannot refresh",
        headline = "You're offline",
        message = "Your saved items are still here. New updates will load when you're back online.",
        primaryAction = "Retry",
        secondaryAction = "Use offline mode",
        secondaryOutcome = SecondaryOutcome.ShowContent("Offline mode: showing items saved on this device."),
        filledItems = listOf("Trip checklist", "Grocery list", "Meeting notes"),
        designTip = "Explain what is still available and make retry obvious without hiding cached content.",
        uxNote = "Best when the app can partially work.",
        icon = Icons.Filled.Refresh,
        accent = Color(0xFF8A5A00)
    ),
    EmptyStatePattern(
        title = "Permission",
        context = "Feature is locked",
        headline = "Find nearby events",
        message = "Allow location access to see what's happening around you. We never share your position.",
        primaryAction = "Allow access",
        secondaryAction = "Maybe later",
        secondaryOutcome = SecondaryOutcome.Acknowledge("No problem. You can turn it on later in Settings."),
        filledItems = listOf("Jazz night · 0.4 km", "Farmers market · 1.1 km", "Open-air cinema · 2.3 km"),
        designTip = "Connect the permission to the benefit before sending the user to system settings.",
        uxNote = "Best before a feature needs camera, location, files, or notifications.",
        icon = Icons.Filled.Lock,
        accent = Color(0xFF6D3FB8)
    ),
    EmptyStatePattern(
        title = "Error",
        context = "Something failed",
        headline = "We couldn't load your inbox",
        message = "Your drafts are safe. Try again in a moment.",
        primaryAction = "Try again",
        secondaryAction = "Contact support",
        secondaryOutcome = SecondaryOutcome.Acknowledge("Report sent. Support will reply by email."),
        filledItems = listOf("Welcome to the team", "Your weekly summary", "Invoice #2481"),
        designTip = "Keep the error calm, preserve the user's work, and provide a recovery path.",
        uxNote = "Best when the user cannot fix the state alone.",
        icon = Icons.Filled.Warning,
        accent = Color(0xFFB3261E)
    )
)

@Composable
private fun EmptyStateGallery() {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val selected = patterns[selectedIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            LabHeader(
                title = "Empty State Gallery",
                subtitle = "Five tiny UI patterns for moments where the screen has no content."
            )
        }
        item {
            PatternPicker(
                selectedIndex = selectedIndex,
                onSelected = { selectedIndex = it }
            )
        }
        item {
            AnimatedContent(
                targetState = selected,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "empty-state-preview"
            ) { pattern ->
                EmptyStatePreview(pattern = pattern)
            }
        }
        item {
            UxNotes(pattern = selected)
        }
    }
}

@Composable
private fun PatternPicker(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        patterns.forEachIndexed { index, pattern ->
            val selected = selectedIndex == index
            Surface(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelected(index) }),
                shape = RoundedCornerShape(50),
                color = if (selected) pattern.accent else Color.White,
                border = BorderStroke(1.dp, if (selected) pattern.accent else Color(0xFFE2DDD4))
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    text = pattern.title,
                    color = if (selected) Color.White else Color(0xFF303632),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private enum class PreviewPhase { Empty, Loading, Filled }

@Composable
private fun EmptyStatePreview(pattern: EmptyStatePattern) {
    var phase by remember(pattern.title) { mutableStateOf(PreviewPhase.Empty) }
    var filledNote by remember(pattern.title) { mutableStateOf<String?>(null) }
    var acknowledgement by remember(pattern.title) { mutableStateOf<String?>(null) }

    // Simulated request behind the primary action.
    LaunchedEffect(phase) {
        if (phase == PreviewPhase.Loading) {
            delay(1200)
            phase = PreviewPhase.Filled
        }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (phase == PreviewPhase.Filled) {
                FilledContent(
                    pattern = pattern,
                    note = filledNote,
                    onReset = {
                        phase = PreviewPhase.Empty
                        filledNote = null
                        acknowledgement = null
                    }
                )
            } else {
                EmptyIllustration(pattern = pattern, loading = phase == PreviewPhase.Loading)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = pattern.context.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = pattern.accent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pattern.headline,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (phase == PreviewPhase.Loading) "Loading..." else pattern.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF656D67),
                        textAlign = TextAlign.Center,
                        lineHeight = 21.sp
                    )
                    acknowledgement?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = pattern.accent,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f).height(50.dp),
                        enabled = phase == PreviewPhase.Empty,
                        onClick = {
                            acknowledgement = null
                            filledNote = null
                            phase = PreviewPhase.Loading
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = pattern.accent),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(pattern.primaryAction, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f).height(50.dp),
                        enabled = phase == PreviewPhase.Empty,
                        onClick = {
                            when (val outcome = pattern.secondaryOutcome) {
                                is SecondaryOutcome.ShowContent -> {
                                    filledNote = outcome.note
                                    phase = PreviewPhase.Filled
                                }
                                is SecondaryOutcome.Acknowledge -> acknowledgement = outcome.message
                            }
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(pattern.secondaryAction)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilledContent(pattern: EmptyStatePattern, note: String?, onReset: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = note ?: "Content is back. The empty state stepped aside.",
            style = MaterialTheme.typography.bodyMedium,
            color = pattern.accent,
            fontWeight = FontWeight.Bold
        )
        pattern.filledItems.forEach { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = pattern.accent.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, pattern.accent.copy(alpha = 0.18f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = pattern.accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(item, fontWeight = FontWeight.SemiBold, color = Color(0xFF171B18))
                }
            }
        }
        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onReset
        ) {
            Text("Show the empty state again", color = pattern.accent, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyIllustration(pattern: EmptyStatePattern, loading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.74f)
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        pattern.accent.copy(alpha = 0.18f),
                        Color(0xFFF8F4EE)
                    )
                )
            )
            .border(1.dp, pattern.accent.copy(alpha = 0.22f), RoundedCornerShape(26.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(pattern.accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(color = pattern.accent, strokeWidth = 3.dp)
            } else {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = pattern.icon,
                    contentDescription = null,
                    tint = pattern.accent
                )
            }
        }
    }
}

@Composable
private fun UxNotes(pattern: EmptyStatePattern) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF171B18)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.MailOutline,
                contentDescription = null,
                tint = Color(0xFFB7F2D1)
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "UX note",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pattern.designTip,
                    color = Color(0xFFD6DDD8),
                    lineHeight = 20.sp
                )
                Text(
                    text = pattern.uxNote,
                    color = Color(0xFFB7F2D1),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
