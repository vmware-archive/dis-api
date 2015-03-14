package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
import io.pivotal.dis.ingest.service.time.TimeProvider;
import org.apache.commons.io.IOUtils;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FileStoreImpl implements FileStore {

    private final AmazonS3 amazonS3;
    private final TimeProvider timeProvider;
    private final String bucketName;

    public FileStoreImpl(AmazonS3 amazonS3, TimeProvider timeProvider, String bucketName) {
        this.amazonS3 = amazonS3;
        this.timeProvider = timeProvider;
        this.bucketName = bucketName;
    }

    @Override
    public void save(String input) {
        amazonS3.putObject(bucketName, "tfl_api_line_mode_status_tube_" + timeProvider.currentTime().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")) + ".json", IOUtils.toInputStream(input), null);
    }
}
