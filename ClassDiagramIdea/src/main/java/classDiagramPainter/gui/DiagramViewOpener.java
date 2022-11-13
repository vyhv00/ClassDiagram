package classDiagramPainter.gui;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class DiagramViewOpener {
    public static FileEditor[] openDiagramWindow(Project project, VirtualFile virtualFile, boolean b) {
        if (DiagramViewEditor.getInitActionCreate().equals(virtualFile.getUserData(DiagramViewEditor.getInitAction()))) {
            FileEditor[] openedEditors = FileEditorManager.getInstance(project).getEditors(virtualFile);
            DiagramViewEditor editor = null;
            for (FileEditor fileEditor : openedEditors) {
                if (fileEditor instanceof DiagramViewEditor) {
                    editor = (DiagramViewEditor) fileEditor;
                    try {
                        virtualFile.refresh(false, true);
                        editor.createGraph();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return FileEditorManager.getInstance(project).openFile(virtualFile, b);
    }
}
