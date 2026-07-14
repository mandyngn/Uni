#version 330

layout(location = 0) in vec3 vertices;
layout(location = 1) in vec3 normals;
// UV-Koordinate fuer den Texturzugriff
layout(location = 2) in vec2 uv;

uniform mat4 uMvpMatrix;
uniform mat4 uModelMatrix;
uniform mat3 uNormalMatrix;

out vec3 vWorldPos;
out vec3 vWorldNormal;
out vec2 vUv;

void main() {
    vWorldPos = (uModelMatrix * vec4(vertices, 1.0)).xyz;
    vWorldNormal = normalize(uNormalMatrix * normals);
    vUv = uv;

    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}
