import java.awt.*;

/**
 * Class Rect extends default java Rectangle class with method used to detect if given points are inside of figure.
 */
class Rect extends Rectangle {
    Rect(int x,int y,int w,int h) {
        super(x,y,w,h);
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
