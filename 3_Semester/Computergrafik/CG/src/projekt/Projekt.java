package projekt;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;

public class Projekt extends AbstractOpenGLBase {

	private ShaderProgram shaderProgram;
	// Urspruengliches Dreieck (bestehende Geometrie) wird weiterhin mitgerendert.
	private int triangleVaoId;
	private int triangleVertexCount;
	// Neuer platonischer Koerper 1: Wuerfel.
	private int cubeVaoId;
	private int cubeVertexCount;
	// Neuer platonischer Koerper 2: Tetraeder.
	private int tetraVaoId;
	private int tetraVertexCount;
	// Uniforms fuer Transformation (MVP) und Objektfarbe.
	private int mvpUniformLocation;
	private int colorUniformLocation;
	// Laufende Animationswerte: Winkel fuer Rotationen, time fuer Schwingung.
	private float angle;
	private float time;

	public static void main(String[] args) {
		new Projekt().start("CG Projekt", 700, 700);
	}

	@Override
	protected void init() {
		// Shaderprogramm laden und aktivieren.
		shaderProgram = new ShaderProgram("projekt");
		glUseProgram(shaderProgram.getId());
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

		// Geometrien in den Grafikspeicher laden und Vertexanzahl merken.
		triangleVaoId = createMesh(triangleCoordinates);
		triangleVertexCount = triangleCoordinates.length / 3;

		cubeVaoId = createMesh(cubeCoordinates);
		cubeVertexCount = cubeCoordinates.length / 3;

		tetraVaoId = createMesh(tetraCoordinates);
		tetraVertexCount = tetraCoordinates.length / 3;

		// Positionen der Uniforms einmalig ermitteln.
		mvpUniformLocation = glGetUniformLocation(shaderProgram.getId(), "uMvpMatrix");
		colorUniformLocation = glGetUniformLocation(shaderProgram.getId(), "uColor");

		glEnable(GL_DEPTH_TEST); // z-Buffer aktivieren
		glEnable(GL_CULL_FACE); // backface culling aktivieren
		glClearColor(0.06f, 0.08f, 0.12f, 1.0f);
	}

	private int createMesh(float[] coordinates) {
		// VAO kapselt die Attributbelegung dieses Meshes.
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// VBO enthaelt alle Vertexpositionen als float-Array.
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, coordinates, GL_STATIC_DRAW);
		// Attribut 0 = vec3 Position (x, y, z), ohne Stride/Offset.
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);

		return vaoId;
	}

	@Override
	public void update() {
		// Transformation durchfuehren (Matrix anpassen)
		// Winkel pro Frame leicht erhoehen -> kontinuierliche Rotation um die z-Achse.
		// Pro Frame fortschreiben: Rotation und Schwingphase.
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

		// Objekt A (bestehendes Dreieck): lokal um Ursprung rotieren, dann nach oben/vorne schieben.
		// Reihenfolge ist wichtig: rotateZ().translate(...) ergibt T * R (erst Rotation, dann Translation im Weltkoordinatensystem).
		Matrix4 triangleModel = new Matrix4()
				.rotateZ(angle * 1.4f)
				.translate(0.0f, 1.1f, -3.3f);
		// MVP = Projection * Model und an den Vertexshader senden.
		// multiply(other) rechnet: this = other * this. Daher: von model aus mit projection multiplizieren => P * M.
		Matrix4 triangleMvp = new Matrix4(triangleModel).multiply(projection);
		glUniformMatrix4fv(mvpUniformLocation, false, triangleMvp.getValuesAsArray());
		// Eigene Farbe nur fuer dieses Objekt setzen.
		glUniform3f(colorUniformLocation, 0.90f, 0.25f, 0.45f);
		glBindVertexArray(triangleVaoId);
		glDrawArrays(GL_TRIANGLES, 0, triangleVertexCount);

		// Objekt B (Wuerfel): lokal um Ursprung drehen, dann links in die Welt verschieben.
		Matrix4 cubeModel = new Matrix4()
				.rotateY(angle)
				.rotateX(angle * 0.6f)
				.translate(-1.3f, 0.0f, -5.0f);
		Matrix4 cubeMvp = new Matrix4(cubeModel).multiply(projection);
		glUniformMatrix4fv(mvpUniformLocation, false, cubeMvp.getValuesAsArray());
		// Eigene Farbe nur fuer den Wuerfel.
		glUniform3f(colorUniformLocation, 0.20f, 0.75f, 0.95f);
		glBindVertexArray(cubeVaoId);
		glDrawArrays(GL_TRIANGLES, 0, cubeVertexCount);

		// Objekt C (Tetraeder): eigene Rotation + sinusfoermige y-Schwingung, dann rechts positionieren.
		Matrix4 tetraModel = new Matrix4()
				.rotateZ(-angle * 0.8f)
				.rotateY(angle * 1.2f)
				.translate(1.4f, (float) Math.sin(time) * 0.35f, -4.2f);
		Matrix4 tetraMvp = new Matrix4(tetraModel).multiply(projection);
		glUniformMatrix4fv(mvpUniformLocation, false, tetraMvp.getValuesAsArray());
		// Eigene Farbe nur fuer das Tetraeder.
		glUniform3f(colorUniformLocation, 1.00f, 0.65f, 0.20f);
		// VAOs zeichnen
		glBindVertexArray(tetraVaoId);
		glDrawArrays(GL_TRIANGLES, 0, tetraVertexCount);
	}
}
