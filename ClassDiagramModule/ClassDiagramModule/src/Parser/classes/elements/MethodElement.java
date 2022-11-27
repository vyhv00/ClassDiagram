/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import elements.utils.ElementType;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

/**
 * Class for method elements
 *
 * @author vojta
 */
public class MethodElement extends APointingElement implements IOwnedElement {

    private final Name method;
    private final Set<Modifier> modifiers;
    private final LinkedHashSet<FieldElement> params = new LinkedHashSet<FieldElement>(0);
    private final String returnType;
    private boolean testMethod;
    private final boolean constructor;
    private final ClassLikeElement owner;

    public MethodElement(Name method, Set<Modifier> modifiers, String returnType, ClassLikeElement owner, boolean constructor) {
        this.method = method;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.owner = owner;
        testMethod = false;
        this.constructor = constructor;
    }

    @Override
    public Name getElement() {
        return method;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.METHOD;
    }

    @Override
    public Set<Modifier> getModifier() {
        return modifiers;
    }
    
    public Set<FieldElement> getParams() {
        return params;
    }

    public void addParam(FieldElement field) {
        this.params.add(field);
    }
    
    public boolean isTestMethod() {
        return testMethod;
    }

    public void setTestMethod(boolean testMethod) {
        this.testMethod = testMethod;
    }

    public boolean isCostructor() {
        return constructor;
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    @Override
    public ClassLikeElement getOwner() {
        return owner;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.method);
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
        final MethodElement other = (MethodElement) obj;
        if (!Objects.equals(this.method, other.method)) {
            return false;
        }
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }
        if (!Objects.equals(this.params.size(), other.params.size())) {
            return false;
        }
        Iterator<FieldElement> iterator1 = this.params.iterator();
        Iterator<FieldElement> iterator2 = other.params.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            if (!iterator1.next().getIdentifier().equals(iterator2.next().getIdentifier())) {
                return false;
            }
        }
        return true;
    }

    
}
