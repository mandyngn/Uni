#version 330

// Vertex-Attribute fuer Position und Normale
layout(location = 0) in vec3 vertices;
layout(location = 1) in vec3 normals;

uniform mat4 uMvpMatrix;
// Model-Matrix fuer Weltkoordinatenposition.
uniform mat4 uModelMatrix;
// Normal-Matrix = inverse(transpose(model3x3)).
uniform mat3 uNormalMatrix;

// Weltposition des Vertex fuer Fragment-Beleuchtung.
out vec3 vWorldPos;
// Transformierte Normale fuer Fragment-Beleuchtung.
out vec3 vWorldNormal;

void main() {
    // Weltposition vorberechnen und weiterreichen.
    vec4 worldPos4 = uModelMatrix * vec4(vertices, 1.0);
    vWorldPos = worldPos4.xyz;

    // Die transformierte Normale wird fuer die Beleuchtung im Fragment Shader weitergegeben.
    vWorldNormal = normalize(uNormalMatrix * normals);

    // Endposition im Clip-Space.
    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}
