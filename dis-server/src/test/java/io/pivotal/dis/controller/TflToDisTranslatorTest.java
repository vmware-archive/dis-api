package io.pivotal.dis.controller;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TflToDisTranslatorTest {

    @Test
    public void getLineDisruptions_returnsDisruptedLines() throws Exception {
        JSONArray tflLineStatus = new JSONArray(loadFixture("line_mode_tube_status"));

        Map<String, List<Map<String, String>>> disLineStatus = TflToDisTranslator.convertJsonArrayToList(tflLineStatus);

        assertThat(disLineStatus, is(aMapWhich(hasEntry(equalTo("disruptions"), isAListContaining(
                aMapWhich(
                        hasEntry("line", "Bakerloo"),
                        hasEntry("status", "Runaway Train")
                ))))));
    }

    @SafeVarargs
    private final <K, V> Matcher<Map<? extends K, ? extends V>> aMapWhich(Matcher<Map<? extends K, ? extends V>>... matchers) {
        return allOf(matchers);
    }

    private <E> Matcher<Iterable<? extends E>> isAListContaining(Matcher<E> itemMatcher) {
        return contains(itemMatcher);
    }

    private String loadFixture(final String name) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name + ".json"));
    }

}
