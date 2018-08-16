/*******************************************************************************
 * Copyright (c) 2018 le-cds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.testing;

/**
 * This interface has to be implemented by layout providers that allow for white box testing. As opposed to black box
 * testing, white box testing does not simply execute the whole algorithm and examine the results. Instead, it allows
 * the layout algorithm to provide intermediate results that can be tested. 
 */
public interface IWhiteBoxTestable {
    
    /**
     * Sets the {@link TestController} that controls a white box test. The layout algorithm needs to notify the test
     * controller of interesting events during the layout process. The layout engine will call this method on a layout
     * provider before invoking layout. 
     * 
     * @param controller the test controller to install.
     */
    void setTestController(TestController controller);

}
