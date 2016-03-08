package io.pivotal.dis.ingest.system;

import java.time.LocalDateTime;

public class ClockImpl implements Clock {
    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
