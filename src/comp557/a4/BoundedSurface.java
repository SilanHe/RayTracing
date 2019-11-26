/**
 * 
 */
package comp557.a4;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * @author Silan He 260738985
 *
 */
public class BoundedSurface extends Intersectable {
	
	/**
	 * List of Surfaces
	 */
	public List<Intersectable> surfaces = new ArrayList<Intersectable>();
	
	public Point3d max;
	public Point3d min;
	
	private IntersectResult tmpResult = new IntersectResult();

	/**
	 * Axis Aligned Bounding Box : Default constructor.
	 */
	public BoundedSurface() {
		// TODO Auto-generated constructor stub

		this.material = null; // if material is null then bounding box
		
    	this.max = new Point3d();
    	this.min = new Point3d();
	}

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO Auto-generated method stub
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
			
			// check for intersection in child surfaces
			
			for (Intersectable s : surfaces) {
				
				s.intersect( ray, tmpResult );
	            if ( tmpResult.t > 1e-9 && tmpResult.t < result.t ) {

	            	result.set(tmpResult);
	            
	            }
			}
		}
	}
	
	public void generateBoundedSurfaces() {
		
		//get min point and max point
		
		for (Intersectable s : surfaces) {
		
		}
	}

}
