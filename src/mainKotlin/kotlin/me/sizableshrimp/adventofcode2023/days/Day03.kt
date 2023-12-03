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
import me.sizableshrimp.adventofcode2023.templates.Coordinate
import me.sizableshrimp.adventofcode2023.templates.Day
import me.sizableshrimp.adventofcode2023.templates.Direction

class Day03 : Day() {
    override fun evaluate(): Result {
        // Oneliner!
        // java.io.File("aoc_input/day03.txt").readLines().let { lines ->
        //     lines.mapIndexed { y, row ->
        //         row.withIndex().filter { it.value != '.' && !it.value.isDigit() }.map { (x, c) ->
        //             c to arrayOf(-1 to -1, 0 to -1, 1 to -1, -1 to 0, 1 to 0, -1 to 1, 0 to 1, 1 to 1)
        //                 .map { dir -> (x + dir.first) to (y + dir.second) }
        //                 .filterNot { it.first < 0 || it.first >= row.lastIndex || it.second < 0 || it.second >= lines.size }
        //                 .filter { lines[it.second][it.first].isDigit() }
        //                 .map { p ->
        //                     generateSequence(p) { (it.first - 1) to it.second }
        //                         .takeWhile { it.first >= 0 && lines[it.second][it.first].isDigit() }
        //                         .last()
        //                 }.distinct().map { p ->
        //                     p to (generateSequence(p) { (it.first + 1) to it.second }
        //                         .takeWhile { it.first <= row.lastIndex && this.lines[it.second][it.first].isDigit() }
        //                         .map { lines[it.second][it.first] }
        //                         .joinToString("").toInt())
        //                 }
        //         }
        //     }
        // }.let { symData ->
        //     (symData.flatMap { row -> row.flatMap { it.second } }.distinctBy { it.first }.sumOf { it.second }) to
        //             (symData.sumOf { row -> row.filter { it.first == '*' && it.second.size == 2}.sumOf { p -> p.second.map { it.second }.reduce(Int::times) } })
        // }.also { (p1, p2) -> println("p1: $p1, p2: $p2") }

        val grid = GridHelper.convertChar(this.lines) { it }
        val ids = mutableMapOf<Coordinate, Int>()
        val gearRatios = mutableListOf<Int>()

        for ((y, row) in grid.withIndex()) {
            for ((x, c) in row.withIndex()) {
                val coord = Coordinate.of(x, y)
                if (c == '.' || c.isDigit())
                    continue

                val gearData = if (c == '*') mutableMapOf<Coordinate, Int>() else null

                for (dir in Direction.cardinalOrdinalDirections()) {
                    val offset = coord.resolve(dir)
                    if (!GridHelper.isValid(grid, offset) || !grid[offset.y][offset.x].isDigit())
                        continue

                    var tempCoord = offset
                    do {
                        tempCoord = tempCoord.resolve(Direction.WEST)
                    } while (GridHelper.isValid(grid, tempCoord) && grid[tempCoord.y][tempCoord.x].isDigit())

                    var id = 0
                    tempCoord = tempCoord.resolve(Direction.EAST)
                    val partNumPos = tempCoord
                    do {
                        id = id * 10 + (grid[tempCoord.y][tempCoord.x] - '0')
                        tempCoord = tempCoord.resolve(Direction.EAST)
                    } while (GridHelper.isValid(grid, tempCoord) && grid[tempCoord.y][tempCoord.x].isDigit())

                    ids[partNumPos] = id
                    if (c == '*')
                        gearData!![partNumPos] = id
                }

                if (c == '*' && gearData!!.size == 2) {
                    gearRatios.add(gearData.values.reduce(Int::times))
                }
            }
        }

        return Result.of(ids.values.sum(), gearRatios.sum())
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day03().run()
        }
    }
}