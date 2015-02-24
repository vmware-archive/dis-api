package io.pivotal.dis.ingest.service;

import io.pivotal.dis.ingest.config.ApplicationConfig;
import io.pivotal.dis.ingest.service.tfl.UrlProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class})
public class UrlProviderImplTest {

    @Value("${tfl.appId}")
    private String appId;

    @Value("${tfl.appKey}")
    private String appKey;

    @Autowired
    private UrlProvider urlProvider;

    @Test
    public void providesCorrectUrl() throws Exception {
        assertThat(urlProvider.getUrl(), equalTo(new URL("http://api.tfl.gov.uk/Line/Mode/%7Bmodes%7D/Status?modes=tube&detail=False&app_id=" + appId + "&app_key=" + appKey)));
    }
}