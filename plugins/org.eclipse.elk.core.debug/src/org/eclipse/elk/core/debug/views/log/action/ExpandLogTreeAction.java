package org.eclipse.elk.core.debug.views.log.action;

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.elk.core.debug.views.log.LayoutLogView;
import org.eclipse.jface.action.Action;

/**
 * Action for expanding all elements in the tree view of the debug log view. 
 */
public class ExpandLogTreeAction extends Action {
    
    /** Identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.log.expandLogTree";
    /** Relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/expand_all.gif";
    /** The debug output view associated with this action. */
    private LayoutLogView view;
    
    
    /**
     * Create a collapsing action for the given debug log view.
     * 
     * @param view for debug logs that created this action.
     */
    public ExpandLogTreeAction(final LayoutLogView view) {
        this.view = view;
        setId(ACTION_ID);
        setText("&Expand All");
        setToolTipText("Expands every element of the tree viewer.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }

    @Override
    public void run() {
        view.expandAllTreeViewerElements();
    }

}
