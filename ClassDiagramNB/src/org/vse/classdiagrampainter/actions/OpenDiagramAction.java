/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vse.classdiagrampainter.actions;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.vse.classdiagrampainter.gui.WindowDiaTopComponent;

@ActionID(
        category = "Diagram",
        id = "org.vse.classdiagrampainter.OpenDiagramAction"
)
@ActionRegistration(
        displayName = "#CTL_OpenDiagramAction"
)
@ActionReferences({
    @ActionReference(path = "Projects/package/Actions", position = Integer.MAX_VALUE-2, separatorAfter = Integer.MAX_VALUE-1)
})
@Messages("CTL_OpenDiagramAction=Open Class Diagram")
public final class OpenDiagramAction extends DiagramAction {
    
    public OpenDiagramAction() {
        this(Utilities.actionsGlobalContext());
    }
    
    public OpenDiagramAction(Lookup context) {
        super(context.lookup(FileObject.class));
        putValue(Action.NAME, "Open Class Diagram");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (!super.getWindow().isOpened()) {
            super.getWindow().openGraph();
            super.getWindow().open();
        }
        super.getWindow().requestActive();
    }
    
    @Override
    public boolean isEnabled() {
        return WindowDiaTopComponent.openable(FileUtil.toFile(super.getPkgFile()).getAbsolutePath());
    }
    
    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new OpenDiagramAction(context);
    }
}
