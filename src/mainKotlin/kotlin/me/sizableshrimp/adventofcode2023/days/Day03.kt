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
import java.lang.StringBuilder

class Day03 : Day() {
    override fun evaluate(): Result {
        val grid = GridHelper.convertChar(this.lines) { it }
        val ids = mutableMapOf<Coordinate, Int>()
        val gears = mutableMapOf<Coordinate, MutableSet<Pair<Coordinate, Int>>>()

        for ((y, row) in grid.withIndex()) {
            inner@ for ((x, c) in row.withIndex()) {
                val coord = Coordinate.of(x, y)
                if (c.isDigit().not())
                    continue

                for (dir in Direction.cardinalOrdinalDirections()) {
                    val offset = coord.resolve(dir)
                    if (!GridHelper.isValid(grid, offset))
                        continue

                    val otherC = grid[offset.y][offset.x]
                    if (otherC != '.' && otherC.isDigit().not()) {
                        var tempCoord = coord
                        while (GridHelper.isValid(grid, tempCoord) && grid[tempCoord.y][tempCoord.x].isDigit()) {
                            tempCoord = tempCoord.resolve(Direction.WEST)
                        }
                        val builder = StringBuilder()
                        tempCoord = tempCoord.resolve(Direction.EAST)
                        val pos = tempCoord
                        while (GridHelper.isValid(grid, tempCoord) && grid[tempCoord.y][tempCoord.x].isDigit()) {
                            builder.append(grid[tempCoord.y][tempCoord.x])
                            tempCoord = tempCoord.resolve(Direction.EAST)
                        }
                        val id = builder.toString().toInt()
                        ids[tempCoord] = id

                        if (otherC == '*') {
                            val gearSet = gears.getOrPut(offset) {
                                mutableSetOf()
                            }
                            if (!gearSet.any { it.first == pos })
                                gearSet.add(pos to id)
                        }

                        continue@inner
                    }
                }
            }
        }

        return Result.of(ids.values.sum(), gears.filter { it.value.size == 2 }.map { it.value.first().second * it.value.last().second }.sum())
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day03().run()
        }
    }
}