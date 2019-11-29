package comp557.a4;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class CubeMap{
	
	public BufferedImage[] images = new BufferedImage[6];

	public CubeMap() {
		// TODO Auto-generated constructor stub

	}
	
	public void map(Ray ray,Color3f c) {
		
		// map this ray onto a pixel of an image
		
		//image a 2 length cube around camera
		
		Color3f color = new Color3f();
		
		double tXMax = (1)/ray.viewDirection.x; 
		double tXMin = (-1)/ray.viewDirection.x;
		double tYMax = (1)/ray.viewDirection.y; 
		double tYMin = (-1)/ray.viewDirection.y;
		double tZMax = (1)/ray.viewDirection.z;
		double tZMin = (-1)/ray.viewDirection.z;
		
		double bestT = Double.POSITIVE_INFINITY;
		int imagesIndex = 0;
		
		if (tZMax > 0 && tZMax < bestT) {
			bestT = tZMax;
			imagesIndex = 0;
		}
		if (tZMin > 0 && tZMin < bestT) {
			bestT = tZMin;
			imagesIndex = 2;
		}
		if (tXMin > 0 && tXMin < bestT) {
			bestT = tXMin;
			imagesIndex = 3;
		}
		if (tXMax > 0 && tXMax < bestT) {
			bestT = tXMax;
			imagesIndex = 4;
		}
		if (tYMax > 0 && tYMax < bestT) {
			bestT = tYMax;
			imagesIndex = 5;
		}
		if (tYMin > 0 && tYMin < bestT) {
			bestT = tYMin;
			imagesIndex = 1;
		}
		
		// there is 100% intersection just need to
		// check which plane it hit first
		
		Vector3d intersectionPoint = new Vector3d();
		
		Vector3d tempDisplacement = new Vector3d(ray.viewDirection);
		tempDisplacement.scale(bestT);
		
		intersectionPoint.add(tempDisplacement);
		
		if (imagesIndex == 3) {
				// hit -X plane

				// map to image
				// index 3 left
				int x = (int)((1 - intersectionPoint.z) / 2.0 * (double)images[3].getWidth());
				int y = (int)((1 + intersectionPoint.y) / 2.0 * (double)images[3].getHeight());
				
				Color mycolor = new Color( images[3].getRGB(x, y));
				color.set(mycolor);
				
		} else if (imagesIndex == 4){
				//hit  +X plane
				// map to image
				// index 4 right
				int x = (int)((1.0 + intersectionPoint.z) / 2.0 * (double)images[4].getWidth());
				int y = (int)((1.0 + intersectionPoint.y) / 2.0 * (double)images[4].getHeight());
				
				Color mycolor = new Color( images[4].getRGB(x, y));
				color.set(mycolor);
				
		} else if (imagesIndex == 1) {
			
				// hit -Y plane
				// map to image
				
				// index 1 down
				int x = (int)((1.0 + intersectionPoint.x) / 2.0 * (double)images[1].getWidth());
				int y = (int)((1.0 - intersectionPoint.z) / 2.0 * (double)images[1].getHeight());
				
				Color mycolor = new Color( images[1].getRGB(x, y));
				color.set(mycolor);
				
				
		} else if (imagesIndex == 5){
				// hit +Y plane
				
				// index 5 up
				int x = (int)((1.0 + intersectionPoint.x) / 2.0 * (double)images[5].getWidth());
				int y = (int)((1.0 + intersectionPoint.z) / 2.0 * (double)images[5].getHeight());
				
				Color mycolor = new Color( images[5].getRGB(x, y));
				color.set(mycolor);
		
		} else if (imagesIndex == 2){
			
				// hit -Z plane
				
				//index 2 : front
				int x = (int)((1.0 + intersectionPoint.x) / 2.0 * (double)images[2].getWidth());
				int y = (int)((1.0 + intersectionPoint.y) / 2.0 * (double)images[2].getHeight());
				
				Color mycolor = new Color( images[2].getRGB(x, y));
				color.set(mycolor);
		} else {
				// hit +Z plane
				
				//index 0 : back
				int x = (int)((1.0 - intersectionPoint.x) / 2.0 * (double)images[0].getWidth());
				int y = (int)((1.0 + intersectionPoint.y) / 2.0 * (double)images[0].getHeight());
				
				Color mycolor = new Color( images[0].getRGB(x, y));
				color.set(mycolor);
		}
		
		c.x += color.x;
		c.y += color.y;
		c.z += color.z;
	}
}
