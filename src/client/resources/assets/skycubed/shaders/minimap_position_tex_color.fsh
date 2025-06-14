#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;

uniform vec2 circlePosition;
uniform float scale;
uniform float radius;

in vec2 texCoord0;
in vec4 vertexColor;
in vec4 position;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;

    vec2 minimapPosition = circlePosition * vec2(scale);

    vec2 fragmentPosition = (0.5 + vec2(0.5, -0.5) * position.xy) * ScreenSize;

    vec2 relativePosition = fragmentPosition - minimapPosition + 2.0;

    float pointDistance = length(relativePosition.xy);

    if (
        pointDistance > radius * scale - 5
    ) {
        if (pointDistance > radius * scale) discard;
        float factor = 1 - (pointDistance - (radius * scale - 5)) / 5;
        color.a *= factor * factor;
    }
    fragColor = color * ColorModulator;
}
