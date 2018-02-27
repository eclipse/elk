/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.sequence.graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.elk.core.math.ElkMargin;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.LabelSide;
import org.eclipse.elk.core.util.adapters.GraphAdapters.EdgeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.GraphElementAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.LabelAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.NodeAdapter;
import org.eclipse.elk.core.util.adapters.GraphAdapters.PortAdapter;
import org.eclipse.elk.graph.properties.IProperty;

import com.google.common.collect.Lists;

/**
 * Implements adapaters for the SGraph data structure to be used with ELK's node size calculation.
 */
public final class SGraphAdapters {

    /**
     * Creates an adapter for the graph.
     * 
     * @param sgraph
     *            the graph to adapt.
     * @return graph adapter.
     */
    public static SGraphAdapter adaptGraph(final SGraph sgraph) {
        return new SGraphAdapter(sgraph);
    }

    /**
     * Creates an adapter that adapts the given comment.
     * 
     * @param scomment
     *            the comment to adapt.
     * @param sgraphAdapter
     *            a possibly {@code null} adapter that adapts the {@link SGraph} the comment is part of. Supplying one
     *            makes it easier for ELK's algorithms to find default spacings in the absence of spacing overrides on
     *            comments.
     * @return comment adapter.
     */
    public static SCommentAdapter adaptComment(final SComment scomment, final SGraphAdapter sgraphAdapter) {
        return new SCommentAdapter(scomment, sgraphAdapter);
    }

    /**
     * Adapts an {@link SGraph} for use with ELK's core algorithms.
     */
    public static class SGraphAdapter implements GraphAdapter<SGraph> {
        private SGraph sgraph;
        private int volatileId = 0;

        private SGraphAdapter(final SGraph sgraph) {
            this.sgraph = sgraph;
        }

        @Override
        public KVector getSize() {
            return sgraph.getSize();
        }

        @Override
        public void setSize(KVector size) {
            sgraph.getSize().set(size);
        }

        @Override
        public KVector getPosition() {
            throw new UnsupportedOperationException("Not supported by LGraph");
        }

        @Override
        public void setPosition(KVector pos) {
            throw new UnsupportedOperationException("Not supported by LGraph");
        }

        @Override
        public <P> P getProperty(IProperty<P> prop) {
            return sgraph.getProperty(prop);
        }

        @Override
        public <P> boolean hasProperty(IProperty<P> prop) {
            return sgraph.hasProperty(prop);
        }

        @Override
        public int getVolatileId() {
            return volatileId;
        }

        @Override
        public void setVolatileId(int volatileId) {
            this.volatileId = volatileId;
        }

        @Override
        public Iterable<NodeAdapter<?>> getNodes() {
            return Collections.emptyList();
        }
    }

    /**
     * Super class for all adapters that adapt subclass of {@link SShape}.
     * 
     * @param <E>
     *            the {@link SShape} subclass that should be adapted.
     */
    private static class SGraphElementAdapter<E extends SShape> implements GraphElementAdapter<E> {
        protected final E sshape;
        private int volatileId = 0;

        public SGraphElementAdapter(final E sshape) {
            this.sshape = sshape;
        }

        @Override
        public KVector getSize() {
            return sshape.getSize();
        }

        @Override
        public void setSize(KVector size) {
            sshape.getSize().set(size);
        }

        @Override
        public KVector getPosition() {
            return sshape.getPosition();
        }

        @Override
        public void setPosition(KVector pos) {
            sshape.getPosition().set(pos);
        }

        @Override
        public <P> P getProperty(IProperty<P> prop) {
            return sshape.getProperty(prop);
        }

        @Override
        public <P> boolean hasProperty(IProperty<P> prop) {
            return sshape.hasProperty(prop);
        }

        @Override
        public int getVolatileId() {
            return volatileId;
        }

        @Override
        public void setVolatileId(int volatileId) {
            this.volatileId = volatileId;
        }
    }

    /**
     * Adapts an {@link SComment} for use with ELK's core algorithms.
     */
    public static final class SCommentAdapter extends SGraphElementAdapter<SComment> implements NodeAdapter<SComment> {

        /** The adapter that adapts the graph the comment is part of. */
        private SGraphAdapter sgraphAdapter;
        /** List of cached label adapters. */
        private List<LabelAdapter<?>> labelAdapters = null;

        private SCommentAdapter(final SComment scomment, final SGraphAdapter sgraphAdapter) {
            super(scomment);

            this.sgraphAdapter = sgraphAdapter;
        }

        @Override
        public GraphAdapter<?> getGraph() {
            return sgraphAdapter;
        }

        @Override
        public Iterable<LabelAdapter<?>> getLabels() {
            if (labelAdapters == null) {
                if (sshape.getLabel() != null) {
                    labelAdapters = Lists.newArrayList(new SLabelAdapter(sshape.getLabel()));
                } else {
                    labelAdapters = Collections.emptyList();
                }
            }
            return labelAdapters;
        }

        @Override
        public Iterable<PortAdapter<?>> getPorts() {
            return Collections.emptyList();
        }

        @Override
        public Iterable<EdgeAdapter<?>> getIncomingEdges() {
            return Collections.emptyList();
        }

        @Override
        public Iterable<EdgeAdapter<?>> getOutgoingEdges() {
            return Collections.emptyList();
        }

        @Override
        public void sortPortList() {
            // We don't have ports
        }

        @Override
        public void sortPortList(Comparator<?> comparator) {
            // We don't have ports
        }

        @Override
        public boolean isCompoundNode() {
            return false;
        }

        @Override
        public ElkPadding getPadding() {
            return new ElkPadding();
        }

        @Override
        public void setPadding(ElkPadding padding) {
            // We don't have a padding
        }

        @Override
        public ElkMargin getMargin() {
            return new ElkMargin();
        }

        @Override
        public void setMargin(ElkMargin margin) {
            // We don't have a margin
        }
    }

    /**
     * Adapts an {@link SLabel} for use with ELK's core algorithms.
     */
    public static final class SLabelAdapter extends SGraphElementAdapter<SLabel> implements LabelAdapter<SLabel> {
        public SLabelAdapter(SLabel slabel) {
            super(slabel);
        }

        @Override
        public LabelSide getSide() {
            return null;
        }
    }
}
