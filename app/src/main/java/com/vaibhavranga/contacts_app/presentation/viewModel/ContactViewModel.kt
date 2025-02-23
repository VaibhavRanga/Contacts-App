package com.vaibhavranga.contacts_app.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhavranga.contacts_app.data.entity.Contact
import com.vaibhavranga.contacts_app.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppState(
    val isLoading: Boolean = false,
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImage: ByteArray? = null,
    val contactsList: List<Contact> = emptyList()
)

@HiltViewModel
class ContactViewModel
@Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        getAllContacts()
    }

    fun upsertContact(contact: Contact) {
        if (contact.name.isNotBlank() || contact.email.isNotBlank() || contact.phoneNumber.isNotBlank()) {
            viewModelScope.launch {
                contactRepository.upsertContact(contact)
            }
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.deleteContact(contact)
        }
    }

    private fun getAllContacts() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            contactRepository.getAllContacts().collectLatest { contactsList ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        contactsList = contactsList
                    )
                }
            }
        }
    }

    fun editContact(contact: Contact) {
        _state.update {
            it.copy(
                id = contact.id,
                name = contact.name,
                email = contact.email,
                phoneNumber = contact.phoneNumber,
                profileImage = contact.profileImage
            )
        }
    }

    fun clearContactState() {
        _state.update {
            it.copy(
                id = 0,
                name = "",
                email = "",
                phoneNumber = "",
                profileImage = null
            )
        }
    }

    fun updateNameValue(name: String) {
        _state.update {
            it.copy(
                name = name
            )
        }
    }

    fun updatePhoneNumberValue(phoneNumber: String) {
        _state.update {
            it.copy(
                phoneNumber = phoneNumber
            )
        }
    }

    fun updateImage(byteArray: ByteArray?) {
        _state.update {
            it.copy(
                profileImage = byteArray
            )
        }
    }

    fun updateLastNameValue(email: String) {
        _state.update {
            it.copy(
                email = email
            )
        }
    }
}
