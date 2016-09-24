import java.awt.Frame;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class A1Q1Skeleton implements GLEventListener {
	public static final String WINDOW_TITLE = "A1Q1: [Ryan Reid]";
	public static final int INITIAL_WIDTH = 640;
    public static final float PI = (float) Math.PI;
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

	public void setup(final GLCanvas canvas) {
	}

	private int width = INITIAL_WIDTH;
	private int height = INITIAL_HEIGHT;

	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();

		gl.glBegin(GL2.GL_POINTS);
		drawPlasma(gl);
		gl.glEnd();
	}

	private static float sin(Float number) {
        return (float) Math.sin(number);
    }

    private static float cos(Float number) {
        return (float) Math.cos(number);
    }

    private static float tan(Float number) {
        return (float) Math.tan(number);
    }

    private static float sqrt(Float number) {
        return (float) Math.sqrt(number);
    }

	private void drawPlasma(GL2 gl) {
        float scalar = .08f;
        float colour = 0;
        float scaledX;
        float scaledY;
		for(float x = 0.5f; x < width; x++) {
			for(float y = 0.5f; y < height; y++) {
                scaledX = x * scalar;
                scaledY = y * scalar;

                colour = sin(scaledX);
                colour += sin((scaledX * sin(5/2f) + (y * cos(5/3f))));
                colour += sin(sqrt(((scaledX * scaledX) + (scaledY *scaledY)) + 1));

                gl.glColor3f(sin(colour * PI), sin(colour * PI + 2 * PI / 3), sin(colour * PI + 4 * PI / 3));
				gl.glVertex2f(x, y);
			}
		}
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
