package io.pivotal.dis.ingest.store

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.Permission
import com.amazonaws.services.s3.model.PutObjectRequest
import org.apache.commons.io.IOUtils

class AmazonS3FileStore(private val amazonS3: AmazonS3,
                        private val bucketName: String,
                        private val disruptionsAcl: AccessControlList) : FileStore {

    override fun save(name: String, input: String) {
        val putObjectRequest = PutObjectRequest(
                bucketName,
                name,
                IOUtils.toInputStream(input),
                null).withAccessControlList(disruptionsAcl)

        amazonS3.putObject(putObjectRequest)
    }
}
