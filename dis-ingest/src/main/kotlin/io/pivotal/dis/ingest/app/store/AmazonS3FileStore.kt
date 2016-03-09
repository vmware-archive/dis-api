package io.pivotal.dis.ingest.app.store

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import org.apache.commons.io.IOUtils

class AmazonS3FileStore(private val amazonS3: AmazonS3,
                        private val bucketName: String,
                        private val disruptionsAcl: AccessControlList) : FileStore {
    override fun read(name: String): String? {
        val s3Object = amazonS3.getObject(bucketName, name)

        if (s3Object != null) {
            return IOUtils.toString(s3Object.objectContent)
        } else {
            return null;
        }


    }

    override fun save(name: String, input: String) {
        val putObjectRequest = PutObjectRequest(
                bucketName,
                name,
                IOUtils.toInputStream(input),
                null).withAccessControlList(disruptionsAcl)

        amazonS3.putObject(putObjectRequest)
    }
}
