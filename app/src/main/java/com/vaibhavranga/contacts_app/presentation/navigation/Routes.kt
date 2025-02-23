package com.vaibhavranga.contacts_app.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object ContactsListScreen : Routes()

    @Serializable
    data object AddContactScreen : Routes()
}