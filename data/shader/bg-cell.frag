#version 330 core

#define TWO_PI 6.28318530718

in vec2 TexCoords;
in vec4 baseColor;

out vec4 color;

uniform sampler2D texture0;
uniform float time;

vec2 random2( vec2 p ) {
    return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453);
}

void main() {
    vec2 screen = vec2(800*2,600*2);
    vec2 st = gl_FragCoord.xy/screen.xy;
    st.x *= screen.x/screen.y;
    vec3 _color = vec3(0.1);

    // Scale
    st *= 6.;

    // Tile the space
    vec2 i_st = floor(st);
    vec2 f_st = fract(st);

    float m_dist = 1.;  // minimum distance

    for (int y= -1; y <= 1; y++) {
        for (int x= -1; x <= 1; x++) {
            // Neighbor place in the grid
            vec2 neighbor = vec2(float(x),float(y));

            // Random position from current + neighbor place in the grid
            vec2 point = random2(i_st + neighbor);

            // Animate the point
            point = 0.5 + 0.5*sin(time + 6.2831*point);

            // Vector between the pixel and the point
            vec2 diff = neighbor + point - f_st;

            // Distance to the point
            float dist = length(diff);

            // Keep the closer distance
            m_dist = min(m_dist, dist);
        }
    }

    // Draw the min distance (distance field)
    _color += m_dist;
    // Draw cell center
    _color += 1.-step(.01, m_dist);
    // Draw grid
//    _color.r += step(.98, f_st.x) + step(.98, f_st.y);

    color = vec4(_color,1.)*texture(texture0,TexCoords);
}