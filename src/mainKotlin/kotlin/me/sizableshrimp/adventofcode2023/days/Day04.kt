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

import me.sizableshrimp.adventofcode2023.helper.Processor
import me.sizableshrimp.adventofcode2023.templates.Day
import kotlin.math.min

class Day04 : Day() {
    override fun evaluate(): Result {
        val cards = this.lines.map {
            val colonIdx = it.indexOf(':')
            val split = it.substring(colonIdx + 2).trim().split(" | ")
            val winningNums = split[0].trim().split(" ").mapNotNull { if (it.isEmpty()) null else it.trim().toInt() }.toSet()
            val heldNums = split[1].trim().split(" ").mapNotNull { if (it.isEmpty()) null else it.trim().toInt() }.toSet()
            Card(it.substring(it.indexOf(' '), colonIdx).trim().toInt(), winningNums, heldNums, Processor.intersection(winningNums, heldNums).size)
        }
        val cardWins = IntArray(cards.size) { 1 }

        for ((i, wins) in cardWins.withIndex()) {
            if (wins == 0) continue
            val card = cards[i]
            for (j in 1..min(card.matches, cards.size)) {
                cardWins[i + j] += wins
            }
        }

        return Result.of(cards.sumOf { if (it.matches == 0) 0 else (1..it.matches).reduce { acc, _ -> acc * 2 } }, cardWins.sum())
    }

    private data class Card(val id: Int, val winningNums: Set<Int>, val heldNumbers: Set<Int>, val matches: Int)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day04().run()
        }
    }
}