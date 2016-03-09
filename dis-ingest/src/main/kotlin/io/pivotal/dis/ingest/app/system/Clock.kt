package io.pivotal.dis.ingest.app.system

import java.time.LocalDateTime

interface Clock {
    val currentTime: LocalDateTime
}
