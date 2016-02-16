package io.pivotal.dis.ingest.domain.tfl;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LineColorTest {

    @Test
    public void returnsBackgroundColorForLine() {
        String bakerlooBackgroundColor = LineColor.getBackgroundColorForLine("Bakerloo");
        assertThat(bakerlooBackgroundColor, equalTo("#AE6118"));
    }

    @Test
    public void returnsDefaultBackgroundColorForUnknownLine() {
        String fakeLineBackgroundColor = LineColor.getBackgroundColorForLine("fakerfakefake");
        assertThat(fakeLineBackgroundColor, equalTo("#FFFFFF"));
    }

    @Test
    public void returnsForegroundColorForLine() {
        String bakerlooForegroundColor = LineColor.getForegroundColorForLine("Bakerloo");
        assertThat(bakerlooForegroundColor, equalTo("#FFFFFF"));
    }

    @Test
    public void returnsDefaultForegroundColorForUnknownLine() {
        String fakeLineForegroundColor = LineColor.getForegroundColorForLine("fakerfakefake");
        assertThat(fakeLineForegroundColor, equalTo("#000000"));
    }
}
