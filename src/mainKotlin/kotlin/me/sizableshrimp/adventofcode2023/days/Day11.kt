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

import me.sizableshrimp.adventofcode2023.helper.GridHelper
import me.sizableshrimp.adventofcode2023.helper.Itertools
import me.sizableshrimp.adventofcode2023.templates.Day

class Day11 : Day() {
    override fun evaluate(): Result {
        val (p1, p2) = GridHelper.convertToSet(this.lines) { it == '#' }.let { coords ->
            listOf(
                (0..<this.lines[0].length).map { y -> coords.filter { it.y == y } },
                (0..<this.lines.size).map { x -> coords.filter { it.x == x } }
            ).fold(0L to 0L) { tot, l ->
                l.fold(listOf(0L, 0L, 0L)) { acc, c ->
                    if (c.isEmpty()) {
                        listOf(acc[0] + acc[2] * (coords.size - acc[2]), acc[1] + 999_999L * (acc[2] * (coords.size - acc[2])), acc[2])
                    } else {
                        listOf(acc[0], acc[1], acc[2] + c.size)
                    }
                }.let { (a, b) -> (tot.first + a) to (tot.second + b) }
            }.let { (a, b) ->
                Itertools.combinations(coords, 2).sumOf { (a, b) ->
                    a.distance(b)
                }.toLong().let { n -> (a + n) to (b + n) }
            }
        }

        return Result.of(p1, p2)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day11().run()
        }
    }
}