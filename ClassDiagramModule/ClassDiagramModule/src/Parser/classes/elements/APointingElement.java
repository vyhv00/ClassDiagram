/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elements;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vojta
 */
public abstract class APointingElement implements IDiagramElement {
    
    private final Set<ClassLikeElement> pointers = new HashSet<>();
    
    
    /**
     * Adds pointers to some ClassLikeElements
     *
     * @param pointer
     */
    public void addPointers(Set<ClassLikeElement> pointer) {
        this.pointers.addAll(pointer);
    }

    /**
     * Adds a single pointer to some ClassLikeElements
     *
     * @param pointer
     */
    public void addPointers(ClassLikeElement pointer) {
        this.pointers.add(pointer);
    }
    
    
    public Set<ClassLikeElement> getPointers() {
        return this.pointers;
    }
}
