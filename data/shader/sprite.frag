#version 330 core

in vec2 TexCoords;
in vec4 baseColor;

out vec4 color;

uniform sampler2D baseTexture;

void main()
{
    color = baseColor * texture(baseTexture,TexCoords);
}