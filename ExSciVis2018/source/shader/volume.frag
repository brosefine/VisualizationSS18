#version 150
//#extension GL_ARB_shading_language_420pack : require
#extension GL_ARB_explicit_attrib_location : require

#define TASK 10
#define ENABLE_OPACITY_CORRECTION 0
#define ENABLE_LIGHTNING 0
#define ENABLE_SHADOWING 0
#define FRONT_TO_BACK 1

in vec3 ray_entry_position;

layout(location = 0) out vec4 FragColor;

uniform mat4 Modelview;

uniform sampler3D volume_texture;
uniform sampler2D transfer_texture;


uniform vec3    camera_location;
uniform float   sampling_distance;
uniform float   sampling_distance_ref;
uniform float   iso_value;
uniform vec3    max_bounds;
uniform ivec3   volume_dimensions;

uniform vec3    light_position;
uniform vec3    light_ambient_color;
uniform vec3    light_diffuse_color;
uniform vec3    light_specular_color;
uniform float   light_ref_coef;

float epsilon = 0.0001;


bool inside_volume_bounds(const in vec3 sampling_position)
{
    return (   all(greaterThanEqual(sampling_position, vec3(0.0)))
            && all(lessThanEqual(sampling_position, max_bounds)));
}


float get_sample_data(vec3 in_sampling_pos)
{
    vec3 obj_to_tex = vec3(1.0) / max_bounds;
    return texture(volume_texture, in_sampling_pos * obj_to_tex).r;

}

vec3 binary_search(vec3 low, vec3 high){
    float val_low, val_high, val_mid;    
    vec3 mid;

    val_low = get_sample_data(low);
    val_high = get_sample_data(high);

    if(val_high < val_low){
        mid = low;
        low = high;
        high = mid;
    }

    {
        mid = low + (high-low)/2;
        val_low = get_sample_data(low);
        val_high = get_sample_data(high);
        val_mid = get_sample_data(mid);

        if(val_mid < iso_value + epsilon || val_mid > iso_value + epsilon){
            return mid;
        } else if(val_mid > iso_value){
            high = mid;
        } else {
            low = mid;
        }
    } while(val_low <= val_high)

    return vec3(0.0, 0.0, 0.0);
}

vec3 get_gradient(vec3 pos) {

    float step_x = max_bounds.x / volume_dimensions.x;
    float step_y = max_bounds.y / volume_dimensions.y;
    float step_z = max_bounds.z / volume_dimensions.z;


    float d_x = (get_sample_data(vec3(pos.x + step_x, pos.yz)) - get_sample_data(vec3(pos.x - step_x, pos.yz))) / 2;
    float d_y = (get_sample_data(vec3(pos.x, pos.y + step_y, pos.z)) - get_sample_data(vec3(pos.x, pos.y - step_y, pos.z))) / 2;
    float d_z = (get_sample_data(vec3(pos.xy, pos.z + step_z)) - get_sample_data(vec3(pos.xy, pos.z - step_z))) / 2;

    return vec3(d_x, d_y, d_z);

}

vec3 diffuseLighting(vec3 color ,vec3 norm, vec3 light_dir){

    float diffuseTerm = max(dot(norm,light_dir),0);
    float diff_x = color.x * light_diffuse_color.x * diffuseTerm;
    float diff_y = color.y * light_diffuse_color.y * diffuseTerm;
    float diff_z = color.z * light_diffuse_color.z * diffuseTerm;

    return vec3(diff_x, diff_y, diff_z);

}

void main()
{
    /// One step trough the volume
    vec3 ray_increment      = normalize(ray_entry_position - camera_location) * sampling_distance;
    /// Position in Volume
    vec3 sampling_pos       = ray_entry_position + ray_increment; // test, increment just to be sure we are in the volume

    /// Init color of fragment
    vec4 dst = vec4(0.0, 0.0, 0.0, 0.0);

    /// check if we are inside volume
    bool inside_volume = inside_volume_bounds(sampling_pos);
    
    if (!inside_volume)
        discard;

#if TASK == 10
    vec4 max_val = vec4(0.0, 0.0, 0.0, 0.0);
    
    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added
    while (inside_volume) 
    {      
        // get sample
        float s = get_sample_data(sampling_pos);
                
        // apply the transfer functions to retrieve color and opacity
        vec4 color = texture(transfer_texture, vec2(s, s));
           
        // this is the example for maximum intensity projection
        max_val.r = max(color.r, max_val.r);
        max_val.g = max(color.g, max_val.g);
        max_val.b = max(color.b, max_val.b);
        max_val.a = max(color.a, max_val.a);
        
        // increment the ray sampling position
        sampling_pos  += ray_increment;

        // update the loop termination condition
        inside_volume  = inside_volume_bounds(sampling_pos);
    }

    dst = max_val;
#endif 
    
#if TASK == 11

    int steps = 0;
    vec4 accumulated_val = vec4(0.0, 0.0, 0.0, 0.0);
    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added
    while (inside_volume)
    {      
        // get sample
        float s = get_sample_data(sampling_pos);
        vec4 color = texture(transfer_texture, vec2(s, s));
        // dummy code
        //dst = vec4(1.0, 0.0, 0.0, 1.0);
        accumulated_val += color;
        
        // increment the ray sampling position
        sampling_pos  += ray_increment;

        // update the loop termination condition
        inside_volume  = inside_volume_bounds(sampling_pos);

        steps ++;
    }

    dst = accumulated_val / steps;
#endif
    
#if TASK == 12 || TASK == 13
    float s = 0, r = iso_value;
    dst = vec4(0.0, 0.0, 0.0, 0.0);
    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added
    while (inside_volume)
    {
        // get sample
        s = get_sample_data(sampling_pos);
        //if(s < iso_value + epsilon && s > iso_value - epsilon){
        if((s > iso_value && r < iso_value) || (s < iso_value && r > iso_value)){
            dst = texture(transfer_texture, vec2(iso_value, iso_value));

    #if TASK == 13 // Binary Search
            sampling_pos = binary_search(sampling_pos - ray_increment, sampling_pos);
            if(sampling_pos != vec3(0.0, 0.0, 0.0)){
                dst = texture(transfer_texture, vec2(iso_value, iso_value));
            }
    #endif
    #if ENABLE_LIGHTNING == 1 // Add Shading
            vec3 normal = normalize(get_gradient(sampling_pos)) * -1;
            vec3 light_dir = normalize(light_position - sampling_pos);
            dst = vec4(diffuseLighting(dst.xyz, normal, light_dir), 1);
    #if ENABLE_SHADOWING == 1 // Add Shadows
            vec3 light_step = light_dir * sampling_distance;
            vec3 shadow_sample = sampling_pos;
            //r = iso_value;
            while(inside_volume){
                shadow_sample += light_step;

                s = get_sample_data(shadow_sample);
                r = get_sample_data(shadow_sample + light_step);
                if((s > iso_value && r < iso_value) || (s < iso_value && r > iso_value)){
                    dst = vec4(0.0,0.0,0.0,1.0);
                    break;
                }
                r = s;
                inside_volume = inside_volume_bounds(shadow_sample);

            }
    #endif
    #endif
            break;
        }
        // save previous sample value
        r = s;
        // increment the ray sampling position
        sampling_pos += ray_increment;
        // update the loop termination condition
        inside_volume = inside_volume_bounds(sampling_pos);
    }
#endif 

#if TASK == 31
    float transparency = 1.0;
    dst = vec4(0.0, 0.0, 0.0, 0.0);
    #if FRONT_TO_BACK == 0 
    while(inside_volume){
        sampling_pos += ray_increment;
        inside_volume = inside_volume_bounds(sampling_pos);
    }
    sampling_pos -= 2* ray_increment;
    inside_volume = true;
    #endif
    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added
    while (inside_volume)
    {
            float s = get_sample_data(sampling_pos);
            vec4 sample_color = texture(transfer_texture, vec2(s, s));
        // get sample
    #if ENABLE_OPACITY_CORRECTION == 1 // Opacity Correction
            float d = sampling_distance / sampling_distance_ref;
            sample_color.a = 1 - pow((1 - sample_color.a), 255*d);
    #endif
    #if ENABLE_LIGHTNING == 1 // Add Shading
            vec3 normal = normalize(get_gradient(sampling_pos)) * -1;
            vec3 light_dir = normalize(light_position - sampling_pos);
            sample_color.xyz = diffuseLighting(sample_color.xyz, normal, light_dir);
    #endif
    #if FRONT_TO_BACK == 1
            dst.xyz += sample_color.xyz * transparency * (sample_color.a);
            transparency *= (1.0 - sample_color.a);
            dst.a = 1.0 - transparency;

            if(transparency < epsilon){ break; }

            // increment the ray sampling position
            sampling_pos += ray_increment;
    #else
            dst.xyz = sample_color.xyz * sample_color.a + dst.xyz * (1.0-sample_color.a);
            dst.a += sample_color.a;
            sampling_pos -= ray_increment;
    #endif


            // update the loop termination condition
            inside_volume = inside_volume_bounds(sampling_pos);
    }
#endif 

    // return the calculated color value
    FragColor = dst;
}

