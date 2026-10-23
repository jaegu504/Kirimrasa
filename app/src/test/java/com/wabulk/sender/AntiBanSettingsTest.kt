package com.wabulk.sender

import com.wabulk.sender.engine.queue.AntiBanConfig
import com.wabulk.sender.engine.queue.AntiBanSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AntiBanSettingsTest {

    @Test
    fun `presets map to documented ranges`() {
        assertEquals(35..75, AntiBanConfig(preset = AntiBanConfig.PRESET_CONSERVATIVE).delayRangeSec)
        assertEquals(25..65, AntiBanConfig(preset = AntiBanConfig.PRESET_OPTIMAL).delayRangeSec)
        assertEquals(15..30, AntiBanConfig(preset = AntiBanConfig.PRESET_TURBO).delayRangeSec)
        assertEquals(40..90, AntiBanConfig(preset = AntiBanConfig.PRESET_CUSTOM, customMinSec = 40, customMaxSec = 90).delayRangeSec)
    }

    @Test
    fun `custom range is clamped to floor, ceiling and minimum spread`() {
        val tooLow = AntiBanSettings.normalize(AntiBanConfig(preset = AntiBanConfig.PRESET_CUSTOM, customMinSec = 1, customMaxSec = 2))
        assertEquals(AntiBanConfig.CUSTOM_FLOOR_SEC, tooLow.customMinSec)
        assertEquals(AntiBanConfig.CUSTOM_FLOOR_SEC + AntiBanConfig.MIN_SPREAD_SEC, tooLow.customMaxSec)

        val tooHigh = AntiBanSettings.normalize(AntiBanConfig(customMinSec = 999, customMaxSec = 999))
        assertEquals(AntiBanConfig.CUSTOM_CEIL_SEC, tooHigh.customMaxSec)
        assertTrue(tooHigh.customMinSec <= tooHigh.customMaxSec - AntiBanConfig.MIN_SPREAD_SEC)
    }

    @Test
    fun `contact delay stays inside range and is never the old fixed 4 seconds`() {
        val config = AntiBanConfig(preset = AntiBanConfig.PRESET_OPTIMAL)
        val random = Random(42)
        repeat(1000) {
            val d = AntiBanSettings.nextContactDelaySec(config, random)
            assertTrue("delay $d out of range", d in 25..65)
        }
    }

    @Test
    fun `bubble delay is 3 to 6 seconds`() {
        val random = Random(7)
        repeat(1000) {
            val ms = AntiBanSettings.nextBubbleDelayMs(random)
            assertTrue("bubble delay $ms out of range", ms in 3000L..6000L)
        }
    }
}
