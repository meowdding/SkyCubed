#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;

uniform vec2 CirclePosition;
uniform float Scale;
uniform float Radius;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;

    vec2 minimapPosition = CirclePosition * vec2(scale);

    vec2 fragmentPosition = vec2(gl_FragCoord.x, ScreenSize.y - gl_FragCoord.y);

    vec2 relativePosition = fragmentPosition - minimapPosition + 2.0;

    float pointDistance = length(relativePosition.xy);

    if (pointDistance > Radius * Scale || color.a == 0.0) {
        discard;
    }

    fragColor = color * ColorModulator;
}
