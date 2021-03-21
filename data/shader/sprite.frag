#version 330 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D baseTexture;
uniform vec4 baseColor;

void main()
{
    color = baseColor * texture(baseTexture, TexCoords);
}