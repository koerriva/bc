#version 330 core

in vec2 TexCoords;

out vec4 color;

uniform sampler2D texture0;

void main()
{
    float len = length(TexCoords-vec2(0.5,0.5));
    vec2 coord = TexCoords;
    coord.y =-coord.y;
    color = texture(texture0,coord);
//    float a = color.a;
//    if(len>0.5){
//        color.a -= a/0.5;
//    }
}