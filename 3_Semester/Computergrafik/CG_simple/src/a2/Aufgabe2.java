package a2;

import static org.lwjgl.opengl.GL30.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;

public class Aufgabe2 extends AbstractOpenGLBase {

	public static void main(String[] args) {
		new Aufgabe2().start("CG Aufgabe 2", 700, 700);
	}

	@Override
	protected void init() {
		// laedt automatisch "aufgabe2_v.glsl" (vertex) und "aufgabe2_f.glsl" (fragment)
		ShaderProgram shaderProgram = new ShaderProgram("aufgabe2");
		glUseProgram(shaderProgram.getId());

		// ein Dreieck, x/y Koordinaten der 3 Ecken
		float[] coordinates = { -0.5f, 1, -0.5f, 0, 0, 0.5f };

		// eine RGB-Farbe pro Ecke
		float[] colorCorners = { 0, 0, 0, 1, 0.5f, 1, 0.5f, 0.5f, 0.5f };

		// ein VAO fuer dieses Dreieck, beide Attribute (Position + Farbe) landen darin
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		addAttribute(0, coordinates, 2); // Attribut 0 = Position (2 Werte pro Ecke)
		addAttribute(1, colorCorners, 3); // Attribut 1 = Farbe (3 Werte pro Ecke)
	}

	@Override
	public void update() {
	}

	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		glDrawArrays(GL_TRIANGLES, 0, 3);
	}

	// legt ein VBO an und haengt es als Attribut an das aktuell gebundene VAO
	protected void addAttribute(int listIndex, float[] array, int elements) {
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, array, GL_STATIC_DRAW);
		glVertexAttribPointer(listIndex, elements, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(listIndex);
	}
}
