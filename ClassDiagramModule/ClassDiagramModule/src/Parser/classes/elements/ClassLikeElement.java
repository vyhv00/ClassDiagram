/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

/**
 * An abstract class for Classes, Abstract classes, Interfaces and Enumerations
 *
 * @author vojta
 */
public abstract class ClassLikeElement extends APointingElement {

    private final Name element;
    private final Set<FieldElement> fields = new LinkedHashSet<FieldElement>();
    private final Set<MethodElement> methods = new LinkedHashSet<MethodElement>();
    private final Set<Modifier> modifiers;
    private ClassLikeElement extension;
    private ClassLikeElement containment;
    private final Set<InterfaceElement> implementations = new LinkedHashSet<InterfaceElement>();

    public ClassLikeElement(Name element, Set<Modifier> modifiers) {
        this.element = element;
        this.modifiers = modifiers;
    }

    @Override
    public Name getElement() {
        return element;
    }

    public Set<FieldElement> getFields() {
        return fields;
    }

    public void addField(FieldElement field) {
        this.fields.add(field);
    }

    public Set<MethodElement> getMethods() {
        return methods;
    }

    public void addMethod(MethodElement method) {
        this.methods.add(method);
    }

    public ClassLikeElement getExtension() {
        return extension;
    }

    public void setExtension(ClassLikeElement extension) {
        this.extension = extension;
    }

    public ClassLikeElement getContainment() {
        return containment;
    }

    public void setContainment(ClassLikeElement containment) {
        this.containment = containment;
    }

    public Set<InterfaceElement> getImplementations() {
        return implementations;
    }

    public void addImplementations(Set<InterfaceElement> implementations) {
        this.implementations.addAll(implementations);
    }

    @Override
    public Set<Modifier> getModifier() {
        return this.modifiers;
    }

    // class is exactly defined by its name in a single package
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.element);
        return hash;
    }

    /**
     * There cannot be more than one public class (object) with exactly same
     * name in one package
     *
     * @param obj
     * @return
     */
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
        final ClassLikeElement other = (ClassLikeElement) obj;
        if (!Objects.equals(this.element, other.element)) {
            return false;
        }
        return true;
    }
}
