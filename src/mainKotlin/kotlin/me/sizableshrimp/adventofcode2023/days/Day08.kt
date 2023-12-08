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

import me.sizableshrimp.adventofcode2023.templates.Day
import me.sizableshrimp.adventofcode2023.util.lcm

class Day08 : Day() {
    override fun evaluate(): Result {
        val nodes = mutableMapOf<String, Node>()
        val getNode = { it: String ->
            nodes.getOrPut(it) {
                Node(it)
            }
        }

        this.lines.drop(2).map { line ->
            val node = getNode(line.substring(0, 3))
            node.left = getNode(line.substring(7, 10))
            node.right = getNode(line.substring(12, 15))
        }

        val instructions = this.lines[0]
        val startNodes = nodes.values.filter { it.name[2] == 'A' }

        val loops = startNodes.associate { startNode ->
            var steps = 0
            var i = 0
            var node = startNode
            val seen = mutableSetOf<Any>()
            seen.add(0 to startNode)

            do {
                if (i == instructions.length)
                    i = 0

                val child = if (instructions[i++] == 'R') node.right else node.left
                node = child!!
                steps++
            } while (node.name[2] != 'Z')

            startNode.name to steps.toLong()
        }

        return Result.of(loops["AAA"], loops.values.lcm())
    }

    private class Node(val name: String) {
        var left: Node? = null
        var right: Node? = null

        override fun toString() = this.name
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day08().run()
        }
    }
}