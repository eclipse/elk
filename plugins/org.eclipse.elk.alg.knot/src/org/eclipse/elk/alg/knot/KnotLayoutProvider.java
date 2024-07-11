package org.eclipse.elk.alg.knot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.eclipse.elk.alg.common.NodeMicroLayout;
import org.eclipse.elk.alg.force.ComponentsProcessor;
import org.eclipse.elk.alg.force.ElkGraphImporter;
import org.eclipse.elk.alg.force.ForceLayoutProvider;
import org.eclipse.elk.alg.force.IGraphImporter;
import org.eclipse.elk.alg.force.graph.FBendpoint;
import org.eclipse.elk.alg.force.graph.FEdge;
import org.eclipse.elk.alg.force.graph.FGraph;
import org.eclipse.elk.alg.force.graph.FNode;
import org.eclipse.elk.alg.force.options.ForceOptions;
import org.eclipse.elk.alg.force.options.StressOptions;
import org.eclipse.elk.alg.force.stress.StressMajorization;
import org.eclipse.elk.alg.knot.options.KnotOptions;

/**
 * Layout provider for the knot layout algorithm. This algorithm places nodes and routes edges in order to modify
 * positions further via stress minimization.
 */
public class KnotLayoutProvider extends AbstractLayoutProvider {

    /** connected components processor. */
    private ComponentsProcessor componentsProcessor = new ComponentsProcessor();
    /** implementation of stress majorization. */
    private StressMajorization stressMajorization = new StressMajorization();
    
    /** the distance which bend points around nodes must preserve. */
    private final double bendPointDistance = 25;
    
    @Override
    public void layout(final ElkNode layoutGraph, final IElkProgressMonitor progressMonitor) {
        
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
        
        
        
        for (ElkNode node : layoutGraph.getChildren()) {
            if ((node.getIncomingEdges().size() + node.getOutgoingEdges().size()) != 4) {
                // Abort because crossings always contain 4 edges.
            }
            // Shrink node sizes.
            node.setHeight(1);
            node.setWidth(1);
        }
        
        // transform the input graph
        IGraphImporter<ElkNode> graphImporter = new ElkGraphImporter();
        FGraph fgraph = graphImporter.importGraph(layoutGraph);

        
        
        // Set bend points.
        // Problem: Bendpoints lassen sich nicht positionieren, fnode hat keine Liste seiner Edges mehr
        // Evtl muss eigenen KnotGraphen machen oder auf ELKNodes setzen
        // Layered Layouts Version nutzen? -> Irgendwie schwer durchzuschauen, nutzen unsichtbare nodes fuer routing
        // SplineEdgeRoute ? Geht nur mit Layered Layout.
        
        // FGraph Importer erzeugt eigene FBendpoints (ubernimmt keine exisiterenden) aber wandelt sie dann in
        // ELKBEndpoints um beim apply Layout
        // ELKBendPoint braucht die Section der Edge
        

        
        /*
        // Route the edges
        if (!layoutGraph.getContainedEdges().isEmpty()) {
            currY += edgeNodeSpacing;
            edgeRoutingMonitor.log("currY: " + currY);
            
            for (ElkEdge edge : layoutGraph.getContainedEdges()) {
                ElkNode source = ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));
                ElkNode target = ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));
                
                ElkEdgeSection section = ElkGraphUtil.firstEdgeSection(edge, true, true);
                
                section.setStartLocation(
                        source.getX() + source.getWidth() / 2,
                        source.getY() + source.getHeight());
                section.setEndLocation(
                        target.getX() + target.getWidth() / 2,
                        target.getY() + target.getHeight());
                
                ElkGraphUtil.createBendPoint(section, section.getStartX(), currY);
                ElkGraphUtil.createBendPoint(section, section.getEndX(), currY);
                                
                currY += edgeEdgeSpacing;
                edgeRoutingMonitor.log("currY: " + currY);
                edgeRoutingMonitor.logGraph(layoutGraph, source.getIdentifier() + " -> " + target.getIdentifier());
            }
            
            currY -= edgeEdgeSpacing;
        }
        */
        

        
        
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

        
        ///////////////////////////////////////////////////////////////////////////
        // Bend point positioning
        
        
        // The 4 bend points around each node.
        initalizeBendPoints(layoutGraph);
        
        
        

        

        
        
        
        // TODO: Creating stress from angles between bend points of different nodes --> Adjust rotation.
        
        // Test rotation 110, 55, -10
        rotateNode(layoutGraph.getChildren().get(0), 0);
        rotateNode(layoutGraph.getChildren().get(1), 0); 
        rotateNode(layoutGraph.getChildren().get(2), 0);
        
        //rotateOutgoingAxis(layoutGraph.getChildren().get(0), 45);
        //rotateNode(layoutGraph.getChildren().get(0), -45);
        
        // Testing angle calculation of node 2:
        for (ElkEdge e : layoutGraph.getChildren().get(2).getOutgoingEdges()) {
            ElkBendPoint bp =e.getSections().get(0).getBendPoints().get(0);

            System.out.println("o bp = " + calculateBendPointAngle(bp, e));
        }
        System.out.println("-----------------");
        for (ElkEdge e : layoutGraph.getChildren().get(2).getIncomingEdges()) {
            ElkBendPoint bp =e.getSections().get(0).getBendPoints().get(1);
            System.out.println("i bp = " + calculateBendPointAngle(bp, e));
        }
        System.out.println("-----------------");
        // Testing stress calculation:
        System.out.println("Befor stress n1: " + computeNodeStress(layoutGraph.getChildren().get(0)));
        System.out.println("Befor stress n2: " + computeNodeStress(layoutGraph.getChildren().get(1)));
        System.out.println("Befor stress n3: " + computeNodeStress(layoutGraph.getChildren().get(2)));
        
        

        
        
        //TODO: When bend points still have sharp angles --> add another bend point in between
        //TODO: Problem: Where to put to avoid collision? --> Taking all nodes into account?
        //      Position der nodes + Kontrollpunktabstand + Puffer als Radius zum avoiden.
        //      Richtungen betrachten von nodes zu ersten Kontrollpunkten, in diese Richtungen
        //      auch andere nodes checken
        
        
        // Letzte Moeglichkeit
        //TODO: Kontrollpunkt in groesser werdender Spirale bewegen, position behalten, wenn die Linie zu
        //      Nachbarpunkten weniger Schnittpunkte mit anderen Kanten aufweist
        //      2 Edges checken indem durch alle ihre Kontrollpunkt-Segmente durchiteriert wird.
        
        
        
        // INITIAL ROTATION
        for(ElkNode n : layoutGraph.getChildren()) {
            //stressMinimizingShift(n);
            stressMinimizingRotation(n);
        }
        /*
        */

        
        
        // nochmal rotieren?
        
        
        //count = 0;
        //prevStress = Double.MAX_VALUE;
        //angle =  1;
        
        /*
        do { 
            for (ElkNode currNode : layoutGraph.getChildren()) {
                
                
                // The more stress the greater the rotation should be
                prevStress = computeNodeStress(currNode);
                angle = prevStress / 10000;
                rotateNode(currNode, angle);
                
                // When new stress larger, turn back
                if (prevStress < computeNodeStress(currNode)) {
                    // -2 even better
                    rotateNode(currNode, - 2*angle);

                }

            }
            count++;   
        } while(count < 3600);
        
        */
        
        //TODO: Shifting nodes when most angles at their bend points are sharp
        
        
        // STRESS MINIMIZATION:
        
        
        int count = 0;
        do {
            
            for(ElkNode n : layoutGraph.getChildren()) {
                
                // This node has many sharp angles, could use a shift.
                if (computeNodeAngles(n) < 420) {
                    stressMinimizingShift(n);
                }
                
                stressMinimizingRotation(n);
            }
            
            count++;
        } while(count < 5);
        /*
        */

        ////////
        // Create additional pylons- ehm... bend points
        /*
        double angleThreshold = 85;
        
        for (ElkNode currNode : layoutGraph.getChildren()) {
        
            for(ElkEdge oEdge : currNode.getOutgoingEdges()) {
                
                
                
                
                ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);

                if(needsMidPoint(oEdge, angleThreshold)) {
                    
                    System.out.println("Node: " + currNode + " vordert bp fuer o an");
                    addMiddleBendPoint(oEdge);
                    shiftMiddlePoint(oEdge);
                }
                    
             }
                
         
            /*
            for(ElkEdge iEdge : currNode.getIncomingEdges()) {
                
                List<ElkBendPoint> bps = iEdge.getSections().get(0).getBendPoints();
                ElkBendPoint bp = bps.get(bps.size()-1);
                

                    
                if(needsMidPoint(iEdge, angleThreshold)) {
                    System.out.println("Node: " + currNode + " vordert bp fuer i an");
                    addMiddleBendPoint(iEdge);
                }
                    
                
            }
           
        }
        */
        
        
        
        System.out.println("-----------------");
        // Testing stress calculation:
        System.out.println("After stress n1: " + computeNodeStress(layoutGraph.getChildren().get(0)));
        System.out.println("After stress n2: " + computeNodeStress(layoutGraph.getChildren().get(1)));
        System.out.println("After stress n3: " + computeNodeStress(layoutGraph.getChildren().get(2)));
        
        
        progressMonitor.done();
    }
    
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Helping functions:
    
    
    /**
     * Initialize bend points around a node for each of it's 4 edges.
     * @param graph the node that contains the graph.
     */
    private void initalizeBendPoints(ElkNode graph) {

        // Initialize bend points for outgoing edges around nodes. They need to get placed first.
        for (ElkNode currNode : graph.getChildren()) {
            
            // Check if node has correct amount of outgoing edges (always 2)
            // There should be always 4 edges per node: 2 outgoing + 2 incoming.
            checkEdgeCondition(currNode);
            
            
            // First below node, second above node.
            for (int i = 0; i < 2; i++) {

                double d = bendPointDistance * Math.pow(-1, i);
                
                
                
                
                // Outgoing edges share initially the same x coordinate as the node.
                ElkEdge oEdge = currNode.getOutgoingEdges().get(i);
                // We don't have hyperedges and therefore only one section.
                ElkGraphUtil.createBendPoint(oEdge.getSections().get(0), currNode.getX(), currNode.getY()+d);
                
                // TODO: Extra bend point.
                //ElkNode target = (ElkNode) oEdge.getTargets().get(0);
                //ElkGraphUtil.createBendPoint(oEdge.getSections().get(0), currNode.getX(), target.getY()); 
            }
        }
         
        // Initialize bend points for incoming edges around nodes. They need to get placed last.
        for (ElkNode currNode : graph.getChildren()) {
   
            // First right of node, second left of node
            for (int i = 0; i < 2; i++) {
            
            
                double d = bendPointDistance * Math.pow(-1, i);
                
                
                // Outgoing edges share initially the same y coordinate as the node.
                ElkEdge iEdge = currNode.getIncomingEdges().get(i);
                // We don't have hyperedges and therefore only one section.
                ElkGraphUtil.createBendPoint(iEdge.getSections().get(0), currNode.getX()+d, currNode.getY());
                
                // TODO: Extra bend point.
                //ElkNode target = (ElkNode) iEdge.getTargets().get(0);
                //ElkGraphUtil.createBendPoint(iEdge.getSections().get(0), target.getX(), target.getY());
  
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
        // Check for correct amount of edges (2 outgoing).
        if (node.getOutgoingEdges().size() == 2) {
            for (ElkEdge oEdge : node.getOutgoingEdges()) {
                // For outgoing edges, the first bend point needs to rotate.
                ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
                rotateBendPointAroundPoint(bp, node.getX(), node.getY(), angle); 
            }  
        }
    }
    
    
    /**
     * Rotates the axis of incoming edges for the given node by the angle provided in degrees. Rotation is clockwise.
     * @param node of which the incoming axis will rotate.
     * @param angle in degree.
     * */
    private void rotateIncomingAxis(ElkNode node, double angle) {
        // Check for correct amount of edges (2 incoming).
        if (node.getIncomingEdges().size() == 2) {
            for (ElkEdge iEdge : node.getIncomingEdges()) {
                // For incoming edges, the last bend point needs to rotate.
                List<ElkBendPoint> bps = iEdge.getSections().get(0).getBendPoints();
                ElkBendPoint bp = bps.get(bps.size()-1);
                rotateBendPointAroundPoint(bp, node.getX(), node.getY(), angle);
            }
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
     * Calculates the angle at the given bend point between its previous and next points the along the given edge.
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
    
    
    private double calculateAxisAngle(ElkNode node) {
        
        double x = node.getX();
        double y = node.getY();
        
        // Vectors from the node (x,y) towards the bend points of each axis.
        KVector prevPoint;
        KVector nextPoint;
        
        ElkBendPoint bpo = node.getOutgoingEdges().get(0).getSections().get(0).getBendPoints().get(0);
        
        List<ElkBendPoint> bps = node.getIncomingEdges().get(0).getSections().get(0).getBendPoints();
        ElkBendPoint bpi = bps.get(bps.size()-1);
        
        prevPoint = new KVector(bpo.getX() - x, bpo.getY() - y);
        nextPoint = new KVector(bpi.getX() - x, bpi.getY() - y);
        
        // Calculate angle between vectors.
        return(Math.toDegrees(nextPoint.angle(prevPoint)));
    }
    
    
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
    
    
    /**
     * Computes the stress value of a given node that is created by sharp angles of its corresponding edges.
     * The outgoing edges as well as the incoming edges are taken into account. Sharper angles create more stress
     * while angles of 180° would be perfect.
     * 
     * @param node of which the stress value is desired.
     * @return The stress value as a Double.
     */
    private double computeNodeStress(ElkNode node) {
       
        checkEdgeCondition(node);
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
     * Computes the stress value for a given node that is created by sharp angles of only its outgoing edges.
     * Sharper angles create more stress while angles of 180° would be perfect.
     * 
     * @param node of which the stress value for the outgoing edges is desired.
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
        angle = 90 - calculateAxisAngle(node);
        stress = stress + Math.pow(angle,2);
        
        return stress;
    }
    
    
    /**
     * Computes the stress value for a given node that is created by sharp angles of only its incoming edges.
     * Sharper angles create more stress while angles of 180° would be perfect.
     * 
     * @param node of which the stress value for the incoming edges is desired.
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
        angle = 90 - calculateAxisAngle(node);
        stress = stress + Math.pow(angle,2);
        
        return stress;
    }
    
    /**
     * Whether an given edge is so overstreched that it needs an additional bend point to relax the angles.
     * @param edge in question.
     * @param angleThreshold when the edge is considered overstreched.
     * @return If the edge needs a middle bend point.
     */
    private boolean needsMidPoint(ElkEdge edge, double angleThreshold) {
        
        List<ElkBendPoint> bps = edge.getSections().get(0).getBendPoints();
        ElkBendPoint FirstBp = bps.get(0);
        ElkBendPoint LastBp = bps.get(bps.size()-1);
        
        return (calculateBendPointAngle(FirstBp, edge) <= angleThreshold 
                && calculateBendPointAngle(LastBp, edge) <= angleThreshold);
    }
    
    
    // Testing
    private void addMiddleBendPoint(ElkEdge edge) {
        
        
        List<ElkBendPoint>  bps = edge.getSections().get(0).getBendPoints();
        
        ElkBendPoint firstBp = bps.get(0);
        ElkBendPoint lastBp = bps.get(bps.size()-1); 
        
        double x1 = firstBp.getX();
        double y1 = firstBp.getY();
        double x2 = lastBp.getX();
        double y2 = lastBp.getY();

        
        // Initial position in between.
        double x = (x1 + x2)/2;
        double y = (y1 + y2)/2;
        
        /*
        if (x1 < x2) {
            x = x1;
        } else {
            x = x2;
        }
        
        if (y1 < y2) {
            y = y2;
        } else {
            y = y1;
        }
        */
        //TODO: von source -> bp1 -> newpos -> bpLast -> target
        
        
        // Computing bend point position for optimal angle relaxation.
        int count = 0;
        
        double prevAngle1;
        double prevAngle2;
        
        
        /*
        do { 
            
            
            prevAngle1 = calculateBendPointAngle(firstBp, edge);
            prevAngle2 = calculateBendPointAngle(lastBp, edge);
            
            
            x = x+2;
            if(x > 90) {
                x = x-2;
            }
            
            
            // Revert when one of the angles getting even smaller.
            if(prevAngle1 >= calculateBendPointAngle(firstBp, edge) || 
                    prevAngle2 >= calculateBendPointAngle(lastBp, edge)) {
                x = x - 3;
               
            }
            
            /*
            y = y+2;
            // Revert when one of the angles getting even smaller.
            if(prevAngle1 >= calculateBendPointAngle(firstBp, edge) || 
                    prevAngle2 >= calculateBendPointAngle(lastBp, edge)) {
                y = y - 4;
               
            }
                
            
            
            
            count++;   
        } while(count < 100);
        */
        
        
        
        
        ElkBendPoint newBp = ElkGraphUtil.createBendPoint(null, x, y); 
        
        bps.remove(lastBp);
        bps.add(newBp);
        bps.add(lastBp);
        
        
        
        
    }
    
    private void shiftMiddlePoint(ElkEdge edge) {
        
        
        // TODO: Schnittpunkte berechnen -> schieben bis keine Schnittpunkte mit anderen Kanten
        
        ElkNode source = (ElkNode) edge.getSources().get(0);
        ElkNode target = (ElkNode) edge.getTargets().get(0);
        
        ElkBendPoint bp = edge.getSections().get(0).getBendPoints().get(1);
        
        // Computing bend point position for optimal angle relaxation.
        int count = 0;
        
        double stressSource;
        double stressTarget;
        double x = bp.getX();
        double y = bp.getY();
        
        do { 
            
            
            // The more stress the greater the rotation should be
            stressSource = computeNodeStress(source);
            stressTarget = computeNodeStress(target);
            
            System.out.println("Count: " + count);
            System.out.println("Stress S = " + stressSource);
            System.out.println("Stress T = " + stressTarget);
            System.out.println("Shift um x");
            bp.setX(x+2);

            System.out.println("Neu Stress S = " + computeNodeStress(source));
            System.out.println("Neu Stress T = " + computeNodeStress(target));
            
            

            
            
            //TODO: Problem: wird zu ausschweifend, Winkel werden kleiner und Stress weniger aber Schlaufe riesig.
            // --> Einfach Iteration begrenzen? Funktioniert bislag
            //TODO: Andere Nodes avoiden.
            // --> Schnittpunkte mit anderen Kanten. Knoten umgehen, dessen meisten Kanten Schnittpunkte erzeugen?
            
            
            // When new stress larger, turn back
            if (stressSource < computeNodeStress(source) || stressTarget < computeNodeStress(target)) {
                // -2 even better
                bp.setX(x-3);
                System.out.println("Shift back");

            }
            System.out.println("---------");
            x = bp.getX();
            
            
            
            stressSource = computeNodeStress(source);
            stressTarget = computeNodeStress(target);
            
            bp.setY(y+2);

            // When new stress larger, turn back
            if (stressSource < computeNodeStress(source) || stressTarget < computeNodeStress(target)) {
                // -2 even better
               bp.setY(y-3);

            }
            y = bp.getY();
            
            
            /*
            y = y+2;
            // Revert when one of the angles getting even smaller.
            if(prevAngle1 >= calculateBendPointAngle(firstBp, edge) || 
                    prevAngle2 >= calculateBendPointAngle(lastBp, edge)) {
                y = y - 4;
               
            }
                */
            
            
            
            count++;   
        } while(count < 10);
        
        bp.setX(x);
        bp.setY(y);
        
        
    }
    
    
    private void stressMinimizingShift(ElkNode node) { 
        
        int count = 0;
        double prevStress = Double.MAX_VALUE;
        
        do { 
            
            
            //TODO: Bend Point Abstand beruecksichtigen.
            //TODO: Evtl andere Nodes wegdruecken um sich dazwischen zu packen.
            
            
            // The more stress the greater the rotation should be
            prevStress = computeNodeStress(node);
            moveNode(node, node.getX() + 2, node.getY());
            
            // When new stress larger, turn back
            if (prevStress < computeNodeStress(node)) {
                // -2 even better
                moveNode(node, node.getX() - 3, node.getY());

            }
            
            prevStress = computeNodeStress(node);
            moveNode(node, node.getX(), node.getY() + 2);
            
            // When new stress larger, turn back
            if (prevStress < computeNodeStress(node)) {
                // -2 even better
                moveNode(node, node.getX(), node.getY() - 3);

            }
            
            
            
            count++;   
        } while(count < 100);
        
        
        
    }
    
    /**
     * Move node with its 4 bend points.
     * @param node
     * @param x
     * @param y
     */
    private void moveNode(ElkNode node, double x, double y) {
        
        
        double distX = x - node.getX();
        double distY = y - node.getY();
        
        
        for (ElkEdge oEdge : node.getOutgoingEdges()) {
            
            
            ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
            bp.set(bp.getX() + distX, bp.getY() + distY);
            
        }
        
        for (ElkEdge iEdge : node.getIncomingEdges()) {
            
            List<ElkBendPoint>  bps = iEdge.getSections().get(0).getBendPoints();
            ElkBendPoint bp = bps.get(bps.size()-1);
            bp.set(bp.getX() + distX, bp.getY() + distY);
            
        }
        
        node.setLocation(x, y);
        
        
    }
    
    
    private void stressMinimizingRotation(ElkNode node) {
        int count = 0;
        double prevStress = Double.MAX_VALUE;
        double angle =  1;
        
        do { 
            
            
            // The more stress the greater the rotation should be
            prevStress = computeNodeStress(node);
            angle = prevStress / 4000;
            rotateNode(node, angle);
            
            // When new stress larger, turn back
            if (prevStress < computeNodeStress(node)) {
                // -2 even better
                rotateNode(node, - 2*angle);

            }
            
            
            /* AXIS ROTATION:
            // Whole rotation + individual axis rotation works well (only axis not so well)
            // Somehow, checking node stress for out axis but stress for in axis works good.
            // The more stress the greater the rotation should be
            // TODO: Rather devastating then helpful in its current state.
            
            prevStress = computeOutAxisStress(node);
            angle = prevStress / 10000;
            rotateOutgoingAxis(node, angle);
            
            // When new stress larger, turn back
            if (prevStress < computeNodeStress(node)) {
                // -2 even better
                rotateOutgoingAxis(node, - 2*angle);

            }
            
            // The more stress the greater the rotation should be
            prevStress = computeInAxisStress(node);
            angle = prevStress / 10000;
            rotateIncomingAxis(node, angle);
            
            // When new stress larger, turn back
            if (prevStress < computeInAxisStress(node)) {
                // -2 even better
                rotateIncomingAxis(node, - 2*angle);

            }
            */
            
            
            
        
            count++;   
        } while(count < 3600);
    }
    
    
    
    /**
     * Checks if the edge condition of knot diagrams for a given node is satisfied. A node must always have
     * 2 outgoing edges + 2 incoming edges, representing the overlapping and undergoing edge of a crossing.
     * 
     * @param node to check.
     */
    private void checkEdgeCondition(ElkNode node) {
        // TODO: Causing abort.
        if(node.getOutgoingEdges().size() != 2 && node.getIncomingEdges().size() != 2) {
            System.out.println("Abort");
        }
    }
    
    
}
