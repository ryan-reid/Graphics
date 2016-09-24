import java.awt.Frame;
import java.awt.event.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt .*;

public class Checkerboard implements GLEventListener {
    public static final boolean TRACE = true;

    public static final String WINDOW_TITLE = "Checkerboard";
    public static final int INITIAL_WIDTH = 640;
    public static final int INITIAL_HEIGHT = 640;

    public static void main(String[] args) {
        final Frame frame = new Frame(WINDOW_TITLE);
        addListenerToFrame(frame);

        GLCanvas canvas = getCanvas();
        addListener(canvas);
        canvas.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);

        setupFrame(frame, canvas);

        System.out.println("\nEnd of processing.");
    }

    private static void setupFrame(Frame frame, GLCanvas canvas) {
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

    private static void addListenerToFrame(Frame frame) {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private static void addListener(GLCanvas canvas) {
        try {
            canvas.addGLEventListener((GLEventListener)(self().getConstructor().newInstance()));
        } catch (Exception e) {
            assert(false);
        }
    }

    private static GLCanvas getCanvas() {
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);

        return canvas;
    }

    private static Class<?> self() {
        // This ugly hack gives us the containing class of a static method
        return new Object() { }.getClass().getEnclosingClass();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Called when the canvas is (re-)created - use it for initial GL setup
        if (TRACE) {
            System.out.println("-> executing init()");
        }

        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Draws the display
        if (TRACE) {
            System.out.println("-> executing display()");
        }

        final GL2 gl = getAndSetupGL(drawable);

        drawCheckerboard(gl);

        gl.glEnd();
    }

    private static void drawCheckerboard(GL2 gl) {
        final int DIM = 32;
        for (int x = 0; x < INITIAL_WIDTH/DIM; x++) {
            for (int y = 0; y < INITIAL_HEIGHT/DIM; y++) {

                if ((x + y) % 2 == 0) {
                    gl.glVertex2f(x * DIM, y * DIM);
                    gl.glVertex2f((x + 1) * DIM, y * DIM);
                    gl.glVertex2f((x + 1) * DIM, (y + 1) * DIM);
                    gl.glVertex2f(x * DIM, (y + 1) * DIM);
                }
            }
        }
    }

    private static GL2 getAndSetupGL(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);

        return gl;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Called when the canvas is destroyed (reverse anything from init)
        if (TRACE) {
            System.out.println("-> executing dispose()");
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Called when the canvas has been resized
        if (TRACE)  {
            System.out.println("-> executing reshape(" + x + ", " + y + ", " + width + ", " + height + ")");
        }

        final GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(x, x + width, y, y + height, 0.0f, 10.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}
