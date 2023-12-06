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

class Day06 : Day() {
    override fun evaluate(): Result {
        val (p1, p2) = this.lines.map { it.substringAfter(':').trim() }.map { line ->
            line.split(' ').filterNot { it.isEmpty() }.map { it.toLong() } to
                    listOf(line.replace(" ", "").toLong())
        }.unzip().toList().map { (times, distances) ->
            times.zip(distances).map { (time, distance) ->
                (1..<time).sumOf { speed -> if ((time - speed) * speed > distance) 1L else 0L }
            }.reduce(Long::times)
        }

        return Result.of(p1, p2)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day06().run()
        }
    }
}