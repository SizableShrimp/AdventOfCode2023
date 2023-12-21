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

class Day20 : Day() {
    override fun evaluate(): Result {
        val modules = this.lines.associate { line ->
            val (start, destinations) = line.split(" -> ")
            val destSplit = destinations.split(", ")
            val type = Type.getType(start[0])
            val name = if (type == Type.BROADCAST) start else start.substring(1)
            name to Module(type, name, destSplit)
        }

        // Map of module name to the modules that it receives from
        val receivers = modules.values.flatMap { m -> m.destinations.map { it to m.name } }
            .groupBy({ it.first }) { it.second }
        modules.values.filter { it.type == Type.CONJUNCTION }
            .forEach { m -> m.receivedPulses.putAll(receivers[m.name]!!.associateWith { false }) }
        // We assume that there is one module feeding into rx and that it is a conjunction module.
        val conjModule = modules[receivers["rx"]!!.first()]!!
        val conjModuleName = conjModule.name
        val loopIds = conjModule.receivedPulses.keys.toList()
        val loops = IntArray(loopIds.size) { -1 }
        var foundLoops = 0

        var lowPulses = 0
        var highPulses = 0

        outer@ for (buttonPress in 1..Int.MAX_VALUE) {
            val queue = ArrayDeque<Triple<String, String, Boolean>>()
            queue.add(Triple("", "broadcaster", false))

            while (queue.isNotEmpty()) {
                val (from, name, high) = queue.removeFirst()
                if (buttonPress <= 1000) {
                    if (high)
                        highPulses++
                    else
                        lowPulses++
                }
                if (!modules.containsKey(name))
                    continue

                if (high && name == conjModuleName) {
                    loops[loopIds.indexOf(from)] = buttonPress
                    foundLoops++
                    if (foundLoops == loops.size)
                        break@outer // We assume that the loop sizes are >1000 so that P1 is already solved by this point
                }

                modules[name]!!.processPulse(queue, from, high)
            }
        }

        return Result.of(highPulses.toLong() * lowPulses, loops.lcm())
    }

    private data class Module(val type: Type, val name: String, val destinations: List<String>) {
        val receivedPulses = mutableMapOf<String, Boolean>()
        var on = false

        private fun MutableList<Triple<String, String, Boolean>>.sendPulse(high: Boolean) {
            this@Module.destinations.forEach { this.add(Triple(this@Module.name, it, high)) }
        }

        fun processPulse(queue: MutableList<Triple<String, String, Boolean>>, from: String, high: Boolean) {
            when (this.type) {
                Type.BROADCAST -> queue.sendPulse(high)

                Type.FLIP_FLOP -> {
                    if (high)
                        return

                    if (this.on) {
                        this.on = false
                        queue.sendPulse(false)
                    } else {
                        this.on = true
                        queue.sendPulse(true)
                    }
                }

                Type.CONJUNCTION -> {
                    this.receivedPulses[from] = high
                    val sendHigh = !this.receivedPulses.values.all { it }
                    queue.sendPulse(sendHigh)
                }
            }
        }
    }

    private enum class Type {
        BROADCAST, FLIP_FLOP, CONJUNCTION;

        companion object {
            fun getType(c: Char) = when (c) {
                '%' -> FLIP_FLOP
                '&' -> CONJUNCTION
                else -> BROADCAST
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Day20().run()
        }
    }
}