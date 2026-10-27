package com.wabulk.sender.data.io

import com.wabulk.sender.data.locale.LocalizationManager
import java.io.ByteArrayOutputStream

/** Reads vCard (.vcf) files, e.g. the export from the phone's Contacts app or Google Contacts. */
object VcfContactParser {

    fun looksLikeVcf(text: String): Boolean =
        text.removePrefix("﻿").trimStart().startsWith("BEGIN:VCARD", ignoreCase = true)

    fun parse(text: String): CsvParseResult {
        val cards = mutableListOf<List<String>>()
        var current: MutableList<String>? = null
        for (line in unfold(text.removePrefix("﻿"))) {
            when {
                line.equals("BEGIN:VCARD", ignoreCase = true) -> current = mutableListOf()
                line.equals("END:VCARD", ignoreCase = true) -> {
                    current?.let { cards.add(it) }
                    current = null
                }
                else -> current?.add(line)
            }
        }
        if (cards.isEmpty()) {
            return CsvParseResult.Error(LocalizationManager.activeStrings.csvNoRecords)
        }

        val contacts = mutableListOf<ParsedContact>()
        var skipped = 0
        for (card in cards) {
            val contact = parseCard(card)
            if (contact == null) skipped++ else contacts.add(contact)
        }
        return CsvParseResult.Success(contacts = contacts, rowCount = cards.size, skippedCount = skipped)
    }

    private fun parseCard(lines: List<String>): ParsedContact? {
        var fullName = ""
        var structuredName = ""
        var nickname = ""
        var company = ""
        var tags = emptyList<String>()
        val phones = mutableListOf<Pair<String, String>>() // params, number

        for (line in lines) {
            val colon = line.indexOf(':')
            if (colon <= 0) continue
            val head = line.substring(0, colon)
            // "item1.TEL;TYPE=CELL" -> name TEL, params TYPE=CELL
            val parts = head.split(';')
            val name = parts[0].substringAfterLast('.').uppercase()
            val params = parts.drop(1).joinToString(";").uppercase()
            val value = decode(line.substring(colon + 1), params)
            when (name) {
                "FN" -> fullName = unescape(value)
                "N" -> structuredName = value.split(';').let { n ->
                    listOf(n.getOrElse(1) { "" }, n.getOrElse(2) { "" }, n.getOrElse(0) { "" })
                        .map { unescape(it).trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString(" ")
                }
                "NICKNAME" -> nickname = unescape(value.split(',').first()).trim()
                "ORG" -> company = unescape(value.split(';').first()).trim()
                "CATEGORIES" -> tags = value.split(',').map { unescape(it).trim() }.filter { it.isNotEmpty() }
                "TEL" -> phones.add(params to value)
            }
        }

        // A mobile number is the one WhatsApp most likely uses
        val phone = (phones.firstOrNull { "CELL" in it.first } ?: phones.firstOrNull())
            ?.second
            ?.removePrefix("tel:")
            ?.let { CsvContactParser.normalizePhoneNumber(it) }
            .orEmpty()
        if (phone.isBlank()) return null

        return ParsedContact(
            rawName = fullName.ifBlank { structuredName },
            phoneNumber = phone,
            customNickname = nickname,
            company = company,
            tags = tags
        )
    }

    /** Joins folded lines; quoted-printable soft breaks ("=" at line end) continue on the next line. */
    private fun unfold(text: String): List<String> {
        val out = mutableListOf<String>()
        for (raw in text.split("\r\n", "\n", "\r")) {
            val last = out.lastOrNull()
            when {
                last != null && last.endsWith("=") && "QUOTED-PRINTABLE" in last.substringBefore(':').uppercase() ->
                    out[out.lastIndex] = last.dropLast(1) + raw.trimStart()
                last != null && (raw.startsWith(" ") || raw.startsWith("\t")) ->
                    out[out.lastIndex] = last + raw.substring(1)
                raw.isNotBlank() -> out.add(raw.trim())
            }
        }
        return out
    }

    private fun decode(value: String, params: String): String {
        if ("QUOTED-PRINTABLE" !in params) return value
        val bytes = ByteArrayOutputStream()
        var i = 0
        while (i < value.length) {
            val c = value[i]
            if (c == '=' && i + 2 <= value.lastIndex) {
                val hex = value.substring(i + 1, i + 3).toIntOrNull(16)
                if (hex != null) {
                    bytes.write(hex)
                    i += 3
                    continue
                }
            }
            bytes.write(c.toString().toByteArray(Charsets.UTF_8))
            i++
        }
        return bytes.toString("UTF-8")
    }

    private fun unescape(value: String): String =
        value.replace("\\n", " ").replace("\\N", " ").replace("\\,", ",").replace("\\;", ";").replace("\\\\", "\\")
}
