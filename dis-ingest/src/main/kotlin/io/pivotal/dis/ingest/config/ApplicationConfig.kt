package io.pivotal.dis.ingest.config

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.Permission
import io.pivotal.dis.ingest.store.AmazonS3FileStore
import io.pivotal.dis.ingest.store.FileStore
import io.pivotal.labs.cfenv.CloudFoundryEnvironment
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException
import io.pivotal.labs.cfenv.Environment

import java.io.IOException
import java.net.URISyntaxException
import java.net.URL

class ApplicationConfig @Throws(IOException::class, CloudFoundryEnvironmentException::class, URISyntaxException::class)
constructor() {

    private val tflUrl: URL
    private val rawBucketName: String
    private val digestedBucketName: String

    init {
        val cloudFoundryEnvironment = CloudFoundryEnvironment(Environment { System.getenv(it) })

        tflUrl = cloudFoundryEnvironment.getService("tfl").uri.toURL()
        rawBucketName = System.getenv("S3_BUCKET_NAME_RAW")
        digestedBucketName = System.getenv("S3_BUCKET_NAME_DIGESTED")
    }

    fun tflUrl(): URL {
        return tflUrl
    }

    fun digestedFileStore(): FileStore {
        val publicReadableAcl = AccessControlList()
        publicReadableAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read)
        return AmazonS3FileStore(amazonS3(), digestedBucketName, publicReadableAcl)
    }

    fun rawFileStore(): FileStore {
        return AmazonS3FileStore(amazonS3(), rawBucketName, AccessControlList())
    }

    private fun amazonS3(): AmazonS3 {
        return AmazonS3Client(EnvironmentVariableCredentialsProvider())
    }
}
