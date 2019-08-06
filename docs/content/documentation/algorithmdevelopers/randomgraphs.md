---
title: "Random Graph Generation"
menu:
  main:
    identifier: "RandomGraphs"
    parent: "AlgorithmDevelopers"
    weight: 60
---

If you need to quickly create random graphs in `.elkt` or `.elkg` format, you can use our random graph creation DSL. This way you can save, store, comment and diff your random graph configurations easily!

To get started, create a file with the file ending `.elkr`. Now Eclipse will offer the usual features you know and love, such as code completion, syntax highlighting and validation. So remember Eclipse rule number 1: Ctrl+Space is your friend!

Now let's generate some random graphs. The following configuration would generates 5 trees and two graphs using the default predefined values:

```
generate 5 trees
generate 2 graphs
```

Right-clicking the saved file in the package explorer and choosing _Generate Random Graphs_ will result in seven graph files to be created. You can further specify how the graphs are created. For example:

```
generate 2 graphs {
    // Gaussian distribution: mean +/- standard deviation
    nodes = 20 +/- 5 {
        // With ports and extra options block
        ports {
            // Set port constraint to fixed order
            constraint = order

            // Equal distribution: min to max. Re-use 30%
            // to 40% of ports, i.e. different edges exit
            // or enter same port
            re-use = 0.3 to 0.4
        }
        
        // Generate node labels
        labels
    }

    // Relative to the number of nodes
    edges relative = 0.5 to 1.5 {
        // Allow self loops
        self loops
    }

    filename = "whatABeautifulGraph"
}
```

There are a lot more options to be found. Again, use Ctrl-Space to find out more.
