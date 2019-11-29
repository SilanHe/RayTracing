package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO: Objective 6: intersection of Ray with axis aligned box
		
		// compute t values for where the ray crosses each plane
		
		double tXMin = (min.x - ray.eyePoint.x) / ray.viewDirection.x;
		double tYMin = (min.y - ray.eyePoint.y) / ray.viewDirection.y;
		double tZMin = (min.z - ray.eyePoint.z) / ray.viewDirection.z;
		double tXMax = (max.x - ray.eyePoint.x) / ray.viewDirection.x;
		double tYMax = (max.y - ray.eyePoint.y) / ray.viewDirection.y;
		double tZMax = (max.z - ray.eyePoint.z) / ray.viewDirection.z;
		
		double tXLow = Math.min(tXMin, tXMax);
		double tXHigh = Math.max(tXMin, tXMax);
		double tYLow = Math.min(tYMin, tYMax);
		double tYHigh = Math.max(tYMin, tYMax);
		double tZLow = Math.min(tZMin, tZMax);
		double tZHigh = Math.max(tZMin, tZMax);
		
		double tMin = Math.max(Math.max(tXLow,tYLow),tZLow);
		double tMax = Math.min(Math.min(tXHigh,tYHigh),tZHigh);
		
		if (tMin > 0 && tMin <= tMax) {
			// there is intersection
			result.t = tMin;
			
			// find the point of intersection
			Vector3d intersectionPoint = new Vector3d(ray.eyePoint);
			Vector3d displacement = new Vector3d(ray.viewDirection);
			displacement.scale(result.t);
			
			intersectionPoint.add(displacement);
			
			//set point of inersection
			result.p.set(intersectionPoint);
			
			// find the normal vector of the intersection point with respect to the surface
			Vector3d normal = new Vector3d();
			
			if (tMin == tXLow) {
				if (displacement.x > 0) {
					normal.set(-1, 0, 0);
				} else {
					normal.set(1, 0, 0);
				}
				
			} else if (tMin == tYLow) {
				if (displacement.y > 0) {
					normal.set(0, -1, 0);
				} else {
					normal.set(0, 1, 0);
				}
			} else {
				if (displacement.z > 0) {
					normal.set(0, 0, -1);
				} else {
					normal.set(0, 0, 1);
				}
			}
			// set normal
			result.n.set(normal);
			
			// set material
			result.material = this.material;
			
		}
	}	

}
