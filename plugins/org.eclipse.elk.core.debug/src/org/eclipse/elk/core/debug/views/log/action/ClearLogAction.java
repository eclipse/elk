package org.eclipse.elk.core.debug.views.log.action;

import org.eclipse.elk.core.debug.ElkDebugPlugin;
import org.eclipse.elk.core.debug.views.log.LayoutLogView;
import org.eclipse.jface.action.Action;

/**
 * Action that clears all debug logs in the debug log view.
 */
public class ClearLogAction extends Action {

    /** Identifier string for this action. */
    private static final String ACTION_ID = "org.eclipse.elk.debug.log.clearLog";
    /** Relative path to the icon to use for this action. */
    private static final String ICON_PATH = "icons/clear.gif";
    
    /** The debug log view associated with this action. */
    private LayoutLogView view;

    
    /**
     * Create a clearing action for the given debug log view.
     * 
     * @param view for debug logs that created this action.
     */
    public ClearLogAction(final LayoutLogView view) {
        this.view = view;
        setId(ACTION_ID);
        setText("&Clear");
        setToolTipText("Clears all logs.");
        setImageDescriptor(ElkDebugPlugin.imageDescriptorFromPlugin(ElkDebugPlugin.PLUGIN_ID, ICON_PATH));
    }

    @Override
    public void run() {
        view.clearDebugLogs();
    }

}
