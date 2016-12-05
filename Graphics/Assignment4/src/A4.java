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

public class A4 implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
    public static final boolean TRACE = false;

    public static final String WINDOW_TITLE = "A4: [Kenny Hong]";
    public static final int INITIAL_WIDTH = 640;
    public static final int INITIAL_HEIGHT = 640;

    private static final GLU glu = new GLU();

    private static final String TEXTURE_PATH = "resources/";

    // TODO: change this
    public static final String[] TEXTURE_FILES = { "circle.png", "circle.png", "circle.png", "circle.png", "circle.png", "circle.png", "circle.png", "circle.png", "circle.png"};

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
    private int isometric = 1;
    //float testAngle = 0.0f;
    private float qBertX = 0.0f;
    private float qBertZ = -12.0f;
    private float qBertY = 14.0f;
    private float qbNewX = 0.0f;
    private float qbNewZ = -12.0f;
    private float qbNewY = 14.0f;
    private boolean turnLeft = false;
    private boolean turnRight = false;
    private float newAngle = 45.0f;
    private float qBertAngle = 45.0f;
    private int faceDirection = 0;
    private int faceDirectionFP = 0;
    private boolean isTurning = false;
    private boolean isMoving = false;
    private float t = 0.0f;
    private float fpX = 0;
    private float fpY =  0f;
    private float fpZ = 0f;
    private float fpNX  = 0f;
    private float fpNY  = 0f;
    private float fpNZ  = 0f;
    private float fpAngle = 0;
    private float newFpAngle = 0;

    private boolean newScene = true;
    private boolean justChanged = false;
    private float fpTestAngleX = 10.5f;
    private float fpTestAngleY = 78.5f;
    private float fpTestAngleZ = 11.0f;

    private float fpTestTX = 12.5f;
    private float fpTestTY = -14.0f;
    private float fpTestTZ = -17.0f;

    private float sceneNX = 1.55f;
    private float sceneNY = -1.5f;
    private float sceneNZ = -3.4f;

    private float specialAngle1 = 0.0f;
    private float specialAngle2 = 0.0f;

    private float[] colour = new float[] { 0.2f, 0.6f, 1.0f, 0.0f };
    private boolean enableFog = false;
    private float density = 0.045f;

    private float prevDragX;
    private float prevDragY;

    private float viewX = 0.0f;
    private float viewY = 42.0f;




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
        //gl.glEnable(GL2.GL_CULL_FACE);


    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Draws the display
        if (TRACE)
            System.out.println("-> executing display()");

        final GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glLoadIdentity();

        if (enableFog)
        {
            // You can set this up in init() if you don't need to change it
            gl.glEnable(GL2.GL_FOG);
            gl.glFogfv(GL2.GL_FOG_COLOR, colour, 0);
            gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
            gl.glFogf(GL2.GL_FOG_DENSITY, density);
        }
        else
        {
            gl.glDisable(GL2.GL_FOG);
        }

        gl.glPushMatrix();

        if(newScene)
        {
            newScene = false;
            isometric = 0;
        }

        if(isometric == 0)
        {

            enableFog = false;
            gl.glTranslatef(sceneNX, sceneNY, sceneNZ);

            gl.glRotatef(35.0f, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);

            gl.glScalef(0.18f, 0.18f, 0.18f);
        }
        else
        {
            enableFog = true;
            justChanged = false;
            //gl.glRotatef(viewY, 1.0f, 0.0f, 0.0f);
            //gl.glRotatef(viewX, 0.0f, 0.1f, 0.0f);
            if(turnLeft || turnRight)
            {
                if (turnLeft) {
                    fpAngle = (fpAngle + 5.0f ) % 360;

                    if (fpAngle == newFpAngle) {
                        turnLeft = false;
                        isTurning = false;
                        fpAngle = newFpAngle;
                    }
                }
                if (turnRight) {
                    fpAngle = (fpAngle + 355.0f) % 360;

                    if (fpAngle == newFpAngle) {
                        turnRight = false;
                        isTurning = false;
                        fpAngle = newFpAngle;
                    }
                }
                //gl.glRotatef(-fpAngle, 0.0f, 1.0f, 0.0f);
            }
            else
            {
                gl.glRotatef(-newFpAngle, 0.0f, 1.0f, 0.0f);
                fpAngle = newFpAngle;
            }

           // glu.gluLookAt(3.0f , 3f , -16.0f , 0 , 0 , 0, 0 , 4 , 4);
            gl.glTranslatef(fpTestTX, fpTestTY, fpTestTZ);
            gl.glRotatef(fpTestAngleY, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(fpTestAngleX, 1.0f,0.0f,0.0f);
            gl.glRotatef(fpTestAngleZ, 0.0f, 0.0f, 1.0f);
            if(isMoving)
            {
                fpZ = lerp(t, fpZ, fpNZ);
                fpY = lerp(t, fpY, fpNY);
                fpX = lerp(t, fpX, fpNX);
                t += 0.03 * Math.PI;
                gl.glTranslatef(fpX,  fpY + (float)Math.sin(t * Math.PI), fpZ);
                if(t > 1)
                {
                    isMoving = false;
                    t = 0;
                    fpZ = fpNZ;
                    fpY = fpNY;
                    fpX = fpNX;
                }
            }
            else
            {
                gl.glTranslatef(fpNX, fpNY, fpNZ);
                fpZ = fpNZ;
                fpY = fpNY;
                fpX = fpNX;
            }

        }
        gl.glPushMatrix();
        gl.glScalef(150.0f, 1.0f, 155f);
        gl.glTranslatef(0.0f, 0.0f, -.5f);
        drawFloorFP(gl);
        gl.glPopMatrix();


        drawStage(gl);

        gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Called when the canvas is destroyed (reverse anything from init)
        if (TRACE)
            System.out.println("-> executing dispose()");
    }

    public void drawOddish(GL2 gl)
    {

        float x, y;
        final float INC = 0.0001f;
        GLUquadric gluNewQuadric = glu.gluNewQuadric();


        gl.glPushMatrix();
        gl.glTranslatef(-1.0f, 0.5f, -0.76F);
        gl.glRotatef(135.0f, 0.0f, -1.0f ,0.0f);
        gl.glScalef(0.2f, 0.3f, 1);
        gl.glColor3f(1.0f, 0.4f, 0.8f);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2f(0, 0);
        for (float t = 0.5f; t <= 1.00f; t += INC) {
            x = (float)(Math.cos(2.0f * Math.PI * t));
            y = (float)(Math.sin(2.0f * Math.PI * t));
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
        gl.glPopMatrix();

        gl.glColor3f(1.0f,0.0f,0.0f);
        gl.glPushMatrix();
        gl.glTranslatef(-0.60f, 0.5f, -1.0f);
        gl.glRotatef(65.0f, 0.0f, 1.0f ,0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f ,0.0f);
        glu.gluSphere(gluNewQuadric, 0.1f, 25, 25);
        gl.glPopMatrix();

        gl.glColor3f(1.0f,0.0f,0.0f);
        gl.glPushMatrix();
        gl.glTranslatef(-1.15f, 0.5f, -0.325f);
        gl.glRotatef(65.0f, 0.0f, 1.0f ,0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f ,0.0f);
        glu.gluSphere(gluNewQuadric, 0.1f, 25, 25);
        gl.glPopMatrix();


        gl.glColor3f(0.2f, 0.8f, 0.2f);
        gl.glPushMatrix();
        gl.glTranslatef(0.2f, 2.7f, 0.4f);
        gl.glRotatef(45.0f, 0.0f, -1.0f ,0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(0.3f, 1.0f, 1.3f);
        glu.gluCylinder(gluNewQuadric, 0.02, 0.7, 1.5, 25, 25);
        gl.glPopMatrix();

        gl.glColor3f(0.2f,0.4f,1.0f);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.2f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, -1.0f ,0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(1.3f, 1.6f, 1.3f);
        glu.gluSphere(gluNewQuadric, 0.9f, 25, 25);
        gl.glPopMatrix();

        gl.glColor3f(0.2f, 0.8f, 0.2f);
        gl.glPushMatrix();
        gl.glTranslatef(-1.2f, 1.7f, 1.6f);
        gl.glRotatef(45.0f, 0.0f, -1.0f ,0.0f);
        gl.glRotatef(155.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(0.3f, 0.8f, 1.3f);
        glu.gluCylinder(gluNewQuadric, 0.02, 0.7, 1.5, 25, 25);
        gl.glPopMatrix();

        gl.glColor3f(0.2f, 0.8f, 0.2f);
        gl.glPushMatrix();
        gl.glTranslatef(1.7f, 1.7f, -1.2f);
        gl.glRotatef(45.0f, 0.0f, -1.0f ,0.0f);
        gl.glRotatef(25.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(0.3f, 0.8f, 1.3f);
        glu.gluCylinder(gluNewQuadric, 0.02, 0.7, 1.5,25, 25);
        gl.glPopMatrix();gl.glColor3f(0.2f, 0.8f, 0.2f);

        gl.glPushMatrix();
        gl.glTranslatef(1.0f, 2.2f,-0.3f);
        gl.glRotatef(45.0f, 0.0f, -1.0f ,0.0f);
        gl.glRotatef(60.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(0.3f, 0.8f, 1.3f);
        glu.gluCylinder(gluNewQuadric, 0.02, 0.7, 1.5, 25, 25);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(-0.6f, 2.2f, 1.1f);
        gl.glRotatef(45.0f, 0.0f, -1.0f ,0.0f);
        gl.glRotatef(120.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(0.3f, 0.8f, 1.3f);
        glu.gluCylinder(gluNewQuadric, 0.02, 0.7, 1.5, 25, 25);
        gl.glPopMatrix();

        gl.glColor3f(0.2f,0.4f,1.0f);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.2f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, -1.0f ,0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(1.3f, 1.6f, 1.3f);
        glu.gluSphere(gluNewQuadric, 0.8f, 25, 25);
        gl.glPopMatrix();

        gl.glColor3f(0.2f,0.4f,1.0f);
        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, -0.8f, 0.75f);
        gl.glRotatef(65.0f, 0.0f, 1.0f ,0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(0.5f, 1.6f, 1.0f);
        glu.gluSphere(gluNewQuadric, 0.4f, 25, 25);
        gl.glPopMatrix();

        gl.glColor3f(0.2f,0.4f,1.0f);
        gl.glPushMatrix();
        gl.glTranslatef(0.5f, -0.8f, -0.75f);
        gl.glRotatef(20.0f, 0.0f, 1.0f ,0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f ,0.0f);
        gl.glScalef(0.5f, 1.6f, 1.0f);
        glu.gluSphere(gluNewQuadric, 0.4f, 25, 25);
        gl.glPopMatrix();
    }
    public void drawCube(GL2 gl)
    {
        int c = 0;
        float[][] verts = {
                { -1, -1, -1 },	// llr
                { -1, -1, 1 },  // llf
                { -1, 1, -1 },	// lur
                { -1, 1, 1 },	// luf
                { 1, -1, -1 },	// rlr
                { 1, -1, 1 },	// rlf
                { 1, 1, -1 },	// rur
                { 1, 1, 1 }     // ruf
        };

        int[][] faces = {
                { 1, 5, 7, 3 }, // front
                { 4, 0, 2, 6 }, // rear
                { 3, 7, 6, 2 }, // top
                { 0, 4, 5, 1 }, // bottom
                { 0, 1, 3, 2 }, // left
                { 5, 4, 6, 7 }, // right
        };

        // outline
        final float OFF = 0.00f;
        gl.glColor3f(1, 1, 1);
        for (int[] face: faces) {
            gl.glBegin(GL2.GL_LINE_LOOP);
            for (int i: face) {
                gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            }
            gl.glEnd();
        }


    //FRONT
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[0])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //REAR
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[1])
        {
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
        }
        gl.glEnd();



        //TOP
        //FRONT
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[2])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //BOTTOM
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[3])
        {
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
        }
        gl.glEnd();

        //LEFT
        c=0;
        textures[0].bind(gl);
        textures[0].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[4])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[0].disable(gl);

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[5])
        {
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
        }
        gl.glEnd();

    }

    public void drawSpecialCube(GL2 gl)
    {
        int c = 0;
        float[][] verts = {
                { -1, -1, -1 },	// llr
                { -1, -1, 1 },  // llf
                { -1, 1, -1 },	// lur
                { -1, 1, 1 },	// luf
                { 1, -1, -1 },	// rlr
                { 1, -1, 1 },	// rlf
                { 1, 1, -1 },	// rur
                { 1, 1, 1 }     // ruf
        };

        int[][] faces = {
                { 1, 5, 7, 3 }, // front
                { 4, 0, 2, 6 }, // rear
                { 3, 7, 6, 2 }, // top
                { 0, 4, 5, 1 }, // bottom
                { 0, 1, 3, 2 }, // left
                { 5, 4, 6, 7 }, // right
        };

        // outline
        final float OFF = 0.00f;
        gl.glColor3f(0, 0, 0);
        for (int[] face: faces) {
            gl.glBegin(GL2.GL_LINE_LOOP);
            for (int i: face) {
                gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            }
            gl.glEnd();
        }


        //FRONT
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[0])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //REAR
        c=0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[1])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);



        //TOP
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[2])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //Bottom
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[3])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //LEFT
        c=0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[4])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);


        //right
        c=0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[5])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);
    }

    public void drawSpecialCube2(GL2 gl)
    {
        int c = 0;
        float[][] verts = {
                { -1, -1, -1 },	// llr
                { -1, -1, 1 },  // llf
                { -1, 1, -1 },	// lur
                { -1, 1, 1 },	// luf
                { 1, -1, -1 },	// rlr
                { 1, -1, 1 },	// rlf
                { 1, 1, -1 },	// rur
                { 1, 1, 1 }     // ruf
        };

        int[][] faces = {
                { 1, 5, 7, 3 }, // front
                { 4, 0, 2, 6 }, // rear
                { 3, 7, 6, 2 }, // top
                { 0, 4, 5, 1 }, // bottom
                { 0, 1, 3, 2 }, // left
                { 5, 4, 6, 7 }, // right
        };

        // outline
        final float OFF = 0.00f;
        gl.glColor3f(0, 0, 0);
        for (int[] face: faces) {
            gl.glBegin(GL2.GL_LINE_LOOP);
            for (int i: face) {
                gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            }
            gl.glEnd();
        }


        //FRONT
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[0])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //REAR
        c=0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[1])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);



        //TOP
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[2])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //Bottom
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[3])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        //LEFT
        c=0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[4])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);


        //right
        c=0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces[5])
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);
    }

    public void drawStage(GL2 gl)
    {

        int stageSize = 7;
        int levelStep = 7;
        float z = 0.0f;
        float stepsZ = 0.0f;
        float y = 0.0f;
        float x = 0.0f;

        gl.glPushMatrix();
        for(int i = 0; i < levelStep; i++)
        {
            for (int j = 0; j < stageSize; j++) {

                gl.glPushMatrix();
                gl.glTranslatef(x, y, z);
                drawCube(gl);
                gl.glPopMatrix();
                z -= 2.0f;
                y += 2.0;
            }
            stepsZ -= 2.0f;
            z = stepsZ;
            y = 0.0f;
            x -= 2.0f;
            stageSize--;
        }
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(-6.0f, 13.0f, -12.0f);
        gl.glRotatef(specialAngle1, 0.0f, 1.0f, 0.0f);
        specialAngle1 += 1.0f;
        if(specialAngle1 == 360.0f)
        {
            specialAngle1 = 0;
        }
        drawSpecialCube(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 13.0f, -6.0f);
        gl.glRotatef(specialAngle2, -1.0f, 0.0f, 0.0f);
        specialAngle2 += 1.0f;
        if(specialAngle2 == 360.0f)
        {
            specialAngle2 = 0;
        }
        drawSpecialCube2(gl);
        gl.glPopMatrix();

        if(isometric == 0)
        {
            gl.glPushMatrix();

            if (isMoving) {
                qBertZ = lerp(t, qBertZ, qbNewZ);
                qBertY = lerp(t, qBertY, qbNewY);
                qBertX = lerp(t, qBertX, qbNewX);
                t += 0.03 * Math.PI;
                //gl.glTranslatef(qBertX, qBertY +  (float) Math.sin(t* Math.PI), qBertZ);
                if (t > 1) {
                    isMoving = false;
                    t = 0;
                    qBertZ = qbNewZ;
                    qBertY = qbNewY;
                    qBertX = qbNewX;
                }
            }
            else
            {
                gl.glTranslatef(qbNewX, qbNewY, qbNewZ);
                qBertZ = qbNewZ;
                qBertY = qbNewY;
                qBertX = qbNewX;
            }

            if(turnLeft || turnRight) {
                if (turnLeft) {
                    qBertAngle = (qBertAngle + 355.0f) % 360;
                    //System.out.println(qBertAngle + " | " + newAngle);
                    if (qBertAngle == newAngle) {
                        turnLeft = false;
                        isTurning = false;
                        qBertAngle = newAngle;
                    }
                }
                if (turnRight) {
                    qBertAngle = (qBertAngle + 5.0f) % 360;
                    //System.out.println(qBertAngle + " | " + newAngle);
                    if (qBertAngle == newAngle) {
                        turnRight = false;
                        isTurning = false;
                        qBertAngle = newAngle;
                    }
                }
                gl.glRotatef(qBertAngle, 0.0f, 1.0f, 0.0f);
            }
            else
            {
                gl.glRotatef(newAngle, 0.0f, 1.0f, 0.0f);
                qBertAngle = newAngle;
            }

            gl.glPushMatrix();
            gl.glTranslatef(0.0f, -0.4f, -0.0f);
            gl.glScalef(0.60f, 0.60f, 0.60f);
            drawOddish(gl);
            gl.glPopMatrix();

            gl.glPopMatrix();
        }
    }

    public float lerp(float t, float a, float b) {
        return (1 - t) * a + t * b;
    }

    public void drawFloorFP(GL2 gl)
    {
        int c = 0;
        float[][] verts = {
                { -1, -1, -1 },	// llr
                { -1, -1, 1 },  // llf
                { -1, 1, -1 },	// lur
                { -1, 1, 1 },	// luf
                { 1, -1, -1 },	// rlr
                { 1, -1, 1 },	// rlf
                { 1, 1, -1 },	// rur
                { 1, 1, 1 }     // ruf
        };

        int [] faces = { 0, 4, 5, 1 }; // bottom

        //Bottom
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces)
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef(2.0f, 0.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces)
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef(0.0f, 0.0f, 2.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces)
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef(2.0f, 0.0f, 2.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces)
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef(4.0f, 0.0f, 6.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces)
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);
        c = 0;
        textures[1].bind(gl);
        textures[1].enable(gl);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef(0.0f, 0.0f, 4.0f);
        gl.glBegin(GL2.GL_QUADS);
        for(int i : faces)
        {
            if(c == 0)
            {
                gl.glTexCoord2f(0, 0);
            }
            else if (c == 1)
            {
                gl.glTexCoord2f(1, 0);
            }
            else if (c == 2)
            {
                gl.glTexCoord2f(1, 1);
            }
            else if (c ==3)
            {
                gl.glTexCoord2f(0, 1);
            }
            gl.glVertex3f(verts[i][0], verts[i][1], verts[i][2]);
            c++;
        }
        gl.glEnd();
        textures[1].disable(gl);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Called when the canvas has been resized
        // Note: glViewport(x, y, width, height) has already been called so don't bother if that's what you want
        if (TRACE)
            System.out.println("-> executing reshape(" + x + ", " + y + ", " + width + ", " + height + ")");

        final GL2 gl = drawable.getGL().getGL2();

        float ar = (float)width / (height == 0 ? 1 : height);

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);

        // TODO: use a perspective projection instead
        //gl.glOrthof(ar < 1 ? -1.0f : -ar, ar < 1 ? 1.0f : ar, ar > 1 ? -1.0f : -1/ar, ar > 1 ? 1.0f : 1/ar, 0.5f, 2.5f);

        if(isometric == 0)
        {
            gl.glLoadIdentity();
            //glu.gluPerspective(80.0f, ar, 1.0f, 1000.0f);
            gl.glFrustum(-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 10.0f);
        }
        else
        {
            gl.glLoadIdentity();
            glu.gluPerspective(60.0f, ar, 0.01f, 15000.0f);
        }



    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        direction = null;
//
//
//        if(e.getKeyChar() == 'o')
//        {
//            fpTestAngleZ += 0.5f;
//            System.out.println("Angle: X: " + fpTestAngleX + "Y: " + fpTestAngleY + "Z: " + fpTestAngleZ);
//        }
//        if(e.getKeyChar() == 'l')
//        {
//            fpTestAngleZ -= 0.5f;
//            System.out.println("Angle: X: " + fpTestAngleX + "Y: " + fpTestAngleY + "Z: " + fpTestAngleZ);
//        }
//        if(e.getKeyChar() == 'i')
//        {
//            fpTestAngleY += 0.5f;
//            System.out.println("Angle: X: " + fpTestAngleX + "Y: " + fpTestAngleY + "Z: " + fpTestAngleZ);
//        }
//        if(e.getKeyChar() == 'k')
//        {
//            fpTestAngleY -= 0.5f;
//            System.out.println("Angle: X: " + fpTestAngleX + "Y: " + fpTestAngleY + "Z: " + fpTestAngleZ);
//        }
//        if(e.getKeyChar() == 'u')
//        {
//            fpTestAngleX += 0.5f;
//            System.out.println("Angle: X: " + fpTestAngleX + "Y: " + fpTestAngleY + "Z: " + fpTestAngleZ);
//        }
//        if(e.getKeyChar() == 'j')
//        {
//            fpTestAngleX -= 0.5f;
//            System.out.println("Angle: X: " + fpTestAngleX + "Y: " + fpTestAngleY + "Z: " + fpTestAngleZ);
//        }
//        if(e.getKeyChar() == 'h')
//        {
//            fpTestTX -= 0.5f;
//            System.out.println("World View: X: " + fpTestTX + "Y: " + fpTestTY + "Z: " + fpTestTZ);
//        }
//
//        if(e.getKeyChar() == 'y')
//        {
//            fpTestTX += 0.5f;
//            System.out.println("World View: X: " + fpTestTX + "Y: " + fpTestTY + "Z: " + fpTestTZ);
//        }
//        if(e.getKeyChar() == 'g')
//        {
//            fpTestTY -= 0.5f;
//            System.out.println("World View: X: " + fpTestTX + "Y: " + fpTestTY + "Z: " + fpTestTZ);
//        }
//        if(e.getKeyChar() == 't')
//        {
//            fpTestTY += 0.5f;
//            System.out.println("World View: X: " + fpTestTX + "Y: " + fpTestTY + "Z: " + fpTestTZ);
//        }
//        if(e.getKeyChar() == 'f')
//        {
//            fpTestTZ -= 0.5f;
//            System.out.println("World View: X: " + fpTestTX + "Y: " + fpTestTY + "Z: " + fpTestTZ);
//        }
//        if(e.getKeyChar() == 'r')
//        {
//            fpTestTZ += 0.5f;
//            System.out.println("World View: X: " + fpTestTX + "Y: " + fpTestTY + "Z: " + fpTestTZ);
//        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyChar() == 'a') {
            if(!isTurning && !isMoving) {
                direction = "left";
                turnLeft = true;
                if(isometric == 0)
                {
                    newAngle = (newAngle + 270) % 360;
                    faceDirection--;
                    newFpAngle = (newFpAngle + 270) % 360;
                    faceDirectionFP++;
                }
                else
                {
                    newAngle = (newAngle + 90) % 360;
                    faceDirection++;
                    newFpAngle = (newFpAngle + 90) % 360;
                    faceDirectionFP--;
                }

                if (faceDirection < 0)
                {
                    faceDirection = 3;
                }
                if (faceDirection > 3)
                {
                    faceDirection = 0;
                }
                if (faceDirectionFP < 0)
                {
                    faceDirectionFP = 3;
                }
                if (faceDirectionFP > 3)
                {
                    faceDirectionFP = 0;
                }
                System.out.println(faceDirection +  " | " + faceDirectionFP);
                isTurning = true;
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyChar() == 'd') {
            if(!isTurning && !isMoving) {
                direction = "right";
                turnRight = true;
                if(isometric == 0)
                {
                    newAngle = (newAngle + 90) % 360;
                    faceDirection++;
                    newFpAngle = (newFpAngle + 90) % 360;
                    faceDirectionFP--;
                }
                else
                {
                    newFpAngle = (newFpAngle + 270) % 360;
                    faceDirectionFP++;
                    newAngle = (newAngle + 270) % 360;
                    faceDirection--;
                }


                if (faceDirection < 0)
                {
                    faceDirection = 3;
                }
                if (faceDirection > 3)
                {
                    faceDirection = 0;
                }
                if (faceDirectionFP < 0)
                {
                    faceDirectionFP = 3;
                }
                if (faceDirectionFP > 3)
                {
                    faceDirectionFP = 0;
                }
                System.out.println(faceDirection +  " | " + faceDirectionFP);
                isTurning = true;
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyChar() == 'w') {
            if(!isTurning && !isMoving)
            {
                direction = "up";

                if(faceDirection == 2)
                {
                    isMoving = true;
                    qbNewX += 2.0f;
                    qbNewY += 2.0f;
                    fpNX -= 2.0f;
                    fpNY -= 2.0f;

                    if(qbNewX > 0)
                    {
                        qbNewZ = -12.0f;
                        qbNewY = 14.0f;
                        qbNewX = 0.0f;

                        fpNX = 0.0f;
                        fpNY = 0.0f;
                        fpNZ = 0.0f;
                    }
                }
                else if (faceDirection == 3)
                {
                    isMoving = true;
                    qbNewZ -= 2.0f;
                    qbNewY += 2.0f;
                    fpNZ += 2.0f;
                    fpNY -= 2.0f;

                    if(qbNewZ < -12)
                    {
                        qbNewZ = -12.0f;
                        qbNewY = 14.0f;
                        qbNewX = 0.0f;

                        fpNX = 0.0f;
                        fpNY = 0.0f;
                        fpNZ = 0.0f;
                    }
                }
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyChar() == 's')
        {
            if(!isTurning && !isMoving)
            {
                direction = "down";

                if(faceDirection == 0)
                {
                    isMoving = true;
                    qbNewX -= 2.0f;
                    qbNewY -= 2.0f;
                    fpNY += 2.0f;
                    fpNX += 2.0f;

                    if(qbNewY < 2)
                    {
                        qbNewZ = -12.0f;
                        qbNewY = 14.0f;
                        qbNewX = 0.0f;

                        fpNX = 0.0f;
                        fpNY = 0.0f;
                        fpNZ = 0.0f;
                    }
                }
                else if (faceDirection == 1 )
                {
                    isMoving = true;
                    qbNewZ += 2.0f;
                    qbNewY -= 2.0f;
                    fpNZ -= 2.0f;
                    fpNY += 2.0f;
                    if(qbNewY < 2)
                    {
                        qbNewZ = -12.0f;
                        qbNewY = 14.0f;
                        qbNewX = 0.0f;

                        fpNX = 0.0f;
                        fpNY = 0.0f;
                        fpNZ = 0.0f;
                    }
                }
            }
        }

        if (direction != null) {
            System.out.println("Direction key pressed: " + direction);
            ((GLCanvas)e.getSource()).repaint();
        }
        if (e.getKeyChar() == ' ')
        {
            isometric = (isometric + 1) % 2;
            // anything else...
        }
        // TODO: add more keys as necessary
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO: you will need this
        System.out.println("drag: (" + e.getX() + "," + e.getY() + ") at " + e.getWhen());

        float differenceX = e.getX() - prevDragX;
        float differenceY = e.getY() - prevDragY;

        if(viewY < -90)
        {
            viewY = -90;
        }
        if(viewY > 90)
        {
            viewY = 90;
        }
        viewY += differenceY / 5;
        viewX += differenceX /5;

        prevDragX = e.getX();
        prevDragY = e.getY();


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
        prevDragX = e.getX();
        prevDragY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
}
