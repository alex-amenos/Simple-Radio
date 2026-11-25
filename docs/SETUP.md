# Project Setup

Follow these steps to configure, build, and run the Simple Radio app locally.

## Prerequisites

- Git
- Android Studio
- JDK 21 (Android Studio's bundled JDK is suitable if it is version 21)
- An Android emulator or a physical Android device running API 26 or newer

The project includes the Gradle wrapper, so you do not need to install Gradle separately. Android Studio can install the Android SDK components required by the project when you sync.

## 1. Get the project

Clone the repository and open its root directory in Android Studio:

```shell
git clone https://github.com/alex-amenos/Simple-Radio.git
cd Simple-Radio
```

In Android Studio, choose **Open** and select the `Simple-Radio` directory. If prompted, trust the project and sync it with Gradle. In **Settings > Build, Execution, Deployment > Build Tools > Gradle**, set **Gradle JDK** to JDK 21.

## 2. API credentials (optional)

The radio, home and settings features do not require any credentials. The `shared:api` module can optionally read a TMDB token, which is only needed if you add features that use the TMDB API.

The token is read as a Gradle project property. Add it to your user-level Gradle properties file, which is outside the repository and must not be committed:

- macOS or Linux: `~/.gradle/gradle.properties`
- Windows: `%USERPROFILE%\.gradle\gradle.properties`

```properties
THEMOVIEDB_ORG_API_TOKEN=your_tmdb_api_read_access_token
```

Use the **API Read Access Token (v4 auth)** from your TMDB account's **API** settings, not the v3 API key. You can also pass it to a Gradle command with `-PTHEMOVIEDB_ORG_API_TOKEN=...`.

Avoid putting real credentials in committed files, screenshots, or shared command history.

## 3. Build the debug app

From the project root, build the debug APK:

```shell
./gradlew :app:assembleDebug
```

On Windows, use `gradlew.bat :app:assembleDebug`. The Android Gradle Plugin may prompt you to install missing SDK components; accept the licenses and install the requested components.

## 4. Launch the app

In Android Studio, start an emulator or connect an Android device with USB debugging enabled. Select the **app** run configuration and click **Run**.

Alternatively, install and launch the debug app on a running emulator or connected device:

```shell
./gradlew :app:installDebug
```

The debug application ID is `com.alxnophis.jetpack.debug`.

## Useful checks

Run the project's CI checks from the repository root:

```shell
./gradlew ktlintCheck
./gradlew lintDebug
./gradlew koverHtmlReportDebug
```
