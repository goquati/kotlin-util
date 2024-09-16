package io.github.goquati.kotlin.util.csv

public data class CsvColumn<Row>(
    val name: String,
    val forceEscape: Boolean,
    val row2Cell: Row.() -> String,
)
