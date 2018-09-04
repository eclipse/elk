/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.common.structure;

import org.eclipse.elk.core.util.IElkProgressMonitor;

public enum TestProcessors implements ILayoutProcessorFactory<StringBuffer> {
	PROCESSOR_1,
	PROCESSOR_2,
	PROCESSOR_3;
	
	public ILayoutProcessor<StringBuffer> create() {
		return new ILayoutProcessor<StringBuffer>() {
			@Override
			public void process(StringBuffer graph, IElkProgressMonitor progressMonitor) {
			    graph.append(TestProcessors.this.toString());
			}
		};
	}
}
