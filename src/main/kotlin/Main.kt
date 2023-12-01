import java.io.File

fun main(_args: Array<String>) {
    /**
     * Solution: 54968
     */
    println("Puzzle 1: ${findCalibrationDigits("input/day1/input")}")

    /**
     * Solution: 54094
     */
    println("Puzzle 2: ${findCalibrationData("input/day1/input")}")
}

fun findCalibrationDigits(fileName: String): Int {
    var total = 0
    File(fileName).forEachLine { l ->
        val digits = l.toCharArray().filter { c -> c.isDigit() }
        val num = if (digits.size == 1) {
            digits[0].digitToInt() * 10 + digits[0].digitToInt()
        } else {
            digits[0].digitToInt() * 10 + digits.last().digitToInt()
        }
        total += num
    }

    return total
}

fun findCalibrationData(fileName: String): Int {
    var total = 0
    val matcher = Regex("(?=(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)|(\\d))")
    File(fileName).forEachLine { l ->
        val matches = matcher.findAll(l)
        val matchList = matches.toList()
        val tens = extractDigit(matchList[0].groupValues.first{v -> v.isNotEmpty()})
        val ones = if (matchList.size == 1)
            tens
        else
            extractDigit(matchList.last().groupValues.first{v -> v.isNotEmpty()})
        total += tens * 10 + ones
    }
    return total
}

fun extractDigit(match: String): Int {
    return when (match) {
        "one" -> 1
        "two" -> 2
        "three" -> 3
        "four" -> 4
        "five" -> 5
        "six" -> 6
        "seven" -> 7
        "eight" -> 8
        "nine" -> 9
        else -> { return match.toInt() }
    }
}
