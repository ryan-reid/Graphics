import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class A3Q1 implements GLEventListener, MouseListener, MouseMotionListener {
	public static final boolean TRACE = false;

	public static final String WINDOW_TITLE = "A3Q1: [Ryan Reid]";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 480;
    public static Wand wand;
    public static boolean mousePressed;
    public static boolean mouseHasBeenDragged;
    public static int mouseX;
    public static int mouseY;
    public static int ID = 1;
    private static long lastTimeDragged = 0;
    private static ArrayList<Bubble> bubbles = new ArrayList<>();
    private static final int _THRESHOLD = 10;
    private static float _bubbleGeneration = 0;

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
		capabilities.setDoubleBuffered(true);
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
		canvas.setAutoSwapBufferMode(true);

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
	
	int width, height;
	float left, top, right, bottom;
	long time = 0;
	
	public void setup(final GLCanvas canvas) {
		// Called for one-time setup
		if (TRACE)
			System.out.println("-> executing setup()");

		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				canvas.repaint();
			}
		}, 1000, 1000/60);

        width = INITIAL_WIDTH;
        height = INITIAL_HEIGHT;
        wand = new Wand();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Called when the canvas is (re-)created - use it for initial GL setup
		if (TRACE)
			System.out.println("-> executing init()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		long delta = 0;
		long now = System.nanoTime();
		if (time != 0 && now - time < 100000000)
			delta = now - time;
		time = now;

		// Draws the display
		if (TRACE)
			System.out.println("-> executing display()");

		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        if(mousePressed) {
            detectIfWandSelected(gl);
        }

        if(mouseHasBeenDragged && wand._selected) {
            float distance = (float) Math.sqrt( Math.pow(mouseX - wand.oldMouseX, 2) + Math.pow(mouseY - wand.oldMouseY, 2));
            long timeSinceLastDrag = time - lastTimeDragged;
            timeSinceLastDrag /= 1000000;
            _bubbleGeneration += (float) (Math.random() * (distance / timeSinceLastDrag));

            if(_bubbleGeneration >= _THRESHOLD) {
                float volInX = mouseX - wand.oldMouseX;
                volInX /= (timeSinceLastDrag);
                float volInY = mouseY - wand.oldMouseY;
                volInY /= (timeSinceLastDrag);
                addBubble(timeSinceLastDrag, volInX, volInY);
                _bubbleGeneration = 0;
            }

            System.out.println(_bubbleGeneration);

            moveWand();
            lastTimeDragged = time;
        }

        drawBubbles(gl, delta);
        drawWand(gl, wand.colour);
	}


	public void drawBubbles(GL2 gl, float delta) {
        bubbles.forEach( bubble -> drawBubble(gl, bubble, delta));

        bubbles.removeIf( bubble -> bubble.markForDestruction);
    }

	public void moveWand() {
        wand.transX += (mouseX - wand.oldMouseX);
        wand.transY += (mouseY - wand.oldMouseY);

        wand.oldMouseX = mouseX;
        wand.oldMouseY = mouseY;

        mouseHasBeenDragged = false;
    }

	public void detectIfWandSelected(GL2 gl) {
        gl.glLoadIdentity();
        gl.glDrawBuffer(GL2.GL_BACK);
        Color color = new Color(wand._RGB);
        float[] colours = {color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
        drawWand(gl, colours);


        gl.glReadBuffer(GL2.GL_BACK);
        FloatBuffer buff = FloatBuffer.allocate(4);
        gl.glReadPixels(mouseX, mouseY, 1, 1, GL2.GL_RGBA, GL2.GL_FLOAT, buff);
        Color index = new Color(buff.get(0), buff.get(1), buff.get(2));
        int i = index.getRGB();

        if(wand._RGB == i) {
            wand._selected = true;
            wand.oldMouseX = mouseX;
            wand.oldMouseY = mouseY;
            System.out.println("Wand selected");
        } else {
            wand._selected = false;
        }

        mousePressed = false;
    }

    public void addBubble(float delta, float volX, float volY) {
        Bubble bubble = new Bubble(wand.transX, wand.transY + (wand.y2 * wand.scaleY) + 32, volX, volY, 3f, (float) (Math.random() * delta), (float) (Math.random() * delta));
        bubbles.add(bubble);
    }

	public void drawWand(GL2 gl, float[] colour) {
        drawShaft(gl, colour);
        drawCircle(gl, colour);
	}

    public void drawShaft(GL2 gl, float[] colour) {
        gl.glLoadIdentity();
        gl.glTranslatef(wand.transX, wand.transY, 0);
        gl.glPushMatrix();
        gl.glPushMatrix();
        gl.glScalef(wand.scaleX, wand.scaleY, 0);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f(colour[0], colour[1], colour[2]);
        gl.glVertex2f(wand.x1, wand.y1);
        gl.glVertex2f(wand.x1, wand.y2);
        gl.glVertex2f(wand.x2, wand.y2);
        gl.glVertex2f(wand.x2, wand.y1);
        gl.glEnd();
    }

    public void drawOuter(GL2 gl, float[] colour) {
        float radius = 8f;

        gl.glColor3f(colour[0], colour[1], colour[2]);
        gl.glTranslatef(0, (wand.y2 + -wand.y1) * wand.scaleY, 0);
        gl.glScalef(2, 4, 0);

        gl.glBegin(GL2.GL_POLYGON);

        for(double i = 0; i < 2 * Math.PI; i += Math.PI / 6)
            gl.glVertex3f((float) Math.cos(i) * radius, (float) Math.sin(i) * radius, 0.0f);

        gl.glEnd();
    }

    public void drawInner(GL2 gl) {
        float rad2 = 7f;
        gl.glColor3f(0f, 0f, 0f);
        gl.glTranslatef(0, (wand.y2 + -wand.y1) * wand.scaleY, 0);
        gl.glScalef(2, 4, 0);

        gl.glBegin(GL2.GL_POLYGON);
        for(double i = 0; i < 2 * Math.PI; i += Math.PI / 6)
            gl.glVertex3f((float) Math.cos(i) * rad2, (float) Math.sin(i) * rad2, 0.0f);

        gl.glEnd();
    }

    public void drawBubble(GL2 gl, Bubble bubble, float delta) {
        float modX = (delta * bubble.vx);
        float modY = (delta * bubble.vy);

        modX /= 100000000;
        modY /= 100000000;

        bubble.x += modX;
        bubble.y += modY;

        float radius = bubble.radius;
        gl.glLoadIdentity();
        gl.glTranslatef(bubble.x, bubble.y, 0f);
        gl.glScalef(bubble._scaleX, bubble._scaleY, 0);

        gl.glBegin(GL2.GL_POLYGON);
        for(double i = 0; i < 2 * Math.PI; i += Math.PI / 32) {
            gl.glColor3f((float) Math.random(), (float) Math.random(), (float) Math.random());
            gl.glVertex3f((float) Math.cos(i) * radius, (float) Math.sin(i) * radius, 0.0f);
        }

        gl.glEnd();

        radius = bubble.radius * .90f;
        gl.glLoadIdentity();
        gl.glColor3f(0f, 0f, 0f);
        gl.glTranslatef(bubble.x, bubble.y, 0f);
        gl.glScalef(bubble._scaleX, bubble._scaleY, 0);

        gl.glBegin(GL2.GL_POLYGON);
        for(double i = 0; i < 2 * Math.PI; i += Math.PI / 6)
            gl.glVertex3f((float) Math.cos(i) * radius, (float) Math.sin(i) * radius, 0.0f);

        gl.glEnd();


        if(bubble.x - (bubble.radius * bubble._scaleX) < 0 || bubble.x + (bubble.radius * bubble._scaleX) > width
        || bubble.y + (bubble.radius * bubble._scaleY) > height || bubble.y - (bubble.radius * bubble._scaleY) < 0) {
            destroyBubble(bubble);
        }
    }

    private void destroyBubble(Bubble bubble) {
        bubble.markForDestruction = true;
    }

    public void drawCircle(GL2 gl, float[] colour) {
        gl.glPopMatrix();
        drawOuter(gl, colour);
        gl.glPopMatrix();
        drawInner(gl);
    }
	
	public float lerp(float t, float a, float b) {
		return (1 - t) * a + t * b;
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
		if (TRACE)
			System.out.println("-> executing reshape(" + x + ", " + y + ", " + width + ", " + height + ")");

		final GL2 gl = drawable.getGL().getGL2();

		this.width = width;
		this.height = height;
		// TODO: choose your coordinate system
//		final float ar = (float)width / (height == 0 ? 1 : height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
//		left = ar < 1 ? -1.0f : -ar;
//		right = ar < 1 ? 1.0f : ar;
//		bottom = ar > 1 ? -1.0f : -1/ar;
//		top = ar > 1 ? 1.0f : 1/ar;
//		gl.glOrthof(left, right, bottom, top, -1.0f, 1.0f);
		gl.glOrthof(0, width, 0, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	class Bubble {
		public float x, y, vx, vy, _scaleX, _scaleY;
		public float radius;
        public boolean markForDestruction;
		
		public Bubble(float x, float y, float vx, float vy, float radius, float scaleX, float scaleY) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = radius;
            this._scaleX = scaleX;
            this._scaleY = scaleY;
            markForDestruction = false;
		}
		
	}

	class Wand {
        public float x1, x2, y2, y1, scaleX, scaleY, transX, transY;
        public float[] colour;
        int _RGB;
        public boolean _selected;
        public int oldMouseX, oldMouseY;

        public Wand() {
            x1 = -1;
            y1 = -1;
            x2 = 1;
            y2 = 1;
            scaleX = 2;
            scaleY = 30;
            transX = width / 2;
            transY = height / 2;
            colour = new float[3];
            colour[0] = 0;
            colour[1] = 0;
            colour[2] = 1;

            Color color = new Color(ID);
            _RGB = color.getRGB();
            ID+= 1;
        }
    }

	@Override
	public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = (height - e.getY());
        mouseHasBeenDragged = true;
        System.out.println("Mouse dragged to (" + e.getX() + "," + (height - e.getY()) + ")");
        ((GLCanvas)e.getSource()).repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
        System.out.println("Mouse pressed on (" + e.getX() + "," + (height - e.getY()) + ")");

        mousePressed = true;
        mouseX = e.getX();
        mouseY = (height - e.getY());
        ((GLCanvas)e.getSource()).repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
