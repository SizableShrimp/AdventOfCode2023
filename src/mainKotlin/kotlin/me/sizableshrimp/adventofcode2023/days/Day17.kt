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

import me.sizableshrimp.adventofcode2023.templates.Coordinate
import me.sizableshrimp.adventofcode2023.templates.Direction
import me.sizableshrimp.adventofcode2023.templates.SeparatedDay
import me.sizableshrimp.adventofcode2023.util.getCardinalNeighbors
import me.sizableshrimp.adventofcode2023.util.toCharGrid
import java.util.PriorityQueue

class Day17 : SeparatedDay() {
    private lateinit var grid: Array<CharArray>

    override fun part1() = simulate(3)

    override fun part2() = simulate(10, 4)

    private fun simulate(maxStraight: Int, minStraight: Int = 1): Int {
        val distances = mutableMapOf<Node, Pair<Int, Node>>()
        val targetCoord = Coordinate(this.grid[0].size - 1, this.grid.size - 1)
        val queue = PriorityQueue<Node> { a, b -> a.heatLoss.compareTo(b.heatLoss) }
        queue.add(Node(0, Coordinate.ORIGIN, null, 0, null))
        var minHeatLoss = Int.MAX_VALUE
        var minNode: Node? = null

        while (queue.isNotEmpty()) {
            val node = queue.remove()

            if (node.heatLoss > minHeatLoss)
                continue

            for ((dir, next) in this.grid.getCardinalNeighbors(node.coord)) {
                if (node.oldDir == dir.opposite())
                    continue // Cannot reverse directions

                val isSameDirection = dir == node.oldDir
                if (node.oldNode != null && node.timesSameDirection < minStraight && !isSameDirection)
                    continue

                val newTimesSameDirection = if (isSameDirection) node.timesSameDirection + 1 else 1
                if (newTimesSameDirection > maxStraight)
                    continue

                val nextHeatLoss = node.heatLoss + (this.grid[next.y][next.x] - '0')
                if (nextHeatLoss > minHeatLoss)
                    continue

                val nextNode = Node(nextHeatLoss, next, dir, newTimesSameDirection, node)
                if (!distances.contains(nextNode) || distances[nextNode]!!.first > nextHeatLoss) {
                    if (newTimesSameDirection >= minStraight && next == targetCoord && nextHeatLoss < minHeatLoss) {
                        minHeatLoss = nextHeatLoss
                        minNode = nextNode
                    }

                    distances[nextNode] = nextHeatLoss to nextNode
                    queue.add(nextNode)
                }
            }
        }

        // visualizePath(minNode!!)

        return minHeatLoss
    }

    override fun parse() {
        this.grid = this.lines.toCharGrid()
    }

    private fun visualizePath(minNode: Node) {
        val path = mutableListOf<Node>()
        var node = minNode

        while (node.oldNode != null) {
            path.add(0, node)
            node = node.oldNode!!
        }

        val pathCoords = path.map { it.coord }

        for ((y, row) in this.grid.withIndex()) {
            for ((x, c) in row.withIndex()) {
                val coord = Coordinate(x, y)
                val pathIdx = pathCoords.indexOf(coord)
                if (pathIdx != -1) {
                    print(path[pathIdx].oldDir!!.charArrow)
                } else {
                    // print(c)
                    print('.')
                }
            }
            println()
        }
    }

    private data class Node(
        val heatLoss: Int, val coord: Coordinate, val oldDir: Direction?, val timesSameDirection: Int, val oldNode: Node?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Node

            if (coord != other.coord) return false
            if (oldDir != other.oldDir) return false
            if (timesSameDirection != other.timesSameDirection) return false

            return true
        }

        override fun hashCode(): Int {
            var result = coord.hashCode()
            result = 31 * result + (oldDir?.hashCode() ?: 0)
            result = 31 * result + timesSameDirection
            return result
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day17().run()
        }
    }
}