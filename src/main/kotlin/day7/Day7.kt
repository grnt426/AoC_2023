package day7

import java.io.File

enum class Type(order: Int) {
    FIVE_KIND(6),
    FOUR_KIND(5),
    FULL(4),
    THREE_KIND(3),
    TWO_PAIR(2),
    ONE_PAIR(1),
    HIGH(0),
    NONE(-1)
}
data class Hand(val cards: String, val bid: Long, val type: Type) : Comparator<Hand> {

    init {
//        val strength =
    }

    override fun compare(o1: Hand?, o2: Hand?): Int {
        if (o1 == null)
            return 1
        else if (o2 == null)
            return -1
        else {

        }

        return 0
    }

}
fun main(_args: Array<String>) {

    /**
     * Solution:
     */
    println("Puzzle 1: ${findTotalWinnings("input/day7/input")}")

    /**
     * Solution:
     */
//    println("Puzzle 2: ${findLowestLocationAmongRanges("input/day7/input")} (example)")

}

fun findTotalWinnings(filename: String): Long {
    val hands = mutableListOf<Hand>()
    File(filename).forEachLine { line ->
        val (cards, bid) = line.split(" ")
//        hands.add(Hand(cards, bid.toLong()))
    }

//    hands.sortWith()
    return 0
}