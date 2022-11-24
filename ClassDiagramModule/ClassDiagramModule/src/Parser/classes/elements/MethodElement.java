/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import elements.utils.ElementType;
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
    private final Set<FieldElement> params = new LinkedHashSet<FieldElement>();
    private final String returnType;
    private boolean testMethod;
    private final ClassLikeElement owner;

    public MethodElement(Name method, Set<Modifier> modifiers, String returnType, ClassLikeElement owner) {
        this.method = method;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.owner = owner;
        testMethod = false;
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
        if (!Objects.equals(this.params, other.params)) {
            return false;
        }
        return Objects.equals(this.owner, other.owner);
    }

    
}
