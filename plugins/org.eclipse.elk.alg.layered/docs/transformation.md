Internally, 
we map any desired layout direction to the variant of creating a left-to-right drawing.
This is done by transforming the input graph's coordinates in various ways. 
Depending on the way this transformation is performed, 
one can either preserve a _reading direction_ or _rotation_. 

When preserving reading direction, 
the vertical reading direction of a left-to-right or right-to-left layout
(and thus the node, edge, and edge label order) corresponds to the horizontal reading direction 
of a top-to-bottom or bottom-to-top layout. 
When preserving rotation,
the four possible drawings are simply rotated versions of the left-to-right variant. 
This might be of interest if one wants to preserve a _clockwise orientation_
of feedback loops.  

The following drawings illustrate the different behaviors.  
 
### Reading Direction
![readingdirection](/images/options/transformation_readingdirection_based.png)
 
### Rotation 
![rotation](/images/options/transformation_rotation_based.png) 
