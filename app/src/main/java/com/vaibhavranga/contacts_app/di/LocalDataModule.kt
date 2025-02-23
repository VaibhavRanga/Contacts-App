package com.vaibhavranga.contacts_app.di

import android.content.Context
import androidx.room.Room
import com.vaibhavranga.contacts_app.data.dao.ContactDao
import com.vaibhavranga.contacts_app.data.repository.ContactRepository
import com.vaibhavranga.contacts_app.data.roomDb.ContactDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Provides
    @Singleton
    fun providesContactDatabase(@ApplicationContext context: Context): ContactDatabase {
        return Room.databaseBuilder(
            context,
            ContactDatabase::class.java,
            "contact_database",
        ).build()
    }

    @Provides
    @Singleton
    fun providesContactDao(contactDatabase: ContactDatabase): ContactDao {
        return contactDatabase.contactDao
    }

    @Provides
    @Singleton
    fun providesContactRepository(contactDao: ContactDao): ContactRepository {
        return ContactRepository(contactDao)
    }
}