/**
 * Copyright (c) 2016 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.core.labels;

import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
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
  public static final IProperty<ILabelManager> LABEL_MANAGER = new Property<ILabelManager>(
            "org.eclipse.elk.labels.labelManager");

  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData.Builder()
        .id("org.eclipse.elk.labels.labelManager")
        .group("")
        .name("Label Manager")
        .description("The label manager responsible for a given part of the graph. A label manager can either be attached to a compound node (in which case it is responsible for all labels inside) or to specific labels. The label manager can then be called by layout algorithms to modify labels that are too wide to try and shorten them to a given target width.")
        .type(LayoutOptionData.Type.OBJECT)
        .optionClass(ILabelManager.class)
        .targets(EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.LABELS))
        .visibility(LayoutOptionData.Visibility.HIDDEN)
        .create()
    );
  }
}
