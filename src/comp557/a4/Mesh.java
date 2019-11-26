package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3d;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		// TODO: Objective 7: ray triangle intersection for meshes
		
		for (int[] face:soup.faceList) {
			
			// refer to notes for variable names
			// for each face try to find t
			Vector3d aMinusP = new Vector3d();
			aMinusP.sub(soup.vertexList.get(face[0]).p, ray.eyePoint);
			
			// compute the face's normal vector
			Vector3d faceEdge1 = new Vector3d();
			faceEdge1.sub(soup.vertexList.get(face[1]).p, soup.vertexList.get(face[0]).p);
			
			Vector3d faceEdge2 = new Vector3d();
			faceEdge2.sub(soup.vertexList.get(face[2]).p, soup.vertexList.get(face[0]).p);
			
			Vector3d faceNormal = new Vector3d();
			faceNormal.cross(faceEdge1, faceEdge2);
			
			double curFaceT = aMinusP.dot(faceNormal) / ray.viewDirection.dot(faceNormal);
			
			// if a point of intersection has been found with a plane
			if (curFaceT < Double.POSITIVE_INFINITY) {
				
				//find point of intersection
				Vector3d intersectionPoint = new Vector3d();
				intersectionPoint.scaleAdd(curFaceT, ray.viewDirection, ray.eyePoint);
				
				// inside edge test
				
				Vector3d bMinusA = new Vector3d();
				bMinusA.sub(soup.vertexList.get(face[1]).p, soup.vertexList.get(face[0]).p);
				
				Vector3d cMinusB = new Vector3d();
				cMinusB.sub(soup.vertexList.get(face[2]).p, soup.vertexList.get(face[1]).p);
				
				Vector3d aMinusC = new Vector3d();
				aMinusC.sub(soup.vertexList.get(face[0]).p, soup.vertexList.get(face[2]).p);
				
				Vector3d xMinusA = new Vector3d();
				xMinusA.sub(intersectionPoint, soup.vertexList.get(face[0]).p);
				
				Vector3d xMinusB = new Vector3d();
				xMinusB.sub(intersectionPoint, soup.vertexList.get(face[1]).p);
				
				Vector3d xMinusC = new Vector3d();
				xMinusC.sub(intersectionPoint, soup.vertexList.get(face[2]).p);
				
				Vector3d crossA = new Vector3d();
				crossA.cross(bMinusA, xMinusA);
				
				Vector3d crossB = new Vector3d();
				crossB.cross(cMinusB, xMinusB);
				
				Vector3d crossC = new Vector3d();
				crossC.cross(aMinusC, xMinusC);
				
				double dotA = crossA.dot(faceNormal);
				double dotB = crossB.dot(faceNormal);
				double dotC = crossC.dot(faceNormal);
				
				if (dotA > 0 && dotB > 0 && dotC > 0) {
					// this is a valid intersection
					if (curFaceT < result.t) {
						result.p.set(intersectionPoint);
						result.n.set(faceNormal);
						result.n.normalize();
						result.t = curFaceT;
						result.material = this.material;
					}
				}
			}
		}
	}

}
