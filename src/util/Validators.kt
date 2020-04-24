package io.ruban.util

// TODO: throw custom exception
fun validateISIN(isin: String) {
    if (isin.isBlank()) throw RuntimeException("ISIN $isin is invalid")
}

fun validatePrice(price: Double) {
    if (price < 0) throw RuntimeException("Price $price is invalid")
}