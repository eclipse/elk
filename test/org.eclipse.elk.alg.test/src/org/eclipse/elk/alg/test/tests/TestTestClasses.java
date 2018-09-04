/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.tests;

import org.eclipse.elk.alg.test.framework.LayoutTestRunner;
import org.eclipse.elk.alg.test.framework.annotations.StoreFailedGraphs;
import org.eclipse.elk.alg.test.framework.annotations.StoreResults;
import org.eclipse.elk.alg.test.framework.annotations.TestClasses;
import org.junit.runner.RunWith;

/**
 * Runs the other test classes.
 */
@StoreResults(false)
@StoreFailedGraphs(false)
@RunWith(LayoutTestRunner.class)
@TestClasses({BlackBoxTest.class, WhiteBoxTest.class})
public class TestTestClasses {

}
