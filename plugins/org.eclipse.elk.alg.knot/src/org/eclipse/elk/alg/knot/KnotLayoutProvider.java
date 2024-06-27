package org.eclipse.elk.alg.knot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.ElkPadding;
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
        
        
        
        

        
        /*
        for (FEdge edge : fgraph.getEdges()) {
            int count = edge.getProperty(ForceOptions.REPULSIVE_POWER);
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    FBendpoint fbend = new FBendpoint(edge);
                    bends.add(fbend);
                    
                }
                edge.distributeBendpoints();
            }
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

        
        
        
        // bendpoint distance
        int bpd = 25;
        
        // Hier eigene Bendpoints noch zufugen (nach Stress ausrichtung zum testen)
        // create bend points for curved edges.
        for (ElkNode node : layoutGraph.getChildren()) {
            
            
            
            List<ElkEdge> allEdges = new ArrayList<>();
            
            for (ElkEdge edge : ElkGraphUtil.allOutgoingEdges(node)) {
                allEdges.add(edge);
            }
            for (ElkEdge edge : ElkGraphUtil.allIncomingEdges(node)) {
                allEdges.add(edge);
            }
            
            
            //allEdges.addAll(node.getOutgoingEdges());
            
            // Info about Edges of a node
            System.out.println("All Edges: " + allEdges);
            System.out.println("------------");
            
            // upper bendpoint
            //ElkGraphUtil.createBendPoint(allEdges.get(0).getSections().get(0), node.getX(), node.getY()-bpd);
            // right bendpoint
            //ElkGraphUtil.createBendPoint(allEdges.get(1).getSections().get(0), node.getX()+bpd, node.getY());
            // lower bendpoint
            //ElkGraphUtil.createBendPoint(allEdges.get(2).getSections().get(0), node.getX(), node.getY()+bpd);
            // left bendpoint
            //ElkGraphUtil.createBendPoint(allEdges.get(3).getSections().get(0), node.getX()-bpd, node.getY());            
        }
        
        
        progressMonitor.done();
    }
}
