# Eclipse Layout Kernel (ELK)

Diagrams and visual languages are a great thing,
but getting the layout just right for them to be easily understandable
can be tedious and time-consuming.
The Eclipse Layout Kernel provides a number of layout algorithms
as well as an Eclipse-based infrastructure to connect them
to editors and viewers.
The layout algorithms are plain Java
and can thus also be used outside of Eclipse.

If you want to know more,
let us refer you [to the ELK homepage](http://www.eclipse.org/elk).


## Repository Structure

The repository's structure is pretty straightforward. We only have a few folders:

* `build`:
  Contains all the files necessary to build ELK in all its different forms.
* `features`:
  Contains all the Eclipse features ELK consists of.
* `plugins`:
  Contains all the plugins ELK consists of.


## Building ELK

ELK is built using Maven.
To build the update site,
simply navigate to the `build` folder
and type:

```
mvn clean package
```

There are more build profiles available
that are defined and documented in `build/pom.xml`.
