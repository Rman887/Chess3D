#version 330 core

in vec3 out_position;
in vec2 out_texCoord;
in vec3 out_normal;
in vec3 out_eyeDirection;
in vec3 out_lightDirection;

out vec3 fragColor;

uniform sampler2D TextureSampler;
uniform vec3 LightPosition;
uniform vec3 LightColor;
uniform float LightPower;
uniform vec3 AmbientLightColor;
uniform vec3 SpecularLightColor;

void main() {
	vec3 MaterialDiffuseColor = texture2D(TextureSampler, out_texCoord).rgb;
	vec3 MaterialAmbientColor = AmbientLightColor * MaterialDiffuseColor;
	vec3 MaterialSpecularColor = SpecularLightColor;

	float distance = length(LightPosition - out_position);

	vec3 n = normalize(out_normal);
	vec3 l = normalize(out_lightDirection);
	float cosTheta = clamp(dot(n, l), 0, 1);
	
	vec3 Eye = normalize(out_eyeDirection);
	vec3 R = reflect(-l, n);
	float cosAlpha = clamp(dot(Eye, R), 0, 1);
	
	fragColor = 
		MaterialAmbientColor +
		MaterialDiffuseColor * LightColor * LightPower * cosTheta / (distance * distance) +
		MaterialSpecularColor * LightColor * LightPower * pow(cosAlpha, 5) / (distance * distance);
}