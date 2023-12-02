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
import kotlin.math.max

class Day02 : Day() {
    override fun evaluate(): Result {
        val games = this.lines.map { line ->
            line.indexOf(':').let { colonIdx ->
                line.substring(5, colonIdx).toInt() to line.substring(colonIdx + 2).split("; ").map { round ->
                    sortedMapOf(reverseOrder(), 'r' to 0, 'g' to 0, 'b' to 0).apply {
                        round.split(", ").map { it.split(" ") }.forEach { this[it[1][0]] = it[0].toInt() }
                    }.values.toList()
                }.reduceRight { round, acc -> round.zip(acc).map { max(it.first, it.second) } }
            }
        }

        return Result.of(games.filter { (_, round) ->
            round[0] <= 12 && round[1] <= 13 && round[2] <= 14
        }.sumOf { it.first }, games.sumOf { (_, round) ->
            round.reduceRight(Int::times)
        })
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day02().run()
        }
    }
}