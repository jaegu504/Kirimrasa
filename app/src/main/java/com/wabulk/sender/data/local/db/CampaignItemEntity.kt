package com.wabulk.sender.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class CampaignItemStatus {
    PENDING,
    SENDING,
    SENT,
    FAILED
}

@Entity(
    tableName = "campaign_items",
    foreignKeys = [
        ForeignKey(
            entity = CampaignEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaign_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["campaign_id"])
    ]
)
data class CampaignItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "campaign_id")
    val campaignId: Long,

    @ColumnInfo(name = "recipient_phone")
    val recipientPhone: String,

    @ColumnInfo(name = "recipient_name")
    val recipientName: String,

    @ColumnInfo(name = "message_text")
    val messageText: String,

    @ColumnInfo(name = "status")
    val status: String = CampaignItemStatus.PENDING.name,

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null,

    @ColumnInfo(name = "sent_at")
    val sentAt: Long = System.currentTimeMillis()
) {
    val itemStatus: CampaignItemStatus
        get() = try {
            CampaignItemStatus.valueOf(status)
        } catch (_: IllegalArgumentException) {
            CampaignItemStatus.PENDING
        }
}
