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

import me.sizableshrimp.adventofcode2023.templates.SeparatedDay
import kotlin.random.Random

class Day25 : SeparatedDay() {
    override fun part1(): Int {
        val wires = mutableMapOf<String, MutableList<String>>()

        this.lines.map { l ->
            val (a, b) = l.split(": ")
            a to b.split(' ')
        }.forEach { m ->
            m.second.forEach {
                wires.computeIfAbsent(m.first) { mutableListOf() }.add(it)
                wires.computeIfAbsent(it) { mutableListOf() }.add(m.first)
            }
        }

        // val start = wires.keys.random(Random(153))
        // val end = findMaxPath(start, wires)
        val start = findMaxPath(wires.keys.random(Random(153)), wires).name
        val end = findMaxPath(start, wires)

        return sever(start, end.name, wires, minPath = end)
    }

    override fun part2() = null // No part 2 :)

    private fun sever(
        start: String, target: String, wires: MutableMap<String, MutableList<String>>,
        depth: Int = 3, minPath: Node? = findMinPath(start, target, wires)
    ): Int {
        if (depth == 0) {
            if (minPath != null)
                return 0 // There is a still a path, so we did not separate into 2 distinct groups

            val first = floodFill(start, wires).size
            // println(first)
            return first * (wires.size - first)
        }
        var temp = minPath!!

        for (i in 1..<minPath.dist) {
            temp = temp.prior!!
            val innerStart = temp.name
            val innerEnd = temp.prior!!.name
            val startChildren = wires[innerStart]!!
            val endChildren = wires[innerEnd]!!

            startChildren.remove(innerEnd)
            endChildren.remove(innerStart)
            sever(innerStart, innerEnd, wires, depth - 1).let { if (it != 0) return it }
            startChildren.add(innerEnd)
            endChildren.add(innerStart)
        }

        return 0
    }

    private fun floodFill(start: String, wires: Map<String, List<String>>): Set<String> {
        val queue = ArrayDeque<String>()
        queue.add(start)
        val seen = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val node = queue.removeLast()

            for (wire in wires[node]!!) {
                if (seen.add(wire))
                    queue.add(wire)
            }
        }

        return seen
    }

    private fun findMinPath(start: String, target: String, wires: Map<String, List<String>>): Node? {
        val queue = ArrayDeque<Node>()
        val startNode = Node(start, 0, null)
        queue.add(startNode)
        val distances = mutableMapOf<String, Int>()
        var minDist = Int.MAX_VALUE
        var minNode: Node? = null

        while (queue.isNotEmpty()) {
            val node = queue.removeLast()
            if (node.dist >= minDist)
                continue

            if (node.name == target) {
                minDist = node.dist
                minNode = node
                continue
            }

            val next = wires[node.name]!!
            val nextDist = node.dist + 1

            for (wire in next) {
                if (!distances.contains(wire) || distances[wire]!! > nextDist) {
                    val nextNode = Node(wire, nextDist, node)
                    distances[wire] = nextDist
                    queue.add(nextNode)
                }
            }
        }

        return minNode
    }

    private fun findMaxPath(start: String, wires: Map<String, List<String>>): Node {
        val queue = ArrayDeque<Node>()
        val startNode = Node(start, 0, null)
        queue.add(startNode)
        val distances = mutableMapOf<String, Node>()

        while (queue.isNotEmpty()) {
            val node = queue.removeLast()
            val next = wires[node.name]!!
            val nextDist = node.dist + 1

            for (wire in next) {
                if (!distances.contains(wire) || distances[wire]!!.dist > nextDist) {
                    val nextNode = Node(wire, nextDist, node)
                    distances[wire] = nextNode
                    queue.add(nextNode)
                }
            }
        }

        return distances.values.maxBy { it.dist }
    }

    private data class Node(val name: String, val dist: Int, val prior: Node?)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day25().run()
        }
    }
}