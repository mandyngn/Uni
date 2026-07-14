#version 330

// interpolierte Werte pro Fragment (nicht mehr pro Vertex, deshalb praeziser als Gouraud)
in vec3 vWorldPos;
in vec3 vWorldNormal;

uniform vec3 uLightPos;
uniform vec3 uBaseColor;

out vec4 FragColor;

void main() {
    vec3 normal = normalize(vWorldNormal);
    vec3 lightDir = normalize(uLightPos - vWorldPos);

    // diffuser Anteil (Lambert)
    float diffuse = max(dot(normal, lightDir), 0.0);
    float ambient = 0.2;

    // Glanzlicht: je naeher der Reflexionsvektor an der Blickrichtung, desto staerker
    vec3 viewDir = normalize(-vWorldPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), 24.0);

    vec3 color = uBaseColor * (ambient + 0.8 * diffuse) + vec3(0.25) * specular;
    FragColor = vec4(color, 1.0);
}
