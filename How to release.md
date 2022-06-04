# How to release

## Update the table of contents in `README.md`

If you changed `README.md`, that is.

## Update the version

```
mvn versions:set -DnewVersion=$VERSION
mvn versions:commit
```

## Update the URL to the license

The root `pom.xml` contains a tag which includes the tag name (version):

```xml
    <licenses>
        <license>
            <name>MIT License</name>
            <url>https://github.com/wernerdegroot/applicatives/blob/v1.0.1/LICENSE</url>
        </license>
    </licenses>
```

## Update the version in `README.md`

The section "Prerequisites" mentions the version three times (when describing the dependencies you need).

## Commit, push and tag

Commit name should be something like `Release v1.0.3`.

## Push to Artifactory

```
mvn clean deploy -Pdeploy
```

## References

* [https://dev.to/julbrs/beginner-guide-to-maven-central-publishing-3jio](https://dev.to/julbrs/beginner-guide-to-maven-central-publishing-3jio)