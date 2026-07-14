#version 330

// Attribut 0: Position aus dem VBO
layout(location=0) in vec2 inPosition;
// Attribut 1: Farbe pro Ecke
layout(location=1) in vec3 inColor;

// Farbe zum Fragment-Shader weiterreichen
out vec3 vColor;

void main() {
	float winkel = -0.2;
	mat2 rotation = mat2(cos(winkel), sin(winkel), -sin(winkel), cos(winkel));

	vec2 rotPos = inPosition * rotation;
	gl_Position = vec4(rotPos, 0.0, 1.0);
	vColor = inColor;
}
