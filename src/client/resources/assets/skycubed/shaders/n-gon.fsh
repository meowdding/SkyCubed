#version 150


const float TAU = 6.28318530718; // 2 * PI

uniform sampler2D Sampler0;
uniform int Sides;
uniform vec2 WindowWidthHeight;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

// By https://www.shadertoy.com/view/MtKcWW

float sdNGon(vec2 p, float r) {
    // these 2 lines can be precomputed
    float an = TAU / float(Sides);
    float he = r * tan(0.5 * an);

    // rotate to first sector
    p = -p.yx; // if you want the corner to be up
    float bn = an * floor((atan(p.y, p.x) + 0.5 * an) / an);
    vec2 cs = vec2(cos(bn), sin(bn));
    p = mat2(cs.x, -cs.y, cs.y, cs.x) * p;

    // side of polygon
    return length(p - vec2(r, clamp(p.y, -he, he))) * sign(p.x - r);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    vec2 p = (2.0 * texCoord0 - WindowWidthHeight.xy) / WindowWidthHeight.y;
    p *= 2.0;

    float d = sdNGon(p, 1.0);

    if (d > 0.0f) discard;

    fragColor = color * vertexColor;
}
