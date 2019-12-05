/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.core.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.elk.core.service.LayoutConnectorsService;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Tester class for checking whether the active editor is supported by ELK.
 * 
 * @author jjc
 */
public class ActiveEditorSupportedTester extends PropertyTester {

    /**
     * {@inheritDoc}
     */
    public boolean test(final Object receiver, final String property,
            final Object[] args, final Object expectedValue) {
        if (receiver instanceof IWorkbenchPart) {
            IWorkbenchPart workbenchPart = (IWorkbenchPart) receiver;
            return LayoutConnectorsService.getInstance().getConnector(workbenchPart, null) != null;
        }
        return false;
    }

}
