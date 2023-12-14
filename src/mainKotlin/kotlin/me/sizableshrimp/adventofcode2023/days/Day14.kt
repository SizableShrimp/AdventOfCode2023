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
import me.sizableshrimp.adventofcode2023.helper.Printer
import me.sizableshrimp.adventofcode2023.templates.Coordinate
import me.sizableshrimp.adventofcode2023.templates.Day
import me.sizableshrimp.adventofcode2023.templates.Direction
import me.sizableshrimp.adventofcode2023.util.deepCopy
import me.sizableshrimp.adventofcode2023.util.toCharGrid

class Day14 : Day() {
    override fun evaluate(): Result {
        val grid = this.lines.toCharGrid()
        val seenKeys = mutableSetOf<String>()
        val seenGrids = mutableListOf<Array<CharArray>>()
        var p1 = -1

        val dirs = arrayOf(Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST)
        while (true) {
            for (dir in dirs) {
                for (y in if (dir == Direction.SOUTH) grid.indices.reversed() else grid.indices) {
                    for (x in if (dir == Direction.EAST) grid[y].indices.reversed() else grid[y].indices) {
                        if (grid[y][x] != 'O')
                            continue

                        val coord = Coordinate(x, y)
                        var temp = coord

                        while (GridHelper.isValid(grid, temp.resolve(dir)) && grid[temp.y + dir.y][temp.x + dir.x] == '.') {
                            temp = temp.resolve(dir)
                        }

                        if (temp != coord) {
                            grid[coord.y][coord.x] = '.'
                            grid[temp.y][temp.x] = 'O'
                        }
                    }
                }

                if (p1 == -1)
                    p1 = calculateLoad(grid)
            }

            val key = getKey(grid)

            if (!seenKeys.add(key)) {
                val seenIdx = seenKeys.toList().indexOf(key)
                val loopSize = seenGrids.size - seenIdx
                val stepsInto = seenIdx + 1
                val targetIdx = seenIdx + (1_000_000_000 - stepsInto) % loopSize
                val load = calculateLoad(seenGrids[targetIdx])
                return Result.of(p1, load)
            } else {
                seenGrids.add(grid.deepCopy())
            }
        }
    }

    private fun getKey(grid: Array<CharArray>): String {
        return Printer.toString(grid)
    }

    private fun calculateLoad(grid: Array<CharArray>): Int {
        var load = 0
        for (y in grid.indices) {
            for (x in 0..<grid[y].size) {
                val state = grid[y][x]
                if (state != 'O')
                    continue

                load += grid.size - y
            }
        }

        return load
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day14().run()
        }
    }
}