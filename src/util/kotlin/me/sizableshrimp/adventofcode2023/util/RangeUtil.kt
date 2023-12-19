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

fun IntRange.intersectWith(other: IntRange): IntRange? {
    val start = maxOf(this.first, other.first)
    val end = minOf(this.last, other.last)
    return if (start <= end) start..end else null
}

fun LongRange.intersectWith(other: LongRange): LongRange? {
    val start = maxOf(this.first, other.first)
    val end = minOf(this.last, other.last)
    return if (start <= end) start..end else null
}

fun CharRange.intersectWith(other: CharRange): CharRange? {
    val start = maxOf(this.first, other.first)
    val end = minOf(this.last, other.last)
    return if (start <= end) start..end else null
}

fun IntRange.getDisjointRanges(other: IntRange): List<IntRange> {
    val intersection = this.intersectWith(other) ?: return listOf(this, other)
    val start = minOf(this.first, other.first)
    val end = maxOf(this.last, other.last)
    return listOf(start..<intersection.first, (intersection.last + 1)..end).filter { it.first <= it.last }
}

fun LongRange.getDisjointRanges(other: LongRange): List<LongRange> {
    val intersection = this.intersectWith(other) ?: return listOf(this, other)
    val start = minOf(this.first, other.first)
    val end = maxOf(this.last, other.last)
    return listOf(start..<intersection.first, (intersection.last + 1)..end).filter { it.first <= it.last }
}

fun CharRange.getDisjointRanges(other: CharRange): List<CharRange> {
    val intersection = this.intersectWith(other) ?: return listOf(this, other)
    val start = minOf(this.first, other.first)
    val end = maxOf(this.last, other.last)
    return listOf(start..<intersection.first, (intersection.last + 1)..end).filter { it.first <= it.last }
}