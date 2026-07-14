#version 330

out vec3 pixelFarbe;

//Rectangle
/*
void main() {
    vec2 p = gl_FragCoord.xy;

    if(p.x >= 100.0 && p.x <= 500.0 &&
       p.y >= 100.0 && p.y <= 500.0) {
        pixelFarbe = vec3(1.0, 1.0, 1.0);
    } else {
        pixelFarbe = vec3(0.0, 0.0, 0.0);
    }
}*/

//Circle
bool istImKreis(in vec2 p) { 
    vec2 middle = gl_FragCoord.xy;
    p = gl_FragCoord.xy;

    middle.x = 200.0;
    middle.y = 200.0;

    if(distance(middle, p) <= 100){
        return true;
    } else {
        return false;
    }
}

/*
void main() {
    vec2 p = gl_FragCoord.xy;

    if(istImKreis(p)){
        pixelFarbe = vec3(1.0,1.0,1.0);
    } else {
        pixelFarbe = vec3(0.0,0.0,0.0);
    }
}
*/

//Both in one
/*
void main() {
    vec2 p = gl_FragCoord.xy;

    if(istImKreis(p)){
        pixelFarbe = vec3(1.0,1.0,1.0);
    } else {
        pixelFarbe = vec3(0.0,0.0,0.0);
    }

    if(p.x >= 400.0 && p.x <= 500.0 &&
       p.y >= 400.0 && p.y <= 500.0) {
        pixelFarbe = vec3(1.0, 1.0, 1.0);
    }
}*/

//Winkel
/*void main(){
    vec2 p = gl_FragCoord.xy;

    float winkel = -0.2;
    mat2 rotation = mat2(cos(winkel), sin(winkel), -sin(winkel), cos(winkel));

    vec2 rotPos = p * rotation;

    if(rotPos.x >= 400.0 && rotPos.x <= 500.0 &&
        rotPos.y >= 400.0 && rotPos.y <= 500.0) {
        pixelFarbe = vec3(1.0, 1.0, 1.0);
    } else {
        pixelFarbe = vec3(0.1, 0.1, 0.3);
    }

    if(p.x >= 100.0 && p.x <= 500.0 &&
       p.y >= 100.0 && p.y <= 500.0) {
        pixelFarbe = vec3(1.0, 1.0, 1.0);
    }
}*/

//Line
void main() {
    vec2 p = gl_FragCoord.xy;

    if(p.x >= 100.0 && p.x <= 500.0 &&
       p.y >= 100.0 && p.y <= 100.5) {
        pixelFarbe = vec3(1.0, 1.0, 1.0);
    } else {
        pixelFarbe = vec3(0.0,0.0,0.0);
    }
}