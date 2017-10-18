import java.awt.geom.Ellipse2D;

/**
 * Class that extends creates circle object and provides methods to resize, move object and check if given point is inside the circle.
 */
class Circle extends Ellipse2D.Double {
    Circle(double v, double v1, double v2, double v3) {
        super(v,v1,v2,v3);
    }

    /**
     * Moves circle by given distance.
     * @param x/y moves circle in horizontal/vertical axis.
     */

    public void moveX(double x) {
        this.x += x;
    }
    public void moveY(double y) {
        this.y += y;
    }

    /**
     * Resizes circle by given scaleRate
     * @param scaleRate rate of scale in percent
     */

    public void resize(double scaleRate) {
        double radius = this.height/2*(1-scaleRate);
        this.height = radius*2;
        this.width = radius*2;
        this.x += radius*scaleRate;
        this.y += radius*scaleRate;
    }

    /**
     * Detects if given coordinates are inside of figure
     * @param x first parameter of coordinates
     * @param y second parameter of coordinates
     * @return {@code true} if figure contains given point, {@code false} otherwise.
     */
    public boolean isHit(double x, double y) {
        return this.contains(x,y);
    }
}
