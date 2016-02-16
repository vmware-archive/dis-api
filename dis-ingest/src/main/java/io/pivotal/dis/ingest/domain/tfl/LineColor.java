package io.pivotal.dis.ingest.domain.tfl;

import java.util.HashMap;

public class LineColor {

    private static final String DEFAULT_FOREGROUND = "#000000";
    private static final String DEFAULT_BACKGROUND = "#FFFFFF";
    private final String foregroundColor;
    private final String backgroundColor;


    private static HashMap<String, LineColor> lineDetails = new HashMap<String, LineColor>(){{
        put("Bakerloo", new LineColor("#FFFFFF", "#AE6118"));
        put("Central", new LineColor("#FFFFFF", "#E41F1F"));
        put("Circle", new LineColor("#113892", "#F8D42D"));
        put("District", new LineColor("#FFFFFF", "#007229"));
        put("Hammersmith & City", new LineColor("#113892", "#E899A8"));
        put("Jubilee", new LineColor("#FFFFFF", "#686E72"));
        put("Metropolitan", new LineColor("#FFFFFF", "#893267"));
        put("Northern", new LineColor("#FFFFFF", "#000000"));
        put("Piccadilly", new LineColor("#FFFFFF", "#0450A1"));
        put("Victoria", new LineColor("#FFFFFF", "#009FE0"));
        put("Waterloo & City", new LineColor("#113892", "#70C3CE"));
    }};

    public LineColor(String foregroundColor, String backgroundColor) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public static String getForegroundColorForLine(String line) {
        LineColor color = LineColor.getColorForProperty(line);
        return color != null ? color.foregroundColor : DEFAULT_FOREGROUND;
    }

    public static String getBackgroundColorForLine(String line) {
        LineColor color = LineColor.getColorForProperty(line);
        return color != null ? color.backgroundColor : DEFAULT_BACKGROUND;
    }

    private static LineColor getColorForProperty(String line) {
        return lineDetails.containsKey(line) ? lineDetails.get(line) : null;
    }
}
