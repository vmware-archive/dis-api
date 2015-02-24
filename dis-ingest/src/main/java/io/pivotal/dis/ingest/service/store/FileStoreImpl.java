package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
import io.pivotal.dis.ingest.service.time.TimeProvider;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class FileStoreImpl implements FileStore {

    private final AmazonS3 amazonS3;
    private final TimeProvider timeProvider;
    private final String bucketName;

    @Autowired
    public FileStoreImpl(AmazonS3 amazonS3, TimeProvider timeProvider, @Value("${s3.bucketName}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.timeProvider = timeProvider;
        this.bucketName = bucketName;
    }

    @Override
    public void save(String input) {
        amazonS3.putObject(bucketName, "tfl_api_line_mode_status_tube_" + timeProvider.currentTime().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")) + ".json", IOUtils.toInputStream(input), null);
    }
}
