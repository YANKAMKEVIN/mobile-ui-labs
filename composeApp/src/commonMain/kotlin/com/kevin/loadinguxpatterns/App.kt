package com.kevin.loadinguxpatterns

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.CardColor
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.LabFooter
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTabs
import com.kevin.labs.ui.LabTheme
import com.kevin.labs.ui.Muted
import com.kevin.labs.ui.Paper
import com.kevin.labs.ui.RuleCard
import com.kevin.labs.ui.swipeBetweenTabs
import com.kevin.labs.ui.asText
import kotlinx.coroutines.delay

/** The UX question this lab asks; also its summary on the catalog. */
internal const val PITCH =
    "How should progress show after an action? Compare in-button, overlay, skeleton, optimistic and step by step."

@Composable
fun App() {
    LabTheme(primary = Color(0xFF0E6251), secondary = Color(0xFFB26A00)) {
        LoadingGallery()
    }
}

private data class LoadingPattern(
    val title: String,
    val context: String,
    val buttonText: String,
    val note: String,
    val mark: String,
    val accent: Color
)

private val patterns = listOf(
    LoadingPattern(
        title = "Button",
        context = "Confirming one small action",
        buttonText = "Save changes",
        note = "Best when only the tapped action is blocked.",
        mark = "01",
        accent = Color(0xFF0E6251)
    ),
    LoadingPattern(
        title = "Overlay",
        context = "Submitting a full screen flow",
        buttonText = "Submit",
        note = "Best when leaving the screen would create ambiguity.",
        mark = "02",
        accent = Color(0xFF2F5DA8)
    ),
    LoadingPattern(
        title = "Skeleton",
        context = "Fetching content structure",
        buttonText = "Refresh feed",
        note = "Best when layout is predictable but content is not ready.",
        mark = "03",
        accent = Color(0xFF6D3FB8)
    ),
    LoadingPattern(
        title = "Optimistic",
        context = "Reacting before the network returns",
        buttonText = "Like item",
        note = "Best when rollback is cheap and confidence is high.",
        mark = "04",
        accent = Color(0xFFC2410C)
    ),
    LoadingPattern(
        title = "Steps",
        context = "Explaining a longer operation",
        buttonText = "Sync data",
        note = "Best when users need progress, not just patience.",
        mark = "05",
        accent = Color(0xFF455A64)
    )
)

@Composable
private fun LoadingGallery() {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }
    // Each tap bumps runId; 0 means "no run". Keying the effect on runId (and not on
    // isLoading) keeps the effect from cancelling itself when it flips isLoading to false,
    // which used to leave the button stuck on "Done".
    var runId by remember { mutableStateOf(0) }
    val selected = patterns[selectedIndex]

    LaunchedEffect(runId) {
        if (runId == 0) return@LaunchedEffect
        isLoading = true
        completed = false
        delay(1800)
        isLoading = false
        completed = true
        delay(1400)
        completed = false
    }

    val selectPattern: (Int) -> Unit = {
        selectedIndex = it
        runId = 0
        isLoading = false
        completed = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 12.dp),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            LabHeader(
                title = "Loading UX Patterns",
                subtitle = PITCH
            )
        }
        item {
            LabTabs(
                items = patterns,
                label = { it.title },
                accent = { it.accent },
                selectedIndex = selectedIndex,
                onSelected = selectPattern
            )
        }
        item {
            AnimatedContent(
                targetState = selected,
                modifier = Modifier.swipeBetweenTabs(selectedIndex, patterns.size, selectPattern),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "loading-pattern"
            ) { pattern ->
                LoadingPreview(
                    pattern = pattern,
                    isLoading = isLoading,
                    completed = completed,
                    onRun = {
                        if (!isLoading) runId++
                    }
                )
            }
        }
        item {
            RuleCard(
                rule = selected.note,
                accent = selected.accent
            )
        }
        item { LabFooter() }
    }
}

@Composable
private fun LoadingPreview(
    pattern: LoadingPattern,
    isLoading: Boolean,
    completed: Boolean,
    onRun: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = CardColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                PatternIllustration(pattern, isLoading, completed)
                Text(
                    text = pattern.context,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                PatternSpecificContent(pattern.title, isLoading, completed, pattern.accent)
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = pattern.title != "Overlay" || !isLoading,
                    onClick = onRun,
                    colors = ButtonDefaults.buttonColors(containerColor = pattern.accent),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    if (pattern.title == "Button" && isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (completed) "Done" else pattern.buttonText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (pattern.title == "Overlay" && isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CardColor.copy(alpha = 0.88f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = pattern.accent)
                        Text("Submitting securely", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PatternIllustration(
    pattern: LoadingPattern,
    isLoading: Boolean,
    completed: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.74f)
            .height(190.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(pattern.accent.copy(alpha = 0.18f), Paper)
                )
            )
            .border(1.dp, pattern.accent.copy(alpha = 0.22f), RoundedCornerShape(26.dp)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(94.dp),
            shape = CircleShape,
            color = pattern.accent.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(color = pattern.accent, strokeWidth = 3.dp)
                } else {
                    if (completed) {
                        Icon(
                            modifier = Modifier.size(44.dp),
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = pattern.accent
                        )
                    } else {
                        Text(
                            text = pattern.mark,
                            color = pattern.accent.asText,
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PatternSpecificContent(
    title: String,
    isLoading: Boolean,
    completed: Boolean,
    accent: Color
) {
    when (title) {
        "Skeleton" -> SkeletonRows(isLoading, accent)
        "Steps" -> StepProgress(isLoading, completed, accent)
        "Optimistic" -> OptimisticState(isLoading, completed, accent)
        else -> StatusText(isLoading, completed)
    }
}

@Composable
private fun StatusText(isLoading: Boolean, completed: Boolean) {
    Text(
        text = when {
            isLoading -> "Keeping the feedback close to the action."
            completed -> "The UI confirms completion without stealing focus."
            else -> "Tap the action to preview the loading behavior."
        },
        color = Muted,
        textAlign = TextAlign.Center,
        lineHeight = 21.sp
    )
}

@Composable
private fun SkeletonRows(isLoading: Boolean, accent: Color) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "skeleton-alpha"
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (index == 1) 0.86f else 1f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = if (isLoading) alpha else 0.16f))
            )
        }
    }
}

@Composable
private fun StepProgress(isLoading: Boolean, completed: Boolean, accent: Color) {
    val progress = when {
        completed -> 1f
        isLoading -> 0.62f
        else -> 0.18f
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = accent,
            trackColor = accent.copy(alpha = 0.16f)
        )
        Text(
            text = if (completed) "Synced" else if (isLoading) "Uploading changes" else "Ready to sync",
            color = Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun OptimisticState(isLoading: Boolean, completed: Boolean, accent: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = if (isLoading || completed) 0.14f else 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.20f))
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp),
            text = if (isLoading || completed) "Visible immediately, confirmed quietly." else "Waiting for the first tap.",
            color = Ink,
            textAlign = TextAlign.Center
        )
    }
}

