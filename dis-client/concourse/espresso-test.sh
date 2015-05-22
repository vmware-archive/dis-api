#!/bin/sh

export TERM=dumb

cd dis-client/dis-client
./gradlew connectedAndroidTest
