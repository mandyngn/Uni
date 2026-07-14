package projekt;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;

public class ProjektShading extends AbstractOpenGLBase {

	// Zwei getrennte Shaderprogramme: einmal Gouraud, einmal Phong.
	private ShaderProgram gouraudProgram;
	private ShaderProgram phongProgram;
	// Urspruengliches Dreieck (bestehende Geometrie) wird weiterhin mitgerendert.
	private int triangleVaoId;
	private int triangleVertexCount;
	// Neuer platonischer Koerper 1: Wuerfel.
	private int cubeVaoId;
	private int cubeVertexCount;
	// Neuer platonischer Koerper 2: Tetraeder.
	private int tetraVaoId;
	private int tetraVertexCount;
	// Uniform-Positionen fuer Gouraud-Shader.
	private int gouraudMvpUniformLocation;
	private int gouraudModelUniformLocation;
	private int gouraudNormalUniformLocation;
	private int gouraudLightUniformLocation;
	private int gouraudBaseColorUniformLocation;
	// Uniform-Positionen fuer Phong-Shader.
	private int phongMvpUniformLocation;
	private int phongModelUniformLocation;
	private int phongNormalUniformLocation;
	private int phongLightUniformLocation;
	private int phongBaseColorUniformLocation;
	// Laufende Animationswerte: Winkel fuer Rotationen, time fuer Schwingung.
	private float angle;
	private float time;

	public static void main(String[] args) {
		// Startet die OpenGL-Anwendung mit Fenstertitel und Aufloesung.
		new ProjektShading().start("CG Projekt Shading", 700, 700);
	}

	@Override
	protected void init() {
		// Shaderprogramme laden und aktivieren.
		gouraudProgram = new ShaderProgram("projekt_gouraud");
		phongProgram = new ShaderProgram("projekt_phong");
		// Ein initiales Program binden, bevor Uniform- und Attributzustand verwendet wird.
		glUseProgram(gouraudProgram.getId());
		// Koordinaten, VAO, VBO, ... hier anlegen und im Grafikspeicher ablegen

		// Grundgroesse fuer den Wuerfel (halbe Kantenlaenge).
		float s = 0.5f;

		// Bestehendes Dreieck lokal um den Ursprung.
		float[] triangleCoordinates = {
				-0.5f, -0.5f, 0.5f,
				 0.5f, -0.5f, -0.5f,
				 0.0f,  0.5f, 0.0f
		};

		// Wuerfel lokal um den Ursprung (12 Dreiecke = 6 Flaechen).
		float[] cubeCoordinates = {
				// Frontseite (+z)
				-s, -s,  s,   s, -s,  s,   s,  s,  s,
				-s, -s,  s,   s,  s,  s,  -s,  s,  s,
				// Rueckseite (-z)
				 s, -s, -s,  -s, -s, -s,  -s,  s, -s,
				 s, -s, -s,  -s,  s, -s,   s,  s, -s,
				// Linke Seite (-x)
				-s, -s, -s,  -s, -s,  s,  -s,  s,  s,
				-s, -s, -s,  -s,  s,  s,  -s,  s, -s,
				// Rechte Seite (+x)
				 s, -s,  s,   s, -s, -s,   s,  s, -s,
				 s, -s,  s,   s,  s, -s,   s,  s,  s,
				// Oberseite (+y)
				-s,  s,  s,   s,  s,  s,   s,  s, -s,
				-s,  s,  s,   s,  s, -s,  -s,  s, -s,
				// Unterseite (-y)
				-s, -s, -s,   s, -s, -s,   s, -s,  s,
				-s, -s, -s,   s, -s,  s,  -s, -s,  s
		};

		// Tetraeder lokal um den Ursprung (4 Dreiecke).
		float[] tetraCoordinates = {
				// Tetraeder mit Schwerpunkt nahe Ursprung
				 0.0f,  0.70f,  0.0f,  -0.60f, -0.35f,  0.60f,   0.60f, -0.35f,  0.60f,
				 0.0f,  0.70f,  0.0f,   0.60f, -0.35f,  0.60f,   0.00f, -0.35f, -0.70f,
				 0.0f,  0.70f,  0.0f,   0.00f, -0.35f, -0.70f,  -0.60f, -0.35f,  0.60f,
				-0.60f, -0.35f,  0.60f,  0.00f, -0.35f, -0.70f,  0.60f, -0.35f,  0.60f
		};

		// Normalenvektoren pro Ecke: kommen aus dem Kreuzprodukt zweier Kanten pro Dreieck.
		// Sie muessen nicht "ultra-praezise" sein, da sie im Shader normalisiert werden.
		float[] triangleNormals = calculateFlatNormals(triangleCoordinates);
		float[] cubeNormals = calculateFlatNormals(cubeCoordinates);
		float[] tetraNormals = calculateFlatNormals(tetraCoordinates);

		// Geometrien in den Grafikspeicher laden und Vertexanzahl merken.
		triangleVaoId = createMesh(triangleCoordinates, triangleNormals);
		triangleVertexCount = triangleCoordinates.length / 3;

		cubeVaoId = createMesh(cubeCoordinates, cubeNormals);
		cubeVertexCount = cubeCoordinates.length / 3;

		tetraVaoId = createMesh(tetraCoordinates, tetraNormals);
		tetraVertexCount = tetraCoordinates.length / 3;

		// Positionen der Uniforms einmalig ermitteln.
		// Damit ersparen wir uns glGetUniformLocation in jeder Render-Iteration.
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

		// Tiefentest und Backface-Culling fuer korrektes 3D-Verhalten einschalten.
		glEnable(GL_DEPTH_TEST); // z-Buffer aktivieren
		glEnable(GL_CULL_FACE); // backface culling aktivieren
		// Hintergrundfarbe (dunkelblau) fuer besseren Kontrast zu den Objekten.
		glClearColor(0.06f, 0.08f, 0.12f, 1.0f);
	}

	private int createMesh(float[] coordinates, float[] normals) {
		// Jede Geometrie bekommt ein eigenes VAO, das Attributlayout + VBO-Bindings speichert.
		// VAO kapselt die Attributbelegung dieses Meshes.
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// VBO enthaelt alle Vertexpositionen als float-Array.
		int positionVboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
		// Vertexpositionen in den GPU-Speicher kopieren (statisch, da sich Geometrie nicht aendert).
		glBufferData(GL_ARRAY_BUFFER, coordinates, GL_STATIC_DRAW);
		// Attribut 0 = vec3 Position (x, y, z), ohne Stride/Offset.
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);

		// Zweites VBO fuer Normalenvektoren, Attribut 1 = vec3 Normale.
		int normalVboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
		// Normalen ebenfalls statisch in die GPU laden.
		glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(1);

		return vaoId;
	}

	private float[] calculateFlatNormals(float[] coordinates) {
		// Flat Shading Normalen: pro Dreieck eine gemeinsame Normale fuer alle 3 Ecken.
		float[] normals = new float[coordinates.length];

		for (int i = 0; i < coordinates.length; i += 9) {
			// Drei Punkte (p0, p1, p2) eines Dreiecks aus dem Positionsarray lesen.
			float x0 = coordinates[i];
			float y0 = coordinates[i + 1];
			float z0 = coordinates[i + 2];

			float x1 = coordinates[i + 3];
			float y1 = coordinates[i + 4];
			float z1 = coordinates[i + 5];

			float x2 = coordinates[i + 6];
			float y2 = coordinates[i + 7];
			float z2 = coordinates[i + 8];

			float ux = x1 - x0;
			float uy = y1 - y0;
			float uz = z1 - z0;

			float vx = x2 - x0;
			float vy = y2 - y0;
			float vz = z2 - z0;

			// Kreuzprodukt u x v liefert den Flaechennormalenvektor.
			float nx = uy * vz - uz * vy;
			float ny = uz * vx - ux * vz;
			float nz = ux * vy - uy * vx;

			float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (len > 1e-7f) {
				// Auf Laenge 1 normieren, damit Beleuchtungsberechnung stabil bleibt.
				nx /= len;
				ny /= len;
				nz /= len;
			} else {
				// Fallback fuer degenerierte Dreiecke mit praktisch 0 Flaeche.
				nx = 0.0f;
				ny = 1.0f;
				nz = 0.0f;
			}

			for (int v = 0; v < 3; v++) {
				// Dieselbe Normale fuer alle drei Eckpunkte des Dreiecks speichern.
				normals[i + v * 3] = nx;
				normals[i + v * 3 + 1] = ny;
				normals[i + v * 3 + 2] = nz;
			}
		}

		return normals;
	}

	private float[] calculateNormalMatrix3x3(Matrix4 model) {
		// Model-Matrix als column-major Array auslesen (OpenGL-Layout).
		float[] m = model.getValuesAsArray();

		float a00 = m[0];
		float a01 = m[4];
		float a02 = m[8];
		float a10 = m[1];
		float a11 = m[5];
		float a12 = m[9];
		float a20 = m[2];
		float a21 = m[6];
		float a22 = m[10];

		float det = a00 * (a11 * a22 - a12 * a21)
				- a01 * (a10 * a22 - a12 * a20)
				+ a02 * (a10 * a21 - a11 * a20);

		if (Math.abs(det) < 1e-7f) {
			// Falls nicht invertierbar: Identitaet als sichere Ersatz-Normalmatrix.
			return new float[] {
					1.0f, 0.0f, 0.0f,
					0.0f, 1.0f, 0.0f,
					0.0f, 0.0f, 1.0f
			};
		}

		float invDet = 1.0f / det;

		float inv00 = (a11 * a22 - a12 * a21) * invDet;
		float inv01 = (a02 * a21 - a01 * a22) * invDet;
		float inv02 = (a01 * a12 - a02 * a11) * invDet;
		float inv10 = (a12 * a20 - a10 * a22) * invDet;
		float inv11 = (a00 * a22 - a02 * a20) * invDet;
		float inv12 = (a02 * a10 - a00 * a12) * invDet;
		float inv20 = (a10 * a21 - a11 * a20) * invDet;
		float inv21 = (a01 * a20 - a00 * a21) * invDet;
		float inv22 = (a00 * a11 - a01 * a10) * invDet;

		// inverse-transponierte Model-Matrix als mat3 im column-major Layout.
		// Fuer orthogonale Rotationen entspricht das effektiv der Rotationsmatrix.
		return new float[] {
				inv00, inv01, inv02,
				inv10, inv11, inv12,
				inv20, inv21, inv22
		};
	}

	private void renderWithGouraud(int vaoId, int vertexCount, Matrix4 model, Matrix4 projection, float r, float g, float b,
			float lightX, float lightY, float lightZ) {
		// Gouraud-Programm binden: Licht wird im Vertex-Shader berechnet.
		glUseProgram(gouraudProgram.getId());

		// MVP fuer Positionstransformation und Normalmatrix fuer korrekte Normalenrichtung.
		Matrix4 mvp = new Matrix4(model).multiply(projection);
		float[] normalMatrix = calculateNormalMatrix3x3(model);

		// Alle benoetigten Uniforms fuer diesen Drawcall setzen.
		glUniformMatrix4fv(gouraudMvpUniformLocation, false, mvp.getValuesAsArray());
		glUniformMatrix4fv(gouraudModelUniformLocation, false, model.getValuesAsArray());
		glUniformMatrix3fv(gouraudNormalUniformLocation, false, normalMatrix);
		glUniform3f(gouraudLightUniformLocation, lightX, lightY, lightZ);
		glUniform3f(gouraudBaseColorUniformLocation, r, g, b);

		// Geometrie binden und Dreiecke zeichnen.
		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}

	private void renderWithPhong(int vaoId, int vertexCount, Matrix4 model, Matrix4 projection, float r, float g, float b,
			float lightX, float lightY, float lightZ) {
		// Phong-Programm binden: Licht wird pro Fragment im Fragment-Shader berechnet.
		glUseProgram(phongProgram.getId());

		// Wie bei Gouraud: dieselben Raumtransformationen, anderes Shading-Verfahren.
		Matrix4 mvp = new Matrix4(model).multiply(projection);
		float[] normalMatrix = calculateNormalMatrix3x3(model);

		// Uniforms setzen (Matrizen, Lichtposition, Materialgrundfarbe).
		glUniformMatrix4fv(phongMvpUniformLocation, false, mvp.getValuesAsArray());
		glUniformMatrix4fv(phongModelUniformLocation, false, model.getValuesAsArray());
		glUniformMatrix3fv(phongNormalUniformLocation, false, normalMatrix);
		glUniform3f(phongLightUniformLocation, lightX, lightY, lightZ);
		glUniform3f(phongBaseColorUniformLocation, r, g, b);

		// Geometrie zeichnen.
		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}

	@Override
	public void update() {
		// Transformation durchfuehren (Matrix anpassen)
		// Winkel pro Frame leicht erhoehen -> kontinuierliche Rotation um die z-Achse.
		// Pro Frame fortschreiben: Rotation und Schwingphase.
		// angle steuert alle Rotationen, time steuert die sinusfoermige Auf/Ab-Bewegung.
		angle += 0.015f;
		time += 0.02f;
	}

	@Override
	protected void render() {
		// Farb- und Tiefenpuffer loeschen (neues Frame).
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Matrix an Shader uebertragen
		// Projektionsparameter: sichtbarer Bereich von near bis far entlang der z-Achse.
		float aspect = 1.0f;
		float near = 0.1f;
		float far = 12.0f;
		float fovY = (float) Math.toRadians(60.0f);

		// Perspektivprojektion fuer alle Objekte in diesem Frame.
		Matrix4 projection = new Matrix4(near, far, fovY, aspect);
		// Eine gemeinsame Punktlichtquelle fuer beide Shadingverfahren.
		float lightX = 2.5f;
		float lightY = 2.0f;
		float lightZ = -2.0f;

		// Objekt A (bestehendes Dreieck): lokal um Ursprung rotieren, dann nach oben/vorne schieben.
		// Reihenfolge ist wichtig: rotateZ().translate(...) ergibt T * R (erst Rotation, dann Translation im Weltkoordinatensystem).
		Matrix4 triangleModel = new Matrix4()
				.rotateZ(angle * 1.4f)
				.translate(0.0f, 1.1f, -3.3f);
		// MVP = Projection * Model und an den Vertexshader senden.
		// multiply(other) rechnet: this = other * this. Daher: von model aus mit projection multiplizieren => P * M.
		// Dieses Dreieck laeuft ebenfalls im Gouraud-Pfad (pro-Vertex-Licht).
		renderWithGouraud(triangleVaoId, triangleVertexCount, triangleModel, projection,
				0.90f, 0.25f, 0.45f, lightX, lightY, lightZ);

		// Objekt B (Wuerfel): Gouraud-Schattierung, links in der Szene.
		Matrix4 cubeModel = new Matrix4()
				.rotateY(angle)
				.rotateX(angle * 0.6f)
				.translate(-1.3f, 0.0f, -5.0f);
		// Wuerfel links: gut sichtbar fuer Gouraud-Interpolation ueber groessere Flaechen.
		renderWithGouraud(cubeVaoId, cubeVertexCount, cubeModel, projection,
				0.20f, 0.75f, 0.95f, lightX, lightY, lightZ);

		// Objekt C (Tetraeder): Phong-Schattierung, rechts in der Szene.
		Matrix4 tetraModel = new Matrix4()
				.rotateZ(-angle * 0.8f)
				.rotateY(angle * 1.2f)
				.translate(1.4f, (float) Math.sin(time) * 0.35f, -4.2f);
		// Tetraeder rechts: Phong mit pro-Fragment-Normale + Spekularanteil.
		renderWithPhong(tetraVaoId, tetraVertexCount, tetraModel, projection,
				1.00f, 0.65f, 0.20f, lightX, lightY, lightZ);

		// VAOs zeichnen
		// Auf Default-VAO zuruecksetzen, um unbeabsichtigte Seiteneffekte zu vermeiden.
		glBindVertexArray(0);
	}
}
