package classDiagramPainter.gui;

import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiagramViewTitleProvider implements EditorTabTitleProvider {
    @Override
    public @NlsContexts.TabTitle
    @Nullable
    String getEditorTabTitle(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (virtualFile != null && project != null) {

            String title = virtualFile.getUserData(DiagramViewEditor.getTitle());
            if (title != null) {
                return title;
            }
            return virtualFile.getName();
        }
        return null;
    }
}
