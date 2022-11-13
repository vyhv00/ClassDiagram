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
 * Class for abstract class elements
 *
 * @author vojta
 */
public class AbstractClassElement extends ClassLikeElement {

    public AbstractClassElement(Name element, Set<Modifier> modifiers) {
        super(element, modifiers);
    }

    @Override
    public ElementType getElementType() {
        return ElementType.ABSTRACT_CLASS;
    }
}
