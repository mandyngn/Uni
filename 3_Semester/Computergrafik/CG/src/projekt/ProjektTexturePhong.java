package projekt;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lenz.opengl.AbstractOpenGLBase;
import lenz.opengl.ShaderProgram;
import lenz.opengl.Texture;

public class ProjektTexturePhong extends AbstractOpenGLBase {

	// Neues Shaderprogramm: Texturierung + Phong-Schattierung in einem Pass.
	private ShaderProgram texturePhongProgram;

	// Zwei Objekte fuer den Vergleich (links/rechts).
	private int cubeVaoId;
	private int cubeVertexCount;
	private int tetraVaoId;
	private int tetraVertexCount;
	// Zusaetzliches Objekt aus Datei (OBJ), als hierarchisches Kind des Wuerfels.
	private int orbiterVaoId;
	private int orbiterVertexCount;

	// Uniform-Locations.
	private int mvpUniformLocation;
	private int modelUniformLocation;
	private int normalUniformLocation;
	private int lightUniformLocation;
	private int texture0UniformLocation;
	private int texture1UniformLocation;
	private int blendUniformLocation;

	// Vier Filter-/MIP-Modi als Texturpaare (riesig + winzig).
	// 0: NEAREST ohne MIP
	// 1: LINEAR ohne MIP
	// 2: NEAREST_MIPMAP_NEAREST
	// 3: LINEAR_MIPMAP_LINEAR
	private final int[] hugeTextureByMode = new int[4];
	private final int[] tinyTextureByMode = new int[4];

	// Laufende Animation und Interaktionszustand.
	private float angle;
	private float time;
	private float textureBlend = 0.35f;
	private int filterMode = 3;
	private boolean key1WasDown;
	private boolean key2WasDown;
	private boolean key3WasDown;
	private boolean key4WasDown;
	private int lastShownFilterMode = -1;
	private float lastShownBlend = -1.0f;

	public static void main(String[] args) {
		// Separater Starter fuer diese Uebung.
		new ProjektTexturePhong().start("CG Projekt Textur + Phong", 900, 700);
	}

	@Override
	protected void init() {
		// Shader laden.
		texturePhongProgram = new ShaderProgram("projekt_texture_phong");
		glUseProgram(texturePhongProgram.getId());

		// Wuerfelgeometrie lokal um den Ursprung, pro Seite zwei Dreiecke.
		float s = 0.8f;
		float[] cubeCoordinates = {
				// Frontseite (+z)
				-s, -s, s,  s, -s, s,  s, s, s,
				-s, -s, s,  s, s, s,  -s, s, s,
				// Rueckseite (-z)
				s, -s, -s,  -s, -s, -s,  -s, s, -s,
				s, -s, -s,  -s, s, -s,  s, s, -s,
				// Linke Seite (-x)
				-s, -s, -s,  -s, -s, s,  -s, s, s,
				-s, -s, -s,  -s, s, s,  -s, s, -s,
				// Rechte Seite (+x)
				s, -s, s,  s, -s, -s,  s, s, -s,
				s, -s, s,  s, s, -s,  s, s, s,
				// Oberseite (+y)
				-s, s, s,  s, s, s,  s, s, -s,
				-s, s, s,  s, s, -s,  -s, s, -s,
				// Unterseite (-y)
				-s, -s, -s,  s, -s, -s,  s, -s, s,
				-s, -s, -s,  s, -s, s,  -s, -s, s
		};

		// UV-Koordinaten pro Ecke, wiederholt pro Dreieck.
		float[] cubeUvs = createCubeUvs();

		// Tetraeder mit 4 Dreiecken.
		float[] tetraCoordinates = {
				0.0f, 1.0f, 0.0f,  -0.9f, -0.5f, 0.9f,   0.9f, -0.5f, 0.9f,
				0.0f, 1.0f, 0.0f,   0.9f, -0.5f, 0.9f,   0.0f, -0.5f, -1.0f,
				0.0f, 1.0f, 0.0f,   0.0f, -0.5f, -1.0f, -0.9f, -0.5f, 0.9f,
				-0.9f, -0.5f, 0.9f,  0.0f, -0.5f, -1.0f, 0.9f, -0.5f, 0.9f
		};

		// UVs fuer Tetraeder: einfaches Layout pro Flaeche.
		float[] tetraUvs = {
				0.5f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,
				0.5f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,
				0.5f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f,
				0.0f, 0.0f,  0.5f, 1.0f,  1.0f, 0.0f
		};

		// Normalen aus Geometrie (Kreuzprodukt je Dreieck) ableiten.
		float[] cubeNormals = calculateFlatNormals(cubeCoordinates);
		float[] tetraNormals = calculateFlatNormals(tetraCoordinates);

		// VAOs/VBOs aufbauen: Position + Normale + UV.
		cubeVaoId = createMesh(cubeCoordinates, cubeNormals, cubeUvs);
		cubeVertexCount = cubeCoordinates.length / 3;
		tetraVaoId = createMesh(tetraCoordinates, tetraNormals, tetraUvs);
		tetraVertexCount = tetraCoordinates.length / 3;

		// OBJ aus Datei laden: erfuellt "Objekt aus Datei" als zusaetzliches 3D-Objekt.
		MeshData orbiterMesh = loadObjMesh("/res/models/orbiter.obj", 0.45f);
		float[] orbiterNormals = calculateFlatNormals(orbiterMesh.positions);
		orbiterVaoId = createMesh(orbiterMesh.positions, orbiterNormals, orbiterMesh.uvs);
		orbiterVertexCount = orbiterMesh.positions.length / 3;

		// Uniform-Locations einmalig cachen.
		mvpUniformLocation = glGetUniformLocation(texturePhongProgram.getId(), "uMvpMatrix");
		modelUniformLocation = glGetUniformLocation(texturePhongProgram.getId(), "uModelMatrix");
		normalUniformLocation = glGetUniformLocation(texturePhongProgram.getId(), "uNormalMatrix");
		lightUniformLocation = glGetUniformLocation(texturePhongProgram.getId(), "uLightPos");
		texture0UniformLocation = glGetUniformLocation(texturePhongProgram.getId(), "uTexture0");
		texture1UniformLocation = glGetUniformLocation(texturePhongProgram.getId(), "uTexture1");
		blendUniformLocation = glGetUniformLocation(texturePhongProgram.getId(), "uTextureBlend");

		// Riesige und winzige Textur fuer jeden Filter-/MIP-Modus erzeugen.
		buildTextureModes();

		// Statische Samplerzuordnung: Textureinheit 0/1.
		glUniform1i(texture0UniformLocation, 0);
		glUniform1i(texture1UniformLocation, 1);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glClearColor(0.05f, 0.07f, 0.12f, 1.0f);

		// On-Screen-Hilfe direkt beim Start anzeigen.
		updateWindowTitle(glfwGetCurrentContext(), true);
	}

	private String getFilterModeName(int mode) {
		switch (mode) {
			case 0:
				return "NEAREST (no mip)";
			case 1:
				return "LINEAR (no mip)";
			case 2:
				return "NEAREST_MIPMAP_NEAREST";
			case 3:
				return "LINEAR_MIPMAP_LINEAR";
			default:
				return "unknown";
		}
	}

	private void updateWindowTitle(long window, boolean force) {
		if (window == 0L) {
			return;
		}

		// Nur aktualisieren, wenn sich die angezeigten Werte geaendert haben.
		if (!force && lastShownFilterMode == filterMode && Math.abs(lastShownBlend - textureBlend) < 0.0001f) {
			return;
		}

		String title = String.format(
				"CG Textur+Phong | Mode [1-4]: %d %s | Keys: 1..4 Filter",
				filterMode + 1,
				getFilterModeName(filterMode));
		glfwSetWindowTitle(window, title);

		lastShownFilterMode = filterMode;
		lastShownBlend = textureBlend;
	}

	private void buildTextureModes() {
		// Mode 0: naechster Nachbar, ohne MIPs.
		hugeTextureByMode[0] = createFileTexture("checker_large.png", false, GL_NEAREST, GL_NEAREST);
		tinyTextureByMode[0] = createFileTexture("checker_small.png", false, GL_NEAREST, GL_NEAREST);

		// Mode 1: bilinear, ohne MIPs.
		hugeTextureByMode[1] = createFileTexture("checker_large.png", false, GL_LINEAR, GL_LINEAR);
		tinyTextureByMode[1] = createFileTexture("checker_small.png", false, GL_LINEAR, GL_LINEAR);

		// Mode 2: nearest MIP-Level + nearest sampling.
		hugeTextureByMode[2] = createFileTexture("checker_large.png", true, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST);
		tinyTextureByMode[2] = createFileTexture("checker_small.png", true, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST);

		// Mode 3: trilinear.
		hugeTextureByMode[3] = createFileTexture("checker_large.png", true, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR);
		tinyTextureByMode[3] = createFileTexture("checker_small.png", true, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR);
	}

	private int createFileTexture(String resourceName, boolean useMipMaps, int minFilter, int magFilter) {
		// Bilddatei aus /res/textures laden und danach den gewuenschten Sampling-Modus setzen.
		Texture texture = new Texture(resourceName, useMipMaps ? 10 : 1);
		int textureId = texture.getId();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

		if (useMipMaps) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 1000);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
		}

		return textureId;
	}

	private MeshData loadObjMesh(String resourcePath, float scale) {
		List<float[]> vertices = new ArrayList<>();
		List<float[]> texCoords = new ArrayList<>();
		// Dummy-Eintrag, damit OBJ-Indizes direkt (1-basiert) adressiert werden koennen.
		vertices.add(new float[] { 0.0f, 0.0f, 0.0f });
		texCoords.add(new float[] { 0.0f, 0.0f });

		List<Float> outPositions = new ArrayList<>();
		List<Float> outUvs = new ArrayList<>();

		try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
			if (in == null) {
				throw new RuntimeException("OBJ resource not found: " + resourcePath);
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String trimmed = line.trim();
					if (trimmed.isEmpty() || trimmed.startsWith("#")) {
						continue;
					}

					if (trimmed.startsWith("v ")) {
						String[] p = trimmed.substring(2).trim().split("\\s+");
						float x = Float.parseFloat(p[0]) * scale;
						float y = Float.parseFloat(p[1]) * scale;
						float z = Float.parseFloat(p[2]) * scale;
						vertices.add(new float[] { x, y, z });
					} else if (trimmed.startsWith("vt ")) {
						String[] p = trimmed.substring(3).trim().split("\\s+");
						float u = Float.parseFloat(p[0]);
						float v = Float.parseFloat(p[1]);
						texCoords.add(new float[] { u, v });
					} else if (trimmed.startsWith("f ")) {
						String[] p = trimmed.substring(2).trim().split("\\s+");
						if (p.length < 3) {
							continue;
						}

						// Fan-Triangulierung fuer Polygone mit mehr als 3 Ecken.
						for (int i = 2; i < p.length; i++) {
							appendObjVertex(p[0], vertices, texCoords, outPositions, outUvs);
							appendObjVertex(p[i - 1], vertices, texCoords, outPositions, outUvs);
							appendObjVertex(p[i], vertices, texCoords, outPositions, outUvs);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load OBJ resource: " + resourcePath, e);
		}

		return new MeshData(toFloatArray(outPositions), toFloatArray(outUvs));
	}

	private void appendObjVertex(String token, List<float[]> vertices, List<float[]> texCoords, List<Float> outPositions,
			List<Float> outUvs) {
		String[] indices = token.split("/");

		int vertexIndex = resolveObjIndex(indices[0], vertices.size());
		float[] v = vertices.get(vertexIndex);
		outPositions.add(v[0]);
		outPositions.add(v[1]);
		outPositions.add(v[2]);

		if (indices.length > 1 && !indices[1].isEmpty()) {
			int texIndex = resolveObjIndex(indices[1], texCoords.size());
			float[] uv = texCoords.get(texIndex);
			outUvs.add(uv[0]);
			outUvs.add(uv[1]);
		} else {
			// Fallback: einfache UV-Projektion auf X/Z, falls OBJ keine vt-Daten enthaelt.
			float u = v[0] * 0.5f + 0.5f;
			float w = v[2] * 0.5f + 0.5f;
			outUvs.add(u);
			outUvs.add(w);
		}
	}

	private int resolveObjIndex(String rawIndex, int listSize) {
		int idx = Integer.parseInt(rawIndex);
		if (idx > 0) {
			return idx;
		}
		// Negative OBJ-Indizes referenzieren relativ vom Ende.
		return listSize + idx;
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

	private float[] calculateFlatNormals(float[] coordinates) {
		float[] normals = new float[coordinates.length];

		for (int i = 0; i < coordinates.length; i += 9) {
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

	private float[] calculateNormalMatrix3x3(Matrix4 model) {
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

		return new float[] {
				inv00, inv01, inv02,
				inv10, inv11, inv12,
				inv20, inv21, inv22
		};
	}

	@Override
	public void update() {
		angle += 0.012f;
		time += 0.015f;

		// Optionaler Bonus: einfache Tastaturabfrage via GLFW.
		// 1..4 schalten Filter-/MIP-Modus um.
		long window = glfwGetCurrentContext();
		if (window != 0L) {
			boolean key1Down = glfwGetKey(window, GLFW_KEY_1) == GLFW_PRESS;
			boolean key2Down = glfwGetKey(window, GLFW_KEY_2) == GLFW_PRESS;
			boolean key3Down = glfwGetKey(window, GLFW_KEY_3) == GLFW_PRESS;
			boolean key4Down = glfwGetKey(window, GLFW_KEY_4) == GLFW_PRESS;

			if (key1Down && !key1WasDown) {
				filterMode = 0;
			}
			if (key2Down && !key2WasDown) {
				filterMode = 1;
			}
			if (key3Down && !key3WasDown) {
				filterMode = 2;
			}
			if (key4Down && !key4WasDown) {
				filterMode = 3;
			}

			key1WasDown = key1Down;
			key2WasDown = key2Down;
			key3WasDown = key3Down;
			key4WasDown = key4Down;

			// On-Screen-Hilfe dynamisch aktualisieren, wenn Nutzer Werte aendert.
			updateWindowTitle(window, false);
		}
	}

	@Override
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glUseProgram(texturePhongProgram.getId());

		float aspect = 900.0f / 700.0f;
		Matrix4 projection = new Matrix4(0.1f, 30.0f, (float) Math.toRadians(60.0f), aspect);

		// Bewegte Lichtquelle fuer sichtbaren Phong-Effekt.
		float lightX = 2.2f + (float) Math.cos(time * 0.7f) * 1.8f;
		float lightY = 2.0f;
		float lightZ = -2.0f + (float) Math.sin(time * 0.7f) * 1.4f;
		glUniform3f(lightUniformLocation, lightX, lightY, lightZ);
		glUniform1f(blendUniformLocation, textureBlend);

		// Objekt 1 (Wuerfel): links, mit Distanzschwankung fuer MIP-Effekt-Beobachtung.
		float cubeZ = -7.0f - (float) Math.sin(time * 0.9f) * 3.0f;
		float cubeScale = 0.95f + (float) Math.sin(time * 1.1f) * 0.12f;
		Matrix4 cubeModel = new Matrix4()
				.scale(cubeScale)
				.rotateY(angle)
				.rotateX(angle * 0.5f)
				.translate(-1.8f, 0.0f, cubeZ);
		renderTexturedObject(cubeVaoId, cubeVertexCount, cubeModel, projection,
				hugeTextureByMode[filterMode], tinyTextureByMode[filterMode]);

		// Objekt 2 (Tetraeder): rechts, Texturreihenfolge umgekehrt fuer klaren Vergleich.
		float tetraScale = 0.85f + (float) Math.sin(time * 1.7f) * 0.10f;
		Matrix4 tetraModel = new Matrix4()
				.scale(tetraScale)
				.rotateY(-angle * 1.1f)
				.rotateZ(angle * 0.7f)
				.translate(1.8f, (float) Math.sin(time) * 0.4f, -5.5f);
		renderTexturedObject(tetraVaoId, tetraVertexCount, tetraModel, projection,
				tinyTextureByMode[filterMode], hugeTextureByMode[filterMode]);

		// Objekt 3 (OBJ): hierarchisch an den Wuerfel gekoppelt (lokales Parent-Child-System).
		float orbiterScale = 0.28f + (float) Math.sin(time * 2.8f) * 0.06f;
		Matrix4 orbiterLocal = new Matrix4()
				.scale(orbiterScale)
				.rotateY(angle * 3.0f)
				.translate(2.4f, 0.0f, 0.0f)
				.rotateY(angle * 1.5f);
		Matrix4 orbiterWorld = new Matrix4(orbiterLocal).multiply(cubeModel);
		renderTexturedObject(orbiterVaoId, orbiterVertexCount, orbiterWorld, projection,
				hugeTextureByMode[filterMode], tinyTextureByMode[filterMode]);

		glBindVertexArray(0);
	}

	private void renderTexturedObject(int vaoId, int vertexCount, Matrix4 model, Matrix4 projection, int texture0, int texture1) {
		Matrix4 mvp = new Matrix4(model).multiply(projection);
		float[] normalMatrix = calculateNormalMatrix3x3(model);

		glUniformMatrix4fv(mvpUniformLocation, false, mvp.getValuesAsArray());
		glUniformMatrix4fv(modelUniformLocation, false, model.getValuesAsArray());
		glUniformMatrix3fv(normalUniformLocation, false, normalMatrix);

		// Mehrfachtexturierung: zwei Sampler in zwei Textureinheiten.
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture0);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, texture1);

		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}
}
