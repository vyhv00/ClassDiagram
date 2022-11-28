package classDiagramPainter.gui;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class DiagramViewProvider implements FileEditorProvider, DumbAware {

    private static String EDITOR_TYPE_ID = "DiagramView";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile lightFile) {
        boolean accept = false;
        if (!(lightFile instanceof LightVirtualFile)) {
            return accept;
        }
        VirtualFile virtualFile = ((LightVirtualFile) lightFile).getOriginalFile();
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
    FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile lightFile) {
        VirtualFile virtualFile = ((LightVirtualFile) lightFile).getOriginalFile();
        FileEditor[] openedEditors = FileEditorManager.getInstance(project).getEditors(lightFile);
        DiagramViewEditor editor = null;
        for (FileEditor fileEditor : openedEditors) {
            if (fileEditor instanceof DiagramViewEditor) {
                editor = (DiagramViewEditor) fileEditor;
                break;
            }
        }
        try {
            if (DiagramViewEditor.getInitActionCreate().equals(virtualFile.getUserData(DiagramViewEditor.getInitAction()))) {
                if (editor == null) {
                    editor = new DiagramViewEditor(lightFile);
                }
                lightFile.refresh(false, true);
                editor.createGraph();
            } else {
                if (editor == null && DiagramViewEditor.openable(virtualFile.getPath())) {
                    editor = new DiagramViewEditor(lightFile);
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
