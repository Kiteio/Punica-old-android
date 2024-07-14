package org.kiteio.punica.candy

/**
 * 若为奇数，返回 true
 * @receiver [Int]
 * @return [Boolean]
 */
fun Int.isOdd() = rem(2) != 0


/**
 * 若为偶数，返回 true
 * @receiver [Int]
 * @return [Boolean]
 */
fun Int.isEven() = rem(2) == 0