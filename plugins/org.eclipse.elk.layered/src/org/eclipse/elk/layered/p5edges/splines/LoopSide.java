/*******************************************************************************
 * Copyright (c) 2014, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.layered.p5edges.splines;

import java.util.Set;

import org.eclipse.elk.core.options.PortSide;

import com.google.common.collect.ImmutableSet;

/**
 * Elements of this enumeration represent the four sides, four corners and four "across" LoopSides
 * that are possible. For example: an edge laying on LoopPortSide.N would go
 * from PortSide.NORTH to PortSide.NORTH. An edge laying on LoopPortSide.NE would go from
 * PortSide.EAST to PortSide.NORTH. An edge laying on LoopSide.ENW would go from PortSide.EAST to
 * PortSide.WEST and runs across PortSide.NORTH.
 * 
 * @author tit
 */
public enum LoopSide {
    /** North Loop. */
    N(PortSide.NORTH, PortSide.NORTH, LoopSideType.SIDE), 
    /** North-East Loop. */
    EN(PortSide.EAST, PortSide.NORTH, LoopSideType.CORNER), 
    /** East Loop. */
    E(PortSide.EAST, PortSide.EAST, LoopSideType.SIDE), 
    /** South-East Loop. */
    SE(PortSide.SOUTH, PortSide.EAST, LoopSideType.CORNER), 
    /** South Loop. */
    S(PortSide.SOUTH, PortSide.SOUTH, LoopSideType.SIDE), 
    /** South-West Loop. */
    WS(PortSide.WEST, PortSide.SOUTH, LoopSideType.CORNER), 
    /** West Loop. */
    W(PortSide.WEST, PortSide.WEST, LoopSideType.SIDE), 
    /** North-West Loop. */
    NW(PortSide.NORTH, PortSide.WEST, LoopSideType.CORNER),
    /** West-East Loop around the north side. */
    ENW(PortSide.EAST, PortSide.WEST, LoopSideType.ACROSS),
    /** West-East Loop around the south side. */
    ESW(PortSide.EAST, PortSide.WEST, LoopSideType.ACROSS),
    /** North-South Loop around the east side. */
    SEN(PortSide.SOUTH, PortSide.NORTH, LoopSideType.ACROSS),
    /** North-South Loop around the west side. */
    SWN(PortSide.SOUTH, PortSide.NORTH, LoopSideType.ACROSS),
    /** Undefined loop. */
    UNDEFINED(PortSide.UNDEFINED, PortSide.UNDEFINED, LoopSideType.UNDEFINED);
    
    // Predefined sets for performance.
    /** All LoopSides. */
    private static final Set<LoopSide> ALL = 
            ImmutableSet.of(N, EN, E, SE, S, WS, W, NW, ENW, ESW, SEN, SWN);
    /** All straight LoopSides. */
    private static final Set<LoopSide> ALL_STRAIGHTS = 
            ImmutableSet.of(N, E, S, W);
    /** All corner LoopSides. */
    private static final Set<LoopSide> ALL_CORNERS = 
            ImmutableSet.of(EN, SE, WS, NW);
    
    // Predefined sets for performance, listing the PortSides that a LoopSide is spanning.
    /** PortSides of the LoopSide N. */
    private static final Set<PortSide> SIDES_N = 
            ImmutableSet.of(PortSide.NORTH);
    /** PortSides of the LoopSide EN. */
    private static final Set<PortSide> SIDES_EN = 
            ImmutableSet.of(PortSide.EAST, PortSide.NORTH);
    /** PortSides of the LoopSide E. */
    private static final Set<PortSide> SIDES_E = 
            ImmutableSet.of(PortSide.EAST);
    /** PortSides of the LoopSide SE. */
    private static final Set<PortSide> SIDES_SE = 
            ImmutableSet.of(PortSide.SOUTH, PortSide.EAST);
    /** PortSides of the LoopSide S. */
    private static final Set<PortSide> SIDES_S = 
            ImmutableSet.of(PortSide.SOUTH);
    /** PortSides of the LoopSide WS. */
    private static final Set<PortSide> SIDES_WS = 
            ImmutableSet.of(PortSide.WEST, PortSide.SOUTH);
    /** PortSides of the LoopSide W. */
    private static final Set<PortSide> SIDES_W = 
            ImmutableSet.of(PortSide.WEST);
    /** PortSides of the LoopSide NW. */
    private static final Set<PortSide> SIDES_NW = 
            ImmutableSet.of(PortSide.NORTH, PortSide.WEST);
    /** PortSides of the LoopSide ENW. */
    private static final Set<PortSide> SIDES_ENW = 
            ImmutableSet.of(PortSide.EAST, PortSide.NORTH, PortSide.WEST);
    /** PortSides of the LoopSide ESW. */
    private static final Set<PortSide> SIDES_ESW = 
            ImmutableSet.of(PortSide.EAST, PortSide.SOUTH, PortSide.WEST);
    /** PortSides of the LoopSide SWN. */
    private static final Set<PortSide> SIDES_SWN = 
            ImmutableSet.of(PortSide.SOUTH, PortSide.WEST, PortSide.NORTH);
    /** PortSides of the LoopSide SEN. */
    private static final Set<PortSide> SIDES_SEN = 
            ImmutableSet.of(PortSide.SOUTH, PortSide.EAST, PortSide.NORTH);
    /** PortSides of the undefined LoopSide. */
    private static final Set<PortSide> SIDES_UNDEF = ImmutableSet.of();

   
    /** The source portSide of the LoopSide. */
    private final PortSide source;
    /** The target portSide of the LoopSide. */
    private final PortSide target;
    /** The LoopSideType of the LoopSide. */
    private final LoopSideType type;
    
    /**
     * The type of a {@link LoopSide}.
     * @author tit
     *
     */
    public enum LoopSideType {
        /** Laying on a straight side of a node. {@code N, E, S, W}  */
        SIDE, 
        /** Laying on a corner of a node. {@code EN, SE, WS, NW}  */
        CORNER, 
        /** Going from one straight side to the opposed. {@code ENW, ESW, SEN, SWN}  */
        ACROSS, 
        /** Undefined. */
        UNDEFINED;
    }

    /**
     * Creates a LoopSide from the source to the target PortSide of the given type.
     * 
     * @param source PortSide where the LoopSide shall start. 
     * @param target PortSide where the LoopSide shall end.
     * @param type The type of the PortSide.
     */
    private LoopSide(final PortSide source, final PortSide target, final LoopSideType type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }
    
    /**
     * Returns the corresponding {@link LoopSide} for a {@link PortSide}. 
     * Only straight sides (N|E|S|W) are returned. If the PortSide is {@code UNDEFINED} the 
     * {@code UNDEFINED} LoopSide is returned.
     * 
     * @param side The port side.
     * @return The corresponding loop side.
     */
    public static LoopSide fromPortSides(final PortSide side) {
        switch (side) {
        case NORTH:
            return N;
        case EAST:
            return E;
        case SOUTH:
            return S;
        case WEST:
            return W;
        default:
            return UNDEFINED;
        }
    }
    
    /**
     * Returns the corresponding {@link LoopSide} laying between two {@link PortSide}.
     *  
     * @param side0 First port side.
     * @param side1 Second port side.
     * @return The corresponding loop side.
     */
    public static LoopSide fromPortSide(final PortSide side0, final PortSide side1) {
        if (side0.equals(side1)) {
            return fromPortSides(side0);
        }
        
        switch (side0) {
        case NORTH:
            switch (side1) {
            case WEST:
                return NW;
            case NORTH:
                return N;
            case EAST:
                return EN;
            case SOUTH:
                return SEN;
            }
        case EAST:
            switch (side1) {
            case NORTH:
                return EN;
            case EAST:
                return E;
            case SOUTH:
                return SE;
            case WEST:
                return ENW;
            }
        case SOUTH:
            switch (side1) {
            case EAST:
                return SE;
            case SOUTH:
                return S;
            case WEST:
                return WS;
            case NORTH:
                return SEN;
            }
        case WEST:
            switch (side1) {
            case SOUTH:
                return WS;
            case WEST:
                return W;
            case NORTH:
                return NW;
            case EAST:
                return ENW;
            }
        }
        return UNDEFINED;
    }
    

    /**
     * @return The side opposed to this one.
     */
    public LoopSide opposite() {
        switch (this) {
        case N:
            return S;
        case EN:
            return WS;
        case E:
            return W;
        case SE:
            return NW;
        case S:
            return N;
        case WS:
            return EN;
        case W:
            return E;
        case NW:
            return SE;
        case ENW:
            return ESW;
        case ESW:
            return ENW;
        case SEN:
            return SWN;
        case SWN:
            return SEN;
        default:
            return UNDEFINED;
        }
    }

    /**
     * @return Right neighbor of this side.
     */
    public LoopSide right() {
        switch (this) {
        case N:
            return EN;
        case EN:
            return E;
        case E:
            return SE;
        case SE:
            return S;
        case S:
            return WS;
        case WS:
            return W;
        case W:
            return NW;
        case NW:
            return N;
        case ENW:
            return ESW;
        case ESW:
            return ENW;
        case SEN:
            return SWN;
        case SWN:
            return SEN;
        default:
            return UNDEFINED;
        }
    }

    /**
     * @return Left neighbor of this side
     */
    public LoopSide left() {
        switch (this) {
        case N:
            return NW;
        case EN:
            return N;
        case E:
            return EN;
        case SE:
            return E;
        case S:
            return SE;
        case WS:
            return S;
        case W:
            return WS;
        case NW:
            return W;
        case ENW:
            return ESW;
        case ESW:
            return ENW;
        case SEN:
            return SWN;
        case SWN:
            return SEN;
        default:
            return UNDEFINED;
        }
    }
    
    /** 
     * True, if this loopSide is a corner-loopSide.
     * 
     * @return True if this loopSide is one of: EN, SE, WS, NW. False otherwise.
     */
    public boolean isCorner() {
        return type == LoopSideType.CORNER;
    }
    
    /**
     * True, if this loop side is a across loop side. Going from one node side to it's opposed.
     * 
     * @return True if this loopSide is one of ENW, ESW. False otherwise.
     */
    public boolean isAcross() {
        return type == LoopSideType.ACROSS;
    }
    
    /**
     * True, if this loop side is a straight loop side. Laying on a single node side.
     * 
     * @return True if this loopSide is one of N, E, S, W. False otherwise.
     */
    public boolean isStraight() {
        return type == LoopSideType.SIDE;
    }
    
    /**
     * Returns the {@link LoopSideType} of this LoopSide.
     * 
     * @return The type of this LoopSide.
     */
    public LoopSideType getType() {
        return type;
    }
    
    /**
     * Returns the port side of a across loop side, that this loop side is going through.
     *  
     * @return NORTH for ENW, South for ESW, East for SEN, West for SWN, undefined otherwise.
     */
    public PortSide getMiddleSide() {
        switch (this) {
        case ENW:
            return PortSide.NORTH;
        case ESW:
            return PortSide.SOUTH;
        case SEN:
            return PortSide.EAST;
        case SWN:
            return PortSide.WEST;
        default:
            return PortSide.UNDEFINED;
        }
    }

    /**
     * @return The target side of an loop-edge laying on this LoopSide.
     */
    public PortSide getTargetSide() {
        return target;
    }

    /**
     * @return The source side of an loop-edge laying on this LoopSide.
     */
    public PortSide getSourceSide() {
        return source;
    }
    
    /**
     * @return All {@link PortSide}s of this LoopSide.
     */
    public Set<PortSide> getPortSides() {
        switch (this) {
        case N:
            return SIDES_N;
        case EN:
            return SIDES_EN;
        case E:
            return SIDES_E;
        case SE:
            return SIDES_SE;
        case S:
            return SIDES_S;
        case WS:
            return SIDES_WS;
        case W:
            return SIDES_W;
        case NW:
            return SIDES_NW;
        case ENW:
            return SIDES_ENW;
        case ESW:
            return SIDES_ESW;
        case SWN:
            return SIDES_SWN;
        case SEN:
            return SIDES_SEN;
        default:
            return SIDES_UNDEF;
        }
    }
    
    /**
     * @return A {@link Set} of all LoopSides except {@code UNDEFINED}.
     */
    public static Set<LoopSide> getAllDefinedSides() {
        return ALL;
    }

    /**
     * @return A {@link Set} of all LoopSides that are side loopSides.
     */
    public static Set<LoopSide> getAllStraightSides() {
       return ALL_STRAIGHTS;
    }

    /**
     * @return A {@link Set} of all LoopSides that are corner loopSides.
     */
    public static Set<LoopSide> getAllCornerSides() {
       return ALL_CORNERS;
    }
    
    /**
     * Returns {@code true} if the loop crosses the {@code NW} edge in counter-clockwise direction. This 
     * information is useful then inspecting the order of the {@link LPort}s in the port list of 
     * a {@link LNode}. 
     *  
     * @return True if the loop crosses the NW edge in counter-clockwise direction. 
     */
    public boolean viaNW() {
        if (this == NW || this == ENW) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns the {@link PortSide} of this LoopSide. If the LoopSide is laying on more than one 
     * side (i.e. EN, ENW ...), the parameter determines which of the sides to return.
     *  
     * @param i Determines which of the sides to return. If i > number of sides, the last side is
     * returned.
     * @return The PortSide.
     */
    public PortSide getPortSide(final int i) {
        switch (this) {
        case N:
            return PortSide.NORTH;
        case E:
            return PortSide.EAST;
        case S:
            return PortSide.SOUTH;
        case W:
            return PortSide.WEST;
        case EN:
            if (i == 0) {
                return PortSide.NORTH;
            } else {
                return PortSide.EAST;
            }
        case NW:
            if (i == 0) {
                return PortSide.NORTH;
            } else {
                return PortSide.WEST;
            }
        case SE:
            if (i == 0) {
                return PortSide.SOUTH;
            } else {
                return PortSide.EAST;
            }
        case WS:
            if (i == 0) {
                return PortSide.SOUTH;
            } else {
                return PortSide.WEST;
            }
        case ENW:
            if (i == 0) {
                return PortSide.WEST;
            } else if (i == 1) {
                return PortSide.NORTH;
            } else {
                return PortSide.EAST;
            }
        case ESW:
            if (i == 0) {
                return PortSide.WEST;
            } else if (i == 1) {
                return PortSide.SOUTH;
            } else {
                return PortSide.EAST;
            }
        case SEN:
            if (i == 0) {
                return PortSide.NORTH;
            } else if (i == 1) {
                return PortSide.EAST;
            } else {
                return PortSide.SOUTH;
            }
        case SWN:
            if (i == 0) {
                return PortSide.NORTH;
            } else if (i == 1) {
                return PortSide.WEST;
            } else {
                return PortSide.SOUTH;
            }
        default:
            return PortSide.UNDEFINED;
        }
    }
}
