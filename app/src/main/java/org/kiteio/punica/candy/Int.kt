package org.kiteio.punica.candy

/**
 * 是否为奇数
 * @receiver [Int]
 * @return [Boolean]
 */
fun Int.isOdd() = rem(2) != 0


/**
 * 是否为偶数
 * @receiver [Int]
 * @return [Boolean]
 */
fun Int.isEven() = rem(2) == 0