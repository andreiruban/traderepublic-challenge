package io.ruban.util

fun validateISIN(isin: String) {
    if (isin.isBlank()) throw RuntimeException("ISIN $isin is invalid")
}

fun validatePrice(price: Double) {
    if (price < 0) throw RuntimeException("Price $price is invalid")
}

fun validatePeriod(period: Long) {
    if (period < 1) throw RuntimeException("Period $period is invalid")
}