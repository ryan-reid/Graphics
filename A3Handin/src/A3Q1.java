package A3Q1;

import com.jogamp.opengl.GLEventListener;

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
    private static final boolean TRACE = false;

	private static final String WINDOW_TITLE = "A3Q1.A3Q1: [Ryan Reid]";
	private static final int INITIAL_WIDTH = 640;
	private static final int INITIAL_HEIGHT = 480;
    private static Wand wand;
    private static boolean mousePressed;
    private static boolean mouseHasBeenDragged;
    private static int mouseX;
    private static int mouseY;
    private static int ID = 1;
    private static long lastTimeDragged = 0;
    private static ArrayList<Bubble> bubbles = new ArrayList<>();
    private static final int _THRESHOLD = 10;
    private static float _bubbleGeneration = 0;
    private static ArrayList<Particle> particles = new ArrayList<>();
    private static SliderBar xAxisBar;
    private static SliderBar yAxisBar;
    private static Slider xSlider;
    private static Slider ySlider;
    private static double yWind;
    private static double xWind;

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

    private int width, height;
    private long time = 0;
	
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

        xSlider = new Slider((width / 2) - 5, (width / 2) + 5, 4, 10, 0, width - (width / 3), 0, (width / 3), 0);
        ySlider = new Slider(width - 5, width - 11, (height / 2) - 5, (height / 2) + 5, 0, width - 5,  height - (height / 3), 0, (height / 3));

        xAxisBar = new SliderBar(width / 3, width - (width / 3), 4, 10);
        yAxisBar = new SliderBar(width - 5, width - 11, height / 3, height - (height / 3));
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
            detectSelectedObject(gl);
        }

        handleMouseDrag();

        bubbles.forEach(bubble -> bubble.incrementVelocity(xWind, yWind));
        particles.forEach(particle -> particle.incrementVelocity(xWind, yWind));

        drawSliders(gl);
        drawBubbles(gl, delta);
        drawWand(gl, wand.colour);
        drawParticles(gl, delta);
	}

	private void incrementVelocity(Drawable drawable) {
        drawable.vY += yWind;
        drawable.vX += xWind;
    }

	private void handleMouseDrag() {
        if(mouseHasBeenDragged && wand._selected) {
            float distance = (float) Math.sqrt( Math.pow(mouseX - wand.oldMouseX, 2) + Math.pow(mouseY - wand.oldMouseY, 2));
            long timeSinceLastDrag = time - lastTimeDragged;
            timeSinceLastDrag /= 1000000;
            timeSinceLastDrag /= 2;
            _bubbleGeneration += (float) (Math.random() * (distance / timeSinceLastDrag));

            if(_bubbleGeneration >= _THRESHOLD) {
                float volInX = (mouseX - wand.oldMouseX) * (float) (Math.random() * 2);
                volInX /= (timeSinceLastDrag);
                float volInY = mouseY - wand.oldMouseY * (float) (Math.random() * 2);
                volInY /= (timeSinceLastDrag);
                addBubble(timeSinceLastDrag, volInX, volInY);
                _bubbleGeneration = 0;
            }

            moveWand();
            lastTimeDragged = time;
        } else if(mouseHasBeenDragged && xSlider.selected) {
            moveXSlider();
        } else if(mouseHasBeenDragged && ySlider.selected) {
            moveYSlider();
        }
    }

    private void moveXSlider() {
        xSlider.x1 += (mouseX - ((xSlider.x1 + xSlider.x2) / 2));
        xSlider.x2 = xSlider.x1 + 10;

        if(xSlider.x2 > xSlider.maxX) {
            xSlider.x2 = xSlider.maxX;
            xSlider.x1 = xSlider.x2 - 10;
        } else if(xSlider.x1 < xSlider.minX) {
            xSlider.x1 = xSlider.minX;
            xSlider.x2 = xSlider.x1 + 10;
        }

        double velocityMod = (double) (((xSlider.x2 + xSlider.x1) / 2) - (xSlider.maxX + xSlider.minX) / 2) / (double) xSlider.maxX;

        xWind = velocityMod;
    }

    private void moveYSlider() {
        ySlider.y1 += (mouseY - ((ySlider.y1 + ySlider.y2) / 2));
        ySlider.y2 = ySlider.y1 + 10;

        if(ySlider.y2 > ySlider.maxY) {
            ySlider.y2 = ySlider.maxY;
            ySlider.y1 = ySlider.y2 - 10;
        } else if(ySlider.y1 < ySlider.minY) {
            ySlider.y1 = ySlider.minY;
            ySlider.y2 = ySlider.y1 + 10;
        }

        double velocityMod = (double) (((ySlider.y2 + ySlider.y1) / 2) - (ySlider.maxY + ySlider.minY) / 2) / (double) ySlider.maxY;

        yWind = velocityMod;
    }

    private void drawSliders(GL2 gl) {
        gl.glLoadIdentity();
        gl.glColor3f(1f, 1f, 1f);

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(xAxisBar.x1, xAxisBar.y1);
        gl.glVertex2f(xAxisBar.x1, xAxisBar.y2);
        gl.glVertex2f(xAxisBar.x2, xAxisBar.y2);
        gl.glVertex2f(xAxisBar.x2, xAxisBar.y1);

        gl.glVertex2f(yAxisBar.x1, yAxisBar.y1);
        gl.glVertex2f(yAxisBar.x1, yAxisBar.y2);
        gl.glVertex2f(yAxisBar.x2, yAxisBar.y2);
        gl.glVertex2f(yAxisBar.x2, yAxisBar.y1);

        gl.glEnd();

        drawXSliderBar(gl, new float[]{1,0,0});
        drawYSliderBar(gl, new float[]{1,0,0});
    }

    private void drawXSliderBar(GL2 gl, float[] colours) {
        gl.glLoadIdentity();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(colours[0], colours[1], colours[2]);
        gl.glVertex2f(xSlider.x1, xSlider.y1);
        gl.glVertex2f(xSlider.x1, xSlider.y2);
        gl.glVertex2f(xSlider.x2, xSlider.y2);
        gl.glVertex2f(xSlider.x2, xSlider.y1);
        gl.glEnd();
    }

    private void drawYSliderBar(GL2 gl, float[] colours) {
        gl.glLoadIdentity();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(colours[0], colours[1], colours[2]);
        gl.glVertex2f(ySlider.x1, ySlider.y1);
        gl.glVertex2f(ySlider.x1, ySlider.y2);
        gl.glVertex2f(ySlider.x2, ySlider.y2);
        gl.glVertex2f(ySlider.x2, ySlider.y1);
        gl.glEnd();
    }

    private void drawParticles(GL2 gl, long delta) {
        particles.forEach( particle -> drawSpecificParticle(particle, gl, delta));
        particles.removeIf( particle -> particle.markForDestruction);
    }

    private void drawSpecificParticle(Particle particle, GL2 gl, long delta) {
        float modX = (delta * particle.vX);
        float modY = (delta * particle.vY);

        modX /= 100000000;
        modY /= 100000000;

        particle._x += modX;
        particle._y += modY;

        gl.glLoadIdentity();
        gl.glColor3f((float) Math.random(), (float) Math.random(), (float) Math.random());
        gl.glPointSize(2);

        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex2f(particle._x, particle._y);
        gl.glEnd();

        particle.timeAlive += (float) (Math.random() * (delta / 1000000));

        if(particle.timeAlive >= 1000) {
            particle.markForDestruction = true;
        }
    }

    private void drawBubbles(GL2 gl, float delta) {
        bubbles.forEach( bubble -> drawBubble(gl, bubble, delta));

        destroyBubbles();
    }

    private void moveWand() {
        wand.transX += (mouseX - wand.oldMouseX);
        wand.transY += (mouseY - wand.oldMouseY);

        wand.oldMouseX = mouseX;
        wand.oldMouseY = mouseY;

        mouseHasBeenDragged = false;
    }

    private void drawAlllSelectableObjects(GL2 gl) {
        gl.glDrawBuffer(GL2.GL_BACK);
        Color color = new Color(wand._RGB);
        float[] colours = {color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
        drawWand(gl, colours);

        color = new Color(xSlider.RGB);
        colours = new float[]{color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
        drawXSliderBar(gl, colours);

        color = new Color(ySlider.RGB);
        colours = new float[]{color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
        drawYSliderBar(gl, colours);
    }

    private void detectSelectedObject(GL2 gl) {
        gl.glLoadIdentity();

        drawAlllSelectableObjects(gl);

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

        if(xSlider.RGB == i) {
            xSlider.selected = true;
            xSlider.oldMouseX = mouseX;
            xSlider.oldMouseY = mouseY;
            System.out.println("X Slider selected");
        } else {
            xSlider.selected = false;
        }

        if(ySlider.RGB == i) {
            ySlider.selected = true;
            ySlider.oldMouseX = mouseX;
            ySlider.oldMouseY = mouseY;
            System.out.println("Y Slider selected");
        } else {
            ySlider.selected = false;
        }

        mousePressed = false;
    }

    private void addBubble(float delta, float volX, float volY) {
        float scale = ((float) Math.random() * delta);
        Bubble bubble = new Bubble(wand.transX, wand.transY + (wand.y2 * wand.scaleY) + 32, volX, volY, 3f, scale, scale);
        bubbles.add(bubble);
    }

    private void drawWand(GL2 gl, float[] colour) {
        drawShaft(gl, colour);
        drawCircle(gl, colour);
	}

    private void drawShaft(GL2 gl, float[] colour) {
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

    private void drawOuter(GL2 gl, float[] colour) {
        float radius = 8f;

        gl.glColor3f(colour[0], colour[1], colour[2]);
        gl.glTranslatef(0, (wand.y2 + -wand.y1) * wand.scaleY, 0);
        gl.glScalef(2, 4, 0);

        gl.glBegin(GL2.GL_POLYGON);

        for(double i = 0; i < 2 * Math.PI; i += Math.PI / 6)
            gl.glVertex3f((float) Math.cos(i) * radius, (float) Math.sin(i) * radius, 0.0f);

        gl.glEnd();
    }

    private void drawInner(GL2 gl) {
        float rad2 = 7f;
        gl.glColor3f(0f, 0f, 0f);
        gl.glTranslatef(0, (wand.y2 + -wand.y1) * wand.scaleY, 0);
        gl.glScalef(2, 4, 0);

        gl.glBegin(GL2.GL_POLYGON);
        for(double i = 0; i < 2 * Math.PI; i += Math.PI / 6)
            gl.glVertex3f((float) Math.cos(i) * rad2, (float) Math.sin(i) * rad2, 0.0f);

        gl.glEnd();
    }

    private void drawBubble(GL2 gl, Bubble bubble, float delta) {
        float modX = (delta * bubble.vX);
        float modY = (delta * bubble.vY);

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
            bubble.markForDestruction = true;
        }
    }

    private void destroyBubbles() {
        bubbles.forEach( bubble -> {
            if(bubble.markForDestruction) {
                blowUpBubble(bubble);
            }
        });

        bubbles.removeIf(bubble -> bubble.markForDestruction);
    }

    private void blowUpBubble(Bubble bubble) {
        for(double i = 0; i < 2 * Math.PI; i += Math.PI / 128) {
            float velocityX = (float) (Math.random() * 2) - 1;
            float velocityY = (float) (Math.random() * 2) - 1;
            Particle particle = new Particle( (((float) Math.cos(i) * bubble.radius) * bubble._scaleX) + bubble.x , ((((float) Math.sin(i) * bubble.radius) * bubble._scaleY) + bubble.y), 2, velocityX, velocityY);
            particles.add(particle);
        }
    }

    private void drawCircle(GL2 gl, float[] colour) {
        gl.glPopMatrix();
        drawOuter(gl, colour);
        gl.glPopMatrix();
        drawInner(gl);
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

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, width, 0, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
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
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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
        wand._selected = false;
        xSlider.selected = false;
        ySlider.selected = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private abstract class Drawable {
        float vX, vY;

        public void incrementVelocity(double velocityX, double velocityY) {
            vX += velocityX;
            vY += velocityY;
        }
    }

    private class Bubble extends Drawable {
        float x, y, _scaleX, _scaleY;
        float radius;
        boolean markForDestruction;

        Bubble(float x, float y, float vx, float vy, float radius, float scaleX, float scaleY) {
            this.x = x;
            this.y = y;
            super.vX = vx;
            super.vY = vy;
            this.radius = radius;
            this._scaleX = scaleX;
            this._scaleY = scaleY;
            markForDestruction = false;
        }

    }

    private class Particle extends Drawable {
        float _x, _y;
        int _pointSize;
        long timeAlive;
        boolean markForDestruction;

        Particle(float x, float y, int pointSize, float velocityX, float velocityY) {
            _x = x;
            _y = y;
            _pointSize = pointSize;
            super.vX = velocityX;
            super.vY = velocityY;
            timeAlive = 0;
        }
    }

    private class Wand {
        float x1, x2, y2, y1, scaleX, scaleY, transX, transY;
        float[] colour;
        int _RGB;
        boolean _selected;
        int oldMouseX, oldMouseY;

        Wand() {
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
            ID++;
        }
    }

    private class SliderBar {
        int x1, x2, y1, y2;

        SliderBar(int x1, int x2, int y1, int y2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }
    }

    private class Slider {
        int x1, x2, y1, y2;
        int startingX, startingY;
        int maxX, maxY, minX, minY;
        float velocityMod;
        int RGB;
        boolean selected;
        int oldMouseX, oldMouseY;

        Slider(int x1, int x2, int y1, int y2, int velocityMod, int maxX, int maxY, int minX, int minY) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.velocityMod = velocityMod;
            this.startingX = (x1 + x2) / 2;
            this.startingY = (y1 + y2) / 2;
            this.maxX = maxX;
            this.maxY = maxY;
            this.minX = minX;
            this.minY = minY;

            Color color = new Color(ID);
            RGB = color.getRGB();
            ID++;
        }
    }
}
