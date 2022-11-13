/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vse.classdiagrampainter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.openide.filesystems.FileObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.vse.classdiagrampainter.gui.WindowDiaTopComponent;

public abstract class DiagramAction extends AbstractAction implements ContextAwareAction, ActionListener {

    private final FileObject pkgFileObject;
    private WindowDiaTopComponent window;

    public DiagramAction(FileObject context) 
    {
        pkgFileObject = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            window = WindowDiaTopComponent.getInstance(pkgFileObject);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected FileObject getPkgFile() {
        return pkgFileObject;
    }

    protected WindowDiaTopComponent getWindow() {
        return window;
    }    
    
}