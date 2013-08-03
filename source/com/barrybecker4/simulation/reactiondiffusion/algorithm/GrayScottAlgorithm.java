/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm;


/**
 * This is the core of the Gray-Scott reaction diffusion simulation implementation.
 * Based on an work by Joakim Linde and modified by Barry Becker.
 */
final class GrayScottAlgorithm {

    /** We could add scrollbars to scale these */
    private static final double DU = 2.0e-5;
    private static final double DV = 1.0e-5;

    private GrayScottModel model_;

    private double duDivh2;
    private double dvDivh2;


    /**
     * Constructor
     */
    GrayScottAlgorithm(GrayScottModel model) {
        model_ = model;
    }


    public void computeNextTimeStep(int minX, int maxX, double dt) {

        double uv2;
        double[][] u = model_.tmpU;
        double[][] v = model_.tmpV;
        int height = model_.getHeight();
        for (int x = minX; x <= maxX; x++) {
            for (int y = 1; y < height - 1; y++) {
                uv2 = u[x][y] * v[x][y] * v[x][y];
                model_.u[x][y] = calcNewCenter(u, x, y, duDivh2, true, uv2, dt);
                model_.v[x][y] = calcNewCenter(v, x, y, dvDivh2, false, uv2, dt);
            }
        }
    }

    public void computeNewEdgeValues(double dt) {

        int width = model_.getWidth();
        int height = model_.getHeight();

        // top and bottom edges
        for (int x = 0; x < width; x++) {
            calcEdge(x, 0, dt);
            calcEdge(x, height - 1, dt);
        }

         // left and right edges
        for (int y = 0; y < height; y++) {
            calcEdge(0, y, dt);
            calcEdge(width - 1, y, dt);
        }
    }


    public void setH(double h) {
        double h2 = h * h;
        duDivh2 = DU / h2;
        dvDivh2 = DV / h2;
    }


    /**
     * Calculate new values on an edge.
     */
    private void calcEdge(int x, int y, double dt) {

        double uv2 = model_.tmpU[x][y] * model_.tmpV[x][y] * model_.tmpV[x][y];
        model_.u[x][y] = calcNewEdge(model_.tmpU, x, y, duDivh2, true, uv2, dt);
        model_.v[x][y] = calcNewEdge(model_.tmpV, x, y, dvDivh2, false, uv2, dt);
    }


    /**
     * @return new value for a center point.
     */
    private double calcNewCenter(double[][] tmp, int x, int y,
                                 double dDivh2, boolean useF, double uv2, double dt) {

        double sum = model_.getNeighborSum(tmp, x, y) - 4 * tmp[x][y];

        return calcNewAux(tmp[x][y], sum, dDivh2, useF, uv2, dt);
    }

    /**
     * @return new value for an edge point.
     */
    private double calcNewEdge(double[][] tmp, int x, int y,
                               double dDivh2, boolean useF, double uv2, double dt) {

        double sum = model_.getEdgeNeighborSum(tmp, x, y) - 4 * tmp[x][y];

        return calcNewAux(tmp[x][y], sum, dDivh2, useF, uv2, dt);
    }


    private double calcNewAux(double txy, double sum,
                              double dDivh2, boolean useF, double uv2, double dt) {

        double c = useF ? -uv2 + model_.getF() * (1.0 - txy)
                        :  uv2 - model_.getK() * txy;

        return txy + dt * (dDivh2 * sum  + c);
    }
}
