package io.pivotal.dis.ingest.domain

import io.pivotal.dis.ingest.domain.tfl.LineColor

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class DisruptedLine(val status: String,
                    val line: String,
                    val startTimestamp: Long,
                    val endTimestamp: Long,
                    val earliestEndTimestamp: Long,
                    val latestEndTimestamp: Long) {

    val startTime: String
        get() = epochMillisToTimeString(this.startTimestamp)

    val endTime: String
        get() = epochMillisToTimeString(this.endTimestamp)

    val earliestEndTime: String
        get() = epochMillisToTimeString(this.earliestEndTimestamp)

    val latestEndTime: String
        get() = epochMillisToTimeString(this.latestEndTimestamp)

    val backgroundColor: String
        get() = LineColor.getBackgroundColorForLine(line)

    val foregroundColor: String
        get() = LineColor.getForegroundColorForLine(line)

    companion object {

        val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

        private fun epochMillisToTimeString(millis: Long?): String {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis!!), ZoneOffset.UTC).format(TIME_FORMAT)
        }
    }
}
