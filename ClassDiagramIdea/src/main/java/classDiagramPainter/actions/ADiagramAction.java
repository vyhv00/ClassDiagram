package classDiagramPainter.actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;

public abstract class ADiagramAction extends AnAction {
    @Override
    public void update(@NotNull final AnActionEvent anActionEvent) {
        boolean show = false;
        if(anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE) != null && anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE).isDirectory()) {
            IdeView view = LangDataKeys.IDE_VIEW.getData(anActionEvent.getDataContext());
            if(view != null) {
                PsiDirectory[] dir = view.getDirectories();
                if(dir.length > 0) {
                    PsiPackage pkg = JavaDirectoryService.getInstance().getPackage(dir[0]);
                    if (pkg != null) {
                        show = true;
                    }
                }}
        }
        anActionEvent.getPresentation().setEnabledAndVisible(show);
    }
}
