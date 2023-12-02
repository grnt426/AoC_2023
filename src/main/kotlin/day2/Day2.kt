package day2

import java.io.File
import kotlin.math.max

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
     * Solution: 71535
     */
    println("Puzzle 2: ${findFewestCubes("input/day2/input")}")
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

fun findFewestCubes(filename: String): Int {
    var total = 0
    File(filename).forEachLine { line ->
        val pulls = line.substringAfter(":").split(";")
        val minMap = mutableMapOf(CubeColor.RED to 0, CubeColor.GREEN to 0, CubeColor.BLUE to 0)
        for (p in pulls) {
            val colors = extractNumColors(p)
            colors.asIterable().forEach { (color, count) ->  minMap[color] = max(minMap[color]!!, count)}
        }

        total += minMap.values.fold(1){
            acc, ele -> acc * ele
        }
    }
    return total
}

fun extractNumColors(data: String): Map<CubeColor, Int> = data.split(",").associate { d ->
    val parts = d.split(" ")
    CubeColor.valueOf(parts[2].uppercase()) to parts[1].toInt()
}