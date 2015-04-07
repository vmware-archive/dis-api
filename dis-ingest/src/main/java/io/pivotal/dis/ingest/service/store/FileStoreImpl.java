package io.pivotal.dis.ingest.service.store;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.io.IOUtils;

public class FileStoreImpl implements FileStore {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public FileStoreImpl(AmazonS3 amazonS3, String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @Override
    public void save(String name, String input) {
        amazonS3.putObject(bucketName, name, IOUtils.toInputStream(input), null);
    }

}
