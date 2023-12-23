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

import me.sizableshrimp.adventofcode2023.templates.Day;
import me.sizableshrimp.adventofcode2023.templates.ZCoordinate;
import me.sizableshrimp.adventofcode2023.templates.ZDirection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Day22 extends Day {
    public static void main(String[] args) {
        new Day22().run();
    }

    @Override
    protected Result evaluate() {
        List<Brick> bricks = new ArrayList<>(this.lines.size());

        for (String line : this.lines) {
            String[] split = line.split("~");
            ZCoordinate start = ZCoordinate.parse(split[0]);
            ZCoordinate end = ZCoordinate.parse(split[1]);
            bricks.add(new Brick(start, end));
        }

        bricks.sort(Comparator.comparing(b -> b.start.z()));

        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            ZCoordinate start = brick.start;
            ZCoordinate end = brick.end;

            outer:
            while (true) {
                start = start.resolve(ZDirection.DOWN);
                end = end.resolve(ZDirection.DOWN);

                if (start.z() <= 0)
                    break;

                for (int j = 0; j < i; j++) {
                    Brick other = bricks.get(j);
                    if (other.intersects(start, end))
                        break outer;
                }
            }

            bricks.set(i, new Brick(start.resolve(ZDirection.UP), end.resolve(ZDirection.UP)));
        }

        // Map of brick to all bricks supporting it
        Map<Brick, BitSet> supportingBricks = new HashMap<>();
        // Map of brick to all bricks it supports
        Map<Brick, BitSet> supportedBricks = new HashMap<>();

        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            if (brick.start.z() == 1)
                continue;

            ZCoordinate start = brick.start.resolve(ZDirection.DOWN);
            ZCoordinate end = new ZCoordinate(brick.end.x(), brick.end.y(), start.z());

            for (int j = 0; j < i; j++) {
                Brick other = bricks.get(j);
                if (other.intersects(start, end)) {
                    supportingBricks.computeIfAbsent(brick, b -> new BitSet()).set(j);
                    supportedBricks.computeIfAbsent(other, b -> new BitSet()).set(i);
                }
            }
        }

        BitSet shouldDisintegrate = supportingBricks.values().stream()
                .filter(s -> s.cardinality() == 1)
                .reduce(new BitSet(), (acc, b) -> {
                    acc.or(b);
                    return acc;
                });
        int safeBricks = bricks.size() - shouldDisintegrate.cardinality();

        int p2 = 0;

        for (int i = shouldDisintegrate.nextSetBit(0); i >= 0; i = shouldDisintegrate.nextSetBit(i + 1)) {
            BitSet seen = new BitSet(i + 1);
            seen.set(i);
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(i);

            while (!queue.isEmpty()) {
                int idx = queue.remove();
                Brick brick = bricks.get(idx);
                BitSet supportedSet = supportedBricks.get(brick);

                if (supportedSet == null)
                    continue;

                for (int j = supportedSet.nextSetBit(0); j >= 0; j = supportedSet.nextSetBit(j + 1)) {
                    BitSet intersection = (BitSet) seen.clone();
                    BitSet supportingSubSet = supportingBricks.get(bricks.get(j));
                    intersection.and(supportingSubSet);
                    if (intersection.equals(supportingSubSet) && !seen.get(j)) {
                        seen.set(j);
                        queue.add(j);
                    }
                }
            }

            p2 += seen.cardinality() - 1;
        }

        return Result.of(safeBricks, p2);
    }

    private record Brick(ZCoordinate start, ZCoordinate end) {
        private boolean intersects(ZCoordinate otherStart, ZCoordinate otherEnd) {
            return this.start.x() <= otherEnd.x() && this.end.x() >= otherStart.x()
                   && this.start.y() <= otherEnd.y() && this.end.y() >= otherStart.y()
                   && this.start.z() <= otherEnd.z() && this.end.z() >= otherStart.z();
        }
    }
}
