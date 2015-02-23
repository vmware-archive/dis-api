package io.pivotal.dis.provider;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeProviderImpl implements TimeProvider {
    @Override
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }
}
