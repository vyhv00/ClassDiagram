/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2010,2012,2013,2014  Michael Kolling and John Rosenberg 
 
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
package canvas.bluej.pkgmgr.graphPainter;

import canvas.bluej.graph.GraphEditor;
import canvas.bluej.graph.Moveable;
import canvas.bluej.graph.Edge;
import canvas.bluej.graph.Graph;
import canvas.bluej.graph.GraphPainter;
import canvas.bluej.graph.Vertex;
import java.awt.*;
import java.util.Iterator;

import canvas.bluej.pkgmgr.dependency.ContainmentDependency;
import canvas.bluej.pkgmgr.dependency.AssociationDependency;
import canvas.bluej.pkgmgr.dependency.Dependency;
import canvas.bluej.pkgmgr.dependency.ExtendsDependency;
import canvas.bluej.pkgmgr.dependency.ImplementsDependency;
import canvas.bluej.pkgmgr.dependency.UsesDependency;
import canvas.bluej.pkgmgr.target.ClassTarget;

/**
 * Paints a Graph using TargetPainters
 * 
 * @author fisker
 */
public class GraphPainterStdImpl
    implements GraphPainter
{
    static final int TEXT_HEIGHT = 12;
    static final int TEXT_BORDER = 4;
    static final float alpha = (float) 0.5;
    static AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

    private final ClassTargetPainter classTargetPainter = new ClassTargetPainter();
    private final ExtendsDependencyPainter extendsDependencyPainter = new ExtendsDependencyPainter();
    private final ImplementsDependencyPainter implementsDependencyPainter = new ImplementsDependencyPainter();
    private final UsesDependencyPainter usesDependencyPainter = new UsesDependencyPainter();
    private final ContainmentDependencyPainter containmentDependencyPainter = new ContainmentDependencyPainter();
    private DependencyPainter associationDependencyPainter = new AssociationDependencyPainter();
    private static final GraphPainterStdImpl singleton = new GraphPainterStdImpl();

    private GraphEditor graphEditor;

    private GraphPainterStdImpl()
    {} // prevent instantiation

    /**
     * Paint 'graph' on 'g'
     */
    public void paint(Graphics2D g, GraphEditor graphEditor)
    {
        // Use system settings for text rendering (Java 6 only)
        this.graphEditor = graphEditor;
        Graph graph = graphEditor.getGraph();
        paintEdges(g, graph);
        paintVertices(g, graph);
        paintGhosts(g, graph);
        
    }

    /**
     * Paint the edges in 'graph' on 'g'. Edges that are declared as
     * "not visible" will not be painted.
     * 
     * @param g
     * @param graph
     */
    private void paintEdges(Graphics2D g, Graph graph)
    {
        Edge edge;
        //Paint the edges
        for (Iterator<? extends Edge> it = graph.getEdges(); it.hasNext();) {
            edge = it.next();
            if (edge.isVisible()) {
                paintEdge(g, edge);
            }
        }
    }

    /**
     * Paint the vertices in 'graph' on 'g'. If one of the targets to be painted
     * is in the process of drawing a dependency to another class, assign that
     * class to 'dependency'. Vertices that are declared as "not visible" will
     * not be painted.
     * 
     * @param g
     * @param graph
     * @param dependentTarget
     * @return the class from which a dependency is being drawn. Null if none.
     */
    private void paintVertices(Graphics2D g, Graph graph)
    {
        for (Iterator<? extends Vertex> it = graph.getVertices(); it.hasNext();) {
            Vertex vertex = it.next();
            if (vertex.isVisible()) {
                paintVertex(g, vertex);
            }
        }
    }

    /**
     * Paint the ghosts (transparent versions) of the vertices in 'graph' that
     * are being dragged in the diagram.
     * 
     * @param g
     * @param graph
     */
    private void paintGhosts(Graphics2D g, Graph graph)
    {
        for (Iterator<? extends Vertex> it = graph.getVertices(); it.hasNext();) {
            Vertex vertex = it.next();
            if (vertex instanceof Moveable) {
                Moveable moveable = (Moveable) vertex;
                if (moveable.isDragging()) {
                    paintGhostVertex(g, moveable);
                }
            }
        }
    }

    /**
     * Paint 'edge' on 'g'
     */
    private void paintEdge(Graphics2D g, Edge edge)
    {
        if (!(edge instanceof Dependency)) {
            throw new IllegalArgumentException("Not a dependency");
        }
        Dependency dependency = (Dependency) edge;
        getDependencyPainter(dependency).paint(g, dependency, isPermanentFocusOwner());
    }

    /**
     * Return the appropriate painter for a given dependency.
     * 
     * @param edge  The dependency we want to paint
     * @return  A painter that can paint the given dependency.
     */
    public DependencyPainter getDependencyPainter(Edge edge)
    {
        if (edge instanceof ImplementsDependency) {
            return implementsDependencyPainter;
        }
        else if (edge instanceof ExtendsDependency) {
            return extendsDependencyPainter;
        }
        else if (edge instanceof ContainmentDependency) {
            return containmentDependencyPainter;
        }
        else if (edge instanceof AssociationDependency) {
            return associationDependencyPainter;
        }
        else if (edge instanceof UsesDependency) {
            return usesDependencyPainter;
        }
        else {
            //assert false;
            return null;
        }
    }

    /**
     * Paint 'vertex' on 'g' using the appropiate painter.
     * 
     * @param g
     * @param vertex
     */
    private void paintVertex(Graphics2D g, Vertex vertex)
    {
        if (vertex instanceof ClassTarget) {
            classTargetPainter.paint(g, (ClassTarget) vertex, isPermanentFocusOwner());
        }
//        else if (vertex instanceof ReadmeTarget) {
//            readmePainter.paint(g, (ReadmeTarget) vertex, false);
//        }
//        else if (vertex instanceof PackageTarget) {
//            packageTargetPainter.paint(g, (PackageTarget) vertex, false);
//        }
//        else {
//            asserts false;
//        }
    }

    /**
     * Paint a ghostet (transparent) version of 'vertex' on 'g'
     * 
     * @param g
     * @param vertex
     */
    private void paintGhostVertex(Graphics2D g, Moveable vertex)
    {
        if (vertex instanceof ClassTarget) {
            classTargetPainter.paintGhost(g, (ClassTarget) vertex, isPermanentFocusOwner());
        }
//        else if (vertex instanceof PackageTarget) {
//            packageTargetPainter.paintGhost(g, (PackageTarget) vertex, isPermanentFocusOwner());
//        }
//        else {
//            //asserts false;
//        }
    }

    /**
     * Tell whether the graph editor has the permanent key focus - 
     * this is NOT the temporary which hasFocus() and isFocusOwner() uses.
     */
    private boolean isPermanentFocusOwner()
    {
        return graphEditor.hasPermFocus();
    }

    /**
     * Get reference to the singleton GraphPainterStdImpl
     * 
     * @return GraphPainterStdImpl
     */
    public static GraphPainter getInstance()
    {
        return singleton;
    }

}
