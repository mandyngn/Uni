#version 330

// Interpolierte Farbe aus dem Vertex-Shader (bereits beleuchtet).
in vec3 vColor;
// Finale Ausgabe in den Farbpuffer.
out vec4 FragColor;

void main() {
    // Gouraud: Fragment-Shader gibt die interpolierte Vertexfarbe nur noch aus.
    FragColor = vec4(vColor, 1.0);
}
