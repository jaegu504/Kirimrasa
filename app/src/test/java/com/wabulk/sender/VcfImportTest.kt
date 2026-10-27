package com.wabulk.sender

import com.wabulk.sender.data.io.CsvContactParser
import com.wabulk.sender.data.io.CsvParseResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VcfImportTest {

    @Test
    fun `parses vCard 3 and 2_1 exports and prefers the mobile number`() {
        val vcf = listOf(
            "BEGIN:VCARD",
            "VERSION:3.0",
            "FN:Budi Santoso",
            "N:Santoso;Budi;;;",
            "NICKNAME:Bud",
            "ORG:Acme;Sales",
            "CATEGORIES:keluarga,vip",
            "item1.TEL;TYPE=HOME:021-555 1234",
            "TEL;TYPE=CELL:+62 812-3456-7890",
            "END:VCARD",
            "BEGIN:VCARD",
            "VERSION:2.1",
            "N;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:;=C3=87a=C4=9Flar;;;",
            "FN;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:=C3=87a=C4=9Flar Y=C4=B1lmaz=",
            "=20Bey",
            "TEL;CELL:0532 111 22 33",
            "END:VCARD",
            "BEGIN:VCARD",
            "VERSION:3.0",
            "FN:No Number",
            "END:VCARD"
        ).joinToString("\r\n")

        val result = CsvContactParser.parseCsv(vcf)
        assertTrue(result is CsvParseResult.Success)
        result as CsvParseResult.Success
        assertEquals(3, result.rowCount)
        assertEquals(1, result.skippedCount)

        val budi = result.contacts[0]
        assertEquals("Budi Santoso", budi.rawName)
        assertEquals("+6281234567890", budi.phoneNumber)
        assertEquals("Bud", budi.customNickname)
        assertEquals("Acme", budi.company)
        assertEquals(listOf("keluarga", "vip"), budi.tags)

        val caglar = result.contacts[1]
        assertEquals("Çağlar Yılmaz Bey", caglar.rawName)
        assertEquals("05321112233", caglar.phoneNumber)
    }

    @Test
    fun `folded lines and name from N when FN is missing`() {
        val vcf = "BEGIN:VCARD\nN:Rahman;Siti;;;\nTEL:+90 555\n 123 4567\nEND:VCARD\n"
        val result = CsvContactParser.parseCsv(vcf) as CsvParseResult.Success
        assertEquals("Siti Rahman", result.contacts.single().rawName)
        assertEquals("+905551234567", result.contacts.single().phoneNumber)
    }
}
