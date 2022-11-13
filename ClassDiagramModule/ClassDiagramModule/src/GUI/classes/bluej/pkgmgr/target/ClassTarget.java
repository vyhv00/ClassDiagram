/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2010,2011,2012,2013,2014,2015  Michael Kolling and John Rosenberg

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
package bluej.pkgmgr.target;

import bluej.Utility;
import bluej.graph.Moveable;
import java.awt.Paint;
import java.util.Iterator;

//// import bluej.Config;
////~+ SIMJ08
////import bluej.collect.DataCollector;
////~- SIMJ08
//import bluej.graph.Moveable;
import bluej.pkgmgr.Package;
import bluej.pkgmgr.target.role.AbstractClassRole;
import bluej.pkgmgr.target.role.ClassRole;
import bluej.pkgmgr.target.role.EnumClassRole;
import bluej.pkgmgr.target.role.InterfaceClassRole;
import bluej.pkgmgr.target.role.StdClassRole;
import bluej.pkgmgr.target.role.UnitTestClassRole;
import java.awt.event.MouseEvent;
import java.util.Properties;
import java.util.Set;
import bluej.graph.IExpandable;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.LinkedHashSet;
import javax.swing.JComponent;
import bluej.pkgmgr.graphPainter.ClassTargetPainter;
import static bluej.pkgmgr.target.Target.FRC;

/**
 * A class target in a package, i.e. a target that is a class file built from
 * Java source code
 *
 * @author Michael Cahill
 * @author Michael Kolling
 * @author Bruce Quig
 */
public class ClassTarget extends DependentTarget
        implements Moveable, IExpandable {

    final static int MIN_WIDTH = 60;
    final static int MIN_HEIGHT = 30;

    // the role object represents the changing roles that are class
    // target can have ie changing from applet to an interface etc
    // 'role' should never be null
    // role should be accessed using getRole() and set using
    // setRole(). A role should not contain important state information
    // because role objects are thrown away at a whim.
    private ClassRole role = new StdClassRole();

    private int ghostX;
    private int ghostY;
    private int ghostWidth;
    private int ghostHeight;
    private boolean isDragging = false;
    private boolean isMoveable = true;

    //~vyhv
    private final Set<String> methods = new LinkedHashSet<>();
    private final Set<String> fields = new LinkedHashSet<>();

    private JComponent ghostComponent;
    private JComponent component;

    private boolean expanded = false;
    private int regWidth;
    private int regHeight;
    private int expWidth = 0;
    private int expHeight = 0;
    private int headerHeight;

    /**
     * Create a new class target in package 'pkg'.
     *
     * @param pkg Description of the Parameter
     * @param baseName Description of the Parameter
     */
    public ClassTarget(Package pkg, String baseName) {
        this(pkg, baseName, null);
    }

    /**
     * Create a new class target in package 'pkg'.
     *
     * @param pkg Description of the Parameter
     * @param baseName Description of the Parameter
     * @param template Description of the Parameter
     */
    public ClassTarget(Package pkg, String baseName, String template) {
        super(pkg, baseName);
        ghostComponent = new GhostComponent();
        ghostComponent.setFocusable(false);
        ghostComponent.setBounds(getX(), getY(), getGhostHeight(), getGhostHeight());
        ghostComponent.setVisible(false);
        // we can take a guess at what the role is going to be for the
        // object based on the start of the template name. If we get this
        // wrong, its no great shame as it'll be fixed the first time they
        // successfully analyse/compile the source.
        if (template != null) {
            //~+ SIMJ08
            /*
            if (template.startsWith("applet")) {
                role = new AppletClassRole();
            }
            else
             */
            //~- SIMJ08
            if (template.startsWith("unittest")) {
                role = new UnitTestClassRole(true);
            } else if (template.startsWith("abstract")) {
                role = new AbstractClassRole();
            } else if (template.startsWith("interface")) {
                role = new InterfaceClassRole();
            } else if (template.startsWith("enum")) {
                role = new EnumClassRole();
            }
            //~+ SIMJ08
            /*
            else if (template.startsWith("midlet")) {
                role = new MIDletClassRole();
            }
             */
            //~- SIMJ08
        }
        setGhostPosition(0, 0);
        setGhostSize(0, 0);
        role.setClassTarget(this);

        component = new ClassComponent();
        component.setFocusable(true);
        component.setBounds(getX(), getY(), getHeight(), getHeight());
        component.setVisible(true);
    }

    /**
     * Return the target's base name (ie the name without the package name). eg.
     * Target
     *
     * @return The baseName value
     */
    public String getBaseName() {
        return getIdentifierName();
    }

    /**
     * Return the role object for this class target.
     *
     * @return The role value
     */
    public ClassRole getRole() {
        return role;
    }

    /**
     * Set the role for this class target.
     *
     * <p>
     * Avoids changing over the role object if the new one is of the same type.
     *
     * @param newRole The new role value
     */
    protected void setRole(ClassRole newRole) {
        if (role.getRoleName() != newRole.getRoleName()) {
            role = newRole;
        }
    }

    /**
     * Verify whether this class target is an unit test class
     *
     * @return true if class target is a unit test class, else returns false
     */
    public boolean isUnitTest() {
        return (getRole() instanceof UnitTestClassRole);
    }

    // --- Target interface ---
    /**
     * Gets the backgroundColour attribute of the ClassTarget object
     *
     * @return The backgroundColour value
     */
    public Paint getBackgroundPaint(int width, int height) {
        return getRole().getBackgroundPaint(width, height);
    }

    /**
     * @return Returns the ghostX.
     */
    @Override
    public int getGhostX() {
        return ghostX;
    }

    /**
     * @return Returns the ghostX.
     */
    @Override
    public int getGhostY() {
        return ghostY;
    }

    /**
     * @return Returns the ghostX.
     */
    public int getGhostWidth() {
        return ghostWidth;
    }

    /**
     * @return Returns the ghostX.
     */
    public int getGhostHeight() {
        return ghostHeight;
    }

    /**
     * Set the position of the ghost image given a delta to the real size.
     *
     * @param deltaX The new ghostPosition value
     * @param deltaY The new ghostPosition value
     */
    @Override
    public void setGhostPosition(int deltaX, int deltaY) {
        this.ghostX = getX() + deltaX;
        this.ghostY = getY() + deltaY;
        ghostComponent.setLocation(ghostX, ghostY);
    }

    /**
     * Set the size of the ghost image given a delta to the real size.
     *
     * @param deltaX The new ghostSize value
     * @param deltaY The new ghostSize value
     */
    @Override
    public void setGhostSize(int deltaX, int deltaY) {
        ghostWidth = Math.max(getWidth() + deltaX, MIN_WIDTH);
        ghostHeight = Math.max(getHeight() + deltaY, MIN_HEIGHT);
        ghostWidth = Math.max(regWidth + deltaX, ghostWidth);
        ghostHeight = Math.max(regHeight + deltaY, ghostHeight);
        ghostComponent.setSize(ghostWidth, ghostHeight);
    }

    /**
     * Set the target's position to its ghost position.
     */
    @Override
    public void setPositionToGhost() {
        super.setPos(ghostX, ghostY);
        setSize(ghostWidth, ghostHeight);
        isDragging = false;
        ghostComponent.setVisible(false);
    }

    /**
     * Ask whether we are currently dragging.
     *
     * @return The dragging value
     */
    @Override
    public boolean isDragging() {
        return isDragging;
    }
//

    /**
     * Set whether or not we are currently dragging this class (either moving or
     * resizing).
     *
     * @param isDragging The new dragging value
     */
    @Override
    public void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
        if (this.isDragging) {
            ghostComponent.setVisible(true);
        }
    }

    /**
     * Set the position of this target.
     *
     * @param x The new pos value
     * @param y The new pos value
     */
    @Override
    public void setPos(int x, int y) {
        super.setPos(x, y);
        setGhostPosition(0, 0);
    }

    /**
     * Set the size of this target.
     *
     * @param width The new size value
     * @param height The new size value
     */
    @Override
    public void setSize(int width, int height) {
        regWidth = width;
        regHeight = height;
        if (!isExpanded()) {
            super.setSize(Math.max(width, MIN_WIDTH), Math.max(height, MIN_HEIGHT));
            setGhostSize(0, 0);
        }
    }

    @Override
    public boolean isMoveable() {
        return isMoveable;
    }

    /**
     * Set whether this ClassTarget can be moved by the user (dragged around).
     * This is set false for unit tests which are associated with another class.
     *
     * @see bluej.graph.Moveable#setIsMoveable(boolean)
     */
    @Override
    public void setIsMoveable(boolean isMoveable) {
        this.isMoveable = isMoveable;
    }

    /**
     * Load existing information about this class target
     *
     * @param props the properties object to read
     * @param prefix an internal name used for this target to identify its
     * properties in a properties file used by multiple targets.
     * @exception NumberFormatException Description of the Exception
     */
    @Override
    public void load(Properties props, String prefix) {
        super.load(props, prefix);

        // try to determine if any role was set when we saved
        // the class target. Be careful here as if you add role types
        // you need to add them here as well.
        String type = props.getProperty(prefix + ".type");

        if (UnitTestClassRole.UNITTEST_ROLE_NAME.equals(type)) {
            setRole(new UnitTestClassRole(false));
        } else if (UnitTestClassRole.UNITTEST_ROLE_NAME_JUNIT4.equals(type)) {
            setRole(new UnitTestClassRole(true));
        } else if (AbstractClassRole.ABSTRACT_ROLE_NAME.equals(type)) {
            setRole(new AbstractClassRole());
        } else if (InterfaceClassRole.INTERFACE_ROLE_NAME.equals(type)) {
            setRole(new InterfaceClassRole());
        } else if (EnumClassRole.ENUM_ROLE_NAME.equals(type)) {
            setRole(new EnumClassRole());
        }

        String sFields = props.getProperty(prefix + ".fields");
        if (sFields != null) {
            fields.addAll(Arrays.asList(Utility.split(sFields, "/,/")));
        }
        String sMethods = props.getProperty(prefix + ".methods");
        if (sMethods != null) {
            methods.addAll(Arrays.asList(Utility.split(sMethods, "/,/")));
        }
    }

    /**
     * Save information about this class target
     *
     *
     * @param props the properties object to save to
     * @param prefix an internal name used for this target to identify its
     * properties in a properties file used by multiple targets.
     */
    @Override
    public void save(Properties props, String prefix) {
        super.save(props, prefix);

        if (getRole().getRoleName() != null) {
            props.put(prefix + ".type", getRole().getRoleName());
        }

        props.put(prefix + ".showInterface", Boolean.toString(true));

        props.put(prefix + ".fields", arrayfy(fields));
        props.put(prefix + ".methods", arrayfy(methods));
    }

    //-vyhv
    @Override
    public void doubleClick(MouseEvent evt) {
        expand();
    }

    @Override
    public void expand() {
        expanded = !expanded;
        if (expanded) {
            calcExpSize();
            super.setSize(expWidth, expHeight);
            super.getPackage().getEditor().moveToFront(super.getComponent());
        } else {
            super.setSize(regWidth, regHeight);
            super.getPackage().getEditor().moveToBack(super.getComponent());
            if (assoc != null) {
                super.getPackage().getEditor().moveToBack(assoc.getComponent());
            }
        }
        super.getPackage().getEditor().graphChanged();
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public int getRegWidth() {
        return regWidth;
    }

    @Override
    public int getRegHeight() {
        return regHeight;
    }

    // forbid resing while expanded
    @Override
    public boolean isResizable() {
        return !expanded;
    }

    /**
     * calc expanded size
     */
    private void calcExpSize() {
        int maxWidth = getRegWidth();
        int maxHeight = headerHeight;

        Iterator<String> it = fields.iterator();
        while (it.hasNext()) {
            int width = (int) new Font("SansSerif-bold", Font.PLAIN, 12).getStringBounds(it.next(), FRC).getWidth();
            maxWidth = Math.max(width + 10, maxWidth);
            maxHeight += 12;
        };

        it = methods.iterator();
        while (it.hasNext()) {
            int width = (int) new Font("SansSerif-bold", Font.PLAIN, 12).getStringBounds(it.next(), FRC).getWidth();
            maxWidth = Math.max(width + 20, maxWidth);
            maxHeight += 12;
        };

        expWidth = Math.max(maxWidth, regWidth + 5);
        expHeight = Math.max(maxHeight + 8, regHeight + 5);
    }

    public Set<String> getMethods() {
        return methods;
    }

    public void putMethods(Set<String> methods) {
        this.methods.addAll(methods);
    }

    public Set<String> getFields() {
        return fields;
    }

    public void putFields(Set<String> fields) {
        this.fields.addAll(fields);
    }

    private String arrayfy(Set<String> toArray) {
        String array = "";
        Iterator<String> it = toArray.iterator();
        while (it.hasNext()) {
            array = array + it.next();
            if (it.hasNext()) {
                array = array + "/,/";
            }
        };
        return array;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    @Override
    public int getWidth() {
        return isExpanded() ? expWidth : super.getWidth();
    }

    @Override
    public int getHeight() {
        return isExpanded() ? expHeight : super.getHeight();
    }

    public JComponent getGhostComponent() {
        return ghostComponent;
    }

    private class GhostComponent extends JComponent {

        @Override
        public void paintComponent(Graphics g) {
            ClassTargetPainter.drawSkeleton((Graphics2D) g, ClassTarget.this, ClassTarget.this.getGhostWidth(), ClassTarget.this.getGhostHeight());
            ClassTargetPainter.drawUMLStyle((Graphics2D) g, ClassTarget.this, ClassTarget.this.getGhostWidth(), ClassTarget.this.getGhostHeight(), true);
            ClassTargetPainter.drawShadow((Graphics2D) g, ClassTarget.this.getGhostWidth(), ClassTarget.this.getGhostHeight());
        }

    }
    
    private class ClassComponent extends VertexJComponent {

        @Override
        public void paintComponent(Graphics g) {
            ClassTargetPainter.drawSkeleton((Graphics2D) g, ClassTarget.this, ClassTarget.this.getWidth(), ClassTarget.this.getHeight());
            ClassTargetPainter.drawUMLStyle((Graphics2D) g, ClassTarget.this, ClassTarget.this.getWidth(), ClassTarget.this.getHeight(), false);
            ClassTargetPainter.drawShadow((Graphics2D) g, ClassTarget.this.getWidth(), ClassTarget.this.getHeight());
        }
}
}
