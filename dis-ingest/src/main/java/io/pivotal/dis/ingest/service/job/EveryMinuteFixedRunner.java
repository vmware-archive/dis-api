package io.pivotal.dis.ingest.service.job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EveryMinuteFixedRunner {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public void addRunnable(Runnable runnable){
        executorService.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
    }
}
