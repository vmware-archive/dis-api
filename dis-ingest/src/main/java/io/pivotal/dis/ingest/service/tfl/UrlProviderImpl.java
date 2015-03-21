package io.pivotal.dis.ingest.service.tfl;

import java.net.URL;

public class UrlProviderImpl implements UrlProvider {
    private URL url;

    public UrlProviderImpl(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
