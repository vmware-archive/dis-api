package io.pivotal.dis.ingest.app.store

interface FileStore {
    fun save(name: String, input: String)
}
