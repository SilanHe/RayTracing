package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
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
    
    public CubeMap background = null;
    
    public FastPoissonDisk fpd = null;

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * my changes, fast poisson disk for depth of field like in a2
     */

    
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
            	if ((i == w/2) && j == h/2) {
            		int x = 0;
            		x = 1;
            	}
            	
            	Color3f c = new Color3f(render.bgcolor);
            	
            	for (int dofSample = 0; dofSample < cam.cameraSamples; dofSample ++) {
	            	
	            	// double nested for loop to make the sampling easier
	            	for (int sample = 0; sample < render.samples; sample ++) {
	            		
	            		//stochastic sampling
	            		double[] sample_offset = {Math.random() - 0.5,Math.random() - 0.5};
	            			
	        			// TODO: Objective 1: generate a ray (use the generateRay method)
		            	Ray ray = new Ray();
		            	if (this.render.jitter) {
		            		double[] jitter_offset = {sample_offset[0] + 0.5 + Math.random() * 0.01 - 0.005,sample_offset[1] + 0.5 + Math.random() * 0.01 - 0.005};
			            	generateRay(i,j,jitter_offset,cam,ray);
		            	} else {
		            		double[] offset = {0.5,0.5};
		            		generateRay(i,j,offset,cam,ray);
		            	}
	            	
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
		            	
		            	// if we hit an object
		            	if (intersectResult.t < Double.POSITIVE_INFINITY) {
		            		
		            		//set the lookFrom from the start
		            		Vector3d lookFrom = new Vector3d();
	                        lookFrom.sub(cam.from, intersectResult.p);
	                        lookFrom.normalize();
		            		
		            		// TODO: Objective 11: mirror reflection , glossy
	                        
	                        Vector3d specular = new Vector3d(1,1,1);
	                        Vector3d diffuse = new Vector3d(1,1,1);
	                        
	                        boolean mirror = false;
	                        
		            		if (intersectResult.material.mirror) {
	                        	// do mirror reflection
		                        
		                        IntersectResult mirrorResult = new IntersectResult();
		                        Ray newRay = new Ray();
		                        
	                        	getReflection(intersectResult, ray,1,specular,diffuse,mirrorResult,newRay);
	                        	
	                        	intersectResult.set(mirrorResult);
	                        	ray.set(newRay);
	                            lookFrom.set(newRay.viewDirection);
	                            lookFrom.scale(-1);
	                        }

		            		if (intersectResult.t < Double.POSITIVE_INFINITY) {
		            			// ambient
			                    
			                    c.x += this.ambient.x * intersectResult.material.diffuse.x;
			                    c.y += this.ambient.y * intersectResult.material.diffuse.y;
			                    c.z += this.ambient.z * intersectResult.material.diffuse.z;
			            		
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
				                        
				                        c.x += diffuse.x * intersectResult.material.diffuse.x * light.color.x * light.power * nDotL;
				                        c.y += diffuse.y * intersectResult.material.diffuse.y * light.color.y * light.power * nDotL;
				                        c.z += diffuse.z * intersectResult.material.diffuse.z * light.color.z * light.power * nDotL;
				                    
				                        // lookFrom set before loop over the different lights and mirror check
				                        
			                        	//specular blinn phong
				                        
				                        Vector3d h_bisector = new Vector3d();
				                        h_bisector.add(lightFrom, lookFrom);
				                        h_bisector.normalize();
				                        
				                        double nDotH = Math.pow(Math.max(0, intersectResult.n.dot(h_bisector)),intersectResult.material.shinyness);
				                        
				                        c.x += specular.x * intersectResult.material.specular.x * light.color.x * light.power * nDotH;
				                        c.y += specular.y * intersectResult.material.specular.y * light.color.y * light.power * nDotH;
				                        c.z += specular.z * intersectResult.material.specular.z * light.color.z * light.power * nDotH;
				        			}
				            	}
		            		} else if (this.background != null) {
		            			this.background.map(ray, c);
		            		}
		            	} else if (this.background != null){
		            		// if we dont hit any object, map to background cube mapping
		            		
		            		this.background.map(ray, c);
		            		
		            	}
	            	}
            	}
            	
            	
            	// Here is an example of how to calculate the pixel value.
            	float scaleFactor = (float)1/(float)(render.samples * cam.cameraSamples);
            	c.scale(scaleFactor);
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
    
    public void getReflection(IntersectResult oldResult, Ray oldRay,int level,Vector3d specular,Vector3d diffuse,IntersectResult newResult,Ray newRay) {
    	
    	Vector3d lookFrom = new Vector3d(oldRay.viewDirection);
    	lookFrom.scale(-1);
    	
    	Vector3d reflectedRayDirection = new Vector3d(oldResult.n);
    	reflectedRayDirection.scale(2 * oldResult.n.dot(lookFrom));
    	reflectedRayDirection.sub(lookFrom);
    	reflectedRayDirection.normalize();
    	
    	// create our new reflected ray from the computed vector
    	Ray reflectedRay = new Ray();
    	reflectedRay.eyePoint.set(oldResult.p);
    	reflectedRay.viewDirection.set(reflectedRayDirection);
    	
    	// get glossy reflection here
    	if (oldResult.material.glossy) {
    		double offsetX = Math.random() * oldResult.material.blurWidth - oldResult.material.blurWidth;
    		double offsetY = Math.random() * oldResult.material.blurWidth - oldResult.material.blurWidth;
    		double offsetZ = Math.random() * oldResult.material.blurWidth - oldResult.material.blurWidth;
    		
    		reflectedRay.viewDirection.x += offsetX;
    		reflectedRay.viewDirection.y += offsetY;
    		reflectedRay.viewDirection.z += offsetZ;
    	}
    	
    	
    	// find intersection with out new reflected ray
    	IntersectResult tempIntersectResult = new IntersectResult();
    	
    	for (Intersectable surface : surfaceList) {
    		IntersectResult tempResult = new IntersectResult();
    		surface.intersect(reflectedRay, tempResult);
    		
    		// get closest intersection
        	
    		if (tempResult.t > 0 && tempResult.t < tempIntersectResult.t) {
    			tempIntersectResult.set(tempResult);
    		}
    	}
    	
    	// set up for next recursive call
    	Vector3d newLookFrom = new Vector3d(reflectedRayDirection);
		newLookFrom.scale(-1);
		
		specular.x = specular.x * oldResult.material.specular.x;
		specular.y = specular.y * oldResult.material.specular.y;
		specular.z = specular.z * oldResult.material.specular.z;
		
		diffuse.x = diffuse.x * oldResult.material.diffuse.x;
		diffuse.y = diffuse.y * oldResult.material.diffuse.y;
		diffuse.z = diffuse.z * oldResult.material.diffuse.z;
		// exit conditions
		
    	if (tempIntersectResult.t < Double.POSITIVE_INFINITY && tempIntersectResult.material.mirror && level < 5) {
    		// if the reflected ray intersects with another mirror, recursively call our function and pass color down
    		// level limits the depth of the recursion
    		
    		getReflection(tempIntersectResult, reflectedRay,level + 1,specular,diffuse,newResult,newRay);
    	} else {
    		newResult.set(tempIntersectResult);
    		newRay.set(reflectedRay);
    	}
    	
    	
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
		
		Point2d p = new Point2d();
		
		// generate random sample in square
		p.x = Math.random() * cam.lensRadius - cam.lensRadius/2;
		p.y = Math.random() * cam.lensRadius - cam.lensRadius/2;
		
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

 		// for depth of field blur
 		Vector3d verticalSample = new Vector3d(vertical);
 		Vector3d horizontalSample = new Vector3d(horizontal);
 		verticalSample.scale(p.y);
 		horizontalSample.scale(p.x);

 		// scale the vertical and horizontal offset
 		vertical.scale(v);
 		horizontal.scale(u);

 		// set the ray direction
 		Vector3d rayDirection = new Vector3d();
 		rayDirection.set(lookTowards);
 		rayDirection.normalize();

 		//adding the offset to the direction of the ray
 		rayDirection.scale(cam.focusDistance);
 		rayDirection.sub(verticalSample);
 		rayDirection.sub(horizontalSample);

 		rayDirection.normalize();
 		rayDirection.add(horizontal);
 		rayDirection.add(vertical);
 		rayDirection.normalize();

 		Vector3d tempFrom = new Vector3d(cam.from);
 		tempFrom.add(verticalSample);
 		tempFrom.add(horizontalSample);

 		// update ray

 		ray.set(new Point3d(tempFrom), rayDirection);

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
