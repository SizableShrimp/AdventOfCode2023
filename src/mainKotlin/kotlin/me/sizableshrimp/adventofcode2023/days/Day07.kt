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

import me.sizableshrimp.adventofcode2023.helper.OccurrenceHelper
import me.sizableshrimp.adventofcode2023.templates.Day

class Day07 : Day() {
    override fun evaluate(): Result {
        val input = this.lines.map { it.split(" ") }.map { (a, b) -> a to b.toInt() }

        val (p1, p2) = listOf(CARD_ORDER_P1, CARD_ORDER_P2).map { cardOrder ->
            val cache = mutableMapOf<String, Int>()
            val sorted = input.sortedWith(compareBy<Pair<String, Int>> { (hand, _) ->
                getHandWinning(cache, hand, cardOrder)
            }.thenComparing { (hand, _) -> getTieBreaker(hand, cardOrder) })

            sorted.withIndex().sumOf { (idx, pair) ->
                val bid = pair.second
                (idx + 1) * bid
            }
        }

        return Result.of(p1, p2)
    }

    private fun getHandWinning(cache: MutableMap<String, Int>, hand: String, cardOrder: String): Int {
        cache[hand]?.also { return it }

        if (cardOrder === CARD_ORDER_P2) {
            val jIdx = hand.indexOf('J')
            if (jIdx != -1) {
                val pre = hand.substring(0, jIdx)
                val post = hand.substring(jIdx + 1)
                return (1..<cardOrder.length).maxOf { i ->
                    getHandWinning(cache, pre + cardOrder[i] + post, cardOrder)
                }.also { cache[hand] = it }
            }
        }

        val occurrences = OccurrenceHelper.getOccurrences(hand.toCharArray().toList())

        when (occurrences.size) {
            1 -> 6 // Five of a kind
            2 -> {
                if (occurrences.values.any { it == 4L })
                    5 // Four of a kind
                else // if (sizesSet == setOf(2, 3))
                    4 // Full house
            }

            3 -> {
                if (occurrences.values.any { it == 3L })
                    3 // Three of a kind
                else // if (sizesSet == listOf(1, 2, 2))
                    2 // Two pair
            }

            4 -> 1 // One pair
            5 -> 0 // High card
            else -> error("Not possible")
        }.also {
            cache[hand] = it
            return it
        }
    }

    private fun getTieBreaker(card: String, cardOrder: String) =
        card.map { cardOrder.indexOf(it) }.reduce { acc, i -> (acc shl 4) or i }

    companion object {
        private const val CARD_ORDER_P1 = "23456789TJQKA"
        private const val CARD_ORDER_P2 = "J23456789TQKA"

        @JvmStatic
        fun main(args: Array<String>) {
            Day07().run()
        }
    }
}