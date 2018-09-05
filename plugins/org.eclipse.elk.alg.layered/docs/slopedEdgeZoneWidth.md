Usually, the polyline edge router takes care to route edges horizontally once they enter a layer's area.
This is to prevent edges from crossing nodes that may be in that layer
and leads to results such as this:  
 
![Sloped Edge Zone Width off](/images/options/edgeRouting_polyline_slopedEdgeZoneWidth_off.png)

If nodes extend almost, but not quite to their layer's boundary,
the horizontal routing segment becomes arbitrarily small,
leading to awkward layouts.
The polyline edge router thus refrains from producing such segments
if their length would fall below a certain threshold.
That threshold is what this option defines.
With a proper setting,
the above diagram looks like this:
 
![Sloped Edge Zone Width off](/images/options/edgeRouting_polyline_slopedEdgeZoneWidth_on.png) 
