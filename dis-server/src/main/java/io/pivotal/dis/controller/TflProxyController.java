package io.pivotal.dis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TflProxyController {

    @RequestMapping("/lines/disruptions")
    public Map<String, List<Map<String, String>>> lineDisruptions() {
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        result.put("disruptions", new ArrayList<>());
        result.get("disruptions").add(new HashMap<>());
        result.get("disruptions").get(0).put("line", "northern");
        return result;
    }
}
