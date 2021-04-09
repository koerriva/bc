package rtx.demo01;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import rtx.linalg.Vector3f;
import rtx.util.Camera;
import rtx.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL11C.GL_TRUE;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL42C.glBindImageTexture;
import static org.lwjgl.opengl.GL43C.*;

public class Demo01 {

	private long window;
	private int width = 1024;
	private int height = 768;

	private int tex;
	private int vao;
	private int computeProgram;
	private int quadProgram;

	private int eyeUniform;
	private int ray00Uniform;
	private int ray10Uniform;
	private int ray01Uniform;
	private int ray11Uniform;

	private int workGroupSizeX;
	private int workGroupSizeY;

	private Camera camera;

	private final Vector3f eyeRay = new Vector3f();

	GLFWErrorCallback errFun;
	GLFWKeyCallback keyFun;

	private void init() throws IOException {
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

		window = glfwCreateWindow(width, height, "Demo01", MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL) {
			throw new AssertionError("Failed to create the GLFW window");
		}

		glfwSetKeyCallback(window, keyFun = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window,true);
			}
		});

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		assert vidmode != null;
		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		GL.createCapabilities();

		/* Create all needed GL resources */
		tex = createFramebufferTexture();
		vao = quadFullScreenVao();
		computeProgram = createComputeProgram();
		initComputeProgram();
		quadProgram = createQuadProgram();
		initQuadProgram();

		/* Setup camera */
		camera = new Camera();
		camera.setFrustumPerspective(60.0f, (float) width / height, 1f, 2f);
		camera.setLookAt(new Vector3f(3.0f, 2.0f, 7.0f), new Vector3f(0.0f, 0.5f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
	}

	/**
	 * Creates a VAO with a full-screen quad VBO.
	 */
	private int quadFullScreenVao() {
		int vao = glGenVertexArrays();
		int vbo = glGenBuffers();
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		ByteBuffer bb = BufferUtils.createByteBuffer(2 * 6);
		bb.put((byte) -1).put((byte) -1);
		bb.put((byte) 1).put((byte) -1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) -1).put((byte) 1);
		bb.put((byte) -1).put((byte) -1);
		bb.flip();
		glBufferData(GL_ARRAY_BUFFER, bb, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_BYTE, false, 0, 0L);
		glBindVertexArray(0);
		return vao;
	}

	/**
	 * Create a shader object from the given classpath resource.
	 * 
	 * @param resource
	 *            the class path
	 * @param type
	 *            the shader type
	 * @return the shader object id
	 * @throws IOException
	 */
	private int createShader(String resource, int type) throws IOException {
		int shader = glCreateShader(type);
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
		glShaderSource(shader, Util.getCode(is));
		is.close();
		glCompileShader(shader);
		int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
		String shaderLog = glGetShaderInfoLog(shader);
		if (shaderLog.trim().length() > 0) {
			System.err.println(shaderLog);
		}
		if (compiled == 0) {
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}

	/**
	 * Create the full-scren quad shader.
	 * 
	 * @return that program id
	 * @throws IOException
	 */
	private int createQuadProgram() throws IOException {
		int quadProgram = glCreateProgram();
		int vshader = createShader("43/quad.vert", GL_VERTEX_SHADER);
		int fshader = createShader("43/quad.frag", GL_FRAGMENT_SHADER);
		glAttachShader(quadProgram, vshader);
		glAttachShader(quadProgram, fshader);
		glBindAttribLocation(quadProgram, 0, "vertex");
		glBindFragDataLocation(quadProgram, 0, "color");
		glLinkProgram(quadProgram);
		int linked = glGetProgrami(quadProgram, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(quadProgram);
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		return quadProgram;
	}

	/**
	 * Create the tracing compute shader program.
	 * 
	 * @return that program id
	 * @throws IOException
	 */
	private int createComputeProgram() throws IOException {
		int program = glCreateProgram();
		int cshader = createShader("43/demo01.comp", GL_COMPUTE_SHADER);
		glAttachShader(program, cshader);
		glLinkProgram(program);
		int linked = glGetProgrami(program, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(program);
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		return program;
	}

	/**
	 * Initialize the full-screen-quad program.
	 */
	private void initQuadProgram() {
		glUseProgram(quadProgram);
		int texUniform = glGetUniformLocation(quadProgram, "tex");
		glUniform1i(texUniform, 0);
		glUseProgram(0);
	}

	/**
	 * Initialize the compute shader.
	 */
	private void initComputeProgram() {
		glUseProgram(computeProgram);
		IntBuffer workGroupSize = BufferUtils.createIntBuffer(3);
		glGetProgramiv(computeProgram, GL_COMPUTE_WORK_GROUP_SIZE, workGroupSize);
		workGroupSizeX = workGroupSize.get(0);
		workGroupSizeY = workGroupSize.get(1);
		eyeUniform = glGetUniformLocation(computeProgram, "eye");
		ray00Uniform = glGetUniformLocation(computeProgram, "ray00");
		ray10Uniform = glGetUniformLocation(computeProgram, "ray10");
		ray01Uniform = glGetUniformLocation(computeProgram, "ray01");
		ray11Uniform = glGetUniformLocation(computeProgram, "ray11");
		glUseProgram(0);
	}

	/**
	 * Create the texture that will serve as our framebuffer.
	 * 
	 * @return the texture id
	 */
	private int createFramebufferTexture() {
		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, tex);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		ByteBuffer black = null;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, black);
		glBindTexture(GL_TEXTURE_2D, 0);
		return tex;
	}

	/**
	 * Compute one frame by tracing the scene using our compute shader and
	 * presenting that image on the screen.
	 */
	private void trace() {
		glUseProgram(computeProgram);

		/* Set viewing frustum corner rays in shader */
		glUniform3f(eyeUniform, camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
		camera.getEyeRay(-1, -1, eyeRay);
		glUniform3f(ray00Uniform, eyeRay.x, eyeRay.y, eyeRay.z);
		camera.getEyeRay(-1, 1, eyeRay);
		glUniform3f(ray01Uniform, eyeRay.x, eyeRay.y, eyeRay.z);
		camera.getEyeRay(1, -1, eyeRay);
		glUniform3f(ray10Uniform, eyeRay.x, eyeRay.y, eyeRay.z);
		camera.getEyeRay(1, 1, eyeRay);
		glUniform3f(ray11Uniform, eyeRay.x, eyeRay.y, eyeRay.z);

		/* Bind level 0 of framebuffer texture as writable image in the shader. */
		glBindImageTexture(0, tex, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);

		/* Compute appropriate invocation dimension. */
		int worksizeX = Util.nextPowerOfTwo(width);
		int worksizeY = Util.nextPowerOfTwo(height);

		/* Invoke the compute shader. */
		glDispatchCompute(worksizeX / workGroupSizeX, worksizeY / workGroupSizeY, 1);

		/* Reset image binding. */
		glBindImageTexture(0, 0, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
		glUseProgram(0);

		/*
		 * Draw the rendered image on the screen using textured full-screen
		 * quad.
		 */
		glUseProgram(quadProgram);
		glBindVertexArray(vao);
		glBindTexture(GL_TEXTURE_2D, tex);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindVertexArray(0);
		glUseProgram(0);
	}

	private void loop() {
		while (!glfwWindowShouldClose(window)) {
			glfwPollEvents();
			glViewport(0, 0, width, height);

			trace();

			glfwSwapBuffers(window);
		}
	}

	private void run() {
		try {
			init();
			loop();
			glfwDestroyWindow(window);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			glfwTerminate();
		}
	}

	public static void main(String[] args) {
		new Demo01().run();
	}

}
