/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2012,2015  Michael Kolling and John Rosenberg 
 
 This program is free software; you can redistribute it and/or 
 modify it under the terms of the GNU General Public License 
 as published by the Free Software Foundation; either version 2 
 of the License, or (at your option) any later version. 
 
 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 
 
 You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software 
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package canvas.bluej.pkgmgr.dependency;

import canvas.bluej.pkgmgr.target.DependentTarget;
import canvas.bluej.pkgmgr.Package;

import java.util.Properties;

/**
 * A dependency between two targets in a package
 *
 * @author  Michael Kolling
 * @version $Id: UsesDependency.java 14822 2015-10-16 15:47:55Z davmac $
 */
public class ContainmentDependency extends UsesDependency
{

    public ContainmentDependency(Package pkg, DependentTarget from, DependentTarget to)
    {
        super(pkg, from, to);
    }

    public ContainmentDependency(Package pkg)
    {
        this(pkg, null, null);
    }    

    
    @Override
    public void save(Properties props, String prefix)
    {
        super.save(props, prefix);

        // This may be overridden by decendents
        props.put(prefix + ".type", "ContainmentDependency");
    }

    @Override
    public Type getType()
    {
        return Type.CONTAINMENT;
    }
}
