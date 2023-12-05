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
import me.sizableshrimp.adventofcode2023.util.splitOnBlankLines

class Day05 : Day() {
    override fun evaluate(): Result {
        // Oneliner!
        // java.io.File("aoc_input/day05.txt").readLines().also { lines ->
        //     lines.drop(2).joinToString("\n").split("\n\n").map { it.split("\n").drop(1) }.map { l ->
        //         l.map { it.split(" ").map(String::toLong) }.map { (destStart, inStart, length) ->
        //             inStart..<(inStart + length) to destStart - inStart
        //         }
        //     }.let { mappings ->
        //         lines[0].substring(7).split(" ").map { it.toLong() }.let { base ->
        //             listOf(base.map { it..it }.toList(), base.windowed(2, step = 2).map { (a, b) -> a..<(a + b) }.toList())
        //         }.map { seeds ->
        //             mappings.fold(seeds) { acc, l ->
        //                 l.fold(listOf<LongRange>() to acc) { toCheck, (mapper, offset) ->
        //                     toCheck.second.map {
        //                         (mapper.contains(it.first) to mapper.contains(it.last)).let { (hasF, hasL) ->
        //                             if (hasF && hasL)
        //                                 (it.first + offset)..(it.last + offset) to listOf()
        //                             else if (hasF)
        //                                 (it.first + offset)..(mapper.last + offset) to listOf((mapper.last + 1)..it.last)
        //                             else if (hasL)
        //                                 (mapper.first + offset)..(it.last + offset) to listOf(it.first..<mapper.first)
        //                             else if (it.contains(mapper.first) && it.contains(mapper.last))
        //                                 (mapper.first + offset)..(mapper.last + offset) to listOf(it.first..<mapper.first, (mapper.last + 1)..it.last)
        //                             else null to listOf(it)
        //                         }
        //                     }.unzip().let { (toCheck.first + it.first.filterNotNull()) to it.second.flatten() }
        //                 }.let { it.first + it.second }
        //             }.minOf { it.first }
        //         }
        //     }.also { (p1, p2) -> println("p1: $p1, p2: $p2") }
        // }

        val mappings = this.lines.drop(2).splitOnBlankLines().map { it.drop(1) }.map { l ->
            l.map { it.split(" ").map(String::toLong) }.map { (destStart, inStart, length) ->
                inStart..<(inStart + length) to destStart - inStart
            }
        }

        val seedsBase = this.lines[0].substring(7).split(" ").map { it.toLong() }
        val seedsP1 = seedsBase.map { it..it }.toSet()
        val seedsP2 = seedsBase.windowed(2, step = 2).map { (a, b) -> a..<(a + b) }.toSet()

        val (p1, p2) = listOf(seedsP1, seedsP2).map { seeds ->
            mappings.fold(seeds) { acc, l ->
                val nextRanges = mutableSetOf<LongRange>()
                val toCheck = ArrayDeque(acc)
                for ((mapper, offset) in l) {
                    val temp = mutableSetOf<LongRange>()
                    while (toCheck.isNotEmpty()) {
                        val source = toCheck.removeFirst()
                        val (output, remainder) = getRanges(source, mapper, offset)
                        output?.let { nextRanges.add(it) }
                        temp.addAll(remainder)
                    }
                    toCheck.addAll(temp)
                }
                nextRanges.addAll(toCheck)
                nextRanges
            }.minOf { it.first }
        }

        return Result.of(p1, p2)
    }

    private fun getRanges(source: LongRange, mapper: LongRange, offset: Long): Pair<LongRange?, Collection<LongRange>> {
        var containsFirst = mapper.contains(source.first)
        var containsLast = mapper.contains(source.last)
        if (containsFirst && containsLast)
            return (source.first + offset)..(source.last + offset) to listOf()

        if (containsFirst)
            return (source.first + offset)..(mapper.last + offset) to listOf((mapper.last + 1)..source.last)

        if (containsLast)
            return (mapper.first + offset)..(source.last + offset) to listOf(source.first..<mapper.first)

        containsFirst = source.contains(mapper.first)
        containsLast = source.contains(mapper.last)

        if (containsFirst && containsLast)
            return (mapper.first + offset)..(mapper.last + offset) to listOf(source.first..<mapper.first, (mapper.last + 1)..source.last)

        return null to listOf(source)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day05().run()
        }
    }
}