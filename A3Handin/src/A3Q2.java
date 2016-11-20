package A3Q2;

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

public class A3Q2 implements GLEventListener, KeyListener {
	public static final boolean TRACE = false;

	public static final String WINDOW_TITLE = "A3Q2: [Ryan Reid]";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 640;
    private static float xMod = 0;
    private static double near = 0;
    private static double far = 0;
    private static double rotate = 0.01;

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
        Structure leftLeg = new Structure(new Shape[] { }, new float[][] {});
        Structure rightLeg = new Structure(new Shape[] { }, new float[][] {});

        Structure torso = new Structure(new Shape[] {}, new float[][] {});
        torso.vertices.forEach(vert -> vert[0] *= 2.5);

        Structure head = new Structure(new Shape[] {}, new float[][] {});
        head.vertices.forEach(vert -> vert[1] *= .5);

        Structure rightArm = new Structure(new Shape[] {}, new float[][] {});

        Structure leftArm = new Structure(new Shape[] {}, new float[][] {});

        robot = new Structure(new Shape[] {leftLeg, rightLeg, torso, head, rightArm, leftArm}, new float[][] {{0f, 0f, 0f}, {.75f, 0f, 0f}, {.375f, 1.1f, 0f}, {.375f, 1.95f, 0f}, {1.35f, 1.5f, 0f}, {-.6f, 1.5f, 0}});
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

		if (viewChanged) {
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();

            if (0 == projection) {
                gl.glOrthof(-3, 3, -3, 3, -10.0f, 30.0f);
            } else {
                gl.glFrustumf(-1, 1, -1, 1, 1.0f, 30.0f);
            }

			gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glTranslatef(0.0f, -0.5f, -2f - 0.3f * cameraAngle);

            if(cameraAngle == 0) {
                gl.glRotatef(70, 1.0f, 0.0f, 0.0f);
            } else if(cameraAngle == 1) {
                gl.glRotatef(30, 1f, 0f, 0.0f);
            } else if(cameraAngle == 2) {
                gl.glRotatef(60, 0f, 1.0f, 0f);
                gl.glRotatef(20, 0f, 1.0f, 1f);
            }

			gl.glScalef(0.5f, 0.5f, 0.5f);

            viewChanged = false;
		}

		xMod += (Math.random() * .05);

        if(xMod > 10) {
            xMod = -5;
        }

        rotate += .01;

        gl.glPushMatrix();
        drawFloor(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
		robot.draw(gl, xMod, rotate);
        gl.glPopMatrix();

	}

	private void drawFloor(GL2 gl) {
        gl.glRotatef(90, 0, 1 , 0);
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glColor3f(0.2f, 0.1f, 0.1f);

        gl.glVertex3f(500, -1, -2);
        gl.glVertex3f(-500, -1, -2);
        gl.glVertex3f(-500, -1, 2);

        gl.glColor3f(0.1f, 0.2f, 0.1f);

        gl.glVertex3f(500, -1, -2);
        gl.glVertex3f(-500, -1, 2);
        gl.glVertex3f(500,  -1, 2);

        gl.glEnd();
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

class Shape {
	// set this to NULL if you don't want outlines
	public float[] line_colour;

	protected ArrayList<float[]> vertices;
	protected ArrayList<Face> faces;

	public Shape() {
		// you could subclass Shape and override this with your own
		init();

		// default shape: cube
		vertices.add(new float[] { -.25f, -.5f, .1f });
		vertices.add(new float[] { .25f, -.5f, .1f });
		vertices.add(new float[] { .25f, .5f, .1f });
		vertices.add(new float[] { -.25f, .5f, .1f });
		vertices.add(new float[] { -.25f, -.5f, -.1f });
		vertices.add(new float[] { .25f, -.5f, -.1f });
		vertices.add(new float[] { .25f, .5f, -.1f });
		vertices.add(new float[] { -.25f, .5f, -.1f });

		faces.add(new Face(new int[] { 0, 1, 2, 3 }, new float[] { 1.0f, 0.0f, 0.0f } ));
		faces.add(new Face(new int[] { 0, 3, 7, 4 }, new float[] { 1.0f, 1.0f, 0.0f } ));
		faces.add(new Face(new int[] { 7, 6, 5, 4 }, new float[] { 1.0f, 1.0f, 1.0f } ));
		faces.add(new Face(new int[] { 2, 1, 5, 6 }, new float[] { 0.0f, 1.0f, 0.0f } ));
		faces.add(new Face(new int[] { 3, 2, 6, 7 }, new float[] { 0.0f, 0.0f, 1.0f } ));
		faces.add(new Face(new int[] { 1, 0, 4, 5 }, new float[] { 0.0f, 1.0f, 1.0f } ));
	}

	public Shape(String filename) {
		init();

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

	protected void init() {
		vertices = new ArrayList<float[]>();
		faces = new ArrayList<Face>();
		
		line_colour = new float[] { 1,1,1 };
	}
	
	public void draw(GL2 gl) {
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
	}
}

// TODO: rewrite the following as you like
class Structure extends Shape {
	// this array can include other structures...
	public Shape[] contents;
    public boolean roateUp;
	private float[][] positions;
	
	public Structure(Shape[] contents, float[][] positions) {
		//super();
		init(contents, positions);
	}
	
	public Structure(String filename, Shape[] contents, float[][] positions) {
		super(filename);
		init(contents, positions);
	}
	
	private void init(Shape[] contents, float[][] positions) {
		this.contents = new Shape[contents.length];
		this.positions = new float[positions.length][3];
		System.arraycopy(contents, 0, this.contents, 0, contents.length);
		for (int i = 0; i < positions.length; i++) {
			System.arraycopy(positions[i], 0, this.positions[i], 0, 3);
		}

		roateUp = false;
	}

	public void draw(GL2 gl, float zMod, double rotate) {
        float angle;
        gl.glTranslatef(0, 0, zMod);
		for (int i = 0; i < contents.length; i++) {
			gl.glPushMatrix();

            if(i == 0) {

                if(roateUp) {
                    angle = (float) (45 * rotate) % 45;

                    if(angle == 45) {
                        roateUp = false;
                    }
                } else {
                    angle = (float) (45 * rotate);
                    if(angle <= 0.5) {
                        angle = 45;
                        roateUp = true;
                    }
                }

                System.out.println("Angle:" + angle);
                gl.glRotatef(angle, 1, 0, 0);
            }

            if(i == 1) {
                angle = (float) (45 * rotate);

                if(angle > 45) {
                    angle = 45 - angle;
                }
                gl.glRotatef(-angle, 1, 0, 0);
            }

            if(i == 3) {
                gl.glRotatef(-(float) (180.0f * rotate), 0, 1, 0);
            }

            if(i == 4) {
                angle = (float) (45 * rotate);

                if(angle > 45) {
                    angle = 45 - angle;
                }
                gl.glRotatef(-angle, 1, 0, 0);
            }

            if(i == 5) {
                angle = (float) (45 * rotate);

                if(angle > 45) {
                    angle = 45 - angle;
                }
                gl.glRotatef(angle, 1, 0, 0);
            }

            gl.glTranslatef(positions[i][0], positions[i][1], positions[i][2]);
            contents[i].draw(gl);
            gl.glPopMatrix();
		}
	}

	private float[] getCenter(Shape shape) {
        float[] center = new float[3];
        float xMax = -500;
        float xMin = 500;
        float yMax = -500;
        float yMin = 500;
        float zMax = -500;
        float zMin = 500;


        for(int i = 0; i < shape.vertices.size(); i++) {
            if(shape.vertices.get(i)[0] > xMax) {
                xMax = shape.vertices.get(i)[0];
            }

            if(shape.vertices.get(i)[1] > yMax) {
                yMax = shape.vertices.get(i)[1];
            }

            if(shape.vertices.get(i)[2] > zMax) {
                zMax = shape.vertices.get(i)[2];
            }

            if(shape.vertices.get(i)[0] < xMin) {
                xMin = shape.vertices.get(i)[0];
            }

            if(shape.vertices.get(i)[1] < yMin) {
                yMin = shape.vertices.get(i)[1];
            }

            if(shape.vertices.get(i)[2] < zMin) {
                zMin = shape.vertices.get(i)[2];
            }
        }

        center[0] = (xMax + xMin) / 2;
        center[1] = (yMax + yMin) / 2;
        center[2] = (zMax + zMin) / 2;

        return center;
    }
}