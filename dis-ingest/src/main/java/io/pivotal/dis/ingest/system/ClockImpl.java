package io.pivotal.dis.ingest.system;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClockImpl implements Clock {

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
