# Dis Ingest

## Build

The following command produces a zip file which Cloud Foundry can accept.

```
gradle distZip
```

## Deploy

The following variables must be defined in the application's environment:

Variable  | Value
--------- | -----
AWS_ACCESS_KEY_ID | Your AWS access key
AWS_SECRET_KEY | Your AWS secret key
S3_BUCKET_NAME_RAW | The name of the S3 bucket to store raw TfL data into
S3_BUCKET_NAME_DIGESTED | The name of the S3 bucket to store the digested current disruption data into
VCAP_SERVICES | [A Cloud Foundry services definition](http://docs.cloudfoundry.org/devguide/deploy-apps/environment-variable.html#VCAP-SERVICES) with a service called ``tfl`` with a credential ``uri`` which points to the TfL API

A valid ``VCAP_SERVICES`` value would be:

```
{"": [{"name": "tfl", "credentials": {"uri": "http://api.tfl.gov.uk/Line/Mode/tube/Status?detail=False&app_id=APP_ID&app_key=APP_KEY"}, "label": "", "tags": []}]}
```

To run locally, create ``gradle.properties``, based on ``gradle.template.properties``, and then use:

```
gradle run
```

This will set the necessary variables.

## Configuring S3

Dis stores its data in S3. The file containing the digested disruptions file must be public. An easy way to manage S3 resources is using [s3cmd](http://s3tools.org/s3cmd). You should be able to install this using any decent package manager, or Homebrew. Once installed, configure it:

```
s3cmd --configure
```

You will be prompted for all sorts of highly personal details, of which only the access key and secret key are essential. You can then make any file public:

```
s3cmd setacl --acl-public s3://pivotal-london-dis-digest/disruptions.json
```

Alternatively, use the ``--recursive`` flag to make a directory, or an entire bucket, public.
