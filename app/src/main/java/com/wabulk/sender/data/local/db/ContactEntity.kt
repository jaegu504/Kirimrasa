package com.wabulk.sender.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contacts",
    indices = [
        Index(value = ["phone_number"], unique = true),
        Index(value = ["system_lookup_key"])
    ]
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "system_lookup_key")
    val systemLookupKey: String = "",

    @ColumnInfo(name = "raw_name")
    val rawName: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "custom_nickname")
    val customNickname: String = "",

    // Second way to call the contact, for {Nickname2}: one bubble that names someone twice reads less like a template
    @ColumnInfo(name = "custom_nickname2", defaultValue = "")
    val customNickname2: String = "",

    @ColumnInfo(name = "company", defaultValue = "")
    val company: String = "",

    @ColumnInfo(name = "custom1", defaultValue = "")
    val custom1: String = "",

    @ColumnInfo(name = "tags_json")
    val tagsJson: String = "[]",

    @ColumnInfo(name = "is_selected")
    val isSelected: Boolean = false,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long = System.currentTimeMillis()
)
