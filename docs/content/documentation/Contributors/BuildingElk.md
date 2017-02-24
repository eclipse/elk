---
title: "Building ELK"
menu:
  main:
    identifier: "BuildingELK"
    parent: "ELKContributors"
    weight: 30
---

ELK is built using [Apache Maven](https://maven.apache.org/) in conjunction with [Tycho](https://eclipse.org/tycho/) to tell Maven how to build Eclipse projects. There are two parts that can be built: the Eclipse Layout Kernel itself, and the metadata compiler used by the main ELK build. The remainder of this page assumes that you have opened a shell in the `build/` directory inside your clone of the ELK repository.


## Building ELK

Execute Maven using the following command line (note that the command line is split in order to improve readability):

```bash
mvn
    --define elkMeta.repositoryUrl=META_REPOSITORY_URL
    clean
    ADDITIONAL_PHASES
```

`clean` is not strictly necessary.

For `META_REPOSITORY_URL`, insert the URL of the Maven repository where the ELK Metadata compiler can be found. If you have _not_ touched the thing (if in doubt, this is the case), the [Downloads]({{< sectionref "downloads" >}}) page will tell you which repository to use to build different releases (to build the current development version, use the nightly build repository). If you _have_ touched the metadata compiler, the repository URL should be the same as in the metadata compiler build (see below).

For `ADDITIONAL_PHASES`, the following are typical choices:

Phase     | Description
-----     | -----------
`package` | Simply compile everything. You will find the result in the update site placed in the `org.eclipse.elk.repository/target/` directory.
`verify`  | Compile ELK and execute the unit tests.


### Building Documentation for ELK

If the ELK build should also generate documentation from all metadata files (usually to be put into our website), Maven needs to be supplied with the path to the ELK repository's `doc/` folder (or to whereever the documentation should be placed). Do so by adding another command-line parameter to the Maven call:

```bash
--define elk.metadata.documentation.outputPath=/path/to/docs/folder
```


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
