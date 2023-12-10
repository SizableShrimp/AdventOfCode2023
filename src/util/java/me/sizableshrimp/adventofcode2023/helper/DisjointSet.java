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

package me.sizableshrimp.adventofcode2023.helper;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Useful for finding/merging clusters in a data set, like on Day 25 of Advent of Code 2018.
 */
public class DisjointSet {
    private final int[] parent;
    private final int[] rank;
    private int numSets;

    /**
     * Create a disjoint set of the given size.
     * This size is immutable.
     * All elements start out in their own disjoint set.
     *
     * @param size the size of the disjoint set
     */
    public DisjointSet(int size) {
        this.parent = new int[size];
        this.rank = new int[size];
        this.numSets = size;

        for (int i = 0; i < size; i++) {
            this.parent[i] = i;
        }
    }

    /**
     * Resolve the root of the given element.
     *
     * @param i the starting element
     * @return the root of the given element
     */
    public int find(int i) {
        int child = this.parent[i];
        if (i == child)
            return i;

        // Optimize future calls with the same input by collapsing the path.
        return this.parent[i] = find(child);
    }

    /**
     * Unify two disjointed sets into a merged set.
     */
    public void union(int x, int y) {
        if (x == y)
            return;

        int xRoot = find(x);
        int yRoot = find(y);
        if (xRoot != yRoot) {
            int xRank = this.rank[xRoot];
            int yRank = this.rank[yRoot];
            if (xRank < yRank) {
                this.parent[xRoot] = yRoot;
            } else {
                this.parent[yRoot] = xRoot;
                if (xRank == yRank)
                    this.rank[xRoot]++;
            }
            this.numSets--;
        }
    }

    public static <T> DisjointSet findClusters(List<T> data, BiPredicate<T, T> inSameCluster) {
        DisjointSet disjointSet = new DisjointSet(data.size());

        for (int i = 0; i < data.size(); i++) {
            T element = data.get(i);
            for (int j = i + 1; j < data.size(); j++) {
                if (inSameCluster.test(element, data.get(j))) {
                    disjointSet.union(i, j);
                }
            }
        }

        return disjointSet;
    }

    public <T> List<Set<T>> resolve(List<T> data) {
        Int2IntArrayMap map = new Int2IntArrayMap(this.numSets);
        for (int i = 0; i < this.parent.length; i++) {
            int root = find(i);
            map.putIfAbsent(root, map.size());
        }

        List<Set<T>> result = new ArrayList<>(this.numSets);

        for (int i = 0; i < this.numSets; i++) {
            result.add(new HashSet<>());
        }

        for (int i = 0; i < this.parent.length; i++) {
            int root = this.find(i);
            int index = map.get(root);
            result.get(index).add(data.get(i));
        }

        return result;
    }

    public int getNumSets() {
        return this.numSets;
    }
}
