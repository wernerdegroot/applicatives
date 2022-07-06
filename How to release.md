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

All the way to the bottom, there is a similar link in `README.md`.

## Update the version in `README.md`

The README mentions the version number several times.

The READMEs of the `json` module and the `records` module also mentions the version number.

## Upgrade the `nexus-staging-maven-plugin` plugin

Upgrade the `nexus-staging-maven-plugin` plugin to the latest version.

## Commit, push and tag

Commit name should be something like `Release v1.0.3`.

## Push to Artifactory

Using Java 8:

```
export GPG_TTY=$(tty)
mvn clean deploy -Pdeploy,\!records
```

Using Java 17:

```
export GPG_TTY=$(tty)
mvn clean deploy -pl records -Pdeploy,records
```

## Check if pushing succeeded

At [https://s01.oss.sonatype.org/#nexus-search;quick~wernerdegroot](https://s01.oss.sonatype.org/#nexus-search;quick~wernerdegroot)

## Common issues

**If you get an error about `ioctl`**

If you get an error saying something like

```
gpg: signing failed: Inappropriate ioctl for device
```

first export `GPG_TTY`:

```
export GPG_TTY=$(tty)
```

(Source: https://github.com/keybase/keybase-issues/issues/2798)

**If you forgot your password**

Check `settings.xml` and remember that Sonatype requires the passwords to be pretty long.

## References

* [https://dev.to/julbrs/beginner-guide-to-maven-central-publishing-3jio](https://dev.to/julbrs/beginner-guide-to-maven-central-publishing-3jio)