package classDiagramPainter.gui.utils;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

@State(
    name = "OpenedDiagrams",
    storages = {
        @Storage(StoragePathMacros.WORKSPACE_FILE)
    }
)
public class DiagramsService implements PersistentStateComponent<DiagramsService.State> {

    private static DiagramsService instance = new DiagramsService();
    private State myState = new State();

    private HashSet<String> diagrams = new HashSet<>();

    private DiagramsService() {
    }

    public static DiagramsService getInstance(Project project) {
        return project.getService(DiagramsService.class);
    }

    public State getState() {
        return myState;
    }

    public void loadState(State state) {
        myState = state;
    }

    public void addDiagram(String diagramPath) {
        diagrams.add(diagramPath);
        saveState();
    }

    public void removeDiagram(String diagramPath) {
        diagrams.remove(diagramPath);
        saveState();
    }

    private void saveState() {
        String paths = "";
        Iterator<String> it = diagrams.iterator();

        while (it.hasNext()) {
            paths = paths + it.next();
            if (it.hasNext()) {
                paths = paths + "###";
            }
        };
        myState.value = paths;
    }

    public static class State {
        public String value;
    }
}
