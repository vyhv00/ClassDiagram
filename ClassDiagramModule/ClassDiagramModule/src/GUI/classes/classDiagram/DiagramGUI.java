/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classDiagram;

import bluej.pkgmgr.BlueJPackageFile;
import bluej.pkgmgr.PackageFile;
import bluej.pkgmgr.PackageFileFactory;
import bluej.pkgmgr.PkgMgrFrame;
import java.io.File;
import java.io.IOException;
import bluej.pkgmgr.Package;

public class DiagramGUI {

    private final Package pkg;
    private final PkgMgrFrame frame;
    
    public DiagramGUI(String packagePath) throws IOException {
        File path = new File(packagePath);
        PackageFile pkgFile = PackageFileFactory.getPackageFile(path);
        pkgFile.create();
        pkg = new bluej.pkgmgr.Package(null, path);
        frame = PkgMgrFrame.createFrame(pkg);
    }

    public void generate() throws Exception {
        ParserAdapter adapter = new ParserAdapter(pkg);
        adapter.paintGraph();
        frame.doSave();
        pkg.repaint();
    }

    public void open() {
        pkg.refreshPackage();
        pkg.repaint();
    }
    
    public String getPkgName() {
        return pkg.getBaseName();
    }
    
    public PkgMgrFrame getFrame() {
        return frame;
    }
    
    public void save() {
        frame.doSave();
    }
    
    public static boolean openable(String packagePath) {
        File path = new File(packagePath);
        return BlueJPackageFile.exists(path);
    }
}
