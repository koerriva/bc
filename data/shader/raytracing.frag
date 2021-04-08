#version 330 core
in vec2 TexCoords;
out vec4 color;
uniform sampler2D texture0;

struct Camera{
    vec3 origin;
    vec3 lowerLeftCorner;
    vec3 horizontal;
    vec3 vertical;
};

struct Ray{
    vec3 origin;
    vec3 direction;
};

Ray getRay(Camera camera,vec2 uv){
    Ray ray;
    ray.origin = camera.origin;
    ray.direction = camera.lowerLeftCorner+uv.x*camera.horizontal+uv.y*camera.vertical;
    return ray;
}

vec3 getRayPoint(Ray ray,float t){
    return ray.origin+ray.direction*t;
}

vec3 getColor(Ray ray){
    float t = (normalize(ray.direction).y+1.)*0.5;
    return mix(vec3(1.),vec3(0.5,0.7,1.),t);
}

void main()
{
    Camera camera;
    camera.origin = vec3(0);
    camera.horizontal = vec3(4,0,0);
    camera.vertical = vec3(0,2,0);
    camera.lowerLeftCorner = vec3(-2,-1,-1);
    Ray ray = getRay(camera,TexCoords);
    vec3 rayColor = getColor(ray);
    color = vec4(rayColor,1.);
}