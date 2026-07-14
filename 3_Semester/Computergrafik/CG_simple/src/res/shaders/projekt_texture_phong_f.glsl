#version 330

in vec3 vWorldPos;
in vec3 vWorldNormal;
in vec2 vUv;

// zwei Texturen (grosse + winzige), werden gemischt
uniform sampler2D uTexture0;
uniform sampler2D uTexture1;
uniform vec3 uLightPos;
uniform float uTextureBlend;

out vec4 FragColor;

void main() {
    // die zweite Textur wird mit vergroesserten UVs gesampelt, damit man den Unterschied sieht
    vec3 tex0 = texture(uTexture0, vUv).rgb;
    vec3 tex1 = texture(uTexture1, vUv * 10.0).rgb;
    vec3 albedo = mix(tex0, tex1, uTextureBlend);

    // Phong-Beleuchtung wie in projekt_phong_f.glsl, nur mit Textur als Grundfarbe
    vec3 normal = normalize(vWorldNormal);
    vec3 lightDir = normalize(uLightPos - vWorldPos);
    vec3 viewDir = normalize(-vWorldPos);

    float diffuse = max(dot(normal, lightDir), 0.0);
    float ambient = 0.20;

    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), 24.0);

    vec3 lit = albedo * (ambient + 0.80 * diffuse) + vec3(0.30) * specular;
    FragColor = vec4(lit, 1.0);
}
