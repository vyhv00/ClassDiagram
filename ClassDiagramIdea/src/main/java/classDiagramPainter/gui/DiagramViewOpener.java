package classDiagramPainter.gui;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;

import java.io.IOException;

public class DiagramViewOpener {
    public static FileEditor[] openDiagramWindow(Project project, VirtualFile virtualFile, boolean b) {
        LightVirtualFile lightFile = new LightVirtualFile();
        lightFile.setOriginalFile(virtualFile);

        if (DiagramViewEditor.getInitActionCreate().equals(virtualFile.getUserData(DiagramViewEditor.getInitAction()))) {
            FileEditor[] openedEditors = FileEditorManager.getInstance(project).getEditors(lightFile);
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
        return FileEditorManager.getInstance(project).openFile(lightFile, b);
    }
}
