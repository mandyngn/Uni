#version 330

layout(location = 0) in vec3 vertices;
layout(location = 1) in vec3 normals;

uniform mat4 uMvpMatrix;
uniform mat4 uModelMatrix;
uniform mat3 uNormalMatrix;

// Phong: hier wird nur weitergereicht, die eigentliche Beleuchtung passiert im Fragment-Shader
out vec3 vWorldPos;
out vec3 vWorldNormal;

void main() {
    vWorldPos = (uModelMatrix * vec4(vertices, 1.0)).xyz;
    vWorldNormal = normalize(uNormalMatrix * normals);

    gl_Position = uMvpMatrix * vec4(vertices, 1.0);
}
