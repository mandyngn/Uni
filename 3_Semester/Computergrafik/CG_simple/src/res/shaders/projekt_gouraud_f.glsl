#version 330

// bereits fertig berechnete, interpolierte Farbe aus dem Vertex-Shader
in vec3 vColor;
out vec4 FragColor;

void main() {
    FragColor = vec4(vColor, 1.0);
}
