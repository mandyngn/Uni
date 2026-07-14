#version 330

// Vertex-Attribute fuer Position und Normale
layout(location = 0) in vec3 vertices;
layout(location = 1) in vec3 normals;

uniform mat4 uMvpMatrix;
// Model-Matrix fuer Weltkoordinatenberechnung der Position.
uniform mat4 uModelMatrix;
// Normal-Matrix = inverse(transpose(model3x3)) fuer korrekte Normalentransformation.
uniform mat3 uNormalMatrix;
// Punktlichtposition im selben Raum wie worldPos/worldNormal.
uniform vec3 uLightPos;
// Grundfarbe des Objekts (Materialfarbe ohne Beleuchtung).
uniform vec3 uBaseColor;

out vec3 vColor;

void main() {
    // Position des Vertex in Weltkoordinaten.
    vec3 worldPos = (uModelMatrix * vec4(vertices, 1.0)).xyz;

    // Normalen werden mit der inverse-transponierten Model-Matrix transformiert.
    vec3 worldNormal = normalize(uNormalMatrix * normals);
    // Richtung vom Punkt zur Lichtquelle.
    vec3 lightDir = normalize(uLightPos - worldPos);

    // Lambert-Anteil (diffus) mit Clamp auf [0, 1].
    float diffuse = max(dot(worldNormal, lightDir), 0.0);
    // Konstanter Umgebungsanteil.
    float ambient = 0.2;

    // Gouraud: Beleuchtung wird hier pro Vertex berechnet und spaeter interpoliert.
    vColor = uBaseColor * (ambient + 0.8 * diffuse);

    // Endposition im Clip-Space fuer Rasterizer.
    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}
