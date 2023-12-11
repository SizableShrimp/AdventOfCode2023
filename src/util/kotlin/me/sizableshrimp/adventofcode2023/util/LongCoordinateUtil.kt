/*
 * AdventOfCode2023
 * Copyright (C) 2023 SizableShrimp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.sizableshrimp.adventofcode2023.util

import me.sizableshrimp.adventofcode2023.templates.LongCoordinate
import me.sizableshrimp.adventofcode2023.templates.Direction
import kotlin.math.max
import kotlin.math.min

operator fun LongCoordinate.plus(other: LongCoordinate): LongCoordinate = this.resolve(other)

operator fun LongCoordinate.plus(dir: Direction): LongCoordinate = this.resolve(dir)

operator fun LongCoordinate.plus(num: Long): LongCoordinate = this.resolve(num, num)

operator fun LongCoordinate.plus(num: Int): LongCoordinate = this + num.toLong()

operator fun LongCoordinate.minus(other: LongCoordinate) = LongCoordinate(this.x - other.x, this.y - other.y)

operator fun LongCoordinate.minus(dir: Direction) = LongCoordinate(this.x - dir.x, this.y - dir.y)

operator fun LongCoordinate.minus(num: Long): LongCoordinate = this.resolve(-num, -num)

operator fun LongCoordinate.minus(num: Int): LongCoordinate = this - num.toLong()

operator fun LongCoordinate.times(other: LongCoordinate): LongCoordinate = this.multiply(other)

operator fun LongCoordinate.times(num: Long): LongCoordinate = this.multiply(num)

operator fun LongCoordinate.times(num: Int): LongCoordinate = this * num.toLong()

operator fun LongCoordinate.div(other: LongCoordinate) = LongCoordinate(this.x / other.x, this.y / other.y)

operator fun LongCoordinate.div(num: Int): LongCoordinate = LongCoordinate(this.x / num, this.y / num)

operator fun LongCoordinate.rem(other: LongCoordinate) = LongCoordinate(this.x % other.x, this.y % other.y)

/**
 * Calls the consumer for all (x, y) LongCoordinates between this LongCoordinate and the other LongCoordinate, inclusive.
 */
inline fun LongCoordinate.betweenCoordsInclusive(other: LongCoordinate, consumer: (Long, Long) -> Unit) {
    val minY = min(this.y, other.y)
    val maxY = max(this.y, other.y)
    val minX = min(this.x, other.x)
    val maxX = max(this.x, other.x)

    for (y in minY..maxY) {
        for (x in minX..maxX) {
            consumer(x, y)
        }
    }
}