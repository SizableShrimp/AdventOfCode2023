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

package me.sizableshrimp.adventofcode2023.days

import me.sizableshrimp.adventofcode2023.templates.Day

class Day01 : Day() {
    override fun evaluate(): Result {
        var sum1 = 0
        var sum2 = 0

        for (line in this.lines) {
            sum1 += (line.first(Char::isDigit) - '0') * 10 + (line.last(Char::isDigit) - '0')
            sum2 += line.withIndex().map { (idx, c) ->
                c.digitToIntOrNull()
                    ?: line.substring(idx).let { sub -> DIGITS.indexOfFirst { dig -> sub.startsWith(dig) } + 1 }
            }.filter { it != 0 }.let { it.first() * 10 + it.last() }
        }

        return Result(sum1, sum2)
    }

    companion object {
        private val DIGITS = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

        @JvmStatic
        fun main(args: Array<String>) {
            Day01().run()
        }
    }
}