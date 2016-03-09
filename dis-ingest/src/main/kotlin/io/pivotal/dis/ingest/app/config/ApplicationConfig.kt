package io.pivotal.dis.ingest.app.config

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.Permission
import io.pivotal.dis.ingest.app.store.AmazonS3FileStore
import io.pivotal.dis.ingest.app.store.FileStore
import io.pivotal.labs.cfenv.CloudFoundryEnvironment
import io.pivotal.labs.cfenv.Environment
import java.net.URL

class ApplicationConfig() {

    private val rawBucketName: String
    private val digestedBucketName: String

    val tflUrl: URL

    val digestedFileStore: FileStore
        get() {
            val publicReadableAcl = AccessControlList()
            publicReadableAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read)
            return AmazonS3FileStore(amazonS3, digestedBucketName, publicReadableAcl)
        }

    val rawFileStore: FileStore
        get() {
            return AmazonS3FileStore(amazonS3, rawBucketName, AccessControlList())
        }

    private val amazonS3: AmazonS3
        get() {
            return AmazonS3Client(EnvironmentVariableCredentialsProvider())
        }

    init {
        val cloudFoundryEnvironment = CloudFoundryEnvironment(Environment { System.getenv(it) })

        tflUrl = cloudFoundryEnvironment.getService("tfl").uri.toURL()
        rawBucketName = System.getenv("S3_BUCKET_NAME_RAW")
        digestedBucketName = System.getenv("S3_BUCKET_NAME_DIGESTED")
    }
}
