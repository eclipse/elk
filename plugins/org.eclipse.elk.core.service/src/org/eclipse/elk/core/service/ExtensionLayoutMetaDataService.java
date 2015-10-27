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
package org.eclipse.elk.core.service;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
/**
 * A layout data service that reads its content from the Eclipse extension registry.
 *
 * @author msp
 * @author csp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-10-10 review KI-25 by chsch, bdu
 */
public class ExtensionLayoutMetaDataService extends AbstractExtensionLayoutMetaDataService {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reportError(final String extensionPoint, final IConfigurationElement element,
            final String attribute, final Throwable exception) {
        String message;
        if (element != null && attribute != null) {
            message = "Extension point " + extensionPoint + ": Invalid entry in attribute '"
                    + attribute + "' of element " + element.getName() + ", contributed by "
                    + element.getContributor().getName();
        } else {
            message = "Extension point " + extensionPoint
                    + ": An error occured while loading extensions.";
        }
        IStatus status = new Status(IStatus.WARNING, ElkServicePlugin.PLUGIN_ID,
                0, message, exception);
        StatusManager.getManager().handle(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reportError(final CoreException exception) {
        StatusManager.getManager().handle(exception, ElkServicePlugin.PLUGIN_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutAlgorithmData createLayoutAlgorithmData(final IConfigurationElement element) {
        LayoutAlgorithmData algoData = new LayoutAlgorithmData();
        String previewPath = element.getAttribute(ATTRIBUTE_PREVIEW);
        if (previewPath != null) {
            algoData.setPreviewImage(AbstractUIPlugin.imageDescriptorFromPlugin(
                    element.getContributor().getName(), previewPath));
        }
        return algoData;
    }
    
}
