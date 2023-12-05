package day5

import java.io.File

data class Seed(var pos: Long, var moved: Boolean)
fun main(_args: Array<String>) {

    /**
     * Solution: 282277027
     */
    println("Puzzle 1: ${findLowestLocation("input/day5/input")}")

    /**
     * Solution:
     */
//    println("Puzzle 2: ${findLowestLocation("input/day5/input")}")
}

fun findLowestLocation(filname: String): Long {
    var state = "initial"
    lateinit var followedSeeds: List<Seed>
    File(filname).forEachLine read@ { line ->
        if (line.isEmpty())
            return@read
        when(state) {
            "initial" -> { // first line of seeds
                if (line.contains("map")) {
                    state = "ranges"
                }
                else {
                    followedSeeds = line.split(": ").last().split(" ").map { s ->
                        Seed(s.toLong(), false)
                    }
                }
            }
            "ranges" -> { // seed to soil map section
                if (line.contains("map")) {
                    followedSeeds.forEach { it.moved = false }
                }
                else {
                    // DST IS FIRST, SRC IS SECOND
                    val (dst, src) = processRanges(line)
                    followedSeeds.filter { s -> !s.moved }.forEach { s ->
                        if (s.pos in src) {
                            s.moved = true
                            s.pos = s.pos - src.start + dst.start
                            assert(s.pos in dst)
                            assert(s.pos >= 0)
                        }
                    }
                }
            }
        }
    }

    return followedSeeds.minOf { it.pos }
}

/**
 * DESTINATION IS FIRST
 * SOURCE IS SECOND
 */
fun processRanges(line: String): Pair<LongRange, LongRange> {
    val (dst, src, rng) = line.split(" ").map { it.toLong() }
    return Pair(LongRange(dst, dst + rng - 1), LongRange(src, src + rng - 1))
}