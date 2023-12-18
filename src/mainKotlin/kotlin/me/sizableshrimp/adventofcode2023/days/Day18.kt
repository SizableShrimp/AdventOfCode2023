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

import me.sizableshrimp.adventofcode2023.templates.Direction
import me.sizableshrimp.adventofcode2023.templates.LongCoordinate
import me.sizableshrimp.adventofcode2023.templates.SeparatedDay
import kotlin.math.abs

class Day18 : SeparatedDay() {
    override fun part1() = this.findArea(this.lines.map { Direction.getCardinalDirection(it[0]) to it.substring(2, it.indexOf(' ', 2)).toInt() })

    override fun part2() = this.findArea(this.lines.map { l -> l.substringAfter('#').let { Direction.cardinalDirections()[it[5] - '0'] to it.take(5).toInt(16) } })

    private fun findArea(plans: List<Pair<Direction, Int>>): Long {
        var area = 2L
        val vertices = plans.runningFold(LongCoordinate.ORIGIN) { coord, plan ->
            area += plan.second
            coord.resolve(plan.first, plan.second)
        }

        // Gauss' shoelace formula
        area += (vertices + vertices.first()).windowed(2).sumOf { (a, b) ->
            a.x * b.y - b.x * a.y
        }

        return abs(area) / 2
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day18().run()
        }
    }
}