/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.service.data;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.elk.core.util.IDataObject;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

import com.google.common.collect.Lists;

/**
 * Data type used to store information for a layout option.
 * 
 * @kieler.design 2011-02-01 reviewed by cmot, soh
 * @kieler.rating yellow 2012-10-09 review KI-25 by chsch, bdu
 * @author msp
 */
public final class LayoutOptionData implements ILayoutMetaData, IProperty<Object>,
        Comparable<IProperty<?>> {

    /** literal value constant for booleans. */
    public static final String BOOLEAN_LITERAL = "boolean";
    /** literal value constant for integer numbers. */
    public static final String INT_LITERAL = "int";
    /** literal value constant for strings. */
    public static final String STRING_LITERAL = "string";
    /** literal value constant for floating point numbers. */
    public static final String FLOAT_LITERAL = "float";
    /** literal value constant for enumerations. */
    public static final String ENUM_LITERAL = "enum";
    /** literal value constant for enumeration sets. */
    public static final String ENUMSET_LITERAL = "enumset";
    /** literal value constant for data objects. */
    public static final String OBJECT_LITERAL = "object";
    /** default name for layout options for which no name is given. */
    public static final String DEFAULT_OPTION_NAME = "<Unnamed Option>";

    /** data type enumeration. */
    public enum Type {
        /** undefined type. */
        UNDEFINED,
        /** boolean type. */
        BOOLEAN,
        /** integer type. */
        INT,
        /** string type. */
        STRING,
        /** float type. */
        FLOAT,
        /** enumeration type. */
        ENUM,
        /** enumeration set type. */
        ENUMSET,
        /** {@link IDataObject} type. */
        OBJECT;        
        
        /**
         * Returns a user-friendly literal for the enumeration value.
         * 
         * @return a literal
         */
        public String literal() {
            switch (this) {
            case BOOLEAN:
                return BOOLEAN_LITERAL;
            case INT:
                return INT_LITERAL;
            case STRING:
                return STRING_LITERAL;
            case FLOAT:
                return FLOAT_LITERAL;
            case ENUM:
                return ENUM_LITERAL;
            case ENUMSET:
                return ENUMSET_LITERAL;
            case OBJECT:
                return OBJECT_LITERAL;
            default:
                return toString();
            }
        }
    }

    /** option target enumeration. */
    public enum Target {
        /** parents target (hierarchical nodes). */
        PARENTS,
        /** nodes target. */
        NODES,
        /** edges target. */
        EDGES,
        /** ports target. */
        PORTS,
        /** labels target. */
        LABELS;
    }

    /** identifier of the layout option. */
    private String id = "";
    /** the default value of this option. */
    private Object defaultValue;
    /** type of the layout option. */
    private Type type = Type.UNDEFINED;
    /** user friendly name of the layout option. */
    private String name = DEFAULT_OPTION_NAME;
    /** a description to be displayed in the UI. */
    private String description = "";
    /** configured targets. */
    private Set<Target> targets = Collections.emptySet();
    /** dependencies to other layout options. */
    private List<Pair<LayoutOptionData, Object>> dependencies = Lists.newLinkedList();
    /** the class that represents this option type. */
    private Class<?> clazz;
    /** cached value of the available choices. */
    private String[] choices;
    /** whether the option should be shown in advanced mode only. */
    private boolean advanced;
    
    /**
     * Checks whether the enumeration class is set correctly. This method must
     * not be called for options other than of type {@code enum} and {@code enumset}.
     */
    private void checkEnumClass() {
        if (clazz == null || !clazz.isEnum()) {
            throw new IllegalStateException("Enumeration class expected for layout option " + id);
        }
    }

    /**
     * Checks whether the {@link IDataType} class is set correctly and creates an instance.
     * This method must not be called for options other than of type 'object'.
     * 
     * @return an instance of the data object
     */
    private IDataObject createDataInstance() {
        if (clazz == null || !IDataObject.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("IDataType class expected for layout option " + id);
        }
        try {
            return (IDataObject) clazz.newInstance();
        } catch (InstantiationException exception) {
            throw new IllegalStateException("The data object for layout option " + id
                    + " cannot be instantiated.", exception);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("The data object for layout option " + id
                    + " cannot be accessed.", exception);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object obj) {
        if (obj instanceof LayoutOptionData) {
            return this.id.equals(((LayoutOptionData) obj).id);
        } else if (obj instanceof IProperty<?>) {
            return this.id.equals(((IProperty<?>) obj).getId());
        } else {
            return false;
        }
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
    public int compareTo(final IProperty<?> other) {
        return id.compareTo(other.getId());
    }

     /**
      * {@inheritDoc}
      */
     @Override
    public String toString() {
        return "Layout Option: " + id;
    }

    /**
     * Sets the type field depending on the given literal.
     * 
     * @param typeLiteral a string value that is expected to be equal to one of
     *            the predefined literal value constants
     */
    public void setType(final String typeLiteral) {
        if (BOOLEAN_LITERAL.equalsIgnoreCase(typeLiteral)) {
            type = Type.BOOLEAN;
        } else if (INT_LITERAL.equalsIgnoreCase(typeLiteral)) {
            type = Type.INT;
        } else if (STRING_LITERAL.equalsIgnoreCase(typeLiteral)) {
            type = Type.STRING;
        } else if (FLOAT_LITERAL.equalsIgnoreCase(typeLiteral)) {
            type = Type.FLOAT;
        } else if (ENUM_LITERAL.equalsIgnoreCase(typeLiteral)) {
            type = Type.ENUM;
        } else if (ENUMSET_LITERAL.equalsIgnoreCase(typeLiteral)) {
            type = Type.ENUMSET;
        } else if (OBJECT_LITERAL.equalsIgnoreCase(typeLiteral)) {
            type = Type.OBJECT;
        } else {
            throw new IllegalArgumentException("The given type literal is invalid.");
        }
    }

    /**
     * Parses a string value for this layout option.
     * 
     * @param valueString a serialized value
     * @return an instance of the corresponding correctly typed value, or
     *         {@code null} if the given value string is invalid
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object parseValue(final String valueString) {
        if (valueString == null || valueString.equals("null")) {
            return null;
        }
        
        // if this open data instance is an ENUMSET or a REMOTE_ENUMSET, we need to allow empty strings
        // to denote that no enumeration value is selected. Otherwise, we forbid empty strings
        if (valueString.length() == 0 && type != Type.ENUMSET) {
            return null;
        }
        
        switch (type) {
        case BOOLEAN:
            return Boolean.valueOf(valueString);
        case INT:
            try {
                return Integer.valueOf(valueString);
            } catch (NumberFormatException exception) {
                return null;
            }
        case STRING:
            return valueString;
        case FLOAT:
            try {
                return Float.valueOf(valueString);
            } catch (NumberFormatException exception) {
                return null;
            }
        case ENUM:
            checkEnumClass();
            return enumForString(valueString);
        case ENUMSET:
            checkEnumClass();
            return enumSetForStringArray((Class<? extends Enum>) clazz, valueString);
        case OBJECT:
            try {
                IDataObject value = createDataInstance();
                value.parse(valueString);
                return value;
            } catch (IllegalArgumentException exception) {
                return null;
            }
        default:
            throw new IllegalStateException("Invalid type set for this layout option.");
        }
    }
    
    /**
     * Tries to turn the given string representation into a set over the enumeration of
     * the type given by the class type parameter. The parameter is supposed to be the
     * {@link #clazz} attribute, but has to be given here explicitly for type inference
     * reasons. The string consists of multiple parts, with each part following the
     * convention specified in the comment of {@link #enumForString(String)}. The format
     * of the string is something like {@code [a, b, c]}.
     * 
     * @param leClazz the enumeration class.
     * @param leString the string to convert.
     * @return the enumeration set.
     */
    private <E extends Enum<E>> EnumSet<E> enumSetForStringArray(final Class<E> leClazz,
            final String leString) {
        
        EnumSet<E> set = EnumSet.noneOf(leClazz);
        
        // break the value string into its different components and iterate over them;
        // the string will be of the form "[a, b, c]"
        String[] components = leString.split("[\\[\\]\\s,]+");
        for (String component : components) {
            // Check for empty strings
            if (component.trim().length() == 0) {
                continue;
            }
            
            Object o = enumForString(component);
            
            if (o == null) {
                // we were unable to get the enumeration instance, so the whole enumset
                // is pointless; let's all take a moment of silence to mourn
                return null;
            } else {
                // add the enumeration object to the set
                set.add(leClazz.cast(o));
            }
        }
        
        return set;
    }

    /**
     * Tries to turn the given string representation into an enumeration of the type given by
     * the {@link #clazz} attribute. The string may either specify one of the named items of
     * the enumeration, or a number referring to one of the enumeration's items by its ordinal
     * number.
     * 
     * @param leString the string to convert to an enumeration.
     * @return the enumeration.
     */
    @SuppressWarnings("unchecked")
    private Object enumForString(final String leString) {
        try {
            @SuppressWarnings("rawtypes")
            Enum<?> value = Enum.valueOf((Class<? extends Enum>) clazz, leString);
            return value;
        } catch (IllegalArgumentException exception) {
            // the value could not be parsed as enumeration constant, try as integer
            try {
                int index = Integer.parseInt(leString);
                Object[] constants = clazz.getEnumConstants();
                if (index >= 0 && index < constants.length) {
                    return constants[index];
                }
            } catch (NumberFormatException e) {
                // ignore exception and return null
            }
            return null;
        }
    }

    /**
     * Parses the possible values for a remote enumeration from a space separated string.
     * 
     * @param valueString
     *            the space separated string containing the possible values
     */
    public void parseRemoteEnumValues(final String valueString) {
        Vector<String> tmp = new Vector<String>();
        StringTokenizer tokenizer = new StringTokenizer(valueString, " ");
        while (tokenizer.hasMoreTokens()) {
            tmp.add(tokenizer.nextToken());
        }      
        choices = tmp.toArray(new String[tmp.size()]);
    }
    
    /**
     * Creates a default-default value for this layout option which is used if no default value was
     * specified for a layout option. In contrast to {@link #getDefault()}, this never returns
     * {@code null} for options with type other than 'object'.
     * 
     * @return a default-default value, depending on the option type
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getDefaultDefault() {
        switch (type) {
        case STRING:
            return "";
        case BOOLEAN:
            return Boolean.FALSE;
        case INT:
            return Integer.valueOf(0);
        case FLOAT:
            return Float.valueOf(0.0f);
        case ENUM:
            checkEnumClass();
            Enum<?>[] enums = ((Class<Enum>) clazz).getEnumConstants();
            return enums[0];
        case ENUMSET:
            checkEnumClass();
            return EnumSet.noneOf(((Class<Enum>) clazz));
        case OBJECT:
            return null;
        default:
            throw new IllegalStateException("Invalid type set for this layout option.");
        }
    }
    
    /** choices for boolean type options. */
    private static final String[] BOOLEAN_CHOICES = { "false", "true" };

    /**
     * Creates an array of choices that can be selected by the user to set a
     * value for this option. This makes only sense for enumeration type
     * or boolean type options.
     * 
     * @return an array of values to be displayed for the user
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String[] getChoices() {
        if (choices == null) {
            switch (type) {
            case ENUM:
            case ENUMSET:
                checkEnumClass();
                Enum<?>[] enums = ((Class<Enum>) clazz).getEnumConstants();
                choices = new String[enums.length];
                for (int i = 0; i < enums.length; i++) {
                    choices[i] = enums[i].toString();
                }
                break;
            case BOOLEAN:
                choices = BOOLEAN_CHOICES;
                break;
            default:
                choices = new String[0];
            }
        }
        return choices;
    }

    /**
     * Returns the enumeration value for a given index.
     * 
     * @param intValue zero-based index of the enumeration value
     * @return the corresponding enumeration value
     */
    public Enum<?> getEnumValue(final int intValue) {
        switch (type) {
        case ENUM:
        case ENUMSET:
            checkEnumClass();
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Enum<?>[] enums = ((Class<? extends Enum>) clazz).getEnumConstants();
            return enums[intValue]; 
        default:
            return null;
        }
    }

    /**
     * Sets the layout option targets from a serialized definition. The input is expected to be
     * a comma separated list of target literals.
     * 
     * @param targetsString comma separated list of targets
     */
    public void setTargets(final String targetsString) {
        targets = EnumSet.noneOf(Target.class);
        if (targetsString != null) {
            StringTokenizer tokenizer = new StringTokenizer(targetsString, ", \t");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equalsIgnoreCase(Target.PARENTS.toString())) {
                    targets.add(Target.PARENTS);
                } else if (token.equalsIgnoreCase(Target.NODES.toString())) {
                    targets.add(Target.NODES);
                } else if (token.equalsIgnoreCase(Target.EDGES.toString())) {
                    targets.add(Target.EDGES);
                } else if (token.equalsIgnoreCase(Target.PORTS.toString())) {
                    targets.add(Target.PORTS);
                } else if (token.equalsIgnoreCase(Target.LABELS.toString())) {
                    targets.add(Target.LABELS);
                }
            }
        }
    }

    /**
     * Returns the set of layout option targets, which determine the types of graph elements
     * the option can be related to.
     * 
     * @return the layout option targets
     */
    public Set<Target> getTargets() {
        return targets;
    }
    
    /**
     * Returns the dependencies to other layout options. The option should only be made visible if
     * at least one of the dependencies is met.
     * 
     * @return the options from which this option depends together with the expected values
     */
    public List<Pair<LayoutOptionData, Object>> getDependencies() {
        return dependencies;
    }

    /**
     * Sets the identifier.
     *
     * @param theid the identifier to set
     */
    public void setId(final String theid) {
        assert theid != null;
        this.id = theid;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the data type. If the option class can be derived from the class, it is also set.
     *
     * @param thetype the data type to set
     */
    public void setType(final Type thetype) {
        this.type = thetype;
        switch (thetype) {
        case STRING:
            clazz = String.class;
            break;
        case BOOLEAN:
            clazz = Boolean.class;
            break;
        case INT:
            clazz = Integer.class;
            break;
        case FLOAT:
            clazz = Float.class;
            break;
        default:
            // the class cannot be derived from the type and must be set with setOptionClass(Class)
        }
    }

    /**
     * Returns the type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the name.
     *
     * @param thename the name to set
     */
    public void setName(final String thename) {
        if (thename == null || thename.length() == 0) {
            this.name = DEFAULT_OPTION_NAME;
        } else {
            this.name = thename;
        }
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
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

    /**
     * Returns the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns the default value of this property.
     * 
     * @return the default value.
     */
    public Object getDefault() {
        // Clone the default value if it's a Cloneable. We need to use reflection for this to work
        // properly (classes implementing Cloneable are not required to make their clone() method
        // public, so we need to check if they have such a method and invoke it via reflection, which
        // results in ugly and unchecked type casting)
        if (defaultValue instanceof Cloneable) {
            try {
                Method cloneMethod = defaultValue.getClass().getMethod("clone");
                return cloneMethod.invoke(defaultValue);
            } catch (Exception e) {
                // Give up cloning and return the default instance
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * Sets the default value.
     * 
     * @param thedefaultValue the default value
     */
    public void setDefault(final Object thedefaultValue) {
        this.defaultValue = thedefaultValue;
    }

    /**
     * Returns the option type class.
     * 
     * @return the type class
     */
    public Class<?> getOptionClass() {
        return clazz;
    }

    /**
     * Sets the option type class.
     * 
     * @param theclazz the class to set
     */
    public void setOptionClass(final Class<?> theclazz) {
        this.clazz = theclazz;
    }

    /**
     * Whether the option should be shown in advanced mode only.
     * 
     * @return true if the option is advanced
     */
    public boolean isAdvanced() {
        return advanced;
    }

    /**
     * Sets the advanced property of the layout option.
     * 
     * @param theadvanced true if the option is advanced
     */
    public void setAdvanced(final boolean theadvanced) {
        this.advanced = theadvanced;
    }

    @Override
    public Comparable<? super Object> getLowerBound() {
        return Property.NEGATIVE_INFINITY;
    }

    @Override
    public Comparable<? super Object> getUpperBound() {
        return Property.POSITIVE_INFINITY;
    }

}
