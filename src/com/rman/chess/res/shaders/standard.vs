#version 330 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;
layout (location = 2) in vec3 in_normal;

out vec3 out_position;
out vec2 out_texCoord;
out vec3 out_normal;
out vec3 out_eyeDirection;
out vec3 out_lightDirection;

uniform mat4 ProjectionMatrix;
uniform mat4 ViewMatrix;
uniform mat4 ModelMatrix;
uniform vec3 LightPosition;

void main() {
	mat4 MVPMatrix = ProjectionMatrix * ViewMatrix * ModelMatrix;
	
	gl_Position =  MVPMatrix * vec4(in_position, 1);
	
	out_position = (ModelMatrix * vec4(in_position, 1)).xyz;
	out_texCoord = in_texCoord;
	out_normal = (ViewMatrix * ModelMatrix * vec4(in_normal, 0)).xyz;
	
	vec3 vertexPosition_cameraSpace = (ViewMatrix * ModelMatrix * vec4(in_position, 1)).xyz;
	out_eyeDirection = vec3(0, 0, 0) - vertexPosition_cameraSpace;

	vec3 LightPosition_cameraspace = (ViewMatrix * vec4(LightPosition, 1)).xyz;
	out_lightDirection = LightPosition + out_eyeDirection;
}