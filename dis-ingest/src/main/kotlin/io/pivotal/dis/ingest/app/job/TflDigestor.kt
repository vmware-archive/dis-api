package io.pivotal.dis.ingest.app.job

import com.amazonaws.util.json.JSONException
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.pivotal.dis.ingest.domain.Digest
import io.pivotal.dis.ingest.domain.DisruptedLine
import io.pivotal.dis.ingest.domain.tfl.TflLine
import io.pivotal.dis.ingest.domain.tfl.LineStatus

import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.util.Optional
import java.util.stream.Stream

import com.squareup.moshi.Types.newParameterizedType
import java.util.stream.Collectors.toList

class TflDigestor(tflData: String,
                  private val currentTime: LocalDateTime,
                  previousDigest: Optional<String>) {

    private val tflLines: List<TflLine>
    private val previousDigest: Optional<Digest>

    init {

        try {
            this.tflLines = moshiTflLinesAdapter().fromJson(tflData)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        this.previousDigest = parseDigest(previousDigest)
    }

    private fun parseDigest(digest: Optional<String>): Optional<Digest> {
        return digest.map { string ->
            try {
                 moshiDigestAdapter().fromJson(string)
            } catch (e: IOException) {
                null
            }
        }
    }

    @Throws(JSONException::class, IOException::class)
    fun digest(): String {
        val disruptedLines = tflLines.filter({ line ->
            val lineStatus = line.lineStatuses.get(0)
            lineStatus.statusSeverityDescription != "Good Service"
        })

        val digestedLines = disruptedLines.map({ line ->
            val status = line.lineStatuses[0].statusSeverityDescription
            val lineName = line.name

            DisruptedLine(
                    status,
                    lineName,
                    getStartTimestamp(lineName),
                    getEndTimestamp(status, lineName),
                    getEarliestEndTimestamp(status, lineName),
                    getLatestEndTimestamp(status, lineName))

        })

        val digest = Digest(digestedLines)

        return moshiDigestAdapter().toJson(digest)
    }

    private fun moshiTflLinesAdapter(): JsonAdapter<List<TflLine>> {
        return moshi().adapter<List<TflLine>>(newParameterizedType(List::class.java, TflLine::class.java))
    }

    private fun moshiDigestAdapter(): JsonAdapter<Digest> {
        return moshi().adapter(Digest::class.java)
    }

    private fun moshi(): Moshi {
        return Moshi.Builder().build()
    }

    private fun getTimestampWithMultiplier(status: String,
                                           multiplier: Double): Long {

        val minutes = TflDigestor.Companion.statusToMinutes(status)
        val estimatedDelayInMinutes = (minutes * multiplier).toInt()
        return currentTime.plusMinutes(estimatedDelayInMinutes.toLong()).toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    private fun getStartTimestamp(lineName: String): Long {
        val startTime = previousDigest.flatMap { d -> d.getStartTimestampFromDisruptedLine(lineName) }

        return startTime.orElse(
                currentTime.toInstant(ZoneOffset.UTC).toEpochMilli())
    }

    private fun getEndTimestamp(status: String, lineName: String): Long {
        val endTime = previousDigest.flatMap { d -> d.getEndTimestampFromDisruptedLine(lineName) }

        return endTime.orElse(
                getTimestampWithMultiplier(status, 1.0))
    }

    private fun getEarliestEndTimestamp(status: String, lineName: String): Long {
        val earliestEndTime = previousDigest.flatMap { d -> d.getEarliestEndTimestampFromDisruptedLine(lineName) }

        return earliestEndTime.orElse(
                getTimestampWithMultiplier(status, TflDigestor.Companion.EARLIEST_END_TIME_MULTIPLIER))
    }

    private fun getLatestEndTimestamp(status: String, lineName: String): Long {
        val latestEndTime = previousDigest.flatMap { d -> d.getLatestEndTimestampFromDisruptedLine(lineName) }

        return latestEndTime.orElse(
                getTimestampWithMultiplier(status, TflDigestor.Companion.LATEST_END_TIME_MULTIPLIER))
    }

    companion object {

        val EARLIEST_END_TIME_MULTIPLIER = 2.0 / 3.0
        val LATEST_END_TIME_MULTIPLIER = 4.0 / 3.0

        private fun statusToMinutes(status: String): Int {
            var minutes = 0

            when (status) {
                "Minor Delays" -> minutes = 30
                "Severe Delays" -> minutes = 60
                "Part Suspended" -> minutes = 120
                "Part Closure" -> minutes = 60 * 24
                "No Service" -> minutes = 60 * 24
            }
            return minutes
        }
    }

}
