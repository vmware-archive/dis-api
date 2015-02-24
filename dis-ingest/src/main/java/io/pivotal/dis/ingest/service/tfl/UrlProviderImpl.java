package io.pivotal.dis.ingest.service.tfl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class UrlProviderImpl implements UrlProvider {
    private URL url;

    @Autowired
    public UrlProviderImpl(@Value("${tfl.url}") URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
