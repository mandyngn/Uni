#version 330

out vec3 pixelFarbe;

// prueft ob der Punkt p im Kreis um "mitte" mit "radius" liegt
bool istImKreis(vec2 p, vec2 mitte, float radius) {
    return distance(p, mitte) <= radius;
}

void main() {
    vec2 p = gl_FragCoord.xy;

    // Hintergrund
    pixelFarbe = vec3(0.1, 0.1, 0.3);

    // Rechteck
    if (p.x >= 50.0 && p.x <= 250.0 && p.y >= 50.0 && p.y <= 250.0) {
        pixelFarbe = vec3(1.0, 0.0, 0.0);
    }

    // Kreis
    if (istImKreis(p, vec2(500.0, 500.0), 100.0)) {
        pixelFarbe = vec3(0.0, 1.0, 0.0);
    }
}
