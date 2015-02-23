package io.pivotal.dis.provider;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Primary
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
