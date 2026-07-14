package projekt;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;
import lenz.opengl.Texture;

public class ProjektTexturePhong extends AbstractOpenGLBase {

	private ShaderProgram program;

	private int cubeVaoId;
	private int cubeVertexCount;
	private int tetraVaoId;
	private int tetraVertexCount;
	// zusaetzliches Objekt aus einer .obj-Datei, dreht sich lokal um den Wuerfel (Hierarchie wie Mond um Erde)
	private int orbiterVaoId;
	private int orbiterVertexCount;

	private int mvpUniformLocation;
	private int modelUniformLocation;
	private int normalUniformLocation;
	private int lightUniformLocation;
	private int blendUniformLocation;

	// eine grosse und eine winzige Textur, im Shader wird zwischen beiden gemischt (uTextureBlend)
	private int textureHuge;
	private int textureTiny;

	private float angle;
	private float time;

	// Bonus: Filter-Modus per Taste 1-4 umschaltbar
	// 0 = NEAREST, 1 = LINEAR, 2 = NEAREST_MIPMAP, 3 = LINEAR_MIPMAP (trilinear)
	private int filterMode = 3;
	private boolean[] keyWasDown = new boolean[4];

	public static void main(String[] args) {
		new ProjektTexturePhong().start("CG Projekt Textur + Phong", 900, 700);
	}

	@Override
	protected void init() {
		program = new ShaderProgram("projekt_texture_phong");
		glUseProgram(program.getId());

		float s = 0.8f;
		float[] cubeCoordinates = {
				-s, -s, s,  s, -s, s,  s, s, s,
				-s, -s, s,  s, s, s,  -s, s, s,
				s, -s, -s,  -s, -s, -s,  -s, s, -s,
				s, -s, -s,  -s, s, -s,  s, s, -s,
				-s, -s, -s,  -s, -s, s,  -s, s, s,
				-s, -s, -s,  -s, s, s,  -s, s, -s,
				s, -s, s,  s, -s, -s,  s, s, -s,
				s, -s, s,  s, s, -s,  s, s, s,
				-s, s, s,  s, s, s,  s, s, -s,
				-s, s, s,  s, s, -s,  -s, s, -s,
				-s, -s, -s,  s, -s, -s,  s, -s, s,
				-s, -s, -s,  s, -s, s,  -s, -s, s
		};
		float[] cubeUvs = createCubeUvs();

		float[] tetraCoordinates = {
				0.0f, 1.0f, 0.0f,  -0.9f, -0.5f, 0.9f,   0.9f, -0.5f, 0.9f,
				0.0f, 1.0f, 0.0f,   0.9f, -0.5f, 0.9f,   0.0f, -0.5f, -1.0f,
				0.0f, 1.0f, 0.0f,   0.0f, -0.5f, -1.0f, -0.9f, -0.5f, 0.9f,
				-0.9f, -0.5f, 0.9f,  0.0f, -0.5f, -1.0f, 0.9f, -0.5f, 0.9f
		};
		float[] tetraUvs = {
				0.5f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,
				0.5f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,
				0.5f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,
				0.0f, 0.0f,  0.5f, 1.0f,  1.0f, 0.0f
		};

		cubeVaoId = createMesh(cubeCoordinates, calculateFlatNormals(cubeCoordinates), cubeUvs);
		cubeVertexCount = cubeCoordinates.length / 3;
		tetraVaoId = createMesh(tetraCoordinates, calculateFlatNormals(tetraCoordinates), tetraUvs);
		tetraVertexCount = tetraCoordinates.length / 3;

		// zusaetzliches 3D-Objekt, das aus einer Datei eingelesen wird (statt in Java einprogrammiert)
		MeshData orbiterMesh = loadObjMesh("/res/models/orbiter.obj", 0.45f);
		orbiterVaoId = createMesh(orbiterMesh.positions, calculateFlatNormals(orbiterMesh.positions), orbiterMesh.uvs);
		orbiterVertexCount = orbiterMesh.positions.length / 3;

		mvpUniformLocation = glGetUniformLocation(program.getId(), "uMvpMatrix");
		modelUniformLocation = glGetUniformLocation(program.getId(), "uModelMatrix");
		normalUniformLocation = glGetUniformLocation(program.getId(), "uNormalMatrix");
		lightUniformLocation = glGetUniformLocation(program.getId(), "uLightPos");
		blendUniformLocation = glGetUniformLocation(program.getId(), "uTextureBlend");

		// Texturen laden, 10 Mipmap-Level erlauben (fuer Aufgabe: Mipmapping beobachten)
		textureHuge = new Texture("checker_large.png", 10).getId();
		textureTiny = new Texture("checker_small.png", 10).getId();
		applyFilter(filterMode);

		glUniform1i(glGetUniformLocation(program.getId(), "uTexture0"), 0);
		glUniform1i(glGetUniformLocation(program.getId(), "uTexture1"), 1);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glClearColor(0.05f, 0.07f, 0.12f, 1.0f);
	}

	// setzt Filter-/Mipmap-Parameter fuer beide Texturen, wird bei Tastendruck (1-4) neu aufgerufen
	private void applyFilter(int mode) {
		int minFilter, magFilter;
		switch (mode) {
			case 0:
				minFilter = GL_NEAREST;
				magFilter = GL_NEAREST;
				break;
			case 1:
				minFilter = GL_LINEAR;
				magFilter = GL_LINEAR;
				break;
			case 2:
				minFilter = GL_NEAREST_MIPMAP_NEAREST;
				magFilter = GL_NEAREST;
				break;
			default:
				minFilter = GL_LINEAR_MIPMAP_LINEAR;
				magFilter = GL_LINEAR;
				break;
		}

		for (int textureId : new int[] { textureHuge, textureTiny }) {
			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
		}
	}

	// UV-Koordinaten fuer den Wuerfel: jede der 6 Seiten bekommt dieselbe quadratische Textur
	private float[] createCubeUvs() {
		float[] face = {
				0.0f, 0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
				0.0f, 0.0f,  1.0f, 1.0f,  0.0f, 1.0f
		};

		float[] uvs = new float[6 * face.length];
		for (int i = 0; i < 6; i++) {
			System.arraycopy(face, 0, uvs, i * face.length, face.length);
		}
		return uvs;
	}

	// VAO mit drei VBOs: Attribut 0 = Position, Attribut 1 = Normale, Attribut 2 = UV
	private int createMesh(float[] coordinates, float[] normals, float[] uvs) {
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

		int uvVboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvVboId);
		glBufferData(GL_ARRAY_BUFFER, uvs, GL_STATIC_DRAW);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(2);

		return vaoId;
	}

	// eine Normale pro Dreieck (Kreuzprodukt zweier Kanten), fuer alle 3 Ecken gleich (Flat Shading Normalen)
	private float[] calculateFlatNormals(float[] coordinates) {
		float[] normals = new float[coordinates.length];

		for (int i = 0; i < coordinates.length; i += 9) {
			float x0 = coordinates[i], y0 = coordinates[i + 1], z0 = coordinates[i + 2];
			float x1 = coordinates[i + 3], y1 = coordinates[i + 4], z1 = coordinates[i + 5];
			float x2 = coordinates[i + 6], y2 = coordinates[i + 7], z2 = coordinates[i + 8];

			float ux = x1 - x0, uy = y1 - y0, uz = z1 - z0;
			float vx = x2 - x0, vy = y2 - y0, vz = z2 - z0;

			float nx = uy * vz - uz * vy;
			float ny = uz * vx - ux * vz;
			float nz = ux * vy - uy * vx;

			float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (len > 1e-7f) {
				nx /= len;
				ny /= len;
				nz /= len;
			}

			for (int v = 0; v < 3; v++) {
				normals[i + v * 3] = nx;
				normals[i + v * 3 + 1] = ny;
				normals[i + v * 3 + 2] = nz;
			}
		}

		return normals;
	}

	// wir benutzen nur Rotation/Translation/gleichmaessige Skalierung, deshalb reicht
	// der obere 3x3-Teil der Model-Matrix als Normal-Matrix (nach dem Normalisieren im Shader)
	private float[] normalMatrixFrom(Matrix4 model) {
		float[] m = model.getValuesAsArray();
		return new float[] {
				m[0], m[1], m[2],
				m[4], m[5], m[6],
				m[8], m[9], m[10]
		};
	}

	// liest ein einfaches OBJ ein (nur Dreiecke, nur "v" und "f" Zeilen).
	// Die Datei hat keine eigenen UVs, deshalb berechnen wir sie einfach aus x/z der Position.
	private MeshData loadObjMesh(String resourcePath, float scale) {
		List<float[]> vertices = new ArrayList<>();

		List<Float> outPositions = new ArrayList<>();
		List<Float> outUvs = new ArrayList<>();

		try (InputStream in = getClass().getResourceAsStream(resourcePath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("v ")) {
					// Vertex-Zeile: "v x y z"
					String[] p = line.substring(2).trim().split("\\s+");
					vertices.add(new float[] {
							Float.parseFloat(p[0]) * scale,
							Float.parseFloat(p[1]) * scale,
							Float.parseFloat(p[2]) * scale
					});
				} else if (line.startsWith("f ")) {
					// Face-Zeile: "f i1 i2 i3", Indizes sind 1-basiert
					String[] p = line.substring(2).trim().split("\\s+");
					for (String indexToken : p) {
						float[] v = vertices.get(Integer.parseInt(indexToken) - 1);
						outPositions.add(v[0]);
						outPositions.add(v[1]);
						outPositions.add(v[2]);
						outUvs.add(v[0] * 0.5f + 0.5f);
						outUvs.add(v[2] * 0.5f + 0.5f);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Konnte OBJ-Datei nicht laden: " + resourcePath, e);
		}

		return new MeshData(toFloatArray(outPositions), toFloatArray(outUvs));
	}

	private float[] toFloatArray(List<Float> values) {
		float[] out = new float[values.size()];
		for (int i = 0; i < values.size(); i++) {
			out[i] = values.get(i);
		}
		return out;
	}

	private static final class MeshData {
		private final float[] positions;
		private final float[] uvs;

		private MeshData(float[] positions, float[] uvs) {
			this.positions = positions;
			this.uvs = uvs;
		}
	}

	@Override
	public void update() {
		angle += 0.012f;
		time += 0.015f;

		// Bonus: Tasten 1-4 schalten den Textur-Filter um (nur beim Druecken, nicht bei gehaltener Taste)
		long window = glfwGetCurrentContext();
		int[] keys = { GLFW_KEY_1, GLFW_KEY_2, GLFW_KEY_3, GLFW_KEY_4 };
		for (int i = 0; i < keys.length; i++) {
			boolean isDown = glfwGetKey(window, keys[i]) == GLFW_PRESS;
			if (isDown && !keyWasDown[i]) {
				filterMode = i;
				applyFilter(filterMode);
				System.out.println("Filter-Modus: " + filterMode);
			}
			keyWasDown[i] = isDown;
		}
	}

	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		float aspect = 900.0f / 700.0f;
		Matrix4 projection = new Matrix4(0.1f, 30.0f, (float) Math.toRadians(60.0f), aspect);

		// Licht bewegt sich im Kreis, damit man die Phong-Beleuchtung gut sieht
		float lightX = 2.2f + (float) Math.cos(time * 0.7f) * 1.8f;
		float lightY = 2.0f;
		float lightZ = -2.0f + (float) Math.sin(time * 0.7f) * 1.4f;
		glUniform3f(lightUniformLocation, lightX, lightY, lightZ);
		glUniform1f(blendUniformLocation, 0.35f);

		// Wuerfel: bewegt sich naeher/weiter weg, damit man Mipmapping/Filterung gut sieht
		float cubeZ = -7.0f - (float) Math.sin(time * 0.9f) * 3.0f;
		Matrix4 cubeModel = new Matrix4().rotateY(angle).rotateX(angle * 0.5f).translate(-1.8f, 0.0f, cubeZ);
		drawObject(cubeVaoId, cubeVertexCount, cubeModel, projection);

		// Tetraeder
		Matrix4 tetraModel = new Matrix4()
				.rotateY(-angle * 1.1f)
				.rotateZ(angle * 0.7f)
				.translate(1.8f, (float) Math.sin(time) * 0.4f, -5.5f);
		drawObject(tetraVaoId, tetraVertexCount, tetraModel, projection);

		// Orbiter: eigene lokale Transformation (Rotation + Verschiebung), danach mit cubeModel
		// kombiniert -> dreht sich also im lokalen Koordinatensystem des Wuerfels mit (Hierarchie)
		Matrix4 orbiterLocal = new Matrix4().rotateY(angle * 3.0f).translate(2.4f, 0.0f, 0.0f);
		Matrix4 orbiterWorld = new Matrix4(orbiterLocal).multiply(cubeModel);
		drawObject(orbiterVaoId, orbiterVertexCount, orbiterWorld, projection);
	}

	private void drawObject(int vaoId, int vertexCount, Matrix4 model, Matrix4 projection) {
		Matrix4 mvp = new Matrix4(model).multiply(projection);

		glUniformMatrix4fv(mvpUniformLocation, false, mvp.getValuesAsArray());
		glUniformMatrix4fv(modelUniformLocation, false, model.getValuesAsArray());
		glUniformMatrix3fv(normalUniformLocation, false, normalMatrixFrom(model));

		// zwei Texturen gleichzeitig binden (Textureinheit 0 und 1), der Shader mischt sie
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureHuge);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, textureTiny);

		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}
}
