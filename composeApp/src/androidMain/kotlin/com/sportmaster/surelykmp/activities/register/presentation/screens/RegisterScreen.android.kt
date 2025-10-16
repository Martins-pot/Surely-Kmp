package com.sportmaster.surelykmp.activities.register.presentation.screens

//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.mertswork.footyreserve.ui.theme.Dimens
//import org.jetbrains.compose.resources.painterResource
//import org.jetbrains.compose.resources.stringResource
//import surelykmp.composeapp.generated.resources.Res
//import surelykmp.composeapp.generated.resources.click_to_upload_image
//import surelykmp.composeapp.generated.resources.select_image



import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color
import com.mertswork.footyreserve.ui.theme.Dimens
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.unselected_tennis


@Composable
actual fun ProfileImagePicker(
    imageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri?.toString())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
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
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.unselected_tennis),
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(100.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Upload image",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontSize = Dimens.Title
                )
            }
        }
    }
}
//
//@Composable
//actual fun ProfileImagePicker(
//    imageUri: String?,
//    onImageSelected: (String?) -> Unit
//) {
//    val context = LocalContext.current
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        onImageSelected(uri?.toString())
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(150.dp)
////            .clip(CircleShape)
////            .background(MaterialTheme.colorScheme.surfaceVariant)
////            .clickable { launcher.launch("image/*") }
//        ,
//        contentAlignment = Alignment.Center
//    ) {
//        if (imageUri != null) {
//            AsyncImage(
//                model = imageUri,
//                contentDescription = "Profile Image",
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//        } else {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(CircleShape)
////            .background(MaterialTheme.colorScheme.surfaceVariant)
//                        .clickable { launcher.launch("image/*") },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        painter = painterResource(Res.drawable.select_image),
//                        contentDescription = "Add Photo",
////                    tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(100.dp)
//                    )
//                }
//                Spacer(
//                    Modifier.height(14.dp)
//                )
//                Text(
//                    text = stringResource(Res.string.click_to_upload_image),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.White,
//                    fontSize = Dimens.Title
//                )
//            }
//        }
//    }
//}