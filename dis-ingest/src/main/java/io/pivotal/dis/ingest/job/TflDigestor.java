package io.pivotal.dis.ingest.job;

import com.amazonaws.util.json.JSONException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import io.pivotal.dis.ingest.domain.Digest;
import io.pivotal.dis.ingest.domain.DisruptedLine;
import io.pivotal.dis.ingest.domain.tfl.TflLine;
import io.pivotal.dis.ingest.domain.tfl.LineStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.squareup.moshi.Types.newParameterizedType;
import static java.util.stream.Collectors.toList;

public class TflDigestor {

    public static final double EARLIEST_END_TIME_MULTIPLIER = 2d / 3d;
    public static final double LATEST_END_TIME_MULTIPLIER = 4d / 3d;

    private final List<TflLine> tflLines;
    private final LocalDateTime currentTime;
    private final Optional<Digest> previousDigest;

    public TflDigestor(String tflData,
                       LocalDateTime currentTime,
                       Optional<String> previousDigest) {

        try {
            this.tflLines = moshiTflLinesAdapter().fromJson(tflData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.previousDigest = parseDigest(previousDigest);
        this.currentTime = currentTime;
    }

    private Optional<Digest> parseDigest(Optional<String> digest) {
        return digest.map(string -> {
            try {
                return moshiDigestAdapter().fromJson(string);
            } catch (IOException e) {
                return null;
            }
        });
    }

    public String digest() throws JSONException, IOException {
        Stream<TflLine> disruptedLines = tflLines.stream().filter(line -> {
            LineStatus lineStatus = line.getLineStatuses().get(0);
            return !lineStatus.getStatusSeverityDescription().equals("Good Service");
        });

        Stream<DisruptedLine> digestedLines = disruptedLines.map(line -> {
                    String status = line.getLineStatuses().get(0).getStatusSeverityDescription();
                    String lineName = line.getName();

                    return new DisruptedLine(
                            status,
                            lineName,
                            getStartTimestamp(lineName),
                            getEndTimestamp(status, lineName),
                            getEarliestEndTimestamp(status, lineName),
                            getLatestEndTimestamp(status, lineName));

                });

        Digest digest = new Digest(digestedLines.collect(toList()));

        return moshiDigestAdapter().toJson(digest);
    }

    private JsonAdapter<List<TflLine>> moshiTflLinesAdapter() {
        return moshi().adapter(newParameterizedType(List.class, TflLine.class));
    }

    private JsonAdapter<Digest> moshiDigestAdapter() {
        return moshi().adapter(Digest.class);
    }

    private Moshi moshi() {
        return new Moshi.Builder().build();
    }

    private Long getTimestampWithMultiplier(String status,
                                         double multiplier) {

        int minutes = statusToMinutes(status);
        int estimatedDelayInMinutes = (int) (minutes * multiplier);
        return currentTime.plusMinutes(estimatedDelayInMinutes).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private Long getStartTimestamp(String lineName) {
        Optional<Long> startTime =
                previousDigest.flatMap(
                        d -> d.getStartTimestampFromDisruptedLine(lineName));

        return startTime.orElse(
                currentTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    private Long getEndTimestamp(String status, String lineName) {
        Optional<Long> endTime =
                previousDigest.flatMap(
                        d -> d.getEndTimestampFromDisruptedLine(lineName));

        return endTime.orElse(
                getTimestampWithMultiplier(status, 1));
    }

    private Long getEarliestEndTimestamp(String status, String lineName) {
        Optional<Long> earliestEndTime =
                previousDigest.flatMap(
                        d -> d.getEarliestEndTimestampFromDisruptedLine(lineName));

        return earliestEndTime.orElse(
                getTimestampWithMultiplier(status, EARLIEST_END_TIME_MULTIPLIER));
    }

    private Long getLatestEndTimestamp(String status, String lineName) {
        Optional<Long> latestEndTime =
                previousDigest.flatMap(
                        d -> d.getLatestEndTimestampFromDisruptedLine(lineName));

        return latestEndTime.orElse(
                getTimestampWithMultiplier(status, LATEST_END_TIME_MULTIPLIER));
    }

    private static int statusToMinutes(String status) {
        int minutes = 0;

        switch (status) {
            case "Minor Delays":
                minutes = 30;
                break;
            case "Severe Delays":
                minutes = 60;
                break;
            case "Part Suspended":
                minutes = 120;
                break;
            case "Part Closure":
                minutes = 60 * 24;
                break;
            case "No Service":
                minutes = 60 * 24;
                break;
        }
        return minutes;
    }

}
