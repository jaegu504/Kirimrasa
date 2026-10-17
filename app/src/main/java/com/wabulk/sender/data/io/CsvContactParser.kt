package com.wabulk.sender.data.io

import com.wabulk.sender.data.local.db.ContactEntity
import java.io.InputStream
import java.nio.charset.StandardCharsets

data class ParsedContact(
    val rawName: String,
    val phoneNumber: String,
    val customNickname: String = "",
    val company: String = "",
    val custom1: String = "",
    val tags: List<String> = emptyList()
) {
    fun toContactEntity(): ContactEntity {
        val jsonTags = if (tags.isEmpty()) {
            "[]"
        } else {
            tags.joinToString(separator = "\",\"", prefix = "[\"", postfix = "\"]") {
                it.replace("\"", "\\\"")
            }
        }
        return ContactEntity(
            rawName = rawName.ifBlank { phoneNumber },
            phoneNumber = phoneNumber,
            customNickname = customNickname,
            tagsJson = jsonTags
        )
    }
}

sealed class CsvParseResult {
    data class Success(
        val contacts: List<ParsedContact>,
        val rowCount: Int,
        val skippedCount: Int
    ) : CsvParseResult()

    data class Error(val message: String) : CsvParseResult()
}

object CsvContactParser {

    private val NAME_HEADERS = setOf(
        "nama", "name", "full name", "fullname", "contact name", "isim",
        "full_name", "contact_name", "nama lengkap"
    )

    private val PHONE_HEADERS = setOf(
        "phone", "telepon", "no hp", "no. hp", "nomor", "wa", "whatsapp",
        "mobile", "telefon", "no_hp", "nohp", "phone number", "phonenumber",
        "no telepon", "mobile phone", "telephone"
    )

    private val NICKNAME_HEADERS = setOf(
        "panggilan", "nickname", "nama panggilan", "call name", "takma ad",
        "takma_ad", "takmaad", "short name", "panggilan akrab"
    )

    private val COMPANY_HEADERS = setOf(
        "company", "perusahaan", "instansi", "organisasi", "sirket", "kurum",
        "organization", "şirket"
    )

    private val CUSTOM1_HEADERS = setOf(
        "custom 1", "custom_1", "custom1", "kustom 1", "kustom_1", "kustom1",
        "ozel 1", "özel 1", "ozel_1", "özel_1", "promo"
    )

    private val TAGS_HEADERS = setOf(
        "tag", "tags", "label", "labels", "kategori", "etiket", "etiketler",
        "group", "groups", "kategoriler"
    )

    private enum class ColumnType {
        NAME, PHONE, NICKNAME, COMPANY, CUSTOM1, TAGS
    }

    fun parseCsv(inputStream: InputStream): CsvParseResult {
        return try {
            val text = inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            parseCsv(text)
        } catch (e: Exception) {
            CsvParseResult.Error("Failed to read CSV input stream: ${e.message}")
        }
    }

    fun parseCsv(csvText: String): CsvParseResult {
        val sanitizedText = csvText.removePrefix("\uFEFF").trim()
        if (sanitizedText.isEmpty()) {
            return CsvParseResult.Error("CSV text is empty or blank")
        }

        val delimiter = detectDelimiter(sanitizedText)
        val rows = parseCsvToRows(sanitizedText, delimiter)
        if (rows.isEmpty()) {
            return CsvParseResult.Error("CSV contains no valid records")
        }

        val headerRow = rows[0]
        val columnMap = mapHeadersToColumns(headerRow)

        val phoneIndex = columnMap[ColumnType.PHONE]
            ?: return CsvParseResult.Error("Missing required phone number column in CSV header")

        val nameIndex = columnMap[ColumnType.NAME]
        val nickIndex = columnMap[ColumnType.NICKNAME]
        val compIndex = columnMap[ColumnType.COMPANY]
        val custom1Index = columnMap[ColumnType.CUSTOM1]
        val tagsIndex = columnMap[ColumnType.TAGS]

        val contacts = mutableListOf<ParsedContact>()
        var rowCount = 0
        var skippedCount = 0

        for (i in 1 until rows.size) {
            val row = rows[i]
            if (row.all { it.isBlank() }) {
                continue
            }
            rowCount++

            val rawPhone = row.getOrElse(phoneIndex) { "" }
            val normalizedPhone = normalizePhoneNumber(rawPhone)

            if (normalizedPhone.isBlank()) {
                skippedCount++
                continue
            }

            val rawName = if (nameIndex != null) row.getOrElse(nameIndex) { "" } else ""
            val nickname = if (nickIndex != null) row.getOrElse(nickIndex) { "" } else ""
            val company = if (compIndex != null) row.getOrElse(compIndex) { "" } else ""
            val custom1 = if (custom1Index != null) row.getOrElse(custom1Index) { "" } else ""
            val rawTags = if (tagsIndex != null) {
                if (tagsIndex == headerRow.lastIndex && row.size > headerRow.size) {
                    row.subList(tagsIndex, row.size).joinToString(";")
                } else {
                    row.getOrElse(tagsIndex) { "" }
                }
            } else ""
            val tags = parseTags(rawTags)

            contacts.add(
                ParsedContact(
                    rawName = rawName,
                    phoneNumber = normalizedPhone,
                    customNickname = nickname,
                    company = company,
                    custom1 = custom1,
                    tags = tags
                )
            )
        }

        return CsvParseResult.Success(
            contacts = contacts,
            rowCount = rowCount,
            skippedCount = skippedCount
        )
    }

    fun detectDelimiter(text: String): Char {
        var commaCount = 0
        var semiCount = 0
        var tabCount = 0
        var inQuotes = false
        var i = 0
        val n = text.length

        while (i < n) {
            val c = text[i]
            if (c == '"') {
                if (inQuotes && i + 1 < n && text[i + 1] == '"') {
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (!inQuotes) {
                if (c == '\n' || c == '\r') {
                    if (commaCount > 0 || semiCount > 0 || tabCount > 0) {
                        break
                    }
                } else {
                    when (c) {
                        ',' -> commaCount++
                        ';' -> semiCount++
                        '\t' -> tabCount++
                    }
                }
            }
            i++
        }

        return when {
            semiCount > commaCount && semiCount >= tabCount -> ';'
            tabCount > commaCount && tabCount > semiCount -> '\t'
            else -> ','
        }
    }

    fun normalizePhoneNumber(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        val hasLeadingPlus = trimmed.startsWith("+")
        val digits = trimmed.filter { it.isDigit() }
        return when {
            digits.isEmpty() -> ""
            hasLeadingPlus -> "+$digits"
            else -> digits
        }
    }

    fun parseTags(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        return raw.split(',', ';', '|')
            .map { it.trim().trim('"', '\'') }
            .filter { it.isNotEmpty() }
    }

    private fun mapHeadersToColumns(headers: List<String>): Map<ColumnType, Int> {
        val map = mutableMapOf<ColumnType, Int>()
        for ((index, header) in headers.withIndex()) {
            val colType = matchHeader(header)
            if (colType != null && !map.containsKey(colType)) {
                map[colType] = index
            }
        }
        return map
    }

    private fun matchHeader(header: String): ColumnType? {
        val clean = header.trim().trim('"', '\'')
            .replace("İ", "i")
            .replace("I", "i")
            .replace("ı", "i")
            .lowercase()
            .replace("\u0307", "")
        val normalized = clean
            .replace("ş", "s")
            .replace("ö", "o")
            .replace("ü", "u")
            .replace("ç", "c")
            .replace("ğ", "g")

        fun checkMatch(candidate: String): ColumnType? {
            return when {
                candidate in PHONE_HEADERS -> ColumnType.PHONE
                candidate in NAME_HEADERS -> ColumnType.NAME
                candidate in NICKNAME_HEADERS -> ColumnType.NICKNAME
                candidate in COMPANY_HEADERS -> ColumnType.COMPANY
                candidate in CUSTOM1_HEADERS -> ColumnType.CUSTOM1
                candidate in TAGS_HEADERS -> ColumnType.TAGS
                else -> null
            }
        }

        checkMatch(clean)?.let { return it }
        checkMatch(normalized)?.let { return it }

        if (clean.contains("/")) {
            val parts = clean.split("/").map { it.trim() }
            for (part in parts) {
                checkMatch(part)?.let { return it }
            }
        }

        return null
    }

    private fun parseCsvToRows(text: String, delimiter: Char): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val currentRow = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false
        var i = 0
        val len = text.length

        while (i < len) {
            val c = text[i]

            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < len && text[i + 1] == '"') {
                        currentField.append('"')
                        i += 2
                        continue
                    } else {
                        inQuotes = false
                        i++
                        continue
                    }
                } else {
                    currentField.append(c)
                    i++
                }
            } else {
                when (c) {
                    '"' -> {
                        inQuotes = true
                        i++
                    }
                    delimiter -> {
                        currentRow.add(currentField.toString().trim())
                        currentField.clear()
                        i++
                    }
                    '\r' -> {
                        if (i + 1 < len && text[i + 1] == '\n') {
                            i++
                        }
                        currentRow.add(currentField.toString().trim())
                        currentField.clear()
                        if (currentRow.any { it.isNotBlank() }) {
                            rows.add(ArrayList(currentRow))
                        }
                        currentRow.clear()
                        i++
                    }
                    '\n' -> {
                        currentRow.add(currentField.toString().trim())
                        currentField.clear()
                        if (currentRow.any { it.isNotBlank() }) {
                            rows.add(ArrayList(currentRow))
                        }
                        currentRow.clear()
                        i++
                    }
                    else -> {
                        currentField.append(c)
                        i++
                    }
                }
            }
        }

        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            currentRow.add(currentField.toString().trim())
            if (currentRow.any { it.isNotBlank() }) {
                rows.add(ArrayList(currentRow))
            }
        }

        return rows
    }
}
