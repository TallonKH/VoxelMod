#version 330

in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform sampler2D emissive_sampler;
uniform float brightness;

void main()
{
    vec4 base = texture(texture_sampler, outTexCoord);
    vec4 emissive = texture(emissive_sampler, outTexCoord);
    fragColor = base * min(vec4(1), brightness + emissive);
}