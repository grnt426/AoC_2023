package day5

import java.io.File

data class Seed(var pos: Long, var moved: Boolean)
data class SeedRange(var range: LongRange, var moved: Boolean)

fun main(_args: Array<String>) {

    /**
     * Solution: 282277027
     */
    println("Puzzle 1: ${findLowestLocation("input/day5/input")}")

    /**
     * Solution: 46
     */
    println("Puzzle 2: ${findLowestLocationAmongRanges("input/day5/example")} (example)")

    /**
     * Solution:
     */
    println("Puzzle 2: ${findLowestLocationAmongRanges("input/day5/input")}")
}

fun findLowestLocationAmongRanges(filname: String): Long {
    var state = "initial"
    var followedSeeds = mutableListOf<SeedRange>()
    File(filname).forEachLine read@ { line ->
        if (line.isEmpty())
            return@read
        when(state) {
            "initial" -> { // first line of seeds
                if (line.contains("map")) {
                    state = "ranges"
                }
                else {
                    followedSeeds.addAll(line.split(": ").last().split(" ").chunked(2).map { (s, r) ->
                        val start = s.toLong()
                        val range = r.toLong() - 1
                        SeedRange(LongRange(start, start + range), false)
                    })
                }
            }
            "ranges" -> { // seed to soil map section
                if (line.contains("map")) {
                    followedSeeds.forEach { it.moved = false }
                }
                else {
                    // DST IS FIRST, SRC IS SECOND
                    val (dst, src) = processRanges(line)
                    val processed = followedSeeds.filter { it.moved }.toMutableList()
                    followedSeeds.retainAll { !it.moved }
                    while(followedSeeds.isNotEmpty()) {
                        val s = followedSeeds.removeFirst()
                        val seedRange = s.range
                        if (seedRange.start in src && seedRange.endInclusive in src) {
                            adjustRange(s, dst, src)
                            processed.add(s)
                        }
                        else if (seedRange.start in src) {
                            val unmoved = LongRange(src.endInclusive + 1, seedRange.endInclusive)
                            val moved = LongRange(seedRange.start, src.endInclusive)
                            followedSeeds.add(SeedRange(unmoved, false))
                            val newSeedRange = SeedRange(moved, true)
                            processed.add(newSeedRange)
                            adjustRange(newSeedRange, dst, src)
                        }
                        else if (seedRange.endInclusive in src) {
                            val unmoved = LongRange(seedRange.start, src.start - 1)
                            val moved = LongRange(src.start, seedRange.endInclusive)
                            followedSeeds.add(SeedRange(unmoved, false))
                            val newSeedRange = SeedRange(moved, true)
                            processed.add(newSeedRange)
                            adjustRange(newSeedRange, dst, src)
                        }
                        else {
                            processed.add(s)
                        }
                    }
                    followedSeeds.addAll(processed)
                    followedSeeds = followedSeeds.distinctBy { it.range }.toMutableList()
                }
            }
        }
    }

    return followedSeeds.minOf { s -> s.range.start }
}
fun adjustRange(s: SeedRange, dst: LongRange, src: LongRange) {
    val seedRange = s.range
    val start = seedRange.start
    val end = seedRange.endInclusive
    val newStart = start - src.start + dst.start
    val newEnd = end - src.start + dst.start
    s.range = LongRange(newStart, newEnd)
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