# Eclipse Layout Kernel (ELK)

Diagrams and visual languages are a great thing,
but getting the layout just right for them to be easily understandable
can be tedious and time-consuming.
The Eclipse Layout Kernel provides a number of layout algorithms
as well as an Eclipse-based infrastructure to connect them
to editors and viewers.
The layout algorithms are plain Java
and can thus also be used outside of Eclipse.

## More Info

* [The ELK homepage](http://www.eclipse.org/elk)
* [Our documentation](https://github.com/eclipse/elk/wiki)


## Repository Structure

The repository's structure is pretty straightforward. We only have a few folders:

* `build`:
  Contains all the files necessary to build ELK in all its different forms.
* `config`:
  Contains configuration files, such as our Checkstyle configuration.
* `features`:
  Contains all the Eclipse features ELK consists of.
* `plugins`:
  Contains all the plugins ELK consists of.
* `setups`:
  Contains our Oomph setup files.
* `tests`:
  Contains unit tests.


## Building ELK

ELK is built using Maven.
To build everything,
first navigate to the `build` folder.

The build itself is split into two parts.
We first need to build and install
our meta data language compiler
required during the main build,
and then trigger the main build itself:

```
mvn clean install -P elk-meta
mvn clean package
```

If built on the Eclipse infrastructure,
the produced artifacts can be signed
by calling the main build as follows:

```
mvn clean verify -P elk-update-site -P sign
```
