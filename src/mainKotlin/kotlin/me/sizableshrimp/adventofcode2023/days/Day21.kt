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
import me.sizableshrimp.adventofcode2023.util.getCardinalNeighbors
import java.math.BigInteger

class Day21 : Day() {
    override fun evaluate(): Result {
        val grid = GridHelper.convertChar(this.lines) { c -> if (c == '#') '#' else '.' }
        val width = grid[0].size
        val height = grid.size
        val startPos = GridHelper.findCoordinate(this.lines, 'S')
        val distancesFromStart = getDistances(startPos, grid)
        val maxSteps = if (width == 131) 26501365 else 50
        val modRemainingSteps = maxSteps % width
        val oddNess = maxSteps % 2
        if (oddNess != 1)
            error("Oddness not odd")
        val oddCount = distancesFromStart.values.count { it % 2 == oddNess }.toBigInteger()
        val evenCount = distancesFromStart.values.count { it % 2 != oddNess }.toBigInteger()

        val corners = listOf(Coordinate(width - 1, height - 1), Coordinate(0, height - 1), Coordinate(0, 0), Coordinate(width - 1, 0))
        // val cornerDists = corners.map { distancesFromStart[it]!! }
        val edgeCenters = listOf(Coordinate(width / 2, 0), Coordinate(width / 2, height - 1), Coordinate(0, height / 2), Coordinate(width - 1, height / 2))
        val edgeCenterDists = edgeCenters.map { distancesFromStart[it]!! }
        if (width != height)
            error("Width and height not equal")
        if (edgeCenterDists.toSet().size > 1)
            error("Edge center dists not equal")

        val loopSize = width
        val plotReach = (maxSteps - edgeCenterDists[0]) / loopSize
        if (edgeCenterDists[0] % 2 == 0)
            error("Edge center dist not odd")
        if (plotReach % 2 != 0)
            error("Plot reach not even")
        val targetEdgeOddNess = 0
        val targetCornerOddNess = 0
        val targetSideLengthOddNess = 0

        var result = BigInteger.ZERO

        for (i in 0..<plotReach) {
            // Rings of size 1, 4, 8, 12, 16, etc. alternating between odd and even w/ odd to start
            result += (if (i == 0) 1 else (i * 4)).toBigInteger() * if (i % 2 == 1) evenCount else oddCount
        }

        // Topmost Square
        var distances = getDistances(setOf(Direction.NORTH), distancesFromStart, grid)
        // printDistances(grid, distances, width - 1, targetEdgeOddNess)
        result += distances.values.count { it < width && it % 2 == targetEdgeOddNess }.toBigInteger()

        // Leftmost Square
        distances = getDistances(setOf(Direction.WEST), distancesFromStart, grid)
        // printDistances(grid, distances, width - 1, targetEdgeOddNess)
        result += distances.values.count { it < width && it % 2 == targetEdgeOddNess }.toBigInteger()

        // Rightmost Square
        distances = getDistances(setOf(Direction.EAST), distancesFromStart, grid)
        // printDistances(grid, distances, width - 1, targetEdgeOddNess)
        result += distances.values.count { it < width && it % 2 == targetEdgeOddNess }.toBigInteger()

        // Bottommost Square
        distances = getDistances(setOf(Direction.SOUTH), distancesFromStart, grid)
        // printDistances(grid, distances, width - 1, targetEdgeOddNess)
        result += distances.values.count { it < width && it % 2 == targetEdgeOddNess }.toBigInteger()

        val sideLength = plotReach - 1
        val extraCorners = plotReach
        val remainingCornerSteps = modRemainingSteps - 1

        for ((i, dir) in Direction.cardinalDirections().withIndex()) {
            val otherDir = dir.counterClockwise()
            val dirSet = setOf(dir, otherDir)

            // Side-Length, can reach everything except the furthest corner of this square
            distances = getDistances(dirSet, distancesFromStart, grid)
            // printDistances(grid, distances, width - 1, targetSideLengthOddNess)
            result += sideLength.toBigInteger() * distances.values.count { it < width && it % 2 == targetSideLengthOddNess }.toBigInteger()

            // Corner, can only reach the closest corner of this square
            val corner = corners[i]
            distances = getDistances(corner, grid)
            // printDistances(grid, distances, remainingCornerSteps, targetCornerOddNess)
            result += extraCorners.toBigInteger() * distances.values.count { it <= remainingCornerSteps && it % 2 == targetCornerOddNess }.toBigInteger()
        }

        val p1 = distancesFromStart.values.count { it <= 64 && it % 2 == 0 }

        return Result.of(p1, result)
    }

    private fun getDistances(startPos: Coordinate, grid: Array<CharArray>): MutableMap<Coordinate, Int> {
        val distances = mutableMapOf<Coordinate, Int>()
        distances[startPos] = 0
        search(distances, grid)

        return distances
    }

    private fun getDistances(startEdges: Set<Direction>, distancesFromStart: Map<Coordinate, Int>, grid: Array<CharArray>): MutableMap<Coordinate, Int> {
        val distances = mutableMapOf<Coordinate, Int>()
        startEdges.forEach { dir ->
            when (dir.opposite()) {
                Direction.NORTH -> {
                    grid[0].indices.minByOrNull { x ->
                        val coord = Coordinate(x, 0)
                        distancesFromStart[coord]!!
                    }!!.let { distances[Coordinate(it, 0)] = 0 }
                }

                Direction.SOUTH -> {
                    grid[0].indices.minByOrNull { x ->
                        val coord = Coordinate(x, grid.size - 1)
                        distancesFromStart[coord]!!
                    }!!.let { distances[Coordinate(it, grid.size - 1)] = 0 }
                }

                Direction.EAST -> {
                    grid.indices.minByOrNull { y ->
                        val coord = Coordinate(grid[0].size - 1, y)
                        distancesFromStart[coord]!!
                    }!!.let { distances[Coordinate(grid[0].size - 1, it)] = 0 }
                }

                Direction.WEST -> {
                    grid.indices.minByOrNull { y ->
                        val coord = Coordinate(0, y)
                        distancesFromStart[coord]!!
                    }!!.let { distances[Coordinate(0, it)] = 0 }
                }

                else -> error("")
            }
        }

        search(distances, grid)

        return distances
    }

    private fun search(distances: MutableMap<Coordinate, Int>, grid: Array<CharArray>) {
        val queue = ArrayDeque<Pair<Coordinate, Int>>()
        distances.forEach { queue.add(it.key to it.value) }

        while (queue.isNotEmpty()) {
            val (coord, dist) = queue.removeFirst()
            val nextDist = dist + 1

            for ((_, next) in grid.getCardinalNeighbors(coord)) {
                if (grid[next.y][next.x] == '.' && (next !in distances || distances[next]!! > nextDist)) {
                    distances[next] = nextDist
                    queue.add(next to nextDist)
                }
            }
        }
    }

    private fun printDistances(grid: Array<CharArray>, distances: Map<Coordinate, Int>, maxDist: Int, oddNess: Int) {
        for ((y, row) in grid.withIndex()) {
            for ((x, c) in row.withIndex()) {
                if (c == '#') {
                    print('#')
                } else {
                    val dist = distances[Coordinate(x, y)]
                    if (dist == null) {
                        print(' ')
                    } else {
                        print(if (dist <= maxDist && dist % 2 == oddNess) 'X' else ' ')
                    }
                }
            }
            println()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day21().run()
        }
    }
}