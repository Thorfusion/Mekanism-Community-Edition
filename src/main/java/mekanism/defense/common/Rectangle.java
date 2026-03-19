package defense.common;

public class Rectangle
{
    public Vector2 min;
    public Vector2 max;

    public Rectangle()
    {
        this(new Vector2(), new Vector2());
    }

    public Rectangle(Vector2 min, Vector2 max)
    {
        this.min = min;
        this.max = max;
    }

    /** Checks if a point is located inside a region */
    public boolean isIn(Vector2 point)
    {
        return (point.x > this.min.x && point.x < this.max.x) && (point.y > this.min.y && point.y < this.max.y);
    }

    /** Returns whether the given region intersects with this one. */
    public boolean isIn(Rectangle region)
    {
        return region.max.x > this.min.x && region.min.x < this.max.x ? (region.max.y > this.min.y && region.min.y < this.max.y ? true : false) : false;
    }
}
