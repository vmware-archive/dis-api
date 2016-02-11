package io.pivotal.dis.ingest.app;

import io.pivotal.dis.ingest.job.Ingester;
import io.pivotal.dis.ingest.system.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IngestTask {

    private final Clock clock;
    private final Ingester ingester;

    @Autowired
    public IngestTask(Clock clock, Ingester ingester) {
        this.clock = clock;
        this.ingester = ingester;
    }

    @Scheduled(fixedDelay = 60000)
    public void ingest() {
        ingester.ingest(clock);
    }

}




