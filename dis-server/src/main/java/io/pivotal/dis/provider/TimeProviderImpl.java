package io.pivotal.dis.provider;

import java.time.LocalDateTime;

public class TimeProviderImpl implements TimeProvider {
    @Override
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }
}
