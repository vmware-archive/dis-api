package io.pivotal.dis.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class TflUrlProvider {

    private URL url;

    @Autowired
    public TflUrlProvider(@Value("${tfl.appId}") String appId, @Value("${tfl.appKey}") String appKey) throws MalformedURLException {
        url = new URL("http://api.tfl.gov.uk/Line/Mode/%7Bmodes%7D/Status?modes=tube&detail=False&app_id=" + appId + "&app_key=" + appKey);
    }

    public void set(URL url) {
        this.url = url;
    }

    public URL get() {
        return url;
    }
}
