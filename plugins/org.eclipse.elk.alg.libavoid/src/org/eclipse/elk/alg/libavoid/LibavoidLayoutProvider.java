/*******************************************************************************
 * Copyright (c) 2013, 2022 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.alg.libavoid;

import org.eclipse.elk.alg.common.nodespacing.NodeDimensionCalculation;
import org.eclipse.elk.alg.libavoid.options.LibavoidOptions;
import org.eclipse.elk.alg.libavoid.server.LibavoidServer;
import org.eclipse.elk.alg.libavoid.server.LibavoidServerPool;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.adapters.ElkGraphAdapters;
import org.eclipse.elk.core.util.adapters.ElkGraphAdapters.ElkGraphAdapter;
import org.eclipse.elk.graph.ElkNode;

/**
 * A layout provider for KIML that performs layout using the Libavoid connector routing library. See
 * http://www.adaptagrams.org/documentation/ for further information on the library.
 * 
 * @author uru
 */
public class LibavoidLayoutProvider extends AbstractLayoutProvider {

    private LibavoidServerCommunicator comm = new LibavoidServerCommunicator();

    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final ElkNode parentNode, final IElkProgressMonitor progressMonitor) {

        // TODO
        // at this point the 'DIRECTION' should be set to a reasonable value ... 
        // parentNode.setProperty(LibavoidOptions.DIRECTION, Direction.DOWN);
        
        // TODO check if we should set this to 0 by default
        parentNode.setProperty(LibavoidOptions.IDEAL_NUDGING_DISTANCE, 0d);
        
        // calculate node margins
        ElkGraphAdapter adapter = ElkGraphAdapters.adapt(parentNode);

        NodeDimensionCalculation.sortPortLists(adapter);
        
        // TODO this behaves strangely (creates different node sizes for the layered alg)
        // NodeDimensionCalculation.calculateLabelAndNodeSizes(adapter);
        
        NodeDimensionCalculation.calculateNodeMargins(adapter);
        
        // create an Libavoid server process instance or use an existing one
        LibavoidServer lvServer = LibavoidServerPool.INSTANCE.fetch();
        // send a layout request to the server process and apply the layout
        comm.requestLayout(parentNode, progressMonitor, lvServer);
        // if everything worked well, release the used process instance
        LibavoidServerPool.INSTANCE.release(lvServer);

    }

}
