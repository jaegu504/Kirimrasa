package com.wabulk.sender.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "total_recipients")
    val totalRecipients: Int,

    @ColumnInfo(name = "sent_count")
    val sentCount: Int = 0,

    @ColumnInfo(name = "failed_count")
    val failedCount: Int = 0,

    @ColumnInfo(name = "status")
    val status: String = "COMPLETED",

    @ColumnInfo(name = "node_used")
    val nodeUsed: String = "WA Personal",

    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Long = 0
)
