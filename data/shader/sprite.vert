#version 330 core
layout (location = 0) in vec4 vertex; // <vec2 position, vec2 texCoords>
layout (location = 1) in vec4 color;
layout (location = 2) in mat4 M;

out vec2 TexCoords;
out vec4 baseColor;

uniform mat4 P;
uniform mat4 V;

void main()
{
    TexCoords = vertex.zw;
    baseColor = color;
    gl_Position = P * V * M * vec4(vertex.xy, 0.0, 1.0);
}