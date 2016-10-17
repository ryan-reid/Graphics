package A2Q2;

import javax.swing.*;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class A2Q2Skeleton implements GLEventListener, MouseListener, MouseMotionListener  {
	public static final boolean TRACE = true;

	public static final String WINDOW_TITLE = "A2Q2: [Your name here]"; // TODO: change
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 600;

	// Name of the input file path and scene file
	public static final String INPUT_PATH_NAME = "resources/";
	public static final String INPUT_SCENE_NAME = "in.scn";

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
			canvas.addMouseListener((MouseListener)self);
			canvas.addMouseMotionListener((MouseMotionListener)self);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		canvas.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);

		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);

		if (TRACE)
			System.out.println("-> end of main().");
	}

	private static Class<?> self() {
		// This ugly hack gives us the containing class of a static method 
		return new Object() { }.getClass().getEnclosingClass();
	}

	/*** Instance variables and methods ***/

	// TODO: Add instance variables
	
	public void setup(GLCanvas canvas) {
		// Called for one-time setup
		if (TRACE)
			System.out.println("-> executing setup()");

		// TODO: Add code here

		readScene(INPUT_PATH_NAME + INPUT_SCENE_NAME);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Called when the canvas is (re-)created - use it for initial GL setup
		if (TRACE)
			System.out.println("-> executing init()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
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
		gl.glLoadIdentity();

		// TODO: Add code here
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

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse dragged to (" + e.getX() + "," + e.getY() + ")");
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// You probably don't need this one
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse clicked on (" + e.getX() + "," + e.getY() + ")");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse pressed on (" + e.getX() + "," + e.getY() + ")");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse released on (" + e.getX() + "," + e.getY() + ")");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// You probably don't need this one
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// You probably don't need this one
	}

	private Model readModel(String filename) {
		// Use as you like
		BufferedReader input;
		String line;
		String[] tokens;
		float[] vertex;
		float[] colour;
		int[] face;

		int currentColourIndex = 0;
		Model model = new Model();

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
					assert tokens.length == 3 : "Invalid vertex specification (line " + lineCount + "): " + line;
					assert colourCount == 0 && faceCount == 0 && currentColourIndex == 0 : "Unexpected (late) vertex (line " + lineCount + "): " + line;

					vertex = new float[2];
					try {
						vertex[0] = Float.parseFloat(tokens[1]);
						vertex[1] = Float.parseFloat(tokens[2]);
					} catch (NumberFormatException nfe) {
						assert false : "Invalid vertex coordinate (line " + lineCount + "): " + line;
					}

					// TODO: process vertex array
					System.out.printf("vertex %d: (%f, %f)\n", vertexCount + 1, vertex[0], vertex[1]);

					vertexCount++;
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

					// TODO: process colour array
					System.out.printf("colour %d: (%f %f %f)\n", colourCount + 1, colour[0], colour[1], colour[2]);

					colourCount++;
				} else if (tokens[0].equals("usemtl")) {
					assert tokens.length == 2 : "Invalid material selection (line " + lineCount + "): " + line;

					try {
						currentColourIndex = Integer.parseInt(tokens[1]);
					} catch (NumberFormatException nfe) {
						assert false : "Invalid material index (line " + lineCount + "): " + line;
					}
					assert currentColourIndex >= 1 && currentColourIndex <= colourCount : "Material index out of range (line " + lineCount + "): " + line;

				} else if (tokens[0].equals("f")) {
					assert tokens.length > 1 : "Invalid face specification (line " + lineCount + "): " + line;

					face = new int[tokens.length - 1];
					try {
						for (int i = 1; i < tokens.length; i++) {
							face[i - 1] = Integer.parseInt(tokens[i]);
						}
					} catch (NumberFormatException nfe) {
						assert false : "Invalid vertex index (line " + lineCount + "): " + line;
					}
					for (int index: face) {
						assert index >= 1 && index <= vertexCount : "Vertex index out of range (line " + lineCount + "): " + line;
					}

					// TODO: process face array (uses colour @ currentColourIndex, or white if it is 0)
					System.out.printf("face %d: [ ", faceCount + 1);
					for (int index: face) {
						System.out.printf("%d ", index);
					}
					System.out.printf("] using material %d\n", currentColourIndex);

					faceCount++;
				} else {
					assert false : "Invalid token at start of line (line " + lineCount + "): " + line;
				}

				line = input.readLine();
			}
			
			input.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			assert false : "Error reading input file " + filename;
			return null;
		}
		
		return model;
	}
	
	private void readScene(String filename) {
		// Use as you like
		BufferedReader input;
		String line;
		String[] tokens;
		String modelFilename = null;
		Model model = null;

		// these are for error checking (which you don't need to do)
		int lineCount = 0;

		try {
			input = new BufferedReader(new FileReader(filename));

			line = input.readLine();
			while (line != null) {
				lineCount++;
				
				if (line.length() == 0) {
					modelFilename = null;

				} else if (modelFilename == null) {
					modelFilename = line;
					System.out.println("*** Reading model " + modelFilename);
					model = readModel(INPUT_PATH_NAME + modelFilename);

					if (model == null) {
						modelFilename = null;
					} else {
						modelFilename = line;
						// TODO: You may want to do something with "model" here
					}

				} else {
					tokens = line.split("\\s+");

					if (tokens.length != 5) {
						assert false : "Invalid instance line (line " + lineCount + "): " + line;
					} else {
						try {
							int[] sceneData = new int[tokens.length];
							for (int i = 0; i < tokens.length; i++) {
								sceneData[i] = Integer.parseInt(tokens[i]);
							}
							
							if (model == null) {
								assert false : "Instance without model (line " + lineCount + "): " + line;
							} else {	
								
								// TODO: process scene data for current model [x y rotation scaleX scaleY]
								System.out.println("* Adding instance at (" + sceneData[0] + "," + sceneData[1] + ") rotation " + sceneData[2] + " scale [" + sceneData[3] + " " + sceneData[4] + "]");
							}
						} catch (NumberFormatException nfe) {
							assert false : "Invalid instance line (line " + lineCount + "): " + line;
						}
					}
				}

				line = input.readLine();
			}

			input.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			assert false : "Error reading input file " + filename;
		}
	}

	// TODO: You can fill this in or change the return value from readModel()
	class Model {
	}
}
