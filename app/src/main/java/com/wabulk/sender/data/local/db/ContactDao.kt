package com.wabulk.sender.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY raw_name ASC")
    fun observeAll(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE raw_name LIKE '%' || :query || '%' OR phone_number LIKE '%' || :query || '%' OR custom_nickname LIKE '%' || :query || '%' ORDER BY raw_name ASC")
    fun search(query: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE tags_json LIKE '%' || :tag || '%' ORDER BY raw_name ASC")
    fun filterByTag(tag: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE (tags_json LIKE '%' || :tag || '%') AND (raw_name LIKE '%' || :query || '%' OR phone_number LIKE '%' || :query || '%' OR custom_nickname LIKE '%' || :query || '%') ORDER BY raw_name ASC")
    fun searchWithTag(query: String, tag: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE phone_number = :phone LIMIT 1")
    suspend fun findByPhone(phone: String): ContactEntity?

    @Query("SELECT COUNT(*) FROM contacts")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM contacts WHERE is_selected = 1")
    fun observeSelectedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM contacts WHERE custom_nickname != ''")
    fun observeNicknameCount(): Flow<Int>

    @Upsert
    suspend fun upsert(contact: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(contacts: List<ContactEntity>): List<Long>

    @Update
    suspend fun update(contact: ContactEntity)

    @Query("UPDATE contacts SET custom_nickname = :nickname WHERE id = :contactId")
    suspend fun updateNickname(contactId: Long, nickname: String)

    @Query("UPDATE contacts SET is_selected = :selected WHERE id = :contactId")
    suspend fun updateSelection(contactId: Long, selected: Boolean)

    @Query("UPDATE contacts SET is_selected = :selected WHERE tags_json LIKE '%' || :tag || '%'")
    suspend fun selectAllInTag(tag: String, selected: Boolean)

    @Query("UPDATE contacts SET tags_json = :tagsJson WHERE id = :contactId")
    suspend fun updateTags(contactId: Long, tagsJson: String)

    @Query("SELECT * FROM contacts WHERE is_selected = 1 ORDER BY raw_name ASC")
    fun observeSelected(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE is_selected = 1")
    suspend fun getSelected(): List<ContactEntity>

    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun delete(contactId: Long)

    @Query("SELECT * FROM contacts")
    suspend fun getAll(): List<ContactEntity>
}
