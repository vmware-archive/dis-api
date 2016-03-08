package io.pivotal.dis.ingest.domain

import io.pivotal.dis.ingest.domain.tfl.LineColor

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DisruptedLine(val status: String,
                    val line: String,
                    val startTimestamp: Long,
                    val endTimestamp: Long,
                    val earliestEndTimestamp: Long,
                    val latestEndTimestamp: Long) {
    val startTime: String
    val endTime: String
    val earliestEndTime: String
    val latestEndTime: String
    val backgroundColor: String
    val foregroundColor: String

    init {
        this.backgroundColor = LineColor.getBackgroundColorForLine(line)
        this.foregroundColor = LineColor.getForegroundColorForLine(line)

        this.startTime = epochMillisToTimeString(this.startTimestamp)
        this.endTime = epochMillisToTimeString(this.endTimestamp)
        this.earliestEndTime = epochMillisToTimeString(this.earliestEndTimestamp)
        this.latestEndTime = epochMillisToTimeString(this.latestEndTimestamp)
    }

    private fun epochMillisToTimeString(millis: Long?): String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis!!), ZoneOffset.UTC).format(TIME_FORMAT)
    }

    companion object {

        val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
    }
}
