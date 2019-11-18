package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 2: intersection of ray with sphere
    	
    	// find t using the quadratic formula
    	Vector3d d = new Vector3d(ray.eyePoint);
    	
    	double dDotP = d.dot(ray.viewDirection);
    	double dDotD = d.dot(d);
    	double pDotP = ray.viewDirection.dot(ray.viewDirection);
    	
    	double t = (-dDotP + Math.sqrt(dDotP * dDotP - dDotD * (pDotP - 1)))/dDotD;
    	double t2 = (-dDotP - Math.sqrt(dDotP * dDotP - dDotD * (pDotP - 1)))/dDotD;
    	
    	result.t = Math.max(t,t2);
    	
    	// find intersection point using t
    	Point3d ray_intersection = new Point3d(ray.eyePoint);
    	ray_intersection.scaleAdd(result.t, ray.viewDirection);
    	
    	result.p.set(ray_intersection);
    	
    	// still need to find unit normal to the surface intersection point
    	
    	
    }
    
}
