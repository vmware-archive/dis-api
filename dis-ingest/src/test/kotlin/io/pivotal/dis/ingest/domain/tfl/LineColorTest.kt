package io.pivotal.dis.ingest.domain.tfl

import org.junit.Test

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat

class LineColorTest {

    @Test
    fun returnsBackgroundColorForLine() {
        val bakerlooBackgroundColor = LineColor.getBackgroundColorForLine("Bakerloo")
        assertThat(bakerlooBackgroundColor, equalTo("#AE6118"))
    }

    @Test
    fun returnsDefaultBackgroundColorForUnknownLine() {
        val fakeLineBackgroundColor = LineColor.getBackgroundColorForLine("fakerfakefake")
        assertThat(fakeLineBackgroundColor, equalTo("#FFFFFF"))
    }

    @Test
    fun returnsForegroundColorForLine() {
        val bakerlooForegroundColor = LineColor.getForegroundColorForLine("Bakerloo")
        assertThat(bakerlooForegroundColor, equalTo("#FFFFFF"))
    }

    @Test
    fun returnsDefaultForegroundColorForUnknownLine() {
        val fakeLineForegroundColor = LineColor.getForegroundColorForLine("fakerfakefake")
        assertThat(fakeLineForegroundColor, equalTo("#000000"))
    }
}
