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
import me.sizableshrimp.adventofcode2023.templates.LongCoordinate

class Day11 : Day() {
    override fun evaluate(): Result {
        val coords = GridHelper.convertToSet(this.lines) { it == '#' }
        val width = this.lines[0].length
        val height = this.lines.size
        val expandYs = mutableSetOf<Int>()
        val expandXs = mutableSetOf<Int>()

        for (y in 0..<height) {
            val yCoords = coords.filter { it.y == y }
            if (yCoords.isEmpty())
                expandYs.add(y)
        }

        for (x in 0..<width) {
            val xCoords = coords.filter { it.x == x }
            if (xCoords.isEmpty())
                expandXs.add(x)
        }

        val expandedCoords = coords.map {
            val xDiff = expandXs.count { x -> it.x >= x }.toLong()
            val yDiff = expandYs.count { y -> it.y >= y }.toLong()
            val newCoord = LongCoordinate.of(it)
            newCoord.resolve(xDiff, yDiff) to newCoord.resolve(xDiff * 999999, yDiff * 999999)
        }

        val combos = Itertools.combinations(expandedCoords, 2)

        val minLengths = combos.map { (a, b) ->
            a.first.distance(b.first) to a.second.distance(b.second)
        }

        return Result.of(minLengths.sumOf { it.first }, minLengths.sumOf { it.second })
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day11().run()
        }
    }
}