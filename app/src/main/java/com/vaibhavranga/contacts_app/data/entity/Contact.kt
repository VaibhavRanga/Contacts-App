package com.vaibhavranga.contacts_app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_table")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "lastName", defaultValue = "")
    val email: String,
    val phoneNumber: String,
    val profileImage: ByteArray? = null,
    val lastEdited: Long,
)
