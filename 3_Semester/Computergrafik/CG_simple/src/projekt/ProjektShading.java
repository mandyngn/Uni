package projekt;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;

public class ProjektShading extends AbstractOpenGLBase {

	// zwei getrennte Shaderprogramme: einmal Gouraud (Licht pro Vertex), einmal Phong (Licht pro Fragment)
	private ShaderProgram gouraudProgram;
	private ShaderProgram phongProgram;

	private int triangleVaoId;
	private int triangleVertexCount;
	private int cubeVaoId;
	private int cubeVertexCount;
	private int tetraVaoId;
	private int tetraVertexCount;

	// Uniform-Locations fuer beide Programme getrennt, da es zwei verschiedene Shader sind
	private int gouraudMvpUniformLocation;
	private int gouraudModelUniformLocation;
	private int gouraudNormalUniformLocation;
	private int gouraudLightUniformLocation;
	private int gouraudBaseColorUniformLocation;

	private int phongMvpUniformLocation;
	private int phongModelUniformLocation;
	private int phongNormalUniformLocation;
	private int phongLightUniformLocation;
	private int phongBaseColorUniformLocation;

	private float angle;
	private float time;

	public static void main(String[] args) {
		new ProjektShading().start("CG Projekt Shading", 700, 700);
	}

	@Override
	protected void init() {
		gouraudProgram = new ShaderProgram("projekt_gouraud");
		phongProgram = new ShaderProgram("projekt_phong");
		glUseProgram(gouraudProgram.getId());

		float s = 0.5f;

		float[] triangleCoordinates = {
				-0.5f, -0.5f, 0.5f,
				 0.5f, -0.5f, -0.5f,
				 0.0f,  0.5f, 0.0f
		};

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

		float[] tetraCoordinates = {
				 0.0f,  0.70f,  0.0f,  -0.60f, -0.35f,  0.60f,   0.60f, -0.35f,  0.60f,
				 0.0f,  0.70f,  0.0f,   0.60f, -0.35f,  0.60f,   0.00f, -0.35f, -0.70f,
				 0.0f,  0.70f,  0.0f,   0.00f, -0.35f, -0.70f,  -0.60f, -0.35f,  0.60f,
				-0.60f, -0.35f,  0.60f,  0.00f, -0.35f, -0.70f,  0.60f, -0.35f,  0.60f
		};

		// pro Dreieck eine Normale (Kreuzprodukt zweier Kanten), noetig fuer die Beleuchtung
		float[] triangleNormals = calculateFlatNormals(triangleCoordinates);
		float[] cubeNormals = calculateFlatNormals(cubeCoordinates);
		float[] tetraNormals = calculateFlatNormals(tetraCoordinates);

		triangleVaoId = createMesh(triangleCoordinates, triangleNormals);
		triangleVertexCount = triangleCoordinates.length / 3;

		cubeVaoId = createMesh(cubeCoordinates, cubeNormals);
		cubeVertexCount = cubeCoordinates.length / 3;

		tetraVaoId = createMesh(tetraCoordinates, tetraNormals);
		tetraVertexCount = tetraCoordinates.length / 3;

		gouraudMvpUniformLocation = glGetUniformLocation(gouraudProgram.getId(), "uMvpMatrix");
		gouraudModelUniformLocation = glGetUniformLocation(gouraudProgram.getId(), "uModelMatrix");
		gouraudNormalUniformLocation = glGetUniformLocation(gouraudProgram.getId(), "uNormalMatrix");
		gouraudLightUniformLocation = glGetUniformLocation(gouraudProgram.getId(), "uLightPos");
		gouraudBaseColorUniformLocation = glGetUniformLocation(gouraudProgram.getId(), "uBaseColor");

		phongMvpUniformLocation = glGetUniformLocation(phongProgram.getId(), "uMvpMatrix");
		phongModelUniformLocation = glGetUniformLocation(phongProgram.getId(), "uModelMatrix");
		phongNormalUniformLocation = glGetUniformLocation(phongProgram.getId(), "uNormalMatrix");
		phongLightUniformLocation = glGetUniformLocation(phongProgram.getId(), "uLightPos");
		phongBaseColorUniformLocation = glGetUniformLocation(phongProgram.getId(), "uBaseColor");

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glClearColor(0.06f, 0.08f, 0.12f, 1.0f);
	}

	// VAO mit zwei VBOs: Attribut 0 = Position, Attribut 1 = Normale
	private int createMesh(float[] coordinates, float[] normals) {
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		int positionVboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
		glBufferData(GL_ARRAY_BUFFER, coordinates, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);

		int normalVboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
		glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(1);

		return vaoId;
	}

	// eine Normale pro Dreieck (Kreuzprodukt zweier Kanten), fuer alle 3 Ecken gleich (Flat Shading Normalen)
	private float[] calculateFlatNormals(float[] coordinates) {
		float[] normals = new float[coordinates.length];

		for (int i = 0; i < coordinates.length; i += 9) {
			// die drei Eckpunkte des Dreiecks
			float x0 = coordinates[i], y0 = coordinates[i + 1], z0 = coordinates[i + 2];
			float x1 = coordinates[i + 3], y1 = coordinates[i + 4], z1 = coordinates[i + 5];
			float x2 = coordinates[i + 6], y2 = coordinates[i + 7], z2 = coordinates[i + 8];

			// zwei Kantenvektoren des Dreiecks
			float ux = x1 - x0, uy = y1 - y0, uz = z1 - z0;
			float vx = x2 - x0, vy = y2 - y0, vz = z2 - z0;

			// Kreuzprodukt der Kanten = Normale der Flaeche
			float nx = uy * vz - uz * vy;
			float ny = uz * vx - ux * vz;
			float nz = ux * vy - uy * vx;

			// auf Laenge 1 normieren
			float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (len > 1e-7f) {
				nx /= len;
				ny /= len;
				nz /= len;
			}

			// dieselbe Normale fuer alle 3 Ecken des Dreiecks speichern
			for (int v = 0; v < 3; v++) {
				normals[i + v * 3] = nx;
				normals[i + v * 3 + 1] = ny;
				normals[i + v * 3 + 2] = nz;
			}
		}

		return normals;
	}

	// Normalen muessen bei Transformationen eigentlich mit inverse(transpose(model)) transformiert werden.
	// Da wir hier aber nur Rotation/Translation/gleichmaessige Skalierung benutzen, reicht der obere
	// 3x3-Teil der Model-Matrix direkt aus (nach dem Normalisieren im Shader macht das keinen Unterschied).
	private float[] normalMatrixFrom(Matrix4 model) {
		float[] m = model.getValuesAsArray();
		return new float[] {
				m[0], m[1], m[2],
				m[4], m[5], m[6],
				m[8], m[9], m[10]
		};
	}

	private void renderWithGouraud(int vaoId, int vertexCount, Matrix4 model, Matrix4 projection, float r, float g, float b,
			float lightX, float lightY, float lightZ) {
		glUseProgram(gouraudProgram.getId());

		Matrix4 mvp = new Matrix4(model).multiply(projection);

		glUniformMatrix4fv(gouraudMvpUniformLocation, false, mvp.getValuesAsArray());
		glUniformMatrix4fv(gouraudModelUniformLocation, false, model.getValuesAsArray());
		glUniformMatrix3fv(gouraudNormalUniformLocation, false, normalMatrixFrom(model));
		glUniform3f(gouraudLightUniformLocation, lightX, lightY, lightZ);
		glUniform3f(gouraudBaseColorUniformLocation, r, g, b);

		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}

	private void renderWithPhong(int vaoId, int vertexCount, Matrix4 model, Matrix4 projection, float r, float g, float b,
			float lightX, float lightY, float lightZ) {
		glUseProgram(phongProgram.getId());

		Matrix4 mvp = new Matrix4(model).multiply(projection);

		glUniformMatrix4fv(phongMvpUniformLocation, false, mvp.getValuesAsArray());
		glUniformMatrix4fv(phongModelUniformLocation, false, model.getValuesAsArray());
		glUniformMatrix3fv(phongNormalUniformLocation, false, normalMatrixFrom(model));
		glUniform3f(phongLightUniformLocation, lightX, lightY, lightZ);
		glUniform3f(phongBaseColorUniformLocation, r, g, b);

		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}

	@Override
	public void update() {
		angle += 0.015f;
		time += 0.02f;
	}

	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Matrix4 projection = new Matrix4(0.1f, 12.0f, (float) Math.toRadians(60.0f), 1.0f);
		// eine gemeinsame Punktlichtquelle fuer beide Shading-Verfahren
		float lightX = 2.5f, lightY = 2.0f, lightZ = -2.0f;

		// Dreieck und Wuerfel: Gouraud (Licht wird im Vertex-Shader berechnet und interpoliert)
		Matrix4 triangleModel = new Matrix4().rotateZ(angle * 1.4f).translate(0.0f, 1.1f, -3.3f);
		renderWithGouraud(triangleVaoId, triangleVertexCount, triangleModel, projection, 0.90f, 0.25f, 0.45f, lightX, lightY, lightZ);

		Matrix4 cubeModel = new Matrix4().rotateY(angle).rotateX(angle * 0.6f).translate(-1.3f, 0.0f, -5.0f);
		renderWithGouraud(cubeVaoId, cubeVertexCount, cubeModel, projection, 0.20f, 0.75f, 0.95f, lightX, lightY, lightZ);

		// Tetraeder: Phong (Licht wird pro Fragment berechnet, praeziser vor allem an Kanten)
		Matrix4 tetraModel = new Matrix4()
				.rotateZ(-angle * 0.8f)
				.rotateY(angle * 1.2f)
				.translate(1.4f, (float) Math.sin(time) * 0.35f, -4.2f);
		renderWithPhong(tetraVaoId, tetraVertexCount, tetraModel, projection, 1.00f, 0.65f, 0.20f, lightX, lightY, lightZ);
	}
}
