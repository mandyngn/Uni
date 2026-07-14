#version 330

in vec3 vWorldPos;
in vec3 vWorldNormal;
in vec2 vUv;

// Zwei Texturen fuer Mehrfachtexturierung.
uniform sampler2D uTexture0;
uniform sampler2D uTexture1;

// Licht- und Materialparameter.
uniform vec3 uLightPos;
uniform float uTextureBlend;

out vec4 FragColor;

void main() {
    // UV fuer die Detailtextur staerker skalieren, damit MIP-Effekte klarer sichtbar werden.
    vec2 uvBase = vUv;
    vec2 uvDetail = vUv * 10.0;

    vec3 tex0 = texture(uTexture0, uvBase).rgb;
    vec3 tex1 = texture(uTexture1, uvDetail).rgb;

    // Beide Texturen mischen (0.0 = nur tex0, 1.0 = nur tex1).
    vec3 albedo = mix(tex0, tex1, clamp(uTextureBlend, 0.0, 1.0));

    // Phong-Beleuchtung mit Ambient + Diffuse + Spekular.
    vec3 normal = normalize(vWorldNormal);
    vec3 lightDir = normalize(uLightPos - vWorldPos);
    vec3 viewDir = normalize(-vWorldPos);

    float ambient = 0.20;
    float diffuse = max(dot(normal, lightDir), 0.0);

    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), 24.0);

    vec3 lit = albedo * (ambient + 0.80 * diffuse) + vec3(0.30) * specular;
    FragColor = vec4(lit, 1.0);
}
