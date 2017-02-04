---
layout: page
title: ELK Layered
type: algorithm
---
## ELK Layered

![](images/org-eclipse-elk-layered_preview_layered.png)
**Identifier:** org.eclipse.elk.layered
**Meta Data Provider:** properties.LayeredMetaDataProvider

### Description

Layer-based algorithm provided by the Eclipse Layout Kernel. Arranges as many edges as possible into one direction by placing nodes into subsequent layers. This implementation supports different routing styles (straight, orthogonal, splines); if orthogonal routing is selected, arbitrary port constraints are respected, thus enabling the layout of block diagrams such as actor-oriented models or circuit schematics. Furthermore, full layout of compound graphs with cross-hierarchy edges is supported when the respective option is activated on the top level.

## Category: Layered

The layer-based method was introduced by Sugiyama, Tagawa and Toda in 1981. It emphasizes the direction of edges by pointing as many edges as possible into the same direction. The nodes are arranged in layers, which are sometimes called "hierarchies", and then reordered such that the number of edge crossings is minimized. Afterwards, concrete coordinates are computed for the nodes and edge bend points.

## Supported Graph Features

Name | Description
----|----
Self Loops | Edges connecting a node with itself.
Inside Self Loops | Self-loops routed through a node instead of around it.
Multi Edges | Multiple edges with the same source and target node.
Edge Labels | Labels that are associated with edges.
Ports | Edges are connected to nodes over ports.
Compound | Edges that connect nodes from different hierarchy levels and are incident to compound nodes.
Clusters | Edges that connect nodes from different clusters, but not the cluster parent nodes.

## Supported Options

Option | Type | Default Value | Identifier
----|----|----|----
[Activate Inside Self Loops](org-eclipse-elk-insideSelfLoops-activate) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.insideSelfLoops&#8203;.activate
[Add Unnecessary Bendpoints](org-eclipse-elk-layered-unnecessaryBendpoints) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.unnecessaryBendpoints
[Alignment](org-eclipse-elk-alignment) | `Alignment` | `Alignment.AUTOMATIC` | org&#8203;.eclipse&#8203;.elk&#8203;.alignment
[Aspect Ratio](org-eclipse-elk-aspectRatio) | `double` | `1.6f` | org&#8203;.eclipse&#8203;.elk&#8203;.aspectRatio
[BK Edge Straightening](org-eclipse-elk-layered-nodePlacement-bk-edgeStraightening) | `EdgeStraighteningStrategy` | `EdgeStraighteningStrategy.IMPROVE_STRAIGHTNESS` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.nodePlacement&#8203;.bk&#8203;.edgeStraightening
[BK Fixed Alignment](org-eclipse-elk-layered-nodePlacement-bk-fixedAlignment) | `FixedAlignment` | `FixedAlignment.NONE` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.nodePlacement&#8203;.bk&#8203;.fixedAlignment
[Comment Box](org-eclipse-elk-commentBox) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.commentBox
[Components Spacing](org-eclipse-elk-spacing-componentComponent) | `double` | `12f` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.componentComponent
[Connected Components Compaction](org-eclipse-elk-layered-compaction-connectedComponents) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.compaction&#8203;.connectedComponents
[Content Alignment](org-eclipse-elk-layered-contentAlignment) | `EnumSet<ContentAlignment>` | `EnumSet.noneOf(ContentAlignment)` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.contentAlignment
[Crossing Minimization Strategy](org-eclipse-elk-layered-crossingMinimization-strategy) | `CrossingMinimizationStrategy` | `CrossingMinimizationStrategy.LAYER_SWEEP` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.crossingMinimization&#8203;.strategy
[Cycle Breaking Strategy](org-eclipse-elk-layered-cycleBreaking-strategy) | `CycleBreakingStrategy` | `CycleBreakingStrategy.GREEDY` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.cycleBreaking&#8203;.strategy
[Debug Mode](org-eclipse-elk-debugMode) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.debugMode
[Direction](org-eclipse-elk-direction) | `Direction` | `Direction.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.direction
[Direction Priority](org-eclipse-elk-layered-priority-direction) | `int` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.priority&#8203;.direction
[Distribute Nodes (Deprecated)](org-eclipse-elk-layered-layering-distributeNodes) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.distributeNodes
[Edge Edge Between Layer Spacing](org-eclipse-elk-layered-spacing-edgeEdgeBetweenLayers) | `double` | `10` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.spacing&#8203;.edgeEdgeBetweenLayers
[Edge Label Placement](org-eclipse-elk-edgeLabels-placement) | `EdgeLabelPlacement` | `EdgeLabelPlacement.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.edgeLabels&#8203;.placement
[Edge Label Placement Strategy](org-eclipse-elk-layered-edgeCenterLabelPlacementStrategy) | `EdgeLabelPlacementStrategy` | `EdgeLabelPlacementStrategy.CENTER` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.edgeCenterLabelPlacementStrategy
[Edge Label Side Selection](org-eclipse-elk-layered-edgeLabelSideSelection) | `EdgeLabelSideSelection` | `EdgeLabelSideSelection.ALWAYS_DOWN` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.edgeLabelSideSelection
[Edge Label Spacing](org-eclipse-elk-spacing-edgeLabel) | `double` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.edgeLabel
[Edge Node Between Layers Spacing](org-eclipse-elk-layered-spacing-edgeNodeBetweenLayers) | `double` | `10` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.spacing&#8203;.edgeNodeBetweenLayers
[Edge Node Spacing](org-eclipse-elk-spacing-edgeNode) | `double` | `10` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.edgeNode
[Edge Routing](org-eclipse-elk-edgeRouting) | `EdgeRouting` | `EdgeRouting.ORTHOGONAL` | org&#8203;.eclipse&#8203;.elk&#8203;.edgeRouting
[Edge Spacing](org-eclipse-elk-spacing-edgeEdge) | `double` | `10` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.edgeEdge
[Edge Thickness](org-eclipse-elk-edge-thickness) | `double` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.edge&#8203;.thickness
[Favor Straight Edges Over Balancing](org-eclipse-elk-layered-nodePlacement-favorStraightEdges) | `boolean` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.nodePlacement&#8203;.favorStraightEdges
[Feedback Edges](org-eclipse-elk-layered-feedbackEdges) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.feedbackEdges
[Greedy Switch Activation Threshold](org-eclipse-elk-layered-crossingMinimization-greedySwitch-activationThreshold) | `int` | `40` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.crossingMinimization&#8203;.greedySwitch&#8203;.activationThreshold
[Greedy Switch Crossing Minimization](org-eclipse-elk-layered-crossingMinimization-greedySwitch-type) | `GreedySwitchType` | `GreedySwitchType.TWO_SIDED` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.crossingMinimization&#8203;.greedySwitch&#8203;.type
[Hierarchical Sweepiness](org-eclipse-elk-layered-crossingMinimization-hierarchicalSweepiness) | `double` | `0.1` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.crossingMinimization&#8203;.hierarchicalSweepiness
[Hierarchy Handling](org-eclipse-elk-hierarchyHandling) | `HierarchyHandling` | `HierarchyHandling.INHERIT` | org&#8203;.eclipse&#8203;.elk&#8203;.hierarchyHandling
[High Degree Node Maximum Tree Height](org-eclipse-elk-layered-highDegreeNodes-treeHeight) | `int` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.highDegreeNodes&#8203;.treeHeight
[High Degree Node Threshold](org-eclipse-elk-layered-highDegreeNodes-threshold) | `int` | `16` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.highDegreeNodes&#8203;.threshold
[High Degree Node Treatment](org-eclipse-elk-layered-highDegreeNodes-treatment) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.highDegreeNodes&#8203;.treatment
[Hypernode](org-eclipse-elk-hypernode) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.hypernode
[Individual Spacing Override](org-eclipse-elk-spacing-individualOverride(org.eclipse.elk.layered)) | `IndividualSpacings` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.individualOverride
[Inside Self Loop](org-eclipse-elk-insideSelfLoops-yo) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.insideSelfLoops&#8203;.yo
[Interactive Reference Point](org-eclipse-elk-layered-interactiveReferencePoint) | `InteractiveReferencePoint` | `InteractiveReferencePoint.CENTER` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.interactiveReferencePoint
[Junction Points](org-eclipse-elk-junctionPoints) | `KVectorChain` | `new KVectorChain()` | org&#8203;.eclipse&#8203;.elk&#8203;.junctionPoints
[Label Node Spacing](org-eclipse-elk-spacing-labelNode) | `double` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.labelNode
[Label Port Spacing](org-eclipse-elk-spacing-labelPort) | `double` | `5` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.labelPort
[Label Spacing](org-eclipse-elk-spacing-labelLabel) | `double` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.labelLabel
[Layer Bound](org-eclipse-elk-layered-layering-coffmanGraham-layerBound) | `int` | `Integer.MAX_VALUE` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.coffmanGraham&#8203;.layerBound
[Layer Constraint](org-eclipse-elk-layered-layering-layerConstraint) | `LayerConstraint` | `LayerConstraint.NONE` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.layerConstraint
[Layout Hierarchy](org-eclipse-elk-layoutHierarchy) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layoutHierarchy
[Layout Partition](org-eclipse-elk-partitioning-partition) | `Integer` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.partitioning&#8203;.partition
[Layout Partitioning](org-eclipse-elk-partitioning-activate) | `Boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.partitioning&#8203;.activate
[Linear Segments Deflection Dampening](org-eclipse-elk-layered-nodePlacement-linearSegments-deflectionDampening) | `double` | `0.3` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.nodePlacement&#8203;.linearSegments&#8203;.deflectionDampening
[Margins](org-eclipse-elk-margins) | `ElkMargin` | `new ElkMargin()` | org&#8203;.eclipse&#8203;.elk&#8203;.margins
[Max Node Promotion Iterations](org-eclipse-elk-layered-layering-nodePromotion-maxIterations) | `int` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.nodePromotion&#8203;.maxIterations
[Merge Edges](org-eclipse-elk-layered-mergeEdges) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.mergeEdges
[Merge Hierarchy-Crossing Edges](org-eclipse-elk-layered-mergeHierarchyEdges) | `boolean` | `true` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.mergeHierarchyEdges
[Minimum Height](org-eclipse-elk-nodeSize-minHeight) | `double` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.minHeight
[Minimum Width](org-eclipse-elk-nodeSize-minWidth) | `double` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.minWidth
[No Layout](org-eclipse-elk-noLayout) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.noLayout
[Node Label Placement](org-eclipse-elk-nodeLabels-placement) | `EnumSet<NodeLabelPlacement>` | `NodeLabelPlacement.fixed()` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeLabels&#8203;.placement
[Node Layering Strategy](org-eclipse-elk-layered-layering-strategy) | `LayeringStrategy` | `LayeringStrategy.NETWORK_SIMPLEX` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.strategy
[Node Node Between Layers Spacing](org-eclipse-elk-layered-spacing-nodeNodeBetweenLayers) | `double` | `20` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.spacing&#8203;.nodeNodeBetweenLayers
[Node Placement Strategy](org-eclipse-elk-layered-nodePlacement-strategy) | `NodePlacementStrategy` | `NodePlacementStrategy.BRANDES_KOEPF` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.nodePlacement&#8203;.strategy
[Node Promotion Strategy](org-eclipse-elk-layered-layering-nodePromotion-strategy) | `NodePromotionStrategy` | `NodePromotionStrategy.NONE` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.nodePromotion&#8203;.strategy
[Node Size Constraints](org-eclipse-elk-nodeSize-constraints) | `EnumSet<SizeConstraint>` | `EnumSet.noneOf(SizeConstraint)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.constraints
[Node Size Minimum](org-eclipse-elk-nodeSize-minimum) | `KVector` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.minimum
[Node Size Options](org-eclipse-elk-nodeSize-options) | `EnumSet<SizeOptions>` | `EnumSet.of(SizeOptions.DEFAULT_MINIMUM_SIZE, SizeOptions.APPLY_ADDITIONAL_PADDING)` | org&#8203;.eclipse&#8203;.elk&#8203;.nodeSize&#8203;.options
[Node Spacing](org-eclipse-elk-spacing-nodeNode) | `double` | `20` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.nodeNode
[North or South Port](org-eclipse-elk-layered-northOrSouthPort) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.northOrSouthPort
[Padding](org-eclipse-elk-padding) | `ElkPadding` | `new ElkPadding(12)` | org&#8203;.eclipse&#8203;.elk&#8203;.padding
[Port Alignment](org-eclipse-elk-portAlignment-basic) | `PortAlignment` | `PortAlignment.JUSTIFIED` | org&#8203;.eclipse&#8203;.elk&#8203;.portAlignment&#8203;.basic
[Port Alignment (East)](org-eclipse-elk-portAlignment-east) | `PortAlignment` | `PortAlignment.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.portAlignment&#8203;.east
[Port Alignment (North)](org-eclipse-elk-portAlignment-north) | `PortAlignment` | `PortAlignment.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.portAlignment&#8203;.north
[Port Alignment (South)](org-eclipse-elk-portAlignment-south) | `PortAlignment` | `PortAlignment.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.portAlignment&#8203;.south
[Port Alignment (West)](org-eclipse-elk-portAlignment-west) | `PortAlignment` | `PortAlignment.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.portAlignment&#8203;.west
[Port Anchor Offset](org-eclipse-elk-port-anchor) | `KVector` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.port&#8203;.anchor
[Port Border Offset](org-eclipse-elk-port-borderOffset) | `double` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.port&#8203;.borderOffset
[Port Constraints](org-eclipse-elk-portConstraints) | `PortConstraints` | `PortConstraints.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.portConstraints
[Port Index](org-eclipse-elk-port-index) | `int` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.port&#8203;.index
[Port Label Placement](org-eclipse-elk-portLabels-placement) | `PortLabelPlacement` | `PortLabelPlacement.OUTSIDE` | org&#8203;.eclipse&#8203;.elk&#8203;.portLabels&#8203;.placement
[Port Side](org-eclipse-elk-port-side) | `PortSide` | `PortSide.UNDEFINED` | org&#8203;.eclipse&#8203;.elk&#8203;.port&#8203;.side
[Port Spacing](org-eclipse-elk-spacing-portPort) | `double` | `10` | org&#8203;.eclipse&#8203;.elk&#8203;.spacing&#8203;.portPort
[Position](org-eclipse-elk-position) | `KVector` | `<not defined>` | org&#8203;.eclipse&#8203;.elk&#8203;.position
[Post Compaction Constraint Calculation](org-eclipse-elk-layered-compaction-postCompaction-constraints) | `ConstraintCalculationStrategy` | `ConstraintCalculationStrategy.SCANLINE` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.compaction&#8203;.postCompaction&#8203;.constraints
[Post Compaction Strategy](org-eclipse-elk-layered-compaction-postCompaction-strategy) | `GraphCompactionStrategy` | `GraphCompactionStrategy.NONE` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.compaction&#8203;.postCompaction&#8203;.strategy
[Priority](org-eclipse-elk-priority(org.eclipse.elk.layered)) | `int` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.priority
[Randomization Seed](org-eclipse-elk-randomSeed) | `int` | `1` | org&#8203;.eclipse&#8203;.elk&#8203;.randomSeed
[Sausage Folding](org-eclipse-elk-layered-sausageFolding) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.sausageFolding
[Semi-Interactive Crossing Minimization](org-eclipse-elk-layered-crossingMinimization-semiInteractive) | `boolean` | `false` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.crossingMinimization&#8203;.semiInteractive
[Separate Connected Components](org-eclipse-elk-separateConnectedComponents) | `boolean` | `true` | org&#8203;.eclipse&#8203;.elk&#8203;.separateConnectedComponents
[Shortness Priority](org-eclipse-elk-layered-priority-shortness(org.eclipse.elk.layered)) | `int` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.priority&#8203;.shortness
[Sloppy Spline Layer Spacing Factor](org-eclipse-elk-layered-edgeRouting-sloppySplineLayerSpacing) | `double` | `0.4` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.edgeRouting&#8203;.sloppySplineLayerSpacing
[Sloppy Spline Routing](org-eclipse-elk-layered-edgeRouting-sloppySplineRouting) | `boolean` | `true` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.edgeRouting&#8203;.sloppySplineRouting
[Spline Self-Loop Placement](org-eclipse-elk-layered-edgeRouting-selfLoopPlacement) | `SelfLoopPlacement` | `SelfLoopPlacement.NORTH_STACKED` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.edgeRouting&#8203;.selfLoopPlacement
[Straightness Priority](org-eclipse-elk-layered-priority-straightness) | `int` | `0` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.priority&#8203;.straightness
[Thoroughness](org-eclipse-elk-layered-thoroughness) | `int` | `7` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.thoroughness
[Upper Bound On Width [MinWidth Layerer]](org-eclipse-elk-layered-layering-minWidth-upperBoundOnWidth) | `int` | `4` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.minWidth&#8203;.upperBoundOnWidth
[Upper Layer Estimation Scaling Factor [MinWidth Layerer]](org-eclipse-elk-layered-layering-minWidth-upperLayerEstimationScalingFactor) | `int` | `2` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.minWidth&#8203;.upperLayerEstimationScalingFactor
[Wide Nodes on Multiple Layers](org-eclipse-elk-layered-layering-wideNodesOnMultipleLayers) | `WideNodesStrategy` | `WideNodesStrategy.OFF` | org&#8203;.eclipse&#8203;.elk&#8203;.layered&#8203;.layering&#8203;.wideNodesOnMultipleLayers

