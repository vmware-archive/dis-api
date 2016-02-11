# Dis Ingest

## Local set up

Set the following environment variables locally:

SPRING_PROFILES_ACTIVE=local | Use the local profile
AWS_ACCESS_KEY_ID | Your AWS access key
AWS_SECRET_KEY | Your AWS secret key
TFL_URL | Your TFL url with app id and app key

For convenience with `direnv` copy the `.envrc.template` to `.envrc` and add the secrets.

The TFL url has the following format:

```
http://api.tfl.gov.uk/Line/Mode/tube/Status?detail=False&app_id=APP_ID&app_key=APP_KEY
```

## Cloud set up

The Java buildpack will use the `cloud` profile.

Set the following environment variables on the Cloud Foundry app:

```
cf set-env dis-ingest AWS_ACCESS_KEY_ID REPLACE_WITH_AWS_KEY
cf set-env dis-ingest AWS_SECRET_KEY REPLACE_WITH_AWS_SECRET
```

The TfL endpoint is obtained from a user provided service called `tfl`. To create run:

```
cf create-user-provided-sevice tfl -p '{"uri":"REPLACE_WITH_TFL_URL"}'
cf bind-service dis-ingest tfl
```

## Configuration

The S3 bucket names are configured in `resources/application.properties`.

## Build

The following command produces a Spring Boot JAR file which Cloud Foundry can accept.

```
./gradlew bootRepackage
```

## Run locally

```
./gradlew bootRun
```

## Deploy to Cloud Foundry

```
./gradlew bootRepackage
cf target -o ORG -s SPACE
cf push
```
