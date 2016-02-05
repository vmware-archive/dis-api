package io.pivotal.dis.ingest.job;

import com.amazonaws.util.json.JSONException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import io.pivotal.dis.ingest.domain.Digest;
import io.pivotal.dis.ingest.domain.DisruptedLine;
import io.pivotal.dis.ingest.domain.tfl.Line;
import io.pivotal.dis.ingest.domain.tfl.LineStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.squareup.moshi.Types.newParameterizedType;
import static java.util.stream.Collectors.toList;

public class TflDigestor {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final double EARLIEST_END_TIME_MULTIPLIER = 2d / 3d;
    public static final double LATEST_END_TIME_MULTIPLIER = 4d / 3d;

    private final List<Line> lines;
    private final LocalDateTime currentTime;
    private final Optional<Digest> previousDigest;

    public TflDigestor(String tflData,
                       LocalDateTime currentTime,
                       Optional<String> previousDigest) {

        try {
            this.lines = moshiTflLinesAdapter().fromJson(tflData);
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
        Stream<Line> disruptedLines = lines.stream().filter(line -> {
            LineStatus lineStatus = line.getLineStatuses().get(0);
            return !lineStatus.getStatusSeverityDescription().equals("Good Service");
        });

        Stream<DisruptedLine> digestedLines = disruptedLines.map(line -> {
            String status = line.getLineStatuses().get(0).getStatusSeverityDescription();
            String lineName = line.getName();
            String startTime = getStartTime(lineName);
            String endTime = getEndTime(status, lineName);
            String earliestEndTime = getEarliestEndTime(status, lineName);
            String latestEndTime = getLatestEndTime(status, lineName);

            DisruptedLine disruptedLine =
                    new DisruptedLine(
                            status,
                            lineName,
                            startTime,
                            endTime,
                            earliestEndTime,
                            latestEndTime);

            return disruptedLine;

        });

        Digest digest = new Digest(digestedLines.collect(toList()));

        return moshiDigestAdapter().toJson(digest);
    }

    private String getStartTime(String lineName) {
        Optional<String> startTime =
                previousDigest.flatMap(
                        d -> d.getStartTimeFromDisruptedLine(lineName));

        return startTime.orElse(
                currentTime.format(TIME_FORMAT));
    }

    private String getEndTime(String status, String lineName) {
        Optional<String> endTime =
                previousDigest.flatMap(
                        d -> d.getEndTimeFromDisruptedLine(lineName));

        return endTime.orElse(getTimeWithMultiplier(status, 1));
    }

    private String getEarliestEndTime(String status, String lineName) {
        Optional<String> earliestEndTime =
                previousDigest.flatMap(
                        d -> d.getEarliestEndTimeFromDisruptedLine(lineName));

        return earliestEndTime.orElse(getTimeWithMultiplier(status, EARLIEST_END_TIME_MULTIPLIER));
    }

    private String getLatestEndTime(String status, String lineName) {
        Optional<String> latestEndTime =
                previousDigest.flatMap(
                        d -> d.getLatestEndTimeFromDisruptedLine(lineName));

        return latestEndTime.orElse(getTimeWithMultiplier(status, LATEST_END_TIME_MULTIPLIER));
    }

    private JsonAdapter<List<Line>> moshiTflLinesAdapter() {
        return moshi().adapter(newParameterizedType(List.class, Line.class));
    }

    private JsonAdapter<Digest> moshiDigestAdapter() {
        return moshi().adapter(Digest.class);
    }

    private Moshi moshi() {
        return new Moshi.Builder().build();
    }

    private String getTimeWithMultiplier(String status,
                                         double multiplier) {

        int minutes = statusToMinutes(status);
        int estimatedDelayInMinutes = (int) (minutes * multiplier);
        return currentTime.plusMinutes(estimatedDelayInMinutes).format(TIME_FORMAT);
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
