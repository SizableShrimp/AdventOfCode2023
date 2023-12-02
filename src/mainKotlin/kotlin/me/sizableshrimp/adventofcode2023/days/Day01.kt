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
            sum1 += (line.first(Char::isDigit).code - '0'.code) * 10 + line.last(Char::isDigit).code - '0'.code
            val match2 = P2_REGEX.matchEntire(line)!!
            val first2 = match2.groups[1]!!.value
            val last2 = match2.groups[2]?.value ?: first2
            sum2 += ((if (first2.length == 1) first2[0].code - '0'.code else DIGITS.indexOf(first2) + 1) * 10
                    + if (last2.length == 1) last2[0].code - '0'.code else DIGITS.indexOf(last2) + 1)
        }

        return Result(sum1, sum2)
    }

    companion object {
        private val DIGITS = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        private const val DIGIT_MATCH_P2 = "(\\d|one|two|three|four|five|six|seven|eight|nine)"
        private val P2_REGEX = Regex(".*?$DIGIT_MATCH_P2.*(?<=$DIGIT_MATCH_P2).*?")

        @JvmStatic
        fun main(args: Array<String>) {
            Day01().run()
        }
    }
}