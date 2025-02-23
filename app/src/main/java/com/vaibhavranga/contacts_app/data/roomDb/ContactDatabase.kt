package com.vaibhavranga.contacts_app.data.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vaibhavranga.contacts_app.data.dao.ContactDao
import com.vaibhavranga.contacts_app.data.entity.Contact

@Database(
    entities = [Contact::class],
    exportSchema = false,
    version = 1
)
abstract class ContactDatabase : RoomDatabase() {

    abstract val contactDao: ContactDao
}