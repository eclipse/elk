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
package org.eclipse.elk.core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Data type used to store information for a layout category. A layout category defines the basic
 * graph drawing approach employed by a layout algorithm, for instance the layer-based approach
 * or the force-based approach. The category of an algorithm can be left empty, in which case it
 * is displayed as "Other" in the user interface.
 *
 * @kieler.design 2011-02-01 reviewed by cmot, soh
 * @kieler.rating yellow 2012-10-09 review KI-25 by chsch, bdu
 * @author msp
 */
public final class LayoutCategoryData implements ILayoutMetaData {
    
    /** identifier of the layout type. */
    private final String id;
    /** user friendly name of the layout type. */
    private final String name;
    /** detail description. */
    private final String description;
    /** the list of layout algorithms that are registered for this category. */
    private final List<LayoutAlgorithmData> layouters = new LinkedList<LayoutAlgorithmData>();
    
    /**
     * Create a layout category data entry.
     */
    public LayoutCategoryData(final String aid, final String aname, final String adescription) {
        this.id = aid;
        this.name = aname;
        this.description = adescription;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj instanceof LayoutCategoryData) {
            return this.id.equals(((LayoutCategoryData) obj).id);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Layout Type: " + id;
    }
    
    /**
     * Returns the list of layout algorithms that are registered for this type.
     * 
     * @return the layouters
     */
    public List<LayoutAlgorithmData> getLayouters() {
        return layouters;
    }

    /**
     * Returns the layout type identifier.
     * 
     * @return the layout type identifier
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the name of the layout type.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
}
