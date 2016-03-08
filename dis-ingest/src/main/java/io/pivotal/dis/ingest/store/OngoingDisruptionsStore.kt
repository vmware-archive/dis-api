package io.pivotal.dis.ingest.store

class OngoingDisruptionsStore {
    private var _previousDisruptionDigest: String? = null

    var previousDisruptionDigest: String?
        get() = _previousDisruptionDigest
        set(value) {
            _previousDisruptionDigest = value
        }
}
