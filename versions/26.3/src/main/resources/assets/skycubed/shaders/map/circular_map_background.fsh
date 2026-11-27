#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D Sampler0;

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>

layout (std140) uniform SkyCubedCircularMinimapUniform {
    vec2 Resolution;
    vec2 Offset;
    vec2 Center;
    vec2 Screen;
    float Radius;
};

layout(location = 0) in vec2 texCoord0;
layout(location = 1) in vec4 vertexColor;

layout(location = 0) out vec4 fragColor;

void main() {
    vec2 pos = Offset + Resolution * texCoord0;
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    float distance = length(Center - pos) - Radius;

    if (distance >= 1.0 || color.a <= 0.1) {
        discard;
    }

    fragColor = color;
}
