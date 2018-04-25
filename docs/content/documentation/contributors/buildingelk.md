---
title: "Building ELK"
menu:
  main:
    identifier: "BuildingELK"
    parent: "ELKContributors"
    weight: 30
---

ELK is built using [Apache Maven](https://maven.apache.org/) in conjunction with [Tycho](https://eclipse.org/tycho/) to tell Maven how to build Eclipse projects. There are two parts that can be built: the Eclipse Layout Kernel itself, and the metadata compiler used by the main ELK build. The remainder of this page assumes that you have opened a shell in the `build/` directory inside your clone of the ELK repository.


## The Main Build Process

Execute Maven using the following command line (note that the command line is split in order to improve readability):

```bash
mvn
    --define elkMeta.repositoryUrl=META_REPOSITORY_URL
    clean
    ADDITIONAL_PHASES
```

`clean` is not strictly necessary.

For `META_REPOSITORY_URL`, insert the URL of the Maven repository where the ELK Metadata compiler can be found. If you have _not_ touched the thing (if in doubt, this is the case), the Downloads section will tell you which repository to use to build different releases (to build the current development version, use the nightly build repository). If you _have_ touched the metadata compiler, the repository URL should be the same as in the metadata compiler build (see below).

For `ADDITIONAL_PHASES`, the following are typical choices:

Phase     | Description
-----     | -----------
`package` | Simply compile everything. You will find the result in the update site placed in the `org.eclipse.elk.repository/target/` directory.
`verify`  | Compile ELK and execute the unit tests.


## Building the Metadata Compiler

Execute Maven using the following command line (note that the command line is split in order to improve readability):

```bash
mvn
    -P elk-meta
    --define elkMeta.repositoryUrl=META_REPOSITORY_URL
    --define elkMeta.publishUrl=META_REPOSITORY_URL
    clean
    ADDITIONAL_PHASES
```

`clean` is not strictly necessary.

Maven will build a Maven repository that can be used by the main build to compile `.elkm` files. For `META_REPOSITORY_URL`, insert the URL where you want that repository to be placed. If you're building locally, this should be a path on your local file system. For example:

```bash
--define elkMeta.repositoryUrl=file:///home/user/elkmetarepository
--define elkMeta.publishUrl=file:///home/user/elkmetarepository
```

For `ADDITIONAL_PHASES`, the following are typical choices:

Phase     | Description
-----     | -----------
`deploy`  | Compile the metadata compiler and put it into a Maven repository at `META_REPOSITORY_URL`.


## Building the Documentation

The documentation website is our source of documentation. The main build can be configured to generate the reference documentation which is part of the website from our metadata files. To do so, add the following command-line argument to the main build call:

```bash
--define elk.metadata.documentation.outputPath=/path/to/docs/folder
```

The documentation website itself is built with [Hugo](https://gohugo.io/), a static website generator. The website is setup in a way to be browsable both online and offline. To actually build the documentation website, go to the repository's `docs/` folder and simply execute the following command:

```bash
hugo
```

To edit the website, start a Hugo server instance in the repository's `docs/` folder:

```bash
hugo server
```

You can then direct your browser to `http://localhost:1313/elk/`. As you edit the website's files and save them, the browser's content is dynamically updated.
