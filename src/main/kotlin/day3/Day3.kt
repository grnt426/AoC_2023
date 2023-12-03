package day3

import java.io.File

class NumEntry(var value: Int, var marked: Boolean = false) {
    fun addDigit(digit: Int) {
        value = value * 10 + digit
    }
}
fun main(_args: Array<String>) {

    /**
     * Solution: 539433
     */
    println("Puzzle 1: ${sumAdjacent("input/day3/input")}")

    /**
     * Solution: 75847567
     */
    println("Puzzle 2: ${findGearRatios("input/day3/input")}")
}

fun readMap(filename: String, onlyAsterisk: Boolean = false): Pair<MutableMap<String, NumEntry>, MutableSet<String>> {
    val gridMap = mutableMapOf<String, NumEntry>()
    val symbolSet = mutableSetOf<String>()
    var row = 0
    File(filename).forEachLine { line ->
        var curEntry = NumEntry(0)
        line.forEachIndexed { index, c ->
            when {
                c == '.' -> {
                    curEntry = NumEntry(0)
                }
                c.isDigit() -> {
                    curEntry.addDigit(c.digitToInt())
                    gridMap[coords(row, index)] = curEntry
                }
                else -> {
                    curEntry = NumEntry(0)
                    if ((onlyAsterisk && c == '*') || !onlyAsterisk)
                        symbolSet.add(coords(row, index))
                }
            }
        }
        row++
    }

    return Pair(gridMap, symbolSet)
}

fun sumAdjacent(filename: String): Int {
    var total = 0
    val (gridMap, symbolSet) = readMap(filename)

    symbolSet.forEach { coords ->
        val (r, c) = coords.split(",")
        getSurrounding(r.toInt(), c.toInt()).forEach { coord ->
            val entry = gridMap[coord]
            if (entry != null && !entry.marked) {
                entry.marked = true
                total += entry.value
            }
        }
    }

    return total
}

fun findGearRatios(filename: String): Int {
    var total = 0
    val (gridMap, symbolSet) = readMap(filename, true)

    symbolSet.forEach { coords ->
        val (r, c) = coords.split(",")
        val adj = mutableListOf<NumEntry>()
        getSurrounding(r.toInt(), c.toInt()).forEach { coord ->
            val entry = gridMap[coord]
            if (entry != null && !entry.marked) {
                entry.marked = true
                adj.add(entry)
            }
        }

        if(adj.size == 2)
            total += adj[0].value * adj[1].value
    }

    return total
}

fun coords(row: Int, col: Int): String = "$row,$col"

fun getSurrounding(row: Int, col: Int): List<String> {
    val surroundingCells = mutableListOf<String>()
    val vectors = listOf(
        Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
        Pair(0, -1), Pair(0, 1),
        Pair(1, -1), Pair(1, 0), Pair(1, 1)
    )
    vectors.forEach { (r, c) ->
        val newR = row + r
        val newC = col + c
        if (newR in 0..140 && newC in 0..140)
            surroundingCells.add(coords(newR, newC))
    }
    return surroundingCells
}