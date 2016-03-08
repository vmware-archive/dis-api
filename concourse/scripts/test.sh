#!/bin/bash

set -e
export TERM=dumb # needed for Gradle: https://issues.gradle.org/browse/GRADLE-2634

pushd github/dis-ingest
./gradlew test
popd
