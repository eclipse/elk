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
package org.eclipse.elk.graphviz.dot;

/**
 * Initialization support for running Xtext languages without equinox extension registry.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class GraphvizDotStandaloneSetup extends GraphvizDotStandaloneSetupGenerated {

    /**
     * Do the standalone setup.
     */
    public static void doSetup() {
        new GraphvizDotStandaloneSetup().createInjectorAndDoEMFRegistration();
    }
}
