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
package org.eclipse.elk.alg.layered.p3order.counting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

// CHECKSTYLEOFF MagicNumber
// CHECKSTYLEOFF javadoc
public class FenwickTreeTest {
    @Test
    public void sumBefore() throws Exception {
        FenwickTree list = new FenwickTree(5);
        list.add(1);
        list.add(2);
        list.add(1);
        assertThat(list.sumBefore(1), is(0));
        assertThat(list.sumBefore(2), is(2));
    }

    @Test
    public void size() throws Exception {
        FenwickTree list = new FenwickTree(5);
        list.add(2);
        list.add(1);
        list.add(1);
        assertThat(list.size(), is(3));
    }

    @Test
    public void removeAll() throws Exception {
        FenwickTree list = new FenwickTree(5);
        list.add(0);
        list.add(2);
        list.add(1);
        list.add(1);
        list.removeAll(1);
        assertThat(list.size(), is(2));
        assertThat(list.sumBefore(2), is(1));
        list.removeAll(1);
        assertThat(list.size(), is(2));
        assertThat(list.sumBefore(2), is(1));
    }
}
