# dis

[![Build Status](https://secure.travis-ci.org/pivotal/dis.png?branch=master)](http://travis-ci.org/pivotal/dis)

## Description

[Pivotal Labs](http://pivotallabs.com) engineers and data scientists will collaborate on building an Android
app named "dis" that makes predictions of about incidents occurring on the London Underground.

## Backlog

The backlog for the project can be found [here](https://www.pivotaltracker.com/n/projects/1278296).

## Releases

You can download releases of the Dis Android app on our Github releases page [here](https://github.com/pivotal/dis/releases).

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
