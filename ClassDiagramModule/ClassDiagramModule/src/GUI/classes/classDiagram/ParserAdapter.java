/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classDiagram;

import bluej.SortedProperties;
import bluej.graph.Edge;
import bluej.graph.Vertex;
import diagramParser.PackageRelations;
import diagramParser.PackageParser;
import elements.ClassElement;
import elements.ClassLikeElement;
import elements.FieldElement;
import elements.IDiagramElement;
import elements.MethodElement;
import bluej.pkgmgr.Package;
import bluej.pkgmgr.PackageFile;
import bluej.pkgmgr.dependency.ContainmentDependency;
import bluej.pkgmgr.dependency.AssociationDependency;
import bluej.pkgmgr.dependency.Dependency;
import bluej.pkgmgr.dependency.ExtendsDependency;
import bluej.pkgmgr.dependency.ImplementsDependency;
import bluej.pkgmgr.dependency.UsesDependency;
import bluej.pkgmgr.target.ClassTarget;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import bluej.pkgmgr.target.DependentTarget;
import bluej.pkgmgr.target.Target;
import bluej.pkgmgr.target.role.AbstractClassRole;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import javax.tools.JavaCompiler;

/**
 *
 * @author vojta
 */
public class ParserAdapter {

    private final Package pkg;
    private final HashSet<Rel> rels = new HashSet<>();

    public ParserAdapter(Package pkg) {
        this.pkg = pkg;
    }

    public void paintGraph(JavaCompiler compiler) throws Exception {
        generate(compiler);
    }

    /**
     *
     */
    private void generate(JavaCompiler compiler) throws Exception {
        try {
            PackageRelations rel = PackageParser.parsePkg(pkg.getPath(), compiler);
            SortedProperties props = new SortedProperties();
            pkg.clean();
            PackageFile packageFile = pkg.getPkgFile();
            HashSet<String> identifiers = new HashSet<>();

            if (packageFile != null) {
                try {
                    packageFile.load(props);
                    int numTargets = 0;
                    numTargets = Integer.parseInt(props.getProperty("package.numTargets", "0"));
                    for (int i = 0; i < numTargets; i++) {
                        identifiers.add(props.getProperty("target" + (i + 1) + ".name"));
                    }
                } catch (IOException | NumberFormatException e) {
                    if (e instanceof IOException) {
                        System.err.println("package.bluej load failed" + e);
                    } else {
                        System.err.println("package.bluej read failed" + e);
                    }
                }
            }

            pkg.setBaseName(rel.getPackageName());
            LinkedHashMap<ClassLikeElement, DependentTarget> classes = new LinkedHashMap<>();
            HashSet<ClassLikeElement> testClasses = new HashSet<>();
            HashSet<Target> toAdd = new HashSet<>();
            for (ClassLikeElement el
                    : rel.getRelatedClasses()) {
                String name = String.valueOf(el.getElement());
                String type = null;
                switch (el.getElementType()) {
                    case ABSTRACT_CLASS: {
                        type = "abstract";
                        break;
                    }
                    case ENUM: {
                        type = "enum";
                        break;
                    }
                    case RECORD: {
                        type = "record";
                        break;
                    }
                    case INTERFACE: {
                        type = "interface";
                        break;
                    }
                    case TEST_CLASS: {
                        testClasses.add(el);
                        type = "unittest";
                        break;
                    }
                    case CLASS: {
                        type = null;
                        break;
                    }
                    default:
                        throw new AssertionError("not a target " + el);
                }
                ClassTarget target = new ClassTarget(pkg, name, type);
                classes.put(el, target);
            }

            for (ClassLikeElement testClass : testClasses) {
                ClassElement test = (ClassElement) testClass;
                if (test.getTestFor() != null) {
                    classes.get(test.getTestFor()).setAssociation(classes.get(test));
                }
            }

            for (ClassLikeElement from
                    : classes.keySet()) {
                if (from.getContainment() != null) {
                    Dependency containDep = new ContainmentDependency(pkg, classes.get(from), classes.get(from.getContainment()));
                    pkg.addDependency(containDep, true);
                }

                LinkedHashSet<String> fields = new LinkedHashSet<>();
                for (FieldElement field : from.getFields()) {
                    for (ClassLikeElement to : field.getPointers()) {
                        if (addible(from, to)) {
                            Dependency uses = new AssociationDependency(pkg, classes.get(from), classes.get(to));
                            pkg.addDependency(uses, true);
                        }
                    }
                    if (classes.get(from) instanceof ClassTarget) {
                        String fieldExp = visibility(field) + field.getElement();
                        if (field.getIdentifier() != null) {
                            fieldExp = fieldExp + " : " + field.getIdentifier();
                        }
                        fields.add(fieldExp);
                    }
                }

                LinkedHashSet<String> methods = new LinkedHashSet<>();
                for (MethodElement method : from.getMethods()) {
                    for (ClassLikeElement to : method.getPointers()) {
                        if (addible(from, to)) {
                            Dependency uses = new UsesDependency(pkg, classes.get(from), classes.get(to));
                            pkg.addDependency(uses, true);
                        }
                    }
                    if (classes.get(from) instanceof ClassTarget) {
                        String methodExp;
                        if (method.isCostructor()) {
                            methodExp = visibility(method) + from.getElement() + params(method);
                        } else {
                            methodExp = visibility(method) + method.getElement() + params(method) + " : " + method.getReturnType();
                        }
                        methods.add(methodExp);
                    }
                }
                ((ClassTarget) classes.get(from)).putFields(fields);
                ((ClassTarget) classes.get(from)).putMethods(methods);

                for (ClassLikeElement to : from.getImplementations()) {
                    Dependency implementsDep = new ImplementsDependency(pkg, classes.get(from), classes.get(to));
                    pkg.addDependency(implementsDep, true);
                }

                for (ClassLikeElement to : from.getPointers()) {
                    if (addible(from, to)) {
                        Dependency uses = new UsesDependency(pkg, classes.get(from), classes.get(to));
                        pkg.addDependency(uses, true);
                    }
                }

                if (from.getExtension() != null) {
                    Dependency extendsDep = new ExtendsDependency(pkg, classes.get(from), classes.get(from.getExtension()));
                    pkg.addDependency(extendsDep, true);
                }
                if (identifiers.contains(classes.get(from).getIdentifierName())) {
                    pkg.addTarget(classes.get(from));
                } else {
                    toAdd.add(classes.get(from));
                }
            }

            try {
                pkg.reReadGraphLayout();
            } catch (IOException ex) {
                System.err.println(ex);
            }

            toAdd.forEach(
                    (dependentTarget) -> {
                        pkg.findSpaceForVertex(dependentTarget);
                        pkg.addTarget(dependentTarget);
                    }
            );
        } catch (AssertionError ex) {
            System.err.println(ex);
        }
    }

    private String visibility(IDiagramElement field) {
        String visibilityMark = "";
        if (field.getModifier().contains(Modifier.PRIVATE)) {
            visibilityMark = "-";
        } else if (field.getModifier().contains(Modifier.PUBLIC)) {
            visibilityMark = "+";
        } else if (field.getModifier().contains(Modifier.PROTECTED)) {
            visibilityMark = "#";
        }
        return visibilityMark + " ";
    }

    private String params(MethodElement method) {
        String params = "(";
        Iterator<FieldElement> it = method.getParams().iterator();
        while (it.hasNext()) {
            params = params + it.next().getIdentifier();
            if (it.hasNext()) {
                params = params + ", ";
            }
        }
        params = params + ")";
        return params;
    }

    private boolean addible(ClassLikeElement fromClass, ClassLikeElement toClass) {
        Rel rel = new Rel(fromClass, toClass);
        return rels.add(rel);
    }
    
    private class Rel{
        private final ClassLikeElement from;
        private final ClassLikeElement to;

        public Rel(ClassLikeElement from, ClassLikeElement to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Rel other = (Rel) obj;
            if (!Objects.equals(this.from, other.from)) {
                return false;
            }
            return Objects.equals(this.to, other.to);
        }        
    }

//    private void testFiller() {
//        ClassTarget a = new ClassTarget(pkg, "Trida1");
//        ClassTarget b = new ClassTarget(pkg, "Trida2");
//        ClassTarget c = new ClassTarget(pkg, "Trida AB", "abstract");
//        ClassTarget d = new ClassTarget(pkg, "Interface", "interface");
//        ClassTarget e = new ClassTarget(pkg, "Enum", "enum");
//        pkg.findSpaceForVertex(a);
//        pkg.addTarget(a);
//        pkg.findSpaceForVertex(b);
//        pkg.addTarget(b);
//        pkg.findSpaceForVertex(c);
//        pkg.addTarget(c);
//        pkg.findSpaceForVertex(d);
//        pkg.addTarget(d);
//        pkg.findSpaceForVertex(e);
//        pkg.addTarget(e);
//        Dependency ab = new UsesDependency(pkg, b, a);
//        pkg.addDependency(ab, true);
//        Dependency ac = new ContainmentDependency(pkg, c, a);
//        pkg.addDependency(ac, true);
//        Dependency bd = new UsesDependency(pkg, b, d);
//        pkg.addDependency(bd, true);
//        Dependency adI = new ImplementsDependency(pkg, a, d);
//        pkg.addDependency(adI, true);
//        Dependency acE = new ExtendsDependency(pkg, a, c);
//        pkg.addDependency(acE, true);
//        HashSet aFields = new LinkedHashSet<String>();
//        aFields.add("aaaaaaaaaa");
//        aFields.add("ab");
//        aFields.add("ac");
//        a.putFields(aFields);
//        HashSet aMethods = new LinkedHashSet<String>();
//        aMethods.add("Maaaaaaaaaa");
//        aMethods.add("Mab");
//        aMethods.add("Mac");
//        a.putMethods(aMethods);
//    }
//    
//    /**
//     * Testing method. Prints package out to console
//     */
//    private static void printOut(PackageRelations relations) {
//        relations.getRelatedClasses().forEach(clazz -> {
//            System.out.print(clazz.getModifier());
//            System.out.print(clazz.getElement());
//            System.out.println(" " + clazz.getElementType());
//            if (clazz.getExtension() != null) {
//                System.out.println("  | Extension: " + clazz.getExtension().getElement());
//}
//            clazz.getImplementations().forEach(implementation -> {
//                System.out.println("  | Implementation: " + implementation.getElement());
//            });
//
//            clazz.getFields().forEach(field -> {
//                System.out.print("   field: " + field.getModifier());
//                System.out.print(" " + field.getIdentifier());
//                System.out.println("  | " + field.getElement());
//                field.getPointers().forEach(pointer -> {
//                    System.out.println("    | " + pointer);
//                });
//            });
//            clazz.getMethods().forEach(method -> {
//                System.out.print("   method: " + method.getModifier());
//                System.out.println("  | " + method.getElement());
//                method.getPointers().forEach(pointer -> {
//                    System.out.println("    | " + pointer);
//                });
//            });
//            System.out.println();
//        });
//    }
}
