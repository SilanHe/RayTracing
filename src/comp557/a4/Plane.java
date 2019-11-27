package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    	this.max = new Point3d();
    	this.min = new Point3d();
    	this.max.set(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    	this.min.set(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 4: intersection of ray with plane
    	
    	double t = - ray.eyePoint.y / ray.viewDirection.y;
    	
    	if (t > 0) {
    		// there is intersection
    		
    		result.t = t;
    		
    		// find intersection point using t
        	Point3d ray_intersection = new Point3d(ray.eyePoint);
        	Vector3d viewIntersect = new Vector3d(ray.viewDirection);
        	viewIntersect.scale(result.t);
        	
        	ray_intersection.add(viewIntersect);
        	
        	result.p.set(ray_intersection);
        	
        	// set normal
        	
        	result.n.set(this.n);
        	
        	// set material
        	
        	// find closest center
        	Point3d quadrant = new Point3d();
        	quadrant.x = ((result.p.x % 2) + 2) % 2;
        	quadrant.z = ((result.p.z % 2) + 2) % 2;
        	
        	// why are the instructions inversed 
        	// if material2 exists
        	if (this.material2 != null) {
        		if (quadrant.x >= 1) {
            		if (quadrant.z >= 1) {
            			//material 2
            			result.material = this.material;
            		} else {
            			//material 1
            			result.material = this.material2;
            		}
            	} else {
            		if (quadrant.z >= 1) {
            			//material 1
            			result.material = this.material2;
            		} else {
            			//material 2
            			result.material = this.material;
            		}
            	}
        	} else {
        		//if material 2 does not exist, default
        		result.material = this.material;
        	}        	
    	}
    	
    }


	@Override
	public void minBoundingBox(Point3d min, Point3d max) {
		// TODO Auto-generated method stub
		min.set(this.min);
		max.set(this.max);
	}
    
}
