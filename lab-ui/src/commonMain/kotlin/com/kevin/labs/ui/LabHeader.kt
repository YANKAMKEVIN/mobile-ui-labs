package com.kevin.labs.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Lab title (announced as a heading) and its one-sentence pitch.
 *
 * Inside the app shell ([LocalLabHosted]) the title already sits in the top bar: only the pitch is
 * shown, on one line, and a tap unfolds it.
 */
@Composable
fun LabHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    if (LocalLabHosted.current) {
        CollapsibleSubtitle(subtitle, modifier)
        return
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            color = Ink,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = subtitle,
            color = Muted,
            lineHeight = 23.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CollapsibleSubtitle(subtitle: String, modifier: Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Only offer to unfold when the line is actually cut.
    var truncated by remember(subtitle) { mutableStateOf(false) }
    val canToggle = expanded || truncated
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .then(
                if (canToggle) {
                    Modifier.clickable(
                        role = Role.Button,
                        onClickLabel = if (expanded) "Show less" else "Show more",
                        onClick = { expanded = !expanded }
                    )
                } else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = subtitle,
            modifier = Modifier.weight(1f),
            color = Muted,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            maxLines = if (expanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) truncated = it.hasVisualOverflow }
        )
        if (canToggle) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Muted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
