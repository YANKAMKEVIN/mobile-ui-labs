package com.kevin.bottomsheetplaybook

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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTheme
import com.kevin.labs.ui.Line
import com.kevin.labs.ui.Muted
import com.kevin.labs.ui.Paper
import kotlinx.coroutines.launch

@Composable
fun App() {
    LabTheme(primary = InkGreen, secondary = Clay) {
        BottomSheetPlaybook()
    }
}

private enum class SheetKind {
    Confirm,
    Filters,
    Peek,
    Form,
    Actions
}

private data class SheetPattern(
    val kind: SheetKind,
    val title: String,
    val context: String,
    val rule: String,
    val accent: Color
)

private val InkGreen = Color(0xFF0F6B55)
private val Blue = Color(0xFF2364AA)
private val Violet = Color(0xFF6C4AB6)
private val Clay = Color(0xFFB5532A)
private val Charcoal = Color(0xFF334046)
private val Danger = Color(0xFFD23B3B)

private val sheetPatterns = listOf(
    SheetPattern(
        kind = SheetKind.Confirm,
        title = "Confirm",
        context = "Small reversible decisions",
        rule = "Keep it short. A sheet is useful when the user needs context from the current screen.",
        accent = InkGreen
    ),
    SheetPattern(
        kind = SheetKind.Filters,
        title = "Filters",
        context = "Temporary search refinement",
        rule = "Let users scan, tweak and apply without losing the result list behind the sheet.",
        accent = Blue
    ),
    SheetPattern(
        kind = SheetKind.Peek,
        title = "Peek",
        context = "Preview before commitment",
        rule = "Use a partial sheet when the background content still matters.",
        accent = Violet
    ),
    SheetPattern(
        kind = SheetKind.Form,
        title = "Form",
        context = "Focused but lightweight input",
        rule = "If the form needs many fields or deep validation, promote it to a full screen.",
        accent = Clay
    ),
    SheetPattern(
        kind = SheetKind.Actions,
        title = "Actions",
        context = "Grouped secondary commands",
        rule = "Make destructive actions visually distinct and keep the primary path obvious.",
        accent = Charcoal
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetPlaybook() {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    var openPattern by remember { mutableStateOf<SheetPattern?>(null) }
    val selected = sheetPatterns[selectedIndex]
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = selected.kind != SheetKind.Peek)

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
            item {
                LabHeader(
                    title = "Bottom Sheet Playbook",
                    subtitle = "A compact UI lab for choosing the right sheet pattern without turning every task into a hidden page."
                )
            }
            item {
                PatternTabs(
                    selectedIndex = selectedIndex,
                    onSelected = {
                        selectedIndex = it
                        openPattern = null
                    }
                )
            }
            item {
                AnimatedContent(
                    targetState = selected,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "selected-sheet"
                ) { pattern ->
                    SheetPreview(pattern = pattern, onOpen = { openPattern = pattern })
                }
            }
            item { DecisionRule(selected) }
        }

        if (openPattern != null) {
            ModalBottomSheet(
                onDismissRequest = { openPattern = null },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                SheetContent(
                    pattern = openPattern!!,
                    onClose = {
                        // Clear the open sheet in invokeOnCompletion rather than after hide():
                        // if hide() is cancelled (e.g. the user grabs the sheet mid-animation),
                        // the code after it never runs and openPattern would stay set while the
                        // sheet is hidden, so the "Open" button could no longer show it again.
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) openPattern = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PatternTabs(selectedIndex: Int, onSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        sheetPatterns.forEachIndexed { index, pattern ->
            val selected = selectedIndex == index
            Surface(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelected(index) }),
                color = if (selected) pattern.accent else Color.White,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, if (selected) pattern.accent else Line)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    text = pattern.title,
                    color = if (selected) Color.White else Ink,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SheetPreview(pattern: SheetPattern, onOpen: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SheetAnatomy(pattern)
            Text(
                text = pattern.context,
                color = Ink,
                fontWeight = FontWeight.Black,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = previewCopy(pattern.kind),
                color = Muted,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onOpen,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
            ) {
                Text("Open ${pattern.title} sheet", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SheetAnatomy(pattern: SheetPattern) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(pattern.accent.copy(alpha = 0.10f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (pattern.kind == SheetKind.Peek) 0.84f else 0.92f)
                .height(if (pattern.kind == SheetKind.Peek) 76.dp else 108.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 46.dp, height = 5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Line)
            )
            repeat(if (pattern.kind == SheetKind.Peek) 2 else 3) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (index == 2) 0.64f else 1f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(50))
                        .background(pattern.accent.copy(alpha = 0.18f))
                )
            }
        }
    }
}

@Composable
private fun SheetContent(pattern: SheetPattern, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = pattern.title,
                color = Ink,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.headlineSmall
            )
            TextButton(onClick = onClose) { Text("Close") }
        }
        when (pattern.kind) {
            SheetKind.Confirm -> ConfirmSheet(pattern, onClose)
            SheetKind.Filters -> FiltersSheet(pattern, onClose)
            SheetKind.Peek -> PeekSheet(pattern, onClose)
            SheetKind.Form -> FormSheet(pattern, onClose)
            SheetKind.Actions -> ActionsSheet(pattern, onClose)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ConfirmSheet(pattern: SheetPattern, onClose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Archive this project idea?", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("The screen behind keeps the object visible, so the user knows exactly what they are confirming.", color = Muted)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(modifier = Modifier.weight(1f), onClick = onClose) { Text("Cancel") }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
            ) { Text("Archive") }
        }
    }
}

@Composable
private fun FiltersSheet(pattern: SheetPattern, onClose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Refine results", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Compose", "KMP", "UX", "Animation", "Architecture", "Offline").forEachIndexed { index, label ->
                ChoicePill(label = label, selected = index in listOf(0, 2, 3), accent = pattern.accent)
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
        ) { Text("Apply filters") }
    }
}

@Composable
private fun PeekSheet(pattern: SheetPattern, onClose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Trip preview", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("ETA", "18 min", pattern.accent)
            StatCard("Walk", "1.2 km", pattern.accent)
            StatCard("Cost", "2.15", pattern.accent)
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
        ) { Text("Expand details") }
    }
}

@Composable
private fun FormSheet(pattern: SheetPattern, onClose: () -> Unit) {
    var priority by remember { mutableStateOf(0.64f) }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Quick note", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Paper,
            border = BorderStroke(1.dp, Line)
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Test dismiss behavior on drag and outside tap",
                color = Ink
            )
        }
        Text("Priority", color = Muted, fontWeight = FontWeight.Bold)
        Slider(value = priority, onValueChange = { priority = it })
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
        ) { Text("Save note") }
    }
}

@Composable
private fun ActionsSheet(pattern: SheetPattern, onClose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("Duplicate", "Share preview", "Pin to collection").forEach {
            ActionRow(label = it, accent = pattern.accent, destructive = false)
        }
        ActionRow(label = "Delete draft", accent = Danger, destructive = true)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = pattern.accent)
        ) { Text("Done") }
    }
}

@Composable
private fun ChoicePill(label: String, selected: Boolean, accent: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) accent else Paper,
        border = BorderStroke(1.dp, if (selected) accent else Line)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            text = label,
            color = if (selected) Color.White else Ink,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RowScope.StatCard(label: String, value: String, accent: Color) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.10f)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Ink, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ActionRow(label: String, accent: Color, destructive: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (destructive) Danger.copy(alpha = 0.08f) else Paper,
        border = BorderStroke(1.dp, if (destructive) Danger.copy(alpha = 0.24f) else Line)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background((if (destructive) Danger else accent).copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = if (destructive) Danger else accent
                )
            }
            Text(
                text = label,
                color = if (destructive) Danger else Ink,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DecisionRule(pattern: SheetPattern) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Ink
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Decision rule", color = Color.White, fontWeight = FontWeight.Black)
            Text(pattern.rule, color = Color(0xFFDDE4DE), lineHeight = 20.sp)
        }
    }
}

private fun previewCopy(kind: SheetKind): String = when (kind) {
    SheetKind.Confirm -> "A sheet works when the decision is connected to the visible object behind it."
    SheetKind.Filters -> "A sheet lets the result page remain the main character while controls stay temporary."
    SheetKind.Peek -> "Partial height is useful when the sheet should add information, not replace the screen."
    SheetKind.Form -> "Short forms can feel fast in a sheet. Long forms usually deserve their own route."
    SheetKind.Actions -> "Grouped actions need hierarchy, spacing and a clear treatment for destructive choices."
}
