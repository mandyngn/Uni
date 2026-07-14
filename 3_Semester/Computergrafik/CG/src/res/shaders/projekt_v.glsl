#version 330

// Vertex-Attribute fuer die Positionen der Geometrie
layout(location=0) in vec3 vertices;
// Matrix aus Java wird hier auf die Vertex-Position angewendet
uniform mat4 uMvpMatrix;

void main() {
    // Die Transformationsmatrix wird auf die Position angewendet
    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}