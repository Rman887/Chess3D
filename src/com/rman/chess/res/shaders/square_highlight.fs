#version 330 core

in vec2 out_texCoord;

out vec3 color;

uniform sampler2D TextureSampler;

void main() {
	color = texture2D(TextureSampler, out_texCoord).rgb;
}