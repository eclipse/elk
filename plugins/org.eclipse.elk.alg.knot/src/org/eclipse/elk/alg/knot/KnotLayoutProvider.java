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
        
        
        
        
        
        // Initialize bend points for outgoing edges around nodes. They need to get placed first.
        for (ElkNode currNode : layoutGraph.getChildren()) {
            
            // Check if node has correct amount of outgoing edges (always 2)
            if (currNode.getOutgoingEdges().size() == 2) {
                
                
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
                
            } else {
                System.out.println("Nope");
            }
            

        }
        
        // Initialize bend points for incoming edges around nodes. They need to get placed last.
        for (ElkNode currNode : layoutGraph.getChildren()) {
            
            // Check if node has correct amount of incoming edges (always 2)
            if (currNode.getIncomingEdges().size() == 2) {
            
                
                 // There should be always 4 edges per node: 2 outgoing + 2 incoming.
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
            } else {
                System.out.println("Nope");
            }
        }
        
        
        
        // TODO: Creating stress from angles between bend points of different nodes --> Adjust rotation.
        
        // Test rotation
        rotateNode(layoutGraph.getChildren().get(0), 110);
        rotateNode(layoutGraph.getChildren().get(1), 55); 
        rotateNode(layoutGraph.getChildren().get(2),-10); 
        
        
        progressMonitor.done();
    }
    
    
    
    /**
     * Rotates the 4 bend points around a given node, creating the illusion of actual rotating the node.
     * 
     * @param node to rotate.
     * @param angle in degree.
     * */
    private void rotateNode(ElkNode node, double angle) {
        // Check for correct amount of edges (2 outgoing + 2 incoming).
        if (node.getOutgoingEdges().size() == 2 && node.getIncomingEdges().size() == 2) {
            
            for (ElkEdge oEdge : node.getOutgoingEdges()) {
                // For outgoing edges, the first bend point needs to rotate.
                ElkBendPoint bp = oEdge.getSections().get(0).getBendPoints().get(0);
                rotateBendPointAroundPoint(bp, node.getX(), node.getY(), angle); 
            }
            
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
    
    
}
