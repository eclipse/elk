package org.eclipse.elk.core.debug.views.log.action;

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.elk.core.debug.views.log.LayoutLogView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * Check box action that toggles between filtered and unfiltered logs in the layout log view.
 */
public class FilterLogAction extends Action {
    
    /** Identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.log.filterLog";
    /** Relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/filter.png";
    
    private LayoutLogView view;
    
    public FilterLogAction(LayoutLogView view) {
        super("&Filter", IAction.AS_CHECK_BOX);
        this.view = view;
        setId(ACTION_ID);
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
        setToolTipText("Filters all logs.");
    }
    
    
    @Override
    public void run() {
        if (isChecked()) {
            view.showFilteredLogs();
        } else {
            view.showUnfilteredLogs();
        }
    }
}
