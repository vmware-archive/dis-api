package io.pivotal.dis.ingest.service.time;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime currentTime();
}
