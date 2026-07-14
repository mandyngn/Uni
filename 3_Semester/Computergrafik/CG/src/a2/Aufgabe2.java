package a2;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;

public class Aufgabe2 extends AbstractOpenGLBase {

	public static void main(String[] args) {
		new Aufgabe2().start("CG Aufgabe 2", 700, 700);
	}

	@Override
	protected void init() {
		// folgende Zeile laed automatisch "aufgabe2_v.glsl" (vertex) und "aufgabe2_f.glsl" (fragment)
		ShaderProgram shaderProgram = new ShaderProgram("aufgabe2");
		glUseProgram(shaderProgram.getId());

		// Koordinaten, VAO, VBO, ... hier anlegen und im Grafikspeicher ablegen
		float coordinates[] = {-0.5f, 1, -0.5f, 0, 0, 0.5f};

		//RGB Werte
		float colorCorners[] = {0, 0, 0, 1, 0.5f, 1, 0.5f, 0.5f, 0.5f};

		createVAOVBO(coordinates, 0, 2);

		/*int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		//coordinates
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, coordinates, GL_STATIC_DRAW);
		//how many elements are taken from the array -> 2
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);*/

		createVBO(colorCorners, 1, 3);
		/*//color
		int vboIdColors = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboIdColors);
		glBufferData(GL_ARRAY_BUFFER, colorCorners, GL_STATIC_DRAW);
		//list index number -> 1
		//how many elements are taken from the array -> 2
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(1);*/
	}

	@Override
	public void update() {
	}

	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT); // Zeichenflaeche leeren

		// hier vorher erzeugte VAOs zeichnen
		//zeichnet Dreiecke, beginnt bei Ecke 0
		//und verarbeitet gegebene Anzahl Ecken
		glDrawArrays(GL_TRIANGLES, 0, 3);
	}

	protected void createVAOVBO(float array[], int listIndex, int elements) {
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, array, GL_STATIC_DRAW);
		glVertexAttribPointer(listIndex, elements, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(listIndex);
	}

	protected void createVBO(float array[], int listIndex, int elements){
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, array, GL_STATIC_DRAW);
		glVertexAttribPointer(listIndex, elements, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(listIndex);
	}
}
