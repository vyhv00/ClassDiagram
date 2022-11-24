/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramParser;

import elements.APointingElement;
import elements.ClassElement;
import elements.ClassLikeElement;
import elements.FieldElement;
import elements.MethodElement;
import elements.utils.ElementType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Name;

/**
 * Holds classes and relations pointers in a single package during code scanning
 * and can create Set of ClassLikeElements with real relations between
 * themselves
 *
 * @author vojta
 */
public class PackageRelations {

    private String packageName;
    private final Map<Name, ClassLikeElement> classLikeElements;
    private final Map<FieldElement, Set<Name>> fieldPointers;
    private final Map<MethodElement, Set<Name>> methodPointers;
    private final Map<ClassLikeElement, Set<Name>> usesPointers;
    private final Map<ClassLikeElement, Set<Name>> classImplementations;
    private final Map<ClassLikeElement, Name> classExtension;

    public PackageRelations(String packageName) {
        this.packageName = packageName;
        this.classLikeElements = new HashMap<>();
        this.fieldPointers = new HashMap<>();
        this.methodPointers = new HashMap<>();
        this.usesPointers = new HashMap<>();
        this.classImplementations = new HashMap<>();
        this.classExtension = new HashMap<>();
    }

    /**
     * Puts a ClassLikeElement into map identified by elements unigue Name for
     * obtain it while creating realtíons
     *
     * @param clazz
     */
    public void putClassLikeElement(ClassLikeElement clazz) {
        this.classLikeElements.put(clazz.getElement(), clazz);
    }

    /**
     * Puts a field element into map with set of Names of field identifiers
     * possibly poiting to some ClassLikeElements in this package
     *
     * @param field
     * @param fieldPointers
     */
    public void putFieldElement(FieldElement field, Set<Name> fieldPointers) {
        this.fieldPointers.put(field, fieldPointers);
    }

    /**
     * Puts a method element into map with set of Names of field identifiers
     * possibly poiting to some ClassLikeElements in this package
     *
     * @param method
     * @param fieldPointers
     */
    public void putMethodElement(MethodElement method, Set<Name> fieldPointers) {
        this.methodPointers.put(method, fieldPointers);
    }
    
    /**
     * Puts a method element into map with set of Names of field identifiers
     * possibly poiting to some ClassLikeElements in this package
     *
     * @param clazz
     * @param classPointer
     */
    public void addUses(ClassLikeElement clazz, Name classPointer) {
        if(!usesPointers.containsKey(clazz)) {
            this.usesPointers.put(clazz, new HashSet<>());
        }
        usesPointers.get(clazz).add(classPointer);
    }

    /**
     * packageName can be set only once, then keeps still
     *
     * @param packageName
     */
    public void setPackageName(String packageName) {
        if (this.packageName == null) {
            this.packageName = packageName;
        }
    }

    /**
     * Returns the identifier of this class
     *
     * @return
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Puts a ClassLikeElement into map with set of Names of implementations
     * possibly pointind to some Interfaces in this package
     *
     * @param clazz
     * @param implementations
     */
    public void putClassImplementations(ClassLikeElement clazz, Set<Name> implementations) {
        this.classImplementations.put(clazz, implementations);
    }

    /**
     * Puts a ClassLikeElement into map with set of Names of implementations
     * possibly pointind to some classes and abstract classes in this package
     *
     * @param clazz
     * @param extension
     */
    public void putClassExtension(ClassLikeElement clazz, Name extension) {
        this.classExtension.put(clazz, extension);
    }

    /**
     * Calls methods for creating the real relations between ClassLikeElements
     * in this package and retruns Set of these related ClassLikeElements in
     * this package
     *
     * @return
     */
    public Set<ClassLikeElement> getRelatedClasses() {
        createClassRelations();
        createRelations(fieldPointers);
        createRelations(methodPointers);
        createRelations(usesPointers);
        createTestClasses();
        return new HashSet<>(classLikeElements.values());
    }

    /**
     * Creates relations between class as implementations and extensions
     */
    private void createClassRelations() {
        classExtension.forEach((clazz, classPointer) -> {
            clazz.setExtension(classLikeElements.get(classPointer));
        });

        classImplementations.forEach((clazz, classPointers) -> {
            Set realPointers = new HashSet<ClassLikeElement>();
            classPointers.forEach(pointer -> {
                realPointers.add(classLikeElements.get(pointer));
            });
            clazz.addImplementations(realPointers);
        });
    }

    /**
     * Creates relations between classes based on their pointers
     */
    private void createRelations(Map<? extends APointingElement, Set<Name>> map) {
        map.forEach((el, classPointers) -> {
            Set realPointers = new HashSet<ClassLikeElement>();
            classPointers.forEach(pointer -> {
                if (classLikeElements.get(pointer) != null) {
                    realPointers.add(classLikeElements.get(pointer));
                }
            });
            el.addPointers(realPointers);
        });
    }

    private void createTestClasses() {
        classLikeElements.values().forEach(testClass -> {
            if (testClass instanceof ClassElement && ((ClassElement) testClass).getElementType().equals(ElementType.TEST_CLASS)) {
                for (ClassLikeElement testFor : classLikeElements.values()) {
                    if (testFor instanceof ClassElement && testFor.getElement().contentEquals(testClass.getElement().subSequence(0, testClass.getElement().length() - 4))) {
                        ((ClassElement) testClass).setTestFor((ClassElement) testFor);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.packageName);
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
        final PackageRelations other = (PackageRelations) obj;
        if (!Objects.equals(this.packageName, other.packageName)) {
            return false;
        }
        return true;
    }

}
