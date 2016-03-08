package io.pivotal.dis.ingest.system

import java.time.LocalDateTime

interface Clock {
    val currentTime: LocalDateTime
}
