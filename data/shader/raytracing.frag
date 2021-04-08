#version 330 core
in vec2 TexCoords;
out vec4 color;
uniform sampler2D texture0;

uniform float time;

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

struct Sphere{
    vec3 center;
    float r;
};

struct HitRecord{
    float t;
    vec3 p;
    vec3 normal;
};

struct HitList{
    Sphere sphere[20];
    int size;
};

Ray getRay(Camera camera,vec2 uv){
    vec3 origin = camera.origin;
    vec3 direction = camera.lowerLeftCorner+uv.x*camera.horizontal+uv.y*camera.vertical;
    return Ray(origin,direction);
}

vec3 getRayPoint(Ray ray,float t){
    return ray.origin+ray.direction*t;
}

bool hit_sphere(Sphere sphere,Ray ray,float min_t,float max_t,out HitRecord rec){
    vec3 oc = ray.origin-sphere.center;
    float a = dot(ray.direction,ray.direction);
    float b = 2.0 * dot(oc,ray.direction);
    float c = dot(oc,oc) - sphere.r*sphere.r;
    float discriminant = b*b - 4*a*c;

    if(discriminant>0){
        float t = (-b-sqrt(discriminant))/a*0.5;
        if(t>min_t&&t<max_t){
            rec.t = t;
            rec.p = getRayPoint(ray,rec.t);
            rec.normal = normalize(rec.p-sphere.center);
            return true;
        }
        t = (-b+sqrt(discriminant))/a*0.5;
        if(t>min_t&&t<max_t){
            rec.t = t;
            rec.p = getRayPoint(ray,rec.t);
            rec.normal = normalize(rec.p-sphere.center);
            return true;
        }
    }
    return false;
}

bool hit(HitList world,Ray ray,float min_t,float max_t,out HitRecord rec){
    HitRecord tmp;
    bool hit_anyting = false;
    float closet_far = max_t;
    for(int i=0;i<world.size;i++){
        //选取最近的一个
        if(hit_sphere(world.sphere[i],ray,min_t,closet_far,tmp)){
            closet_far = tmp.t;
            hit_anyting = true;
            rec = tmp;
        }
    }
    return hit_anyting;
}

vec3 getColor(Ray ray,HitList world){
    HitRecord rec;
    if(hit(world,ray,0,1000,rec)){
        return (rec.normal+1)*0.5;
    }
    float t = (normalize(ray.direction).y+1.)*0.5;
    return mix(vec3(1.),vec3(0.5,0.7,1.),t);
}

float drand48(vec2 co) {
    return 2 * fract(sin(dot(co.xy, vec2(12.9898,78.233))) * 43758.5453) - 1;
}

void main()
{
    Camera camera;
    camera.origin = vec3(0,0,0);
    camera.horizontal = vec3(4,0,0);
    camera.vertical = vec3(0,2,0);
    camera.lowerLeftCorner = vec3(-2,-1,-1);

    HitList world;
    world.size=2;
    world.sphere[0] = Sphere(vec3(0,0,-1),0.5);
    world.sphere[1] = Sphere(vec3(0,-100.5,-1),100);

    Ray ray = getRay(camera,TexCoords);

    vec3 rayColor = getColor(ray,world);
    color = vec4(rayColor,1.);
}