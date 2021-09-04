#version 330 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;
layout (location = 2) in vec3 in_normal;

out vec2 out_texCoord;

uniform mat4 ProjectionMatrix;
uniform mat4 ViewMatrix;
uniform mat4 ModelMatrix;

void main() {
	mat4 MVPMatrix = ProjectionMatrix * ViewMatrix * ModelMatrix;
	gl_Position = MVPMatrix * vec4(in_position, 1);
	
	out_texCoord = in_texCoord;
}