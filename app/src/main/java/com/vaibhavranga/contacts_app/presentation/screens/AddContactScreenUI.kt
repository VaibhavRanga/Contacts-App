package com.vaibhavranga.contacts_app.presentation.screens

import android.icu.util.Calendar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vaibhavranga.contacts_app.data.entity.Contact
import com.vaibhavranga.contacts_app.presentation.ImageCompressor
import com.vaibhavranga.contacts_app.presentation.viewModel.AppState
import com.vaibhavranga.contacts_app.presentation.viewModel.ContactViewModel
import kotlinx.coroutines.launch
import kotlin.io.path.deleteExisting
import kotlin.io.path.outputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreenUI(
    viewModel: ContactViewModel,
    state: AppState,
    onSaveButtonClick: (Contact) -> Unit,
    onNameValueChanged: (String) -> Unit,
    onEmailValueChanged: (String) -> Unit,
    onPhoneNumberValueChanged: (String) -> Unit,
    onScaffoldNavigationIconClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageCompressor =  remember { ImageCompressor(context) }

    val profileImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { contentUri ->
        if (contentUri == null) {
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val file = kotlin.io.path.createTempFile()
            val inputStream = context.contentResolver.openInputStream(contentUri)
            val outputStream = file.outputStream()
            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            val byteArray = imageCompressor.compressImage(contentUri, file.toFile())
            viewModel.updateImage(byteArray)
            inputStream?.close()
            outputStream.close()
            file.deleteExisting()




            /*val inputStream = context.contentResolver.openInputStream(contentUri)
            val byteArray = inputStream?.readBytes()
            viewModel.updateImage(byteArray = byteArray)
            inputStream?.close()*/
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Save Contact")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onScaffoldNavigationIconClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                10.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ) {
            if (state.profileImage == null) {
                Image(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Contact Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clickable {
                            profileImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                )
            } else {
//                val image =
//                    BitmapFactory.decodeByteArray(state.profileImage, 0, state.profileImage.size)
//                Image(
//                    bitmap = image.asImageBitmap(),
//                    contentDescription = "Contact Image",
//                    modifier = Modifier
//                        .size(250.dp)
//                        .clickable {
//                            profileImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                        }
//                )
                AsyncImage(
                    model = state.profileImage,
                    contentDescription = "Contact image",
                    modifier = Modifier
                        .size(250.dp)
                        .clickable {
                            profileImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                )
            }
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameValueChanged,
                label = {
                    Text(text = "Name")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailValueChanged,
                label = {
                    Text(text = "Email")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = onPhoneNumberValueChanged,
                label = {
                    Text(text = "Phone number")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Button(
                onClick = {
                    val contact = Contact(
                        id = state.id,
                        name = state.name,
                        email = state.email,
                        phoneNumber = state.phoneNumber,
                        profileImage = state.profileImage,
                        lastEdited = Calendar.getInstance().timeInMillis
                    )
                    onSaveButtonClick(contact)
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}
/*
fun rotateImages(file: File): ByteArray {
    val exifInterface = ExifInterface(file.absolutePath)
    val orientation = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

    val matrix = android.graphics.Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }

    val rotatedBitmap =
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    val stream = ByteArrayOutputStream()
    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
    val imageByteArray = stream.toByteArray()
    stream.close()
    return imageByteArray
}
*/