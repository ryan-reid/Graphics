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

	private class Vertex {
		private float _x;
		private float _y;

		private Vertex(float x, float y) {
			this._x = x;
			this._y = y;
		}

		private Vertex(Vertex v) {
            this._x = v.getX();
            this._y = v.getY();
        }

        private float getX() {
			return _x;
		}

        private float getY() {
			return _y;
		}

		private boolean equals(Vertex compareTo) {
            return (_x == compareTo.getX() && _y == compareTo.getY());
        }

        private boolean notEquals(Vertex compareTo) {
            return !equals(compareTo);
        }
	}

	private class Triangle {
        private Vertex get_v1() {
            return _v1;
        }

        private Vertex get_v2() {
            return _v2;
        }


        private Vertex get_v3() {
            return _v3;
        }


        private Vertex _v1;
        private Vertex _v2;
        private Vertex _v3;

        private Triangle(Vertex v1, Vertex v2, Vertex v3) {
            this._v1 = v1;
            this._v2 = v2;
            this._v3 = v3;
        }
    }

	private class Polygon {
        private ArrayList<Vertex> _vertices;

        private Vertex getVertex(int index) {
			if(index <= _vertices.size()) {
				return _vertices.get(index);
			} else {
				return null;
			}
		}

        private int size() {
			return _vertices.size();
		}

        private Polygon() {
			this._vertices = new ArrayList<>();
		}

        private void addVertex(Vertex vertex) {
            this._vertices.add(new Vertex(vertex));
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

        fillBackground(gl);
		fillPolygon();
		fillHoles();
		drawRightPolygon(gl);
        drawLeftPolygon(gl);
	}

	private void drawLeftPolygon(GL2 gl) {
        addEdges();
        getEars(gl);
        //fillTrianglesArray(gl);
    }

    private void addEdges() {
        boolean addedPoly = false;
        for(int i = 0; i < holes.size() - EAR_CLIP_IGNORE_HOLES; i++) {
            for(int j = 0; j < holes.get(i).size(); j++) {

                for(int p = 0; p < polygon.size(); p++) {
                    if(!edgeIntersects(holes.get(i).getVertex(j), polygon.getVertex(p))) {
                        addPolygonAtIndex(holes.get(i), (p + 1) % polygon.size(), j, p);
                        addedPoly = true;
                        break;
                    }
                }
                if(addedPoly) {
                    addedPoly = false;
                    break;
                }
            }
        }
    }

    private void addPolygonAtIndex(Polygon poly, int index, int firstVectorInHole, int indexOfEdgeIntoPoly) {
        ArrayList<Vertex> newOrder = new ArrayList<>();

        for(int i = 0; i < poly.size(); i++) {
            newOrder.add(new Vertex(poly.getVertex(firstVectorInHole % poly.size())));
            firstVectorInHole++;
        }
        newOrder.add(new Vertex(poly.getVertex(firstVectorInHole % poly.size())));
        newOrder.add(new Vertex(polygon.getVertex(indexOfEdgeIntoPoly)));

        if(index >= poly.size()) {
            polygon._vertices.addAll(newOrder);
        } else {
            polygon._vertices.addAll(index, newOrder);
        }

    }

    private void drawTriangles(GL2 gl, Triangle triangle, int i) {
        float color[] = COLOURS[i % COLOURS.length];
        gl.glColor3f(color[0], color[1], color[2]);
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(triangle.get_v1().getX(), triangle.get_v1().getY());
        gl.glVertex2f(triangle.get_v2().getX(), triangle.get_v2().getY());
        gl.glVertex2f(triangle.get_v3().getX(), triangle.get_v3().getY());
        gl.glEnd();

        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(triangle.get_v1().getX(), triangle.get_v1().getY());
        gl.glVertex2f(triangle.get_v2().getX(), triangle.get_v2().getY());
        gl.glVertex2f(triangle.get_v3().getX(), triangle.get_v3().getY());
        gl.glVertex2f(triangle.get_v1().getX(), triangle.get_v1().getY());
        gl.glEnd();
    }

    private void getEars(GL2 gl) {
        int attempts = 0;
        int earsColored = 0;
        while(polygon.size() >= 3) {
            for(int i = 1; i < polygon.size(); i++) {
                float crossProduct = crossProduct(polygon.getVertex(i - 1), polygon.getVertex(i), polygon.getVertex((i + 1) % polygon.size()));

                if(crossProduct < 0.0f) {
                    if(!edgeIntersects(polygon.getVertex(i - 1), polygon.getVertex((i + 1) % polygon.size()))) {
                        Triangle triangle = new Triangle(polygon.getVertex(i - 1), polygon.getVertex((i)), polygon.getVertex((i + 1) % polygon.size()));
                        if(!pointInsideTriangle(triangle)) {
                            drawTriangles(gl, triangle, earsColored);
                            polygon._vertices.remove(triangle._v2);
                            earsColored++;
                            attempts = 0;
                        }
                    }
                }
            }

            if(attempts == 500) {
                break;
            }
            attempts++;
        }
    }

    private float sign(Vertex point1, Vertex point2, Vertex point3) {
            return ((point1.getX() - point3.getX()) * (point2.getY() - point3.getY())) - ((point2.getX() - point3.getX()) * (point1.getY() - point3.getY()));
    }

    private boolean pointInsideTriangle(Triangle triangle) {
        Vertex point;
        boolean pointInside = false;

        for(int i = 0; i < polygon.size(); i++) {
            point = polygon.getVertex(i);

            boolean signV1 = sign(point, triangle.get_v1(), triangle.get_v2()) < 0.0f;
            boolean signV2 = sign(point, triangle.get_v2(), triangle.get_v3()) < 0.0f;
            boolean signV3 = sign(point, triangle.get_v3(), triangle.get_v1()) < 0.0f;

            if(((signV1 == signV2) && (signV2 == signV3))) {
                pointInside = true;
                break;
            }
        }

        return pointInside;
    }

    private boolean holeIntersect(Vertex v2, Vertex v3) {
        boolean intersects = false;
        float x1 = v2.getX();
        float y1 = v2.getY();
        float x2 = v3.getX();
        float y2 = v3.getY();

        for(int i = 0; i < holes.size() - EAR_CLIP_IGNORE_HOLES; i++) {
            for(int j = 0; j < holes.get(i).size(); j++) {
                if(holes.get(i).getVertex(j).notEquals(v2) && holes.get(i).getVertex(j).notEquals(v3)
                && holes.get(i).getVertex((j + 1) % holes.get(i).size()).notEquals(v2) && holes.get(i).getVertex((j + 1) % holes.get(i).size()).notEquals(v3)) {
                    float x3 = holes.get(i).getVertex(j).getX();
                    float x4 = holes.get(i).getVertex((j + 1) % holes.get(i).size()).getX();
                    float y3 = holes.get(i).getVertex(j).getY();
                    float y4 = holes.get(i).getVertex((j + 1) % holes.get(i).size()).getY();

                    float ta = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
                    float tb = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);
                    float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

                    if (denom != 0.0f) {
                        ta = ta / denom;
                        tb = tb / denom;
                        if (ta >= 0.0f && ta <= 1.0f && tb >= 0.0f && tb <= 1.0f) {
                            intersects = true;
                            break;
                        }
                    }
                }
            }
            if(intersects) {
                break;
            }
        }

        return intersects;
    }

    private boolean edgeIntersects(Vertex v2, Vertex v3) {
        boolean intersects = false;
        float x1 = v2.getX();
        float y1 = v2.getY();
        float x2 = v3.getX();
        float y2 = v3.getY();

        for(int i = 0; i < polygon.size(); i++) {
            if(polygon.getVertex(i).notEquals(v2) && polygon.getVertex(i).notEquals(v3)
            && polygon.getVertex((i + 1) % polygon.size()).notEquals(v2) && polygon.getVertex((i + 1) % polygon.size()).notEquals(v3)) {
                float x3 = polygon.getVertex(i).getX();
                float x4 = polygon.getVertex((i + 1) % polygon.size()).getX();
                float y3 = polygon.getVertex(i).getY();
                float y4 = polygon.getVertex((i + 1) % polygon.size()).getY();

                float ta = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
                float tb = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);
                float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

                if (denom != 0.0f) {
                    ta = ta / denom;
                    tb = tb / denom;
                    if (ta >= 0.0f && ta <= 1.0f && tb >= 0.0f && tb <= 1.0f) {
                        intersects = true;
                        break;
                    }
                }
            }
        }

        if(!intersects) {
            intersects = holeIntersect(v2, v3);
        }

        return intersects;
    }

    private float crossProduct(Vertex v1, Vertex v2, Vertex v3) {
        float dx1 = v2.getX() - v1.getX();
        float dy1 = v2.getY() - v1.getY();
        float dx2 = v3.getX() - v2.getX();
        float dy2 = v3.getY() - v2.getY();

        return (dx1 * dy2) - (dy1 * dx2);
    }

	private Polygon polygon;
    private ArrayList<Polygon> holes = new ArrayList<>();

    private void fillBackground(GL2 gl) {
        gl.glBegin(GL.GL_POINTS);
        gl.glColor3f(.92f, 0.78f, 0.62f);
        for(float i = .5f; i < width; i++) {
            for(float j = .5f; j < height; j++) {
                gl.glVertex2f(i, j);
            }
        }
    }

	private void fillHoles() {
        holes = new ArrayList<>();
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

	private void drawRightPolygon(GL2 gl) {
        colourPointsInside(gl, width / 2);
        outlinePolygons(gl, width / 2);
	}

	private void outlinePolygons(GL2 gl, int xMod) {
        gl.glColor3f(0f, 0f, 0f);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for(int i = 0; i <  polygon.size(); i++) {
            gl.glVertex2f(polygon.getVertex(i).getX() + xMod, polygon.getVertex(i).getY());
        }
        gl.glEnd();


        for(int i = 0; i < holes.size(); i++) {
            gl.glColor3f(0f, 0f, 0f);
            gl.glBegin(GL2.GL_LINE_STRIP);
            for(int j = 0; j < holes.get(i).size(); j++) {
                gl.glVertex2f(holes.get(i).getVertex(j).getX() + xMod, holes.get(i).getVertex(j).getY());
            }
            gl.glEnd();
        }
    }

    private boolean isInsidePolygon(int x, int y) {
        boolean intersects = false;

        int i, j;
        for (i = 0, j = polygon.size()-1; i < polygon.size(); j = i++) {
            if ( ((polygon.getVertex(i).getY() > y) != (polygon.getVertex(j).getY()>y)) &&
                    (x < (polygon.getVertex(j).getX() - polygon.getVertex(i).getX()) * (y - polygon.getVertex(i).getY())
                    / (polygon.getVertex(j).getY() - polygon.getVertex(i).getY()) + polygon.getVertex(i).getX()) )
                intersects = !intersects;
        }

        return intersects;
    }

    private boolean isInsideHolePolygon(int x, int y) {
        boolean intersects = false;
        for(int poly = 0; poly < holes.size(); poly++) {
            int i, j;
            for (i = 0, j = holes.get(poly).size()-1; i < holes.get(poly).size(); j = i++) {
                if ( ((holes.get(poly).getVertex(i).getY() > y) != (holes.get(poly).getVertex(j).getY()>y)) &&
                        (x < (holes.get(poly).getVertex(j).getX() - holes.get(poly).getVertex(i).getX()) * (y - holes.get(poly).getVertex(i).getY())
                        / (holes.get(poly).getVertex(j).getY() - holes.get(poly).getVertex(i).getY()) + holes.get(poly).getVertex(i).getX()) )

                    intersects = !intersects;
            }
        }

        return intersects;
    }

	private void colourPointsInside(GL2 gl, int xMod) {
        boolean intersects;
        for(int i = 0; i < width / 2; i++) {
            for (int j = 0; j < height; j++) {
                boolean inside = isInsidePolygon(i, j);
                boolean inHole = isInsideHolePolygon(i, j);

                intersects = inside & !inHole;
                if(intersects) {
                    if((i + j) % 2 == 0) {
                        gl.glBegin(GL2.GL_POINTS);
                        gl.glColor3f(0f, 0f, 1f);
                        gl.glVertex2f(i + xMod+ .5f, j + .5f);
                        gl.glEnd();
                    }
                }
            }
        }

        gl.glEnd();
	}

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
