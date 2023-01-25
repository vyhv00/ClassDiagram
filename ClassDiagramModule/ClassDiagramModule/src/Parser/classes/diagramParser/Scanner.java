/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramParser;

import elements.ClassElement;
import elements.utils.ClassFactory;
import elements.ClassLikeElement;
import elements.utils.ElementType;
import elements.FieldElement;
import elements.IDiagramElement;
import elements.MethodElement;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import elements.EnumElement;
import elements.IOwnedElement;
import java.util.ArrayList;
import java.util.HashSet;
import javax.lang.model.element.Name;

/**
 *
 * @author vojta
 */
class Scanner extends TreePathScanner<TreePath, IDiagramElement> {

    PackageRelations relations;

    public Scanner(PackageRelations relations) {
        this.relations = relations;
    }

    @Override
    public TreePath visitClass(ClassTree clazz, IDiagramElement parent) {
        if (clazz.getSimpleName() != null && !clazz.getSimpleName().contentEquals("")) {
            String packageName;
            if (this.getCurrentPath().getCompilationUnit().getPackageName() != null) {
                packageName = this.getCurrentPath().getCompilationUnit().getPackageName().toString();
            } else {
                packageName = "default package";
            }
            relations.setPackageName(packageName);
            if (relations.getPackageName().equals(packageName)) {
                ClassLikeElement clazzElement = ClassFactory.createClass(clazz);
                relations.putClassLikeElement(clazzElement);

                if (clazz.getExtendsClause() != null) {
                    IdentifierTree extension = null;

                    if (clazz.getExtendsClause().getKind().equals(Tree.Kind.IDENTIFIER)) {
                        extension = (IdentifierTree) clazz.getExtendsClause();
                    } else if (clazz.getExtendsClause().getKind().equals(Tree.Kind.PARAMETERIZED_TYPE)) {
                        ParameterizedTypeTree a = (ParameterizedTypeTree) clazz.getExtendsClause();

                        if (a.getType().getKind().equals(Tree.Kind.IDENTIFIER)) {
                            extension = (IdentifierTree) a.getType();
                        }
                    }

                    if (extension != null) {
                        relations.putClassExtension(clazzElement, extension.getName());
                    }
                }

                if (!clazz.getImplementsClause().isEmpty()) {
                    HashSet<Name> implementationNamePoiters = new HashSet<>();
                    clazz.getImplementsClause().forEach(implementationClause -> {
                        IdentifierTree implementationTreeIdentifier = null;

                        if (implementationClause.getKind().equals(Tree.Kind.IDENTIFIER)) {
                            implementationTreeIdentifier = (IdentifierTree) implementationClause;
                        } else if (implementationClause.getKind().equals(Tree.Kind.PARAMETERIZED_TYPE)) {
                            ParameterizedTypeTree a = (ParameterizedTypeTree) implementationClause;

                            if (a.getType().getKind().equals(Tree.Kind.IDENTIFIER)) {
                                implementationTreeIdentifier = (IdentifierTree) a.getType();
                            }
                        }

                        if (implementationTreeIdentifier != null) {
                            implementationNamePoiters.add(implementationTreeIdentifier.getName());
                        }
                    });
                    relations.putClassImplementations(clazzElement, implementationNamePoiters);
                }

                if (parent instanceof ClassLikeElement) {
                    ClassLikeElement parentClass = (ClassLikeElement) parent;
                    clazzElement.setContainment(parentClass);
                }

                return super.visitClass(clazz, clazzElement);
            }
        }
        return null;
    }

    @Override
    public TreePath visitLambdaExpression(LambdaExpressionTree lambda, IDiagramElement parent) {
        if (parent instanceof IOwnedElement owned) {
            parent = getTopOwner(owned);
        }
        if (parent instanceof ClassLikeElement clazz){
            MethodElement emptyMethod = new MethodElement(null, null, null, clazz, false);
            return super.visitLambdaExpression(lambda, emptyMethod); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }
        return null;
    }

    @Override
    public TreePath visitVariable(VariableTree variable, IDiagramElement parent) {
        if (parent instanceof ClassLikeElement) {
            if (parent instanceof EnumElement parentClass && variable.getInitializer() != null && variable.getInitializer().getKind() == Tree.Kind.NEW_CLASS && variable.getType().toString().equals(parentClass.getElement().toString())) {
                FieldElement field = new FieldElement(variable.getName(), null, variable.getModifiers().getFlags(), parent);
                parentClass.addField(field);
                return null;
            }
            FieldElement field = variableScan(variable, parent);
            return super.visitVariable(variable, field);
        }
        if (parent instanceof MethodElement) {
            return super.visitVariable(variable, parent);
        }
        return null;
    }

    @Override
    public TreePath visitIdentifier(IdentifierTree identifier, IDiagramElement parent) {
        if (parent instanceof IOwnedElement owned) {
            parent = getTopOwner(owned);
            if (parent instanceof ClassLikeElement clazz) {
                relations.addUses(clazz, identifier.getName());
            }
        }
        return null;
    }

    protected IDiagramElement getTopOwner(IOwnedElement owned) {
        IDiagramElement owner = owned.getOwner();
        if (owner instanceof IOwnedElement own) {
            return getTopOwner(own);
        }
        return owner;
    }

    protected FieldElement variableScan(VariableTree variable, IDiagramElement parent) {
        FieldElement field = new FieldElement(variable.getName(), variable.getType().toString(), variable.getModifiers().getFlags(), parent);

        HashSet<Name> pointers = new HashSet<>();

        for (Tree argument : scanCollections(variable.getType())) {
            if (argument.getKind().equals(Tree.Kind.IDENTIFIER)) {
                IdentifierTree a = (IdentifierTree) argument;
                pointers.add(a.getName());
            }
        }

        if (parent instanceof ClassLikeElement) {
            ClassLikeElement parentClass = (ClassLikeElement) parent;
            parentClass.addField(field);
            relations.putFieldElement(field, pointers);
        } else if (parent instanceof MethodElement) {
            MethodElement parentMethod = (MethodElement) parent;
            parentMethod.addParam(field);
            relations.putMethodElement(parentMethod, pointers);
        }

        return field;
    }

    @Override
    public TreePath visitMethod(MethodTree method, IDiagramElement parent) {
        if (parent instanceof ClassLikeElement clazz) {
            MethodElement methodElement = null;
            if (!method.getName().contentEquals("<init>")) {

                methodElement = new MethodElement(method.getName(), method.getModifiers().getFlags(), method.getReturnType().toString(), clazz, false);

                clazz.addMethod(methodElement);

                for (VariableTree param : method.getParameters()) {
                    variableScan(param, methodElement);
                }
                for (AnnotationTree anotation : method.getModifiers().getAnnotations()) {
                    if (anotation.getAnnotationType().toString().contains("Test")) { // https://junit.org/junit5/docs/current/user-guide/#writing-tests-classes-and-methods
                        ((MethodElement) parent).setTestMethod(true);
                        ((ClassElement) parent).maketestClass();
                        break;
                    }
                }
            } else {
                if (method.getBody().getStatements().toString().equals("super();")) {
                    return null;
                }

                methodElement = new MethodElement(method.getName(), method.getModifiers().getFlags(), null, clazz, true);

                clazz.addMethod(methodElement);

                for (VariableTree param : method.getParameters()) {
                    variableScan(param, methodElement);
                }
            }
            return super.visitMethod(method, methodElement);
        }
        return super.visitMethod(method, parent);
    }

    private ArrayList<Tree> scanCollections(Tree fieldType) {
        ArrayList<Tree> pointers = new ArrayList<>();
        if (fieldType != null) {
            if (fieldType.getKind().equals(Tree.Kind.PARAMETERIZED_TYPE)) {
                ParameterizedTypeTree parametrizedReturnType = (ParameterizedTypeTree) fieldType;
                for (Tree argument : parametrizedReturnType.getTypeArguments()) {
                    pointers.add(argument);
                    pointers.addAll(scanCollections(argument));
                }
                pointers.add(parametrizedReturnType.getType());
            } else if (fieldType.getKind().equals(Tree.Kind.ARRAY_TYPE)) {
                ArrayTypeTree arrayReturnType = (ArrayTypeTree) fieldType;
                pointers.add(arrayReturnType.getType());
            } else {
                pointers.add(fieldType);
            }
        }
        return pointers;
    }
}
