/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.elk.core.meta.ui;

import com.google.inject.Injector;
import org.eclipse.elk.core.meta.ui.internal.MetaActivator;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class MetaDataExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return FrameworkUtil.getBundle(MetaActivator.class);
	}
	
	@Override
	protected Injector getInjector() {
		MetaActivator activator = MetaActivator.getInstance();
		return activator != null ? activator.getInjector(MetaActivator.ORG_ECLIPSE_ELK_CORE_META_METADATA) : null;
	}

}
