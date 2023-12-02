package day2

import java.io.File
enum class CubeColor(s: String) {
    RED("red"),
    GREEN("green"),
    BLUE("blue")
}

fun main(_args: Array<String>) {

    /**
     * Solution: 2720
     */
    println("Puzzle 1: ${findPossibleGames("input/day2/input")}")

    /**
     * Solution:
     */
//    println("Puzzle 2: ${findPossibleGames("input/day2/input")}")
}

fun findPossibleGames(filename: String): Int {
    val MAX_CUBES = mapOf(CubeColor.RED to 12, CubeColor.GREEN to 13, CubeColor.BLUE to 14)
    var total = 0
    File(filename).forEachLine { line ->
        val gameId = line.substringBefore(":").substringAfter(" ").toInt()
        val pulls = line.substringAfter(":").split(";")
        var failed = 0
        for (p in pulls) {
            val colors = extractNumColors(p)
            failed += colors.filter { (color, count) -> MAX_CUBES[color]!! < count }.count()
        }
        if (failed == 0)
            total += gameId
    }
    return total
}

fun extractNumColors(data: String): Map<CubeColor, Int> = data.split(",").associate { d ->
    val parts = d.split(" ")
    CubeColor.valueOf(parts[2].uppercase()) to parts[1].toInt()
}