import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.glu.*;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class A4Skeleton implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	public static final boolean TRACE = false;

	public static final String WINDOW_TITLE = "A4: [Ryan Reid]";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 640;

	private static final GLU glu = new GLU();

	private static final String TEXTURE_PATH = "resources/";
	
	// TODO: change this
	public static final String[] TEXTURE_FILES = { "circle.png" };

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

		canvas.requestFocusInWindow();

		if (TRACE)
			System.out.println("-> end of main().");
	}

	private static Class<?> self() {
		// This ugly hack gives us the containing class of a static method 
		return new Object() { }.getClass().getEnclosingClass();
	}

	/*** Instance variables and methods ***/

	private String direction;
	private Texture[] textures;

	// TODO: Add instance variables here
	

	public void setup(final GLCanvas canvas) {
		// Called for one-time setup
		if (TRACE)
			System.out.println("-> executing setup()");

		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				canvas.repaint();
			}
		}, 1000, 1000/60);

		// TODO: Add code here
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
		gl.glEnable(GL2.GL_CULL_FACE);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Draws the display
		if (TRACE)
			System.out.println("-> executing display()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		// TODO: Replace with your drawing code
		textures[0].bind(gl);
		textures[0].enable(gl);
		for (int xoff = -1; xoff <= 1; xoff++) {
			for (int yoff = -1; yoff <= 1; yoff++) {
				gl.glLoadIdentity();
				gl.glTranslatef(xoff, yoff, -1);
				gl.glColor3f(1, 1, 1);
				gl.glBegin(GL2.GL_QUADS);
				gl.glTexCoord2f(1, 1);
				gl.glVertex2f(0.5f, 0.5f);
				gl.glTexCoord2f(0, 1);
				gl.glVertex2f(-0.5f, 0.5f);
				gl.glTexCoord2f(0, 0);
				gl.glVertex2f(-0.5f, -0.5f);
				gl.glTexCoord2f(1, 0);
				gl.glVertex2f(0.5f, -0.5f);
				gl.glEnd();
				gl.glColor3f(0, 0, 1);
				gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glVertex2f(0.5f, 0.5f);
				gl.glVertex2f(-0.5f, 0.5f);
				gl.glVertex2f(-0.5f, -0.5f);
				gl.glVertex2f(0.5f, -0.5f);
				gl.glEnd();
			}
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
		float ar = (float)width / (height == 0 ? 1 : height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		
		// TODO: use a perspective projection instead
		gl.glOrthof(ar < 1 ? -1.0f : -ar, ar < 1 ? 1.0f : ar, ar > 1 ? -1.0f : -1/ar, ar > 1 ? 1.0f : 1/ar, 0.5f, 2.5f);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Change this however you like
		direction = null;
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyChar() == 'a')
			direction = "left";
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyChar() == 'd')
			direction = "right";
		else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyChar() == 'w')
			direction = "up";
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyChar() == 's')
			direction = "down";
		if (direction != null) {
			System.out.println("Direction key pressed: " + direction);
			((GLCanvas)e.getSource()).repaint();
		}
		if (e.getKeyChar() == ' ') {
			System.out.println("Space bar: jump!");
		} else if (e.getKeyChar() == '\n') {
			System.out.println("Enter: switch view");
		}
		// TODO: add more keys as necessary
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO: use this or mouse moved for free look
		System.out.println("drag: (" + e.getX() + "," + e.getY() + ") at " + e.getWhen());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO: you may need this
		System.out.println("press: (" + e.getX() + "," + e.getY() + ") at " + e.getWhen());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	
	// bring in shape & structure (or whatever you used) from A3Q2 here
	// include the OBJ reading code if you are using it
}
