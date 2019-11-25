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
     * my changes, grid super sampling
     */
    private double[][] offset = {
    		{0.50, 0.50},
    		{0.15 ,0.15},
    		{0.85 ,0.85},
    		{0.15 ,0.85},
    		{0.85 ,0.15},
    };

    
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
            	
            	
            	// used to debug
            	if ((i == w/2+40) && j == h/2-15) {
            		int x = 0;
            		x = 1;
            	}
            	
            	Color3f c = new Color3f(render.bgcolor);
            	
            	for (int aa = 0; aa < render.samples; aa ++) {
            		
            		double[] jitter_offset = {Math.random(),Math.random()};
            	
	                // TODO: Objective 1: generate a ray (use the generateRay method)
	            	Ray ray = new Ray();
	            	generateRay(i,j,offset[aa],cam,ray);
	            	
	                // TODO: Objective 2: test for intersection with scene surfaces, get closest intersection
	            	
	            	IntersectResult intersectResult = new IntersectResult();
	            	
	            	for (Intersectable surface : surfaceList) {
	            		IntersectResult tempResult = new IntersectResult();
	            		surface.intersect(ray, tempResult);
	            		
	            		// get closest intersection
	                	
	            		if (tempResult.t > 0 && tempResult.t < intersectResult.t) {
	            			intersectResult.set(tempResult);
	            		}
	            	}
	            	
	                // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
	            	
	            	if (intersectResult.t < Double.POSITIVE_INFINITY) {
	            		
	            		// ambient
	                    
	                    c.x = this.ambient.x * intersectResult.material.diffuse.x;
	                    c.y = this.ambient.y * intersectResult.material.diffuse.y;
	                    c.z = this.ambient.z * intersectResult.material.diffuse.z;
	            		
	        			for (Map.Entry<String,Light> mapElement : lights.entrySet()) {  
	                        Light light = (Light)mapElement.getValue();
	                        
	                        // test if in shadow with respect to this light
	                        
	                        // do shadow rays first
		                    Ray shadowRay = new Ray();
		                    generateShadowRay(intersectResult,light,shadowRay);
		                    IntersectResult shadowResult = new IntersectResult();
		                    
		                    // get closest intersection of shadow light
		                    for (Intersectable surface : surfaceList) {
		                		IntersectResult tempResult = new IntersectResult();
		                		surface.intersect(shadowRay, tempResult);
		                		
		                		// get closest intersection
		                    	
		                		if (tempResult.t > 0 && tempResult.t < shadowResult.t) {
		                			shadowResult.set(tempResult);
		                		}
		                	}
		                    
		                    // test if the shadow ray hits light first
		                    if (!inShadow(intersectResult,light,null,shadowResult,shadowRay)) {
			                    // diffuse lambertian
		                        Vector3d lightFrom = new Vector3d();
		                        lightFrom.sub(light.from,intersectResult.p);
		                        lightFrom.normalize();
		                        
		                        double nDotL = Math.max(0, intersectResult.n.dot(lightFrom));
		                        
		                        c.x += intersectResult.material.diffuse.x * light.color.x * light.power * nDotL;
		                        c.y += intersectResult.material.diffuse.y * light.color.y * light.power * nDotL;
		                        c.z += intersectResult.material.diffuse.z * light.color.z * light.power * nDotL;
		                    
		                        //specular blinn phong
		                        
		                        Vector3d lookFrom = new Vector3d();
		                        lookFrom.sub(cam.from, intersectResult.p);
		                        lookFrom.normalize();
		                        
		                        Vector3d h_bisector = new Vector3d();
		                        h_bisector.add(lightFrom, lookFrom);
		                        h_bisector.normalize();
		                        
		                        double nDotH = Math.pow(Math.max(0, intersectResult.n.dot(h_bisector)),intersectResult.material.shinyness);
		                        
		                        c.x += intersectResult.material.specular.x * light.color.x * light.power * nDotH;
		                        c.y += intersectResult.material.specular.y * light.color.y * light.power * nDotH;
		                        c.z += intersectResult.material.specular.z * light.color.z * light.power * nDotH;
		                    }
	        			}
	            	}
            	}
            	
            	// Here is an example of how to calculate the pixel value.
//            	c.scale(0.75f);
            	c.clamp(0, 1);
            	
            	int r = (int)(255*c.x);
                int g = (int)(255*c.y);
                int b = (int)(255*c.z);
                int a = 255;
                int argb = (a<<24 | r<<16 | g<<8 | b);
            	  
                
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
     * Generate a shadow ray from the point of intersection to the given light
     * @param result
     * @param light
     * @param shadowRay
     */
	public static void generateShadowRay(IntersectResult result,Light light,Ray shadowRay) {
		
		Vector3d newEyePoint = new Vector3d(result.p);
		Vector3d tempViewDirection = new Vector3d();
		tempViewDirection.sub(light.from, result.p);
		
		shadowRay.viewDirection.set(tempViewDirection);
		shadowRay.viewDirection.normalize();
		
		tempViewDirection.scale(1e-5);
		newEyePoint.add(tempViewDirection);
		
		shadowRay.eyePoint.set(newEyePoint);
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
		
		
		Vector3d vertical = new Vector3d();
		vertical.cross(horizontal,lookTowards);
		vertical.normalize();
		
		
		vertical.scale(v);
		horizontal.scale(u);
		
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
		
		Vector3d fullShadowRay = new Vector3d();
		fullShadowRay.sub(light.from,result.p);
		
		double light_t = fullShadowRay.x / shadowRay.viewDirection.x;
		
		if (shadowResult.t <= 0 ||light_t < 1e-9 || light_t < shadowResult.t) {
			return false;
		}
		
		return true;
	}    
}
