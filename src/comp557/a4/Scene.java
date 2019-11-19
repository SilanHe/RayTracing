package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * my changes
     */
    private double[] offset = {0.5 ,0.5};
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);
        
        for ( int j = 0; j < h && !render.isDone(); j++ ) {
            for ( int i = 0; i < w && !render.isDone(); i++ ) {
            	
                // TODO: Objective 1: generate a ray (use the generateRay method)
            	Ray ray = new Ray();
            	generateRay(i,j,this.offset,cam,ray);
            	
                // TODO: Objective 2: test for intersection with scene surfaces
            	
            	IntersectResult intersectResult = new IntersectResult();
            	
            	if (i == w/2 && j == 0) {
            		int x = 0;
            		x = 1;
            	}
            	
            	for (Intersectable surface : surfaceList) {
            		surface.intersect(ray, intersectResult);
            		
            		if (intersectResult.t < Double.POSITIVE_INFINITY) {
            			break;
            		}
            	}
            	
            	// Here is an example of how to calculate the pixel value.
            	Color3f c = new Color3f(render.bgcolor);
            	int r = (int)(255*c.x);
                int g = (int)(255*c.y);
                int b = (int)(255*c.z);
                int a = 255;
                int argb = (a<<24 | r<<16 | g<<8 | b);
            	
            	if (intersectResult.t < Double.POSITIVE_INFINITY) {
            		  r = (int)(255);
            		  g = 0;
            		  b = 0;
            		  argb = (a<<24 | r<<16 | g<<8 | b);	  
        		} 
            	
                // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
                
            	double LRed = 0;
            	double LGreen = 0;
            	double LBlue = 0;
            	
            	for (Map.Entry<String,Light> mapElement : lights.entrySet()) { 
                    String key = (String)mapElement.getKey(); 
                    Light light = (Light)mapElement.getValue();
                } 
            	  
                
                // update the render image
                render.setPixel(i, j, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
		
		// TODO: Objective 1: generate rays given the provided parameters
		
		// set d to 1 for simplicity
		
		if (i == cam.imageSize.width/2 && j == cam.imageSize.height-1) {
			int x = 0;
			x = 1;
		}
		
		// get the FOV
		double verticalFOV = 2 * Math.tan(cam.fovy/2);
		double horizontalFOV = verticalFOV * cam.imageSize.getWidth()/cam.imageSize.getHeight();
		
		// get the pixel to image mapping
		double u = -horizontalFOV/2 + horizontalFOV * (i + offset[0]) / cam.imageSize.getWidth();
		double v = verticalFOV/2 - verticalFOV * (j + offset[1]) / cam.imageSize.getHeight();
		
		//convert the mapping to world coordinates
		Vector3d lookTowards = new Vector3d();
		lookTowards.sub(cam.to, cam.from);
		
		Vector3d horizontal = new Vector3d();
		horizontal.cross(lookTowards, cam.up);
		horizontal.normalize();
		horizontal.scale(u);
		
		Vector3d vertical = new Vector3d();
		vertical.cross(horizontal,lookTowards);
		vertical.normalize();
		vertical.scale(v);
		
		Vector3d rayDirection = new Vector3d();
		rayDirection.set(lookTowards);
		rayDirection.normalize();
		rayDirection.add(horizontal);
		rayDirection.add(vertical);
		rayDirection.normalize();
		
		// update ray
		ray.set(cam.from, rayDirection);
	}

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public static boolean inShadow(final IntersectResult result, final Light light, final SceneNode root, IntersectResult shadowResult, Ray shadowRay) {
		
		// TODO: Objective 5: check for shdows and use it in your lighting computation
		
		return false;
	}    
}
