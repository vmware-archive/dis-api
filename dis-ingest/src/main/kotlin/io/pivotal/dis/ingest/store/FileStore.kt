package io.pivotal.dis.ingest.store

interface FileStore {
    fun save(name: String, input: String)
}
