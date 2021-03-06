#version 330 core

#define PI 3.141592653589793
#define DIFFUSE 1
#define METAL   2
#define GLASS   3

#define SPHERE 1
#define TRIANGLE 2

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
    float ref_idx;
};

struct Sphere{
    vec3 center;
    float r;
    Material material;
};

struct Triangle{
    vec3 v0,v1,v2;
    Material material;
};

struct Model{
    int type;
    Sphere sphere;
    Triangle triagnle;
};

struct HitRecord{
    float t;
    vec3 p;
    vec3 normal;
    Material material;
};

struct Scene{
    Model item[20];
    int size;
};

uniform Scene world;

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

float schlick(float cosine, float ref_idx) {
    float r0 = (1.0 - ref_idx) / (1.0 + ref_idx);
    r0 = r0 * r0;
    return r0 + (1.0 - r0) * pow((1.0 - cosine), 5.0);
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
    }else if(materialType==GLASS){
        vec3 outward_normal;
        vec3 reflected = reflect(ray.direction, rec.normal);
        float ni_over_nt;
        float reflected_prob;
        float cosine;
        float ref_idx = rec.material.ref_idx;

        if (dot(ray.direction, rec.normal) > 0.0) {
            outward_normal = - rec.normal;
            ni_over_nt = rec.material.ref_idx;
            cosine = ref_idx * dot(normalize(ray.direction), rec.normal);
        } else {
            outward_normal = rec.normal;
            ni_over_nt = 1.0 / rec.material.ref_idx;
            cosine = -ref_idx * dot(normalize(ray.direction), rec.normal);
        }

        vec3 refracted = refract(normalize(ray.direction), outward_normal, ni_over_nt);

        if (refracted.x != 0.0 && refracted.y != 0.0 && refracted.z != 0.0) {
            reflected_prob = schlick(cosine, ref_idx);
            // ray = Ray(rec.p, refracted);
        } else {
            reflected_prob  = 1.0;
            // ray = Ray(rec.p, reflected);
        }

        if (rand2D() < reflected_prob) {
            ray = Ray(rec.p, reflected);
        } else {
            ray = Ray(rec.p, refracted);
        }

        return true;
    }else{
        return false;
    }
}

bool hit_triangle(Triangle face,Ray ray,float min_t,float max_t,out HitRecord rec){
    // compute plane's normal
    vec3 v0v1 = face.v1 - face.v0;
    vec3 v0v2 = face.v2 - face.v0;
    // no need to normalize
    vec3 N = v0v1*v0v2;// v0v1.crossProduct(v0v2); // N
    float area2 = N.length();

    // Step 1: finding P

    // check if ray and plane are parallel ?
    float NdotRayDirection = dot(N,ray.direction);//N.dotProduct(dir);
    if (abs(NdotRayDirection) < 1e-8) // almost 0
    return false; // they are parallel so they don't intersect !

    // compute d parameter using equation 2
    float d = dot(N,face.v0);//N.dotProduct(v0);

    // compute t (equation 3)
    rec.t = (dot(N,ray.origin) + d) / NdotRayDirection;
    if(rec.t<min_t||rec.t>max_t) return false;
    // check if the triangle is in behind the ray
    if (rec.t < 0) return false; // the triangle is behind

    // compute the intersection point using equation 1
    vec3 P = getRayPoint(ray,rec.t);
    rec.p = P;

    // Step 2: inside-outside test
    vec3 C; // vector perpendicular to triangle's plane

    // edge 0
    vec3 edge0 = face.v1 - face.v0;
    vec3 vp0 = P - face.v0;
    C = edge0*vp0;//edge0.crossProduct(vp0);
    if (dot(N,C) < 0) return false; // P is on the right side

    // edge 1
    vec3 edge1 = face.v2 - face.v1;
    vec3 vp1 = P - face.v1;
    C = edge1*vp1;//edge1.crossProduct(vp1);
    if (dot(N,C) < 0)  return false; // P is on the right side

    // edge 2
    vec3 edge2 = face.v0 - face.v2;
    vec3 vp2 = P - face.v2;
    C = edge2*vp2;//edge2.crossProduct(vp2);
    if (dot(N,C) < 0) return false; // P is on the right side;

    rec.normal = N;
    rec.material = face.material;
    return true; // this ray hits the triangle
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

bool hit_world(Scene world,Ray ray,float min_t,float max_t,out HitRecord rec){
    HitRecord tmp;
    bool hit_anyting = false;
    float closet_far = max_t;
    for(int i=0;i<world.size;i++){
        //?????????????????????
        Model model = world.item[i];
        if(model.type==SPHERE){
            if(hit_sphere(model.sphere,ray,min_t,closet_far,tmp)){
                closet_far = tmp.t;
                hit_anyting = true;
                rec = tmp;
            }
        }
        if(model.type==TRIANGLE){
            if(hit_triangle(model.triagnle,ray,min_t,closet_far,tmp)){
                closet_far = tmp.t;
                hit_anyting = true;
                rec = tmp;
            }
        }
    }
    return hit_anyting;
}

vec3 getColor(Ray ray,Scene world){
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
        //??????
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

//    Triangle triagnle;
//    Sphere sphere;
//    Scene world;
//    world.size=4;
//    world.item[0] = Model(SPHERE,Sphere(vec3(-4.0, 1.0, 0.8), 1.0, Material(DIFFUSE, vec3(0.4, 0.2, 0.1), 0.0, 1.0)),triagnle);
//    world.item[1] = Model(SPHERE,Sphere(vec3(0.0, 1.0, 0.0), 1.0, Material(METAL, vec3(0.2, 1.0, 1.0), 0.2, 1.0)),triagnle);
//    world.item[2] = Model(SPHERE,Sphere(vec3(0.0, -1000.0, 0.0), 1000.0, Material(DIFFUSE, vec3(0.5, 0.5, 0.5), 0.0, 1.0)),triagnle);
//    world.item[3] = Model(TRIANGLE,sphere,Triangle(vec3(0,1,0),vec3(1,1,0),vec3(2,1,0),Material(DIFFUSE, vec3(0.4, 0.2, 0.1), 0.0, 1.0)));

    randState = TexCoords;

    vec3 rayColor = vec3(0);

    float u = TexCoords.s;
    float v = TexCoords.t;
    Ray ray = getRay(camera,vec2(u,v));
    rayColor = getColor(ray,world);

    //gamma ??????
    color = vec4(sqrt(rayColor),1.);
//    color = vec4(rayColor,1.);
}