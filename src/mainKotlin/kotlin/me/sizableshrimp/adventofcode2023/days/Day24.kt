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

import com.microsoft.z3.Context
import com.microsoft.z3.Status
import me.sizableshrimp.adventofcode2023.helper.Itertools
import me.sizableshrimp.adventofcode2023.templates.SeparatedDay

class Day24 : SeparatedDay() {
    private lateinit var stones: List<Pair<List<Long>, List<Int>>>

    override fun part1() = Itertools.combinations(this.stones, 2).count { (a, b) ->
        // We have 2 equations for the individual axes: y = velY * t + y_1 and x = velX * t + x_1
        // Solving for the classic point-slope form of a line equation, we have:
        // y = velY * t + y_1 => t = (y - y_1) / velY and x = velX * t + x_1 => t = (x - x_1) / velX
        // Then, setting the 2 equal to each other, we have:
        // (y - y_1) / velY = (x - x_1) / velX => y - y_1 = (velY / velX) * (x - x_1)
        // m = rise / run = velY / velX
        // So, we have y - y_1 = m * (x - x_1) in the form we expect.

        // Find the slopes of the 2 lines
        val m1 = a.second[1].toDouble() / a.second[0]
        val m2 = b.second[1].toDouble() / b.second[0]

        val (x1, y1) = a.first
        val (x2, y2) = b.first

        // y - y_1 = m * (x - x_1) => y = m * (x - x_1) + y_1 => y = m*x - m*x_1 + y_1
        // We want to solve for when the 2 line equations equal each other.
        // In this case, x is actually like our time variable.
        // Solving for x, we have x = (-m_2*x_2 + y_2 + m_1*x_1 - y_1) / (m_1 - m_2).
        // Note that for Part 1, the (x,y) intersection point can have decimal values.
        val intersectionX = (-m2 * x2 + y2 + m1 * x1 - y1) / (m1 - m2)

        if (!ALLOWED_RANGE.contains(intersectionX)) return@count false

        // x = velX * t + x_1 => t = (x - x_1) / velX, for either stone
        // We have to ensure that the time is greater or equal to 0 for both stones.
        val time1 = (intersectionX - x1) / a.second[0]
        val time2 = (intersectionX - x2) / b.second[0]

        if (time1 < 0 || time2 < 0) return@count false

        // y = velY * t + y_1, for either stone
        val intersectionY = a.second[1] * time1 + y1

        ALLOWED_RANGE.contains(intersectionY)
    }

    override fun part2() =
        Context().use { context ->
            val x = context.mkIntConst("x")
            val y = context.mkIntConst("y")
            val z = context.mkIntConst("z")
            val velX = context.mkIntConst("velX")
            val velY = context.mkIntConst("velY")
            val velZ = context.mkIntConst("velZ")

            // Solve the equation for an (x,y,z) + t*(velX,velY,velZ) that satisfies the equation for all stones
            // We can uniquely find the (x,y,z) and (velX,velY,velZ) with only 3 stones.
            // Why? No idea, but people better at math than me said so.
            val solver = context.mkSolver()
            for ((i, stone) in this.stones.take(3).withIndex()) {
                val (stoneX, stoneY, stoneZ) = stone.first
                val (stoneVelX, stoneVelY, stoneVelZ) = stone.second
                val hitTime = context.mkIntConst("hitTime_$i")

                solver.add(context.mkGe(hitTime, context.mkInt(0)))
                solver.add(
                    context.mkEq(
                        context.mkAdd(context.mkInt(stoneX), context.mkMul(context.mkInt(stoneVelX), hitTime)),
                        context.mkAdd(x, context.mkMul(velX, hitTime))
                    )
                )
                solver.add(
                    context.mkEq(
                        context.mkAdd(context.mkInt(stoneY), context.mkMul(context.mkInt(stoneVelY), hitTime)),
                        context.mkAdd(y, context.mkMul(velY, hitTime))
                    )
                )
                solver.add(
                    context.mkEq(
                        context.mkAdd(context.mkInt(stoneZ), context.mkMul(context.mkInt(stoneVelZ), hitTime)),
                        context.mkAdd(z, context.mkMul(velZ, hitTime))
                    )
                )
            }

            // Find the first time when the equation is satisfied
            val result = solver.check()
            if (result != Status.SATISFIABLE) error("No solution found")

            // Get the solution
            val model = solver.model

            listOf(x, y, z).sumOf { model.getConstInterp(it).toString().toLong() }
        }

    override fun parse() {
        this.stones = this.lines.map { l -> l.replace(" ", "") }.map { l ->
            l.split('@').map { it.split(',') }.let { (a, b) -> a.map { it.toLong() } to b.map { it.toInt() } }
        }
    }

    companion object {
        private val ALLOWED_RANGE = 200_000_000_000_000.0..400_000_000_000_000.0

        @JvmStatic
        fun main(args: Array<String>) {
            Day24().run()
        }
    }
}