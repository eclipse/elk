/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.debug.grandom


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class GRandomStandaloneSetup extends GRandomStandaloneSetupGenerated {

	def static void doSetup() {
		new GRandomStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}
