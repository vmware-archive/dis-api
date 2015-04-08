# Dis Ingest

## Build

The following command produces a zip file which Cloud Foundry can accept.

    `gradle distZip`

## Deploy

The following variables must be defined in the application's environment:

Variable  | Value
--------- | -----
AWS_ACCESS_KEY_ID | Your AWS access key
AWS_SECRET_KEY | Your AWS secret key

To run locally, create ``gradle.properties``, based on ``gradle.template.properties``, and then use:

```
gradle run
```

This will set the necessary variables.
