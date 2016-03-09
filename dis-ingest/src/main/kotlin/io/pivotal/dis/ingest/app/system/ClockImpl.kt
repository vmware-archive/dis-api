package io.pivotal.dis.ingest.app.system

import java.time.LocalDateTime

class ClockImpl : Clock {

    override val currentTime: LocalDateTime
        get() = LocalDateTime.now()
}
