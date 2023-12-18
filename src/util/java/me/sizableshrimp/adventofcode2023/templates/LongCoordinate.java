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

package me.sizableshrimp.adventofcode2023.templates;

import java.util.Arrays;

/**
 * A 2-dimensional coordinate object that holds an x and y value.
 */
public record LongCoordinate(long x, long y) {
    public static final LongCoordinate ORIGIN = new LongCoordinate(0, 0);

    public static LongCoordinate of(long x, long y) {
        return new LongCoordinate(x, y);
    }

    public static LongCoordinate of(double x, double y) {
        return new LongCoordinate((long) x, (long) y);
    }

    public static LongCoordinate of(Coordinate coordinate) {
        return new LongCoordinate(coordinate.x(), coordinate.y());
    }

    /**
     * Parses a coordinate in the format "x,y".
     *
     * @param coord The input string of which to parse a coordinate.
     * @return A new {@link LongCoordinate} object.
     */
    public static LongCoordinate parse(String coord) {
        int commaIdx = coord.indexOf(',');
        long x = Long.parseLong(coord.substring(0, commaIdx));
        long y = Long.parseLong(coord.substring(commaIdx + 1));
        return new LongCoordinate(x, y);
    }

    public LongCoordinate resolve(LongCoordinate other) {
        return resolve(other.x, other.y);
    }

    /**
     * Creates a new {@link LongCoordinate} based on the offset of x and y given in parameters added to the current
     * coordinate.
     *
     * @param x The offset-x to add to the coordinate x.
     * @param y The offset-y to add to the coordinate y.
     * @return A new {@link LongCoordinate} made from adding the offset-x to the base x and offset-y to the base y.
     */
    public LongCoordinate resolve(long x, long y) {
        if (x == 0 && y == 0)
            return this;

        return new LongCoordinate(this.x + x, this.y + y);
    }

    public LongCoordinate multiply(LongCoordinate mut) {
        return multiply(mut.x, mut.y);
    }

    public LongCoordinate multiply(long xFactor, long yFactor) {
        if (xFactor == 1 && yFactor == 1)
            return this;

        return new LongCoordinate(this.x * xFactor, this.y * yFactor);
    }

    public LongCoordinate multiply(long factor) {
        return multiply(factor, factor);
    }

    /**
     * Rotates an (x,y) {@link LongCoordinate} around the {@link LongCoordinate#ORIGIN} by {@code degrees} which is a multiple of 90.
     *
     * @param degrees A degree value that is a multiple of 90.
     * @return A rotated {@link LongCoordinate}.
     */
    public LongCoordinate rotate90(int degrees) {
        if (degrees < 0)
            degrees = 360 + degrees;
        degrees %= 360;

        return switch (degrees) {
            case 90 -> this.swapXY().multiply(-1, 1);
            case 180 -> this.multiply(-1, -1);
            case 270 -> this.swapXY().multiply(1, -1);
            case 0 -> this;
            default -> throw new IllegalStateException("Degrees is not a multiple of 90: " + degrees);
        };
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public LongCoordinate swapXY() {
        return new LongCoordinate(this.y, this.x);
    }

    /**
     * Finds the Manhattan distance from this coordinate to the origin at (0, 0).
     *
     * @return The Manhattan distance from this coordinate to the origin at (0, 0).
     */
    public long distanceToOrigin() {
        return this.x + this.y;
    }

    /**
     * Finds the Manhattan distance from this coordinate to (x2, y2).
     *
     * @param x2 The x of the other coordinate.
     * @param y2 The y of the other coordinate.
     * @return The Manhattan distance from this coordinate to (x2, y2).
     */
    public long distance(long x2, long y2) {
        return Math.abs(this.x - x2) + Math.abs(this.y - y2);
    }

    /**
     * Finds the Manhattan distance from this coordinate to the other coordinate specified.
     *
     * @param other The coordinate object to compare with.
     * @return The Manhattan distance from this coordinate to the other coordinate specified.
     */
    public long distance(LongCoordinate other) {
        return distance(other.x, other.y);
    }

    /**
     * Finds the relative {@link Direction Direction} needed to get from this coordinate to the other coordinate specified.
     * Returns null if the two coordinates are not within one tile in cardinal or ordinal directions away.
     *
     * @param other Another coordinate used to find the relative direction towards.
     * @return The {@link Direction Direction} needed to move from this coordinate to the other coordinate specified,
     * or null if this is not possible in one move in cardinal or ordinal directions.
     */
    public Direction relative(LongCoordinate other) {
        return Arrays.stream(Direction.cardinalOrdinalDirections()).filter(d -> resolve(d).equals(other)).findAny().orElse(null);
    }

    public LongCoordinate resolve(Direction direction) {
        return resolve(direction.x, direction.y);
    }

    public LongCoordinate resolve(Direction direction, int count) {
        return resolve(direction.x * count, direction.y * count);
    }

    /**
     * Returns a coordinate of the form (x, y + 1)
     */
    public LongCoordinate down() {
        return resolve(Direction.SOUTH);
    }

    /**
     * Returns a coordinate of the form (x, y - 1)
     */
    public LongCoordinate up() {
        return resolve(Direction.NORTH);
    }

    /**
     * Returns a coordinate of the form (x - 1, y)
     */
    public LongCoordinate left() {
        return resolve(Direction.WEST);
    }

    /**
     * Returns a coordinate of the form (x + 1, y)
     */
    public LongCoordinate right() {
        return resolve(Direction.EAST);
    }

    /**
     * Returns the value on the direction axis.
     */
    public long getAxis(Direction.Axis axis) {
        return axis == Direction.Axis.X ? this.x : this.y;
    }

    public long component1() {
        return this.x;
    }

    public long component2() {
        return this.y;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", this.x, this.y);
    }
}
