package classDiagramPainter.actions;

import classDiagramPainter.gui.DiagramViewEditor;
import classDiagramPainter.gui.DiagramViewOpener;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class CreateDiagramAction extends ADiagramAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        VirtualFile virtualFile = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        virtualFile.putUserData(DiagramViewEditor.getInitAction(), DiagramViewEditor.getInitActionCreate());
        DiagramViewOpener.openDiagramWindow(project, virtualFile, true);
    }
}
