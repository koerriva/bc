#version 330 core
layout (location = 0) in vec2 vertex;
layout (location = 1) in vec2 texCoords;
layout (location = 2) in vec4 instanceColor;
layout (location = 3) in mat4 instanceTransform;

out vec2 TexCoords;
out vec4 baseColor;

uniform int isInstance;

uniform mat4 P;
uniform mat4 V;

uniform mat4 M;
uniform vec4 color;

void main()
{
    TexCoords = texCoords;
    baseColor = color;

    mat4 model = M;

    if(isInstance==1){
        baseColor = instanceColor;
        model = instanceTransform;
    }

    gl_Position = P * V * model * vec4(vertex, 0.0, 1.0);
}