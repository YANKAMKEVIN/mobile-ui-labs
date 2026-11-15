package com.kevin.feedbacksurfacelab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Pine,
            secondary = Amber,
            background = Paper,
            surface = Color.White
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Paper
        ) {
            FeedbackSurfaceLab()
        }
    }
}

private enum class FeedbackKind {
    Inline,
    Snackbar,
    Banner,
    Undo,
    Blocking
}

private data class FeedbackPattern(
    val kind: FeedbackKind,
    val title: String,
    val trigger: String,
    val rule: String,
    val accent: Color
)

private val Paper = Color(0xFFF7F2EA)
private val Ink = Color(0xFF111713)
private val Muted = Color(0xFF66716B)
private val Line = Color(0xFFE2DBD1)
private val Pine = Color(0xFF0F6B55)
private val Blue = Color(0xFF255EA8)
private val Amber = Color(0xFFB86812)
private val Rose = Color(0xFFC2415D)
private val Slate = Color(0xFF34444B)
private val Danger = Color(0xFFD23B3B)

private val feedbackPatterns = listOf(
    FeedbackPattern(
        kind = FeedbackKind.Inline,
        title = "Inline",
        trigger = "Field needs correction",
        rule = "Put the message next to the thing users can fix.",
        accent = Danger
    ),
    FeedbackPattern(
        kind = FeedbackKind.Snackbar,
        title = "Snackbar",
        trigger = "Low-stakes confirmation",
        rule = "Use it when the user can keep moving without reading a full explanation.",
        accent = Pine
    ),
    FeedbackPattern(
        kind = FeedbackKind.Banner,
        title = "Banner",
        trigger = "Page-level context changed",
        rule = "Make it persistent when the information affects the whole screen.",
        accent = Blue
    ),
    FeedbackPattern(
        kind = FeedbackKind.Undo,
        title = "Undo",
        trigger = "Reversible destructive action",
        rule = "Offer recovery immediately instead of asking for confirmation too early.",
        accent = Rose
    ),
    FeedbackPattern(
        kind = FeedbackKind.Blocking,
        title = "Blocking",
        trigger = "Decision cannot continue",
        rule = "Interrupt only when continuing would create data loss, risk, or confusion.",
        accent = Slate
    )
)

@Composable
private fun FeedbackSurfaceLab() {
    var selectedIndex by rememberSaveable { mutableStateOf(1) }
    // Persistent surfaces (inline error, banner, blocking dialog) currently on screen; null = none.
    // Transient ones (snackbar, undo) go through the SnackbarHostState so they time out on their own.
    var activeKind by remember { mutableStateOf<FeedbackKind?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val selected = feedbackPatterns[selectedIndex]

    fun trigger(kind: FeedbackKind) {
        when (kind) {
            FeedbackKind.Snackbar -> scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar("Settings saved", duration = SnackbarDuration.Short)
            }
            FeedbackKind.Undo -> scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = "Draft deleted",
                    actionLabel = "UNDO",
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    snackbarHostState.showSnackbar("Draft restored", duration = SnackbarDuration.Short)
                }
            }
            else -> activeKind = if (activeKind == kind) null else kind
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 14.dp),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Header() }
            item {
                FeedbackTabs(
                    selectedIndex = selectedIndex,
                    onSelected = {
                        selectedIndex = it
                        activeKind = null
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                )
            }
            item {
                AnimatedContent(
                    targetState = selected,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "feedback-pattern"
                ) { pattern ->
                    FeedbackPreview(
                        pattern = pattern,
                        activeKind = activeKind,
                        onTrigger = { trigger(pattern.kind) },
                        onDismissBlocking = { activeKind = null }
                    )
                }
            }
            item { RuleCard(selected) }
            item { Spacer(Modifier.height(74.dp)) }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = Ink,
                contentColor = Color.White,
                action = {
                    data.visuals.actionLabel?.let { label ->
                        TextButton(onClick = { data.performAction() }) {
                            Text(label, color = Color(0xFFFFD166), fontWeight = FontWeight.Black)
                        }
                    }
                }
            ) {
                Text(text = data.visuals.message, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Feedback Surface Lab",
            color = Ink,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "A product-minded Compose lab for choosing where feedback should live after an interaction.",
            color = Muted,
            lineHeight = 23.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun FeedbackTabs(selectedIndex: Int, onSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        feedbackPatterns.forEachIndexed { index, pattern ->
            val selected = selectedIndex == index
            Surface(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelected(index) }),
                shape = RoundedCornerShape(50),
                color = if (selected) pattern.accent else Color.White,
                border = BorderStroke(1.dp, if (selected) pattern.accent else Line)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    text = pattern.title,
                    color = if (selected) Color.White else Ink,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun FeedbackPreview(
    pattern: FeedbackPattern,
    activeKind: FeedbackKind?,
    onTrigger: () -> Unit,
    onDismissBlocking: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MockProductSurface(pattern, activeKind, onDismissBlocking)
            Text(
                text = pattern.trigger,
                color = Ink,
                fontWeight = FontWeight.Black,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = bodyCopy(pattern.kind),
                color = Muted,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onTrigger,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
            ) {
                Text(
                    text = if (activeKind == pattern.kind) "Hide ${pattern.title}" else "Show ${pattern.title}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MockProductSurface(
    pattern: FeedbackPattern,
    activeKind: FeedbackKind?,
    onDismissBlocking: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(224.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Paper)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AnimatedVisibility(activeKind == FeedbackKind.Banner) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Blue.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Blue.copy(alpha = 0.25f))
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "Offline mode: changes sync when the network returns.",
                        color = Blue,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                }
            }
            Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusDot(pattern.accent)
                        Text(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .weight(1f),
                            text = "Profile settings",
                            color = Ink,
                            fontWeight = FontWeight.Black
                        )
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Pine)
                    }
                    FieldPreview(
                        label = "Display name",
                        value = if (activeKind == FeedbackKind.Inline) "K" else "Kevin Yankam",
                        error = activeKind == FeedbackKind.Inline
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(modifier = Modifier.weight(1f), onClick = {}) { Text("Cancel") }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
                        ) { Text("Save") }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = activeKind == FeedbackKind.Blocking,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.88f),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Line)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = Danger)
                    Text("Session expired", color = Ink, fontWeight = FontWeight.Black)
                    Text(
                        "Reconnect before saving changes.",
                        color = Muted,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    TextButton(onClick = onDismissBlocking) {
                        Text("Reconnect", color = Slate, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusDot(color: Color) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
private fun FieldPreview(label: String, value: String, error: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, if (error) Danger else Line)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                text = value,
                color = Ink,
                fontWeight = FontWeight.Bold
            )
        }
        AnimatedVisibility(error) {
            Text(
                text = "Use at least 3 characters.",
                color = Danger,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RuleCard(pattern: FeedbackPattern) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Ink
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Decision rule", color = Color.White, fontWeight = FontWeight.Black)
            Text(pattern.rule, color = Color(0xFFDDE4DE), lineHeight = 20.sp)
        }
    }
}

private fun bodyCopy(kind: FeedbackKind): String = when (kind) {
    FeedbackKind.Inline -> "The user should not search for what failed. Put the correction beside the field."
    FeedbackKind.Snackbar -> "Fast, temporary and non-blocking. Good for confirmation after a small action."
    FeedbackKind.Banner -> "Affects the whole page, so it deserves a stable place above the content."
    FeedbackKind.Undo -> "Best for reversible actions where confirmation would slow the primary flow."
    FeedbackKind.Blocking -> "Reserve interruption for moments where moving forward would be genuinely risky."
}
