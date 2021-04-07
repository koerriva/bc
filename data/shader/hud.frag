#version 330 core

in vec2 TexCoords;

out vec4 color;

uniform sampler2D texture0;

void main()
{
    float len = length(TexCoords-vec2(0.5,0.5));
    color = texture(texture0,TexCoords);
//    float a = color.a;
//    if(len>0.5){
//        color.a -= a/0.5;
//    }
}