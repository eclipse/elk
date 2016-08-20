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
public class BinaryIndexedTreeTest {
    @Test
    public void sumBefore() throws Exception {
        BinaryIndexedTree ft = new BinaryIndexedTree(5);
        ft.add(1);
        ft.add(2);
        ft.add(1);

        assertThat(ft.rank(1), is(0));
        assertThat(ft.rank(2), is(2));
    }

    @Test
    public void size() throws Exception {
        BinaryIndexedTree ft = new BinaryIndexedTree(5);
        ft.add(2);
        ft.add(1);
        ft.add(1);

        assertThat(ft.size(), is(3));
    }

    @Test
    public void removeAll() throws Exception {
        BinaryIndexedTree ft = new BinaryIndexedTree(5);
        ft.add(0);
        ft.add(2);
        ft.add(1);
        ft.add(1);

        ft.removeAll(1);

        assertThat(ft.size(), is(2));
        assertThat(ft.rank(2), is(1));

        ft.removeAll(1);

        assertThat(ft.size(), is(2));
        assertThat(ft.rank(2), is(1));
    }

    @Test
    public void sumBetween() throws Exception {
        BinaryIndexedTree ft = new BinaryIndexedTree(5);
        ft.add(0);
        ft.add(2);
        ft.add(1);
        ft.add(1);
        ft.add(3);

        assertThat(ft.sumBetween(0, 3), is(3));

    }
}
