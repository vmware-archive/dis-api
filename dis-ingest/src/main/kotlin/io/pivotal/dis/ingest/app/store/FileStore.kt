package io.pivotal.dis.ingest.app.store

interface FileStore {
    fun read(name: String): String?
    fun save(name: String, input: String)
}
