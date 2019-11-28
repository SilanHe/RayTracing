package comp557.a4;

import java.awt.image.BufferedImage;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class CubeMap{
	
	public BufferedImage[] images = new BufferedImage[6];

	public CubeMap() {
		// TODO Auto-generated constructor stub

	}
	
	public void map(Vector3d ray,Color3f color) {
		
		// map this ray onto a pixel of an image
		
		//image a 2 length cube around camera
		
		double tXMax = Math.max(1/ray.x,0); 
		double tXMin = Math.max(-1/ray.x, 0);
		double tYMax = Math.max(1/ray.y,0); 
		double tYMin = Math.max(-1/ray.y, 0);
		double tZMax = Math.max(1/ray.z,0);
		double tZMin = Math.max(-1/ray.z, 0);
		
		double tXLow = Math.min(tXMin, tXMax);
		double tXHigh = Math.max(tXMin, tXMax);
		double tYLow = Math.min(tYMin, tYMax);
		double tYHigh = Math.max(tYMin, tYMax);
		double tZLow = Math.min(tZMin, tZMax);
		double tZHigh = Math.max(tZMin, tZMax);
		
		double tMin = Math.max(Math.max(tXLow,tYLow),tZLow);
		double tMax = Math.min(Math.min(tXHigh,tYHigh),tZHigh);
		
		// there is 100% intersection just need to
		// check which plane it hit first
		
		if (tMin == tXLow) {
			if (tMin == tXMin) {
				// hit -X plane
				
				
			} else {
				//hit  +X plane
			}
		} else if (tMin == tYLow) {
			if (tMin == tYMin) {
				// hit -Y plane
			} else {
				// hit +Y plane
			}
		} else {
			if (tMin == tZMin) {
				// hit -Z plane
			} else {
				// hit +Z plane
			}
		}
	}
}
