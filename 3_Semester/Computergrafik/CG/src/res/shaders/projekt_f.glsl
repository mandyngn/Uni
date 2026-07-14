#version 330

// Ausgabe-Farbe des Fragments
out vec4 FragColor;
uniform vec3 uColor;

void main() {
    // Farbe wird aus Java pro Objekt gesetzt.
    FragColor = vec4(uColor, 1.0);
}