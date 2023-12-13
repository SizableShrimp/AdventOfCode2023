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
import me.sizableshrimp.adventofcode2023.util.repeat

class Day12 : Day() {
    override fun evaluate(): Result {
        val cache: MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf()
        val (p1, p2) = this.lines.map {
            it.split(" ").let { (a, b) -> a to b.split(",").map { it.toInt() } }
        }.let { n ->
            n.sumOf { findArrangements(cache, it.first, it.second) } to
                    n.sumOf { (data, groups) -> findArrangements(cache, "$data?$data?$data?$data?$data", groups.repeat(5)) }
        }

        return Result.of(p1, p2)
    }

    private fun findArrangements(cache: MutableMap<Pair<String, List<Int>>, Long>, data: String, groups: List<Int>): Long {
        val cacheKey = data to groups
        cache[cacheKey]?.let { return it }

        if (data.isEmpty())
            return (if (groups.isEmpty()) 1L else 0L)
                .also { cache[cacheKey] = it }

        if (groups.isEmpty())
            return (if (data.contains('#')) 0L else 1L)
                .also { cache[cacheKey] = it }

        val groupSearch = groups.first()
        val groupsCopy = groups.drop(1)
        var result = 0L
        val firstHash = data.indexOf('#').let { if (it == -1) data.length else it }

        for (i in data.indices) {
            val endIndex = i + groupSearch
            if (i > firstHash || endIndex > data.length)
                break

            val sub = data.substring(i, endIndex)
            if (sub.contains('.'))
                continue

            val withinList = endIndex < data.length
            if (withinList && data[endIndex] == '#')
                continue

            val subArrangements = findArrangements(cache, if (withinList) data.substring(endIndex + 1) else "", groupsCopy)
            result += subArrangements
        }

        return result.also { cache[cacheKey] = it }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day12().run()
        }
    }
}