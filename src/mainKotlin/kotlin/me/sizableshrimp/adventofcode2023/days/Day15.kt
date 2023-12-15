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

class Day15 : Day() {
    override fun evaluate(): Result {
        val instructions = this.lines[0].split(',')
        val labels = instructions.map { l ->
            l.substringBefore('-').substringBefore('=')
        }
        val (p1, hashes) = listOf(instructions, labels).map { it.map { l -> l.fold(0) { acc, c -> (acc + c.code) * 17 % 256 } } }

        val boxes = Array(256) { mutableListOf<Pair<String, Int>>() }

        hashes.forEachIndexed { i, hash ->
            val line = instructions[i]
            val label = labels[i]
            val remove = line.contains('-')
            val lenses = boxes[hash]

            if (remove) {
                lenses.removeIf { it.first == label }
            } else {
                val insertIdx = lenses.indexOfFirst { it.first == label }
                val key = label to line.substringAfter('=').toInt()
                if (insertIdx == -1) {
                    lenses.add(key)
                } else {
                    lenses[insertIdx] = key
                }
            }
        }

        return Result.of(p1.sum(), boxes.withIndex().sumOf { (i, lenses) ->
            lenses.withIndex().sumOf { (j, l) ->
                (i + 1) * (j + 1) * l.second
            }
        })
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day15().run()
        }
    }
}