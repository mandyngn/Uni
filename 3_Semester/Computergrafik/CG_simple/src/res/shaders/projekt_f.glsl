#version 330

out vec4 FragColor;
// Farbe wird aus Java pro Objekt gesetzt
uniform vec3 uColor;

void main() {
    FragColor = vec4(uColor, 1.0);
}
