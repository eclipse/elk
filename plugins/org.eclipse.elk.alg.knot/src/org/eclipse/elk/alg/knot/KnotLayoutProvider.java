/**
 * @author Jean-Pierre Runge (stu224382) (Mat: 1150750)
 */
package org.eclipse.elk.alg.knot;

import java.util.List;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.elk.alg.common.NodeMicroLayout;
import org.eclipse.elk.alg.force.ComponentsProcessor;
import org.eclipse.elk.alg.force.ElkGraphImporter;
import org.eclipse.elk.alg.force.ForceLayoutProvider;
import org.eclipse.elk.alg.force.IGraphImporter;
import org.eclipse.elk.alg.force.graph.FGraph;
import org.eclipse.elk.alg.force.options.StressOptions;
import org.eclipse.elk.alg.force.stress.StressMajorization;
import org.eclipse.elk.alg.knot.options.KnotOptions;

/**
 * Layout provider for the knot layout algorithm. This algorithm places nodes and routes edges in order to modify
 * positions further via stress minimization.
 */
public class KnotLayoutProvider extends AbstractLayoutProvider {

    /** Connected components processor. */
    private ComponentsProcessor componentsProcessor = new ComponentsProcessor();
    /** Implementation of stress majorization for standard stress layout. */
    private StressMajorization stressMajorization = new StressMajorization();
    
    /** The distance which bend points around nodes must preserve. */
    private double nodeRadius = 25; //25 got overall best results;
    /** The sum of all four bend point angles on a node must be lower in order to perform a shift movement. */
    private double shiftThreshold = 420;
    /** Desired distance between connected nodes (should at least be nodeRadius). */
    private double desiredNodeDistance = nodeRadius;
    /** Whether the outgoing and incoming edges can be rotated separately. */
    private boolean separateAxisRotation = false;
    /** Weighted step size for rotations. */
    private double rotationValue = 1;
    /** Weighted step size for shift movements. */
    private double shiftValue = 1;
    
    /** Whether the algorithm is allowed to create new bend points to relax overstretched edges. */
    private boolean enableAdditionalBendPoints = true;
    /** When the bend point angles of an edge are below this threshold the edge is considered overstretched. */
    private double additionalBendPointsThreshold = 100;
    /** How much the additional bend points can distance themselves. Influences the curve height. */
    private double curveHeight = 50;
    /** Factor on how near the helping bend points are positioned for spline visualization. Influences the curve width. */
    private double curveWidthFactor = 0.2;
    /** Enable if the algorithm is allowed to change the bend point distance around nodes. */
    private boolean enableFlexibleNodeRadius = false;
    
    /** Maximum number of iterations of each stress reducing process. */
    private int iterationLimit = 200;
    /** Epsilon for terminating the stress minimizing process. */
    //TODO: Try implementing stress depending termination criterion.
    private double epsilon;

    
    
    @Override
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Initialization:

        // Start progress monitor
        progressMonitor.begin("ELK Knot", 2);
        progressMonitor.log("Knot algorithm start");

        // calculate initial coordinates
        if (!layoutGraph.getProperty(StressOptions.INTERACTIVE)) {
            new ForceLayoutProvider().layout(layoutGraph, progressMonitor.subTask(1));
        } else {
            // If requested, compute nodes's dimensions, place node labels, ports, port labels, etc.
            // Note that for the non-interactive case (above) this will be taken care of by the force layout provider
            if (!layoutGraph.getProperty(StressOptions.OMIT_NODE_MICRO_LAYOUT)) {
                NodeMicroLayout.forGraph(layoutGraph)
                               .execute();
            }
        }
        
        
        // Check for correct amount of edges (2 outgoing + 2 incoming).
        for (ElkNode node : layoutGraph.getChildren()) {
            if (node.getOutgoingEdges().size() != 2 && node.getIncomingEdges().size() != 2) {
                throw new IllegalArgumentException("Invalid amount of edges on node: " + node);
            }
            // Shrink node sizes to appear invisible.
            node.setHeight(0.0000000000001);
            node.setWidth(0.0000000000001);
        }
        
        
        // Apply options.
        this.nodeRadius = layoutGraph.getProperty(KnotOptions.NODE_RADIUS);
        this.shiftThreshold = layoutGraph.getProperty(KnotOptions.SHIFT_THRESHOLD);
        this.desiredNodeDistance = layoutGraph.getProperty(KnotOptions.DESIRED_NODE_DISTANCE);
        this.separateAxisRotation = layoutGraph.getProperty(KnotOptions.SEPARATE_AXIS_ROTATION);
        this.rotationValue = layoutGraph.getProperty(KnotOptions.ROTATION_VALUE);
        this.shiftValue = layoutGraph.getProperty(KnotOptions.SHIFT_VALUE);
        this.enableAdditionalBendPoints = layoutGraph.getProperty(KnotOptions.ENABLE_ADDITIONAL_BEND_POINTS);
        this.additionalBendPointsThreshold = layoutGraph.getProperty(KnotOptions.ADDITIONAL_BEND_POINTS_THRESHOLD);
        this.curveHeight = layoutGraph.getProperty(KnotOptions.CURVE_HEIGHT);
        this.curveWidthFactor = layoutGraph.getProperty(KnotOptions.CURVE_WIDTH_FACTOR);
        this.iterationLimit = layoutGraph.getProperty(KnotOptions.ITERATION_LIMIT);
        
        epsilon = 10e-4;
        
        // -------- Use stress layout as initial layout. (reused section) --------
        // transform the input graph
        IGraphImporter<ElkNode> graphImporter = new ElkGraphImporter();
        FGraph fgraph = graphImporter.importGraph(layoutGraph);

        // split the input graph into components
        List<FGraph> components = componentsProcessor.split(fgraph);

        // perform the actual layout
        for (FGraph subGraph : components) {
            if (subGraph.getNodes().size() <= 1) {
                continue;
            }
            stressMajorization.initialize(subGraph);
            stressMajorization.execute();
            
            // Note that contrary to force itself, labels are not considered during stress layout.
            // Hence, all we can do here is to place the labels at reasonable positions after layout has finished.
            subGraph.getLabels().forEach(label -> label.refreshPosition());
        }

        // pack the components back into one graph
        fgraph = componentsProcessor.recombine(components);
        // apply the layout results to the original graph
        graphImporter.applyLayout(fgraph);
        
        // ----------------------- Stress layout finished -----------------------

        
        // Create the 4 bend points around each node.
        initalizeBendPoints(layoutGraph);
        
        progressMonitor.logGraph(layoutGraph, "Initialization");
        
        
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Main process:
        
        // SETUP ROTATION:
        
        // First improvement that will already reduce the stress significantly.
        for(ElkNode node : layoutGraph.getChildren()) {
            stressMinimizingRotation(node);
        }
        progressMonitor.logGraph(layoutGraph, "Setup rotation");

        
        // STRESS MINIMIZATION:
        
        // The main sequence of moving and rotating nodes in order to reduce stress values and bringing
        // the components into the right position. Multiple iterations improving the results
        int count = 0;
        do {
            
            // Every node in order.
            for(ElkNode node : layoutGraph.getChildren()) {
                // Rotation before shifting had better results.
                stressMinimizingRotation(node);
                
                // If this node has many sharp angles, it could use a shift.
                if (computeNodeAngles(node) < shiftThreshold) {
                    stressMinimizingShift(layoutGraph, node);
                }
            }
            
            progressMonitor.logGraph(layoutGraph, "Stress reduction on iteration " + count);
            count++;

        // Can happen that in one iteration the stress gets worse, but over multiple iterations it gets better.
        // Therefore no further termination criterion.
        } while((count < iterationLimit));
            
        // Closing rotation after the last shift movement:
        for(ElkNode node : layoutGraph.getChildren()) {
            stressMinimizingRotation(node);
        }
        
        
        
        // FURTHER OPTIMIZATIONS:
        
        // Create additional bend points on edges when the existing bend points have a certain angle.
        // Proceed to move the new bend point in order to relax those angles.
        if (enableAdditionalBendPoints) {
            for (ElkNode currNode : layoutGraph.getChildren()) {
                // Consider only outgoing nodes so there are no duplicates.
                for(ElkEdge oEdge : currNode.getOutgoingEdges()) {        
                    addMiddleBendPoint(oEdge);
                }
            }
            progressMonitor.logGraph(layoutGraph, "Additional middle bend points");
        }
        
        if(enableFlexibleNodeRadius) {      
            // TODO: Larger curves by growing nodeRadius until angles hitting a critical range.
            // Individual for each node.
        }
        
        progressMonitor.done(); /**/
    }
    
    
    // TODO: Adjust the image size to the new coordinates.
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Stress computation functions:
    
    /**
     * Computes the stress value of a given node that is created by sharp angles on the bend points of its
     * corresponding edges. The outgoing edges as well as the incoming edges are taken into account.
     * Sharper angles create more stress while angles of 180° would be perfect.
     * 
     * @param node of which the angle stress value is desired.
     * @return The stress value as a Double.
     */
    private double computeAngleStress(ElkNode node) {
       
        double stress = 0;
        double angle = 0;
       
        for (ElkEdge oEdge : node.getOutgoingEdges()) {
            // Calculate angle between source node <- first bend point -> next bend point.
            ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
            // An angle of 180° would cause no stress.
            angle = 180 - calculateBendPointAngle(bp, oEdge);
            // Sharper angles must not be allowed, therefore exponentially causing more stress.
            stress = stress + Math.pow(angle,2);
            
        }
        for (ElkEdge iEdge : node.getIncomingEdges()) {
            // Calculate angle between previous bend point <- last bend point -> target node.
            List<ElkBendPoint> bps = iEdge.getSections().get(0).getBendPoints();
            ElkBendPoint bp = bps.get(bps.size()-1);
            angle = 180 - calculateBendPointAngle(bp, iEdge);
            stress = stress + Math.pow(angle,2);  
        }

        return stress;
    }
    
    
    /**
     * Computes the stress value for a given node that is created by sharp angles on the bend points of only
     * its outgoing edges.
     * Sharper angles create more stress while angles of 180° would be perfect. Additionally, the angle between
     * this axis and the axis of incoming edges creates stress as well.
     * 
     * @param node of which the angle stress value for the outgoing edges is desired.
     * @return The stress value as a Double.
     */
    private double computeOutAxisStress(ElkNode node) {
        
        double stress = 0;
        double angle = 0;
       
        for (ElkEdge oEdge : node.getOutgoingEdges()) {
            // Calculate angle between source node <- first bend point -> next bend point.
            ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
            // An angle of 180° would cause no stress.
            angle = 180 - calculateBendPointAngle(bp, oEdge);
            // Sharper angles must not be allowed, therefore exponentially causing more stress.
            stress = stress + Math.pow(angle,2);
            
        }
        // Additional stress relative to other axis.
        // An angle of 90° between the two axis would be perfect while an angle close to 0° is prohibited.
        angle = 90 - calculateAxisAngle(node);
        stress = stress + Math.pow(angle,2);
        
        return stress;
    }
    
    
    /**
     * Computes the stress value for a given node that is created by sharp angles on the bend points of only
     * its incoming edges.
     * Sharper angles create more stress while angles of 180° would be perfect. Additionally, the angle between
     * this axis and the axis of outgoing edges creates stress as well.
     * 
     * @param node of which the angle stress value for the incoming edges is desired.
     * @return The stress value as a Double.
     */
    private double computeInAxisStress(ElkNode node) {
        
        double stress = 0;
        double angle = 0;
        
        for (ElkEdge iEdge : node.getIncomingEdges()) {
            // Calculate angle between previous bend point <- last bend point -> target node.
            List<ElkBendPoint> bps = iEdge.getSections().get(0).getBendPoints();
            ElkBendPoint bp = bps.get(bps.size()-1);
            // An angle of 180° would cause no stress.
            angle = 180 - calculateBendPointAngle(bp, iEdge);
            // Sharper angles must not be allowed, therefore exponentially causing more stress.
            stress = stress + Math.pow(angle,2);  
        }
        // Additional stress relative to other axis.
        // An angle of 90° between the two axis would be perfect while an angle close to 0° is prohibited.
        angle = 90 - calculateAxisAngle(node);
        stress = stress + Math.pow(angle,2);
        
        return stress;
    }
    
    
    /**
     * Computes the stress value of a given node that is created by distance towards other nodes.
     * The range of the bend points around the nodes are taken into account.
     * 
     * @param graph the whole graph.
     * @param node node of which the distance stress value is desired.
     * @return The stress value as a Double.
     */
    private double computeDistanceStress(ElkNode graph, ElkNode node) { 
        
        double stress = 0;
 
        for (ElkEdge oEdge : node.getOutgoingEdges()) {
            
            // Get neighbor node that are connected via the outgoing edges.
            ElkNode target = (ElkNode) oEdge.getTargets().get(0);
            double dx = target.getX() - node.getX();
            double dy = target.getY() - node.getY();
            
            // Vector from this node to neighbor node.
            KVector distanceVector = new KVector(dx, dy);
            double distance = distanceVector.length();
            
            // Exponentially more stress when node is further away from desired distance.
            stress = stress + Math.pow(Math.abs(distance - desiredNodeDistance), 1.5);
        }
        
        for (ElkEdge iEdge : node.getIncomingEdges()) {
            
            // Get neighbor node that are connected via the incoming edges.
            ElkNode source = (ElkNode) iEdge.getSources().get(0);
            double dx = source.getX() - node.getX();
            double dy = source.getY() - node.getY();
            
            // Vector from this node to neighbor node.
            KVector distanceVector = new KVector(dx, dy);
            double distance = distanceVector.length();
            
            // Exponentially more stress when node is further away from desired distance.
            stress = stress + Math.pow(Math.abs(distance - desiredNodeDistance), 1.5);
        }
        return stress;   
    }
    
    
     /**
     * Calculates the angle at the given bend point between its previous and next points along the given edge.
     * 
     * @param bendPoint of the desired angle.
     * @param edge of the corresponding bend point.
     * @return The angle at the given bend point in degree.
     */
    private double calculateBendPointAngle(ElkBendPoint bendPoint, ElkEdge edge) {
        
        List<ElkBendPoint> bps = edge.getSections().get(0).getBendPoints();
        int i = bps.indexOf(bendPoint);
        double x = bendPoint.getX();
        double y = bendPoint.getY();
        
        // Vectors from the bend point (x,y) towards the previous and next point.
        KVector prevPoint;
        KVector nextPoint;
        
        // When it is the first bend point the previous point along the edge is the source node.
        if(i == 0) {
            ElkNode sNode = (ElkNode) edge.getSources().get(0);    
            prevPoint = new KVector(sNode.getX() - x, sNode.getY() - y); 
            nextPoint = new KVector(bps.get(i+1).getX() - x, bps.get(i+1).getY() - y);
            
        // When it is the last bend point the next point along the edge is the target node.    
        } else if (i == bps.size()-1) {
            ElkNode tNode = (ElkNode) edge.getTargets().get(0);
            prevPoint = new KVector(bps.get(i-1).getX() - x, bps.get(i-1).getY() - y);    
            nextPoint = new KVector(tNode.getX() - x, tNode.getY() - y);
            
        // Bend point is in between other bend points.
        } else {
            prevPoint = new KVector(bps.get(i-1).getX() - x,bps.get(i-1).getY() - y);   
            nextPoint = new KVector(bps.get(i+1).getX() - x,bps.get(i+1).getY() - y);         
        }
        // Calculate angle between vectors.
        return(Math.toDegrees(nextPoint.angle(prevPoint)));
    }
    
    
    /**
    * Calculates the angle at the given node between its outgoing and incoming edges.
    *
    * @param node of which the angle between the incoming and outgoing edges is desired.
    * @return The angle at the given node in degree.
    */
    private double calculateAxisAngle(ElkNode node) {
        
        double x = node.getX();
        double y = node.getY();
        
        // Vectors from the node (x,y) towards one bend point of each axis.
        KVector prevPoint;
        KVector nextPoint;
        
        // Bend point of the first outgoing edge.
        ElkBendPoint bpo = node.getOutgoingEdges().get(0).getSections().get(0).getBendPoints().get(0);
        // Bend point of the first incoming edge.
        List<ElkBendPoint> bps = node.getIncomingEdges().get(0).getSections().get(0).getBendPoints();
        ElkBendPoint bpi = bps.get(bps.size()-1);
        
        prevPoint = new KVector(bpo.getX() - x, bpo.getY() - y);
        nextPoint = new KVector(bpi.getX() - x, bpi.getY() - y);
        
        // Calculate angle between the two axis.
        return(Math.toDegrees(nextPoint.angle(prevPoint)));
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Stress reducing functions:

    /**
    * Attempt in minimizing the stress value of a given node by rotating its corresponding
    * bend points around the node. Rotates the outgoing and incoming axis separately when enabled.
    *
    * @param node of which the stress value should be reduced by rotation.
    */
    private void stressMinimizingRotation(ElkNode node) {
        
        int count = 0;
        double angle = 0;
        double prevStress = Double.MAX_VALUE;
        
        do {    
            // ROTATION:
            
            // Save previous stress for comparision after rotation.
            prevStress = computeAngleStress(node);
            // The more stress the greater the rotation should be.
            angle = rotationValue*(prevStress / 2500);
            rotateNode(node, angle);
            
            // When the new stress is larger, turn back.
            if (prevStress < computeAngleStress(node)) {
                // A greater rotation here allows adjustments in both directions.
                rotateNode(node, - 2*angle);
            }
            
            
            // AXIS ROTATION when enabled:
            
            // Whole rotation + individual axis rotation works well (only axis not so well).
            if (separateAxisRotation) {
                 
                prevStress = computeOutAxisStress(node);
                // The more stress the greater the rotation should be.
                angle = rotationValue*(prevStress / 10000);
                rotateOutgoingAxis(node, angle);
                
                // When the new stress is larger, turn back.
                if (prevStress < computeOutAxisStress(node)) {
                    rotateOutgoingAxis(node, - 2*angle);
                }
            
                // Stress of incoming axis depends on the freshly changed angle of the outgoing axis.
                prevStress = computeInAxisStress(node);
                angle = rotationValue*(prevStress / 10000);
                rotateIncomingAxis(node, angle);
            
                // When the new stress is larger, turn back.
                if (prevStress < computeInAxisStress(node)) {
                    rotateIncomingAxis(node, - 2*angle);
                }
            }
    
            count++;   
        } while(count < iterationLimit && epsilon < 2);
    }
    
    
    /**
    * Attempt in minimizing the stress value of a given node by shifting its position and the corresponding
    * bend points around the node.
    *
    * @param graph the whole graph.
    * @param node of which the stress value should be reduced by shift movements.
    */
    private void stressMinimizingShift(ElkNode graph, ElkNode node) { 
    
        int count = 0;
        double prevStress = Double.MAX_VALUE;
        
        do { 
            // MOVEMENT on x-Axis:
            
            // Stress depends on the angles of bend points + position relative to connected nodes.
            prevStress = computeAngleStress(node) + computeDistanceStress(graph, node);

            moveNode(node, node.getX() + shiftValue, node.getY());
            
            // When the new stress is larger, shift back.
            if (prevStress < (computeAngleStress(node) + computeDistanceStress(graph, node))) {
                // A greater movement here allows adjustments in both directions.
                moveNode(node, node.getX() - 2*shiftValue, node.getY());
            }
            
            // MOVEMENT on y-Axis:
            
            // Compute stress anew with freshly changed position of the node.
            prevStress = computeAngleStress(node) + computeDistanceStress(graph, node);
            moveNode(node, node.getX(), node.getY() + shiftValue);
            
            // When the new stress is larger, shift back.
            if (prevStress < (computeAngleStress(node) + computeDistanceStress(graph, node))) {
                moveNode(node, node.getX(), node.getY() - 2*shiftValue);
            }
            
            count++;   
        } while(count < iterationLimit); 
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Additional improvement functions:

    /**
    * Adds an additional bend point on the middle of a given edge if the angle on the outer bend points
    * are to sharp and below the 'additionalBendPointsThreshold'.
    * 
    * @param edge to which an additional middle bend point is desired.
    */
    private void addMiddleBendPoint(ElkEdge edge) {
        
        // Checks if the outer bend point angles are below the threshold.
        if (needsMidPoint(edge)){

            List<ElkBendPoint>  bps = edge.getSections().get(0).getBendPoints();
            
            double x1 = bps.get(0).getX();
            double y1 = bps.get(0).getY();
            double x2 = bps.get(bps.size()-1).getX();
            double y2 = bps.get(bps.size()-1).getY();
            
            // Initial position is in the center of the edge.
            double x = (x1 + x2)/2;
            double y = (y1 + y2)/2;
             
            // Create the bend point and adds it to the list in between the others.
            ElkBendPoint middleBp = ElkGraphUtil.createBendPoint(null, x, y); 
            bps.add(1, middleBp);
            
            // Shift the new bend point to relax angles of others but keep an equal distance to each side.
            shiftBendPoint(bps.get(1), edge);
                
            
            // Further create 2 bend points to help visualizing smooth splines.
            
            // After the creation of an additional middle bend point, the final visualization consist of
            // 5 Points in total. For the creation of a bézier curve, these get split up into a 3-point and
            // a 2-point bézier curve, resulting in having a smooth curve into a sharp edge.
            // These 2 helper bend points will be opposite of each other next to the new middle point and
            // therefore using the same technique to have a smooth edge transition like the nodes.
            
            ElkBendPoint helperBp1 = ElkGraphUtil.createBendPoint(null, x, y); 
            ElkBendPoint helperBp2 = ElkGraphUtil.createBendPoint(null, x, y); 
            
            // Use vector to mimic the direction from first bp to last bp.
            KVector directionVector = new KVector(x2 - x1, y2 - y1);
            // Distance from the middle bend point to the helpers, large factor results in wider curves.
            directionVector.scale(curveWidthFactor);
            
            // Position helper bend points opposite of each other next to the new middle bend point.
            helperBp1.set(bps.get(1).getX() + directionVector.x, bps.get(1).getY() + directionVector.y);
            directionVector.scale(-1);
            helperBp2.set(bps.get(1).getX() + directionVector.x, bps.get(1).getY() + directionVector.y);
            
            // Adds them to the list in the right order.
            bps.add(2, helperBp1);
            bps.add(1, helperBp2);
        }
    }
    
    
    /**
    * Whether an given edge needs an additional bend point helping to relax the angles on the outer bend points.
    *
    * @param edge in question.
    * @return If the given edge needs a middle bend point.
    */
    private boolean needsMidPoint(ElkEdge edge) {
        
        List<ElkBendPoint> bps = edge.getSections().get(0).getBendPoints();
        
        // Edge needs a middle bend point when the angle of the outer bend points is below a desire threshold.
        ElkBendPoint FirstBp = bps.get(0);
        ElkBendPoint LastBp = bps.get(bps.size()-1);
        
        return (calculateBendPointAngle(FirstBp, edge) <= additionalBendPointsThreshold 
                && calculateBendPointAngle(LastBp, edge) <= additionalBendPointsThreshold);
    }
    
    
    /**
    * Perform a stress reducing shift for a bend point, depending on the angles and distances
    * of the outer bend points.
    * 
    * @param bp to shift.
    * @param edge of corresponding bend point.
    */
    private void shiftBendPoint(ElkBendPoint bp, ElkEdge edge) {
         
        List<ElkBendPoint> bps = edge.getSections().get(0).getBendPoints();
        int bpIndex = bps.indexOf(bp);
        
        // Only shifts bend point if it is not one of the outer ones.
        if (bpIndex != 0 && bpIndex != bps.size()-1) {
                    
            double x = bp.getX();
            double y = bp.getY();
            double prevStress = 0;
            
            // Remember start position calculate the height of the new bend point.
            ElkBendPoint startPos = ElkGraphUtil.createBendPoint(null, x, y);
                
            // Shift middle bend point.
            int count = 0;
            do {
                // MOVEMENT on x-Axis:
                
                // Stress depends on the angles of outer bend points + equal distance towards them.
                prevStress = bendPointStress(bp, edge);
                bp.setX(x+1);
                
                // When angle smaller or distance to other bps to great, shift back.
                if (bendPointStress(bp, edge) > prevStress) {
                    bp.setX(x-2);  
                }
                x = bp.getX();
                
                
                // MOVEMENT on y-Axis:
                
                // Compute stress anew with freshly changed position of the bend point.
                prevStress = bendPointStress(bp, edge);
                bp.setY(y+1);
                        
                // When angle smaller or distance to other bps to great, shift back.
                if (bendPointStress(bp, edge) > prevStress) {
                    bp.setY(y-2);
                }   
                y = bp.getY();
                
                count++;   
                
                // How much the bend point is allowed distance itself (the bigger the curve gets).
                // Otherwise it would relax the angles by keep shifting further away.
            } while(count < iterationLimit && (calculateBendPointDistance(bp, startPos) <= curveHeight));
            
            bp.setX(x);
            bp.setY(y);
        }
    }
    
    
    /**
    * Stress of a single bend point depending on the angles and distances towards the previous and next bend point.
    * 
    * @param bp the bend point of which the stress value is desired.
    * @param edge of the given bend point.
    * @return The stress value as a Double.
    */
    private double bendPointStress(ElkBendPoint bp, ElkEdge edge) {
        
        double stress = 0;
        List<ElkBendPoint> bps = edge.getSections().get(0).getBendPoints();
        int bpIndex = bps.indexOf(bp);
        
        // Only consider bend point that are in between of other bend points (not the outer ones).
        if (bpIndex != 0 && bpIndex != bps.size()-1) {
            // Get previous and next bend point in order.
            ElkBendPoint prevBendPoint = bps.get(bpIndex - 1);
            ElkBendPoint nextBendPoint = bps.get(bpIndex + 1);

            // Angles of the neighbor bend points.
            double anglePrevBp = calculateBendPointAngle(prevBendPoint, edge);
            double angleNextBp = calculateBendPointAngle(nextBendPoint, edge);
            // Distances towards the neighbor bend points.
            double distPrevBp = calculateBendPointDistance(bp, prevBendPoint);
            double distNextBp = calculateBendPointDistance(bp, nextBendPoint);
            
            // Sharper angles must not be allowed, therefore exponentially causing more stress.
            // Angle of 180° would be best, even if it is not achievable.
            stress = Math.pow((180 - anglePrevBp), 2) + Math.pow((180 - angleNextBp), 2);
            
            // It's desired to have equal distances towards neighbor bend points.
            stress = stress + Math.pow(Math.abs(distNextBp - distPrevBp), 3);

        }   
        return stress;
    }
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helping functions:
    
    /**
     * Initialize bend points around a node for each of it's 4 edges.
     * @param graph the node that contains the graph.
     */
    private void initalizeBendPoints(ElkNode graph) {

        // Initialize bend points for outgoing edges around nodes. They need to get placed first.
        for (ElkNode currNode : graph.getChildren()) {  
            // First bp is below node, second bp is above node.
            for (int i = 0; i < 2; i++) {

                double d = nodeRadius * Math.pow(-1, i);
                
                // Outgoing edges share initially the same x coordinate as the node.
                ElkEdge oEdge = currNode.getOutgoingEdges().get(i);
                // We don't have hyperedges and therefore only one section.
                ElkGraphUtil.createBendPoint(oEdge.getSections().get(0), currNode.getX(), currNode.getY()+d);
            }
        }
         
        // Initialize bend points for incoming edges around nodes. They need to get placed last.
        for (ElkNode currNode : graph.getChildren()) {
            // First bp is right of node, second bp is left of node
            for (int i = 0; i < 2; i++) {
            
                double d = nodeRadius * Math.pow(-1, i);

                // Outgoing edges share initially the same y coordinate as the node.
                ElkEdge iEdge = currNode.getIncomingEdges().get(i);
                // We don't have hyperedges and therefore only one section.
                ElkGraphUtil.createBendPoint(iEdge.getSections().get(0), currNode.getX()+d, currNode.getY());
            }
        }
    }
    
    
    /**
     * Rotates the 4 bend points around a given node by the angle provided in degrees, creating the illusion
     * of actual rotating the whole node. Rotation is clockwise. 
     * 
     * @param node to rotate.
     * @param angle in degree.
     * */
    private void rotateNode(ElkNode node, double angle) {
        // Perform rotation on both axis simultaneously.
        rotateOutgoingAxis(node, angle);
        rotateIncomingAxis(node, angle); 
    }
    
    
    /**
     * Rotates the axis of outgoing edges for the given node by the angle provided in degrees. Rotation is clockwise.
     * @param node of which the outgoing axis will rotate.
     * @param angle in degree.
     * */
    private void rotateOutgoingAxis(ElkNode node, double angle) {
        for (ElkEdge oEdge : node.getOutgoingEdges()) {
            // For outgoing edges, the first bend point needs to rotate.
            ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
            rotateBendPointAroundPoint(bp, node.getX(), node.getY(), angle); 
        }  
    }
    
    
    /**
     * Rotates the axis of incoming edges for the given node by the angle provided in degrees. Rotation is clockwise.
     * @param node of which the incoming axis will rotate.
     * @param angle in degree.
     * */
    private void rotateIncomingAxis(ElkNode node, double angle) {
        for (ElkEdge iEdge : node.getIncomingEdges()) {
            // For incoming edges, the last bend point needs to rotate.
            List<ElkBendPoint> bps = iEdge.getSections().get(0).getBendPoints();
            ElkBendPoint bp = bps.get(bps.size()-1);
            rotateBendPointAroundPoint(bp, node.getX(), node.getY(), angle);
        }
    }
    
    
    /**
     * Rotates the given bend point around another point with given coordinates by the angle provided
     * in degrees. Rotation is clockwise.
     * 
     * @param bendPoint to rotate.
     * @param x coordinate of the rotation point.
     * @param y coordinate of the rotation point.
     * @param angle in degree.
     * */
    private void rotateBendPointAroundPoint(ElkBendPoint bendPoint, double x, double y, double angle) {
        // Shift rotation point to origin.
        double bpx = bendPoint.getX() - x;
        double bpy = bendPoint.getY() - y;

        double sin = Math.sin(Math.toRadians(angle));
        double cos = Math.cos(Math.toRadians(angle));
        // Rotate bend point around origin, then shift back to new position.
        bendPoint.setX((bpx * cos - bpy * sin) + x);
        bendPoint.setY((bpy * cos + bpx * sin) + y);
    }
    

    /**
    * Move the given node with its 4 bend points to the given coordinates.
    * @param node to move.
    * @param x the destined position on x-axis
    * @param y the destined position on y-axis
    */
    private void moveNode(ElkNode node, double x, double y) {
        
        // Distance towards the destined location.
        double distX = x - node.getX();
        double distY = y - node.getY();
        
        // Move bend points of outgoing edges.
        for (ElkEdge oEdge : node.getOutgoingEdges()) {
            ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
            bp.set(bp.getX() + distX, bp.getY() + distY);
        }
        
        // Move bend points of incoming edges.
        for (ElkEdge iEdge : node.getIncomingEdges()) {
            List<ElkBendPoint>  bps = iEdge.getSections().get(0).getBendPoints();
            ElkBendPoint bp = bps.get(bps.size()-1);
            bp.set(bp.getX() + distX, bp.getY() + distY); 
        }
        
        // Move node.
        node.setLocation(x, y);
    }
    
    
    /**
    * Calculates the distance between two given bend points.
    * @param first bend point.
    * @param second bend point.
    * @return The distance as a Double.
    */
    private double calculateBendPointDistance(ElkBendPoint bp1, ElkBendPoint bp2) {
        
        // Difference between points.
        double dx = bp2.getX() - bp1.getX();
        double dy = bp2.getY() - bp1.getY();

        // Vector from this bp to other bp.
        KVector distanceVector = new KVector(dx, dy);
        return distanceVector.length(); 
    }
    
    
    /**
    * Sums up the angles at the four bend points around the given node.
    * @param node of which the angles are desired.
    * @return The sum of all four angles in degree as a Double.
    */
    private double computeNodeAngles(ElkNode node) {
        
        double sum = 0;
        
        for (ElkEdge oEdge : node.getOutgoingEdges()) {
            // Calculate angle between source node <- first bend point -> next bend point.
            ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
            sum = sum + calculateBendPointAngle(bp, oEdge);
            
        }
        for (ElkEdge iEdge : node.getIncomingEdges()) {
            // Calculate angle between previous bend point <- last bend point -> target node.
            List<ElkBendPoint> bps = iEdge.getSections().get(0).getBendPoints();
            ElkBendPoint bp = bps.get(bps.size()-1);
            sum = sum + calculateBendPointAngle(bp, iEdge);
        }
        return sum;
    }
 
}



