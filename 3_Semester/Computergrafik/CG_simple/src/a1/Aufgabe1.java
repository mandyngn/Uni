package a1;

import static org.lwjgl.opengl.GL30.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;

public class Aufgabe1 extends AbstractOpenGLBase {

	public static void main(String[] args) {
		new Aufgabe1().start("CG Aufgabe 1", 700, 700);
	}

	@Override
	protected void init() {
		// einfacher Vertex-Shader direkt als String, der eigentliche Fragment-Shader kommt aus aufgabe1.glsl
		String vertexShaderSource = "#version 330\nlayout(location=0) in vec2 v;void main(){gl_Position=vec4(v,0.0,1.0);}";
		ShaderProgram shaderProgram = new ShaderProgram(vertexShaderSource, "aufgabe1.glsl");
		glUseProgram(shaderProgram.getId());

		// zwei Dreiecke, die zusammen den ganzen Bildschirm abdecken (-1..1)
		float[] coordinates = { -1, -1, 1, -1, -1, 1, 1, -1, 1, 1, -1, 1 };

		// VAO anlegen und binden
		glBindVertexArray(glGenVertexArrays());

		// Koordinaten in ein VBO packen und als Attribut 0 verfuegbar machen
		glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, coordinates, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
	}

	@Override
	public void update() {
	}

	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		// zeichnet die 2 Dreiecke (= 6 Ecken), der Rest passiert im Fragment-Shader
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}
}
