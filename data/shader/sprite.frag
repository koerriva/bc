#version 330 core

in vec2 TexCoords;
in vec4 baseColor;

out vec4 color;

uniform sampler2D texture0;

void main()
{
    color = baseColor * texture(texture0,TexCoords);
}