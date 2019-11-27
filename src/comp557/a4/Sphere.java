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
    	Vector3d d = new Vector3d(ray.viewDirection);
    	Vector3d aMinusC = new Vector3d();
    	aMinusC.sub(ray.eyePoint, this.center);
    	
    	double a = d.dot(ray.viewDirection);
    	double b = 2 * d.dot(aMinusC);
    	double c = aMinusC.dot(aMinusC) - this.radius * this.radius;
    	
    	double discriminant = b * b - 4 * a * c;
    	
    	if (discriminant >= 0) {
    		double t = (-b - Math.sqrt(discriminant))/ (2.0 * a);
    		double t2 = (-b + Math.sqrt(discriminant))/ (2.0 * a);
    		
    		if (t < t2) {
    			if (t > 0) {
    				result.t = t;
    			} else {
    				result.t = t2;
    			}
    		} else {
    			if (t2 > 0) {
    				result.t = t2;
    			} else {
    				result.t = t;
    			}
    		}
    	}
    	
    	if (result.t < Double.POSITIVE_INFINITY) {
    		
    		// find intersection point using t
        	Point3d ray_intersection = new Point3d(ray.eyePoint);
        	Vector3d viewIntersect = new Vector3d(ray.viewDirection);
        	viewIntersect.scale(result.t);
        	
        	ray_intersection.add(viewIntersect);
        	
        	result.p.set(ray_intersection);
        	
        	// set the normal of the intersection at the point with respect to the surface of sphere
        	
        	Vector3d normalIntersection = new Vector3d();
        	normalIntersection.set(2*(result.p.x - this.center.x), 2*(result.p.y - this.center.y), 2*(result.p.z - this.center.z));
        	normalIntersection.normalize();
        	
        	result.n.set(normalIntersection);
        	
        	// set the material
        	result.material = this.material;
    	}
    	
    	
    }

	@Override
	public void minBoundingBox(Point3d min, Point3d max) {
		// TODO Auto-generated method stub
		
		if (this.max != null && this.min != null) {
			min.set(this.min);
			max.set(this.max);
			return;
		}
		
		min.set(center);
		min.x = min.x - 1;
		min.y = min.y - 1;
		min.z = min.z - 1;
		
		max.set(center);
		max.x = max.x + 1;
		max.y = max.y + 1;
		max.z = max.z + 1;
		
		this.min = new Point3d();
		this.min.set(min);
		this.max = new Point3d();
		this.max.set(max);
	}
}
