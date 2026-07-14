package projekt;

// Alle Operationen aendern die Matrix selbst und geben sie zurueck, damit man sie verketten kann:
// Matrix4 m = new Matrix4().scale(5).translate(0,1,0).rotateX(0.5f);
public class Matrix4 {

	private float[][] matrix;

	public Matrix4() {
		// TODO mit der Identitaetsmatrix initialisieren
		matrix = new float[4][4];

		// erstellt Identitaetsmatrix
		for (int i = 0; i < 4; i++) {
			matrix[i][i] = 1.0f;
		}
	}

	public Matrix4(Matrix4 copy) {
		// TODO neues Objekt mit den Werten von "copy" initialisieren
		matrix = new float[4][4];

		for (int row = 0; row < 4; row++) {
			for (int column = 0; column < 4; column++) {
				matrix[row][column] = copy.matrix[row][column];
			}
		}
	}

	// TODO erzeugt Projektionsmatrix mit Abstand zur nahen Ebene "near" und Abstand zur fernen Ebene "far"
	// Standardwerte: quadratisches Seitenverhaeltnis und 45 Grad Sichtfeld.
	public Matrix4(float near, float far) {
		this(near, far, (float) Math.toRadians(45.0), 1.0f);
	}

	public Matrix4(float near, float far, float fovYRadians, float aspect) {
		matrix = new float[4][4];

		float f = 1.0f / (float) Math.tan(fovYRadians / 2.0f);
		float rangeInv = 1.0f / (near - far);

		// OpenGL-Perspektivprojektion
		matrix[0][0] = f / aspect;
		matrix[1][1] = f;
		matrix[2][2] = (far + near) * rangeInv;
		matrix[2][3] = 2.0f * far * near * rangeInv;
		matrix[3][2] = -1.0f; // wichtig fuer OpenGL
		matrix[3][3] = 0.0f;
	}

	// TODO hier Matrizenmultiplikation "this = other * this" einfuegen
	public Matrix4 multiply(Matrix4 other) {
		float[][] result = new float[4][4];

		for (int row = 0; row < 4; row++) {
			for (int column = 0; column < 4; column++) {
				result[row][column] = other.matrix[row][0] * matrix[0][column]
						+ other.matrix[row][1] * matrix[1][column]
						+ other.matrix[row][2] * matrix[2][column]
						+ other.matrix[row][3] * matrix[3][column];
			}
		}
		matrix = result;
		return this;
	}

	// TODO Verschiebung um x,y,z zu this hinzufuegen
	public Matrix4 translate(float x, float y, float z) {
		Matrix4 translation = new Matrix4();
		translation.matrix[0][3] = x;
		translation.matrix[1][3] = y;
		translation.matrix[2][3] = z;
		return multiply(translation);
	}

	// TODO gleichmaessige Skalierung um Faktor "uniformFactor" zu this hinzufuegen
	public Matrix4 scale(float uniformFactor) {
		return scale(uniformFactor, uniformFactor, uniformFactor);
	}

	// TODO ungleichfoermige Skalierung zu this hinzufuegen
	public Matrix4 scale(float sx, float sy, float sz) {
		Matrix4 scaling = new Matrix4();
		scaling.matrix[0][0] = sx;
		scaling.matrix[1][1] = sy;
		scaling.matrix[2][2] = sz;
		return multiply(scaling);
	}

	// TODO Rotation um X-Achse zu this hinzufuegen
	public Matrix4 rotateX(float angle) {
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);

		Matrix4 rotation = new Matrix4();
		rotation.matrix[1][1] = cos;
		rotation.matrix[1][2] = -sin;
		rotation.matrix[2][1] = sin;
		rotation.matrix[2][2] = cos;
		return multiply(rotation);
	}

	// TODO Rotation um Y-Achse zu this hinzufuegen
	public Matrix4 rotateY(float angle) {
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);

		Matrix4 rotation = new Matrix4();
		rotation.matrix[0][0] = cos;
		rotation.matrix[0][2] = sin;
		rotation.matrix[2][0] = -sin;
		rotation.matrix[2][2] = cos;
		return multiply(rotation);
	}

	// TODO Rotation um Z-Achse zu this hinzufuegen
	public Matrix4 rotateZ(float angle) {
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);

		Matrix4 rotation = new Matrix4();
		rotation.matrix[0][0] = cos;
		rotation.matrix[0][1] = -sin;
		rotation.matrix[1][0] = sin;
		rotation.matrix[1][1] = cos;
		return multiply(rotation);
	}

	// TODO hier Werte in einem Float-Array mit 16 Elementen (spaltenweise gefuellt) herausgeben
	public float[] getValuesAsArray() {
		float[] values = new float[16];
		for (int column = 0; column < 4; column++) {
			for (int row = 0; row < 4; row++) {
				values[column * 4 + row] = matrix[row][column];
			}
		}
		return values;
	}
}
