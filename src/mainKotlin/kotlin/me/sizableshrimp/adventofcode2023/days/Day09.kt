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

class Day09 : Day() {
    override fun evaluate(): Result {
        val results = this.lines.map { it.split(" ").map { s -> s.toInt() } }.map { startingNums ->
            var temp = startingNums.toMutableList()
            val lists = mutableListOf(temp)

            while (temp.any { it != 0 }) {
                temp = temp.windowed(2, 1).map { (a, b) ->
                    b - a
                }.toMutableList()
                lists.add(temp)
            }

            lists.withIndex().drop(1).reversed().forEach { (idx, list) ->
                val above = lists[idx - 1]
                above.add(0, above.first() - list.first())
                above.add(above.last() + list.last())
            }

            lists.first().let { l -> l.last() to l.first() }
        }

        return Result.of(results.sumOf { it.first }, results.sumOf { it.second })
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day09().run()
        }
    }
}