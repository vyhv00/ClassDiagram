/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vse.classdiagrampainter.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

@ActionID(
        category = "Diagram",
        id = "org.vse.classdiagrampainter.CreateDiagramAction"
)
@ActionRegistration(
        displayName = "#CTL_CreateDiagramAction"
)
@ActionReferences({
    @ActionReference(path = "Projects/package/Actions", position = Integer.MAX_VALUE-3, separatorBefore = Integer.MAX_VALUE-4)
})
@Messages("CTL_CreateDiagramAction=Create Class Diagram")
public final class CreateDiagramAction extends DiagramAction {

    public CreateDiagramAction() {
        this(Utilities.actionsGlobalContext());
    }

    public CreateDiagramAction(Lookup context) {
        super(context.lookup(FileObject.class));
        putValue(Action.NAME, "Create Class Diagram");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        try {
            super.getWindow().createGraph();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (!super.getWindow().isOpened()) {
            super.getWindow().open();
        }
        super.getWindow().requestActive();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new CreateDiagramAction(context);
    }
}
