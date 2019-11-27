package comp557.a4;

import javax.vecmath.Point3d;

/**
 * Abstract class for an intersectable surface 
 */
public abstract class Intersectable {
	
	/** Material for this intersectable surface */
	public Material material;
	
	public Point3d max;
	
	public Point3d min;
	
	/** 
	 * Default constructor, creates the default material for the surface
	 */
	public Intersectable() {
		this.material = new Material();
	}
	
	/**
	 * Test for intersection between a ray and this surface. This is an abstract
	 *   method and must be overridden for each surface type.
	 * @param ray
	 * @param result
	 */
    public abstract void intersect(Ray ray, IntersectResult result);
    
    /**
     * Get the bounding box for this particular object
     * @param min
     * @param max
     * 
     * @author silanhe
     */

	public abstract void minBoundingBox();
    
}
