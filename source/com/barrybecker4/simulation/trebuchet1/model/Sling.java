/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.trebuchet1.model;

import javax.vecmath.Vector2d;
import java.awt.*;

import static java.lang.Math.PI;
import static java.lang.Math.atan;

/**
 * @author Barry Becker Date: Sep 25, 2005
 */
public class Sling extends RenderablePart {


    private double length;
    private Lever lever;
    private Projectile projectile;
    private double releaseAngle;

    private static final BasicStroke STROKE = new BasicStroke(2.0f);
    private static final Color COLOR = new Color(0, 30, 0);
    private static final Color ARC_COLOR = new Color(60, 90, 70, 150);

    public Sling(double slingLength,  double releaseAngle, Lever lever, Projectile p) {
        length = slingLength;
        this.lever = lever;
        this.releaseAngle = releaseAngle;
        projectile = p;

        Vector2d attachPt = getHookPosition();
        p.setX(attachPt.x + SCALE_FACTOR * length);
        p.setY(attachPt.y - SCALE_FACTOR * p.getRadius());
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getReleaseAngle() {
        return releaseAngle;
    }

    public void setReleaseAngle(double releaseAngle) {
        this.releaseAngle = releaseAngle;
    }

    public Vector2d getHookPosition() {
        double leverLength = lever.getSlingLeverLength();
        double cos = SCALE_FACTOR * leverLength * Math.cos(angle);
        double sin = SCALE_FACTOR * leverLength * Math.sin(angle);
        Vector2d attachPt = new Vector2d(STRUT_BASE_X - sin, (int) (-SCALE_FACTOR * height) + cos);
        return attachPt;
    }

    public Vector2d getProjectileAttachPoint() {
        Vector2d attachPt = getHookPosition();
        Vector2d dir = new Vector2d(projectile.getX()-attachPt.x, projectile.getY()-attachPt.y);
        dir.normalize();
        dir.scale(SCALE_FACTOR * length);
        attachPt.add(dir);
        return attachPt;
    }

    /**
     * the sling angle is the bottom angle bwetween the lever and the sling.
     * @return  the angle of the sling with the lever.
     */
    public double getAngleWithLever() {
        double leverAngleWithHorz = PI / 2.0 - getAngle();
        double slingAngleWithHorz = getAngleWithHorz();
        //System.out.println("slingAngle = leverAngleWithHorz("+leverAngleWithHorz+") "
        // + "  slingAngleWithHorz("+ slingAngleWithHorz+") =  "+(leverAngleWithHorz + slingAngleWithHorz));
        return  -(slingAngleWithHorz - leverAngleWithHorz);
    }

    public double getAngleWithHorz() {
        Vector2d hookPos = getHookPosition();
        double deltaY = projectile.getY() - hookPos.y;
        double deltaX = projectile.getX() - hookPos.x;

        double angle = atan(deltaY / deltaX);

        //System.out.println("angle=  "+angle);
        if (deltaX < 0 || getAngle() > PI / 2) {
            angle += PI;
        }
        return -angle;
    }

    public void render(Graphics2D g2, double scale) {

        g2.setStroke(STROKE);
        g2.setColor(COLOR);

        Vector2d attachPt = getHookPosition();
        Vector2d projectileAttachPt = getProjectileAttachPoint();

        g2.drawLine((int) (scale * attachPt.x),
                    (int) (BASE_Y +  scale * attachPt.y),
                    (int) (scale * projectileAttachPt.x),
                    (int) (BASE_Y + scale * projectileAttachPt.y));


        int startAngle = (int) (getAngleWithHorz() * 180.0 / PI);
        int angle = (int) (getAngleWithLever() * 180.0 / PI);
        int endAngle = startAngle + angle;
        int diameter = (int) (SCALE_FACTOR * 2 * length);

        double rad = diameter >> 1;
        g2.setColor(ARC_COLOR);
        g2.drawArc((int) (scale * (attachPt.x - rad)), (int) (BASE_Y + scale *(attachPt.y - rad)),
                   (int)(scale * diameter), (int)(scale * diameter), startAngle, angle);
        //g2.drawString("start = "+ startAngle, (int)(attachPt.x + rad), (int)(attachPt.y));
        //g2.drawString("end   = "+ endAngle, (int)(attachPt.x + rad), (int)(attachPt.y + 15));
        g2.drawString("angle = "+ angle, (int)(scale * (attachPt.x + rad)), (int)(BASE_Y + scale * (attachPt.y + 30)));
    }
}
