package org.eclipse.elk.core.debug.views.log;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.elk.core.debug.LayoutExecutionInfo;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Content provider for the layout log view. This content provider can filter execution infos based on whether they are
 * on a path towards actual log entries.
 */
public class LogContentProvider implements ITreeContentProvider {

    /** Whether to filter out elements that are not on a path to an element with log messages. */
    private boolean filter = false;
    /** A predicate that applies our filter, if active, or lets all elements pass otherwise. */
    private final Predicate<LayoutExecutionInfo> filterPredicate =
            (LayoutExecutionInfo info) -> !filter || info.hasLogMessages() || info.hasDescendantsWithLogMessages();
    
    /**
     * Determines whether this content provider filters out elements that are not on a path towards elements that have
     * log entries.
     */
    public boolean isFiltering() {
        return this.filter;
    }
            
    /**
     * Determines whether this content provider filters out elements that are not on a path towards elements that have
     * log entries.
     */
    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Content Provider

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List<?>) {
            return ((List<?>) inputElement).stream().map(o -> (LayoutExecutionInfo) o).filter(filterPredicate)
                    .toArray();
        } else {
            return new Object[0];
        }
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof LayoutExecutionInfo) {
            // If our filter is active, filter out those that are not on a path towards log messages
            return ((LayoutExecutionInfo) parentElement).getChildren().stream().filter(filterPredicate).toArray();

        } else {
            return new LayoutExecutionInfo[0];
        }
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof LayoutExecutionInfo) {
            return ((LayoutExecutionInfo) element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof LayoutExecutionInfo) {
            // If our filter is turned on, this thing only has children if at least one of them has log items or has
            // ancestors with log items
            return ((LayoutExecutionInfo) element).getChildren().stream().anyMatch(filterPredicate);

        } else {
            return false;
        }
    }

}
