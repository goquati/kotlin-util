package de.quati.kotlin.util.poet

public fun String.toCamelCase(capitalized: Boolean): String = toNameParts()
    .mapIndexed { idx, s ->
        s.replaceFirstChar {
            if (idx == 0 && !capitalized)
                it.lowercaseChar()
            else
                it.titlecaseChar()
        }
    }.joinToString(separator = "")
    .toValidName()

public fun String.toKebabCase(): String = toNameParts()
    .joinToString(separator = "-") { it.lowercase() }
    .toValidName()

public fun String.toSnakeCase(uppercase: Boolean = false): String = toNameParts()
    .joinToString(separator = "_") { if (uppercase) it.uppercase() else it.lowercase() }
    .toValidName()

public fun String.makeDifferent(blackList: Iterable<String>, separator: String = "_"): String {
    val blackListSet = blackList.toSet()
    if (this !in blackListSet) return this
    return generateSequence(1) { it + 1 }
        .map { "$this$separator$it" }
        .first { it !in blackListSet }
}

public fun String.toAsciiIdentifierLike(): String = buildString(length) {
    for (ch in this@toAsciiIdentifierLike) {
        if (ch.isCombiningMark()) continue
        val repl = ch.transliterateLatinToAscii()
        when {
            repl != null -> append(repl)
            ch.isLetterOrDigit() || ch == '_' || ch == ' ' || ch == '-' -> append(ch)
            else -> Unit
        }
    }
}

public val kotlinKeywords: Set<String> = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "val", "var",
    "when", "while", "by", "catch", "constructor", "delegate", "dynamic",
    "field", "file", "finally", "get", "import", "init", "param", "property",
    "receiver", "set", "setparam", "where", "actual", "abstract", "annotation",
    "companion", "const", "crossinline", "data", "enum", "expect", "external",
    "final", "infix", "inline", "inner", "internal", "lateinit", "noinline",
    "open", "operator", "out", "override", "private", "protected", "public",
    "reified", "sealed", "suspend", "tailrec", "vararg"
)

private fun String.toValidName() = when {
    isEmpty() -> "_empty"
    first().isDigit() -> "_$this"
    !first().isLetter() -> "_$this"
    else -> this
}

private fun String.toNameParts(): List<String> =
    toAsciiIdentifierLike()
        .split(' ', '_', '-')
        .filter { it.isNotEmpty() }

private fun Char.isCombiningMark(): Boolean =
    this in '\u0300'..'\u036F' || this in '\u1AB0'..'\u1AFF' || this in '\u1DC0'..'\u1DFF'

private fun Char.transliterateLatinToAscii(): String? = when (this) {
    'ä' -> "ae"; 'ö' -> "oe"; 'ü' -> "ue"
    'Ä' -> "Ae"; 'Ö' -> "Oe"; 'Ü' -> "Ue"
    'ß' -> "ss"; 'ẞ' -> "SS"

    'À', 'Á', 'Â', 'Ã', 'Å', 'Ā', 'Ă', 'Ą', 'Ǎ', 'Ȁ', 'Ȃ', 'Ạ', 'Ả', 'Ấ', 'Ầ', 'Ẩ', 'Ẫ', 'Ậ', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ', 'Ặ' -> "A"
    'à', 'á', 'â', 'ã', 'å', 'ā', 'ă', 'ą', 'ǎ', 'ȁ', 'ȃ', 'ạ', 'ả', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ' -> "a"

    'Ç', 'Ć', 'Ĉ', 'Ċ', 'Č' -> "C"
    'ç', 'ć', 'ĉ', 'ċ', 'č' -> "c"

    'Ð', 'Ď', 'Đ' -> "D"
    'ð', 'ď', 'đ' -> "d"

    'È', 'É', 'Ê', 'Ë', 'Ē', 'Ĕ', 'Ė', 'Ę', 'Ě', 'Ȅ', 'Ȇ', 'Ẹ', 'Ẻ', 'Ẽ', 'Ế', 'Ề', 'Ể', 'Ễ', 'Ệ' -> "E"
    'è', 'é', 'ê', 'ë', 'ē', 'ĕ', 'ė', 'ę', 'ě', 'ȅ', 'ȇ', 'ẹ', 'ẻ', 'ẽ', 'ế', 'ề', 'ể', 'ễ', 'ệ' -> "e"

    'Ĝ', 'Ğ', 'Ġ', 'Ģ' -> "G"
    'ĝ', 'ğ', 'ġ', 'ģ' -> "g"

    'Ĥ', 'Ħ' -> "H"
    'ĥ', 'ħ' -> "h"

    'Ì', 'Í', 'Î', 'Ï', 'Ī', 'Ĭ', 'Į', 'İ', 'Ǐ', 'Ȉ', 'Ȋ', 'Ị', 'Ỉ', 'Ĩ' -> "I"
    'ì', 'í', 'î', 'ï', 'ī', 'ĭ', 'į', 'ǐ', 'ȉ', 'ȋ', 'ị', 'ỉ', 'ĩ' -> "i"

    'Ĵ' -> "J"
    'ĵ' -> "j"

    'Ķ' -> "K"
    'ķ' -> "k"

    'Ĺ', 'Ļ', 'Ľ', 'Ł' -> "L"
    'ĺ', 'ļ', 'ľ', 'ł' -> "l"

    'Ñ', 'Ń', 'Ņ', 'Ň' -> "N"
    'ñ', 'ń', 'ņ', 'ň' -> "n"

    'Ò', 'Ó', 'Ô', 'Õ', 'Ø', 'Ō', 'Ŏ', 'Ő', 'Ǒ', 'Ȍ', 'Ȏ', 'Ọ', 'Ỏ', 'Ố', 'Ồ', 'Ổ', 'Ỗ', 'Ộ', 'Ớ', 'Ờ', 'Ở', 'Ỡ', 'Ợ' -> "O"
    'ò', 'ó', 'ô', 'õ', 'ø', 'ō', 'ŏ', 'ő', 'ǒ', 'ȍ', 'ȏ', 'ọ', 'ỏ', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ' -> "o"

    'Ŕ', 'Ŗ', 'Ř' -> "R"
    'ŕ', 'ŗ', 'ř' -> "r"

    'Ś', 'Ŝ', 'Ş', 'Š' -> "S"
    'ś', 'ŝ', 'ş', 'š' -> "s"

    'Þ', 'Ť', 'Ţ', 'Ŧ' -> "T"
    'þ', 'ť', 'ţ', 'ŧ' -> "t"

    'Ù', 'Ú', 'Û', 'Ũ', 'Ū', 'Ŭ', 'Ů', 'Ű', 'Ų', 'Ǔ', 'Ȕ', 'Ȗ', 'Ụ', 'Ủ', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự' -> "U"
    'ù', 'ú', 'û', 'ũ', 'ū', 'ŭ', 'ů', 'ű', 'ų', 'ǔ', 'ȕ', 'ȗ', 'ụ', 'ủ', 'ứ', 'ừ', 'ử', 'ữ', 'ự' -> "u"

    'Ý', 'Ŷ', 'Ỳ', 'Ỵ', 'Ỷ', 'Ỹ' -> "Y"
    'ý', 'ÿ', 'ŷ', 'ỳ', 'ỵ', 'ỷ', 'ỹ' -> "y"

    'Ź', 'Ż', 'Ž' -> "Z"
    'ź', 'ż', 'ž' -> "z"

    'Æ' -> "Ae"; 'æ' -> "ae"; 'Œ' -> "Oe"; 'œ' -> "oe"

    else -> null
}

