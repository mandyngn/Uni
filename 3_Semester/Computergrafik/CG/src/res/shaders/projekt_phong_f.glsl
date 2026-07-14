#version 330

in vec3 vWorldPos;
in vec3 vWorldNormal;

// Punktlichtposition in Weltkoordinaten.
uniform vec3 uLightPos;
// Grundfarbe des Objekts.
uniform vec3 uBaseColor;

// Finale Fragmentfarbe.
out vec4 FragColor;

void main() {
    // Normale und Lichtvektor fuer Beleuchtungsmodell normieren.
    vec3 normal = normalize(vWorldNormal);
    vec3 lightDir = normalize(uLightPos - vWorldPos);

    // Diffuse Lambert-Beleuchtung.
    float diffuse = max(dot(normal, lightDir), 0.0);
    // Konstanter Ambient-Anteil.
    float ambient = 0.2;

    // Einfache Kamera im Ursprung des View-Space angenaehert fuer sichtbaren Spekularanteil.
    // Blinn/Phong-aehnlicher Spekularterm ueber Reflexionsvektor.
    vec3 viewDir = normalize(-vWorldPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), 24.0);

    // Endfarbe aus Ambient + Diffuse + Spekular.
    vec3 color = uBaseColor * (ambient + 0.8 * diffuse) + vec3(0.25) * specular;
    FragColor = vec4(color, 1.0);
}
