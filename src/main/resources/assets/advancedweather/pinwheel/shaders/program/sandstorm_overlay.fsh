uniform sampler2D DiffuseSampler0;

uniform float AWTime;
uniform float Intensity;

in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(
    mix(hash(i + vec2(0.0, 0.0)), hash(i + vec2(1.0, 0.0)), u.x),
    mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x),
    u.y
    );
}

void main() {
    float intensity = clamp(Intensity, 0.0, 1.0);
    vec4 base = texture(DiffuseSampler0, texCoord);

    if (intensity < 0.01) {
        fragColor = base;
        return;
    }

    float layer1 = noise(texCoord * vec2(8.0, 3.0) + vec2(AWTime * 0.6, 0.0));
    float layer2 = noise(texCoord * vec2(14.0, 5.0) + vec2(AWTime * 1.1, AWTime * 0.1));
    float layer3 = noise(texCoord * vec2(25.0, 8.0) - vec2(AWTime * 1.8, 0.0));

    float grain = (layer1 * 0.5 + layer2 * 0.35 + layer3 * 0.15);

    vec3 sandColor = vec3(0.78, 0.62, 0.38);

    float bottomBias = pow(1.0 - texCoord.y, 0.6);
    float veilStrength = intensity * (0.35 + bottomBias * 0.4);

    vec3 color = mix(base.rgb, sandColor, veilStrength);

    float grainStrength = intensity * 0.25 * (0.5 + bottomBias * 0.5);
    color = mix(color, color * (0.7 + grain * 0.6), grainStrength);

    float gray = dot(color, vec3(0.333));
    color = mix(color, vec3(gray), intensity * 0.15);

    fragColor = vec4(color, base.a);
}