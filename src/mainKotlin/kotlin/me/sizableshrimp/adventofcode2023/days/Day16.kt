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
import me.sizableshrimp.adventofcode2023.util.toCharGrid
import kotlin.math.max

class Day16 : Day() {
    override fun evaluate(): Result {
        val grid = this.lines.toCharGrid()
        val width = grid[0].size
        val height = grid.size
        var p1 = 0
        var max = 0

        for (x in grid[0].indices) {
            val up = Coordinate(x, height) to Direction.NORTH
            max = max(calculateEnergy(grid, up), max)
            val down = Coordinate(x, -1) to Direction.SOUTH
            max = max(calculateEnergy(grid, down), max)
        }
        for (y in grid.indices) {
            val right = Coordinate(-1, y) to Direction.EAST
            val rightEnergy = calculateEnergy(grid, right)
            if (p1 == 0)
                p1 = rightEnergy
            max = max(rightEnergy, max)
            val left = Coordinate(width, y) to Direction.WEST
            max = max(calculateEnergy(grid, left), max)
        }

        return Result.of(p1, max)
    }

    private fun calculateEnergy(grid: Array<CharArray>, start: Pair<Coordinate, Direction>): Int {
        val seen = mutableSetOf<Pair<Coordinate, Direction>>()
        val search = ArrayDeque<Pair<Coordinate, Direction>>()
        search.add(start)

        while (search.isNotEmpty()) {
            val pair = search.removeFirst()
            val (coord, dir) = pair
            if (!seen.add(pair))
                continue

            val next = coord.resolve(dir)

            if (!GridHelper.isValid(grid, next))
                continue

            when (grid[next.y][next.x]) {
                '.' -> search.add(next to dir)
                '/' -> search.add(next to FORWARD_MIRROR[dir]!!)
                '\\' -> search.add(next to BACKWARD_MIRROR[dir]!!)
                '|' -> when (dir) {
                    Direction.NORTH, Direction.SOUTH -> {
                        search.add(next to dir)
                    }

                    Direction.WEST, Direction.EAST -> {
                        search.add(next to Direction.NORTH)
                        search.add(next to Direction.SOUTH)
                    }

                    else -> {}
                }

                '-' -> when (dir) {
                    Direction.WEST, Direction.EAST -> {
                        search.add(next to dir)
                    }

                    Direction.NORTH, Direction.SOUTH -> {
                        search.add(next to Direction.WEST)
                        search.add(next to Direction.EAST)
                    }

                    else -> {}
                }
            }
        }

        // Account for the starting point which is outside the grid
        return seen.map { it.first }.distinct().size - 1
    }

    companion object {
        private val FORWARD_MIRROR = mapOf(
            Direction.NORTH to Direction.EAST,
            Direction.EAST to Direction.NORTH,
            Direction.SOUTH to Direction.WEST,
            Direction.WEST to Direction.SOUTH
        )
        private val BACKWARD_MIRROR = mapOf(
            Direction.NORTH to Direction.WEST,
            Direction.WEST to Direction.NORTH,
            Direction.SOUTH to Direction.EAST,
            Direction.EAST to Direction.SOUTH
        )

        @JvmStatic
        fun main(args: Array<String>) {
            Day16().run()
        }
    }
}