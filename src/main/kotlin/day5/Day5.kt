package day5

import java.io.File

data class Seed(var pos: Long, var moved: Boolean)
data class SeedRange(var range: LongRange, var moved: Boolean)

// this will be a Long with only the most significant bit flipped
var BIT_CACHE = mutableMapOf<Long, Long>()
var expectedSeeds = 0L
var actualPlanted = 0L

fun main(_args: Array<String>) {

    /**
     * Solution: 282277027
     */
    println("Puzzle 1: ${findLowestLocation("input/day5/input")}")

    /**
     * Solution: 46
     */
    println("Puzzle 2: ${fuckItBruteForce("input/day5/example")} (example)")

    /**
     * Solution:
     */
//    println("Puzzle 2: ${findLowestLocationAmongRanges("input/day5/input")}")

    println("Puzzle 2: ${fuckItBruteForce("input/day5/input")}")
}

fun fuckItBruteForce(filname: String): Long {
    /**
     * We use the 64 bit longs as flags to indicate if a seed is stored in little endian format.
     * So, array[0] will get the first 64 values,
     * where 10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000
     * or 0x800000000000000
     * means seed of id "0" is the only one present. Likewise, 0x4000000000000000
     * means seed of id "1" is the only one present. The array is initialized up to 10B,
     * which no number in the input exceeds.
     *
     * We use a moved array so moved seeds aren't accidentally moved a second time.
     */
    var processing = LongArray((10_000_000_000L / 64L).toInt()) { 0 }
    var moved = LongArray((10_000_000_000L / 64L).toInt()) { 0 }

    /**
     * For faster padding of the ends, we use a bit cache, with the assumption that all pads will
     * either be of the form 00...11..1 or 11..00..0. With this format, we can pad the beginning
     * and end of a buffer fill operation with two map lookups rather than a series of shifts.
     */
    var bitVal = 0L
    for (l in 0L..63L) {
        BIT_CACHE.put(l, bitVal)
        bitVal = bitVal.shl(1) or 1
    }
    BIT_CACHE[64] = Long.MIN_VALUE


    var state = "initial"
    File(filname).forEachLine read@{ line ->
        if (line.isEmpty())
            return@read
        when (state) {
            "initial" -> { // first line of seeds
                if (line.contains("map")) {
                    state = "ranges"
                } else {
                    line.split(": ").last().split(" ").chunked(2).map { (s, r) ->
                        expectedSeeds += r.toLong()
                        fillRange(processing, s.toLong(), s.toLong() + r.toLong() - 1)
                    }
                }
            }
            "ranges" -> {

            }
        }
    }

    assert(expectedSeeds == actualPlanted) {
        "the expected number of seeds '$expectedSeeds' does not equal the actual planted '$actualPlanted'"
    }

    return 5
}

fun fillRange(target: LongArray, start: Long, end: Long) {

    // handle single bucket case
    if (start / 64L == end / 64L) {
        var selector = 4611686018427387904L.shr((start % 64).toInt())
        var shifts = (end - start) % 64
        println("Planting $shifts seeds in a single bucket")
        actualPlanted += shifts + 1
        while (shifts >= 0) {
            target[(start / 64).toInt()] = target[(start / 64).toInt()] or selector
            selector = selector.shr(1)
            shifts--
        }
    }
    else {

        val fillIndexStart = if (start % 64L == 0L) {
            (start / 64L).toInt()
        }

        // handle first bucket potentially unfilled
        else {
            val filled = 64 - (start % 64)
            println("Planting $filled seeds in an unfilled start bucket")
            actualPlanted += filled
            target[(start / 64).toInt()] = BIT_CACHE[filled]!!
            (start / 64L).toInt() + 1
        }


        val fillIndexEnd = if (end % 64L == 0L) {
            (end / 64L).toInt()
        }

        // handle last bucket potentially unfilled
        else {
            val filled = 63 - (end % 64)
            actualPlanted += (end % 64) + 1
            println("Planting ${(end % 64) + 1} seeds in an unfilled end bucket")
            target[(end / 64).toInt()] = target[(end / 64).toInt()] or (BIT_CACHE[filled]!!.inv())
            (end / 64L).toInt() - 1
        }

        if (fillIndexStart < fillIndexEnd + 1) {
            // fills non-inclusively
            val planted = ((fillIndexEnd + 1) - fillIndexStart) * 64
            actualPlanted += planted
            println("Planting $planted seeds as full buffer")
            target.fill(Long.MAX_VALUE, fillIndexStart, fillIndexEnd + 1)
        }
    }
}

fun findLowestLocationAmongRanges(filname: String): Long {
    var state = "initial"
    var followedSeeds = mutableListOf<SeedRange>()
    File(filname).forEachLine read@{ line ->
        if (line.isEmpty())
            return@read
        when (state) {
            "initial" -> { // first line of seeds
                if (line.contains("map")) {
                    state = "ranges"
                } else {
                    followedSeeds.addAll(line.split(": ").last().split(" ").chunked(2).map { (s, r) ->
                        val start = s.toLong()
                        val range = r.toLong() - 1
                        SeedRange(LongRange(start, start + range), false)
                    })
                }
            }

            "ranges" -> {
                if (line.contains("map")) {
                    followedSeeds.forEach { it.moved = false }
                } else {
                    // DST IS FIRST, SRC IS SECOND
                    val (dst, src) = processRanges(line)
                    val processed = followedSeeds.filter { it.moved }.toMutableList()
                    followedSeeds.retainAll { !it.moved }
                    while (followedSeeds.isNotEmpty()) {
                        val s = followedSeeds.removeFirst()
                        val seedRange = s.range
                        if (seedRange.start in src && seedRange.endInclusive in src) {
                            adjustRange(s, dst, src)
                            processed.add(s)
                        } else if (src.start in seedRange && src.endInclusive in seedRange) {
                            breakupRanges(
                                seedRange.start, src.start - 1,
                                src.start, src.endInclusive,
                                src.endInclusive + 1, seedRange.endInclusive,
                                dst, src,
                                followedSeeds, processed
                            )
                        } else if (seedRange.start in src) {
                            breakupRanges(
                                0, -1,
                                seedRange.start, src.endInclusive,
                                src.endInclusive + 1, seedRange.endInclusive,
                                dst, src,
                                followedSeeds, processed
                            )
                        } else if (seedRange.endInclusive in src) {
                            breakupRanges(
                                seedRange.start, src.start - 1,
                                src.start, seedRange.endInclusive,
                                0, -1,
                                dst, src,
                                followedSeeds, processed
                            )
                        } else {
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

fun breakupRanges(
    lowerStart: Long, lowerEnd: Long,
    movedStart: Long, movedEnd: Long,
    upperStart: Long, upperEnd: Long,
    dst: LongRange, src: LongRange,
    followed: MutableList<SeedRange>, processed: MutableList<SeedRange>
) {
    if (lowerStart < lowerEnd) { // if false, they were equal or smaller
        val unmovedLower = LongRange(lowerStart, lowerEnd)
        assertValidRange(unmovedLower)
        followed.add(SeedRange(unmovedLower, false))
    }

    val moved = LongRange(movedStart, movedEnd)
    assertValidRange(moved)

    if (upperStart < upperEnd) { // if equal, they were equal or smaller
        val unmovedHigher = LongRange(upperStart, upperEnd)
        assertValidRange(unmovedHigher)
        followed.add(SeedRange(unmovedHigher, false))
    }

    val newSeedRange = SeedRange(moved, true)
    processed.add(newSeedRange)
    adjustRange(newSeedRange, dst, src)
    assertValidRange(moved)
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
    File(filname).forEachLine read@{ line ->
        if (line.isEmpty())
            return@read
        when (state) {
            "initial" -> { // first line of seeds
                if (line.contains("map")) {
                    state = "ranges"
                } else {
                    followedSeeds = line.split(": ").last().split(" ").map { s ->
                        Seed(s.toLong(), false)
                    }
                }
            }

            "ranges" -> { // seed to soil map section
                if (line.contains("map")) {
                    followedSeeds.forEach { it.moved = false }
                } else {
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

fun assertValidRange(range: LongRange) {
    assert(range.start <= range.endInclusive) {
        "${range.start} is not less than ${range.endInclusive}"
    }
}
