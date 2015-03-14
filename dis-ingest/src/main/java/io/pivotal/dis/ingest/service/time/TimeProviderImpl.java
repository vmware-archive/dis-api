package io.pivotal.dis.ingest.service.time;

import java.time.LocalDateTime;

public class TimeProviderImpl implements TimeProvider {
    @Override
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }
}
