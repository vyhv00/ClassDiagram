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
 * Interface for every possible part of class diagram
 *
 * @author vojta
 */
public interface IDiagramElement {

    /**
     * Returns unigue name of element
     *
     * @return
     */
    public Name getElement();

    /**
     * Returns type of element (class, abstract calss, enum, interface, method,
     * field)
     *
     * @return
     */
    public ElementType getElementType();

    /**
     * returns modifiers of element
     *
     * @return
     */
    public Set<Modifier> getModifier();
}
