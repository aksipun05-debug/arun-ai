package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttachmentType
import com.example.ui.theme.BorderLight
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimarySoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.White

data class AttachmentOption(
    val type: AttachmentType,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val sampleName: String,
    val sampleSize: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onSelectAttachment: (name: String, type: AttachmentType, size: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val options = listOf(
        AttachmentOption(
            type = AttachmentType.IMAGE,
            title = "Image",
            subtitle = "Upload photos from gallery",
            icon = Icons.Default.Image,
            sampleName = "Screenshot_Design.png",
            sampleSize = "1.8 MB"
        ),
        AttachmentOption(
            type = AttachmentType.DOCUMENT,
            title = "PDF / Document",
            subtitle = "Upload research papers or notes",
            icon = Icons.Default.Description,
            sampleName = "AI_Report_2026.pdf",
            sampleSize = "3.4 MB"
        ),
        AttachmentOption(
            type = AttachmentType.CAMERA,
            title = "Camera",
            subtitle = "Snap a photo instantly",
            icon = Icons.Default.CameraAlt,
            sampleName = "Camera_Capture.jpg",
            sampleSize = "2.2 MB"
        ),
        AttachmentOption(
            type = AttachmentType.AUDIO,
            title = "Audio Note",
            subtitle = "Attach voice clip or meeting recording",
            icon = Icons.Default.Mic,
            sampleName = "Voice_Memo.m4a",
            sampleSize = "850 KB"
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null,
        modifier = modifier.testTag("attachment_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Add Attachment",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Choose a file to discuss with Arun AI",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                ),
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            options.forEach { option ->
                Surface(
                    modifier = Modifier
                        .testTag("attachment_option_${option.title.lowercase().replace(" ", "_")}")
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            onSelectAttachment(option.sampleName, option.type, option.sampleSize)
                            onDismiss()
                        },
                    color = SkyPrimarySoft.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SkyPrimarySoft,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.title,
                                    tint = SkyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
