import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class A3Q2Solution implements GLEventListener, KeyListener {
	public static final boolean TRACE = false;

	public static final String WINDOW_TITLE = "A3Q2: [Ryan Reid]";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 640;
	public static float VELOCITY = .015f;
	public static float ROTATION = 0f;
    private static float XCOORD = 0f;

	// Name of the input file path
	private static final String TEXTURE_PATH = "resources/";

	public static final String[] TEXTURE_FILES = { "circle.png", "08.jpg", "floor.jpg", "Wood.jpg" };

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

	private String direction;
	private Texture[] textures;

	private Structure robot;
	private Rotator[] rotators;
	private float robotZ;
    private ArrayList<Shape> worldObjects = new ArrayList<>();

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

        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {16, 20} , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {16, 18} , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {5, 1}   , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {0, 22}  , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {3, -14} , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {21, -20}, -1    ));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {7, -8}  , -1    ));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {7, 3}   , -1    ));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {0, 12}  , -1    ));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {-18, 18}, -1    ));

        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {-2, 9}      , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {-8, 0}      , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {-12, -5}    , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {-12, -8}    , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {-17, -2}    , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 4f, (float) Math.random() * 2}, new float[] {-19, 16}    , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {4, -4}      , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {16, -9}     , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {12, -11}    , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {9, -19}     , -1));
        worldObjects.add(new Shape(new float[] {(float) Math.random() * 2, 2f, (float) Math.random() * 2}, new float[] {-15, -3}    , -1));


        //Walls
        worldObjects.add(new Shape(new float[] {22, 50f, 1f}, new float[] {0, 22}, 3));
        worldObjects.add(new Shape(new float[] {22, 50f, 1f}, new float[] {0, -22}, 3));
        worldObjects.add(new Shape(new float[] {1f, 50f, 22f}, new float[] {22, 0}, 3));
        worldObjects.add(new Shape(new float[] {1f, 50f, 22f}, new float[] {-22, 0}, 3));

        //Floor
        worldObjects.add(new Shape(new float[] {22f, -.1f, 22f}, new float[] {0, 0}, 2));
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Called when the canvas is (re-)created - use it for initial GL setup
		if (TRACE)
			System.out.println("-> executing init()");

		final GL2 gl = drawable.getGL().getGL2();

		textures = new Texture[TEXTURE_FILES.length];
		try {
			for (int i = 0; i < TEXTURE_FILES.length; i++) {
				File infile = new File(TEXTURE_PATH + TEXTURE_FILES[i]);
				BufferedImage image = ImageIO.read(infile);
				ImageUtil.flipImageVertically(image);
				textures[i] = TextureIO.newTexture(AWTTextureIO.newTextureData(gl.getGLProfile(), image, false));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO: Add code here
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		//gl.glEnable(GL2.GL_CULL_FACE);

       //gl.glEnable(GL2.GL_FOG);
       //gl.glFogfv(GL2.GL_FOG_COLOR, new float[] {0,0,0}, 0);
       //gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
       //gl.glFogf(GL2.GL_FOG_DENSITY, .3f);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Draws the display
		if (TRACE)
			System.out.println("-> executing display()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		updateCamera(gl);

		robotZ += VELOCITY;

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		if (0 == cameraAngle) {
			gl.glTranslatef(0.0f + XCOORD, -0.9f, -1.3f + robotZ);
			gl.glRotatef(-180, 0f, 1f, 0f);
		} else {
			gl.glTranslatef(0.0f + XCOORD, -0.9f, robotZ);
			gl.glRotatef(-180, 0f, 1f, 0f);
		}

        float[] center = robot.getCenter();

        gl.glTranslatef(-(center[0] + XCOORD), -center[1], -(center[2] + robotZ));
        gl.glRotatef(-ROTATION, 0f, 1f, 0f);
        gl.glTranslatef((center[0] + XCOORD), center[1], (center[2] + robotZ));

		gl.glPushMatrix();

        drawWorldObjects(gl);
		drawRobot(gl);

		for (Rotator r: rotators) {
			r.update(1f);
		}
	}

	private void drawWorldObjects(GL2 gl) {
        worldObjects.forEach(shape ->  {
            gl.glPushMatrix();
            gl.glTranslatef(shape.xOffset, -.75f, shape.zOffset);
            shape.draw(gl);
            gl.glPopMatrix();
        });
    }

	private void updateCamera(GL2 gl) {
		gl.glViewport(0, 0, INITIAL_WIDTH, INITIAL_HEIGHT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective (110, ar, 1.0f, 50.0);
	}

	private void drawRobot(GL2 gl) {
        float[] center = robot.getCenter();

        gl.glTranslatef(-(center[0] + XCOORD), -center[1], -(center[2] + robotZ));
        gl.glRotatef(ROTATION, 0f, 1f, 0f);
        gl.glTranslatef((center[0] + XCOORD), center[1], (center[2] + robotZ));

		gl.glTranslatef(XCOORD, 0, robotZ);
		robot.draw(gl);
		gl.glPopMatrix();
		gl.glPopMatrix();
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
		if (e.getKeyChar() == ' ') {
			cameraAngle++;
			if (cameraAngle == 2) {
				cameraAngle = 0;
			}
			((GLCanvas)e.getSource()).repaint();
		} else if(e.getKeyChar() == 'w') {
			VELOCITY += .005f;
		} else if(e.getKeyChar() == 's') {
			VELOCITY += -.005f;
		} else if(e.getKeyChar() == 'q') {
			ROTATION += 1;
		} else if(e.getKeyChar() == 'e') {
			ROTATION += -1;
		} else if(e.getKeyChar() == 'a') {
            XCOORD += +.1f;
        } else if(e.getKeyChar() == 'd') {
            XCOORD += -.1f;
        }
	}

	class Face {
		private int[] indices;
		private float[] colour;
        private int texture;

		public Face(int[] indices, float[] colour, int texture) {
			this.indices = new int[indices.length];
			this.colour = new float[colour.length];
			System.arraycopy(indices, 0, this.indices, 0, indices.length);
			System.arraycopy(colour, 0, this.colour, 0, colour.length);
            this.texture = texture;
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

        public float xOffset;
        public float zOffset;

		public Shape(float[] scale, Rotator rotator) {
			// you could subclass Shape and override this with your own
			init(scale, rotator);

			// default shape: cube
            addVerticesAndFaces(-1);

		}

		public Shape(float[] scale, float[] offset, int texture) {
            init(scale, null);
            addVerticesAndFaces(texture);
            xOffset = offset[0];
            zOffset = offset[1];
        }

        private void addVerticesAndFaces(int texture) {
            vertices.add(new float[] { -1.0f, -1.0f, 1.0f });
            vertices.add(new float[] { 1.0f, -1.0f, 1.0f });
            vertices.add(new float[] { 1.0f, 1.0f, 1.0f });
            vertices.add(new float[] { -1.0f, 1.0f, 1.0f });
            vertices.add(new float[] { -1.0f, -1.0f, -1.0f });
            vertices.add(new float[] { 1.0f, -1.0f, -1.0f });
            vertices.add(new float[] { 1.0f, 1.0f, -1.0f });
            vertices.add(new float[] { -1.0f, 1.0f, -1.0f });

            faces.add(new Face(new int[] { 0, 1, 2, 3 }, new float[] { 1.0f, 0.0f, 0.0f } , texture));
            faces.add(new Face(new int[] { 0, 3, 7, 4 }, new float[] { 1.0f, 1.0f, 0.0f } , texture));
            faces.add(new Face(new int[] { 7, 6, 5, 4 }, new float[] { 1.0f, 0.0f, 1.0f } , texture));
            faces.add(new Face(new int[] { 2, 1, 5, 6 }, new float[] { 0.0f, 1.0f, 0.0f } , texture));
            faces.add(new Face(new int[] { 3, 2, 6, 7 }, new float[] { 0.0f, 0.0f, 1.0f } , texture));
            faces.add(new Face(new int[] { 1, 0, 4, 5 }, new float[] { 0.0f, 1.0f, 1.0f } , texture));
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

		private void init(Shape[] contents, float[][] positions) {
			this.contents = new Shape[contents.length];
			this.positions = new float[positions.length][3];
			System.arraycopy(contents, 0, this.contents, 0, contents.length);
			for (int i = 0; i < positions.length; i++) {
				System.arraycopy(positions[i], 0, this.positions[i], 0, 3);
			}
		}

		public float[] getCenter() {
            float[] center = new float[3];
            int total = 0;

            for (Shape content : contents) {
                content.vertices.forEach( vertice -> updateCenter(vertice, center));
                total += content.vertices.size();
            }

            center[0] /= total;
            center[1] /= total;
            center[2] /= total;

            return center;
        }

        private float[] updateCenter(float[] vertice, float[] center) {
            center[0] += vertice[0];
            center[1] += vertice[1];
            center[2] += vertice[2];

            return center;
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