package day4

import java.io.File
import java.lang.Math.pow
import kotlin.math.pow

fun main(_args: Array<String>) {

    /**
     * Solution: 18619
     */
    println("Puzzle 1: ${sumWinningMatches("input/day4/input")}")

    /**
     * Solution:
     */
//    println("Puzzle 2: ${findGearRatios("input/day4/input")}")
}

fun sumWinningMatches(filename: String): Int {
    var total = 0
    File(filename).forEachLine { line ->
        val data = line.split(":  ", ": ").last().split(" |  ", " | ")
        val winning = data.first().split("  ", " ").map { s -> s.toInt() }.sorted().toSet()
        val ours = data.last().split("  ", " ").map { s -> s.toInt() }.sorted().toSet()
        total += 2.0.pow(ours.intersect(winning).size.toDouble() - 1.0).toInt()
    }

    return total
}