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

    vec2 centered = texCoord - 0.5;
    float dist = length(centered) * 1.4;

    // Vignette de "gel" qui s'intensifie vers les bords
    float edgeMask = smoothstep(0.35, 0.95, dist);

    // Flou directionnel simulé par échantillonnage multiple le long du vent
    vec2 windDir = normalize(vec2(0.7, -0.3));
    float blurStrength = intensity * edgeMask * 0.012;

    vec3 blurredColor = vec3(0.0);
    int samples = 6;
    for (int i = 0; i < samples; i++) {
        float t = (float(i) / float(samples - 1)) - 0.5;
        vec2 offset = windDir * t * blurStrength;
        blurredColor += texture(DiffuseSampler0, texCoord + offset).rgb;
    }
    blurredColor /= float(samples);

    vec4 base = texture(DiffuseSampler0, texCoord);
    vec3 color = mix(base.rgb, blurredColor, edgeMask * intensity);

    // Cristaux de glace scintillants en bordure — petits points blancs
    float crystalNoise = noise(texCoord * 80.0 + vec2(AWTime * 2.0, AWTime * 1.5));
    float crystalMask = step(0.985, crystalNoise) * edgeMask * intensity;
    color += vec3(crystalMask * 0.6);

    // Teinte bleu-blanc glaciale, plus forte aux bords
    vec3 frostTint = vec3(0.85, 0.92, 1.0);
    float tintStrength = edgeMask * intensity * 0.22;
    color = mix(color, color * frostTint + frostTint * 0.05, tintStrength);

    // Léger assombrissement périphérique pour renforcer la vignette
    color *= 1.0 - edgeMask * intensity * 0.15;

    fragColor = vec4(color, base.a);
}