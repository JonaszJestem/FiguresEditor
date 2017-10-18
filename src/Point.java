/**
 * Class that represents points
 */
class Point {
    private final double x;
    private final double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns first/second parameter of point.
     * @return x/y coordinate
     */
    public double getX() {
        return x;
    }
    public double getY() { return y; }
}
