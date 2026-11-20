package com.kevin.labs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.CardColor
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTheme
import com.kevin.labs.ui.Line
import com.kevin.labs.ui.LocalLabHosted
import com.kevin.labs.ui.Muted
import com.kevin.labs.ui.Paper
import com.kevin.labs.ui.SystemBackHandler

/** The whole app; [darkTheme] follows the system setting unless forced. */
@Composable
fun App(darkTheme: Boolean = isSystemInDarkTheme()) {
    LabTheme(primary = Ink, secondary = Muted, darkTheme = darkTheme) {
        // The open lab is saved by id, so rotation or process death brings the user back to it.
        var openId by rememberSaveable { mutableStateOf<String?>(null) }
        val open = labs.firstOrNull { it.id == openId }
        if (open == null) {
            Catalog(onOpen = { openId = it.id })
        } else {
            LabScreen(lab = open, onBack = { openId = null })
        }
    }
}

@Composable
fun Catalog(onOpen: (Lab) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LabHeader(
                title = "Mobile UI Labs",
                subtitle = "Ten small Compose Multiplatform labs, one UX decision each.",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(labs, key = { it.id }) { lab -> LabRow(lab, onClick = { onOpen(lab) }) }
    }
}

@Composable
private fun LabRow(lab: Lab, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardColor,
        border = BorderStroke(1.dp, Line)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabVignette(lab.vignette, lab.accent)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(lab.title, color = Ink, fontWeight = FontWeight.Black, fontSize = 17.sp)
                Text(lab.summary, color = Muted, lineHeight = 20.sp, fontSize = 14.sp)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Muted
            )
        }
    }
}

/**
 * A lab under a slim top bar: Back to the catalog and the lab title. The lab's own header then
 * shrinks to a one-line pitch ([LocalLabHosted]). System Back also returns to the catalog.
 */
@Composable
fun LabScreen(lab: Lab, onBack: () -> Unit) {
    SystemBackHandler(enabled = true, onBack = onBack)
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Paper)
                .statusBarsPadding()
                .padding(start = 4.dp, end = 16.dp)
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "All labs",
                    tint = Ink
                )
            }
            Text(
                text = lab.title,
                modifier = Modifier.weight(1f).semantics { heading() },
                color = Ink,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        // The bar above already pads for the status bar: labs must not pad for it a second time.
        Box(Modifier.weight(1f).consumeWindowInsets(WindowInsets.statusBars)) {
            CompositionLocalProvider(LocalLabHosted provides true) { lab.content() }
        }
    }
}
