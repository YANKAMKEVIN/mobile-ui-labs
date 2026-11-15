package com.kevin.labs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTheme
import com.kevin.labs.ui.Line
import com.kevin.labs.ui.Muted
import com.kevin.labs.ui.Paper
import com.kevin.labs.ui.SystemBackHandler

@Composable
fun App() {
    LabTheme(primary = Ink, secondary = Muted) {
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
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(lab.accent)
            )
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

/** A lab with a slim "All labs" bar on top; system Back also returns to the catalog. */
@Composable
fun LabScreen(lab: Lab, onBack: () -> Unit) {
    SystemBackHandler(enabled = true, onBack = onBack)
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Paper)
                .statusBarsPadding()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier.heightIn(min = 48.dp).semantics { this.role = Role.Button },
                shape = RoundedCornerShape(50),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Ink,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("All labs", color = Ink, fontWeight = FontWeight.Bold)
                }
            }
        }
        Box(Modifier.weight(1f)) { lab.content() }
    }
}
