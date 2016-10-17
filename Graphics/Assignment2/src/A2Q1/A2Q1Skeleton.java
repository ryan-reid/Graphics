package A2Q1;

import java.awt.Frame;
import java.awt.event.*;
import java.util.ArrayList;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class A2Q1Skeleton implements GLEventListener {
	private static final boolean TRACE = true;
	private static final String WINDOW_TITLE = "A2Q1: [Ryan Reid]";
	private static final int INITIAL_WIDTH = 640;
	private static final int INITIAL_HEIGHT = 640;

    private ArrayList<DrawableStructure> structures = new ArrayList<>();
    private static final int COLOUR = 3;
    private static final int RED    = 0;
    private static final int GREEN  = 1;
    private static final int BLUE   = 2;

	public static void main(String[] args) {
		final Frame frame = new Frame(WINDOW_TITLE);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
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
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		canvas.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		canvas.setAutoSwapBufferMode(true);

		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);

		System.out.println("\nEnd of processing.");
	}

	private static Class<?> self() {
		// This ugly hack gives us the containing class of a static method 
		return new Object() { }.getClass().getEnclosingClass();
	}

	private int width, height; // the real viewport width and height
	private float[][] colours = new float[][] {
		{ 0.0f, 0.0f, 1.0f },
		{ 0.0f, 1.0f, 0.0f },
		{ 1.0f, 0.0f, 0.0f },
		{ 0.0f, 1.0f, 1.0f },
		{ 1.0f, 0.0f, 1.0f },
		{ 1.0f, 1.0f, 0.0f },
		{ 0.0f, 0.5f, 0.5f },
		{ 0.5f, 0.0f, 0.5f },
		{ 0.5f, 0.5f, 0.0f },
		{ 0.5f, 0.5f, 0.5f }
	};
	private float[][] vertices = new float[][] {
		{ -0.58074534f, -0.19254655f },
		{ -0.18012422f, 0.27329195f },
		{ -0.12732917f, 0.11490691f },
		{ 0.31987584f, 0.4223603f },
		{ 0.16770184f, 0.018633604f },
		{ 0.3167702f, -0.009316742f },
		{ 0.67701864f, 0.11801243f },
		{ 0.48447204f, 0.012422323f },
		{ 0.6801243f, 0.6335404f },
		{ 0.7111802f, 0.49068332f },
		{ -0.03726709f, 0.50310564f },
		{ 0.027950287f, 0.6552795f },
		{ 0.24844718f, 0.8447205f },
		{ 0.42857146f, 0.8354038f },
		{ 0.7391305f, 0.40062118f },
		{ 0.69875777f, 0.25155282f },
		{ -0.012422323f, 0.108695626f },
		{ 0.12111807f, 0.062111855f },
		{ 0.11801243f, -0.8043478f },
		{ 0.29192543f, -0.8291925f },
		{ 0.33850932f, -0.31055897f },
		{ 0.70807457f, -0.03726709f },
		{ 0.7298137f, -0.102484465f },
		{ 0.38198757f, 0.3726709f },
		{ -0.13975155f, -0.8664596f },
		{ 0.49068332f, -0.89751554f },
		{ 0.3757764f, -0.63975155f },
		{ 0.33229816f, -0.863354f },
		{ 0.43478262f, -0.64906836f },
		{ 0.34161496f, -0.67701864f },
		{ 0.41925466f, -0.6242236f },
		{ 0.35093164f, -0.5279503f },
		{ 0.51552796f, -0.5496894f },
		{ 0.47515535f, -0.57142854f },
		{ 0.48757768f, -0.6552795f },
		{ 0.45341623f, -0.7111801f },
		{ 0.40993786f, -0.7298137f },
		{ 0.34472048f, -0.69875777f },
		{ 0.38198757f, -0.72670805f },
		{ 0.41614914f, -0.51863354f },
		{ 0.4627329f, -0.5279503f },
		{ -0.66770184f, -0.4751553f },
		{ -0.6708075f, -0.6180124f },
		{ -0.5745342f, -0.48447204f },
		{ -0.5652174f, -0.6086956f },
		{ -0.7515528f, -0.42236024f },
		{ -0.76397514f, -0.8757764f },
		{ -0.6708075f, -0.4751553f },
		{ -0.5310559f, -0.41925466f },
		{ -0.66770184f, -0.47204965f },
		{ -0.5248447f, -0.86024845f },
		{ -0.6614907f, -0.47826087f },
		{ -0.6708075f, -0.878882f },
		{ -0.66770184f, -0.47826087f },
		{ -0.95341617f, -0.8913044f },
		{ -0.9285714f, -0.11801243f },
		{ -0.742236f, -0.07142854f },
		{ -0.39440995f, -0.015527904f },
		{ -0.31987578f, -0.85714287f },
		{ -0.71428573f, 0.26086962f },
		{ -0.6552795f, -0.055900574f },
		{ -0.29813665f, -0.12422359f },
		{ -0.19875777f, -0.052794993f },
		{ -0.7049689f, 0.42546582f },
		{ -0.9751553f, -0.19875777f },
		{ -1f, -0.12111801f }
	};

	private enum ObjectName {
		TRUNK(0), LEAVES(1), FLOWER(2), ROOF(3), HOUSE(4);
		private int i;
		ObjectName(int i) { this.i = i; }
		public int i() { return i; }
	}
	
	private float[][] centres = new float[][] {
		// trunk
		{ 0.23602486f, -0.31366456f },
		// leaves
		{ 0.31987584f, 0.4223603f },
		// flower
		{ 0.40683234f, -0.63043475f },
		// roof
		{ -0.6863354f, 0.07453418f },
		// house
		{ -0.6180124f, -0.5372671f }
	};
	
	private int[][][] objects = new int[][][] {
		// trunk
		{
			{ 18, 19, 3, 8 },
			{ 20, 21, 22, 8 },
			{ 23, 3, 19, 8 },
			{ 24, 18, 19, 8 },
			{ 25, 19, 24, 8 }
		},
		
		// leaves
		{
			{ 1, 2, 3, 6 },
			{ 4, 5, 3, 6 },
			{ 3, 6, 7, 6 },
			{ 8, 9, 3, 6 },
			{ 10, 3, 11, 6 },
			{ 12, 13, 3, 1 },
			{ 3, 14, 15, 1 },
			{ 16, 17, 3, 1 }
		},
		
		// flower
		{
			{ 26, 27, 28, 9 },
			{ 29, 30, 31, 2 },
			{ 33, 34, 30, 2 },
			{ 35, 36, 30, 2 },
			{ 37, 38, 30, 2 },
			{ 39, 40, 30, 2 }
		},
		
		// roof
		{
			{ 59, 55, 60, 0, },
			{ 59, 57, 60, 0, },
			{ 61, 62, 63, 9, },
			{ 64, 65, 63, 9, },
			{ 61, 59, 63, 9, },
			{ 64, 59, 63, 9, }
		},
		
		// house
		{
			{ 41, 42, 43, 4, },
			{ 44, 43, 42, 4, },
			{ 45, 46, 41, 7, },
			{ 48, 45, 41, 7, },
			{ 48, 50, 43, 7, },
			{ 43, 41, 48, 7, },
			{ 52, 46, 53, 7, },
			{ 44, 52, 42, 7, },
			{ 52, 50, 44, 7, },
			{ 44, 43, 50, 7, },
			{ 54, 55, 46, 3, },
			{ 46, 56, 55, 3, },
			{ 56, 48, 45, 3, },
			{ 56, 57, 48, 3, },
			{ 57, 58, 48, 3, },
			{ 58, 50, 48, 3, }
		}
	};

	public void setup(final GLCanvas canvas) {
		// Called for one-time setup
		if (TRACE)
			System.out.println("-> executing setup()");
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Called when the canvas is (re-)created - use it for initial GL setup
		if (TRACE)
			System.out.println("-> executing init()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.1f, 0.15f, 0.1f, 0.0f);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Draws the display
		if (TRACE)
			System.out.println("-> executing display()");

		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        drawBoxes(gl);
		drawLeftSide(gl);
		//drawRightSide(gl);
	}


	private void drawBoxes(GL2 gl) {
        int xIncrements = width / 4;
        int yIncrements = height / 4;

        for(int i = 1; i < 4; i++) {
            gl.glColor3f(1f, 1f, 1f);
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex2f(xIncrements * i, 0);
            gl.glVertex2f(xIncrements * i, height);
            gl.glVertex2f(0, yIncrements * i);
            gl.glVertex2f(width, yIncrements * i);
            gl.glEnd();
        }
    }

	private void drawLeftSide(GL2 gl) {
        for(int i = 0; i < 1; i++) {
            addAllStructures();
        }

        structures.forEach( structure ->  drawSceneOne(gl, structure));
       // structures.forEach( structure ->  drawSceneTwo(gl, structure));
        structures.forEach( structure ->  drawSceneThree(gl, structure));
        structures.forEach( structure ->  drawSceneFour(gl, structure));
        //structures.forEach( structure ->  drawSceneFix(gl, structure));
        //structures.forEach( structure ->  drawSceneSix(gl, structure));
        //structures.forEach( structure ->  drawSceneSeven(gl, structure));
        //structures.forEach( structure ->  drawSceneEight(gl, structure));
	}

    private float[][] transformAllPoints(float[][] transformation, float[][] points) {
        float[][] results = new float[3][3];

        float[][] point1 = transformPoints(transformation, new float[][]{{points[0][0]}, {points[1][0]}, {points[2][0]}});
        float[][] point2 = transformPoints(transformation, new float[][]{{points[0][1]}, {points[1][1]}, {points[2][1]}});
        float[][] point3 = transformPoints(transformation, new float[][]{{points[0][2]}, {points[1][2]}, {points[2][2]}});

        results[0][0] = point1[0][0];
        results[1][0] = point1[1][0];
        results[2][0] = point1[2][0];

        results[0][1] = point2[0][0];
        results[1][1] = point2[1][0];
        results[2][1] = point2[2][0];

        results[0][2] = point3[0][0];
        results[1][2] = point3[1][0];
        results[2][2] = point3[2][0];

        return results;
    }

    private float[][] rotateMatrix(float theta) {
        return new float[][]{{(float) Math.cos(theta), -(float) Math.sin(theta), 0f}, {((float) Math.sin(theta)), (float) Math.cos(theta), 0}, {0, 0, 1}};
    }

    private float[][] scaleMatrix(float scaleX, float scaleY) {
        return new float[][] { {scaleX, 0, 0}, {0, scaleY, 0}, {0, 0, 1}};
    }

    private float[][] translationMatrix(float transX, float transY) {
        return new float[][] { {1, 0, transX}, {0, 1, transY}, {0, 0, 1}};
    }

    private void drawSceneOne(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);
            float[][] scale = scaleMatrix((width / 8), (height / 8));
            float[][] translation = translationMatrix(width / 8, (height - (height / 8)));
            float[][] transformedVertices = transformAllPoints(scale, vertices);
            transformedVertices = transformAllPoints(translation, transformedVertices);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);

            gl.glVertex2f(transformedVertices[0][0], (transformedVertices[1][0]));
            gl.glVertex2f(transformedVertices[0][1], (transformedVertices[1][1]));
            gl.glVertex2f(transformedVertices[0][2], (transformedVertices[1][2]));
            gl.glEnd();
        }
    }

    private void drawSceneTwo(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);

            float[][] scale = scaleMatrix((width / 8), height / 8);
            float[][] translate = translationMatrix(3 * (width / 8), (height - (height / 8)));
            //float[][] transformed = multiply(translate, scale);
            float[][] transformedVertices = transformAllPoints(translate, vertices);
            transformedVertices = transformAllPoints(scale, transformedVertices);

            gl.glVertex2f(transformedVertices[0][0], (transformedVertices[1][0]));
            gl.glVertex2f(transformedVertices[0][1], (transformedVertices[1][1]));
            gl.glVertex2f(transformedVertices[0][2], (transformedVertices[1][2]));
            gl.glEnd();
        }
    }

    private void drawSceneThree(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);

            float[][] scale = scaleMatrix((width / 8), (height / 8));
            float[][] transformedVertices = transformAllPoints(scale, vertices);

            float[][] translation = translationMatrix(width / 8, (height - (3 * (height / 8))));
            transformedVertices = transformAllPoints(translation, transformedVertices);

            scale = scaleMatrix(.75f, 1.25f);
            transformedVertices = transformAllPoints(scale, transformedVertices);

            translation = translationMatrix((width / 8) * .25f, (height - (3 * (height / 8))) * -.25f);
            transformedVertices = transformAllPoints(translation, transformedVertices);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex2f(transformedVertices[0][0], (transformedVertices[1][0]));
            gl.glVertex2f(transformedVertices[0][1], (transformedVertices[1][1]));
            gl.glVertex2f(transformedVertices[0][2], (transformedVertices[1][2]));
            gl.glEnd();
        }
    }

    private void drawSceneFour(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);

            float[][] scale = scaleMatrix((width / 8), (height / 8));
            float[][] transformedVertices = transformAllPoints(scale, vertices);

            float[][] translation = translationMatrix(3 * (width / 8), (height - (3 * (height / 8))));
            transformedVertices = transformAllPoints(translation, transformedVertices);

            scale = scaleMatrix(.60f, .60f);
            transformedVertices = transformAllPoints(scale, transformedVertices);

            translation = translationMatrix(( 3 * (width / 8)) * .4f, (height - (3 * (height / 8))) * .4f);
            transformedVertices = transformAllPoints(translation, transformedVertices);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex2f(transformedVertices[0][0], (transformedVertices[1][0]));
            gl.glVertex2f(transformedVertices[0][1], (transformedVertices[1][1]));
            gl.glVertex2f(transformedVertices[0][2], (transformedVertices[1][2]));
            gl.glEnd();
        }
    }

    private void drawSceneFix(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex2f((vertices[0][0]* (width / 8)) + (width / 8), (vertices[1][0]* (height / 8))+ (3 * (height / 8)));
            gl.glVertex2f((vertices[0][1]* (width / 8)) + (width / 8), (vertices[1][1]* (height / 8))+ (3 * (height / 8)));
            gl.glVertex2f((vertices[0][2]* (width / 8)) + (width / 8), (vertices[1][2]* (height / 8))+ (3 * (height / 8)));
            gl.glEnd();
        }
    }

    private void drawSceneSix(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex2f((vertices[0][0]* (width / 8)) + ( 3 * (width / 8)), (vertices[1][0]* (height / 8))+ (3 * (height / 8)));
            gl.glVertex2f((vertices[0][1]* (width / 8)) + ( 3 * (width / 8)), (vertices[1][1]* (height / 8))+ (3 * (height / 8)));
            gl.glVertex2f((vertices[0][2]* (width / 8)) + ( 3 * (width / 8)), (vertices[1][2]* (height / 8))+ (3 * (height / 8)));
            gl.glEnd();
        }
    }

    private void drawSceneSeven(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex2f((vertices[0][0]* (width / 8)) + (width / 8), (vertices[1][0]* (height / 8))+ (height / 8));
            gl.glVertex2f((vertices[0][1]* (width / 8)) + (width / 8), (vertices[1][1]* (height / 8))+ (height / 8));
            gl.glVertex2f((vertices[0][2]* (width / 8)) + (width / 8), (vertices[1][2]* (height / 8))+ (height / 8));
            gl.glEnd();
        }
    }

    private void drawSceneEight(GL2 gl, DrawableStructure structure) {
        for(int i = 0; i < structure._matrices.size(); i++) {
            float[] colour = structure._colour.get(i);
            float[][] vertices = structure._matrices.get(i);

            gl.glColor3f(colour[RED], colour[GREEN], colour[BLUE]);
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glVertex2f((vertices[0][0]* (width / 8)) + ( 3 * (width / 8)), (vertices[1][0]* (height / 8))+ (height / 8));
            gl.glVertex2f((vertices[0][1]* (width / 8)) + ( 3 * (width / 8)), (vertices[1][1]* (height / 8))+ (height / 8));
            gl.glVertex2f((vertices[0][2]* (width / 8)) + ( 3 * (width / 8)), (vertices[1][2]* (height / 8))+ (height / 8));
            gl.glEnd();
        }
    }

    private void addAllStructures() {
        structures = new ArrayList<>();
        for(int i = 0; i < ObjectName.values().length; i++) {
            addStructure(i);
        }
    }

	private void addStructure(int objectIndex) {
        DrawableStructure structure = new DrawableStructure(objectIndex);
        for(int i = 0; i < objects[objectIndex].length; i++) {
            for(int j = 0; j < objects[objectIndex][i].length; j++) {
                int colourIndex = objects[objectIndex][i][COLOUR];
                float[] colour = colours[colourIndex];
                float[] coordinateOne = vertices[(objects[objectIndex][i][0])];
                float[] coordinateTwo = vertices[(objects[objectIndex][i][1])];
                float[] coordinateThree = vertices[(objects[objectIndex][i][2])];

                float[][] matrix = new float[3][3];
                matrix[0][0] = coordinateOne[0];
                matrix[1][0] = coordinateOne[1];
                matrix[2][0] = 1f;

                matrix[0][1] = coordinateTwo[0];
                matrix[1][1] = coordinateTwo[1];
                matrix[2][1] = 1f;

                matrix[0][2] = coordinateThree[0];
                matrix[1][2] = coordinateThree[1];
                matrix[2][2] = 1f;

                structure.addMatrix(matrix);
                structure.addColor(colour);
            }
        }
        structures.add(structure);
    }
	
	public void drawRightSide(GL2 gl) {
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
	
	public float[][] multiply(float[][] a, float[][] b) {
		float[][] result = new float[3][3];

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				for (int k = 0; k < 3; k++)
					result[i][j] += a[i][k] * b[k][j];

		return result;
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

		gl.glViewport(x, y, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, width, 0, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		this.width = width;
		this.height = height;
	}

	private class DrawableStructure {
        ArrayList<float[][]> _matrices;
        ArrayList<float[][]> _transformedMatrices;
        ArrayList<float[]> _colour;
        float[] _center;

        private DrawableStructure(int objectIndex) {
            _matrices = new ArrayList<>();
            _transformedMatrices = new ArrayList<>();
            _colour = new ArrayList<>();
            _center = centres[objectIndex];
        }

        private void addMatrix(float[][] matrix) {
            _matrices.add(matrix);
        }

        private void addColor(float[] colour) {
            _colour.add(colour);
        }
    }
}
