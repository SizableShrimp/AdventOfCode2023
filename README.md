# AdventOfCode2023
This repository holds my solutions and helper code (in Java/Kotlin) for [Advent Of Code 2023](https://adventofcode.com/2023).
A large part of the util code is based on code I have built up from prior years which you can find in my other AOC GitHub repos.

## Technical Information
This repository supports downloading and caching input data from the Advent Of Code servers.
To set up, you must create a `session.txt` file in the program working directory which holds your session cookie from the Advent Of Code website.
This cookie expires after a month, so it has to be updated from time to time.

Each day's input file is formatted as `dayXX.txt`. If the day is 1-9, then the file will be named `day01.txt`, `day02.txt`, etc.

When running a specific day, the program will look for an input file in the following order:
1. Checks the `days` folder on the classpath, if it's there.
2. Checks the `aoc_input` folder in the current working directory, if it's there.
3. If there's a `session.txt` file with a logged-in cookie, it downloads the input from the AOC servers and saves it to the `aoc_input` folder (creating it if necessary).
4. If the input file can't be found, it throws an exception.

The `session.txt` file should be a single line and should contain your session cookie from the AOC website.
You can find this cookie by logging into the AOC website, opening the developer tools, and looking for the "session" cookie.
Only the cookie's value should be pasted into the file.

### Running A Specific Day
This program runs a specific day based on a few factors inside the `Main` class.
If it is the month of December in EST time and the 25th or earlier, whatever current day it is in EST time will be selected to run.
If this program is not run during Advent season, it will run ALL days by default.
An output from a day looks like the following:
```text
Day 1:
Part 1: 123
Part 2: 456
Completed in 5.298ms
```
Note that "Completed in X.XXXms" is not a real or accurate benchmark, but an approximate measure of how long a day takes to run using nano time.
For real benchmarks, please see `gradle jmh` and the `benchmarks` package (which is still based on your specific hardware).
If you would like to run a specific day outside the month of the December, use `run(int)` in the `Main` class.