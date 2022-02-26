# How to release

## Update the table of contents in `README.md`

If you changed `README.md`, that is.

## Update the version

```
mvn versions:set -DnewVersion=$VERSION
mvn versions:commit
```

## Update the URL to the license

The url includes the tag name (version).

## Update the version in `README.md`

The section "Prerequisites" mentions the version twice.

## Commit, push and tag

## Push to Artifactory

```
mvn clean deploy -Pdeploy
```

## References

* [https://dev.to/julbrs/beginner-guide-to-maven-central-publishing-3jio](https://dev.to/julbrs/beginner-guide-to-maven-central-publishing-3jio)