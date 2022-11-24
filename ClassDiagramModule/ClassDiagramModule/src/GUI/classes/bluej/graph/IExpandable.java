/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bluej.graph;

import java.awt.event.MouseEvent;
import bluej.pkgmgr.target.DependentTarget;

/**
 *
 * @author vojta
 */
public interface IExpandable {

    /**
     * Resize target to expanded/colapsed size
     */
    public void switchExpansion();
    
    /**
     * Expand target to show detailed info
     */
    public void expand();
    
    /**
     * Colapse target to show basic view
     */
    public void colapse();

    /**
     * Returns whether target is expanded
     * @return 
     */
    public boolean isExpanded();

    /**
     * Returns regular width of target. (Usualy the width of target before expanding)
     * @return 
     */
    public int getRegWidth();

    /**
     * Returns regular height of target. (Ususaly the height of target before expanding)
     * @return 
     */
    public int getRegHeight();
}
