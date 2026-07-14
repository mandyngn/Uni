#version 330

// Position der Ecke aus dem VBO
layout(location=0) in vec3 vertices;
// Matrix aus Java (Projection * Model), transformiert die Position
uniform mat4 uMvpMatrix;

void main() {
    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}
