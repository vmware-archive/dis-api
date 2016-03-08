package io.pivotal.dis.ingest.domain.tfl

data class TflLine(val name: String, val lineStatuses: List<LineStatus>)
