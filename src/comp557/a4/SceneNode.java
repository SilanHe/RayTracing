package comp557.a4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import comp557.a4.IntersectResult;
import comp557.a4.Intersectable;
import comp557.a4.Ray;

/**
 * The scene is constructed from a hierarchy of nodes, where each node
 * contains a transform, a material definition, some amount of geometry, 
 * and some number of children nodes.  Each node has a unique name so that
 * it can be instanced elsewhere in the hierarchy (provided it does not 
 * make loops. 
 * 
 * Note that if the material (inherited from Intersectable) for a scene 
 * node is non-null, it should override the material of any child.
 * 
 */
public class SceneNode extends Intersectable {
	
	/** Static map for accessing scene nodes by name, to perform instancing */
	public static Map<String,SceneNode> nodeMap = new HashMap<String,SceneNode>();
	
    public String name;
   
    /** Matrix transform for this node */
    public Matrix4d M;
    
    /** Inverse matrix transform for this node */
    public Matrix4d Minv;
    
    /** Child nodes */
    public List<Intersectable> children;
    
    /**
     * Default constructor.
     * Note that all nodes must have a unique name, so that they can used as an instance later on.
     */
    public SceneNode() {
    	super();
    	this.name = "";
    	this.M = new Matrix4d();
    	this.Minv = new Matrix4d();
    	this.children = new LinkedList<Intersectable>();
    }
    
    private IntersectResult tmpResult = new IntersectResult();
    
    private Ray tmpRay = new Ray();
    
    @Override
    public void intersect(Ray ray, IntersectResult result) {
    	tmpRay.eyePoint.set(ray.eyePoint);
    	tmpRay.viewDirection.set(ray.viewDirection);
    	Minv.transform(tmpRay.eyePoint);
    	Minv.transform(tmpRay.viewDirection);    	
    	tmpResult.t = Double.POSITIVE_INFINITY;
    	tmpResult.n.set(0, 0, 1);
    	
        for ( Intersectable s : children ) {
            s.intersect( tmpRay, tmpResult );
            if ( tmpResult.t > 1e-9 && tmpResult.t < result.t ) {

            	result.set(tmpResult);
            
            }
        }
        // get it back into the right coordinate system
        
        M.transform(result.p);
        M.transform(result.n);
        result.n.normalize();
    }

	@Override
	public void minBoundingBox(Point3d min, Point3d max) {
		// TODO Auto-generated method stub
		
		if (this.max != null && this.min != null) {
			min.set(this.min);
			max.set(this.max);
			return;
		}
		
		for (Intersectable s : children) {
			
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
		
		this.min = new Point3d();
		this.min.set(min);
		this.max = new Point3d();
		this.max.set(max);
		
	}
    
}
