/*******************************************************************************
 * Copyright (c) 2018 TypeFox GmbH and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core.data;

import org.eclipse.elk.core.UnsupportedConfigurationException;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.IGraphElementVisitor;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;

/**
 * Resolves layout algorithms configured through the {@link CoreOptions#ALGORITHM} option and assigns
 * the resulting meta data to the {@link CoreOptions#RESOLVED_ALGORITHM} option.
 */
public class LayoutAlgorithmResolver implements IGraphElementVisitor {

    @Override
    public void visit(final ElkGraphElement element) {
        if (element instanceof ElkNode && !element.getProperty(CoreOptions.NO_LAYOUT)) {
            resolveAlgorithm((ElkNode) element);
        }
    }
    
    /**
     * Resolve the layout algorithm to apply to the content of the given node.
     */
    protected void resolveAlgorithm(final ElkNode node) {
        String algorithmId = node.getProperty(CoreOptions.ALGORITHM);
        LayoutAlgorithmData data = LayoutMetaDataService.getInstance().getAlgorithmDataBySuffixOrDefault(
                algorithmId, getDefaultLayoutAlgorithmID());
        if (data != null) {
            // Assign the algorithm meta data to this node
            node.setProperty(CoreOptions.RESOLVED_ALGORITHM, data);
        } else if (mustResolve(node)) {
            // We must resolve the algorithm for this node, but failed to do so
            if (algorithmId == null || algorithmId.isEmpty()) {
                StringBuilder message = new StringBuilder("No layout algorithm has been specified for ");
                ElkUtil.printElementPath(node, message);
                throw new UnsupportedConfigurationException(message.toString());
            } else {
                StringBuilder message = new StringBuilder("Layout algorithm '");
                message.append(algorithmId);
                message.append("' not found for ");
                ElkUtil.printElementPath(node, message);
                throw new UnsupportedConfigurationException(message.toString());
            }
        }
    }

    /**
     * Determine whether it is mandatory to resolve the layout algorithm for the given node.
     */
    protected boolean mustResolve(final ElkNode node) {
        return !node.hasProperty(CoreOptions.RESOLVED_ALGORITHM) && !node.getChildren().isEmpty();
    }
    
    /**
     * Returns the ID of the layout algorithm to be used if no algorithm is explicitly set in the graph.
     * 
     * <p>The default implementation returns {@code "org.eclipse.elk.layered"}.</p>
     * 
     * @return ID of the default layout algorithm.
     */
    public String getDefaultLayoutAlgorithmID() {
        return "org.eclipse.elk.layered";
    }
    
}
