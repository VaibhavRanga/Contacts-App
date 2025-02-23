package com.vaibhavranga.contacts_app.presentation.screens

import android.icu.util.Calendar
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil3.compose.AsyncImage
import com.vaibhavranga.contacts_app.data.entity.Contact
import com.vaibhavranga.contacts_app.presentation.viewModel.AppState
import com.vaibhavranga.contacts_app.ui.theme.ContactsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsListScreenUI(
    state: AppState,
    onContactLongClick: (Contact) -> Unit,
    onDeleteContactButtonClick: (Contact) -> Unit,
    onFABClick: () -> Unit,
    onCallButtonClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "My Contacts")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFABClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add contact"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        ContactsList(
            state = state,
            onContactLongClick = onContactLongClick,
            onDeleteContactButtonClick = onDeleteContactButtonClick,
            onCallButtonClick = onCallButtonClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ContactsList(
    state: AppState,
    onContactLongClick: (Contact) -> Unit,
    onDeleteContactButtonClick: (Contact) -> Unit,
    onCallButtonClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (state.contactsList.isNotEmpty()) {
            DisplayContactsList(
                state = state,
                onContactLongClick = onContactLongClick,
                onDeleteContactButtonClick = onDeleteContactButtonClick,
                onCallButtonClick = onCallButtonClick
            )
        } else if (state.isLoading) {
            DisplayLoadingUI()
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "No data found!\nClick on the Plus button to add a contact.",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DisplayContactsList(
    state: AppState,
    onContactLongClick: (Contact) -> Unit,
    onDeleteContactButtonClick: (Contact) -> Unit,
    onCallButtonClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(
            items = state.contactsList,
            key = {
                it.id
            }
        ) { contact ->
            ContactCard(
                contact = contact,
                onContactLongClick = onContactLongClick,
                onDeleteContactButtonClick = onDeleteContactButtonClick,
                onCallButtonClick = onCallButtonClick
            )
        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onContactLongClick: (Contact) -> Unit,
    onDeleteContactButtonClick: (Contact) -> Unit,
    onCallButtonClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCallPermissionGranted by remember { mutableStateOf(false) }
    val callPermission = android.Manifest.permission.CALL_PHONE
    val callPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        isCallPermissionGranted = it
    }

    val context = LocalContext.current
    val placeholderString = if (contact.name.isNotBlank()) {
        "${contact.name.first().uppercaseChar()}"
    } else {
        "?"
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        onClick = {
            onContactLongClick(contact)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            if (contact.profileImage != null) {
//                val bitmapImage = BitmapFactory.decodeByteArray(
//                    contact.profileImage,
//                    0,
//                    contact.profileImage.size
//                )
                AsyncImage(
                    model = contact.profileImage,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
//                Image(
//                    bitmap = bitmapImage.asImageBitmap(),
//                    contentDescription = "Contact Image",
//                    modifier = Modifier
//                        .size(64.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop
//                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = placeholderString,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black.copy(alpha = 0.5F)
                    )
                )
            }
            IconButton(
                onClick = {
                    callPermissionRequestLauncher.launch(callPermission)
                    if (isCallPermissionGranted) {
                        onCallButtonClick(contact)
                    } else {
                        Toast.makeText(
                            context,
                            "Please provide permission to call",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call contact"
                )
            }
            IconButton(
                onClick = {
                    onDeleteContactButtonClick(contact)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete contact"
                )
            }
        }
    }
}

@Composable
fun DisplayLoadingUI() {
    Popup {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent.copy(alpha = 0.5F))
        ) {
            CircularProgressIndicator(
                color = Color.Green
            )
        }
    }
}

@Preview
@Composable
private fun CardPreview() {
    ContactsTheme {
        ContactCard(
            contact = Contact(
                id = 0,
                name = "Vaibhav",
                email = "",
                phoneNumber = "1234567890",
                lastEdited = Calendar.getInstance().timeInMillis
            ),
            onContactLongClick = {},
            onDeleteContactButtonClick = {},
            onCallButtonClick = {}
        )
    }
}