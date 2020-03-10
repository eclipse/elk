The packing strategy specifies what is most important during approximation of the drawing width.
During the approximation phase four different node positions are considered for a new node:
Right of the last node, below the last node, right of the whole drawing, below the whole drawing.
These possible positions are rated based on the strategy.

Strategy       | Meaning
----------       | -------
`MAX_SCALE_DRIVEN` | Filters possible placement options for scale measure, area, and aspect ratio in this order.
`ASPECT_RATIO_DRIVEN` | Filters possible placement options for aspect ratio, area, and aspect ratio in this order.
`AREA_DRIVEN` | Filters possible placement options for area, scale measure, and aspect ratio in this order.


### Examples

For the same graph the initial drawing for the width approximation might influence the drawing a lot.
With a aspect ratio of 2.5 the different strategies result in the following initial drawings:

Strategy | Drawing
---------- | -------------------------------------------------------------------
`MAX_SCALE_DRIVEN` | ![MAX_SCALE_DRIVEN](/images/options/strategy_maxscaledriven.png)
`ASPECT_RATIO_DRIVEN` | ![MAX_SCALE_DRIVEN](/images/options/strategy_aspectratiodriven.png)
`AREA_DRIVEN` | ![MAX_SCALE_DRIVEN](/images/options/strategy_areadriven.png)