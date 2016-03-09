package io.pivotal.dis.ingest.domain


import java.time.LocalDateTime
import java.util.*
import java.util.function.Function

class Digest(val disruptions: List<DisruptedLine>, val lastUpdated: Long) {

    private fun getLongFieldFromDisruptedLine(lineName: String,
                                              fieldExtractor: Function<DisruptedLine, Long>): Optional<Long> {

        if (isLineDisrupted(lineName)) {
            val line = getLine(lineName).get()
            return Optional.of(fieldExtractor.apply(line))
        } else {
            return Optional.empty<Long>()
        }
    }

    private fun isLineDisrupted(lineName: String): Boolean {
        return disruptions.any { disruptedLine -> disruptedLine.line == lineName }
    }

    private fun getLine(lineName: String): Optional<DisruptedLine> {
        return Optional.ofNullable(disruptions.filter { disruptedLine -> disruptedLine.line == lineName }.firstOrNull())
    }

    fun getStartTimestampFromDisruptedLine(lineName: String): Optional<Long> {
        return getLongFieldFromDisruptedLine(lineName, Function<DisruptedLine, Long> { it.startTimestamp })
    }

    fun getEndTimestampFromDisruptedLine(lineName: String): Optional<Long> {
        return getLongFieldFromDisruptedLine(lineName, Function<DisruptedLine, Long> { it.endTimestamp })
    }

    fun getEarliestEndTimestampFromDisruptedLine(lineName: String): Optional<Long> {
        return getLongFieldFromDisruptedLine(lineName, Function<DisruptedLine, Long> { it.earliestEndTimestamp })
    }

    fun getLatestEndTimestampFromDisruptedLine(lineName: String): Optional<Long> {
        return getLongFieldFromDisruptedLine(lineName, Function<DisruptedLine, Long> { it.latestEndTimestamp })
    }
}
