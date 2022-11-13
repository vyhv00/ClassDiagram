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
 * Class for regular ćlass elements
 *
 * @author vojta
 */
public class ClassElement extends ClassLikeElement {

    private ClassElement testFor;
    private boolean isTestClass = false;

    public ClassElement(Name element, Set<Modifier> modifiers) {
        super(element, modifiers);
    }

    public ClassElement getTestFor() {
        if (isTestClass) {
            return testFor;
        }
        return null;
    }

    public void setTestFor(ClassElement testFor) {
        if (isTestClass) {
            this.testFor = testFor;
        }
    }
    
    public void maketestClass(){
        isTestClass = true;
    }

    // class is exactly defined by its name in a single package
    @Override
    public ElementType getElementType() {
        if (isTestClass) {
            return ElementType.TEST_CLASS;
        } else {
            return ElementType.CLASS;
        }
    }
}
