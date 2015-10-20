/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.force;

import java.util.List;
import java.util.Random;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.options.LayoutOptions;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.force.graph.FGraph;
import org.eclipse.elk.force.model.AbstractForceModel;
import org.eclipse.elk.force.model.EadesModel;
import org.eclipse.elk.force.model.ForceModelStrategy;
import org.eclipse.elk.force.model.FruchtermanReingoldModel;
import org.eclipse.elk.force.properties.Properties;
import org.eclipse.elk.graph.KNode;

/**
 * Layout provider for the force layout algorithms.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public final class ForceLayoutProvider extends AbstractLayoutProvider {

    /** the layout provider id. */
    public static final String ID = "org.eclipse.elk.algorithm.force";
    
    /** the force model used by this layout algorithm. */
    private AbstractForceModel forceModel;
    /** connected components processor. */
    private ComponentsProcessor componentsProcessor = new ComponentsProcessor();

    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout(final KNode kgraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("KLay Force", 1);
        
        // transform the input graph
        IGraphImporter<KNode> graphImporter = new KGraphImporter();
        FGraph fgraph = graphImporter.importGraph(kgraph);

        // set special properties for the layered graph
        setOptions(fgraph, kgraph);

        // update the force model depending on user selection
        updateModel(fgraph.getProperty(Properties.FORCE_MODEL));
        
        // split the input graph into components
        List<FGraph> components = componentsProcessor.split(fgraph);
        
        // perform the actual layout
        for (FGraph comp : components) {
            forceModel.layout(comp, progressMonitor.subTask(1.0f / components.size()));
        }
        
        // pack the components back into one graph
        fgraph = componentsProcessor.recombine(components);
        
        // apply the layout results to the original graph
        graphImporter.applyLayout(fgraph);
        
        progressMonitor.done();
    }
    
    /**
     * Set special layout options for the force graph.
     * 
     * @param fgraph a new force graph
     * @param parent the original parent node
     */
    private void setOptions(final FGraph fgraph, final KNode parent) {
        // set the random number generator based on the random seed option
        Integer randomSeed = fgraph.getProperty(LayoutOptions.RANDOM_SEED);
        if (randomSeed != null) {
            int val = randomSeed;
            if (val == 0) {
                fgraph.setProperty(Properties.RANDOM, new Random());
            } else {
                fgraph.setProperty(Properties.RANDOM, new Random(val));
            }
        } else {
            fgraph.setProperty(Properties.RANDOM, new Random(1));
        }
    }
    
    /**
     * Update the force model depending on the chosen strategy.
     * 
     * @param strategy a force model strategy
     */
    private void updateModel(final ForceModelStrategy strategy) {
        switch (strategy) {
        case EADES:
            if (!(forceModel instanceof EadesModel)) {
                forceModel = new EadesModel();
            }
            break;
        case FRUCHTERMAN_REINGOLD:
            if (!(forceModel instanceof FruchtermanReingoldModel)) {
                forceModel = new FruchtermanReingoldModel();
            }
            break;
        }
    }

}
