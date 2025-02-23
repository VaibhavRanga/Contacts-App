package com.vaibhavranga.contacts_app.data.repository

import com.vaibhavranga.contacts_app.data.dao.ContactDao
import com.vaibhavranga.contacts_app.data.entity.Contact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepository @Inject constructor(private val contactDao: ContactDao) {

    suspend fun upsertContact(contact: Contact) {
        contactDao.upsertContact(contact)
    }

    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts()
    }
}