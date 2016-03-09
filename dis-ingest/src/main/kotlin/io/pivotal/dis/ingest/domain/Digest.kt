package io.pivotal.dis.ingest.domain


import java.util.*
import java.util.function.Function

class Digest(val disruptions: List<DisruptedLine>) {

    fun getStartTimeFromDisruptedLine(lineName: String): Optional<String> {
        return getFieldFromDisruptedLine(lineName, Function<DisruptedLine, String> { it.startTime })
    }

    fun getEndTimeFromDisruptedLine(lineName: String): Optional<String> {
        return getFieldFromDisruptedLine(lineName, Function<DisruptedLine, String> { it.endTime })
    }

    fun getEarliestEndTimeFromDisruptedLine(lineName: String): Optional<String> {
        return getFieldFromDisruptedLine(lineName, Function<DisruptedLine, String> { it.earliestEndTime })
    }

    fun getLatestEndTimeFromDisruptedLine(lineName: String): Optional<String> {
        return getFieldFromDisruptedLine(lineName, Function<DisruptedLine, String> { it.latestEndTime })
    }

    private fun getFieldFromDisruptedLine(lineName: String,
                                          fieldExtractor: Function<DisruptedLine, String>): Optional<String> {

        if (isLineDisrupted(lineName)) {
            val line = getLine(lineName).get()
            return Optional.of(fieldExtractor.apply(line))
        } else {
            return Optional.empty<String>()
        }
    }

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
