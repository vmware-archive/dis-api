package io.pivotal.dis.service;

import io.pivotal.dis.provider.TimeProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;

public class DisruptedLinesService {

    private final TimeProvider timeProvider;
    private final URL tflUrl;
    private JSONArray tflData;
    private LocalDateTime lastUpdateTime;

    public DisruptedLinesService(TimeProvider timeProvider, URL tflUrl) {
        this.timeProvider = timeProvider;
        this.tflUrl = tflUrl;
    }

    public JSONArray getDisruptedLinesJson() throws IOException {
        if (isCacheUpToDate()) {
            return tflData;
        }
        try (InputStream inputStream = tflUrl.openConnection().getInputStream()) {
            String jsonString = IOUtils.toString(inputStream);
            tflData = new JSONArray(jsonString);
            lastUpdateTime = timeProvider.currentTime();
            return tflData;
        }
    }

    private boolean isCacheUpToDate() {
        return tflData != null && wasLastUpdateLessThanOneMinuteAgo();
    }

    private boolean wasLastUpdateLessThanOneMinuteAgo() {
        return lastUpdateTime.isAfter(timeProvider.currentTime().minusMinutes(1));
    }

}
