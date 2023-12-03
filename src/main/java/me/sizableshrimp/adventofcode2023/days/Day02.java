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

package me.sizableshrimp.adventofcode2023.days;

import me.sizableshrimp.adventofcode2023.templates.SeparatedDay;

import java.util.ArrayList;
import java.util.List;

public class Day02 extends SeparatedDay {
    private List<Game> games;

    public static void main(String[] args) {
        new Day02().run();
    }

    @Override
    protected Object part1() {
        int sum = 0;

        outer:
        for (Game game : this.games) {
            for (Round round : game.rounds) {
                if (round.red > 12 || round.green > 13 || round.blue > 14)
                    continue outer;
            }

            sum += game.id;
        }

        return sum;
    }

    @Override
    protected Object part2() {
        int sum = 0;

        for (Game game : this.games) {
            int maxRed = 0;
            int maxGreen = 0;
            int maxBlue = 0;

            for (Round round : game.rounds) {
                maxRed = Math.max(maxRed, round.red);
                maxGreen = Math.max(maxGreen, round.green);
                maxBlue = Math.max(maxBlue, round.blue);
            }

            sum += maxRed * maxGreen * maxBlue;
        }

        return sum;
    }

    @Override
    protected void parse() {
        this.games = new ArrayList<>();

        for (String line : this.lines) {
            int colonIndex = line.indexOf(':');
            int id = Integer.parseInt(line.substring(5, colonIndex));
            String[] roundStrings = line.substring(colonIndex + 2).split(";");
            List<Round> rounds = new ArrayList<>();

            for (String round : roundStrings) {
                int r = 0;
                int g = 0;
                int b = 0;
                String[] sets = round.trim().split(",");

                for (String set : sets) {
                    set = set.trim();
                    int spaceIdx = set.indexOf(' ');
                    char type = set.charAt(spaceIdx + 1);
                    int value = Integer.parseInt(set.substring(0, spaceIdx));

                    switch (type) {
                        case 'r' -> r = value;
                        case 'g' -> g = value;
                        case 'b' -> b = value;
                    }
                }

                rounds.add(new Round(r, g, b));
            }

            this.games.add(new Game(id, rounds));
        }
    }

    private record Game(int id, List<Round> rounds) {}

    private record Round(int red, int green, int blue) {}
}
