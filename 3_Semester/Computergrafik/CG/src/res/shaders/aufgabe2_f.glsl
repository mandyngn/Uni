#version 330

// Interpolierte Farbe aus dem Vertex-Shader
in vec3 vColor;

// Endgueltige Pixelfarbe
out vec4 FragColor;

void main() {
	FragColor = vec4(vColor, 1.0);
}
