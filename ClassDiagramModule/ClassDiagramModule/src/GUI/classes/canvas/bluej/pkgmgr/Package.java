/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2010,2011,2012,2013,2014,2015  Michael Kolling and John Rosenberg

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 This file is subject to the Classpath exception as provided in the
 LICENSE.txt file that accompanied this code.
 */
package canvas.bluej.pkgmgr;

import canvas.bluej.MultiIterator;
import canvas.bluej.SortedProperties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

//import bluej.Config;
//~+ SIMJ08
/*
import bluej.collect.DataCollectionCompileObserverWrapper;
import bluej.collect.DataCollector;
 */
//~- SIMJ08
import canvas.bluej.graph.Edge;
import canvas.bluej.graph.Graph;
import canvas.bluej.pkgmgr.target.ClassTarget;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import canvas.bluej.pkgmgr.dependency.ContainmentDependency;
import canvas.bluej.pkgmgr.dependency.AssociationDependency;
import canvas.bluej.pkgmgr.dependency.Dependency;
import canvas.bluej.pkgmgr.dependency.ExtendsDependency;
import canvas.bluej.pkgmgr.dependency.ImplementsDependency;
import canvas.bluej.pkgmgr.dependency.UsesDependency;
import canvas.bluej.pkgmgr.target.DependentTarget;
import canvas.bluej.pkgmgr.target.Target;
import canvas.bluej.pkgmgr.target.TargetCollection;
import java.util.HashSet;

/**
 * A Java package (collection of Java classes).
 *
 * @author Michael Kolling
 * @author Axel Schmolitzky
 * @author Andrew Patterson
 */
public final class Package extends Graph {

    /**
     * In the top left corner of each package we have a fixed target - either a
     * ParentPackageTarget or a ReadmeTarget. These are there locations
     */
    public static final int FIXED_TARGET_X = 10;
    public static final int FIXED_TARGET_Y = 10;

    /**
     * base name of package (eg util) ("" for the unnamed package)
     */
    private String baseName;

    /**
     * all the targets in a package
     */
    private TargetCollection targets;

    /**
     * all the uses-arrows in a package
     */
    private List<Dependency> usesArrows;

    private List<Dependency> associtaionArrows;

    private List<Dependency> containmentArrows;

    private List<Dependency> implementsArrows;

    /**
     * all the extends-arrows in a package
     */
    private List<Dependency> extendsArrows;

    /**
     * the CallHistory of a package
     */
    // private CallHistory callHistory;
    /**
     * whether extends-arrows should be shown
     */
    private boolean showExtends = true;
    private boolean showImplements = true;
    /**
     * whether uses-arrows should be shown
     */
    private boolean showUses = true;
    private boolean showAssociations = true;
    private boolean showContainments = true;

    private PackageEditor editor;
    private File dir;
    private PackageFile packageFile;
    private SortedProperties lastSavedProps = new SortedProperties();

    /* ------------------- end of field declarations ------------------- */
    /**
     * Create a package of a project with the package name of baseName (ie
     * reflect) and with a parent package of parent (which may represent
     * java.lang for instance) If the package file (bluej.pkg) is not found, an
     * IOException is thrown.
     */
    public Package(String baseName, File packageDir)
            throws IOException {

        this.baseName = baseName;
        this.dir = packageDir;
        init();
    }

    private void init()
            throws IOException {
        targets = new TargetCollection();
        usesArrows = new ArrayList<Dependency>();
        associtaionArrows = new ArrayList<Dependency>();
        containmentArrows = new ArrayList<Dependency>();
        implementsArrows = new ArrayList<Dependency>();
        extendsArrows = new ArrayList<Dependency>();
        load();
    }

    /**
     * Return this package's base name (eg util) ("" for the unnamed package)
     */
    public String getBaseName() {
        return baseName;
    }

    public void repaint() {
        if (editor != null) {
            editor.repaint();
        }
    }

    void setEditor(PackageEditor editor) {
        this.editor = editor;
    }

    public PackageEditor getEditor() {
        return editor;
    }

    /**
     * Get the currently selected Targets. It will return an empty array if no
     * target is selected.
     *
     * @return the currently selected array of Targets.
     */
    public Target[] getSelectedTargets() {
        Target[] targetArray = new Target[0];
        LinkedList<Target> list = new LinkedList<Target>();
        for (Iterator<Target> it = getVertices(); it.hasNext();) {
            Target target = it.next();
            if (target.isSelected()) {
                list.add(target);
            }
        }
        return list.toArray(targetArray);
    }

    /**
     * Get the selected Dependencies.
     *
     * @return The currently selected dependency or null.
     */
    public Dependency getSelectedDependency() {
        for (Iterator<? extends Edge> it = getEdges(); it.hasNext();) {
            Edge edge = it.next();
            if (edge instanceof Dependency && edge.isSelected()) {
                return (Dependency) edge;
            }
        }
        return null;
    }

    /**
     * Load the elements of a package from a specified directory. If the package
     * file (bluej.pkg) is not found, an IOException is thrown.
     *
     * <p>
     * This does not cause targets to be loaded. Use refreshPackage() for that.
     */
    public void load()
            throws IOException {
        // read the package properties

        packageFile = getPkgFile();

        // try to load the package file for this package
        packageFile.load(lastSavedProps);
    }

    /**
     * Refresh the targets and dependency arrows in the package, based on
     * whatever is actually on disk.
     */
    public void refreshPackage() {
        // read in all the targets contained in this package
        // into this temporary map
        Map<String, Target> propTargets = new HashMap<String, Target>();

        int numTargets = 0, numDependencies = 0;

        try {
            this.baseName = lastSavedProps.getProperty("package.baseName");
            numTargets = Integer.parseInt(lastSavedProps.getProperty("package.numTargets", "0"));
            numDependencies = Integer.parseInt(lastSavedProps.getProperty("package.numDependencies", "0"));
        } catch (Exception e) {
            System.err.println("Error loading from package file " + packageFile + ": " + e);
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < numTargets; i++) {
            Target target;
            String type = lastSavedProps.getProperty("target" + (i + 1) + ".type");
            String identifierName = lastSavedProps.getProperty("target" + (i + 1) + ".name");

            target = new ClassTarget(this, identifierName);

            target.load(lastSavedProps, "target" + (i + 1));
            propTargets.put(identifierName, target);
        }

        List<Target> targetsToPlace = new ArrayList<Target>();
        propTargets.forEach((targetName, target) -> {
            if (target == null || !(target instanceof ClassTarget)) {
                target = new ClassTarget(this, targetName);
                targetsToPlace.add(target);
            }
            addTarget(target);
        });

        // Find an empty spot for any targets which didn't already have
        // a position
        for (Target t : targetsToPlace) {
            findSpaceForVertex(t);
        }

        // Fix up dependency information
        for (int i = 0; i < numDependencies; i++) {
            Dependency dep = null;
            String type = lastSavedProps.getProperty("dependency" + (i + 1) + ".type");

            if (type != null) {
                if (type.equals("UsesDependency")) {
                    dep = new UsesDependency(this);
                } else if (type.equals("ImplementsDependency")) {
                    dep = new ImplementsDependency(this);
                } else if (type.equals("ExtendsDependency")) {
                    dep = new ExtendsDependency(this);
                } else if (type.equals("ContainmentDependency")) {
                    dep = new ContainmentDependency(this);
                } else if (type.equals("AssociationDependency")) {
                    dep = new AssociationDependency(this);
                }
                if (dep.load(lastSavedProps, "dependency" + (i + 1))) {
                    addDependency(dep, true);
                }
            }
        }
        recalcArrows();

        // our associations are based on name so we mustn't deal with
        // them until all classes/packages have been loaded
        for (int i = 0; i < numTargets; i++) {
            String assoc = lastSavedProps.getProperty("target" + (i + 1) + ".association");
            String identifierName = lastSavedProps.getProperty("target" + (i + 1) + ".name");

            if (assoc != null) {
                Target t1 = getTarget(identifierName), t2 = getTarget(assoc);

                if (t1 != null && t2 != null && t1 instanceof DependentTarget) {
                    DependentTarget dt = (DependentTarget) t1;
                    dt.setAssociation((DependentTarget) t2);
                }
            }
        }
    }

    /**
     * ReRead the pkg file and update the position of the targets in the graph
     *
     * @throws IOException
     *
     */
    public void reReadGraphLayout() throws IOException {
        // try to load the package file for this package
        SortedProperties props = new SortedProperties();
        packageFile.load(props);

        int numTargets = 0;

        try {
            numTargets = Integer.parseInt(props.getProperty("package.numTargets", "0"));
        } catch (Exception e) {
            System.err.println("Error loading from bluej package file " + packageFile + ": " + e);
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < numTargets; i++) {
            Target target = null;
            String identifierName = props.getProperty("target" + (i + 1) + ".name");
            int x = Integer.parseInt(props.getProperty("target" + (i + 1) + ".x"));
            int y = Integer.parseInt(props.getProperty("target" + (i + 1) + ".y"));
            int height = Integer.parseInt(props.getProperty("target" + (i + 1) + ".height"));
            int width = Integer.parseInt(props.getProperty("target" + (i + 1) + ".width"));
            target = getTarget(identifierName);
            if (target != null) {
                target.setPos(x, y);
                target.setSize(width, height);
            }
        }
        repaint();
    }

    /**
     * Save this package to disk. The package is saved to the standard package
     * file.
     */
    public void save(Properties frameProperties) {
        /* create the directory if it doesn't exist */
        File dir = getPath();
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                System.err.println("Error creating directory " + dir);
                return;
            }
        }

        SortedProperties props = new SortedProperties();
        props.putAll(frameProperties);

        // save targets and dependencies in package
        props.put("package.baseName", String.valueOf(this.baseName));
        props.put("package.numDependencies", String.valueOf(
                usesArrows.size()
                + associtaionArrows.size()
                + containmentArrows.size()
                + implementsArrows.size()
                + extendsArrows.size()));

        int t_count = 0;

        Iterator<Target> t_enum = targets.iterator();
        while (t_enum.hasNext()) {
            Target t = t_enum.next();
            // should we save this target
            if (t.isSaveable()) {
                t.save(props, "target" + (t_count + 1));
                t_count++;
            }
        }
        props.put("package.numTargets", String.valueOf(t_count));

        List<Dependency> allUsesArrows = new ArrayList<Dependency>();
        allUsesArrows.addAll(usesArrows);
        allUsesArrows.addAll(associtaionArrows);
        allUsesArrows.addAll(containmentArrows);
        for (int i = 0; i < allUsesArrows.size(); i++) { // uses arrows
            Dependency d = allUsesArrows.get(i);
            d.save(props, "dependency" + (i + 1));
        }

        List<Dependency> allExtendsArrows = new ArrayList<Dependency>();
        allExtendsArrows.addAll(extendsArrows);
        allExtendsArrows.addAll(implementsArrows);
        for (int i = 0; i < allExtendsArrows.size(); i++) { // uses arrows
            Dependency d = allExtendsArrows.get(i);
            d.save(props, "dependency" + (i + 1 + allUsesArrows.size()));
        }

        try {
            packageFile.save(props);
        } catch (IOException e) {
            System.err.println("Exception when saving package file : " + e);
            return;
        }
        lastSavedProps = props;

        return;
    }

    @Override
    public Iterator<Target> getVertices() {
        return targets.sortediterator();
    }

    @Override
    public Iterator<? extends Edge> getEdges() {
        List<Iterator<? extends Edge>> iterations = new ArrayList<Iterator<? extends Edge>>();

        if (showUses) {
            iterations.add(usesArrows.iterator());
        }
        if (showAssociations) {
            iterations.add(associtaionArrows.iterator());
        }
        if (showContainments) {
            iterations.add(containmentArrows.iterator());
        }
        if (showImplements) {
            iterations.add(implementsArrows.iterator());
        }
        if (showExtends) {
            iterations.add(extendsArrows.iterator());
        }
        return new MultiIterator<Edge>(iterations);
    }

    /**
     * Return a List of all ClassTargets that have the role of a unit test.
     */
    public List<ClassTarget> getTestTargets() {
        List<ClassTarget> l = new ArrayList<ClassTarget>();

        for (Iterator<Target> it = targets.iterator(); it.hasNext();) {
            Target target = it.next();

            if (target instanceof ClassTarget) {
                ClassTarget ct = (ClassTarget) target;

                if (ct.isUnitTest()) {
                    l.add(ct);
                }
            }
        }
        return l;
    }

    public void addTarget(Target t) {
        targets.add(t.getIdentifierName(), t);
        graphChanged();
    }

    /**
     * Add a dependancy in this package. The dependency is also added to the
     * individual targets involved.
     */
    public void addDependency(Dependency d, boolean recalc) {
        DependentTarget from = d.getFrom();
        DependentTarget to = d.getTo();

        if (from == null || to == null) {
            // Debug.reportError("Found invalid dependency - ignored.");
            return;
        }

        if (d instanceof UsesDependency) {
            if (d instanceof AssociationDependency) {
                int index = associtaionArrows.indexOf(d);

                if (index != -1) {
                    ((AssociationDependency) associtaionArrows.get(index)).setFlag(true);
                    return;
                } else {
                    associtaionArrows.add(d);
                }
            } else if (d instanceof ContainmentDependency) {
                int index = containmentArrows.indexOf(d);

                if (index != -1) {
                    ((ContainmentDependency) containmentArrows.get(index)).setFlag(true);
                    return;
                } else {
                    containmentArrows.add(d);
                }
            } else {
                int index = usesArrows.indexOf(d);

                if (index != -1) {
                    ((UsesDependency) usesArrows.get(index)).setFlag(true);
                    return;
                } else {
                    usesArrows.add(d);
                }
            }
        } else if (d instanceof ImplementsDependency) {
            if (implementsArrows.contains(d)) {
                return;
            } else {
                implementsArrows.add(d);
            }
        } else {
            if (extendsArrows.contains(d)) {
                return;
            } else {
                extendsArrows.add(d);
            }
        }

        from.addDependencyOut(d, recalc);
        to.addDependencyIn(d, recalc);
    }

    /**
     * Lay out the arrows between targets.
     */
    private void recalcArrows() {
        Iterator<Target> it = getVertices();
        while (it.hasNext()) {
            Target t = it.next();

            if (t instanceof DependentTarget) {
                DependentTarget dt = (DependentTarget) t;

                dt.recalcInUses();
                dt.recalcOutUses();
            }
        }
    }

    /**
     * Return the target with name "identifierName".
     *
     * @param identifierName the unique name of a target.
     * @return the target with name "tname" if existent, null otherwise.
     */
    public Target getTarget(String identifierName) {
        if (identifierName == null) {
            return null;
        }
        Target t = targets.get(identifierName);
        return t;
    }

    /**
     * Return the dependent target with name "identifierName".
     *
     * @param identifierName the unique name of a target.
     * @return the target with name "tname" if existent and if it is a
     * DependentTarget, null otherwise.
     */
    public DependentTarget getDependentTarget(String identifierName) {
        if (identifierName == null) {
            return null;
        }
        Target t = targets.get(identifierName);

        if (t instanceof DependentTarget) {
            return (DependentTarget) t;
        }

        return null;
    }

    /**
     * Returns an ArrayList of ClassTargets holding all targets of this package.
     *
     * @return a not null but possibly empty array list of ClassTargets for this
     * package.
     */
    public final ArrayList<ClassTarget> getClassTargets() {
        ArrayList<ClassTarget> risul = new ArrayList<ClassTarget>();

        for (Iterator<Target> it = targets.iterator(); it.hasNext();) {
            Target target = it.next();

            if (target instanceof ClassTarget) {
                risul.add((ClassTarget) target);
            }
        }
        return risul;
    }

    /**
     * Return a List of Strings with names of all classes in this package.
     */
    public List<String> getAllClassnames() {
        List<String> names = new ArrayList<String>();

        for (Iterator<Target> it = targets.iterator(); it.hasNext();) {
            Target t = it.next();

            if (t instanceof ClassTarget) {
                ClassTarget ct = (ClassTarget) t;
                names.add(ct.getBaseName());
            }
        }
        return names;
    }

    public void setShowUses(boolean state) {
        showUses = state;
    }

    public void setShowAssociations(boolean state) {
        showAssociations = state;
    }

    public void setShowContainments(boolean state) {
        showContainments = state;
    }

    public void setShowImplemnets(boolean state) {
        showImplements = state;
    }

    public void setShowExtends(boolean state) {
        showExtends = state;
    }

    public TargetCollection getTargets() {
        return targets;
    }

    /**
     * Return a file object of the directory location of this package.
     *
     * @return The file object representing the full path to the packages
     * directory
     */
    public File getPath() {
        return dir;
    }

    /**
     * Returns the file containing information about the package. For BlueJ this
     * is package.bluej (or for older versions bluej.pkg) and for Greenfoot it
     * is greenfoot.project.
     */
    public PackageFile getPkgFile() {
        File dir = getPath();
        return PackageFileFactory.getPackageFile(dir);
    }

    public Properties getLastSavedProperties() {
        return lastSavedProps;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public void clean() {
        usesArrows = new ArrayList<Dependency>();
        extendsArrows = new ArrayList<Dependency>();
        implementsArrows = new ArrayList<Dependency>();
        associtaionArrows = new ArrayList<Dependency>();
        containmentArrows = new ArrayList<Dependency>();
        targets = new TargetCollection();
    }
}
