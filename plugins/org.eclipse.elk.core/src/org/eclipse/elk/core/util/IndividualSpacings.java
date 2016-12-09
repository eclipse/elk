/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.util;

import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.properties.MapPropertyHolder;

/**
 * A property holder which can be used to define individual spacing overrides. The overrides are applied to the
 * element this object is set on, which is done through the {@link CoreOptions#SPACING_INDIVIDUAL_OVERRIDE} property.
 */
public class IndividualSpacings extends MapPropertyHolder {

}
