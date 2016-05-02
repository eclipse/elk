/**
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christoph Daniel Schulze - initial API and implementation
 */
package org.eclipse.elk.core.labels;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.labels.ILabelManager;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

/**
 * Label management definitions of the Eclipse Layout Kernel.
 */
@SuppressWarnings("all")
public class LabelManagementOptions implements ILayoutMetaDataProvider {
  /**
   * The label manager responsible for a given part of the graph. A label manager can either be
   * attached to a compound node (in which case it is responsible for all labels inside) or to
   * specific labels. The label manager can then be called by layout algorithms to modify labels
   * that are too wide to try and shorten them to a given target width.
   */
  public final static IProperty<ILabelManager> LABEL_MANAGER = new Property<ILabelManager>(
            "org.eclipse.elk.labels.labelManager");
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "org.eclipse.elk.labels.labelManager",
        "",
        "Label Manager",
        "The label manager responsible for a given part of the graph. A label manager can either be attached to a compound node (in which case it is responsible for all labels inside) or to specific labels. The label manager can then be called by layout algorithms to modify labels that are too wide to try and shorten them to a given target width.",
        null,
        null,
        null,
        LayoutOptionData.Type.UNDEFINED,
        ILabelManager.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.HIDDEN
    ));
  }
}
