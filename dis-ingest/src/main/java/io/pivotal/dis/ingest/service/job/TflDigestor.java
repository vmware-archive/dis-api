package io.pivotal.dis.ingest.service.job;

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
import java.util.function.Function;
import java.util.stream.Stream;

import static com.squareup.moshi.Types.newParameterizedType;
import static java.util.stream.Collectors.toList;

public class TflDigestor {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final String tflData;
    private final Optional<Digest> previousDigest;
    private final LocalDateTime currentTime;

    public TflDigestor(String tflData,
                       LocalDateTime currentTime,
                       Optional<String> previousDigest) {

        this.tflData = tflData;
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
        List<Line> lines = moshiTflLinesAdapter().fromJson(tflData);

        Stream<Line> disruptedLines = lines.stream().filter(line -> {
            LineStatus lineStatus = line.getLineStatuses().get(0);
            return !lineStatus.getStatusSeverityDescription().equals("Good Service");
        });

        Stream<DisruptedLine> digestedLines = disruptedLines.map(line -> {
            String status = line.getLineStatuses().get(0).getStatusSeverityDescription();
            String lineName = line.getName();
            String startTime = getStartTime(lineName, currentTime, previousDigest);
            String endTime = getEndTime(lineName, status, currentTime, previousDigest);
            DisruptedLine disruptedLine = new DisruptedLine(status, lineName, startTime, endTime);
            return disruptedLine;
        });

        Digest digest = new Digest(digestedLines.collect(toList()));

        return moshiDigestAdapter().toJson(digest);
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

    private String getStartTime(String lineName,
                                       LocalDateTime currentTime,
                                       Optional<Digest> previousDigest) {

        Optional<String> startTime =
                getFieldFromDisruptedLine(
                        previousDigest,
                        lineName,
                        DisruptedLine::getStartTime);

        return startTime.orElse(
                currentTime.format(TIME_FORMAT));
    }

    private String getEndTime(String lineName,
                                     String status,
                                     LocalDateTime currentTime,
                                     Optional<Digest> previousDigest) {

        Optional<String> endTime =
                getFieldFromDisruptedLine(
                        previousDigest,
                        lineName,
                        DisruptedLine::getEndTime);

        return endTime.orElseGet(() -> {
            int minutes = statusToMinutes(status);
            return currentTime.plusMinutes(minutes).format(TIME_FORMAT);
        });
    }

    private Optional<String> getFieldFromDisruptedLine(Optional<Digest> digest,
                                              String lineName,
                                              Function<DisruptedLine, String> fieldExtractor) {
        return digest
                .filter(d -> d.isLineDisrupted(lineName))
                .map(d -> fieldExtractor.apply(d.getLine(lineName).get()));
    }

    private int statusToMinutes(String status) {
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
