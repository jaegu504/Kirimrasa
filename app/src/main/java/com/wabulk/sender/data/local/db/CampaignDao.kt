package com.wabulk.sender.data.local.db

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class CampaignWithItems(
    @Embedded
    val campaign: CampaignEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "campaign_id"
    )
    val items: List<CampaignItemEntity>
)

@Dao
interface CampaignDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CampaignItemEntity>): List<Long>

    @Update
    suspend fun updateCampaign(campaign: CampaignEntity)

    @Query("SELECT * FROM campaigns ORDER BY created_at DESC")
    fun observeAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :id LIMIT 1")
    suspend fun getCampaignById(id: Long): CampaignEntity?

    @Query("SELECT * FROM campaign_items WHERE campaign_id = :campaignId ORDER BY id ASC")
    suspend fun getItemsForCampaign(campaignId: Long): List<CampaignItemEntity>

    @Query("SELECT * FROM campaign_items WHERE campaign_id = :campaignId ORDER BY id ASC")
    fun observeItemsForCampaign(campaignId: Long): Flow<List<CampaignItemEntity>>

    @Query("SELECT * FROM campaigns ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentCampaigns(limit: Int = 20): List<CampaignEntity>

    @Transaction
    @Query("SELECT * FROM campaigns WHERE id = :id")
    suspend fun getCampaignWithItems(id: Long): CampaignWithItems?

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteCampaign(id: Long)
}
