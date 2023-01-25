package classDiagramPainter.gui.utils;

import classDiagramPainter.gui.DiagramViewEditor;
import classDiagramPainter.gui.DiagramViewOpener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class DiagramStartup implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        //diagram tabs doesn't recover at this moment
//        String[] diagramPaths = DiagramsService.getInstance(project).getState().value.split("###");
//        for (String path : diagramPaths) {
//            try {
//                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
//                virtualFile.putUserData(DiagramViewEditor.getInitAction(), DiagramViewEditor.getInitActionOpen());
//                DiagramViewOpener.openDiagramWindow(project,virtualFile, false);
//            } catch (Exception e) {
//            }
//        }
    }
}
