/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramParser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 *
 * @author vojta
 */
public class PackageParser {

    /**
     * @param packageDir
     */
    public static PackageRelations parsePkg(File packageDir) throws Exception {
        if (packageDir.isDirectory()) {
            File[] files1 = packageDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".java");
                }
            });
            
            String tmpdir = System.getProperty("java.io.tmpdir");
            long pid = ProcessHandle.current().pid();
            File outputDir = new File(tmpdir + "/diagram" + pid);
            outputDir.mkdir();
            outputDir.deleteOnExit();
            List<File> output = new ArrayList<>();
            output.add(outputDir);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, output);

            Iterable<? extends JavaFileObject> compilationUnits1
                    = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));
            
            CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits1);
            LinkedList<AbstractProcessor> processors = new LinkedList<>();
            CodeProcessor showProcessor = new CodeProcessor();
            processors.add(showProcessor);
            task.setProcessors(processors);
            task.call();
            return showProcessor.getRelations();
        }
        throw new IllegalArgumentException(packageDir + " is not a directory");
    }

}
