/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramParser;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import javax.lang.model.element.Element;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_15)
class CodeProcessor extends AbstractProcessor {

    private Trees trees;
    private final PackageRelations relations = new PackageRelations(null);;

    @Override
    public void init(ProcessingEnvironment pEnv) {
        super.init(pEnv);
        trees = Trees.instance(pEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {

        for (Element e : roundEnv.getRootElements()) {
            TreePath tp = trees.getPath(e);

            new Scanner(relations).scan(tp, null);
        }
        return false;
    }

    public PackageRelations getRelations() {
        return relations;
    }
}
