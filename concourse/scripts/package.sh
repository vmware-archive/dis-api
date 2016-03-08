#!/bin/bash

set -e
export TERM=dumb # needed for Gradle: https://issues.gradle.org/browse/GRADLE-2634

pushd github/dis-ingest
./gradlew distZip
popd

cp github/dis-ingest/manifest.yml package/manifest.yml
cp github/dis-ingest/build/distributions/dis-ingest.zip package/dis-ingest.zip
