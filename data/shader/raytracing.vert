#version 330 core
layout (location = 0) in vec2 vertex;
layout (location = 1) in vec2 texCoords;

out vec2 TexCoords;

uniform mat4 P;
uniform mat4 M;

void main()
{
    TexCoords = texCoords;
    gl_Position = P * M * vec4(vertex, 0.0, 1.0);
}