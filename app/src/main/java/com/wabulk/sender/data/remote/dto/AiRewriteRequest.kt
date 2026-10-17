package com.wabulk.sender.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiRewriteRequest(
    @SerialName("message")
    val message: String,

    @SerialName("prompt")
    val prompt: String = message,

    @SerialName("language")
    val language: String = "ID", // "ID" atau "TR"

    @SerialName("tone")
    val tone: String = "friendly", // "friendly", "shorter", "maxVariance"

    @SerialName("maxBubbles")
    val maxBubbles: Int = 3
)
