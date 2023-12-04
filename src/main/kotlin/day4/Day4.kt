package day4

import java.io.File
import kotlin.math.min
import kotlin.math.pow

data class Card(val wins: Int, var copies: Int)

fun main(_args: Array<String>) {

    /**
     * Solution: 18619
     */
    println("Puzzle 1: ${sumWinningMatches("input/day4/input")}")

    /**
     * Solution: 8063216
     */
    println("Puzzle 2: ${playInsaneScratchCardGame("input/day4/input")}")
}

fun sumWinningMatches(filename: String): Int {
    var total = 0
    File(filename).forEachLine { line ->
        val (winning, ours) = processGame(line)
        total += 2.0.pow(ours.intersect(winning).size.toDouble() - 1.0).toInt()
    }

    return total
}

fun playInsaneScratchCardGame(filename: String): Int {
    val games = mutableListOf<Card>()
    File(filename).forEachLine { line ->
        val (winning, ours) = processGame(line)
        games.add(Card(ours.intersect(winning).size, 0))
    }

    games.forEachIndexed { index, card ->
        for (w in 1..min(card.wins, 202)) {
            games[w + index].copies += card.copies + 1
        }
    }

    return games.sumOf { card -> card.copies } + games.size
}

fun processGame(line: String): Pair<Set<Int>, Set<Int>> {
    val data = line.split(":  ", ": ").last().split(" |  ", " | ")
    val winning = data.first().split("  ", " ").map { s -> s.toInt() }.sorted().toSet()
    val ours = data.last().split("  ", " ").map { s -> s.toInt() }.sorted().toSet()
    return Pair(winning, ours)
}