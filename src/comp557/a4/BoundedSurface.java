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

	/**
	 * Axis Aligned Bounding Box : Default constructor.
	 */
	public BoundedSurface() {
		// TODO Auto-generated constructor stub
		this.max = new Point3d();
		this.min = new Point3d();
		this.material = null; // if material is null then bounding box
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
			// check for intersection in child surfaces
			
			for (Intersectable s : surfaces) {
				IntersectResult tmpResult = new IntersectResult();
				s.intersect( ray, tmpResult );
	            if ( tmpResult.t > 1e-9 && tmpResult.t < result.t ) {
	            	result.set(tmpResult);
	            }
			}
		}
	}
	
	public void generateBoundedSurfaces() {
		// lets split the volumes right down the middle of the largest range axis
		
		minBoundingBox();
		
		if (surfaces.size() < 2) {
			return;
		}
		
		double lenX = Math.min(Double.MAX_VALUE,max.x - min.x);
		double lenY = Math.min(Double.MAX_VALUE,max.y - min.y);
		double lenZ = Math.min(Double.MAX_VALUE,max.z - min.z);
		
		BoundedSurface lower = new BoundedSurface();
		BoundedSurface higher = new BoundedSurface();
		List<Intersectable> tempSurfacesMiddle = new ArrayList<Intersectable>();
		
		double maxLen = Math.max(lenX, Math.max(lenY, lenZ));
		
		
		if (maxLen == lenX) {
			
			double midX;
			midX = (max.x + min.x) / 2;
			lower.max.set(midX, max.y, max.z);
			lower.min.set(min.x,min.y,min.z);
			
			higher.max.set(max.x,max.y,max.z);
			higher.min.set(midX,min.y,min.z);

			
		} else if (maxLen == lenY) {
			
			double midY;
			midY = (max.y + min.y) / 2;
			lower.max.set(max.z, midY, max.z);
			lower.min.set(min.x,min.y,min.z);
			
			higher.max.set(max.x,max.y,max.z);
			higher.min.set(min.x,midY,min.z);
			
		} else {
			
			double midZ;
			midZ = (max.z + min.z) / 2;
			lower.max.set(max.x, max.y, midZ);
			lower.min.set(min.x,min.y,min.z);
			
			higher.max.set(max.x,max.y,max.z);
			higher.min.set(min.x,min.y,midZ);
			
		}
		
		for (Intersectable s : surfaces) {
			
			s.minBoundingBox();
			boolean accountedFor = false;
			if (s.min.x >= higher.min.x && s.min.y >= higher.min.y && s.min.z >= higher.min.z) {
				higher.surfaces.add(s);
				accountedFor = true;
			}
			
			if (s.max.x <= lower.max.x && s.max.y <= lower.max.y && s.max.z <= lower.max.z) {
				lower.surfaces.add(s);
				accountedFor = true;
			}
			
			if (!accountedFor) {
				tempSurfacesMiddle.add(s);
			}
		}
		
		surfaces.clear();
		surfaces.add(higher);
		surfaces.add(lower);
		surfaces.addAll(tempSurfacesMiddle);
		
		higher.generateBoundedSurfaces();
		lower.generateBoundedSurfaces();
	}

	@Override
	public void minBoundingBox() {
		// TODO Auto-generated method stub
		
		for (Intersectable s : surfaces) {
			s.minBoundingBox();
			
			min.x = Math.min(s.min.x, min.x);
			min.y = Math.min(s.min.y, min.y);
			min.z = Math.min(s.min.z, min.z);
			
			max.x = Math.max(s.max.x, max.x);
			max.y = Math.max(s.max.y, max.y);
			max.z = Math.max(s.max.z, max.z);
		}
	}

}
