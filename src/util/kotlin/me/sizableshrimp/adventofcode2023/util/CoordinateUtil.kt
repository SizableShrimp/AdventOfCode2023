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

import me.sizableshrimp.adventofcode2023.templates.Coordinate
import me.sizableshrimp.adventofcode2023.templates.Direction
import kotlin.math.max
import kotlin.math.min

operator fun Coordinate.plus(other: Coordinate): Coordinate = this.resolve(other)

operator fun Coordinate.plus(dir: Direction): Coordinate = this.resolve(dir)

operator fun Coordinate.plus(num: Int): Coordinate = this.resolve(num, num)

operator fun Coordinate.minus(other: Coordinate) = Coordinate(this.x - other.x, this.y - other.y)

operator fun Coordinate.minus(dir: Direction) = Coordinate(this.x - dir.x, this.y - dir.y)

operator fun Coordinate.minus(num: Int): Coordinate = this.resolve(-num, -num)

operator fun Coordinate.times(other: Coordinate): Coordinate = this.multiply(other)

operator fun Coordinate.times(num: Int): Coordinate = this.multiply(num)

operator fun Coordinate.div(other: Coordinate) = Coordinate(this.x / other.x, this.y / other.y)

operator fun Coordinate.div(num: Int): Coordinate = Coordinate(this.x / num, this.y / num)

operator fun Coordinate.rem(other: Coordinate) = Coordinate(this.x % other.x, this.y % other.y)

// Hard to use since IntelliJ doesn't seem able to auto-import these or suggest these as a possible import.
// operator fun Array<IntArray>.get(coord: Coordinate) = this[coord.y][coord.x]
//
// operator fun Array<LongArray>.get(coord: Coordinate) = this[coord.y][coord.x]
//
// operator fun Array<CharArray>.get(coord: Coordinate) = this[coord.y][coord.x]
//
// operator fun Array<ByteArray>.get(coord: Coordinate) = this[coord.y][coord.x]
//
// operator fun Array<DoubleArray>.get(coord: Coordinate) = this[coord.y][coord.x]
//
// operator fun Array<FloatArray>.get(coord: Coordinate) = this[coord.y][coord.x]
//
// operator fun Array<BooleanArray>.get(coord: Coordinate) = this[coord.y][coord.x]
//
// operator fun <T> Array<Array<T>>.get(coord: Coordinate) = this[coord.y][coord.x]

/**
 * Calls the consumer for all (x, y) coordinates between this coordinate and the other coordinate, inclusive.
 */
inline fun Coordinate.betweenCoordsInclusive(other: Coordinate, consumer: (Int, Int) -> Unit) {
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