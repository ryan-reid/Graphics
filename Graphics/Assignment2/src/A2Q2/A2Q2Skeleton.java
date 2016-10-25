package A2Q2;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class A2Q2Skeleton implements GLEventListener, MouseListener, MouseMotionListener  {
	public static final boolean TRACE = true;

	public static final String WINDOW_TITLE = "A2Q2: [Ryan Reid]";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 600;
    public static int WIDTH;
    public static int HEIGHT;
    public static int ID = 1;
    public static int mouseX;
    public static int mouseY;
    public static boolean mousePressed;
    public static boolean mouseHasBeenDragged;
    public static boolean ObjectCloned;

	// Name of the input file path and scene file
	public static final String INPUT_PATH_NAME = "resources/";
	public static final String INPUT_SCENE_NAME = "in.scn";

    private static ArrayList<Model> models = new ArrayList<>();

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
	
	public void setup(GLCanvas canvas) {
		// Called for one-time setup
		if (TRACE)
			System.out.println("-> executing setup()");

		WIDTH = INITIAL_WIDTH;
        HEIGHT = INITIAL_HEIGHT;

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

        if(mousePressed) {
            grabObject(gl);
        }

        if(mouseHasBeenDragged) {
            moveSelectedObject();
        }

        drawModels(gl);
	}

    private float[][] rotateMatrix(float theta) {
        return new float[][]{{(float) Math.cos(theta), -(float) Math.sin(theta), 0f}, {((float) Math.sin(theta)), (float) Math.cos(theta), 0}, {0, 0, 1}};
    }

    private float[][] transformPoints(float[][] transformation, float[][] point) {
        float[][] result = new float[3][1];

        for (int k = 0; k < point[0].length; k++) {
            for (int e = 0; e < 3; e++) {
                float sum = 0;
                for (int f = 0; f < point.length; f++) {
                    sum += transformation[e][f] * point[f][k];
                }
                result[e][k] = sum;
            }
        }

        return result;
    }

	private void moveSelectedObject() {
        Model selected = getSelected();

        if(selected != null) {
            if(selected.resizeSelected) {
                float[][] rotateMatrix = rotateMatrix((float) Math.toRadians(-selected.rotation));
                float[][] modifiedMouse = transformPoints(rotateMatrix, new float[][] { {mouseX}, {mouseY}, {1}});
                float[][] oldMouseModified = transformPoints(rotateMatrix, new float[][] { {selected.oldMouseX}, {selected.oldMouseY}, {1}});
                float scaleX = modifiedMouse[0][0] - oldMouseModified[0][0];
                float scaleY = modifiedMouse[1][0] - oldMouseModified[1][0];

                selected.scaleX += scaleX;
                selected.scaleY -= scaleY;
                selected.oldMouseY = mouseY;
                selected.oldMouseX = mouseX;

            } else if(selected.rotateSelected) {
                int adder;

                if(selected.scaleX > 0 && selected.scaleY > 0) {
                    adder = 90;
                } else if(selected.scaleX < 0 && selected.scaleY > 0) {
                    adder = -270;
                } else if(selected.scaleX > 0 && selected.scaleY < 0) {
                    adder = -90;
                } else {
                    adder = 270;
                }

                int angle = (int) ((Math.atan2(selected.translateY - selected.oldMouseY , selected.translateX - selected.oldMouseX)) * 180 / Math.PI) + adder;

                if(angle <= 0) {
                    angle = 360 + angle;
                }

                if(angle % 90 >= 80) {
                    angle += (90 - (angle % 90));
                } else if(angle % 90 <= 10) {
                    angle -= angle % 90;
                }

                selected.rotation = angle;

            } else {
                selected.translateX += (mouseX - selected.oldMouseX);
                selected.translateY += (mouseY - selected.oldMouseY);
            }

            selected.oldMouseX = mouseX;
            selected.oldMouseY = mouseY;
        }

        mouseHasBeenDragged = false;
    }

    private Model getSelected() {
        Model selected = null;

        for(int i = 0; i < models.size(); i++) {
            if(models.get(i).selected) {
                selected = models.get(i);
                break;
            }
        }

        return selected;
    }

	private void grabObject(GL2 gl) {
        gl.glDrawBuffer(GL2.GL_BACK);
        Model modelMatch = null;

        if(ObjectCloned) {
            for(int i = 0; i < models.size(); i++) {
                if(models.get(i).selected) {
                    modelMatch = models.get(i);
                    break;
                }
            }
            ObjectCloned = false;
        } else {
            models.forEach(model -> {
                for (int i = 0; i < model.totalShapes(); i++) {
                    gl.glLoadIdentity();
                    int[] faces = model._shapes.get(i);
                    ArrayList<float[][]> vertices = new ArrayList<>();

                    for (int j = 0; j < faces.length; j++) {
                        vertices.add(model.getVertex(faces[j] - 1));
                    }

                    gl.glTranslatef(model.translateX, model.translateY, 0f);
                    gl.glRotatef(model.rotation, 0, 0, 1);
                    gl.glScalef(model.scaleX, model.scaleY, 1);

                    gl.glBegin(GL2.GL_POLYGON);
                    gl.glColor3f(model.color[0], model.color[1], model.color[2]);
                    for (int j = 0; j < vertices.size(); j++) {
                        gl.glVertex2f(vertices.get(j)[0][0], vertices.get(j)[1][0]);
                    }

                    gl.glEnd();

                    model._resize.drawControlHandle(gl, model, new float[] {model._resize.color.getRed() / 255f, model._resize.color.getGreen() / 255f, model._resize.color.getBlue() / 255f}, true);
                    model._rotate.drawControlHandle(gl, model, new float[] {model._rotate.color.getRed() / 255f, model._rotate.color.getGreen() / 255f, model._rotate.color.getBlue() / 255f}, false);
                }
            });

            gl.glReadBuffer(GL2.GL_BACK);
            FloatBuffer buff = FloatBuffer.allocate(4);
            gl.glReadPixels(mouseX, mouseY, 1, 1, GL2.GL_RGBA, GL2.GL_FLOAT, buff);
            Color index = new Color(buff.get(0), buff.get(1), buff.get(2));
            int i = index.getRGB();
            modelMatch = getModelWithColourMatch(-i);
        }

        if(modelMatch != null) {
            attachBoundingBox(modelMatch);
            attachHandles(modelMatch);
            modelMatch.oldMouseX = mouseX;
            modelMatch.oldMouseY = mouseY;
        }

        mousePressed = false;
        mouseHasBeenDragged = false;
    }

    private void attachHandles(Model modelMatch) {
        ControlHandle control = modelMatch._resize;
        control.x1 = 0;
        control.x2 = 8;
        control.y1 = 0;
        control.y2 = 8;

        ControlHandle rotate = modelMatch._rotate;
        rotate.x1 = 0;
        rotate.x2 = 8;
        rotate.y1 = 0;
        rotate.y2 = 8;
    }

    private void attachBoundingBox(Model model) {
        float maxX = -1;
        float maxY = -1;
        float minX = 1;
        float minY = 1;

        for(int i = 0; i < model._vertices.size(); i++) {
            if(model.getVertex(i)[0][0] >= maxX) {
                maxX = model.getVertex(i)[0][0];
            }

            if(model.getVertex(i)[0][0] <= minX) {
                minX = model.getVertex(i)[0][0];
            }

            if(model.getVertex(i)[1][0] >= maxY) {
                maxY = model.getVertex(i)[1][0];
            }

            if(model.getVertex(i)[1][0] <= minY) {
                minY = model.getVertex(i)[1][0];
            }
        }

        model.selected = true;
        model.maxX = maxX;
        model.minX = minX;
        model.maxY = maxY;
        model.minY = minY;
    }

    private Model getModelWithColourMatch(int color) {
        Model modelMatch = null;

        for(int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            if(model._RGB == color) {
                modelMatch = model;
                modelMatch.selected = true;
            } else if(model._rotate.RGB == color) {
                modelMatch = model;
                model.rotateSelected = true;
            } else if(model._resize.RGB == color) {
                modelMatch = model;
                model.resizeSelected = true;
            } else {
                model.selected = false;
            }
        }

        if(modelMatch != null && !modelMatch.selected) {
            modelMatch.resizeSelected = false;
            modelMatch.rotateSelected = false;
            modelMatch = null;
        }

        return modelMatch;
    }

	private void drawModels(GL2 gl) {
        models.forEach( model -> drawModel(gl, model));
    }

    private void drawModel(GL2 gl, Model model) {
        for(int i = 0; i < model.totalShapes(); i++) {
            gl.glLoadIdentity();
            int[] faces = model._shapes.get(i);
            ArrayList<float[][]> vertices = new ArrayList<>();

            for(int j = 0; j < faces.length; j++) {
                vertices.add(model.getVertex(faces[j] - 1));
            }

            gl.glTranslatef(model.translateX, model.translateY, 0f);
            gl.glRotatef(model.rotation, 0, 0, 1);
            gl.glScalef(model.scaleX, model.scaleY, 1);

            gl.glBegin(GL2.GL_POLYGON);
            gl.glColor3f(model.getColour(i)[0], model.getColour(i)[1], model.getColour(i)[2]);
            for(int j = 0; j < vertices.size(); j++) {
                gl.glVertex2f(vertices.get(j)[0][0], vertices.get(j)[1][0]);
            }

            gl.glEnd();
        }

        if(model.selected) {
            gl.glLoadIdentity();

            gl.glTranslatef(model.translateX, model.translateY, 0f);
            gl.glRotatef(model.rotation, 0, 0, 1);
            gl.glScalef(model.scaleX, model.scaleY, 1);

            gl.glBegin(GL2.GL_POLYGON);
            gl.glColor4f(0f, 0f, 1f, .5f);
            gl.glVertex2f(model.maxX, model.maxY);
            gl.glVertex2f(model.maxX, model.minY);
            gl.glVertex2f(model.minX, model.minY);
            gl.glVertex2f(model.minX, model.maxY);

            gl.glEnd();
            gl.glLoadIdentity();

            model._resize.drawControlHandle(gl, model, new float[]{0, 1, 0}, true);
            model._rotate.drawControlHandle(gl, model, new float[]{1, 0, 0}, false);

            gl.glLoadIdentity();
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

        WIDTH = width;
        HEIGHT = height;

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = (HEIGHT - e.getY());
        mouseHasBeenDragged = true;
		System.out.println("Mouse dragged to (" + e.getX() + "," + (HEIGHT - e.getY()) + ")");
        ((GLCanvas)e.getSource()).repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// You probably don't need this one
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse clicked on (" + e.getX() + "," + (HEIGHT - e.getY()) + ")");
        boolean cloned = false;

        if(e.getClickCount() % 2 == 0) {
            for(int i = 0; i < models.size(); i++) {
                if(models.get(i).selected) {
                    models.get(i).selected = false;
                    Model newModel = models.get(i).copy();
                    newModel.selected = true;
                    models.add(newModel);
                    newModel.shiftDownAndRight();
                    cloned = true;
                    break;
                }
            }

            ObjectCloned = true;
        }

        if(cloned) {
            mousePressed = true;
            ((GLCanvas)e.getSource()).repaint();
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Mouse pressed on (" + e.getX() + "," + (HEIGHT - e.getY()) + ")");

        mousePressed = true;
        mouseX = e.getX();
        mouseY = (HEIGHT - e.getY());
        ((GLCanvas)e.getSource()).repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Mouse released on (" + e.getX() + "," + (HEIGHT - e.getY()) + ")");

        models.forEach( model -> model.deselectControls());
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

					model.addVertex(new float[][] { {vertex[0]}, {vertex[1]}, {1}});
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

					model.addColour(colour);
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

					System.out.printf("face %d: [ ", faceCount + 1);
					for (int index: face) {
						System.out.printf("%d ", index);
					}
					model.addShape(face);
                    model.addShapeColour(new int[] {currentColourIndex});
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

        models = new ArrayList<>();

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

                                model.translateX = sceneData[0];
                                model.translateY = sceneData[1];
                                model.rotation = sceneData[2];
                                model.scaleX = sceneData[3];
                                model.scaleY = sceneData[4];
                                models.add(model.copy());
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

	private class Model {
        ArrayList<float[][]> _vertices;
        ArrayList<float[]> _colour;
        ArrayList<int[]> _shapes;
        ArrayList<int[]> _shapeColour;
        float translateX, translateY;
        float scaleX, scaleY;
        float rotation;
        float[] color;
        int _RGB;
        float maxX, minX, maxY, minY;
        boolean selected = false;
        int oldMouseX, oldMouseY;
        ControlHandle _resize;
        ControlHandle _rotate;
        boolean rotateSelected, resizeSelected;

        private void deselectControls() {
            this.resizeSelected = false;
            this.rotateSelected = false;
        }

        private void addShape(int[] shape) {
            _shapes.add(shape);
        }

        private void addShapeColour(int[] colour) {
            _shapeColour.add(colour);
        }

        private Model() {
            _vertices = new ArrayList<>();
            _colour = new ArrayList<>();
            _shapes = new ArrayList<>();
            _shapeColour = new ArrayList<>();
            _resize = new ControlHandle();
            _rotate = new ControlHandle();
        }

        private void addVertex(float[][] vertex) {
            _vertices.add(vertex);
        }

        private void addColour(float[] colour) {
            _colour.add(colour);
        }

        private float[] getColour(int index) {
            int colour = _shapeColour.get(index)[0] - 1;

            if(colour == -1) {
                return new float[] {0f, 0f, 0f};
            } else {
                return _colour.get(colour);
            }
        }

        private float[][] getVertex(int index) {
            return _vertices.get(index);
        }

        private int totalShapes() {
            return _shapes.size();
        }

        private Model copy() {
            Model newModel = new Model();
            newModel.translateX = translateX;
            newModel.translateY = translateY;
            newModel.scaleY = scaleY;
            newModel.scaleX = scaleX;
            newModel.rotation = rotation;

            _vertices.forEach( vertices -> newModel._vertices.add(new float[][] {{vertices[0][0]}, {vertices[1][0]}}));
            newModel._shapes.addAll(this._shapes);
            newModel._shapeColour.addAll(this._shapeColour);
            newModel._colour.addAll(this._colour);

            Color color = new Color(ID);
            newModel.color = new float[]{color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f};
            newModel._RGB = -color.getRGB();
            ID+= 1;

            return newModel;
        }

        private void shiftDownAndRight() {
            for(int i =0 ; i < _vertices.size(); i++) {
                _vertices.get(i)[0][0] -= .1f;
                _vertices.get(i)[1][0] -= .1f;
            }
        }
	}

	private class ControlHandle {
        float x1;
        float x2;
        float y1;
        float y2;
        Color color;
        int RGB;

        private ControlHandle() {
            color = new Color(ID);
            RGB = -color.getRGB();
            ID++;
        }

        private void drawControlHandle(GL2 gl, Model model, float[] color, boolean resize) {
            gl.glLoadIdentity();
            System.out.println((model.scaleX * model.maxX) + model.translateX);

            gl.glTranslatef(model.translateX, model.translateY, 0);
            gl.glRotatef(model.rotation, 0, 0, 1);
            gl.glTranslatef(-model.translateX, -model.translateY, 0);

            if(resize) {
                gl.glTranslatef((model.scaleX * model.maxX) + model.translateX, (model.scaleY * model.minY) + model.translateY, 0f);
            } else {
                gl.glTranslatef((model.scaleX * ((model.maxX + model.minX) / 2)) + model.translateX, (model.scaleY * model.maxY) + model.translateY, 0f);
            }



            gl.glColor3f(color[0], color[1], color[2]);
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2f(x1, y1);
            gl.glVertex2f(x1, y2);
            gl.glVertex2f(x2, y2);
            gl.glVertex2f(x2, y1);
            gl.glEnd();

            gl.glLoadIdentity();
        }
    }
}
