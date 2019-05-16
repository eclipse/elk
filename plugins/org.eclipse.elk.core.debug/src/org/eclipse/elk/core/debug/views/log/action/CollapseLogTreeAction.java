package org.eclipse.elk.core.debug.views.log.action;

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.elk.core.debug.views.log.LayoutLogView;
import org.eclipse.jface.action.Action;

/**
 * Action for collapsing all elements of the tree view in the debug log view.
 */
public class CollapseLogTreeAction extends Action {
    
    /** Identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.log.collapseLogTree";
    /** Relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/collapse_all.gif";
    
    /** The debug log view associated with this action. */
    private LayoutLogView view;

    
    /**
     * Create a collapsing action for the given debug log view.
     * 
     * @param view debug log view that created this action.
     */
    public CollapseLogTreeAction(final LayoutLogView view) {
        this.view = view;
        setId(ACTION_ID);
        setText("&Collapse All");
        setToolTipText("Collapses every element of the tree viewer.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        view.collapseAllTreeViewerElements();
    }
}
