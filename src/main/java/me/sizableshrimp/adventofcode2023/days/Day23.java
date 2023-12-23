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

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.sizableshrimp.adventofcode2023.helper.GridHelper;
import me.sizableshrimp.adventofcode2023.templates.Coordinate;
import me.sizableshrimp.adventofcode2023.templates.Day;
import me.sizableshrimp.adventofcode2023.templates.Direction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Day23 extends Day {
    private static final long TARGET_COORD = 1L << 35;

    public static void main(String[] args) {
        new Day23().run();
    }

    @Override
    protected Result evaluate() {
        char[][] grid = GridHelper.createCharGrid(this.lines);
        Coordinate start = new Coordinate(1, 0);
        Coordinate target = new Coordinate(grid[0].length - 2, grid.length - 1);

        Long2ObjectMap<List<GraphNodeOptimized>> paths = this.findPaths(start, target, grid);

        return new Result(traverse(paths, false), traverse(paths, true));
    }

    private int traverse(Long2ObjectMap<List<GraphNodeOptimized>> paths, boolean part2) {
        return traverse(paths, part2, 1L, 0, 1L);
    }

    private int traverse(Long2ObjectMap<List<GraphNodeOptimized>> paths, boolean part2, long coord, int dist, long seen) {
        if (coord == TARGET_COORD)
            return dist;

        int max = 0;

        for (GraphNodeOptimized node : paths.get(coord)) {
            if (!part2 && node.dir != Direction.EAST && node.dir != Direction.SOUTH)
                continue;

            if ((node.coord & seen) == node.coord)
                continue;

            int result = traverse(paths, part2, node.coord, dist + node.dist, seen | node.coord);
            if (result > max)
                max = result;
        }

        return max;
    }

    private Long2ObjectMap<List<GraphNodeOptimized>> findPaths(Coordinate start, Coordinate target, char[][] grid) {
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(new Node(start, 0, start, null));
        Map<Coordinate, List<GraphNode>> paths = new LinkedHashMap<>();
        Set<Coordinate> seen = new HashSet<>();

        while (!queue.isEmpty()) {
            Node node = queue.remove();
            List<Coordinate> neighbors = new ArrayList<>(4);
            for (Direction dir : Direction.cardinalDirections()) {
                Coordinate next = node.coord.resolve(dir);
                if (GridHelper.isValid(grid, next) && grid[next.y()][next.x()] != '#' && (node.prior == null || !next.equals(node.prior.coord)))
                    neighbors.add(next);
            }

            if (neighbors.size() != 1) {
                // Fork in the road or nowhere to go
                paths.computeIfAbsent(node.start(), k -> new ArrayList<>())
                        .add(new GraphNode(node.coord, node.dist, node.prior.coord.relative(node.coord)));

                Node startNode = new Node(node.coord, 0, node.coord, null);
                for (Coordinate neighbor : neighbors) {
                    if (seen.add(neighbor))
                        queue.add(new Node(neighbor, 1, node.coord, startNode));
                }
            } else {
                queue.add(new Node(neighbors.get(0), node.dist + 1, node.start, node));
            }
        }

        return optimizePaths(target, paths);
    }

    private static Long2ObjectMap<List<GraphNodeOptimized>> optimizePaths(Coordinate target, Map<Coordinate, List<GraphNode>> paths) {
        List<Coordinate> ids = new ArrayList<>(paths.keySet());
        ids.add(target);

        Long2ObjectMap<List<GraphNodeOptimized>> pathsOptimized = new Long2ObjectOpenHashMap<>();
        for (Map.Entry<Coordinate, List<GraphNode>> entry : paths.entrySet()) {
            long id = 1L << ids.indexOf(entry.getKey());
            List<GraphNodeOptimized> list = new ArrayList<>();

            for (GraphNode node : entry.getValue()) {
                GraphNodeOptimized graphNodeOptimized = new GraphNodeOptimized(1L << ids.indexOf(node.coord), node.dist, node.dir);
                list.add(graphNodeOptimized);
            }

            pathsOptimized.put(id, list);
        }

        return pathsOptimized;
    }

    private record Node(Coordinate coord, int dist, Coordinate start, Node prior) {}

    private record GraphNode(Coordinate coord, int dist, Direction dir) {}

    private record GraphNodeOptimized(long coord, int dist, Direction dir) {}
}
