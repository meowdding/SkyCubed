#version 150

uniform sampler2D Sampler0;

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
    float LineWidth;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};
layout (std140) uniform PolyInventoryUniform {
    vec2 Resolution;
    float Radius;
};

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    vec2 center = Resolution * Radius + ModelOffset.xy;
    float distance = length(center - gl_FragCoord.xy) - (Radius * max(Resolution.x, Resolution.y));

    if (distance >= 1.0 || color.a == 0.0) {
        discard;
    }

    fragColor = color * ColorModulator;
}
