#version 330 core

#define PI 3.141592653589793
#define DIFFUSE 1
#define METAL   2
#define GLASS   3

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

uniform Camera camera;
uniform ivec2 viewport;

struct Ray{
    vec3 origin;
    vec3 direction;
};

struct Material{
    int type;
    vec3 albedo;
    float fuzz;
};

struct Sphere{
    vec3 center;
    float r;
    Material material;
};

struct HitRecord{
    float t;
    vec3 p;
    vec3 normal;
    Material material;
};

struct HitList{
    Sphere sphere[20];
    int size;
};

float drand48(vec2 co) {
    return 2 * fract(sin(dot(co.xy, vec2(12.9898,78.233))) * 43758.5453) - 1;
}

vec2 randState;

float rand2D()
{
    randState.x = fract(sin(dot(randState.xy+time, vec2(12.9898, 78.233))) * 43758.5453);
    randState.y = fract(sin(dot(randState.xy+time, vec2(12.9898, 78.233))) * 43758.5453);;

    return randState.x;
}

bool near_zero(vec3 v){
    float s = 1e-8;
    return (abs(v.x)<s)&&(abs(v.y)<s)&&(abs(v.y)<s);
}

vec3 random_in_unit_sphere()
{
    float phi = 2.0 * PI * rand2D();
    float cosTheta = 2.0 * rand2D() - 1.0;
    float u = rand2D();

    float theta = acos(cosTheta);
    float r = pow(u, 1.0 / 3.0);

    float x = r * sin(theta) * cos(phi);
    float y = r * sin(theta) * sin(phi);
    float z = r * cos(theta);

    return vec3(x, y, z);
}

vec3 random_in_unit_hemisphere(vec3 normal){
    vec3 in_unit_sphere = random_in_unit_sphere();
    if(dot(in_unit_sphere,normal)>0.0){
        return in_unit_sphere;
    }else{
        return -in_unit_sphere;
    }
}

Ray getRay(Camera camera,vec2 uv){
    vec3 origin = camera.origin;
    vec3 direction = camera.lowerLeftCorner+uv.x*camera.horizontal+uv.y*camera.vertical-camera.origin;
    return Ray(origin,direction);
}

vec3 getRayPoint(Ray ray,float t){
    return ray.origin+ray.direction*t;
}

bool scatter(inout Ray ray,HitRecord rec){
    int materialType = rec.material.type;
    if(materialType==DIFFUSE){
        vec3 scatter_direction = random_in_unit_hemisphere(rec.normal);
        if(near_zero(scatter_direction)){
            scatter_direction = rec.normal;
        }
        ray = Ray(rec.p,scatter_direction);
//        ray = Ray(rec.p,rec.normal+random_in_unit_sphere());
        return true;
    }else if(materialType==METAL){
        vec3 reflected = reflect(ray.direction,rec.normal);
        float fuzz = rec.material.fuzz > 1.0 ? 1.0 : rec.material.fuzz < 0.0 ? 0.0 : rec.material.fuzz;
        if(fuzz>0.0){
            ray = Ray(rec.p,normalize(reflected)+fuzz*random_in_unit_sphere());
        }else{
            ray = Ray(rec.p,reflected);
        }
        return (dot(ray.direction,rec.normal)>0.0);
    }else{
        return false;
    }
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
            rec.material = sphere.material;
            return true;
        }
        t = (-b+sqrt(discriminant))/a*0.5;
        if(t>min_t&&t<max_t){
            rec.t = t;
            rec.p = getRayPoint(ray,rec.t);
            rec.normal = normalize(rec.p-sphere.center);
            rec.material = sphere.material;
            return true;
        }
    }
    return false;
}

bool hit_world(HitList world,Ray ray,float min_t,float max_t,out HitRecord rec){
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
    int hitCount = 0;
    bool isHit = hit_world(world,ray,0.001,1000,rec);

    vec3 scale = vec3(1);
    while(isHit && hitCount<50){
        hitCount++;

//        ray = Ray(rec.p,rec.normal+random_in_unit_sphere());
//        ray = Ray(rec.p,random_in_unit_hemisphere(rec.normal));
        bool isScatter = scatter(ray,rec);
        if(!isScatter) return vec3(0);
        //衰减
        scale *= rec.material.albedo;
        isHit = hit_world(world,ray,0.001,1000,rec);
    }

    float t = (normalize(ray.direction).y+1.)*0.5;
    vec3 color = (1.0 - t) * vec3(1.0, 1.0, 1.0) + t * vec3(0.5, 0.7, 1.0);
    return scale*color;
}

vec3 hsb2rgb(vec3 c){
    vec3 rgb = clamp(abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),
    6.0)-3.0)-1.0,
    0.0,
    1.0 );
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return c.z * mix( vec3(1.0), rgb, c.y);
}

vec3 rgb(int r,int g,int b){
    return vec3(float(r)/255,float(g)/255,float(r)/255);
}

void main()
{
//    Camera camera;
//    camera.origin = vec3(0,0,0);
//    camera.horizontal = vec3(4,0,0);
//    camera.vertical = vec3(0,2,0);
//    camera.lowerLeftCorner = vec3(-2,-1,-1);

    HitList world;
    world.size=4;
    world.sphere[0] = Sphere(vec3(0,-100.5,-1),100,Material(DIFFUSE,vec3(0.5,0.5,0.5),0.0));
    world.sphere[1] = Sphere(vec3(0,0,-1),0.5,Material(DIFFUSE,vec3(0.5537532126201208, 0.5414521567205806, 0.7067976503378637),0.0));
    world.sphere[2] = Sphere(vec3(-1,0,-1),0.5,Material(METAL,vec3(0.5537532126201208, 0.5414521567205806, 0.7067976503378637),0.0));
    world.sphere[3] = Sphere(vec3(1,0,-1),0.5,Material(DIFFUSE,vec3(0.5537532126201208, 0.5414521567205806, 0.7067976503378637),0.0));

    randState = TexCoords;

    vec3 rayColor = vec3(0);

    int spp = 8;
    for(int i=0;i<spp;i++){
        float u = float(TexCoords.x*viewport.x + rand2D()) / viewport.x;
        float v = float(TexCoords.y*viewport.y + rand2D()) / viewport.y;
        Ray ray = getRay(camera,vec2(u,v));
        rayColor += getColor(ray,world);
    }
    rayColor /= spp;

//    Ray ray = getRay(camera,TexCoords);
//    rayColor = getColor(ray,world);

    //gamma 补偿
    color = vec4(sqrt(rayColor),1.);
//    color = vec4(rayColor,1.);
}