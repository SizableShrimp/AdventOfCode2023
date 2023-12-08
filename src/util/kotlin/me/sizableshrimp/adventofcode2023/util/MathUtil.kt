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

import me.sizableshrimp.adventofcode2023.helper.MathUtil

infix fun Int.lcm(other: Int) = MathUtil.lcm(this, other)

fun IntArray.lcm() = MathUtil.lcm(*this)

fun Collection<Int>.lcm() = this.toIntArray().lcm()

infix fun Long.lcm(other: Long) = MathUtil.lcm(this, other)

fun LongArray.lcm() = MathUtil.lcm(*this)

fun Collection<Long>.lcm() = this.toLongArray().lcm()

infix fun Int.gcd(other: Int) = MathUtil.gcd(this, other)

fun IntArray.gcd() = MathUtil.gcd(*this)

fun Collection<Int>.gcd() = this.toIntArray().gcd()

infix fun Long.gcd(other: Long) = MathUtil.gcd(this, other)

fun LongArray.gcd() = MathUtil.gcd(*this)

fun Collection<Long>.gcd() = this.toLongArray().gcd()