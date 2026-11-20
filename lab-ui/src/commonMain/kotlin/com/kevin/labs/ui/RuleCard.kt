package com.kevin.labs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The card that closes every lab with its rule, always titled the same way so the "rule to
 * remember" reads at a glance from one lab to the next. Inside the app shell it also links to the
 * code of the lab ([LocalLabSource]).
 *
 * @param accent colour of the pattern on screen, filling the check badge before the title.
 * @param note optional second line, for a "when to use it" hint after the rule.
 */
@Composable
fun RuleCard(
    rule: String,
    accent: Color,
    modifier: Modifier = Modifier,
    note: String? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Inverse
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
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(text = RULE_TITLE, color = OnInverse, fontWeight = FontWeight.Black)
            }
            Text(text = rule, color = OnInverseMuted, lineHeight = 22.sp)
            note?.let { Text(text = it, color = OnInverse, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp) }
            LocalLabSource.current?.let { ViewCode(it) }
        }
    }
}

/** The one title of every rule card. */
const val RULE_TITLE = "Decision rule"

@Composable
private fun ViewCode(source: LabSource) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable(role = Role.Button, onClickLabel = "Open the code in a browser") {
                uriHandler.openUri(source.url)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "View code", color = OnInverse, fontWeight = FontWeight.Black, textDecoration = TextDecoration.Underline)
        Text(text = "${source.symbol}()", color = OnInverseMuted, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
    }
}

