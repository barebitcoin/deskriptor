package no.bb.deskriptor.diffpub

import fr.acinq.bitcoin.Base58Check

// Convert an extended public key (zpub, ypub, xpub) to an xpub. The different 
// foopubs are really just the same data, with a versioned indicated by the first
// four bytes. By swapping out the different version bytes we can convert between 
// the two. 
// Cribbed from https://github.com/jlopp/xpub-converter/blob/master/js/xpubConvert.js
fun toXpub(input: String): String {
    val (_, data) = Base58Check.decode(input)

    val xpubPrefix = decodeHex("0488b21e")

    // Don't know why, but the ACINQ library removes one of the bytes when
    // decoding, and returns it as the first element of the tuple. We therefore
    // set this to 3, and not 4.
    val prefixLength = 3
    return Base58Check.encode(xpubPrefix, data.drop(prefixLength).toByteArray())
}

// https://stackoverflow.com/a/66614516/10359642
fun decodeHex(str: String): ByteArray {
    check(str.length % 2 == 0) { "Must have an even length" }

    return str.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
