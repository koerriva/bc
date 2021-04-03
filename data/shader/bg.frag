#version 330 core

#define TWO_PI 6.28318530718

in vec2 TexCoords;
in vec4 baseColor;

out vec4 color;

uniform sampler2D texture0;
uniform float time;

vec3 hsb2rgb(vec3 c){
    vec3 rgb = clamp(abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),
    6.0)-3.0)-1.0,
    0.0,
    1.0 );
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return c.z * mix( vec3(1.0), rgb, c.y);
}

float wave_sin(float x) {
    float amplitude = 0.5;
    float frequency = 11.0;
    float y = sin(x * frequency);
    float t = 0.01*(-time*50.0);
    y += sin(x * frequency * 2.1 + t)*4.5;
    y += sin(x * frequency * 1.72 + t*1.121)*4.0;
    y += sin(x * frequency * 2.221 + t*0.437)*5.0;
    y += sin(x * frequency * 3.1122+ t*4.269)*2.5;
    y *= amplitude*0.06;
    return y;
}
float wave_cos(float x) {
    float amplitude = 0.5;
    float frequency = 11.0;
    float y = cos(x * frequency);
    float t = 0.01*(-time*30.0);
    y += cos(x * frequency * 2.1 + t)*4.5;
    y += cos(x * frequency * 1.72 + t*1.121)*4.0;
    y += cos(x * frequency * 2.221 + t*0.437)*5.0;
    y += cos(x * frequency * 3.1122+ t*4.269)*2.5;
    y *= amplitude*0.06;
    return y;
}
vec2 wave(vec2 v) {
    return vec2(wave_sin(v.x), wave_cos(v.y));
}

void main() {
    vec2 uv = wave(TexCoords);
    vec3 _color = hsb2rgb(vec3(uv.x + sin(uv.y), 0.41234, 1));

    color = vec4(_color,0.675) * texture(texture0,TexCoords);
//    color = vec4(_color,1.0);
//    color = mix(vec4(_color,1.0),texture(texture0,TexCoords),0.85);
}