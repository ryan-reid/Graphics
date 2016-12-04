import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.glu.*;

public class A3Q2Solution implements GLEventListener, KeyListener {
	public static final boolean TRACE = false;

	public static final String WINDOW_TITLE = "A3Q2: [your name here]";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 640;

	// Name of the input file path
	public static final String INPUT_PATH_NAME = "resources/";

	private static final GLU glu = new GLU();

	public static void main(String[] args) {
		final JFrame frame = new JFrame(WINDOW_TITLE);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (TRACE)
					System.out.println("closing window '" + ((JFrame)e.getWindow()).getTitle() + "'");
				System.exit(0);
			}
		});

		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		final GLCapabilities capabilities = new GLCapabilities(profile);
		final GLCanvas canvas = new GLCanvas(capabilities);
		try {
			Object self = self().getConstructor().newInstance();
			self.getClass().getMethod("setup", new Class[] { GLCanvas.class }).invoke(self, canvas);
			canvas.addGLEventListener((GLEventListener)self);
			canvas.addKeyListener((KeyListener)self);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		canvas.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);

		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);

		canvas.requestFocusInWindow();

		if (TRACE)
			System.out.println("-> end of main().");
	}

	private static Class<?> self() {
		// This ugly hack gives us the containing class of a static method 
		return new Object() { }.getClass().getEnclosingClass();
	}

	/*** Instance variables and methods ***/

	private float ar;
	private int projection = 0;
	private int cameraAngle = 0;
	private boolean viewChanged = true;

	// TODO: Add instance variables here

	private Structure robot;
	private Rotator[] rotators;
	private float robotZ;

	public void setup(final GLCanvas canvas) {
		// Called for one-time setup
		if (TRACE)
			System.out.println("-> executing setup()");

		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				canvas.repaint();
			}
		}, 1000, 1000/60);

		// TODO: Add/modify code here
		Rotator head = new Rotator(new float[]{0,0,0}, new float[]{0,1,0}, 0, -30, 30, 1);
		Rotator rightShoulder = new Rotator(new float[]{0,0,0}, new float[]{1,0,0}, 30, -30, 30, 1);
		Rotator rightElbow = new Rotator(new float[]{0,0.075f,0}, new float[]{1,0,0}, 30, -30, 30, 1);
		Rotator leftShoulder = new Rotator(new float[]{0,0,0}, new float[]{1,0,0}, -30, -30, 30, 1);
		Rotator leftElbow = new Rotator(new float[]{0,0.075f,0}, new float[]{1,0,0}, -30, -30, 30, 1);
		Rotator rightThigh = new Rotator(new float[]{0,0.15f,0}, new float[]{1,0,0}, -30, -30, 30, 1);
		Rotator rightKnee = new Rotator(new float[]{0,0.15f,0}, new float[]{1,0,0}, 0, 0, 45, 0.75f);
		Rotator leftThigh = new Rotator(new float[]{0,0.15f,0}, new float[]{1,0,0}, 30, -30, 30, 1);
		Rotator leftKnee = new Rotator(new float[]{0,0.15f,0}, new float[]{1,0,0}, 45, 0, 45, 0.75f);
		rotators = new Rotator[] {
			head,
			rightShoulder,
			rightElbow,
			leftShoulder,
			leftElbow,
			rightThigh,
			rightKnee,
			leftThigh,
			leftKnee
		};
		robot = new Structure(
							new Shape[] {
								new Shape(new float[] {0.1f,0.15f,0.1f}, head),
//								new Shape(INPUT_PATH_NAME + "dodecahedron.obj", new float[] {0.2f,0.2f,0.2f}, head),
								new Structure(new Shape[] {
										new Shape(new float[] {0.06f, -0.125f, 0.06f}, rightElbow)},
										new float[][] {{-0.058f, -0.15f, -0.001f}},
										new float[] {0.125f, 0.075f, 0.075f}, rightShoulder),
								new Structure(new Shape[] {
										new Shape(new float[] {0.06f, -0.125f, 0.06f}, leftElbow)},
										new float[][] {{0.058f, -0.15f, -0.001f}},
										new float[] {0.125f, 0.075f, 0.075f}, leftShoulder),
								new Structure(new Shape[] {
										new Shape(new float[] {0.1f, 0.15f, 0.1f}, rightKnee)},
										new float[][] {{0.0f, -0.3f, 0.0f}},
										new float[] {0.1f, 0.15f, 0.1f}, rightThigh),
								new Structure(new Shape[] {
										new Shape(new float[] {0.1f, 0.15f, 0.1f}, leftKnee)},
										new float[][] {{0.0f, -0.3f, 0.0f}},
										new float[] {0.1f, 0.15f, 0.1f}, leftThigh)
							}, new float[][] {
								{0, 0.4f, 0 },
								{-0.25f, 0.15f, 0},
								{0.25f, 0.15f, 0},
								{-0.15f, -0.3f, 0 },
								{0.15f, -0.3f, 0 }
							},
							new float[] {0.15f,0.25f,0.15f}, null);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Called when the canvas is (re-)created - use it for initial GL setup
		if (TRACE)
			System.out.println("-> executing init()");

		final GL2 gl = drawable.getGL().getGL2();

		// TODO: Add code here
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Draws the display
		if (TRACE)
			System.out.println("-> executing display()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glViewport(0, 0, INITIAL_WIDTH, INITIAL_HEIGHT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective (110, ar, 1.0f, 50.0);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		// TODO: choose view based on "cameraAngle" (don't use this!)
		if (0 == cameraAngle) {
			gl.glTranslatef(0.0f, -0.9f, -1.3f + robotZ);
			gl.glRotatef(-180, 0f, 1f, 0f);
		} else {
			gl.glTranslatef(0.0f, -0.9f, robotZ);
			gl.glRotatef(-180, 0f, 1f, 0f);
		}

		robotZ += 0.015f;

		gl.glPushMatrix();
		gl.glTranslatef(0, 0, robotZ);
		robot.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0, -0.75f, 0);
		boolean dark = true;
		final float SQSIZE = 0.25f;
		for (float x = -2; x < 2; x+=SQSIZE) {
			for (float z = -2; z < 2; z+=SQSIZE) {
				if (dark) {
					gl.glColor3f(0.2f, 0.2f, 0.2f);
				} else {
					gl.glColor3f(0.5f, 0.5f, 0.5f);
				}
				dark = !dark;
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3f(x, 0, z);
				gl.glVertex3f(x+SQSIZE, 0, z);
				gl.glVertex3f(x+SQSIZE, 0, z+SQSIZE);
				gl.glVertex3f(x, 0, z+SQSIZE);
				gl.glEnd();
			}
			dark = !dark;
		}
		gl.glPopMatrix();
		
		for (Rotator r: rotators) {
			r.update(1);
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// Called when the canvas is destroyed (reverse anything from init) 
		if (TRACE)
			System.out.println("-> executing dispose()");
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// Called when the canvas has been resized
		// Note: glViewport(x, y, width, height) has already been called so don't bother if that's what you want
		if (TRACE)
			System.out.println("-> executing reshape(" + x + ", " + y + ", " + width + ", " + height + ")");

		final GL2 gl = drawable.getGL().getGL2();
		ar = (float)width / (height == 0 ? 1 : height);
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyChar() == ' ') {
			cameraAngle++;
			if (cameraAngle == 3) {
				cameraAngle = 0;
				projection = (projection + 1) % 2;
			}
			System.out.println("Pressed space: camera = " + cameraAngle + ", projection = " + projection);
			viewChanged = true;
			((GLCanvas)e.getSource()).repaint();
		}
	}

	class Face {
		private int[] indices;
		private float[] colour;

		public Face(int[] indices, float[] colour) {
			this.indices = new int[indices.length];
			this.colour = new float[colour.length];
			System.arraycopy(indices, 0, this.indices, 0, indices.length);
			System.arraycopy(colour, 0, this.colour, 0, colour.length);
		}

		public void draw(GL2 gl, ArrayList<float[]> vertices, boolean useColour) {
			if (useColour) {
				if (colour.length == 3)
					gl.glColor3f(colour[0], colour[1], colour[2]);
				else
					gl.glColor4f(colour[0], colour[1], colour[2], colour[3]);
			}

			if (indices.length == 1) {
				gl.glBegin(GL2.GL_POINTS);
			} else if (indices.length == 2) {
				gl.glBegin(GL2.GL_LINES);
			} else if (indices.length == 3) {
				gl.glBegin(GL2.GL_TRIANGLES);
			} else if (indices.length == 4) {
				gl.glBegin(GL2.GL_QUADS);
			} else {
				gl.glBegin(GL2.GL_POLYGON);
			}

			for (int i: indices) {
				gl.glVertex3f(vertices.get(i)[0], vertices.get(i)[1], vertices.get(i)[2]);
			}

			gl.glEnd();
		}
	}

	// TODO: rewrite the following as you like
	class Shape {
		// set this to NULL if you don't want outlines
		public float[] line_colour;

		protected ArrayList<float[]> vertices;
		protected ArrayList<Face> faces;
		
		private float[] scale;
		private Rotator rotator;

		public Shape(float[] scale, Rotator rotator) {
			// you could subclass Shape and override this with your own
			init(scale, rotator);

			// default shape: cube
			vertices.add(new float[] { -1.0f, -1.0f, 1.0f });
			vertices.add(new float[] { 1.0f, -1.0f, 1.0f });
			vertices.add(new float[] { 1.0f, 1.0f, 1.0f });
			vertices.add(new float[] { -1.0f, 1.0f, 1.0f });
			vertices.add(new float[] { -1.0f, -1.0f, -1.0f });
			vertices.add(new float[] { 1.0f, -1.0f, -1.0f });
			vertices.add(new float[] { 1.0f, 1.0f, -1.0f });
			vertices.add(new float[] { -1.0f, 1.0f, -1.0f });

			faces.add(new Face(new int[] { 0, 1, 2, 3 }, new float[] { 1.0f, 0.0f, 0.0f } ));
			faces.add(new Face(new int[] { 0, 3, 7, 4 }, new float[] { 1.0f, 1.0f, 0.0f } ));
			faces.add(new Face(new int[] { 7, 6, 5, 4 }, new float[] { 1.0f, 0.0f, 1.0f } ));
			faces.add(new Face(new int[] { 2, 1, 5, 6 }, new float[] { 0.0f, 1.0f, 0.0f } ));
			faces.add(new Face(new int[] { 3, 2, 6, 7 }, new float[] { 0.0f, 0.0f, 1.0f } ));
			faces.add(new Face(new int[] { 1, 0, 4, 5 }, new float[] { 0.0f, 1.0f, 1.0f } ));
		}

		public Shape(String filename, float[] scale, Rotator rotator) {
			init(scale, rotator);

			// TODO Use as you like
			// NOTE that there is limited error checking, to make this as flexible as possible
			BufferedReader input;
			String line;
			String[] tokens;

			float[] vertex;
			float[] colour;
			String specifyingMaterial = null;
			String selectedMaterial;
			int[] face;

			HashMap<String, float[]> materials = new HashMap<String, float[]>();
			materials.put("default", new float[] {1,1,1});
			selectedMaterial = "default";

			// vertex positions start at 1
			vertices.add(new float[] {0,0,0});

			int currentColourIndex = 0;

			// these are for error checking (which you don't need to do)
			int lineCount = 0;
			int vertexCount = 0, colourCount = 0, faceCount = 0;

			try {
				input = new BufferedReader(new FileReader(filename));

				line = input.readLine();
				while (line != null) {
					lineCount++;
					tokens = line.split("\\s+");

					if (tokens[0].equals("v")) {
						assert tokens.length == 4 : "Invalid vertex specification (line " + lineCount + "): " + line;

						vertex = new float[3];
						try {
							vertex[0] = Float.parseFloat(tokens[1]);
							vertex[1] = Float.parseFloat(tokens[2]);
							vertex[2] = Float.parseFloat(tokens[3]);
						} catch (NumberFormatException nfe) {
							assert false : "Invalid vertex coordinate (line " + lineCount + "): " + line;
						}

						System.out.printf("vertex %d: (%f, %f, %f)\n", vertexCount + 1, vertex[0], vertex[1], vertex[2]);
						vertices.add(vertex);

						vertexCount++;
					} else if (tokens[0].equals("newmtl")) {
						assert tokens.length == 2 : "Invalid material name (line " + lineCount + "): " + line;
						specifyingMaterial = tokens[1];
					} else if (tokens[0].equals("Kd")) {
						assert tokens.length == 4 : "Invalid colour specification (line " + lineCount + "): " + line;
						assert faceCount == 0 && currentColourIndex == 0 : "Unexpected (late) colour (line " + lineCount + "): " + line;

						colour = new float[3];
						try {
							colour[0] = Float.parseFloat(tokens[1]);
							colour[1] = Float.parseFloat(tokens[2]);
							colour[2] = Float.parseFloat(tokens[3]);
						} catch (NumberFormatException nfe) {
							assert false : "Invalid colour value (line " + lineCount + "): " + line;
						}
						for (float colourValue: colour) {
							assert colourValue >= 0.0f && colourValue <= 1.0f : "Colour value out of range (line " + lineCount + "): " + line;
						}

						if (specifyingMaterial == null) {
							System.out.printf("Error: no material name for colour %d: (%f %f %f)\n", colourCount + 1, colour[0], colour[1], colour[2]);
						} else {
							System.out.printf("material %s: (%f %f %f)\n", specifyingMaterial, colour[0], colour[1], colour[2]);
							materials.put(specifyingMaterial, colour);
						}

						colourCount++;
					} else if (tokens[0].equals("usemtl")) {
						assert tokens.length == 2 : "Invalid material selection (line " + lineCount + "): " + line;

						selectedMaterial = tokens[1];
					} else if (tokens[0].equals("f")) {
						assert tokens.length > 1 : "Invalid face specification (line " + lineCount + "): " + line;

						face = new int[tokens.length - 1];
						try {
							for (int i = 1; i < tokens.length; i++) {
								face[i - 1] = Integer.parseInt(tokens[i].split("/")[0]);
							}
						} catch (NumberFormatException nfe) {
							assert false : "Invalid vertex index (line " + lineCount + "): " + line;
						}

						System.out.printf("face %d: [ ", faceCount + 1);
						for (int index: face) {
							System.out.printf("%d ", index);
						}
						System.out.printf("] using material %s\n", selectedMaterial);

						colour = materials.get(selectedMaterial);
						if (colour == null) {
							System.out.println("Error: material " + selectedMaterial + " not found, using default.");
							colour = materials.get("default");
						}
						faces.add(new Face(face, colour));

						faceCount++;
					} else {
						System.out.println("Ignoring: " + line);
					}

					line = input.readLine();
				}
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
				assert false : "Error reading input file " + filename;
			}
		}

		protected void init(float[] scale, Rotator rotator) {
			vertices = new ArrayList<float[]>();
			faces = new ArrayList<Face>();

			line_colour = new float[] { 1,1,1 };
			if (null == scale) {
				this.scale = new float[] { 1,1,1 };
			} else {
				this.scale = new float[] { scale[0], scale[1], scale[2] };
			}
			
			this.rotator = rotator;
		}

		public void rotate(GL2 gl) {
			if (rotator != null) {
				gl.glTranslatef(rotator.origin[0], rotator.origin[1], rotator.origin[2]);
				gl.glRotatef(rotator.angle, rotator.axis[0], rotator.axis[1], rotator.axis[2]);
				gl.glTranslatef(-rotator.origin[0], -rotator.origin[1], -rotator.origin[2]);
			}
		}
		
		public void draw(GL2 gl) {
			gl.glPushMatrix();
			gl.glScalef(scale[0], scale[1], scale[2]);
			for (Face f: faces) {
				if (line_colour == null) {
					f.draw(gl, vertices, true);
				} else {
					gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
					gl.glPolygonOffset(1.0f, 1.0f);
					f.draw(gl, vertices, true);
					gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

					gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
					gl.glLineWidth(2.0f);
					gl.glColor3f(line_colour[0], line_colour[1], line_colour[2]);
					f.draw(gl, vertices, false);
					gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
				}
			}
			gl.glPopMatrix();
		}
	}

	// TODO: rewrite the following as you like
	class Structure extends Shape {
		// this array can include other structures...
		private Shape[] contents;
		private float[][] positions;

		public Structure(Shape[] contents, float[][] positions, float[] scale, Rotator rotator) {
			super(scale, rotator);
			init(contents, positions);
		}

		public Structure(String filename, Shape[] contents, float[][] positions, float[] scale, Rotator rotator) {
			super(filename, scale, rotator);
			init(contents, positions);
		}

		private void init(Shape[] contents, float[][] positions) {
			this.contents = new Shape[contents.length];
			this.positions = new float[positions.length][3];
			System.arraycopy(contents, 0, this.contents, 0, contents.length);
			for (int i = 0; i < positions.length; i++) {
				System.arraycopy(positions[i], 0, this.positions[i], 0, 3);
			}
		}

		public void draw(GL2 gl) {
			super.draw(gl);
			for (int i = 0; i < contents.length; i++) {
				gl.glPushMatrix();
				gl.glTranslatef(positions[i][0], positions[i][1], positions[i][2]);
				contents[i].rotate(gl);
				contents[i].draw(gl);
				gl.glPopMatrix();
			}
		}
	}
	
	class Rotator {
		public float[] origin;
		public float[] axis;
		public float angle, startAngle, endAngle, vAngle;
		boolean up;
		
		public Rotator(float[] origin, float[] axis, float angle, float startAngle, float endAngle, float vAngle) {
			this.origin = new float[] {origin[0], origin[1], origin[2]};
			this.axis = new float[] {axis[0], axis[1], axis[2]};
			this.angle = angle;
			this.startAngle = startAngle;
			this.endAngle = endAngle;
			this.vAngle = vAngle;
			this.up = true;
		}
		
		public void update(float elapsed) {
			if (up) {
				angle += elapsed * vAngle;
				if (angle > endAngle) {
					angle = endAngle - Math.abs(angle - endAngle);
					up = false;
				}
			} else {
				angle -= elapsed * vAngle;
				if (angle < startAngle) {
					angle = startAngle + Math.abs(angle - startAngle);
					up = true;
				}
			}
		}
	}
}