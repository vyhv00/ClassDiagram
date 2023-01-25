package classDiagramPainter.gui;

import classDiagramPainter.gui.utils.DiagramLightFile;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class DiagramViewOpener {
    public static FileEditor[] openDiagramWindow(Project project, VirtualFile virtualFile, boolean b) {
        DiagramLightFile lightFile = new DiagramLightFile(virtualFile.getName());
        lightFile.setOriginalFile(virtualFile);

        if (DiagramViewEditor.getInitActionCreate().equals(virtualFile.getUserData(DiagramViewEditor.getInitAction()))) {
            FileEditor[] openedEditors = FileEditorManager.getInstance(project).getEditors(lightFile);
            DiagramViewEditor editor = null;
            for (FileEditor fileEditor : openedEditors) {
                if (fileEditor instanceof DiagramViewEditor) {
                    editor = (DiagramViewEditor) fileEditor;
                    try {
                        editor.createGraph();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        FileEditor[] a = FileEditorManager.getInstance(project).openFile(lightFile, b);
        lightFile.getOriginalFile().putUserData(DiagramViewEditor.getInitAction(), "");
        return a;
    }
}
