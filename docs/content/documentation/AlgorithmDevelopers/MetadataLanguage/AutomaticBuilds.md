---
title: "Automatic Builds"
menu:
  main:
    identifier: "AutomaticBuilds"
    parent: "ELKMetadataLanguage"
    weight: 10
---

Since `.melk` files result in code being generated, you may not want to check that code into your repository. Instead, the code should probably be generated as part of your automatic build. Indeed, the ELK metadata language compiler is available through a Maven repository. You can find the repository URLs on our [Downloads]({{< sectionref "downloads" >}}) page. Note that we provide a separate repository for nightly builds and for each release.

To use the compiler, add the following to your `pom.xml`:

```xml
<pluginRepositories>
  <!-- Xtext is required to invoke our compiler. -->
  <pluginRepository>
    <id>xtend</id>
    <name>Xtend Maven Repository</name>
    <layout>default</layout>
    <url>http://build.eclipse.org/common/xtend/maven</url>
  </pluginRepository>

  <!-- This is where our compiler is. -->
  <pluginRepository>
    <id>eclipse-elk-meta</id>
    <name>ELK Meta Data Language Compiler Maven Repository</name>
    <url>http://the.repository.url.you.want/see.download.page.for.details</url>
  </pluginRepository>
</pluginRepositories>

<build>
  <plugins>
    <!-- Configure the Xtext plugin to invoke our compiler. -->
    <plugin>
      <groupId>org.eclipse.xtext</groupId>
      <artifactId>xtext-maven-plugin</artifactId>
      <version>2.7.2</version>
      <executions>
        <execution>
          <goals>
            <goal>generate</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <languages>
          <language>
            <setup>org.eclipse.elk.core.meta.MetaDataStandaloneSetup</setup>
            <outputConfigurations>
              <outputConfiguration>
                <outputDirectory>${basedir}/src-gen/</outputDirectory>
              </outputConfiguration>
            </outputConfigurations>
          </language>
        </languages>
      </configuration>
      <dependencies>
        <dependency>
          <groupId>org.eclipse.elk</groupId>
          <artifactId>org.eclipse.elk.graph</artifactId>
          <version>THE_VERSION_YOU_WANT-SNAPSHOT</version>
        </dependency>
        <dependency>
          <groupId>org.eclipse.elk</groupId>
          <artifactId>org.eclipse.elk.core.meta</artifactId>
          <version>THE_VERSION_YOU_WANT-SNAPSHOT</version>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
</build>
```
