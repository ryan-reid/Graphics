import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/*
Texture links
08.jpg - http://www.textures.com/download/brickssmallold0090/72049
floor.jpg - http://www.textures.com/download/woodfine0001/14121
wood.jpg - http://www.textures.com/download/woodbamboo0085/124322
metal.jpg - http://www.textures.com/download/metalbare0064/5350
*/

public class A4 implements GLEventListener, KeyListener {
	private static final boolean TRACE = false;

	private static final String WINDOW_TITLE = "A3Q2: [Ryan Reid]";
	private static final int INITIAL_WIDTH = 640;
	private static final int INITIAL_HEIGHT = 640;
	private static float VELOCITY = .05f;
    private static float XCOORD = 0f;
    private static boolean JUMP = false;

    private static float robotY = 0f;
    private static boolean jumping = false;

	// Name of the input file path
	private static final String TEXTURE_PATH = "resources/";

    private static final String[] TEXTURE_FILES = { "ObjectText.jpg", "08.jpg", "floor.jpg", "wood.jpg", "metal.jpg"};

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
	private int cameraAngle = 0;
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
								new Shape(new float[] {0.1f,0.15f,0.1f}, head, 4),
								new Structure(new Shape[] {
										new Shape(new float[] {0.06f, -0.125f, 0.06f}, rightElbow, 4)},
										new float[][] {{-0.058f, -0.15f, -0.001f}},
										new float[] {0.125f, 0.075f, 0.075f}, rightShoulder, 4),
								new Structure(new Shape[] {
										new Shape(new float[] {0.06f, -0.125f, 0.06f}, leftElbow, 4)},
										new float[][] {{0.058f, -0.15f, -0.001f}},
										new float[] {0.125f, 0.075f, 0.075f}, leftShoulder, 4),
								new Structure(new Shape[] {
										new Shape(new float[] {0.1f, 0.15f, 0.1f}, rightKnee, 4)},
										new float[][] {{0.0f, -0.3f, 0.0f}},
										new float[] {0.1f, 0.15f, 0.1f}, rightThigh, 4),
								new Structure(new Shape[] {
										new Shape(new float[] {0.1f, 0.15f, 0.1f}, leftKnee, 4)},
										new float[][] {{0.0f, -0.3f, 0.0f}},
										new float[] {0.1f, 0.15f, 0.1f}, leftThigh, 4)
							}, new float[][] {
								{0, 0.4f, 0 },
								{-0.25f, 0.15f, 0},
								{0.25f, 0.15f, 0},
								{-0.15f, -0.3f, 0 },
								{0.15f, -0.3f, 0 }
							},
							new float[] {0.15f,0.25f,0.15f}, null, 4);


        createTwoObjectsEveryFiveBlocks();

        //Walls
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 0}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 0},1));

        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 50}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 50},1));

        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 100}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 100},1));

        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 150}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 150},1));

        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 200}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 200},1));

        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 250}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 250},1));

        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 300}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 300},1));

        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {4, -.75f, 350}, 1));
        worldObjects.add(new Shape(new float[] {-.1f, 50f, 50}, new float[] {-4, -.75f, 350},1));

        worldObjects.add(new Shape(new float[] {4, 50f, -.1f}, new float[] {0, -.75f, 400},  1));

        //Floor
       worldObjects.add(new Shape(new float[] {4, -.1f, 100}, new float[] {0, -.75f, 0}, 2));
       worldObjects.add(new Shape(new float[] {4, -.1f, 100}, new float[] {0, -.75f, 100}, 2));
       worldObjects.add(new Shape(new float[] {4, -.1f, 100}, new float[] {0, -.75f, 200}, 2));
       worldObjects.add(new Shape(new float[] {4, -.1f, 100}, new float[] {0, -.75f, 300}, 2));
	}

	private void createTwoObjectsEveryFiveBlocks() {
        for(int i = 2; i <= 80; i++) {
            int count = 0;
            float maxX = 3.5f;
            float minX = -3.5f;

            int maxZ = i * 5;
            int minZ = (i - 1) * 5;


            while(count < 1) {
                Random random = new Random();
                float xCoord =  (random.nextFloat() * ((maxX - minX) + minX)) * (random.nextBoolean() ? -1 : 1);
                random = new Random();
                float zCoord = random.nextFloat() * ( maxZ - minZ) + minZ;

                if(zCoord - 1 > 0) {
                    worldObjects.add(new Shape(new float[] {1, 2f, 1}, new float[] {xCoord, -.75f, zCoord}, 3));
                    count++;
                }
            }
        }
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

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_CULL_FACE);

       gl.glEnable(GL2.GL_FOG);
       gl.glFogfv(GL2.GL_FOG_COLOR, new float[] {0,0,0}, 0);
       gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
       gl.glFogf(GL2.GL_FOG_DENSITY, .3f);

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

        if(JUMP) {
            makeRobotJump();
        }

        if(robotCollided()) {
            resetRobot();
        }

        setCameraAngle(gl);

        drawRobot(gl);

        drawWorldObjects(gl);

		for (Rotator r: rotators) {
			r.update(1f);
		}
	}

	private void makeRobotJump() {
        if(jumping) {
            robotY += .05f;
        } else {
            robotY -= .05f;
        }

        if(robotY >= 5) {
            jumping = false;
        } else if(robotY <= 0) {
            robotY = 0;
            JUMP = false;
        }
    }

	private void resetRobot() {
        robotZ = 0;
        XCOORD = 0;
        robotY = 0;
        jumping = false;
        JUMP = false;
        VELOCITY = 0.05f;
    }

    private void setCameraAngle(GL2 gl) {
        if (1 == cameraAngle) {
            gl.glTranslatef(0.0f, -0.9f, -2.3f);
            gl.glRotatef(-180, 0f, 1f, 0f);
            gl.glRotatef(-15, 1f, 0f, 0f);
        } else {
            gl.glTranslatef(0.0f, -0.9f, 0);
            gl.glRotatef(-180, 0f, 1f, 0f);
        }


        gl.glTranslatef(-XCOORD, -robotY, -robotZ);

        gl.glPushMatrix();
    }

    private float[][] converToMultiDemArray(float[] vertices) {
        return new float[][] { {vertices[0]}, {vertices[1]}, {vertices[2]}, {1}};
    }

    private float[][] transformPoints(float[][] transformation, float[] vertices) {
        float[][] point = converToMultiDemArray(vertices);

        return transformPoints(transformation, point);
    }

    private float[][] transformPoints(float[][] transformation, float[][] point) {
        float[][] result = new float[4][1];

        for (int k = 0; k < point[0].length; k++) {
            for (int e = 0; e < 4; e++) {
                float sum = 0;
                for (int f = 0; f < point.length; f++) {
                    sum += transformation[e][f] * point[f][k];
                }
                result[e][k] = sum;
            }
        }

        return result;
    }

    private float[][] multiply(float[][] a, float[][] b) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 4; k++)
                    result[i][j] += a[i][k] * b[k][j];

        return result;
    }

    private float[][] rotateMatrix(float theta) {
        return new float[][]{{(float) Math.cos(theta), 0, -(float) Math.sin(theta), 0f}, {0, 1, 0, 0}, {((float) Math.sin(theta)), 0, (float) Math.cos(theta), 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
    }

    private float[][] scaleMatrix(float scaleX, float scaleY, float scaleZ) {
        return new float[][] { {scaleX, 0, 0, 0}, {0, scaleY, 0, 0}, {0, 0, scaleZ, 0}, {0, 0, 0, 1}};
    }

    private float[][] translationMatrix(float transX, float transY, float transZ) {
        return new float[][] { {1, 0, 0, transX}, {0, 1, 0, transY}, {0, 0, 1, transZ}, {0, 0, 0, 1}};
    }

	private boolean robotCollided() {
        ArrayList<float[]> boundingBoxes = new ArrayList<>();

        for (Shape content : robot.contents) {
            boundingBoxes.addAll(content.getBoundingBox(true));
        }

        for(Shape object : worldObjects) {
            for(float[] box : object.getBoundingBox(false)) {
                for(float[] roboBox : boundingBoxes) {
                    if(roboBox[0] <= box[1] && roboBox[1] >= box[0]) {
                        if(roboBox[2] <= box[3] && roboBox[3] >= box[2]) {
                            if(roboBox[4] <= box[5] && roboBox[5] >= box[4]) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

	private void drawWorldObjects(GL2 gl) {
        worldObjects.forEach(shape ->  {
            gl.glPushMatrix();

            gl.glTranslatef(shape.xOffset, shape.yOffset, shape.zOffset);
            shape.draw(gl);
            gl.glPopMatrix();
        });
        gl.glPopMatrix();
    }

	private void updateCamera(GL2 gl) {
		gl.glViewport(0, 0, INITIAL_WIDTH, INITIAL_HEIGHT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective (70, ar, .1f, 50.0);
	}

	private void drawRobot(GL2 gl) {
        gl.glPushMatrix();

        gl.glTranslatef(XCOORD, robotY, robotZ);

		robot.draw(gl);
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
		if (e.getKeyChar() == (char) 10) {
			cameraAngle++;
			if (cameraAngle == 2) {
				cameraAngle = 0;
			}
			((GLCanvas)e.getSource()).repaint();
		} else if(e.getKeyChar() == 'w') {
			VELOCITY += .005f;
		} else if(e.getKeyChar() == 'a') {
            XCOORD += +.4f;
        } else if(e.getKeyChar() == 'd') {
            XCOORD += -.4f;
        } else if(e.getKeyChar() == ' ') {
            JUMP = true;
            jumping = true;
        }
	}

    private class Face {
		private int[] indices;
		private float[] colour;
        private int texture;

        private Face(int[] indices, float[] colour, int texture) {
			this.indices = new int[indices.length];
			this.colour = new float[colour.length];
			System.arraycopy(indices, 0, this.indices, 0, indices.length);
			System.arraycopy(colour, 0, this.colour, 0, colour.length);
            this.texture = texture;
		}

        private float[] getBoundingBox(ArrayList<float[]> vertices, float[] scale, float xOffset, float yOffset, float zOffset) {
            float minX = 500;
            float minY = 500;
            float maxX = -500;
            float maxY = -500;
            float minZ = 500;
            float maxZ = -500;

            float[] box = new float[6];
            float[][] transMatrix = translationMatrix(xOffset, yOffset, zOffset);
            float[][] scaleMatrix = scaleMatrix(scale[0], scale[1], scale[2]);
            float[][] transformMatrix = multiply(transMatrix, scaleMatrix);

            for(int i : indices) {
                float[][] vert = transformPoints(transformMatrix, vertices.get(i));
                if(vert[0][0] <= minX) {
                    minX = vert[0][0];
                }
                if(vert[0][0] >= maxX) {
                    maxX = vert[0][0];
                }
                if(vert[1][0] <= minY) {
                    minY = vert[1][0];
                }
                if(vert[1][0] >= maxY) {
                    maxY = vert[1][0];
                }
                if(vert[2][0] <= minZ) {
                    minZ = vert[2][0];
                }
                if(vert[2][0] >= maxZ) {
                    maxZ = vert[2][0];
                }
            }

            box[0] = minX;
            box[1] = maxX;
            box[2] = minY;
            box[3] = maxY;
            box[4] = minZ;
            box[5] = maxZ;

            return box;
        }

        private void draw(GL2 gl, ArrayList<float[]> vertices, boolean useColour) {
			if (useColour && texture == -1) {
				if (colour.length == 3)
					gl.glColor3f(colour[0], colour[1], colour[2]);
				else
					gl.glColor4f(colour[0], colour[1], colour[2], colour[3]);
			}


           if(texture != -1 && texture < textures.length) {
               textures[texture].bind(gl);
               textures[texture].enable(gl);

               gl.glBegin(GL2.GL_QUADS);

               gl.glTexCoord2f(1, 1);
               gl.glVertex3f(vertices.get(indices[0])[0], vertices.get(indices[0])[1], vertices.get(indices[0])[2]);
               gl.glTexCoord2f(0, 1);
               gl.glVertex3f(vertices.get(indices[1])[0], vertices.get(indices[1])[1], vertices.get(indices[1])[2]);
               gl.glTexCoord2f(0, 0);
               gl.glVertex3f(vertices.get(indices[2])[0], vertices.get(indices[2])[1], vertices.get(indices[2])[2]);
               gl.glTexCoord2f(1, 0);
               gl.glVertex3f(vertices.get(indices[3])[0], vertices.get(indices[3])[1], vertices.get(indices[3])[2]);

               textures[texture].disable(gl);
            } else {
               gl.glBegin(GL2.GL_QUADS);
                for(int i : indices) {
                    gl.glVertex3f(vertices.get(i)[0], vertices.get(i)[1], vertices.get(i)[2]);
                }
            }


			gl.glEnd();
		}
	}

	class Shape {
		// set this to NULL if you don't want outlines
        private float[] line_colour;

        private ArrayList<float[]> vertices;
        private ArrayList<Face> faces;
		
		private float[] scale;
		private Rotator rotator;

        private float xOffset;
        private float zOffset;
        private float yOffset;

        private Shape(float[] scale, Rotator rotator, int texture) {
			// you could subclass Shape and override this with your own
			init(scale, rotator);

			// default shape: cube
            addVerticesAndFaces(texture);

		}

        private Shape(float[] scale, float[] offset, int texture) {
            init(scale, null);
            addVerticesAndFaces(texture);
            xOffset = offset[0];
            yOffset = offset[1];
            zOffset = offset[2];
        }

        private ArrayList<float[]> getBoundingBox(boolean isRobot) {
            ArrayList<float[]> boundingBoxes = new ArrayList<>();

            if(isRobot) {
                xOffset = XCOORD;
                zOffset = robotZ;
                yOffset = robotY;
            }
            faces.forEach(face -> boundingBoxes.add(face.getBoundingBox(vertices, scale, xOffset, yOffset, zOffset)));

            return boundingBoxes;
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

        private void init(float[] scale, Rotator rotator) {
			vertices = new ArrayList<>();
			faces = new ArrayList<>();

			line_colour = new float[] { 1,1,1 };
			if (null == scale) {
				this.scale = new float[] { 1,1,1 };
			} else {
				this.scale = new float[] { scale[0], scale[1], scale[2] };
			}
			
			this.rotator = rotator;
		}

        private void rotate(GL2 gl) {
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

    private class Structure extends Shape {
		// this array can include other structures...
		private Shape[] contents;
		private float[][] positions;

        private Structure(Shape[] contents, float[][] positions, float[] scale, Rotator rotator, int texture) {
			super(scale, rotator, texture);
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

    private class Rotator {
        private float[] origin;
        private float[] axis;
        private float angle, startAngle, endAngle, vAngle;
		boolean up;

        private Rotator(float[] origin, float[] axis, float angle, float startAngle, float endAngle, float vAngle) {
			this.origin = new float[] {origin[0], origin[1], origin[2]};
			this.axis = new float[] {axis[0], axis[1], axis[2]};
			this.angle = angle;
			this.startAngle = startAngle;
			this.endAngle = endAngle;
			this.vAngle = vAngle;
			this.up = true;
		}

        private void update(float elapsed) {
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