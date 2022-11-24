package classDiagramPainter.gui;

import classDiagram.DiagramGUI;
import classDiagram.fileCreatedListener.*;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.sun.jna.platform.FileMonitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class DiagramViewEditor extends UserDataHolderBase implements FileEditor, FileListener {

    private static final Key<String> INIT_ACTION = Key.create("DiagramViewEditor.InitAction");
    private static final Key<String> TITLE = Key.create("DiagramViewEditor.Title");
    private static final Key<String> PACKAGE = Key.create("DiagramViewEditor.Package");
    private static final String INIT_ACTION_CREATE = "DiagramViewEditor.InitAction.Create";
    private static final String INIT_ACTION_OPEN = "DiagramViewEditor.InitAction.Open";

    private final VirtualFile file;
    private DiagramGUI diagram;
    private String name;

    public DiagramViewEditor (VirtualFile file) throws IOException {
        this.file = file;
        prepareGraph();
    }

    @Override
    public @NotNull JComponent getComponent() {
        return diagram.getFrame();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull String getName() {
        return name;
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        if (diagram.getFrame() != null) {
            diagram.getFrame().doSave();
            diagram.getFrame().removeListener(this);
        }
        Disposer.dispose(this);
    }

    @Override
    public @Nullable VirtualFile getFile() {
        return this.file;
    }

    public static Key<String> getInitAction() {
        return INIT_ACTION;
    }

    public static Key<String> getTitle() {
        return TITLE;
    }

    public static Key<String> getPackage() {
        return PACKAGE;
    }

    public static String getInitActionCreate() {
        return INIT_ACTION_CREATE;
    }

    public static String getInitActionOpen() {
        return INIT_ACTION_OPEN;
    }

    private void prepareGraph() throws IOException {
        diagram = new DiagramGUI(((LightVirtualFile) file).getOriginalFile().getPath());
    }

    public void openGraph() {
        diagram.open();
        name = diagram.getPkgName() + ": Class Diagram";
        file.putUserData(TITLE, name);
        diagram.getFrame().addListener(this);
    }

    public void createGraph() throws IOException {
        try {
            diagram.getFrame().doSave();
            diagram.generate();
            diagram.getFrame().doSave();
            name = diagram.getPkgName() + ": Class Diagram";
            file.putUserData(TITLE, name);
            diagram.getFrame().addListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean openable(String pakcagePath) {
        return DiagramGUI.openable(pakcagePath);
    }

    @Override
    public void update(FileSubject fileSubject) {
        file.refresh(false, true);
    }
}
