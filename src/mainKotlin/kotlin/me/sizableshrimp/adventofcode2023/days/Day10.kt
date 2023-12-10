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
import me.sizableshrimp.adventofcode2023.util.div

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

        // Part 2: We can allow filling between pipes by imagining a grid with 2x the size.
        // Consider the following part of a larger grid (the loop is assumed to be continuous):
        // OL7
        // 7.|
        // L-J
        // O represents a spot we know to be outside the loop.
        //
        // We know that the center empty space should be considered outside the loop because it can escape
        // between the 7 and L. Let's represent this grid with 2x the size:
        // OOL-7.
        // OO..|.
        // 7...|.
        // |...|.
        // L---J.
        // ......
        //
        // We can think of this as simply "zooming in" to the grid to have a higher resolution.
        // It now becomes obvious that the inner spot should in fact be considered as outside the loop.
        // We can do a flood fill to find all outside spots on this 2x grid.
        // Any spot on the 2x grid can be turned back into a coordinate on the original grid by dividing
        // the x and y by 2 with integer division.
        val doubleGrid = Array(grid.size * 2) { CharArray(grid[it / 2].size * 2) { '.' } }

        for ((y, row) in grid.withIndex()) {
            for ((x, c) in row.withIndex()) {
                val newCoord = Coordinate(x * 2, y * 2)
                doubleGrid[newCoord.y][newCoord.x] = c
                val newCoordRight = newCoord.resolve(Direction.EAST)
                doubleGrid[newCoordRight.y][newCoordRight.x] = when (c) {
                    'F' -> '-'
                    '7' -> '.'
                    'J' -> '.'
                    'L' -> '-'
                    '-' -> '-'
                    '|' -> '.'
                    '.' -> '.'
                    else -> error("Invalid char: $c")
                }
                val newCoordDown = newCoord.resolve(Direction.SOUTH)
                doubleGrid[newCoordDown.y][newCoordDown.x] = when (c) {
                    'F' -> '|'
                    '7' -> '|'
                    'J' -> '.'
                    'L' -> '.'
                    '-' -> '.'
                    '|' -> '|'
                    '.' -> '.'
                    else -> error("Invalid char: $c")
                }
                // We implicitly leave the bottom-right corner of every 2x2 square as a '.'
                // in the "zoomed-in" grid.
            }
        }

        val queue = ArrayDeque<Coordinate>()
        val outsideLoop = mutableSetOf<Coordinate>() // Coordinates that are outside the loop

        // Because the problem description enforces a continuous loop of pipes, we know that
        // there must be at least one empty spot on the grid's edge if we want to be able to flood fill
        // between the pipes. If there is no empty spot on the grid's edge, then there is nothing to flood
        // fill, so there are no spots outside the loop.
        for (x in doubleGrid.indices) {
            if (doubleGrid[0][x] == '.') {
                outsideLoop.add(Coordinate(x, 0))
            }
            if (doubleGrid[doubleGrid.size - 1][x] == '.') {
                outsideLoop.add(Coordinate(x, doubleGrid.size - 1))
            }
        }
        for (y in doubleGrid.indices) {
            if (doubleGrid[y][0] == '.') {
                outsideLoop.add(Coordinate(0, y))
            }
            if (doubleGrid[y][doubleGrid[y].size - 1] == '.') {
                outsideLoop.add(Coordinate(doubleGrid[y].size - 1, y))
            }
        }
        queue.addAll(outsideLoop)

        while (!queue.isEmpty()) {
            val coord = queue.removeFirst()

            for (dir in Direction.cardinalDirections()) {
                val next = coord.resolve(dir)

                if (!GridHelper.isValid(doubleGrid, next) || doubleGrid[next.y][next.x] != '.' || !outsideLoop.add(next))
                    continue

                queue.add(next)
            }
        }

        // Part 1: The pipe loop is continuous and therefore must be even in size. The furthest distance on the loop is half the
        //         size of the loop because the furthest point has an even number of pipes on either side back to the start.
        // Part 2: The number of inside spots is the total number of spots minus (the number of outside & pipe spots).
        return Result.of(pipes.size / 2, this.lines.sumOf { it.length } - (pipes + outsideLoop.map { it / 2 }).size)
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
