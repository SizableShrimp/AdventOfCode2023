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

import me.sizableshrimp.adventofcode2023.helper.FieldUtil;
import me.sizableshrimp.adventofcode2023.templates.SeparatedDay;

import java.util.ArrayList;
import java.util.List;

public class Day01 extends SeparatedDay {
    private static final List<String> DIGITS = List.of(
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine"
    );
    private static final List<List<String>> DIGITS_CACHE = FieldUtil.make(DIGITS, l -> {
        List<List<String>> ret = new ArrayList<>(26);
        for (int i = 0; i < 26; i++) {
            ret.add(null);
        }
        for (String digitStr : l) {
            int i = digitStr.charAt(0) - 'a';
            List<String> subList = ret.get(i);
            if (subList == null) {
                subList = new ArrayList<>();
                ret.set(i, subList);
            }
            subList.add(digitStr);
        }
        return ret;
    });

    public static void main(String[] args) {
        new Day01().run();
    }

    @Override
    protected Object part1() {
        return this.simulate(false);
    }

    @Override
    protected Object part2() {
        return this.simulate(true);
    }

    private int simulate(boolean part2) {
        int sum = 0;
        for (String line : this.lines) {
            sum += findDigit(line, false, part2) * 10 + findDigit(line, true, part2);
        }
        return sum;
    }

    private int findDigit(String line, boolean reverse, boolean part2) {
        for (int i = reverse ? line.length() - 1 : 0; reverse ? i >= 0 : i < line.length(); i += reverse ? -1 : 1) {
            char c = line.charAt(i);
            if (Character.isDigit(c))
                return c - '0';

            if (part2 && (!reverse || i <= line.length() - 3)) {
                List<String> digits = DIGITS_CACHE.get(c - 'a');
                if (digits == null)
                    continue;
                for (String digit : digits) {
                    if (line.startsWith(digit, i))
                        return DIGITS.indexOf(digit) + 1;
                }
            }
        }

        throw new IllegalArgumentException();
    }
}
