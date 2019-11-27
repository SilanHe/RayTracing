/**
 * 
 */
package comp557.a4;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import comp557.a4.PolygonSoup.Vertex;

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
			
			Point3d tmpMin  = new Point3d();
			Point3d tmpMax  = new Point3d();
			
			s.minBoundingBox(tmpMin, tmpMax);
			
			min.x = Math.min(tmpMin.x, min.x);
			min.y = Math.min(tmpMin.y, min.y);
			min.z = Math.min(tmpMin.z, min.z);
			
			max.x = Math.max(tmpMax.x, max.x);
			max.y = Math.max(tmpMax.y, max.y);
			max.z = Math.max(tmpMax.z, max.z);
		}
		
		// lets split the volumes right down the middle of the largest range axis
		
		double len_x = max.x - min.x;
		double len_y = max.y - min.y;
		double len_z = max.z - min.z;
		
		
		
	}

	@Override
	public void minBoundingBox(Point3d min, Point3d max) {
		// TODO Auto-generated method stub
		
	}

}
