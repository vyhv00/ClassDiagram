/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphProvider;

import parserAdapter.ParserAdapter;
import canvas.bluej.pkgmgr.BlueJPackageFile;
import canvas.bluej.pkgmgr.PackageFile;
import canvas.bluej.pkgmgr.PackageFileFactory;
import canvas.bluej.pkgmgr.PkgMgrFrame;
import java.io.File;
import java.io.IOException;
import canvas.bluej.pkgmgr.Package;
import javax.tools.JavaCompiler;

public class DiagramGUI {

    private final Package pkg;
    private final PkgMgrFrame frame;
    
    public DiagramGUI(String packagePath) throws IOException {
        File path = new File(packagePath);
        PackageFile pkgFile = PackageFileFactory.getPackageFile(path);
        pkgFile.create();
        pkg = new canvas.bluej.pkgmgr.Package(null, path);
        frame = PkgMgrFrame.createFrame(pkg);
    }

    public void generate(JavaCompiler compiler) throws Exception {
        ParserAdapter adapter = new ParserAdapter(pkg);
        adapter.paintGraph(compiler);
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
