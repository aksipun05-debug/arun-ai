package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuickActionType
import com.example.ui.theme.BorderLight
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimarySoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.White

data class QuickActionDefinition(
    val type: QuickActionType,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val defaultPrompt: String
)

val defaultQuickActions = listOf(
    QuickActionDefinition(
        type = QuickActionType.ASK_ANYTHING,
        title = "Ask Anything",
        subtitle = "Instant answers & ideas",
        icon = Icons.Default.ChatBubbleOutline,
        defaultPrompt = "What can you help me with today?"
    ),
    QuickActionDefinition(
        type = QuickActionType.VOICE_CHAT,
        title = "Voice Chat",
        subtitle = "Talk with Arun AI hands-free",
        icon = Icons.Default.Mic,
        defaultPrompt = "Hello Arun AI, let's have a voice chat."
    ),
    QuickActionDefinition(
        type = QuickActionType.IMAGE,
        title = "Image",
        subtitle = "Visual queries & analysis",
        icon = Icons.Default.Image,
        defaultPrompt = "Help me analyze this image concept."
    ),
    QuickActionDefinition(
        type = QuickActionType.PDF,
        title = "PDF",
        subtitle = "Document summaries & notes",
        icon = Icons.Default.Description,
        defaultPrompt = "Summarize the key takeaways from my document."
    ),
    QuickActionDefinition(
        type = QuickActionType.CAMERA,
        title = "Camera",
        subtitle = "Snap objects or math problems",
        icon = Icons.Default.CameraAlt,
        defaultPrompt = "Look at this picture and explain what it is."
    ),
    QuickActionDefinition(
        type = QuickActionType.WEB_SEARCH,
        title = "Web Search",
        subtitle = "Latest news & real-time info",
        icon = Icons.Default.TravelExplore,
        defaultPrompt = "Search and tell me the latest updates on AI."
    ),
    QuickActionDefinition(
        type = QuickActionType.REMINDER,
        title = "Reminder",
        subtitle = "Smart schedule & tasks",
        icon = Icons.Default.Alarm,
        defaultPrompt = "Set a daily reminder for my important tasks."
    )
)

@Composable
fun QuickActionCard(
    action: QuickActionDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .testTag("quick_action_${action.title.lowercase().replace(" ", "_")}")
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        color = White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SkyPrimarySoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = SkyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = action.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
