package io.pivotal.dis.ingest.system;

import java.time.LocalDateTime;

public interface Clock {
    LocalDateTime getCurrentTime();
}
