#include veil:space_helper
#include veil:fog

out vec4 fragColor;
in vec2 texCoord;

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform vec2 AnchorPosition;
uniform vec2 PlayerPosition;
uniform int ShouldRender;

void main() {
    vec4 original = texture(DiffuseSampler, texCoord);

    if (ShouldRender > 0) {
        float depth = texture(DiffuseDepthSampler, texCoord).r;

        vec2 worldPosition = screenToWorldSpace(texCoord, depth).xz;
        float blockDistance = distance(worldPosition, AnchorPosition);
        float playerDistance = distance(PlayerPosition, AnchorPosition);
        float fogDistance = fog_distance(screenToLocalSpace(texCoord, depth).xyz, 0);

        float delta = pow(min(1, max(0, (blockDistance) / 25)), 1);
        vec3 greyscaled = vec3((original.r + original.g + original.b) / 3);
        vec4 color = mix(original, vec4(greyscaled, 1.0f), max(0, min(1, delta)-0.3));
        vec4 fogColor = vec4(0.3, 0.3, 0.3, max(0, delta-0.05));

        fragColor = linear_fog(color, fogDistance, 10-playerDistance, 75-playerDistance, fogColor);
    } else fragColor = original;
}

float distance(vec2 pos, vec2 other) {
    float dx = pos.x - other.x;
    float dy = pos.y - other.y;
    return sqrt(dx * dx + dy * dy);
}