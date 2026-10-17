package com.wabulk.sender.data.io

import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity

object CampaignCsvExporter {

    private const val HEADER = "Campaign ID,Campaign Title,Recipient Phone,Recipient Name,Status,Error Message,Sent At Timestamp,Message Preview"

    fun generateCsv(campaign: CampaignEntity, items: List<CampaignItemEntity>): String {
        val sb = StringBuilder()
        sb.append(HEADER).append("\r\n")

        for (item in items) {
            val campaignId = campaign.id.toString()
            val campaignTitle = escapeField(campaign.title)
            val recipientPhone = escapeField(item.recipientPhone)
            val recipientName = escapeField(item.recipientName)
            val status = escapeField(item.status)
            val errorMessage = escapeField(item.errorMessage ?: "")
            val sentAt = item.sentAt.toString()

            val sanitizedPreview = item.messageText
                .replace("\r\n", " ")
                .replace("\n", " ")
                .replace("\r", " ")
            val messagePreview = escapeField(sanitizedPreview)

            sb.append(campaignId).append(',')
                .append(campaignTitle).append(',')
                .append(recipientPhone).append(',')
                .append(recipientName).append(',')
                .append(status).append(',')
                .append(errorMessage).append(',')
                .append(sentAt).append(',')
                .append(messagePreview).append("\r\n")
        }

        return sb.toString()
    }

    private fun escapeField(value: String): String {
        if (value.contains('"') || value.contains(',') || value.contains(';') || value.contains('\n') || value.contains('\r')) {
            val escaped = value.replace("\"", "\"\"")
            return "\"$escaped\""
        }
        return value
    }
}
