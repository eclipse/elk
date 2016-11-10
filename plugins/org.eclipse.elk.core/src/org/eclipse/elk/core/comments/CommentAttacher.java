/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.comments;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Main class of the comment attachment framework. Comment attachment infers the relation between nodes
 * marked as representing comments (through the {@link CoreOptions#COMMENT_BOX} property) and nodes
 * they refer to. Running the framework results in edges being introduced between these pairs of nodes,
 * which allows layout algorithms to place them close to each other. Not doing this would usually lead
 * to comments and nodes not being placed close to each other, thereby breaking the visual link between
 * them.
 * 
 * <p>
 * The comment attachment framework mainly consists of this class and the following interfaces to make
 * the framework customizable to each use case:
 * </p>
 * <ul>
 *   <li>
 *     {@link IExplicitAttachmentProvider}<br/>
 *     Some visual languages allow developers to establish explicit attachments between comments and
 *     nodes. An {@link IExplicitAttachmentProvider} knows how to retrieve such explicit attachments.
 *     Clients can configure whether the presence of explicit attachments in a diagram should disable
 *     heuristic attachments. After all, if the developer already defined some explicit attachments,
 *     chances are he wanted the other comments to be unattached.
 *   </li>
 *   <li>
 *     {@link IBoundsProvider}<br/>
 *     Comment attachment often includes looking at the size and position of comments. A bounds provider
 *     knows how to provide these information for comments and nodes.
 *   </li>
 *   <li>
 *     {@link IAttachmentTargetProvider}<br/>
 *     By default, all non-comment siblings of a comment are considered as possible attachment targets.
 *     However, implementations of this interface can be used to limit the considered attachment targets
 *     to a smaller number to achieve speedups or things. Clients usually won't need to provide a
 *     custom implementation.
 *   </li>
 *   <li>
 *     {@link IEligibilityFilter}<br/>
 *     Some comments clearly refer to a diagram as a whole. Eligibility filters filter these out to
 *     keep them from being attached to anything. Examples are comments that identify the authors of
 *     a diagram.
 *   </li>
 *   <li>
 *     {@link IHeuristic}<br/>
 *     Heuristics provide a heuristic assessment as to whether a given comment-node pair is likely to
 *     be attached or not. Heuristics can be based on a lot of different metrics, but have to provide
 *     a way of normalizing their results to {@code [0, 1]}. Attachment decisions are based on the
 *     values computed by heuristics.
 *   </li>
 *   <li>
 *     {@link IAttachmentDecider}<br/>
 *     Attachment deciders have the final say on which attachment target a comment will be attached to.
 *   </li>
 * </ul>
 * 
 * <h3>Configuration</h3>
 * <p>
 * All of the different aspects of comment attachment can be configured through the configuration
 * methods provided by this class. Each configuration method documents the default used if it is not
 * called. Most aspects provide sensible defaults, but you will want to at least take a look at the
 * following configuration methods:
 * </p>
 * <ul>
 *   <li>
 *     {@link #withExplicitAttachmentProvider(IExplicitAttachmentProvider)}<br/>
 *     Even if no heuristics are configured, explicit attachments may be of interest.
 *   </li>
 *   <li>
 *     {@link #addEligibilityFilter(IEligibilityFilter)}<br/>
 *     Filtering out standalone comments will probably be of interest.
 *   </li>
 *   <li>
 *     {@link #addHeuristic(IHeuristic)}<br/>
 *     Heuristics are the backbone of the comment attachment framework. Unless you only want to add
 *     explicit attachments, you will want to add heuristics to base attachment decisions on.
 *   </li>
 * </ul>
 */
public final class CommentAttacher {
    
    /** Whether to go down on the graph or not. */
    private boolean includeHierarchy = true;
    /** Whether the presence of explicit attachments disables the heuristic attachments. */
    private boolean explicitAttachmentsDisableHeuristics = true;
    /** Retrieves graph elements explicitly attached to a comment by the user. */
    private IExplicitAttachmentProvider explicitAttachmentProvider = (a) -> null;
    /** The bounds provider to be used. */
    private IBoundsProvider boundsProvider = new ShapeLayoutBoundsProvider();
    /** The attachment target provider. */
    private IAttachmentTargetProvider targetProvider = new SiblingAttachmentTargetProvider();
    /** List of eligibility filters. */
    private List<IEligibilityFilter> eligibilityFilters = Lists.newArrayList();
    /** List of attachment heuristics. */
    private List<IHeuristic> heuristics = Lists.newArrayList();
    /** The attachment decider. */
    private IAttachmentDecider attachmentDecider = new AggregatedHeuristicsAttachmentDecider();
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures comment attachment to only attach comments on the current hierarchy level.
     * 
     * <p>
     * If this method is not called, comments on all hierarchy levels below the one passed to the
     * attacher are attached.
     * </p>
     * 
     * @return this comment attacher for method chaining.
     */
    public CommentAttacher limitToCurrentHierarchyLevel() {
        includeHierarchy = false;
        return this;
    }
    
    /**
     * Configures comment attachment to keep using heuristics to attach comments even if explicit
     * attachments are found.
     * 
     * <p>
     * If this method is not called, heuristics are disabled if explicit attachments are found.
     * </p>
     * 
     * @return this comment attacher for method chaining.
     */
    public CommentAttacher keepHeuristicsEnabledWithExplicitAttachments() {
        explicitAttachmentsDisableHeuristics = false;
        return this;
    }
    
    /**
     * Configures comment attachment to use the given explicit attachment provider. See the class
     * documentation for more information on how explicit attachments are handled.
     * 
     * <p>
     * If this method is not called, no explicit attachments are recognized by default.
     * </p>
     * 
     * @param provider
     *            the explicit attachment provider. Can be {@code null} to disable explicit
     *            attachments.
     * @return this comment attacher for method chaining.
     */
    public CommentAttacher withExplicitAttachmentProvider(final IExplicitAttachmentProvider provider) {
        if (provider == null) {
            explicitAttachmentProvider = (a) -> null;
        } else {
            explicitAttachmentProvider = provider;
        }
        
        return this;
    }
    
    /**
     * Configures comment attachment to use the given bounds provider. See the class documentation
     * for more information on bounds providers.
     * 
     * <p>
     * If this method is not called, the {@link ShapeLayoutBoundsProvider} is used by default.
     * </p>
     * 
     * @param provider
     *            the non-{@code null} bounds provider.
     * @return this comment attacher for method chaining.
     * @throws IllegalArgumentException
     *             if {@code provider == null}.
     */
    public CommentAttacher withBoundsProvider(final IBoundsProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("The bounds provider must not be null.");
        } else {
            boundsProvider = provider;
        }
        
        return this;
    }
    
    /**
     * Configures comment attachment to use the given attachment target provider. See the class
     * documentation for more information on attachment target providers.
     * 
     * <p>
     * If this method is not called, the {@link SiblingAttachmentTargetProvider} is used by default,
     * configured such that other comments are not considered valid attachment targets.
     * </p>
     * 
     * @param provider
     *            the non-{@code null} attachment target provider.
     * @return this comment attacher for method chaining.
     * @throws IllegalArgumentException
     *             if {@code provider == null}.
     */
    public CommentAttacher withAttachmentTargetProvider(final IAttachmentTargetProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("The attachment target provider must not be null.");
        } else {
            targetProvider = provider;
        }
        
        return this;
    }
    
    /**
     * Adds the given eligibility filter to the list of eligiblity filters to be used. See the class
     * documentation for more information on eligibility filters.
     * 
     * <p>
     * If this method is not called, all comments are considered eligible for attachment.
     * </p>
     * 
     * @param filter
     *            the non-{@code null} eligibility filter.
     * @return this comment attacher for method chaining.
     * @throws IllegalArgumentException
     *             if {@code filter == null}.
     */
    public CommentAttacher addEligibilityFilter(final IEligibilityFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("The eligibility filter must not be null.");
        } else {
            eligibilityFilters.add(filter);
        }
        
        return this;
    }
    
    /**
     * Adds the given attachment heuristic to the list of heuristics to be used. See the class
     * documentation for more information on eligibility filters.
     * 
     * <p>
     * If this method is not called, no comment will be heuristically attached to anything.
     * </p>
     * 
     * @param heuristic
     *            the non-{@code null} attachment heuristic.
     * @return this comment attacher for method chaining.
     * @throws IllegalArgumentException
     *             if {@code heuristic == null}.
     */
    public CommentAttacher addHeuristic(final IHeuristic heuristic) {
        if (heuristic == null) {
            throw new IllegalArgumentException("The attachment heuristic must not be null.");
        } else {
            heuristics.add(heuristic);
        }
        
        return this;
    }
    
    /**
     * Configures comment attachment to use the given attachment decider. See the class
     * documentation for more information on attachment deciders.
     * 
     * <p>
     * If this method is not called, no comments are ever heuristically attached to anything. This is
     * probably not what you want.
     * </p>
     * 
     * @param decider
     *            the non-{@code null} attachment decider.
     * @return this comment attacher for method chaining.
     * @throws IllegalArgumentException
     *             if {@code decider == null}.
     */
    public CommentAttacher withAttachmentDecider(final IAttachmentDecider decider) {
        if (decider == null) {
            throw new IllegalArgumentException("The attachment target provider must not be null.");
        } else {
            attachmentDecider = decider;
        }
        
        return this;
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Actual Logic
    
    /**
     * Runs the comment attachment heuristic with its current configuration on the graph rooted in
     * the given node. After running the heuristic, edges may have been added to the graph. All such
     * edges are returned by this method for further processing by the caller.
     * 
     * @param graph
     *            the graph to run comment attachment on.
     * @return collection of all edges created to attach comments to nodes. These may need to be
     *         processed further by the caller.
     */
    public Collection<ElkEdge> attachComments(final ElkNode graph) {
        // Do necessary preprocessing
        preprocess(graph);
        
        // We keep track of all comment->graph element pairs we find
        Collection<Pair<ElkNode, ElkGraphElement>> explicitAttachments = Lists.newArrayList();
        Collection<Pair<ElkNode, ElkGraphElement>> heuristicAttachments = Lists.newArrayList();
        
        // Iterate over all nodes we should iterate over
        Queue<ElkNode> processingQueue = Lists.newLinkedList(graph.getChildren());
        while (!processingQueue.isEmpty()) {
            ElkNode node = processingQueue.poll();
            
            // Only comments need to be processed
            if (isComment(node)) {
                // Find explicit attachment
                ElkGraphElement explicitAttachment =
                        explicitAttachmentProvider.findExplicitAttachment(node);
                
                if (explicitAttachment != null) {
                    // We'll use the explicit attachment
                    explicitAttachments.add(Pair.of(node, explicitAttachment));
                    
                } else if ((explicitAttachments.isEmpty() || !explicitAttachmentsDisableHeuristics)
                        && isEligibleForHeuristicAttachment(node)) {
                    
                    // We don't even bother calculating the heuristics if they are disabled by explicit
                    // attachments anyway. If that's not the case, let's roll!
                    ElkGraphElement heuristicAttachment = findHeuristicAttachment(node);
                    
                    if (heuristicAttachment != null) {
                        heuristicAttachments.add(Pair.of(node, heuristicAttachment));
                    }
                }
            }
            
            // If we include hierarchy, check if the current node has children
            if (includeHierarchy) {
                processingQueue.addAll(node.getChildren());
            }
        }
        
        // Tell everyone to clean up after themselves
        cleanup();
        
        // Turn the attachments into edges and return them
        Collection<ElkEdge> createdEdges = edgeifyFoundAttachments(
                explicitAttachments, heuristicAttachments);
        return createdEdges;
    }

    /**
     * Gives everything a chance to preprocess stuff.
     * 
     * @param graph
     *            the graph to attach comments on.
     */
    private void preprocess(final ElkNode graph) {
        explicitAttachmentProvider.preprocess(graph, includeHierarchy);
        boundsProvider.preprocess(graph, includeHierarchy);
        targetProvider.preprocess(graph, includeHierarchy);
        eligibilityFilters.stream().forEach((f) -> f.preprocess(graph, includeHierarchy));
        heuristics.stream().forEach((h) -> h.preprocess(graph, includeHierarchy));
    }

    /**
     * Checks whether the given comment is eligible for heuristically finding attachment targets.
     * 
     * @param comment
     *            the comment to check.
     * @return {@code true} if the comment may be attached to things.
     */
    private boolean isEligibleForHeuristicAttachment(final ElkNode comment) {
        return eligibilityFilters.stream().allMatch((f) -> f.eligibleForAttachment(comment));
    }
    
    /**
     * Runs the attachment heuristics on the given comment and returns the graph element it is to be
     * attached to, if any.
     * 
     * @param comment
     *            the comment to run the attachment heuristics on.
     * @return the graph element the comment is to be attached to, or {@code null} if there isn't
     *         any.
     */
    private ElkGraphElement findHeuristicAttachment(final ElkNode comment) {
        // If there are no heuristics, return nothing...
        if (heuristics.isEmpty()) {
            return null;
        }
        
        // Collect attachment target candidates
        List<ElkGraphElement> candidates = targetProvider.provideAttachmentTargetsFor(comment);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        
        // Collect the heuristic results in this map, indexed by attachment target, then indexed by
        // the heuristic
        Map<ElkGraphElement, Map<Class<? extends IHeuristic>, Double>> results = Maps.newHashMap();
        
        for (ElkGraphElement candidate : candidates) {
            Map<Class<? extends IHeuristic>, Double> candidateResults = Maps.newHashMap();
            results.put(candidate, candidateResults);
            
            // Run the normalized heuristics and collect their results in an array
            for (IHeuristic heuristic : heuristics) {
                candidateResults.put(heuristic.getClass(), heuristic.normalized(comment, candidate));
            }
        }
        
        // Decide which attachment target to attach the comment to
        return attachmentDecider.makeAttachmentDecision(results);
    }
    
    /**
     * Introduces edges into the graph to apply the found attachments. The created edges are
     * returned for further processing by the caller. If there are explicit attachments, the
     * heuristic attachments are only applies if the presence of explicit attachments doesn't
     * disable the heuristics.
     * 
     * @implNote Attachments are currently only applied if the attachment target is a node.
     * 
     * @param explicitAttachments
     *            collection of explicit attachments from comment nodes to graph elements.
     * @param heuristicAttachments
     *            collection of heuristic attachments from comment nodes to graph elements.
     * @return collection of created edges.
     */
    private Collection<ElkEdge> edgeifyFoundAttachments(
            final Collection<Pair<ElkNode, ElkGraphElement>> explicitAttachments,
            final Collection<Pair<ElkNode, ElkGraphElement>> heuristicAttachments) {
        
        Collection<ElkEdge> createdEdges = Lists.newArrayListWithCapacity(
                explicitAttachments.size() + heuristicAttachments.size());
        
        // Explicit attachments
        createdEdges.addAll(edgeifyFoundAttachments(explicitAttachments));
        
        // Only apply heuristic attachments if they are still enabled
        if (explicitAttachments.isEmpty() || !explicitAttachmentsDisableHeuristics) {
            createdEdges.addAll(edgeifyFoundAttachments(heuristicAttachments));
        }
        
        return createdEdges;
    }
    
    /**
     * Introduces edges into the graph to apply the found attachments. The created edges are
     * returned for further processing by the caller.
     * 
     * @implNote Attachments are currently only applied if the attachment target is a node.
     * 
     * @param attachments
     *            collection of attachments from comment nodes to graph elements.
     * @return collection of created edges.
     */
    private Collection<ElkEdge> edgeifyFoundAttachments(final Collection<Pair<ElkNode, ElkGraphElement>> attachments) {
        Collection<ElkEdge> createdEdges = Lists.newArrayListWithCapacity(attachments.size());
        
        for (Pair<ElkNode, ElkGraphElement> attachment : attachments) {
            // We currently only allow nodes as attachment targets
            if (!(attachment.getSecond() instanceof ElkNode)) {
                continue;
            }
            
            ElkNode comment = attachment.getFirst();
            ElkNode target = (ElkNode) attachment.getSecond();
            
            // MIGRATE We may have to check where best to put the edge here
            ElkEdge edge = ElkGraphUtil.createEdge(comment.getParent());
            edge.getSources().add(comment);
            edge.getTargets().add(target);
            
            createdEdges.add(edge);
        }
        
        return createdEdges;
    }
    
    /**
     * Gives everything a chance to cleanup.
     */
    private void cleanup() {
        explicitAttachmentProvider.cleanup();
        boundsProvider.cleanup();
        targetProvider.cleanup();
        eligibilityFilters.stream().forEach((f) -> f.cleanup());
        heuristics.stream().forEach((h) -> h.cleanup());
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities

    /**
     * Checks whether the given node is a comment.
     * 
     * @param node
     *            the node to check.
     * @return {@code true} if the node is a comment.
     */
    public static boolean isComment(final ElkNode node) {
        return node.getProperty(CoreOptions.COMMENT_BOX);
    }
    
}
