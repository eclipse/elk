/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.config;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.elk.core.LayoutAlgorithmData;
import org.eclipse.elk.core.LayoutMetaDataService;
import org.eclipse.elk.core.LayoutOptionData;
import org.eclipse.elk.graph.KEdge;
import org.eclipse.elk.graph.KGraphElement;
import org.eclipse.elk.graph.KLabel;
import org.eclipse.elk.graph.KNode;
import org.eclipse.elk.graph.KPort;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.elk.graph.util.KGraphSwitch;

/**
 * Default implementation of the layout configuration interface. This configuration handles the
 * default values of layout algorithms and layout options.
 *
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating yellow 2012-07-05 review KI-18 by cmot, sgu
 */
public class DefaultLayoutConfig implements ILayoutConfig {
    
    /** the priority for the default layout configuration. */
    public static final int PRIORITY = 0;
    
    /** the layout options that are supported by the active layout algorithm. */
    public static final IProperty<List<LayoutOptionData>> OPTIONS
            = new Property<List<LayoutOptionData>>("context.options");
    
    /** the layout algorithm or type identifier for the content of the current graph element. */
    public static final IProperty<String> CONTENT_HINT = new Property<String>(
            "context.contentHint");
    
    /** the diagram type identifier for the content of the current graph element. */
    public static final IProperty<String> CONTENT_DIAGT = new Property<String>(
            "context.contentDiagramType");
    
    /** the layout algorithm that is responsible for the content of the current graph element. */
    public static final IProperty<LayoutAlgorithmData> CONTENT_ALGO
            = new Property<LayoutAlgorithmData>("context.contentAlgorithm");
    
    /** the layout algorithm or type identifier for the container of the current graph element. */
    public static final IProperty<String> CONTAINER_HINT = new Property<String>(
            "context.containerHint");
    
    /** the diagram type identifier for the container of the current graph element. */
    public static final IProperty<String> CONTAINER_DIAGT = new Property<String>(
            "context.containerDiagramType");
    
    /** the layout algorithm that is assigned to the container of the current graph element. */ 
    public static final IProperty<LayoutAlgorithmData> CONTAINER_ALGO
            = new Property<LayoutAlgorithmData>("context.containerAlgorithm");
    
    /** whether the node in the current context contains any ports. */
    public static final IProperty<Boolean> HAS_PORTS = new Property<Boolean>(
            "context.hasPorts", false);

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return PRIORITY;
    }
    
    /**
     * A switch class for KGraph elements that determines the layout option targets.
     */
    public static class OptionTargetSwitch extends KGraphSwitch<Set<LayoutOptionData.Target>> {
        
        @Override
        public Set<LayoutOptionData.Target> caseKNode(final KNode node) {
            Set<LayoutOptionData.Target> targets = EnumSet.noneOf(LayoutOptionData.Target.class);
            if (node.getParent() != null) {
                targets.add(LayoutOptionData.Target.NODES);
            }
            if (node.getChildren().size() != 0) {
                targets.add(LayoutOptionData.Target.PARENTS);
            }
            return targets;
        }
        
        @Override
        public Set<LayoutOptionData.Target> caseKEdge(final KEdge edge) {
            return EnumSet.of(LayoutOptionData.Target.EDGES);
        }
        
        @Override
        public Set<LayoutOptionData.Target> caseKPort(final KPort port) {
            return EnumSet.of(LayoutOptionData.Target.PORTS);
        }
        
        @Override
        public Set<LayoutOptionData.Target> caseKLabel(final KLabel label) {
            return EnumSet.of(LayoutOptionData.Target.LABELS);
        }
        
    };
    
    /** the cached switch for computing layout option targets. */
    private OptionTargetSwitch kgraphSwitch = new OptionTargetSwitch();

    /**
     * {@inheritDoc}
     */
    public Object getContextValue(final IProperty<?> property, final LayoutContext context) {
        if (property.equals(HAS_PORTS)) {
            // determine whether the node in the context has any ports
            KGraphElement graphElement = context.getProperty(LayoutContext.GRAPH_ELEM);
            if (graphElement instanceof KNode) {
                return !((KNode) graphElement).getPorts().isEmpty();
            }
            
        } else if (property.equals(LayoutContext.OPT_TARGETS)) {
            // determine the layout option targets from the type of graph element
            KGraphElement graphElement = context.getProperty(LayoutContext.GRAPH_ELEM);
            if (graphElement != null) {
                return kgraphSwitch.doSwitch(graphElement);
            }
            
        } else if (property.equals(CONTENT_ALGO)) {
            // determine the content layout algorithm from the layout hint and diagram type
            if (isParentElement(context)) {
                return getLayouterData(context.getProperty(CONTENT_HINT),
                        context.getProperty(CONTENT_DIAGT));
            }
            
        } else if (property.equals(CONTAINER_ALGO)) {
            // determine the container layout algorithm from the layout hint and diagram type
            if (isContainedElement(context)) {
                return getLayouterData(context.getProperty(CONTAINER_HINT),
                        context.getProperty(CONTAINER_DIAGT));
            }
            
        } else if (property.equals(OPTIONS)) {
            // build a list of layout options supported by the layout algorithms in the context
            LayoutMetaDataService layoutDataService = LayoutMetaDataService.getInstance();
            List<LayoutOptionData> optionData = new LinkedList<LayoutOptionData>();
            LayoutAlgorithmData algoData;
            algoData = context.getProperty(CONTENT_ALGO);
            if (algoData != null) {
                optionData.addAll(layoutDataService.getOptionData(algoData,
                        LayoutOptionData.Target.PARENTS));
            }
            algoData = context.getProperty(CONTAINER_ALGO);
            Set<LayoutOptionData.Target> optionTargets = context.getProperty(LayoutContext.OPT_TARGETS);
            if (algoData != null && optionTargets != null) {
                for (LayoutOptionData.Target target : optionTargets) {
                    if (target != LayoutOptionData.Target.PARENTS) {
                        optionData.addAll(layoutDataService.getOptionData(algoData, target));
                    }
                }
            }
            return optionData;
        }
        
        // ask the context - maybe the requested value is stored there
        return context.getProperty(property);
    }
    
    /**
     * Determine whether the {@link LayoutContext#OPT_TARGETS} property of the given context
     * contains the {@code PARENTS} target.
     * 
     * @param context a layout context
     * @return true if the parent target is contained in the context
     */
    private static boolean isParentElement(final LayoutContext context) {
        Set<LayoutOptionData.Target> optionTargets = context.getProperty(LayoutContext.OPT_TARGETS);
        return optionTargets != null && optionTargets.contains(LayoutOptionData.Target.PARENTS);
    }

    /**
     * Determine whether the {@link LayoutContext#OPT_TARGETS} property of the given context
     * contains any target related to a graph element (other than {@code PARENTS}).
     * 
     * @param context a layout context
     * @return true if a graph element target is contained in the context
     */
    private static boolean isContainedElement(final LayoutContext context) {
        Set<LayoutOptionData.Target> optionTargets = context.getProperty(LayoutContext.OPT_TARGETS);
        return optionTargets != null && (
                optionTargets.contains(LayoutOptionData.Target.NODES)
                || optionTargets.contains(LayoutOptionData.Target.EDGES)
                || optionTargets.contains(LayoutOptionData.Target.PORTS)
                || optionTargets.contains(LayoutOptionData.Target.LABELS));
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getOptionValue(final LayoutOptionData optionData, final LayoutContext context) {
        Object result = null;
        
        // check default value of the content layout algorithm
        LayoutAlgorithmData contentAlgoData = context.getProperty(CONTENT_ALGO);
        if (contentAlgoData != null && optionData.getTargets().contains(
                LayoutOptionData.Target.PARENTS)) {
            result = contentAlgoData.getDefaultValue(optionData);
            if (result != null) {
                return result;
            }
        }

        // check default value of the container layout provider
        LayoutAlgorithmData containerAlgoData = context.getProperty(CONTAINER_ALGO);
        if (containerAlgoData != null) {
            result = containerAlgoData.getDefaultValue(optionData);
            if (result != null) {
                return result;
            }
        }
        
        // fall back to default value of the option itself
        result = optionData.getDefault();
        if (result != null) {
            return result;
        }
        
        // fall back to default-default value
        return optionData.getDefaultDefault();
    }
    
    /**
     * Determine the most appropriate layout algorithm for the given layout hint and diagram type.
     * If no layout algorithms are registered, this returns {@code null}.
     * 
     * @param theLayoutHint either a layout algorithm identifier,
     *          or a layout type identifier, or {@code null}
     * @param diagramType a diagram type identifier
     * @return the most appropriate layout algorithm
     */
    public static LayoutAlgorithmData getLayouterData(final String theLayoutHint,
            final String diagramType) {
        String chDiagType = (diagramType == null || diagramType.length() == 0)
                ? LayoutMetaDataService.DIAGRAM_TYPE_GENERAL : diagramType;
        LayoutMetaDataService layoutServices = LayoutMetaDataService.getInstance();
        String layoutHint = theLayoutHint;
        
        // try to get a specific provider for the given hint
        LayoutAlgorithmData directHitData = layoutServices.getAlgorithmData(layoutHint);
        if (directHitData != null) {
            return directHitData;
        }

        // look for the provider with highest priority, interpreting the hint as layout type
        LayoutAlgorithmData bestAlgo = null;
        int bestPrio = LayoutAlgorithmData.MIN_PRIORITY;
        boolean matchesLayoutType = false, matchesDiagramType = false, matchesGeneralDiagram = false;
        for (LayoutAlgorithmData currentAlgo : layoutServices.getAlgorithmData()) {
            int currentPrio = currentAlgo.getDiagramSupport(chDiagType);
            String layoutType = currentAlgo.getType();
            if (matchesLayoutType) {
                if (layoutType.length() > 0 && layoutType.equals(layoutHint)) {
                    if (matchesDiagramType) {
                        if (currentPrio > bestPrio) {
                            // the algorithm matches the layout type hint and has higher priority for
                            // the given diagram type than the previous one
                            bestAlgo = currentAlgo;
                            bestPrio = currentPrio;
                        }
                    } else {
                        if (currentPrio > LayoutAlgorithmData.MIN_PRIORITY) {
                            // the algorithm matches the layout type hint and has higher priority
                            // for the given diagram type than the previous one
                            bestAlgo = currentAlgo;
                            bestPrio = currentPrio;
                            matchesDiagramType = true;
                            matchesGeneralDiagram = false;
                        } else {
                            currentPrio = currentAlgo.getDiagramSupport(
                                    LayoutMetaDataService.DIAGRAM_TYPE_GENERAL);
                            if (matchesGeneralDiagram) {
                                if (currentPrio > bestPrio) {
                                    // the algorithm matches the layout type hint and has higher
                                    // priority for general diagrams than the previous one
                                    bestAlgo = currentAlgo;
                                    bestPrio = currentPrio;
                                }
                            } else {
                                if (currentPrio > LayoutAlgorithmData.MIN_PRIORITY) {
                                    // the algorithm has a priority for general diagrams,
                                    // while the previous one did not have it
                                    bestAlgo = currentAlgo;
                                    bestPrio = currentPrio;
                                    matchesGeneralDiagram = true;
                                } else if (bestAlgo == null) {
                                    bestAlgo = currentAlgo;
                                }
                            }
                        }
                    }
                }
            } else {
                if (layoutType.length() > 0 && layoutType.equals(layoutHint)) {
                    // the first algorithm that matches the layout type hint is found
                    bestAlgo = currentAlgo;
                    matchesLayoutType = true;
                    if (currentPrio > LayoutAlgorithmData.MIN_PRIORITY) {
                        // the algorithm supports the given diagram type
                        bestPrio = currentPrio;
                        matchesDiagramType = true;
                        matchesGeneralDiagram = false;
                    } else {
                        matchesDiagramType = false;
                        currentPrio = currentAlgo.getDiagramSupport(
                                LayoutMetaDataService.DIAGRAM_TYPE_GENERAL);
                        if (currentPrio > LayoutAlgorithmData.MIN_PRIORITY) {
                            // the algorithm does not support the given diagram type, but
                            // has a priority for general diagrams
                            bestPrio = currentPrio;
                            matchesGeneralDiagram = true;
                        } else {
                            matchesGeneralDiagram = false;
                        }
                    }
                } else {
                    if (matchesDiagramType) {
                        if (currentPrio > bestPrio) {
                            // the algorithm has higher priority for the given diagram type
                            // than the previous one
                            bestAlgo = currentAlgo;
                            bestPrio = currentPrio;
                        }
                    } else {
                        if (currentPrio > LayoutAlgorithmData.MIN_PRIORITY) {
                            // the first algorithm that matches the given diagram type is found
                            bestAlgo = currentAlgo;
                            bestPrio = currentPrio;
                            matchesDiagramType = true;
                            matchesGeneralDiagram = false;
                        } else {
                            currentPrio = currentAlgo.getDiagramSupport(
                                    LayoutMetaDataService.DIAGRAM_TYPE_GENERAL);
                            if (matchesGeneralDiagram) {
                                if (currentPrio > bestPrio) {
                                    // the algorithm has higher priority for general diagrams
                                    // than the previous one
                                    bestAlgo = currentAlgo;
                                    bestPrio = currentPrio;
                                }
                            } else {
                                if (currentPrio > LayoutAlgorithmData.MIN_PRIORITY) {
                                    // the first algorithm with a priority for general diagrams is found
                                    bestAlgo = currentAlgo;
                                    bestPrio = currentPrio;
                                    matchesGeneralDiagram = true;
                                } else if (bestAlgo == null) {
                                    // if no match is found, the first algorithm in the list is returned
                                    bestAlgo = currentAlgo;
                                }
                            }
                        }
                    }
                }
            }
        }
        return bestAlgo;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<IProperty<?>> getAffectedOptions(final LayoutContext context) {
        return Collections.emptyList();
    }

}
