package comp557.a4;

import java.util.ArrayList;

import javax.vecmath.Point3d;

public class Metaballs extends Intersectable {
	
	ArrayList<Sphere> spheres = new ArrayList<Sphere>();

	double thresh = 0.2;

	double eps = 0.01;
	
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
	}

	@Override
	public void minBoundingBox(Point3d min, Point3d max) {
		// TODO Auto-generated method stub
		
	}
}

