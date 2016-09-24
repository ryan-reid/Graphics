import java.awt.Frame;
import java.awt.event.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class A1Q2RyanReid implements GLEventListener {
	public static final String WINDOW_TITLE = "A1Q2: [Your name here]"; // TODO: change
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
	
	private static float PIPES[][] = {
			/* ONE */
			{ -0.8913044f, 0.81987584f },
			{ -0.59316766f, 0.89441f },
			{ -0.40993786f, 0.11490691f },
			{ -0.7795031f, 0.065217376f },

			{ -0.621118f, -0.049689412f },
			{ -0.621118f, 0.20496893f },
			{ -0.03416145f, 0.21118009f },
			{ -0.009316742f, -0.03726709f },

			{ 0.13975155f, 0.105590105f },
			{ -0.102484465f, 0.0869565f },
			{ -0.021739125f, -0.38819873f },
			{ 0.16770184f, -0.26708072f },

			{ 0.07142854f, -0.40993786f },
			{ 0.04037273f, -0.28881985f },
			{ -0.71552796f, -0.38819873f },
			{ -0.6751553f, -0.5124223f },

			{ -0.83975155f, -0.50621116f },
			{ -0.62236024f, -0.41925466f },
			{ 0.09316778f, -0.8043478f },
			{ -0.25776398f, -0.90993786f },

			{ 0.14906836f, -0.94720495f },
			{ -0.13664597f, -0.83540374f },
			{ 0.42546582f, -0.45031053f },
			{ 0.73291934f, -0.5559006f },

			{ 0.73291934f, -0.46894407f },
			{ 0.40310564f, -0.55279505f },
			{ 0.2795031f, 0.15838516f },
			{ 0.5880124f, 0.31366467f },

			{ 0.28571427f, 0.2795031f },
			{ 0.5865839f, 0.20186341f },
			{ 0.9565225f, 0.6888199f },
			{ 0.7354038f, 0.7633541f },
			
			{ 0.77329195f, 0.83229816f },
			{ 0.7608696f, 0.6055901f },
			{ 0.13975155f, 0.62732923f },
			{ 0.1925466f, 0.9347826f },
			
			{ 0.19565225f, 0.66770184f },
			{ 0.20496893f, 0.39751554f },
			{ 0.0869565f, 0.39751554f },
			{ -0.012422323f, 0.66770184f },

			{ 0.102484465f, 0.45341623f },
			{ -0.14596272f, 0.4720497f },
			{ -0.1583851f, 0.29192543f },
			{ 0.1428572f, 0.3664596f },

			{ -0.26397514f, 0.668323f },
			{ -0.105590045f, 0.5745342f },
			{ -0.099378884f, 0.38198757f },
			{ -0.23602486f, 0.40683234f },
			
			{ -0.1832298f, 0.6645963f },
			{ -0.18633538f, 0.5465839f },
			{ -0.42546582f, 0.5372671f },
			{ -0.42236024f, 0.69875777f },
			
			{ -0.40993786f, 0.66770184f },
			{ -0.4409938f, 0.67701864f },
			{ -0.41925466f, 0.88447204f },
			{ -0.38509315f, 0.88447204f },
			
			{ -0.4037267f, 0.85093176f },
			{ -0.4037267f, 0.92857146f },
			{ -0.23459626f, 0.96583855f },
			{ -0.21459626f, 0.8819876f },
			
			{ -0.25565219f, 0.757764f },
			{ -0.25186335f, 0.91925466f },
			{ -0.152484465f, 0.909938f },
			{ -0.2090683f, 0.76397514f }

			/* TWO */
//			{ 0.4223603f, -0.9006211f },
//			{ 0.55279505f, -0.8074534f },
//			{ 0.4633541f, -0.5093168f },
//			{ 0.34223602f, -0.5693789f },
//
//			{ 0.4633541f, -0.53416145f },
//			{ 0.3981367f, -0.43167698f },
//			{ 0.5559007f, -0.26397514f },
//			{ 0.5776398f, -0.40683228f },
//
//			{ 0.61180127f, -0.28881985f },
//			{ 0.5465839f, -0.29192543f },
//			{ 0.51552796f, -0.0031055808f },
//			{ 0.60248446f, -0.0031055808f },
//
//			{ 0.59006214f, -0.027950287f },
//			{ 0.81987584f, -0.021739125f },
//			{ 0.8167702f, 0.065217376f },
//			{ 0.57142866f, 0.11490691f },
//
//			{ 0.87888205f, 0.26086962f },
//			{ 0.79192555f, 0.29503107f },
//			{ 0.77329195f, 0.021739125f },
//			{ 0.8726709f, 0.015527964f },
//
//			{ 0.63664603f, 0.25776398f },
//			{ 0.82298136f, 0.24844718f },
//			{ 0.84161496f, 0.33229816f },
//			{ 0.652174f, 0.38819873f },
//
//			{ 0.69565225f, 0.5776398f },
//			{ 0.5652174f, 0.583851f },
//			{ 0.59006214f, 0.34472048f },
//			{ 0.68944097f, 0.33850932f },
//
//			{ 0.6304349f, 0.63975155f },
//			{ 0.6304349f, 0.5559007f },
//			{ 0.84161496f, 0.5590062f },
//			{ 0.8447205f, 0.6459627f },
//
//			{ 0.48003727f, 0.8647826f },
//			{ 0.79192555f, 0.6149069f },
//			{ 0.87888205f, 0.62732923f },
//			{ 0.6365839f, 0.9027329f },
//
//			{ 0.32298136f, 0.9347826f },
//			{ 0.326087f, 0.8478261f },
//			{ 0.54347825f, 0.84161496f },
//			{ 0.5279503f, 0.92857146f },
//
//			{ 0.3757764f, 0.88819885f },
//			{ 0.2888199f, 0.88819885f },
//			{ 0.3167702f, 0.08385098f },
//			{ 0.40062118f, 0.16149068f },
//			
//			{ 0.3850932f, 0.0869565f },
//			{ 0.35714293f, 0.19565225f },
//			{ 0.08074534f, 0.17080748f },
//			{ 0.0962733f, -0.03416145f },
//
//			{ -0.03416145f, -0.012422323f },
//			{ 0.105590105f, -0.012422323f },
//			{ 0.0869565f, -0.49378884f },
//			{ -0.0031055808f, -0.49378884f },
//
//			{ 0.049689412f, -0.47204965f },
//			{ 0.049689412f, -0.54037267f },
//			{ 0.24844718f, -0.5124223f },
//			{ 0.22360253f, -0.39751554f },
//
//			{ 0.2701863f, -0.48757762f },
//			{ 0.15527952f, -0.742236f },
//			{ 0.08385098f, -0.70807457f },
//			{ 0.1832298f, -0.48136646f },
//
//			{ -0.89440995f, -0.70186335f },
//			{ 0.11180127f, -0.68944097f },
//			{ 0.11180127f, -0.7484472f },
//			{ -0.88819873f, -0.76397514f },
//
//			{ -0.92236024f, -0.7204969f },
//			{ -0.84782606f, -0.7173913f },
//			{ -0.9068323f, 0.9534162f },
//			{ -0.98136646f, 0.9409938f },
//
//			{ -0.91925466f, 0.91925466f },
//			{ -0.5124223f, 0.9254658f },
//			{ -0.51552796f, 0.98447204f },
//			{ -0.9254658f, 0.9875777f },
//
//			{ -0.4658385f, 0.93788826f },
//			{ -0.54658383f, 0.94720495f },
//			{ -0.64906836f, 0.70807457f },
//			{ -0.57763976f, 0.70807457f },
//			
//			{ -0.61180127f, 0.652174f },
//			{ -0.61180127f, 0.7173914f },
//			{ -0.6863354f, 0.8633541f },
//			{ -0.757764f, 0.8167702f },
//
//			{ -0.80124223f, 0.8478261f },
//			{ -0.6925466f, 0.82919264f },
//			{ -0.69875777f, 0.42857146f },
//			{ -0.7950311f, 0.42857146f },
//
//			{ -0.742236f, 0.48447204f },
//			{ -0.742236f, 0.36024845f },
//			{ -0.4378882f, 0.40062118f },
//			{ -0.44409937f, 0.5f },
//
//			{ -0.4906832f, 0.43788826f },
//			{ -0.4037267f, 0.43788826f },
//			{ -0.43167698f, 0.21739137f },
//			{ -0.4751553f, 0.2204969f },
//
//			{ -0.45031053f, 0.25155282f },
//			{ -0.60559005f, 0.25155282f },
//			{ -0.60559005f, 0.1832298f },
//			{ -0.45652175f, 0.1832298f },
//
//			{ -0.66770184f, 0.13354039f },
//			{ -0.59006214f, 0.21739137f },
//			{ -0.5124223f, -0.065217376f },
//			{ -0.621118f, -0.04347825f },
//
//			{ -0.5652174f, -0.03416145f },
//			{ -0.568323f, -0.108695626f },
//			{ -0.24844718f, -0.08074534f },
//			{ -0.25465834f, -0.015527904f },
//
//			{ -0.28881985f, -0.052794993f },
//			{ -0.173913f, -0.04037267f },
//			{ -0.33540374f, 0.1428572f },
//			{ -0.40062112f, 0.12732923f },
//
//			{ -0.3757764f, 0.17391312f },
//			{ -0.36024845f, 0.108695626f },
//			{ -0.11180121f, 0.13664603f },
//			{ -0.12111801f, 0.18944097f },
//
//			{ -0.0869565f, 0.17080748f },
//			{ -0.14285713f, 0.16770184f },
//			{ -0.1490683f, 0.43788826f },
//			{ -0.102484465f, 0.4503106f },
//
//			{ -0.12111801f, 0.4037267f },
//			{ -0.12111801f, 0.5279503f },
//			{ 0.09006214f, 0.5217391f },
//			{ 0.09006214f, 0.43478262f },
//
//			{ 0.04347825f, 0.75465846f },
//			{ 0.13664603f, 0.75465846f },
//			{ 0.13664603f, 0.48136652f },
//			{ 0.055900693f, 0.48447204f },
//			
//			{ 0.07763982f, 0.72360253f },
//			{ 0.07763982f, 0.8540373f },
//			{ -0.36335403f, 0.8385093f },
//			{ -0.36335403f, 0.72670805f }
			
			/* THREE */
//			{ -0.86024845f, 0.8385093f },
//			{ -0.47826087f, 0.8136647f },
//			{ -0.38819873f, -0.24844718f },
//			{ -0.7701863f, -0.2795031f },
//
//			{ -0.5559006f, -0.57142854f },
//			{ -0.61180127f, -0.099378884f },
//			{ 0.24534166f, -0.18633538f },
//			{ 0.24534166f, -0.61180127f },
//
//			{ 0.0f, -0.40683228f },
//			{ 0.59937894f, -0.24844718f },
//			{ 0.36024845f, 0.7204969f },
//			{ -0.102484465f, 0.5590062f },
//
//			{ 0.68633544f, 0.20186341f },
//			{ 0.8074534f, 0.6242236f },
//			{ 0.12111807f, 0.8385093f },
//			{ 0.018633604f, 0.4130435f }
	};
	
	private static float[][] COLOURS = {
			{ 0.0f, 0.0f, 1.0f },
			{ 0.0f, 1.0f, 0.0f },
			{ 1.0f, 0.0f, 0.0f },
			{ 0.0f, 1.0f, 1.0f },
			{ 1.0f, 0.0f, 1.0f },
			{ 1.0f, 1.0f, 0.0f },
			{ 0.0f, 0.5f, 0.5f },
			{ 0.5f, 0.0f, 0.5f }
	};
	
	public void setup(final GLCanvas canvas) {
	}

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

		// TODO
		
	}

	// TODO: more methods or data
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();

		gl.glViewport(x, y, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}
