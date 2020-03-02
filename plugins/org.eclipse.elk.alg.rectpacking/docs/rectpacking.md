The different phases of the algorithm are illustrated below on an example:

First the drawing width is approximated. Such a drawing can look like this:

![Width approximation](/images/options/width_approximation.png)

After this the initial drawing is created:

![Initial placement](/images/options/initial_placement.png)

This drawing is then compacted to use less space:

![Row compaction](/images/options/row_compaction.png)

Now the nodes can be either expanded to fill the bounding box:

![Node expansion](/images/options/node_expansion.png)

Or, the nodes can be expanded to fit the aspect ratio.

![Fit aspect ratio](/images/options/expand_to_aspect_ratio.png)