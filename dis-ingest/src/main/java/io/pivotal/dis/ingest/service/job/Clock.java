package io.pivotal.dis.ingest.service.job;

import java.time.LocalDateTime;

public interface Clock {
    LocalDateTime getCurrentTime();
}
