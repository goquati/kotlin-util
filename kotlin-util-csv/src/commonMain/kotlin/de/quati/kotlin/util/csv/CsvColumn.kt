package de.quati.kotlin.util.csv

public data class CsvColumn<Row>(
    val name: String,
    val forceEscape: Boolean,
    val row2Cell: Row.() -> String,
)
