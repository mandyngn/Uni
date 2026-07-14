#version 330

// Attribut 0: Position des Vertex im Modellraum.
layout(location = 0) in vec3 vertices;
// Attribut 1: Normale des Vertex im Modellraum.
layout(location = 1) in vec3 normals;
// Attribut 2: UV-Koordinate pro Ecke fuer Texturzugriff.
layout(location = 2) in vec2 uv;

// Klassische Transformationsuniforms.
uniform mat4 uMvpMatrix;
uniform mat4 uModelMatrix;
uniform mat3 uNormalMatrix;

// Daten fuer den Fragment-Shader.
out vec3 vWorldPos;
out vec3 vWorldNormal;
out vec2 vUv;

void main() {
    // Weltposition zur spaeteren Lichtberechnung.
    vec4 worldPos4 = uModelMatrix * vec4(vertices, 1.0);
    vWorldPos = worldPos4.xyz;

    // Normale korrekt mit der inverse-transponierten Model-Matrix transformieren.
    vWorldNormal = normalize(uNormalMatrix * normals);

    // UV ohne Aenderung weitergeben.
    vUv = uv;

    // Endposition im Clip-Space.
    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}
