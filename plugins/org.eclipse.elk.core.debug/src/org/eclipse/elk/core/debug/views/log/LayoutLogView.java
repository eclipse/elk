package org.eclipse.elk.core.debug.views.log;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.debug.LayoutExecutionInfo;
import org.eclipse.elk.core.debug.views.log.action.ClearLogAction;
import org.eclipse.elk.core.debug.views.log.action.CollapseLogTreeAction;
import org.eclipse.elk.core.debug.views.log.action.ExpandLogTreeAction;
import org.eclipse.elk.core.debug.views.log.action.FilterLogAction;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This view displays the debug logs of an layout algorithm.
 * 
 * TODO Remove single items with actions
 * TODO Support the delete and backspace buttons to remove single items
 * TODO Listen for font preference changes
 */
public class LayoutLogView extends ViewPart {

    /** The view identifier. */
    public static final String VIEW_ID = "org.eclipse.elk.core.debug.logView";

    /** The tree viewer used to display content. */
    private TreeViewer treeViewer;
    /** Our content provider for the tree viewer. */
    private LogContentProvider treeContentProvider;
    /** The text view. */
    private Text logViewer;
    /** List of execution infos to be displayed. */
    private List<LayoutExecutionInfo> executionInfos = new ArrayList<>();
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 

    /**
     * Adds logs to the view and displays it.
     * 
     * @param progressMonitor
     *            holding the debug information.
     */
    public static void addLogs(IElkProgressMonitor progressMonitor) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                LayoutLogView activeView = findView();
                if (activeView != null) {
                    activeView.executionInfos.add(LayoutExecutionInfo.fromProgressMonitor(progressMonitor));
                    activeView.treeViewer.refresh();
                }
            }
        });
    }

    /**
     * Tries to find the relevant, currently open view.
     */
    private static LayoutLogView findView() {
        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow == null) {
            return null;
        }

        IWorkbenchPage activePage = activeWindow.getActivePage();
        if (activePage == null) {
            return null;
        }

        // find debug log view
        IViewPart viewPart = activePage.findView(LayoutLogView.VIEW_ID);
        if (viewPart instanceof LayoutLogView) {
            return (LayoutLogView) viewPart;
        }

        return null;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Action Methods

    /**
     * Clears all debug logs and updates the tree viewer.
     */
    public void clearDebugLogs() {
        executionInfos.clear();
        treeViewer.refresh();
        logViewer.setText("");
    }

    /**
     * Collapses all elements of the tree viewer.
     */
    public void collapseAllTreeViewerElements() {
        logViewer.setText("");
        treeViewer.collapseAll();
    }

    /**
     * Expand all elements of the tree viewer.
     */
    public void expandAllTreeViewerElements() {
        treeViewer.expandAll();
    }

    /**
     * Register filtered input for the tree viewer and refresh to show
     */
    public void showFilteredLogs() {
        treeContentProvider.setFilter(true);
        treeViewer.refresh();
    }

    /**
     * Register unfiltered input for the tree viewer and refresh to show
     */
    public void showUnfilteredLogs() {
        treeContentProvider.setFilter(false);
        treeViewer.refresh();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User Interface

    @Override
    public void createPartControl(Composite parent) {
        // Parent layout
        GridLayout gl = new GridLayout(1, false);
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        parent.setLayout(gl);

        setUpToolBar();
        
        // TODO Create the info bar
        
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        setUpTreeViewer(sashForm);
        setUpLogView(sashForm);
    }

    private void setUpToolBar() {
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(new FilterLogAction(this));
        toolBarManager.add(new ExpandLogTreeAction(this));
        toolBarManager.add(new CollapseLogTreeAction(this));
        toolBarManager.add(new ClearLogAction(this));
    }

    private void setUpTreeViewer(Composite parent) {
        treeViewer = new TreeViewer(parent);
        treeContentProvider = new LogContentProvider();
        treeViewer.setContentProvider(treeContentProvider);
        treeViewer.setInput(executionInfos);
        treeViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new LogLabelProvider()));
        
        // React to selection changes by 
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateLogViewer(event.getStructuredSelection());
            }
        });
    }

    private void setUpLogView(Composite parent) {
        logViewer = new Text(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        logViewer.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        logViewer.setEditable(false);
    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
        logViewer.setFocus();
    }
    
    private void updateLogViewer(IStructuredSelection treeSelection) {
        String text = "";
        
        for (Object o : treeSelection.toList()) {
            LayoutExecutionInfo container = (LayoutExecutionInfo) o;
            if (!container.getLogMessages().isEmpty()) {
                text += String.join("\n", container.getLogMessages()) + "\n\n";
            }
        }

        logViewer.setText(text);
    }

}
