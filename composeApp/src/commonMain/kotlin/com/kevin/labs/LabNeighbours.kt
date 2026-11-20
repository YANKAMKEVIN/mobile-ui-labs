package com.kevin.labs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.CardColor
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.Line
import com.kevin.labs.ui.Muted

/** The labs just before and after [id] in catalog order (null at either end). */
internal fun neighbours(id: String): Pair<Lab?, Lab?> {
    val index = labs.indexOfFirst { it.id == id }
    if (index < 0) return null to null
    return labs.getOrNull(index - 1) to labs.getOrNull(index + 1)
}

/** "Previous lab" / "Next lab" cards closing a lab, so the series reads like chapters. */
@Composable
internal fun LabNeighbours(lab: Lab, onOpen: (Lab) -> Unit) {
    val (previous, next) = neighbours(lab.id)
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (previous != null) {
            NeighbourCard(previous, label = "Previous lab", end = false, onOpen, Modifier.weight(1f))
        } else {
            Spacer(Modifier.weight(1f))
        }
        if (next != null) {
            NeighbourCard(next, label = "Next lab", end = true, onOpen, Modifier.weight(1f))
        } else {
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun NeighbourCard(lab: Lab, label: String, end: Boolean, onOpen: (Lab) -> Unit, modifier: Modifier) {
    Surface(
        onClick = { onOpen(lab) },
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics { contentDescription = "$label: ${lab.title}" },
        shape = RoundedCornerShape(18.dp),
        color = CardColor,
        border = BorderStroke(1.dp, Line)
    ) {
        Row(
            // The card itself is announced as "Next lab: <title>"; arrows and labels stay visual.
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp).clearAndSetSemantics {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!end) Text("←", color = lab.accent, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = if (end) Alignment.End else Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(label.uppercase(), color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
                Text(
                    lab.title,
                    color = Ink,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    lineHeight = 19.sp,
                    textAlign = if (end) TextAlign.End else TextAlign.Start
                )
            }
            if (end) Text("→", color = lab.accent, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}
