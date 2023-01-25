package classDiagramPainter.gui.utils;

import com.intellij.testFramework.LightVirtualFile;

public class DiagramLightFile extends LightVirtualFile {
    public DiagramLightFile(String name) {
        super(name);
    }
    @Override
    public int hashCode() {
        if(super.getOriginalFile() == null) {
            return super.hashCode();
        }
        return super.getOriginalFile().hashCode() + 5;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DiagramLightFile)) {
            return false;
        }
        return super.getOriginalFile().equals(((DiagramLightFile) obj).getOriginalFile());
    }


}
