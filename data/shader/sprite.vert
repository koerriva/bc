#version 330 core
layout (location = 0) in vec4 vertex; // <vec2 position, vec2 texCoords>

out vec2 TexCoords;

uniform mat4 M;
uniform mat4 P;
uniform mat4 V;

void main()
{
    TexCoords = vertex.zw;
    gl_Position = P * V * M * vec4(vertex.xy, 0.0, 1.0);
}