package com.vaibhavranga.contacts_app.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vaibhavranga.contacts_app.presentation.navigation.Routes
import com.vaibhavranga.contacts_app.presentation.screens.AddContactScreenUI
import com.vaibhavranga.contacts_app.presentation.screens.ContactsListScreenUI
import com.vaibhavranga.contacts_app.presentation.viewModel.ContactViewModel
import com.vaibhavranga.contacts_app.ui.theme.ContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContactsTheme {
                val contactViewModel by viewModels<ContactViewModel>()
                val state by contactViewModel.state.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.ContactsListScreen,
                    modifier = Modifier.fillMaxSize()
                ) {

                    composable<Routes.ContactsListScreen> {

                        ContactsListScreenUI(
                            state = state,
                            onContactLongClick = { contact ->
                                contactViewModel.editContact(contact)
                                navController.navigate(Routes.AddContactScreen)
                            },
                            onDeleteContactButtonClick = {
                                contactViewModel.deleteContact(it)
                            },
                            onFABClick = {
                                navController.navigate(Routes.AddContactScreen)
                            },
                            onCallButtonClick = {
                                val intent = Intent().apply {
                                    action = Intent.ACTION_CALL
                                    data = android.net.Uri.parse("tel:${it.phoneNumber}")
                                }
                                navController.context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }

                    composable<Routes.AddContactScreen> {

                        AddContactScreenUI(
                            viewModel = contactViewModel,
                            state = state,
                            onSaveButtonClick = { contact ->
                                contactViewModel.upsertContact(contact)
                                contactViewModel.clearContactState()
                                navController.navigateUp()
                            },
                            onNameValueChanged = {
                                contactViewModel.updateNameValue(it)
                            },
                            onEmailValueChanged = {
                                contactViewModel.updateLastNameValue(it)
                            },
                            onPhoneNumberValueChanged = {
                                contactViewModel.updatePhoneNumberValue(
                                    it
                                )
                            },
                            onScaffoldNavigationIconClick = {
                                contactViewModel.clearContactState()
                                navController.navigateUp()
                            }
                        )
                        BackHandler {
                            navController.navigateUp()
                            contactViewModel.clearContactState()
                        }
                    }
                }
            }
        }
    }
}
