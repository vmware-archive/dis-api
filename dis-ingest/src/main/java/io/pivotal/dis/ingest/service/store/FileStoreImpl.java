package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.io.IOUtils;

import java.util.function.Supplier;

public class FileStoreImpl implements FileStore {

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final Supplier<String> fileNamer;

    public FileStoreImpl(AmazonS3 amazonS3, String bucketName, Supplier<String> fileNamer) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.fileNamer = fileNamer;
    }

    @Override
    public void save(String input) {
        amazonS3.putObject(bucketName, fileNamer.get(), IOUtils.toInputStream(input), null);
    }

}
