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
import me.sizableshrimp.adventofcode2023.templates.Day
import me.sizableshrimp.adventofcode2023.templates.Direction
import me.sizableshrimp.adventofcode2023.util.getCardinalNeighbors
import me.sizableshrimp.adventofcode2023.util.toCharGrid

class Day23 : Day() {
    override fun evaluate(): Result {
        val grid = this.lines.toCharGrid()
        val start = Coordinate(1, 0)
        val target = Coordinate(grid[0].size - 2, grid.size - 1)

        val paths = calculatePaths(start, target, grid)

        val p1 = traverse(paths, part2 = false)
        val p2 = traverse(paths, part2 = true)

        return Result.of(p1, p2)
    }

    private fun calculatePaths(start: Coordinate, target: Coordinate, grid: Array<CharArray>): Map<Long, List<GraphNodeOptimized>> {
        val queue = ArrayDeque<Node>()
        queue.add(Node(start, 0, start, null))
        val paths = mutableMapOf<Coordinate, MutableList<GraphNode>>()
        val seen = mutableSetOf<Coordinate>()
        seen.add(start)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val neighbors = grid.getCardinalNeighbors(node.coord).filter { (_, c) ->
                grid[c.y][c.x] != '#' && c != node.prior?.coord
            }

            if (neighbors.size != 1) {
                // Fork in the road or nowhere to go
                paths.computeIfAbsent(node.start) { mutableListOf() }.add(GraphNode(node.coord, node.dist, node.prior!!.coord.relative(node.coord)))

                val startNode = Node(node.coord, 0, node.coord, null)
                for ((_, next) in neighbors) {
                    if (seen.add(next))
                        queue.add(Node(next, 1, node.coord, startNode))
                }
            } else {
                queue.add(Node(neighbors[0].second, node.dist + 1, node.start, node))
            }
        }

        val ids = paths.keys.toList() + target

        val pathsOptimized = paths.entries.associate { (k, v) ->
            (1L shl ids.indexOf(k)) to v.map { n -> GraphNodeOptimized(1L shl ids.indexOf(n.coord), n.dist, n.dir) }
        }

        return pathsOptimized
    }

    private fun traverse(paths: Map<Long, List<GraphNodeOptimized>>, part2: Boolean) = traverse(paths, part2, 1, 0, 1)

    private fun traverse(paths: Map<Long, List<GraphNodeOptimized>>, part2: Boolean, coord: Long, dist: Int, seen: Long): Int {
        if (coord == TARGET_COORD)
            return dist

        var max = 0

        for ((next, nextDist, dir) in paths[coord]!!) {
            if (!part2 && dir != Direction.EAST && dir != Direction.SOUTH)
                continue

            if (next and seen == next)
                continue

            val result = traverse(paths, part2, next, dist + nextDist, seen or next)
            if (result > max)
                max = result
        }

        return max
    }

    private data class Node(val coord: Coordinate, val dist: Int, val start: Coordinate, val prior: Node?)

    private data class GraphNode(val coord: Coordinate, val dist: Int, val dir: Direction)

    private data class GraphNodeOptimized(val coord: Long, val dist: Int, val dir: Direction)

    companion object {
        private const val TARGET_COORD = 1L shl 35

        @JvmStatic
        fun main(args: Array<String>) {
            Day23().run()
        }
    }
}