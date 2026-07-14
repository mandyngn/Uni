package projekt;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;

public class Projekt extends AbstractOpenGLBase {

	private ShaderProgram shaderProgram;

	// je Objekt: VAO-Id und wie viele Ecken gezeichnet werden muessen
	private int triangleVaoId;
	private int triangleVertexCount;
	private int cubeVaoId;
	private int cubeVertexCount;
	private int tetraVaoId;
	private int tetraVertexCount;

	// Uniform-Locations, damit wir sie nicht jeden Frame neu suchen muessen
	private int mvpUniformLocation;
	private int colorUniformLocation;

	// angle steuert die Rotation, time die sinusfoermige Bewegung
	private float angle;
	private float time;

	public static void main(String[] args) {
		new Projekt().start("CG Projekt", 700, 700);
	}

	@Override
	protected void init() {
		shaderProgram = new ShaderProgram("projekt");
		glUseProgram(shaderProgram.getId());

		float s = 0.5f;

		// Dreieck lokal um den Ursprung
		float[] triangleCoordinates = {
				-0.5f, -0.5f, 0.5f,
				 0.5f, -0.5f, -0.5f,
				 0.0f,  0.5f, 0.0f
		};

		// Wuerfel: 6 Seiten, je 2 Dreiecke
		float[] cubeCoordinates = {
				-s, -s,  s,   s, -s,  s,   s,  s,  s,
				-s, -s,  s,   s,  s,  s,  -s,  s,  s,
				 s, -s, -s,  -s, -s, -s,  -s,  s, -s,
				 s, -s, -s,  -s,  s, -s,   s,  s, -s,
				-s, -s, -s,  -s, -s,  s,  -s,  s,  s,
				-s, -s, -s,  -s,  s,  s,  -s,  s, -s,
				 s, -s,  s,   s, -s, -s,   s,  s, -s,
				 s, -s,  s,   s,  s, -s,   s,  s,  s,
				-s,  s,  s,   s,  s,  s,   s,  s, -s,
				-s,  s,  s,   s,  s, -s,  -s,  s, -s,
				-s, -s, -s,   s, -s, -s,   s, -s,  s,
				-s, -s, -s,   s, -s,  s,  -s, -s,  s
		};

		// Tetraeder: 4 Dreiecke
		float[] tetraCoordinates = {
				 0.0f,  0.70f,  0.0f,  -0.60f, -0.35f,  0.60f,   0.60f, -0.35f,  0.60f,
				 0.0f,  0.70f,  0.0f,   0.60f, -0.35f,  0.60f,   0.00f, -0.35f, -0.70f,
				 0.0f,  0.70f,  0.0f,   0.00f, -0.35f, -0.70f,  -0.60f, -0.35f,  0.60f,
				-0.60f, -0.35f,  0.60f,  0.00f, -0.35f, -0.70f,  0.60f, -0.35f,  0.60f
		};

		triangleVaoId = createMesh(triangleCoordinates);
		triangleVertexCount = triangleCoordinates.length / 3;

		cubeVaoId = createMesh(cubeCoordinates);
		cubeVertexCount = cubeCoordinates.length / 3;

		tetraVaoId = createMesh(tetraCoordinates);
		tetraVertexCount = tetraCoordinates.length / 3;

		mvpUniformLocation = glGetUniformLocation(shaderProgram.getId(), "uMvpMatrix");
		colorUniformLocation = glGetUniformLocation(shaderProgram.getId(), "uColor");

		glEnable(GL_DEPTH_TEST); // z-Buffer, sonst ueberdecken sich die Objekte falsch
		glEnable(GL_CULL_FACE); // Rueckseiten nicht zeichnen
		glClearColor(0.06f, 0.08f, 0.12f, 1.0f);
	}

	// erstellt ein VAO mit einem VBO fuer die Positionen (Attribut 0)
	private int createMesh(float[] coordinates) {
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, coordinates, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);

		return vaoId;
	}

	@Override
	public void update() {
		// jeden Frame ein bisschen weiterdrehen bzw. weiterschwingen
		angle += 0.015f;
		time += 0.02f;
	}

	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// eine gemeinsame Projektionsmatrix fuer alle Objekte
		Matrix4 projection = new Matrix4(0.1f, 12.0f, (float) Math.toRadians(60.0f), 1.0f);

		// Dreieck: rotiert und nach oben/vorne verschoben
		Matrix4 triangleModel = new Matrix4().rotateZ(angle * 1.4f).translate(0.0f, 1.1f, -3.3f);
		drawObject(triangleVaoId, triangleVertexCount, triangleModel, projection, 0.90f, 0.25f, 0.45f);

		// Wuerfel: links in der Szene, dreht sich um zwei Achsen
		Matrix4 cubeModel = new Matrix4().rotateY(angle).rotateX(angle * 0.6f).translate(-1.3f, 0.0f, -5.0f);
		drawObject(cubeVaoId, cubeVertexCount, cubeModel, projection, 0.20f, 0.75f, 0.95f);

		// Tetraeder: rechts, mit leichter Schwingung nach oben/unten (sin von time)
		Matrix4 tetraModel = new Matrix4()
				.rotateZ(-angle * 0.8f)
				.rotateY(angle * 1.2f)
				.translate(1.4f, (float) Math.sin(time) * 0.35f, -4.2f);
		drawObject(tetraVaoId, tetraVertexCount, tetraModel, projection, 1.00f, 0.65f, 0.20f);
	}

	// baut MVP = Projection * Model, setzt Farbe + Matrix als Uniform und zeichnet das Objekt
	private void drawObject(int vaoId, int vertexCount, Matrix4 model, Matrix4 projection, float r, float g, float b) {
		Matrix4 mvp = new Matrix4(model).multiply(projection);
		glUniformMatrix4fv(mvpUniformLocation, false, mvp.getValuesAsArray());
		glUniform3f(colorUniformLocation, r, g, b);

		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}
}
