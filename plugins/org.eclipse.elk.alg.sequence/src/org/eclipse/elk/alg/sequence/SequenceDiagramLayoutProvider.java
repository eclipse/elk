/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.sequence;

import java.util.List;

import org.eclipse.elk.alg.sequence.p0import.KGraphImporter;
import org.eclipse.elk.alg.sequence.p0import.PapyrusImporter;
import org.eclipse.elk.alg.sequence.p1allocation.SpaceAllocator;
import org.eclipse.elk.alg.sequence.p2cycles.SCycleBreaker;
import org.eclipse.elk.alg.sequence.p3layering.MessageLayerer;
import org.eclipse.elk.alg.sequence.p4sorting.InteractiveLifelineSorter;
import org.eclipse.elk.alg.sequence.p4sorting.LayerBasedLifelineSorter;
import org.eclipse.elk.alg.sequence.p4sorting.ShortMessageLifelineSorter;
import org.eclipse.elk.alg.sequence.p5coordinates.KGraphCoordinateCalculator;
import org.eclipse.elk.alg.sequence.p5coordinates.PapyrusCoordinateCalculator;
import org.eclipse.elk.alg.sequence.p6export.KGraphExporter;
import org.eclipse.elk.alg.sequence.p6export.PapyrusExporter;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.UnsupportedGraphException;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

import com.google.common.collect.Lists;

/**
 * Layout algorithm for Papyrus sequence diagrams.
 * 
 * @author grh
 */
public final class SequenceDiagramLayoutProvider extends AbstractLayoutProvider {
    
    /** The layout provider's ID. */
    public static final String ID = "de.cau.cs.kieler.papyrus.sequence.layout";
    

    @Override
    public void layout(final ElkNode parentNode, final IElkProgressMonitor progressMonitor) {
        // Prevent the surrounding diagram from being laid out
        if (parentNode.getParent() == null) {
            throw new UnsupportedGraphException(
                    "Sequence diagram layout can only be run on surrounding interactions.");
        }
        
        // Initialize our layout context
        LayoutContext context = LayoutContext.fromLayoutData(parentNode);

        // Assemble and execute the algorithm
        List<ISequenceLayoutProcessor> algorithm = assembleLayoutProcessors(context);
        
        progressMonitor.begin("Sequence Diagram Layouter", algorithm.size());
        
        for (ISequenceLayoutProcessor processor : algorithm) {
            processor.process(context, progressMonitor.subTask(1));
        }
        
        progressMonitor.done();
    }
    
    /**
     * Assembles the list of layout processors that, when run in order, implement the sequence
     * diagram layout algorithm. The list may be different based on the given layout context.
     * 
     * @param context
     *            the layout context that contains all relevant information for the current layout
     *            run.
     * @return list of layout processors.
     */
    private List<ISequenceLayoutProcessor> assembleLayoutProcessors(final LayoutContext context) {
        List<ISequenceLayoutProcessor> processors = Lists.newArrayList();

        // The import algorithm depends on the coordinate system that is to be used
        switch (context.coordinateSystem) {
        case PAPYRUS:
            processors.add(new PapyrusImporter());
            break;
            
        default:
            processors.add(new KGraphImporter());
            break;   
        }
        
        processors.add(new SpaceAllocator());
        processors.add(new SCycleBreaker());
        processors.add(new MessageLayerer());
        
        // Lifeline sorting provides different options
        switch (context.sortingStrategy) {
        case LAYER_BASED:
            processors.add(new LayerBasedLifelineSorter());
            break;
            
        case SHORT_MESSAGES:
            processors.add(new ShortMessageLifelineSorter());
            break;
            
        default:
            processors.add(new InteractiveLifelineSorter());
            break;
        }
        
        // The rest of the algorithm depends on the coordinate system that is to be used
        switch (context.coordinateSystem) {
        case PAPYRUS:
            processors.add(new PapyrusCoordinateCalculator());
            processors.add(new PapyrusExporter());
            break;
            
        default:
            processors.add(new KGraphCoordinateCalculator());
            processors.add(new KGraphExporter());
            break;   
        }
        
        return processors;
    }
    
}
