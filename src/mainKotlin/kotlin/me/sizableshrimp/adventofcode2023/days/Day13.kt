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
import me.sizableshrimp.adventofcode2023.util.splitOnBlankLines

class Day13 : Day() {
    override fun evaluate(): Result {
        val inputs = this.lines.splitOnBlankLines()

        val (p1, p2) = listOf(false, true).map { allowSmudge ->
            inputs.sumOf { rows ->
                rows.indices.firstOrNull {
                    isValid(rows, it, allowSmudge)
                }?.let { return@sumOf 100 * (it + 1) }

                val columns = (0..<rows[0].length).map { j -> rows.map { row -> row[j] }.joinToString("") }
                columns.indices.first {
                    isValid(columns, it, allowSmudge)
                } + 1
            }
        }

        return Result.of(p1, p2)
    }

    private fun isValid(layers: List<String>, idx: Int, allowSmudge: Boolean = false): Boolean {
        if (idx >= layers.size - 1)
            return false

        var usedSmudged = !allowSmudge
        var low = idx
        var high = idx + 1

        while (low >= 0 && high < layers.size) {
            val lowLayer = layers[low]
            val highLayer = layers[high]
            if (lowLayer != highLayer) {
                if (usedSmudged || lowLayer.zip(highLayer).count { (a, b) -> a != b } > 1)
                    return false

                usedSmudged = true
            }

            low--
            high++
        }

        // There must be at least 1 smudge in Part 2
        return !allowSmudge || usedSmudged
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day13().run()
        }
    }
}