/**
 * Copyright (c) 2015 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.elk.conn.gmf.layouter;

import org.eclipse.elk.core.data.ILayoutMetaDataProvider;

/**
 * Layout algorithms contributed by GMF / GEF.
 */
@SuppressWarnings("all")
public class GmfMetaDataProvider implements ILayoutMetaDataProvider {
  public void apply(final org.eclipse.elk.core.data.ILayoutMetaDataProvider.Registry registry) {
    new org.eclipse.elk.conn.gmf.layouter.Draw2DOptions().apply(registry);
  }
}
