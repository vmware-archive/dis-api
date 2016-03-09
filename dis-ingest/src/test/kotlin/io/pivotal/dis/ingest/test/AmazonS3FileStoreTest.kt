package io.pivotal.dis.ingest.test

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import io.pivotal.dis.ingest.app.store.AmazonS3FileStore
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Assert.*;
import org.hamcrest.Matchers.*;

class AmazonS3FileStoreTest {

    @Test
    fun read_returnsStringOfRequestedFilesContents() {
        val amazonS3 = mock(AmazonS3::class.java)
        val s3Object = S3Object()
        s3Object.objectContent = S3ObjectInputStream(IOUtils.toInputStream("I'm the content!"), null)

        `when`(amazonS3.getObject("bucketName", "fileName")).thenReturn(s3Object)

        val amazonS3FileStore = AmazonS3FileStore(amazonS3, "bucketName", AccessControlList())
        assertThat(amazonS3FileStore.read("fileName"), equalTo("I'm the content!"))
    }

    @Test
    fun read_whenRequestedFileDoesNotExist_returnsNull() {
        val amazonS3 = mock(AmazonS3::class.java)

        `when`(amazonS3.getObject("bucketName", "fileName")).thenReturn(null)

        val amazonS3FileStore = AmazonS3FileStore(amazonS3, "bucketName", AccessControlList())
        assertThat(amazonS3FileStore.read("fileName"), equalTo(null as String?))
    }
}
