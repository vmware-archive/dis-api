package io.pivotal.dis.provider;

import java.time.LocalDateTime;

public class MockTimeProvider implements TimeProvider {

    private LocalDateTime time;

    @Override
    public LocalDateTime currentTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void addSeconds(int seconds) {
        this.time = this.time.plusSeconds(seconds);
    }
}
