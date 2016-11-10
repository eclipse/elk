/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.util;

import java.io.IOException;
import java.util.Map;

import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.core.util.GraphDataUtil;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * A custom resource factory that returns resources for ElkGraph xmi models. The dedicated resource takes care
 * of loading and storing custom layout properties within the model itself.
 * 
 * @see ElkUtil#loadDataElements(ElkNode)
 * @see ElkUtil#persistDataElements(ElkNode)
 * 
 * @author uru
 */
public class ElkGraphResourceFactory implements Resource.Factory {

    /**
     * {@inheritDoc}
     */
    public Resource createResource(final URI uri) {
        return new ElkGraphResource(uri);
    }

    /**
     * See the above class comment for further information. 
     */
    public static class ElkGraphResource extends XMIResourceImpl {

        /**
         * @param uri
         *            the desired uri.
         */
        public ElkGraphResource(final URI uri) {
            super(uri);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void load(final Map<?, ?> options) throws IOException {
            super.load(options);
            if (!this.getContents().isEmpty()) {
                EObject o = this.getContents().get(0);
                if (o instanceof ElkNode) {
                    // parse persisted key-value pairs using KIML's layout data service
                    GraphDataUtil.loadDataElements((ElkNode) o);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void save(final Map<?, ?> options) throws IOException {
            if (!this.getContents().isEmpty()) {
                EObject o = this.getContents().get(0);
                if (o instanceof ElkNode) {
                    ElkUtil.persistDataElements((ElkNode) o);
                }
            }

            super.save(options);
        }

    }

}
