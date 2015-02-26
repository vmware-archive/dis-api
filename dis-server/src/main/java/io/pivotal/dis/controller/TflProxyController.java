package io.pivotal.dis.controller;

import io.pivotal.dis.service.DisruptedLinesService;
import io.pivotal.dis.service.FakeDisruptedLinesService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TflProxyController {

    @Autowired
    private DisruptedLinesService disruptedLinesService;

    @Autowired
    private FakeDisruptedLinesService fakeDisruptedLinesService;

    @RequestMapping("/lines/disruptions")
    public Map<String, List<Map<String, String>>> lineDisruptions() throws IOException {
        return convertJsonArrayToList(disruptedLinesService.getDisruptedLinesJson());
    }

    @RequestMapping("/test/lines/disruptions")
    public Map<String, List<Map<String, String>>> fakeLineDisruptions() throws IOException {
        return convertJsonArrayToList(fakeDisruptedLinesService.getDisruptedLinesJson());
    }

    @RequestMapping(value = "/test/lines/disruptions", method = RequestMethod.POST)
    public ModelAndView setFakeLineDisruptions(@RequestParam JSONArray jsonArray) throws IOException {
        this.fakeDisruptedLinesService.setDisruptedLinesJson(jsonArray);
        return new ModelAndView("redirect:/test");
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView fakeLineDisruptionsIndex() throws IOException {
        return new ModelAndView("fake_disruptions");
    }

    private Map<String, List<Map<String, String>>> convertJsonArrayToList(JSONArray lines) {
        List<Map<String, String>> disruptedLines = new ArrayList<>();

        for (int i = 0; i < lines.length(); i++) {
            JSONObject line = lines.getJSONObject(i);
            JSONObject lineStatus = line.getJSONArray("lineStatuses").getJSONObject(0);

            String statusSeverityDescription = lineStatus.getString("statusSeverityDescription");
            if (!statusSeverityDescription.equals("Good Service")) {
                HashMap<String, String> disruptedLine = new HashMap<>();
                disruptedLine.put("line", line.getString("name"));
                disruptedLine.put("status", statusSeverityDescription);
                disruptedLines.add(disruptedLine);
            }
        }

        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("disruptions", disruptedLines);
        return response;
    }

}
