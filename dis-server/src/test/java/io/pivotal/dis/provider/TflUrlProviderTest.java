package io.pivotal.dis.provider;

import io.pivotal.dis.config.ApplicationConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfiguration.class})
public class TflUrlProviderTest {

    @Value("${tfl.appId}")
    private String appId;

    @Value("${tfl.appKey}")
    private String appKey;

    @Autowired
    private TflUrlProvider tflUrlProvider;

    @Test
    public void defaultsToCurrentLineStatusEndpoint() throws MalformedURLException {
        assertThat(tflUrlProvider.get(), equalTo(new URL("http://api.tfl.gov.uk/Line/Mode/%7Bmodes%7D/Status?modes=tube&detail=False&app_id=" + appId + "&app_key=" + appKey)));
    }
}