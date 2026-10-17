package com.wabulk.sender.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiRewriteResponse(
    @SerialName("success")
    val success: Boolean = true,

    @SerialName("engine")
    val engine: String = "",

    @SerialName("model")
    val model: String = "",

    @SerialName("bubbles")
    val bubbles: List<String> = emptyList(),

    @SerialName("tone")
    val tone: String = "friendly",

    @SerialName("error")
    val error: String? = null
)
