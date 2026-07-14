#version 330

layout(location = 0) in vec3 vertices;
layout(location = 1) in vec3 normals;

uniform mat4 uMvpMatrix;
// Model-Matrix, um die Weltposition der Ecke zu berechnen (fuer die Lichtrichtung)
uniform mat4 uModelMatrix;
// transformiert die Normale mit ins Weltkoordinatensystem
uniform mat3 uNormalMatrix;
uniform vec3 uLightPos;
uniform vec3 uBaseColor;

// Ergebnisfarbe, wird zum Fragment-Shader interpoliert weitergereicht
out vec3 vColor;

void main() {
    vec3 worldPos = (uModelMatrix * vec4(vertices, 1.0)).xyz;
    vec3 worldNormal = normalize(uNormalMatrix * normals);
    vec3 lightDir = normalize(uLightPos - worldPos);

    // Lambert-Beleuchtung: je direkter das Licht auf die Flaeche trifft, desto heller
    float diffuse = max(dot(worldNormal, lightDir), 0.0);
    float ambient = 0.2;

    // Gouraud: Licht wird hier pro Vertex berechnet, der Fragment-Shader interpoliert nur noch
    vColor = uBaseColor * (ambient + 0.8 * diffuse);

    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}
