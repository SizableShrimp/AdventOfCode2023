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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.sizableshrimp.adventofcode2023.templates.SeparatedDay;

import java.util.ArrayList;
import java.util.List;

public class Day15 extends SeparatedDay {
    private String[] instructions;

    public static void main(String[] args) {
        new Day15().run();
    }

    @Override
    protected Object part1() {
        int sum = 0;

        for (int hash : this.getHashes(this.instructions)) {
            sum += hash;
        }

        return sum;
    }

    @Override
    protected Object part2() {
        String[] labels = new String[this.instructions.length];
        int[] focalLengths = new int[this.instructions.length];

        for (int i = 0; i < this.instructions.length; i++) {
            String instruction = this.instructions[i];

            if (instruction.charAt(instruction.length() - 1) == '-') {
                labels[i] = instruction.substring(0, instruction.length() - 1);
            } else {
                int eqIdx = instruction.indexOf('=');
                labels[i] = instruction.substring(0, eqIdx);
                focalLengths[i] = Integer.parseInt(instruction.substring(eqIdx + 1));
            }
        }

        IntList hashes = getHashes(labels);
        List<List<Lens>> boxes = new ArrayList<>(256);

        for (int i = 0; i < 256; i++) {
            boxes.add(new ArrayList<>());
        }

        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];
            List<Lens> box = boxes.get(hashes.getInt(i));
            int focalLength = focalLengths[i];
            int labelIdx = findLabelIndex(box, label);

            if (focalLength == 0) { // Remove
                if (labelIdx != -1)
                    box.remove(labelIdx);
            } else {
                Lens lens = new Lens(label, focalLength);
                if (labelIdx == -1) {
                    box.add(lens);
                } else {
                    box.set(labelIdx, lens);
                }
            }
        }

        int focusingPower = 0;

        for (int i = 0; i < boxes.size(); i++) {
            List<Lens> box = boxes.get(i);
            int boxNumber = i + 1;

            for (int j = 0; j < box.size(); j++) {
                int slot = j + 1;
                Lens lens = box.get(j);

                focusingPower += (boxNumber) * slot * lens.focalLength;
            }
        }

        return focusingPower;
    }

    private int findLabelIndex(List<Lens> box, String label) {
        for (int i = 0; i < box.size(); i++) {
            Lens lens = box.get(i);

            if (lens.label.equals(label))
                return i;
        }

        return -1;
    }

    private IntList getHashes(String[] arr) {
        IntList hashes = new IntArrayList(arr.length);

        for (String s : arr) {
            int hash = 0;
            for (int i = 0; i < s.length(); i++) {
                hash += s.charAt(i);
                hash *= 17;
                hash %= 256;
            }
            hashes.add(hash);
        }

        return hashes;
    }

    private record Lens(String label, int focalLength) {}

    @Override
    protected void parse() {
        this.instructions = this.lines.get(0).split(",");
    }
}
