package io.pivotal.dis.controller;

import io.pivotal.dis.service.DisruptedLinesService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TflProxyController {

    private final DisruptedLinesService disruptedLinesService;

    public TflProxyController(DisruptedLinesService disruptedLinesService) {
        this.disruptedLinesService = disruptedLinesService;
    }

    public Map<String, List<Map<String, String>>> lineDisruptions() throws IOException {
        return TflToDisTranslator.convertJsonArrayToList(disruptedLinesService.getDisruptedLinesJson());
    }

}
