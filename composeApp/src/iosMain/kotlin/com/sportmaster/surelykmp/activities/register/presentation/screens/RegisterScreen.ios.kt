package com.sportmaster.surelykmp.activities.register.presentation.screens

package com.mertswork.footyreserve.core.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ProfileImagePicker(
    imageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
//            .clip(CircleShape)
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .clickable { launcher.launch("image/*") }
        ,
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
//            .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.select_image),
                        contentDescription = "Add Photo",
//                    tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(100.dp)
                    )
                }
                Spacer(
                    Modifier.height(14.dp)
                )
                Text(
                    text = stringResource(Res.string.click_to_upload_image),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontSize = Dimens.Title
                )
            }
        }
    }
}