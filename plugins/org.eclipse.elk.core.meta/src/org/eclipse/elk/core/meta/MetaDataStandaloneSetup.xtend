/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.meta


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class MetaDataStandaloneSetup extends MetaDataStandaloneSetupGenerated {

	def static void doSetup() {
		new MetaDataStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}
