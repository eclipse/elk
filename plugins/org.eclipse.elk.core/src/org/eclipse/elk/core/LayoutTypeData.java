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
package org.eclipse.elk.core;

import java.util.LinkedList;
import java.util.List;

/**
 * Data type used to store information for a layout type. A layout type defines the basic
 * graph drawing approach employed by a layout algorithm, for instance the layer-based
 * approach ({@link #TYPE_LAYERED}) or the force-based approach ({@link #TYPE_FORCE}).
 * The type of an algorithm can be left empty, in which case it is displayed as "Other"
 * in the user interface.
 *
 * @kieler.design 2011-02-01 reviewed by cmot, soh
 * @kieler.rating yellow 2012-10-09 review KI-25 by chsch, bdu
 * @author msp
 */
public final class LayoutTypeData implements ILayoutMetaData {
    
    /** default name for layout types for which no name is given. */
    public static final String DEFAULT_TYPE_NAME = "<Unnamed Type>";
    
    /** type identifier for layer based algorithms. */
    public static final String TYPE_LAYERED = "org.eclipse.elk.type.layered";
    /** type identifier for orthogonalization algorithms. */
    public static final String TYPE_ORTHOGONAL = "org.eclipse.elk.type.orthogonal";
    /** type identifier for force based algorithms. */
    public static final String TYPE_FORCE = "org.eclipse.elk.type.force";
    /** type identifier for circular algorithms. */
    public static final String TYPE_CIRCLE = "org.eclipse.elk.type.circle";
    /** type identifier for tree algorithms. */
    public static final String TYPE_TREE = "org.eclipse.elk.type.tree";

    
    /** identifier of the layout type. */
    private String id = "";
    /** user friendly name of the layout type. */
    private String name = "";
    /** detail description. */
    private String description = "";
    /** the list of layout algorithms that are registered for this type. */
    private final List<LayoutAlgorithmData> layouters = new LinkedList<LayoutAlgorithmData>();
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj instanceof LayoutTypeData) {
            return this.id.equals(((LayoutTypeData) obj).id);
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
        if (name != null && name.length() > 0) {
            if (name.endsWith(">")) {
                return name;
            } else {
                return name + " Type";
            }
        } else {
            return "Other";
        }
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
     * Sets the layout type identifier.
     * 
     * @param theid the identifier to set
     */
    public void setId(final String theid) {
        assert theid != null;
        this.id = theid;
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
     * Sets the name of the layout type.
     * 
     * @param thename the name to set
     */
    public void setName(final String thename) {
        if (thename == null || thename.length() == 0) {
            this.name = DEFAULT_TYPE_NAME;
        } else {
            this.name = thename;
        }
    }
    
    /**
     * Returns the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description.
     * 
     * @param thedescription the description to set
     */
    public void setDescription(final String thedescription) {
        if (thedescription == null) {
            this.description = "";
        } else {
            this.description = thedescription;
        }
    }

}
