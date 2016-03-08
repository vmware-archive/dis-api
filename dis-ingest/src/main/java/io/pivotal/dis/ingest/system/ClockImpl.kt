package io.pivotal.dis.ingest.system

import java.time.LocalDateTime

class ClockImpl : Clock {

    override val currentTime: LocalDateTime
        get() = LocalDateTime.now()
}
