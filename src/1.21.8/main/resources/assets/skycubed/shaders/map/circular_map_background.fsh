#version 150

uniform sampler2D Sampler0;

//!moj_import <minecraft:dynamictransforms.glsl>
//!moj_import <minecraft:projection.glsl>

layout (std140) uniform SkyCubedCircularMinimapUniform {
    vec2 Resolution;
    vec2 Offset;
    vec2 Center;
    vec2 Screen;
    float Radius;
};

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 pos = Offset + Resolution * texCoord0;
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;;
    float distance = length(Center - pos) - Radius;

    if (distance >= 1 || color.a <= 0.1) {
        discard;
    }

    fragColor = color;
}
