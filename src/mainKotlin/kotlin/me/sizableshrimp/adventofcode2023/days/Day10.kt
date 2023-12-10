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

class Day10 : Day() {
    override fun evaluate(): Result {
        val grid = GridHelper.convertChar(this.lines) { it }
        val start = GridHelper.findCoordinate(this.lines, 'S')
        val pipes = mutableSetOf(start)
        var tempCoord = start
        var tempDir = Direction.cardinalDirections().first { dir ->
            val next = start.resolve(dir)
            GridHelper.isValid(grid, next) && CONNECTIONS[grid[next.y][next.x]]?.contains(dir.opposite()) == true
        }

        val validSDirections = mutableSetOf(tempDir)

        do {
            pipes.add(tempCoord)
            val tempDirOpposite = tempDir.opposite()
            tempCoord = tempCoord.resolve(tempDir)
            if (tempCoord == start)
                break // We wrapped all the way back around the pipe loop
            tempDir = CONNECTIONS[grid[tempCoord.y][tempCoord.x]]!!.first { it != tempDirOpposite }
        } while (true)

        validSDirections.add(tempDir.opposite())

        // Update the grid for part 2 to simplify the logic.
        // We replace 'S' with what pipe shape it actually is.
        // We also replace any unused pipe that isn't part of the main loop with a '.'.
        grid[start.y][start.x] = CONNECTIONS.entries.first { (_, dirs) -> dirs == validSDirections }.key

        for ((y, row) in grid.withIndex()) {
            for (x in row.indices) {
                if (!pipes.contains(Coordinate(x, y)))
                    grid[y][x] = '.'
            }
        }

        var insideLoopCount = 0

        // https://en.wikipedia.org/wiki/Point_in_polygon#Ray_casting_algorithm
        for (y in 1..<grid.size-1) {
            val row = grid[y]
            var inside = false
            for (c in row) {
                if (c == '|' || c == 'J' || c == 'L') {
                    inside = !inside
                } else if (c == '.' && inside) {
                    insideLoopCount++
                }
            }
        }

        // Part 1: The pipe loop is continuous and therefore must be even in size. The furthest distance on the loop is half the
        //         size of the loop because the furthest point has an even number of pipes on either side back to the start.
        return Result.of(pipes.size / 2, insideLoopCount)
    }

    companion object {
        private val CONNECTIONS = mapOf(
            '-' to setOf(Direction.EAST, Direction.WEST),
            '|' to setOf(Direction.NORTH, Direction.SOUTH),
            'L' to setOf(Direction.NORTH, Direction.EAST),
            'J' to setOf(Direction.NORTH, Direction.WEST),
            '7' to setOf(Direction.SOUTH, Direction.WEST),
            'F' to setOf(Direction.SOUTH, Direction.EAST)
        )

        @JvmStatic
        fun main(args: Array<String>) {
            Day10().run()
        }
    }
}
