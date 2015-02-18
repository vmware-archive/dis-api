# Deckard (for Gradle)
[![Build Status](https://secure.travis-ci.org/robolectric/deckard-gradle.png?branch=master)](http://travis-ci.org/robolectric/deckard-gradle)

Deckard is the simplest possible Android project that uses Robolectric for testing and Gradle to build. It has one Activity, a single Robolectric test of that Activity, and an Espresso test of that Activity.

Deckard imports easily into the latest editions of Android Studio with minimal setup.

## Setup

*Note: These instructions assume you have a Java 1.8 [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed.*

To start a new Android project:

1. Install [Android Studio](http://developer.android.com/sdk/index.html).

2. Run the [Android SDK Manager](http://developer.android.com/tools/help/sdk-manager.html) and install `API 19` and `Build-tools 21.1.2`.

3. Download Deckard from GitHub:
    ```bash
    wget https://github.com/robolectric/deckard-gradle/archive/master.zip
    unzip master.zip
    mv deckard-gradle-master my-new-project
    ```

4. In the project directory you should be able to run the Robolectric tests:

    ```bash
    cd my-new-project
    ./gradlew clean test
    ```

5. You should also be able to run the Espresso tests:

    ```bash
    ./gradlew clean connectedAndroidTest
    ```

    Note: Make sure to start an Emulator or connect a device first so the test has something to connect to.

6. Change the names of things from 'Deckard' to whatever is appropriate for your project. Package name, classes, build.gradle, and the AndroidManifest are good places to start.

7. Build an app. Win.

## Android Studio Support

### Compatibility
Use the latest Android Studio. The most recent updates were run against Android Studio 1.0.1 with the 'Android Studio Unit Test' plugin available from 'Browse Repositories...'.

### Importing
Import the project into Android Studio by selecting 'Import Project' and selecting the project's `build.gradle`. When prompted, you can just pick the default gradle wrapper.

### Running the Robolectric Test
You should now be able to `DeckardActivityRobolectricTest`. Run it as a normal JUnit test - make sure to choose the JUnit test runner and not the Android one.

### Running the Espresso Test
The Espresso tests are runnable with the Android test runner within Android Studio.
