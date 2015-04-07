package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.io.IOUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class FileStoreImpl implements FileStore {

    private static final DateTimeFormatter FILE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final AmazonS3 amazonS3;
    private final Supplier<LocalDateTime> clock;
    private final String bucketName;

    public FileStoreImpl(AmazonS3 amazonS3, Supplier<LocalDateTime> clock, String bucketName) {
        this.amazonS3 = amazonS3;
        this.clock = clock;
        this.bucketName = bucketName;
    }

    @Override
    public void save(String input) {
        String fileName = String.format("tfl_api_line_mode_status_tube_%s.json", now());
        amazonS3.putObject(bucketName, fileName, IOUtils.toInputStream(input), null);
    }

    private String now() {
        return clock
                .get()
                .atOffset(ZoneOffset.UTC)
                .format(FILE_DATE_TIME_FORMAT);
    }

}
