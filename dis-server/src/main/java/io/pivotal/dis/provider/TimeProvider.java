package io.pivotal.dis.provider;

import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime currentTime();
}
