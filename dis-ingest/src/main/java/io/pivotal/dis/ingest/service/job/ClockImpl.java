package io.pivotal.dis.ingest.service.job;

import java.time.LocalDateTime;

import io.pivotal.dis.ingest.service.job.Clock;

public class ClockImpl implements Clock {
    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
