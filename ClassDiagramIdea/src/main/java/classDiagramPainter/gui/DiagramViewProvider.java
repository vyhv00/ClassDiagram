package classDiagramPainter.gui;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DiagramViewProvider implements FileEditorProvider, DumbAware {

    private static String EDITOR_TYPE_ID = "DiagramView";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        boolean accept = false;
        if (virtualFile != null && project != null && virtualFile.isDirectory()) {
            PsiDirectory dir = PsiManager.getInstance(project).findDirectory(virtualFile);
            PsiPackage pkg = JavaDirectoryService.getInstance().getPackage(dir);
            if (pkg != null) {
                accept = true;
            }
        }
        return accept;
    }

    @Override
    public @NotNull
    FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        FileEditor[] openedEditors = FileEditorManager.getInstance(project).getEditors(virtualFile);
        DiagramViewEditor editor = null;
        for(FileEditor fileEditor : openedEditors) {
            if(fileEditor instanceof DiagramViewEditor) {
                editor = (DiagramViewEditor) fileEditor;
                break;
            }
        }
        try {
            if (DiagramViewEditor.getInitActionCreate().equals(virtualFile.getUserData(DiagramViewEditor.getInitAction()))) {
                if(editor == null) {
                    editor = new DiagramViewEditor(virtualFile);
                }
                virtualFile.refresh(false, true);
                editor.createGraph();
            } else {
                if (editor == null && DiagramViewEditor.openable(virtualFile.getPath())) {
                    editor = new DiagramViewEditor(virtualFile);
                    editor.openGraph();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor;
    }

    @Override
    public @NotNull
    @NonNls
    String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @Override
    public @NotNull
    FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
