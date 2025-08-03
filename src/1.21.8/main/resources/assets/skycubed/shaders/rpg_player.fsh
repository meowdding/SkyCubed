#version 150

//!moj_import <minecraft:dynamictransforms.glsl>
//!moj_import <minecraft:projection.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 maskUv = vec2(texCoord0.x, 1.0 - texCoord0.y);
    if (texture(Sampler1, maskUv).a != 0.0) {
        discard;
    }

    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}
