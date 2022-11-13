/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import elements.utils.ElementType;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

/**
 * Class for Enum elements
 *
 * @author vojta
 */
public class EnumElement extends ClassLikeElement {

    public EnumElement(Name element, Set<Modifier> modifiers) {
        super(element, modifiers);
    }
    
    // class is exactly defined by its name in a single package
    @Override
    public ElementType getElementType() {
        return ElementType.ENUM;
    }
}
