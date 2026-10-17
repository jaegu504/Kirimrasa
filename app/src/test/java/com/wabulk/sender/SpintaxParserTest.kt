package com.wabulk.sender

import com.wabulk.sender.domain.spintax.SpintaxParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpintaxParserTest {

    @Test
    fun `parse resolves single choice spintax options`() {
        val input = "{Halo|Hai} rekan bisnis!"
        val result = SpintaxParser.spin(input)
        assertTrue(result == "Halo rekan bisnis!" || result == "Hai rekan bisnis!")
    }

    @Test
    fun `parse resolves multiple independent spintax blocks`() {
        val input = "{Pagi|Siang|Sore} {Kak|Pak|Bu}, ada {promo|diskon} menarik!"
        val result = SpintaxParser.spin(input)
        assertFalse(result.contains("{"))
        assertFalse(result.contains("}"))
        assertFalse(result.contains("|"))
    }

    @Test
    fun `parse leaves variable tokens untouched`() {
        val input = "{Halo|Hai} {Nickname}! Kami dari {Company}."
        val result = SpintaxParser.spin(input)
        assertTrue(result.contains("{Nickname}"))
        assertTrue(result.contains("{Company}"))
        assertTrue(result.startsWith("Halo ") || result.startsWith("Hai "))
    }

    @Test
    fun `interpolateVariables replaces tokens with actual values`() {
        val template = "Halo {Nickname}! Selamat datang di {Company}."
        val filled = SpintaxParser.interpolateVariables(
            text = template,
            nickname = "Budi",
            variables = mapOf("Company" to "Tech Corp")
        )
        assertEquals("Halo Budi! Selamat datang di Tech Corp.", filled)
    }

    @Test
    fun `interpolateVariables falls back cleanly when nickname is blank`() {
        val template = "Halo {Nickname}! Apa kabar?"
        val filled = SpintaxParser.interpolateVariables(
            text = template,
            nickname = "",
            variables = emptyMap()
        )
        // Should replace {Nickname} with Kak / Sahabat / empty without leaving {Nickname}
        assertFalse(filled.contains("{Nickname}"))
    }

    @Test
    fun `countVariations calculates total permutations accurately`() {
        val input = "{Halo|Hai} {Kak|Pak|Bu}! {Promo|Diskon} {hari ini|spesial}."
        // 2 * 3 * 2 * 2 = 24 permutations
        val count = SpintaxParser.countVariations(input)
        assertEquals(24, count)
    }
}
