/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.config;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.common.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.vecmath.Vector2d;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration constraints and initial conditions to use while running the simulation.
 * Loaded from a config file.
 *
 * @author Barry Becker
 */
public class Conditions {

    private static final String START = "start";
    private static final String STOP = "stop";

    private int gridWidth;
    private int gridHeight;

    private double cellSize;
    private double gravity;

    /** keep the walls globally because we need to draw them each frame. */
    private List<Wall> walls;
    private List<Source> sources;
    private List<Region> sinks;
    private List<Region> initialLiquidRegions;

    /**
     * Constructor
     * @param configFile file defines the constraints and initial conditions.
     */
    public Conditions(String configFile) {
        // use a default if null passed in.
        String file = configFile == null ? ConfigurationEnum.BASIC.getFileName() :  configFile;
        URL url = FileUtil.getURL(file);
        Document document = DomUtil.parseXML(url);

        DomUtil.printTree(document, 0);

        parseFromDocument(document);
    }

    private void parseFromDocument(Document document) {

        Node envRoot = document.getDocumentElement();    // environment element
        NodeList children = envRoot.getChildNodes();

        gridWidth = Integer.parseInt(DomUtil.getAttribute(envRoot, "gridWidth"));
        gridHeight = Integer.parseInt(DomUtil.getAttribute(envRoot, "gridHeight"));

        cellSize = Double.parseDouble(DomUtil.getAttribute(envRoot, "cellSize"));
        gravity = Double.parseDouble(DomUtil.getAttribute(envRoot, "gravity"));

        int num = children.getLength();

        // #comment nodes are skipped
        for (int i=0; i < num; i++) {

            Node n = children.item(i);
            String name = n.getNodeName();
            //String name = DomUtil.getAttribute(n, "name");

            if ("walls".equals(name)) {
                parseWalls(n);
            }
            else if ("liquid".equals(name)) {
                parseLiquidRegions(n);
            }
        }
    }

    /**
     * parses xml of this form
     * <pre>
     * <walls>
     *      <wall start="15,10" stop="15,15" />
     *      ...
     *  </walls>
     * </pre>
     */
    private void parseWalls(Node wallsNode) {

        walls = new ArrayList<Wall>();

        NodeList children = wallsNode.getChildNodes();
        int num = children.getLength();
        for (int i=0; i < num; i++) {

            Node n = children.item(i);
            Wall w = new Wall(parseLocation(n, START), parseLocation(n, STOP));
            walls.add(w);
        }
    }

    /**
     * parses xml of this form
     * <pre>
     * <liquid>
     *     <source start="5, 6" velocity="30.0,0.0" />
     *     <region start="1,25" stop="20, 31" />
     * </liquid>
     * </pre>
     */
    private void parseLiquidRegions(Node wallsNode) {

        sources = new ArrayList<Source>();
        sinks = new ArrayList<Region>();
        initialLiquidRegions = new ArrayList<Region>();

        NodeList children = wallsNode.getChildNodes();
        int num = children.getLength();

        // #comment nodes are skipped
        for (int i=0; i < num; i++) {

            Node n = children.item(i);
            String name = n.getNodeName();

            if ("source".equals(name)) {
                Source source = parseSource(n);
                sources.add(source);
            }
            else if ("sink".equals(name)) {
                Region sink = parseRegion(n);
                sinks.add(sink);
            }
            else if ("region".equals(name)) {
                Region region = parseRegion(n);
                initialLiquidRegions.add(region);
            }
        }
    }

    private Source parseSource(Node sourceNode) {

        double startTime =
                Double.parseDouble(DomUtil.getAttribute(sourceNode, "startTime", "0"));
        double duration =
                Double.parseDouble(DomUtil.getAttribute(sourceNode, "duration", "-1"));
        double repeatInterval =
                Double.parseDouble(DomUtil.getAttribute(sourceNode, "repeatInterval", "-1"));

        return new Source(parseLocation(sourceNode, START),
                                                      parseLocation(sourceNode, STOP),
                                                      parseVector(sourceNode, "velocity"),
                                                      startTime, duration, repeatInterval);
    }

    private Region parseRegion(Node node) {

        return new Region(parseLocation(node, START),
                                                      parseLocation(node, STOP));
    }

    private Location parseLocation(Node n, String locationAttribute) {
         String locationString = DomUtil.getAttribute(n, locationAttribute, null);
         if (locationString == null) {
             return null;
         }
         int commaPos = locationString.indexOf(",");
         assert commaPos != -1;
         int xPos = Integer.parseInt(locationString.substring(0, commaPos));
         int yPos = Integer.parseInt(locationString.substring(commaPos + 1));

         // verify that it is within the bounds of the grid
         assert (xPos <=  this.gridWidth && xPos > 0) : "invalid xpos = "+ xPos;
         assert (yPos <=  this.gridHeight && yPos > 0) : "invalid ypos = "+ yPos;

         return new IntLocation(yPos, xPos);
    }

    private Vector2d parseVector(Node n, String vecAttribute) {
         String vecString = DomUtil.getAttribute(n, vecAttribute);
         int commaPos = vecString.indexOf(",");
         assert commaPos != -1;
         double x = Double.parseDouble(vecString.substring(0, commaPos));
         double y = Double.parseDouble(vecString.substring(commaPos + 1));
         return new Vector2d(x, y);
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Source> getSources() {
        return sources;
    }

    public List<Region> getSinks() {
        return sinks;
    }

    public List<Region> getInitialLiquidRegions() {
        return initialLiquidRegions;
    }

    /**
     * @return the gridWidth
     */
    public int getGridWidth() {
        return gridWidth;
    }

    /**
     * @return the gridHeight
     */
    public int getGridHeight() {
        return gridHeight;
    }

    /**
     * @return the cellSize
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     * @return the gravity
     */
    public double getGravity() {
        return gravity;
    }

}
