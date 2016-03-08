package io.pivotal.dis.ingest.domain.tfl

import java.util.HashMap

class LineColor(val foregroundColor: String, val backgroundColor: String) {
    companion object {

        private val DEFAULT_FOREGROUND = "#000000"
        private val DEFAULT_BACKGROUND = "#FFFFFF"


        private val lineDetails = object : HashMap<String, LineColor>() {
            init {
                put("Bakerloo", LineColor("#FFFFFF", "#AE6118"))
                put("Central", LineColor("#FFFFFF", "#E41F1F"))
                put("Circle", LineColor("#113892", "#F8D42D"))
                put("District", LineColor("#FFFFFF", "#007229"))
                put("Hammersmith & City", LineColor("#113892", "#E899A8"))
                put("Jubilee", LineColor("#FFFFFF", "#686E72"))
                put("Metropolitan", LineColor("#FFFFFF", "#893267"))
                put("Northern", LineColor("#FFFFFF", "#000000"))
                put("Piccadilly", LineColor("#FFFFFF", "#0450A1"))
                put("Victoria", LineColor("#FFFFFF", "#009FE0"))
                put("Waterloo & City", LineColor("#113892", "#70C3CE"))
            }
        }

        fun getForegroundColorForLine(line: String): String {
            val color = LineColor.getColorForProperty(line)
            return if (color != null) color.foregroundColor else DEFAULT_FOREGROUND
        }

        fun getBackgroundColorForLine(line: String): String {
            val color = LineColor.getColorForProperty(line)
            return if (color != null) color.backgroundColor else DEFAULT_BACKGROUND
        }

        private fun getColorForProperty(line: String): LineColor? {
            return if (lineDetails.containsKey(line)) lineDetails[line] else null
        }
    }
}
