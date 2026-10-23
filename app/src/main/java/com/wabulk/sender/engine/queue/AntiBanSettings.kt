package com.wabulk.sender.engine.queue

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class AntiBanConfig(
    val preset: Int = PRESET_OPTIMAL,
    val customMinSec: Int = 30,
    val customMaxSec: Int = 70,
    val typingSimEnabled: Boolean = true,
    val pauseDurationMin: Int = 5,
    val batchSize: Int = 25
) {
    val delayRangeSec: IntRange
        get() = when (preset) {
            PRESET_CONSERVATIVE -> 35..75
            PRESET_TURBO -> 15..30
            PRESET_CUSTOM -> customMinSec..customMaxSec
            else -> 25..65
        }

    companion object {
        const val PRESET_CONSERVATIVE = 0
        const val PRESET_OPTIMAL = 1
        const val PRESET_TURBO = 2
        const val PRESET_CUSTOM = 3

        // Below this WhatsApp flags the pattern quickly; the UI warns under SAFE_MIN_SEC
        const val CUSTOM_FLOOR_SEC = 10
        const val CUSTOM_CEIL_SEC = 300
        const val SAFE_MIN_SEC = 25
        const val MIN_SPREAD_SEC = 5
    }
}

/** Anti-ban delays chosen in Settings, persisted and read by [DispatchQueueManager]. */
object AntiBanSettings {
    private const val PREFS = "anti_ban_prefs"

    private val _config = MutableStateFlow(AntiBanConfig())
    val config: StateFlow<AntiBanConfig> = _config.asStateFlow()

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val d = AntiBanConfig()
        _config.value = AntiBanConfig(
            preset = p.getInt("preset", d.preset),
            customMinSec = p.getInt("custom_min", d.customMinSec),
            customMaxSec = p.getInt("custom_max", d.customMaxSec),
            typingSimEnabled = p.getBoolean("typing_sim", d.typingSimEnabled),
            pauseDurationMin = p.getInt("pause_min", d.pauseDurationMin),
            batchSize = p.getInt("batch_size", d.batchSize)
        )
    }

    fun update(transform: (AntiBanConfig) -> AntiBanConfig) {
        _config.value = normalize(transform(_config.value))
        val c = _config.value
        appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()
            ?.putInt("preset", c.preset)
            ?.putInt("custom_min", c.customMinSec)
            ?.putInt("custom_max", c.customMaxSec)
            ?.putBoolean("typing_sim", c.typingSimEnabled)
            ?.putInt("pause_min", c.pauseDurationMin)
            ?.putInt("batch_size", c.batchSize)
            ?.apply()
    }

    /** Keeps custom min/max inside the floor/ceiling and max at least MIN_SPREAD_SEC above min. */
    fun normalize(c: AntiBanConfig): AntiBanConfig {
        val min = c.customMinSec.coerceIn(AntiBanConfig.CUSTOM_FLOOR_SEC, AntiBanConfig.CUSTOM_CEIL_SEC - AntiBanConfig.MIN_SPREAD_SEC)
        val max = c.customMaxSec.coerceIn(min + AntiBanConfig.MIN_SPREAD_SEC, AntiBanConfig.CUSTOM_CEIL_SEC)
        return c.copy(
            customMinSec = min,
            customMaxSec = max,
            pauseDurationMin = c.pauseDurationMin.coerceIn(1, 60),
            batchSize = c.batchSize.coerceIn(5, 100)
        )
    }

    /** Delay before the next contact: random within the range, biased to the middle (sum of two uniforms). */
    fun nextContactDelaySec(config: AntiBanConfig = _config.value, random: Random = Random.Default): Int {
        val r = config.delayRangeSec
        val span = r.last - r.first
        return r.first + ((random.nextInt(span + 1) + random.nextInt(span + 1)) / 2)
    }

    /** Typing pause between bubbles to the same contact: 3–6 s. */
    fun nextBubbleDelayMs(random: Random = Random.Default): Long = random.nextLong(3000L, 6001L)
}
