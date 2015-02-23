package io.pivotal.dis.service;

import io.pivotal.dis.provider.TflUrlProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DisruptedLinesService {

    @Autowired
    private TflUrlProvider tflUrlProvider;

    public JSONArray getDisruptedLinesJson() throws IOException {
        InputStream inputStream = tflUrlProvider.get().openConnection().getInputStream();
        String jsonString = IOUtils.toString(inputStream);
        return new JSONArray(jsonString);
    }
}
