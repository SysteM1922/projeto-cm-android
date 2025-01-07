package com.example.driverapp

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

object NFCParser {
    fun parseCardIDFromNdefMessage(ndefMessage: NdefMessage): String {
        // Example logic: convert the first record to a String, skip the first 3 chars, etc.
        val payload = String(ndefMessage.records[0].payload, Charsets.UTF_8)
        return payload.substring(3)
    }

    /**
     * Manually build a TEXT record for testing, to avoid calling createTextRecord().
     * This record has a payload: [1-byte language length] + [language bytes] + [text bytes].
     * Example: "enHelloWorld" ->
     *   0x02 (length of "en"), then [0x65, 0x6e], then "HelloWorld" bytes
     */
    fun buildTextRecord(languageCode: String, text: String): NdefRecord {
        val langBytes = languageCode.toByteArray(Charsets.US_ASCII)
        val textBytes = text.toByteArray(Charsets.UTF_8)
        val payload = ByteArray(1 + langBytes.size + textBytes.size)

        // Byte 0: length of the language code
        payload[0] = langBytes.size.toByte()

        // Next part: language code bytes
        System.arraycopy(langBytes, 0, payload, 1, langBytes.size)

        // Following part: actual text
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.size, textBytes.size)

        // Build an NdefRecord with TNF_WELL_KNOWN + RTD_TEXT
        return NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT,
            ByteArray(0),
            payload
        )
    }
}

@RunWith(RobolectricTestRunner::class)
class NFCParserTest {

    @Test
    fun parseCardID_test() {
        // 1) Create a fake NdefRecord (manually built)
        val fakeRecord: NdefRecord = NFCParser.buildTextRecord("en", "HelloWorld")

        // 2) Create an NdefMessage
        val fakeNdefMessage = NdefMessage(arrayOf(fakeRecord))

        // 3) Parse
        val cardID = NFCParser.parseCardIDFromNdefMessage(fakeNdefMessage)

        // "enHelloWorld" -> skipping 3 chars -> "HelloWorld"
        assertEquals("HelloWorld", cardID)
    }

    @Test
    fun parseCardID_multipleRecords() {
        // 1) Build multiple records
        val record1 = NFCParser.buildTextRecord("en", "FirstRecord")
        val record2 = NFCParser.buildTextRecord("en", "SecondRecord")

        // 2) NdefMessage with 2 records
        val fakeNdefMessage = NdefMessage(arrayOf(record1, record2))

        // 3) Parse (we only parse the first record in parseCardIDFromNdefMessage)
        val cardID = NFCParser.parseCardIDFromNdefMessage(fakeNdefMessage)

        // Should parse "enFirstRecord" -> "FirstRecord"
        assertEquals("FirstRecord", cardID)
    }

    @Test
    fun parseCardID_emptyText() {
        // Build a record with empty text
        val record = NFCParser.buildTextRecord("en", "")

        val fakeNdefMessage = NdefMessage(arrayOf(record))
        val cardID = NFCParser.parseCardIDFromNdefMessage(fakeNdefMessage)

        // "en" => substring(3) => might be out of range if you don't guard,
        // so maybe you need to handle that in your parsing.
        // For now let's say we'd expect an empty string
        assertEquals("", cardID)
    }
}
