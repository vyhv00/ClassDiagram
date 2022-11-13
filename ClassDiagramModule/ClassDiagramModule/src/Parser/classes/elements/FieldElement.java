/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import elements.utils.ElementType;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

/**
 * Class for field elements
 *
 * @author vojta
 */
public class FieldElement extends APointingElement {

    private final Name field;
    private final String identifier;
    private final Set<Modifier> modifiers;

    public FieldElement(Name field, String identifier, Set<Modifier> modifiers) {
        this.field = field;
        this.identifier = identifier;
        this.modifiers = modifiers;
    }

    @Override
    public Name getElement() {
        return this.field;
    }

    @Override
    public Set<Modifier> getModifier() {
        return this.modifiers;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.FIELD;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.field);
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
        final FieldElement other = (FieldElement) obj;
        if (!Objects.equals(this.field, other.field)) {
            return false;
        }
        return true;
    }

}
