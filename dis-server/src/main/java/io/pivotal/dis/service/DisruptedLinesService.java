package io.pivotal.dis.service;

import io.pivotal.dis.provider.TflUrlProvider;
import io.pivotal.dis.provider.TimeProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class DisruptedLinesService {

    private final TimeProvider timeProvider;

    private final TflUrlProvider tflUrlProvider;

    private JSONArray tflData;

    private LocalDateTime lastUpdateTime;

    @Autowired
    public DisruptedLinesService(TimeProvider timeProvider, TflUrlProvider tflUrlProvider) {
        this.timeProvider = timeProvider;
        this.tflUrlProvider = tflUrlProvider;
    }

    public JSONArray getDisruptedLinesJson() throws IOException {
        if (isCacheUpToDate())
            return tflData;
        InputStream inputStream = tflUrlProvider.get().openConnection().getInputStream();
        String jsonString = IOUtils.toString(inputStream);
        tflData = new JSONArray(jsonString);
        lastUpdateTime = timeProvider.currentTime();
        return tflData;
    }

    private boolean isCacheUpToDate() {
        return tflData != null && wasLastUpdateLessThanOneMinuteAgo();
    }

    private boolean wasLastUpdateLessThanOneMinuteAgo() {
        return lastUpdateTime.isAfter(timeProvider.currentTime().minusMinutes(1));
    }
}
