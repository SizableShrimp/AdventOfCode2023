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
import me.sizableshrimp.adventofcode2023.util.intersectWith
import me.sizableshrimp.adventofcode2023.util.repeat
import me.sizableshrimp.adventofcode2023.util.splitOnBlankLines

class Day19 : Day() {
    override fun evaluate(): Result {
        val splitBlanks = this.lines.splitOnBlankLines()

        val workflows = splitBlanks[0].associate { line ->
            val openIdx = line.indexOf('{')
            val name = line.substring(0, openIdx)
            val rules = line.substring(openIdx + 1, line.length - 1).split(',')
            val default = rules.last()
            val otherRules = rules.dropLast(1).map { it.split(':') }.map { (a, b) ->
                val id = a[0]
                val greaterThan = a[1] == '>'
                val num = a.substring(2).toInt()
                Rule(id, greaterThan, num, b)
            }
            name to Workflow(name, otherRules, default)
        } as MutableMap
        workflows["A"] = Workflow("A", listOf(), "")
        workflows["R"] = Workflow("R", listOf(), "")

        val parts = splitBlanks[1].map { line ->
            line.substring(1, line.length - 1).split(',').map { it.substring(2).toInt() }
        }

        val inWorkflow = workflows["in"]!!
        val p1 = parts.filter { part -> calculatePossible(workflows, inWorkflow, part.map { it..it }) == 1L }
            .sumOf { it.sum() }
        val p2 = calculatePossible(workflows, inWorkflow, (1..4000).repeat(4))

        return Result.of(p1, p2)
    }

    private fun calculatePossible(workflows: Map<String, Workflow>, workflow: Workflow, part: List<IntRange>): Long {
        if (workflow.name === "A")
            return part.map { r -> (r.last - r.first + 1).toLong() }.reduce(Long::times)
        else if (workflow.name === "R")
            return 0L

        var result = 0L
        val part = part.toMutableList()

        for (rule in workflow.rules) {
            val range = part[rule.idInt]
            val (included, excluded) = rule.getIncludeExcludeRanges(range)

            if (included != null) {
                part[rule.idInt] = included
                result += calculatePossible(workflows, workflows[rule.toDo]!!, part)
                part[rule.idInt] = range // Set it back
            }

            if (excluded != null) {
                part[rule.idInt] = excluded
            } else {
                // All are included, return early
                return result
            }
        }

        result += calculatePossible(workflows, workflows[workflow.default]!!, part)

        return result
    }

    private data class Workflow(val name: String, val rules: List<Rule>, val default: String)

    private data class Rule(val id: Char, val greaterThan: Boolean, val num: Int, val toDo: String) {
        val idInt = LOOKUP.indexOf(id)
        val range = if (this.greaterThan) (this.num + 1)..4000 else 1..<this.num

        fun getIncludeExcludeRanges(range: IntRange): Pair<IntRange?, IntRange?> {
            val included = range.intersectWith(this.range) ?: return null to range

            if (included == range)
                return range to null

            return if (this.greaterThan) {
                included to range.first..<included.first
            } else {
                included to (included.last + 1)..range.last
            }
        }

        override fun toString() = "$id${if (greaterThan) '>' else '<'}$num"
    }

    companion object {
        private val LOOKUP = listOf('x', 'm', 'a', 's')

        @JvmStatic
        fun main(args: Array<String>) {
            Day19().run()
        }
    }
}