import java.awt.Frame;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class A1Q3RyanReid implements GLEventListener {
	public static final String WINDOW_TITLE = "A1Q1: [Ryan Reid]"; //
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 640;

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
		return new Object() { }.getClass().getEnclosingClass();
	}

	private int width;
	private int height;
	
	private int[][] POLYGON = {
			{ 28, 417 },
			{ 0, 640 },
			{ 120, 576 },
			{ 119, 411 },
			{ 217, 484 },
			{ 153, 607 },
			{ 217, 626 },
			{ 269, 476 },
			{ 242, 363 },
			{ 207, 416 },
			{ 120, 380 },
			{ 202, 251 },
			{ 194, 327 },
			{ 160, 352 },
			{ 147, 351 },
			{ 147, 369 },
			{ 180, 376 },
			{ 173, 357 },
			{ 213, 356 },
			{ 206, 382 },
			{ 257, 340 },
			{ 205, 336 },
			{ 220, 306 },
			{ 263, 306 },
			{ 259, 217 },
			{ 224, 288 },
			{ 218, 210 },
			{ 263, 177 },
			{ 265, 22 },
			{ 226, 19 },
			{ 226, 153 },
			{ 176, 204 },
			{ 154, 122 },
			{ 197, 150 },
			{ 211, 14 },
			{ 131, 28 },
			{ 173, 98 },
			{ 92, 98 },
			{ 157, 230 },
			{ 56, 66 },
			{ 128, 64 },
			{ 108, 12 },
			{ 8, 15 },
			{ 10, 97 },
			{ 34, 99 },
			{ 34, 78 },
			{ 55, 78 },
			{ 48, 222 },
			{ 72, 214 },
			{ 63, 185 },
			{ 58, 165 },
			{ 66, 95 },
			{ 148, 226 },
			{ 85, 288 },
			{ 75, 220 },
			{ 43, 229 },
			{ 5, 200 },
			{ 1, 318 },
			{ 116, 319 },
			{ 79, 440 },
			{ 102, 466 },
			{ 76, 488 },
			{ 50, 443 },
			{ 71, 407 },
			{ 62, 370 },
			{ 46, 435 },
			{ 33, 323 },
			{ 15, 323 }
	};
	
	private final int EAR_CLIP_IGNORE_HOLES = 5;
	private int[][][] HOLES = {
			{
				{ 19, 79 },
				{ 15, 22 },
				{ 110, 36 }				
			},
			{
				{ 241, 42 },
				{ 253, 42 },
				{ 248, 180 },
				{ 231, 140 }				
			},
			{
				{ 224, 480 },
				{ 100, 394 },
				{ 112, 383 },
				{ 209, 434 },
				{ 230, 391 },
				{ 243, 406 },
				{ 215, 612 },
				{ 173, 595 }
			},
			{
				{ 153, 175 },
				{ 144, 186 },
				{ 139, 168 },
				{ 158, 167 },
				{ 133, 117 },
				{ 186, 112 },
				{ 197, 38 },
				{ 160, 35 },
				{ 175, 26 },
				{ 204, 29 },
				{ 191, 133 },
				{ 154, 120 },
				{ 146, 125 },
				{ 174, 204 }
			},
			{
				{ 162, 270 },
				{ 193, 246 },
				{ 165, 296 },
				{ 141, 281 },
				{ 130, 293 },
				{ 148, 315 },
				{ 135, 332 },
				{ 107, 290 },
				{ 101, 315 },
				{ 87, 302 },
				{ 103, 277 },
				{ 127, 278 },
				{ 138, 269 },
				{ 121, 262 },
				{ 133, 249 },
				{ 157, 255 },
				{ 150, 239 },
				{ 165, 239 },
				{ 149, 197 },
				{ 161, 195 },
				{ 171, 217 },
				{ 202, 195 },
				{ 211, 231 },
				{ 202, 242 },
				{ 190, 234 }
			},
			{
				{ 119, 110 },
				{ 151, 162 },
				{ 125, 150 },
				{ 105, 100 },
				{ 125, 105 },
				{ 178, 100 },
				{ 173, 39 },
				{ 194, 40 },
				{ 183, 108 }
			},

			{
				{ 44, 292 },
				{ 25, 284 },
				{ 25, 253 },
				{ 39, 278 },
				{ 52, 267 },
				{ 57, 283 }
			},

			{
				{ 10, 630 },
				{ 40, 440 },
				{ 110, 570 }
			},
			{
				{ 30, 600 },
				{ 90, 570 },
				{ 45, 480 },
				{ 45, 520 },
				{ 40, 510 },
				{ 28, 580 },
				{ 38, 560 }
			},
			{
				{ 75, 560 },
				{ 40, 580 },
				{ 50, 510 }
			},
			{
				{ 50, 550 },
				{ 66, 550 },
				{ 53, 530 }
			},
			{
				{ 47, 554 },
				{ 47, 570 },
				{ 66, 554 }
			},
			{
				{ 10, 288 },
				{ 10, 217 },
				{ 45, 247 },
				{ 66, 231 },
				{ 77, 306 },
				{ 9, 312 },
				{ 8, 300 },
				{ 15, 288 },
				{ 25, 302 },
				{ 66, 293 },
				{ 58, 251 },
				{ 40, 263 },
				{ 20, 235 },
				{ 20, 280 }
			}
	};
	
	private static float[][] COLOURS = {
			{ 0.5f, 0.5f, 1.0f },
			{ 0.5f, 1.0f, 0.5f },
			{ 1.0f, 0.5f, 0.5f },
			{ 0.5f, 1.0f, 1.0f },
			{ 1.0f, 0.5f, 1.0f },
			{ 1.0f, 1.0f, 0.25f },
			{ 0.5f, 0.75f, 0.75f },
			{ 0.75f, 0.5f, 0.75f }
	};

	private class Vertex implements Comparable{
		private float _x;
		private float _y;

		public Vertex(float x, float y) {
			this._x = x;
			this._y = y;
		}

		public void incrementY(float value) {
			_y += value;
		}

		public float getX() {
			return _x;
		}

		public float getY() {
			return _y;
		}

		public int compareTo(Object vert) {
			if(vert instanceof Vertex) {
				return (int) (this._x - ((Vertex) vert).getX());
			}

			return -1;
		}
	}

	private class Edge{
		private Vertex _first;
		private Vertex _second;

		public Edge(Vertex first, Vertex second) {
			this._first = first;
			this._second = second;
		}

		public Vertex getFirst() {
			return _first;
		}

		public Vertex getSecond() {
			return _second;
		}
	}

	private class Polygon {
		public ArrayList<Vertex> _vertices;

		public Vertex getVertex(int index) {
			if(index <= _vertices.size()) {
				return _vertices.get(index);
			} else {
				return null;
			}
		}

		public int size() {
			return _vertices.size();
		}

		public Polygon(ArrayList<Vertex> vertices) {
			_vertices = new ArrayList<>();
			this._vertices.addAll(vertices);
		}

		public Polygon() {
			this._vertices = new ArrayList<>();
		}

		public void addVertex(Vertex vertex) {
			_vertices.add(vertex);
		}

		public void removeVertex(Vertex vertex) {
			_vertices.remove(vertex);
		}
	}

	private class Triangle {
		public Edge _edge1;
		public Edge _edge2;
		public Edge _edge3;

		public Triangle(Edge edge1, Edge edge2, Edge edge3) {
			this._edge1 = edge1;
			this._edge2 = edge2;
			this._edge3 = edge3;
		}
	}

	public void setup(final GLCanvas canvas) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		// TODO: change background colour
		gl.glClearColor(0f, 0f, 0f, 0.0f);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();

		fillPolygon();
		fillHoles();
		drawRightPolygon(gl);
		drawLeftPolygon(gl);
		drawLeftHole(gl);
		drawRightHole(gl);
	}

	public Polygon polygon;
	public ArrayList<Polygon> holes = new ArrayList<>();

	private void fillHoles() {
		for(int i = 0; i < HOLES.length; i++) {
			Polygon hole = new Polygon();
			for(int j = 0; j < HOLES[i].length; j++) {
				hole.addVertex(new Vertex(HOLES[i][j][0], HOLES[i][j][1]));
			}
			holes.add(hole);
		}
	}

	private void fillPolygon() {
		polygon = new Polygon();
		for(int i = 0; i < POLYGON.length; i++) {
			Vertex vertex = new Vertex(POLYGON[i][0], POLYGON[i][1]);
			polygon.addVertex(vertex);
		}
	}

	private void drawLeftHole(GL2 gl) {
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0f, 0f, 0f);
		for(int i = 0; i < holes.size(); i++) {
			for(int j = 0; j < holes.get(i).size(); j++) {
				gl.glVertex2f(holes.get(i).getVertex(j)._x, holes.get(i).getVertex(j)._y);
			}
		}
		gl.glEnd();
	}

	private void drawRightHole(GL2 gl) {
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(0f, 0f, 0f);
		for(int i = 0; i < holes.size(); i++) {
			for(int j = 0; j < holes.get(i).size(); j++) {
				gl.glVertex2f(holes.get(i).getVertex(j)._x + width/2, holes.get(i).getVertex(j)._y);
			}
		}
		gl.glEnd();
	}

	private void drawRightPolygon(GL2 gl) {
		for(int y = 0; y < height; y ++) {
			Edge edge = new Edge(new Vertex(width / 2, y), new Vertex(width, y));
			colourPointsInside(edge, gl);
		}
	}

	private void colourPointsInside(Edge edge, GL2 gl) {
		for(int y = 0; y < height; y++) {
			
		}
	}

	private void drawLeftPolygon(GL2 gl) {
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(1f, 1f, 1f);
		for(int i = 0; i < polygon.size(); i++) {
			gl.glVertex2f(polygon.getVertex(i)._x, polygon.getVertex(i)._y);
		}
		gl.glEnd();
	}

	// TODO: more methods or data

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();

		gl.glViewport(x, y, width, height);

		this.width = width;
		this.height = height;
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, width, 0, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}
