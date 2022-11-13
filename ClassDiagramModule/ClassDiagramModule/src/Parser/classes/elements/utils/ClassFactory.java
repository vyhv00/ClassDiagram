/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elements.utils;

import elements.AbstractClassElement;
import elements.ClassElement;
import elements.ClassLikeElement;
import elements.EnumElement;
import elements.InterfaceElement;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import javax.lang.model.element.Modifier;

/**
 * A factory for creating proper kind of ClassLikeElement from ClassTree
 *
 * @author vojta
 */
public class ClassFactory {

    public static ClassLikeElement createClass(ClassTree element) {
        if (element.getKind().equals(Tree.Kind.INTERFACE)) {
            return new InterfaceElement(element.getSimpleName(), element.getModifiers().getFlags());
        } else if (element.getKind().equals(Tree.Kind.ENUM)) {
            return new EnumElement(element.getSimpleName(), element.getModifiers().getFlags());
        } else if (element.getKind().equals(Tree.Kind.CLASS)) {
            if (element.getModifiers().getFlags().contains(Modifier.ABSTRACT)) {
                return new AbstractClassElement(element.getSimpleName(), element.getModifiers().getFlags());
            } else {
                return new ClassElement(element.getSimpleName(), element.getModifiers().getFlags());
            }
        }
        return null;
    }
}
