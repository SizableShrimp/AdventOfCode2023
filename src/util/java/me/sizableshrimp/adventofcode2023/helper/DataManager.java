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

import me.sizableshrimp.adventofcode2023.AOCUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DataManager {
    private static final List<String> INVALID_SESSION_COOKIE_INPUT = List.of("""
               <!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
               <html><head>
               <title>500 Internal Server Error</title>
               </head><body>
               <h1>Internal Server Error</h1>
               <p>The server encountered an internal error or
               misconfiguration and was unable to complete
               your request.</p>
               <p>Please contact the server administrator at\s
                webmaster@localhost to inform them of the time this error occurred,
                and the actions you performed just before this error.</p>
               <p>More information about this error may be available
               in the server error log.</p>
               </body></html>
               """.split("\n"));
    private static final String EXPIRED_SESSION_COOKIE_INPUT = "Puzzle inputs differ by user.  Please log in to get your puzzle input.";
    private static String sessionCookie;

    /**
     * Locates input data for the specified day, numbered 1 through 25, inclusive, looking on the filesystem and potentially querying the
     * Advent of Code servers.
     * <p>
     * The input filename for a day is formatted as <code>dayXX.txt</code>, where <code>XX</code> is the specified day number.
     * If it is day 1-9, then the filename is formatted as <code>day01.txt</code>, <code>day02.txt</code>, etc.
     * This method checks for the existence of the day input file first in the <code>days</code> directory included on the classpath.
     * If the file is not found, it then checks if the file exists in the <code>aoc_input</code> directory inside the program working directory.
     * <p>
     * If no input text file is found, this method then attempts to connect to the Advent Of Code servers for input data.
     * If <code>session.txt</code> does not exist, this method will throw an {@link IllegalArgumentException}.
     * This session file should hold your session cookie for the <a href="http://adventofcode.com">Advent Of Code Website</a>.
     * This cookie can be found using browser inspection.
     * <p>
     * If a successful connection is made to the AOC servers, the input data is cached in <code>aoc_input/dayXX.txt</code>
     * relative to the program working directory.
     * <p>
     * The returned list of input data has each line from the input as a separate String.
     *
     * @param day the integer day of which to read input data
     * @return an unmodifiable {@link List} of Strings representing each line of input data
     * @throws IllegalArgumentException if an existing day input file cannot be found and <code>sessions.txt</code> does not exist
     */
    public static List<String> read(int day) throws IOException {
        Path path = getPath(day);

        if (Files.isRegularFile(path))
            return List.copyOf(Files.readAllLines(path));

        if (!loadSessionCookie())
            throw new IllegalArgumentException("Could not find input data for day " + day + ". Perhaps try creating a session.txt with your AOC session cookie?");

        return getDataFromServer(day, AOCUtil.YEAR, path);
    }

    private static boolean loadSessionCookie() throws IOException {
        if (sessionCookie != null)
            return true;

        Path path = Path.of("session.txt");

        if (!Files.exists(path))
            return false;

        sessionCookie = Files.readString(path).trim();
        return true;
    }

    private static List<String> getDataFromServer(int day, int year, Path path) throws IOException {
        Path parent = path.getParent();
        Files.createDirectories(parent);

        try {
            URI uri = new URI("https://adventofcode.com/" + year + "/day/" + day + "/input");
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("User-Agent",
                            "SizableShrimp-AOC-Data-Bot/2.0.2.3 (+https://github.com/SizableShrimp/AdventOfCode2023)")
                    .header("Cookie", "session=" + sessionCookie)
                    .build();

            HttpResponse<Stream<String>> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofLines());
            List<String> lines = response.body().toList();

            if (isInputInvalid(lines)) {
                // Input is invalid. Error out without saving the input data to disk.
                throw new IllegalStateException("session.txt contains an outdated or invalid session cookie. Please update it and try again.");
            }

            writeFile(path, lines);

            return List.copyOf(lines);
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isInputInvalid(List<String> lines) {
        if (lines.size() == INVALID_SESSION_COOKIE_INPUT.size() && lines.equals(INVALID_SESSION_COOKIE_INPUT)) {
            // Session cookie loaded from session.txt is considered invalid from the server.
            return true;
        }

        // Session cookie was valid at some point, but is now invalid due to expiring.
        return lines.size() == 1 && lines.get(0).equals(EXPIRED_SESSION_COOKIE_INPUT);
    }

    private static void writeFile(Path path, List<String> lines) throws IOException {
        // Remove empty last line of input files
        Files.writeString(path, String.join(System.lineSeparator(), lines));
    }

    private static Path getPath(int day) {
        String filename = "day" + AOCUtil.padDay(day) + ".txt";

        URL url = DataManager.class.getResource("/days/" + filename);
        if (url == null) {
            return getBasePath(filename);
        }

        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getBasePath(String filename) {
        return Path.of("aoc_input", filename);
    }

    /**
     * Reads all input data for a given year from the server using the provided AOC session cookie
     * and saves it to the "aoc_input" directory under the program working directory.
     * See {@link #read(int)} for more details.
     *
     * @param year The Advent Of Code year to read input data for each day.
     */
    public static void writeAllDaysToFile(int year) throws IOException {
        for (int i = 1; i <= 25; i++) {
            String filename = "day" + AOCUtil.padDay(i) + ".txt";
            Path path = getBasePath(filename);
            getDataFromServer(i, year, path);
        }
    }
}
