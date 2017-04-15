package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.pukekogames.airportdesigner.Activities.Game;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.R;
import org.w3c.dom.Text;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Marko Rapka on 14.04.2017.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private Game game;
    private float red;

    private static float[] quad = {
            -0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 1.0f,

            -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 1.0f

    };
    private static float[] texCoords = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    private FloatBuffer quadBuffer;
    private FloatBuffer texCoordsBuffer;
    private ShaderProgram basicShader;



    public OpenGLRenderer(Game game){
        this.game = game;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        PrimitiveHelper.init();
        TextureLoader.Instance().loadTextures(game);


        ByteBuffer quadByteBuffer = ByteBuffer.allocateDirect(quad.length * 4);
        quadByteBuffer.order(ByteOrder.nativeOrder());
        quadBuffer = quadByteBuffer.asFloatBuffer();
        quadBuffer.put(quad);
        quadBuffer.rewind();

        ByteBuffer texCoordsByteBuffer = ByteBuffer.allocateDirect(texCoords.length * 4);
        texCoordsByteBuffer.order(ByteOrder.nativeOrder());
        texCoordsBuffer = texCoordsByteBuffer.asFloatBuffer();
        texCoordsBuffer.put(texCoords);
        texCoordsBuffer.rewind();


        String vertexShaderSource = "" +
                "attribute vec4 a_position;" +
                "attribute vec2 a_texCoordinate;" +
                "" +
                "varying vec2 v_TexCoordinate;" +
                "" +
                "void main()" +
                "{" +
                "   gl_Position = a_position;" +
                "   v_TexCoordinate = a_texCoordinate;" +
                "}" +
                "";

        String fragmentShaderSource = "" +
                "uniform sampler2D u_Texture;" +
                "" +
                "varying vec2 v_TexCoordinate;" +
                "" +
                "void main()" +
                "{" +
                "   gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
                "}" +
                "";

//        String vert = ShaderProgram.readTextFileFromRawResource(game, R.)

        basicShader = ShaderProgram.createBasicShader(vertexShaderSource, fragmentShaderSource);
        basicShader.bindAttribLocation(0, "a_position");
        basicShader.bindAttribLocation(1, "a_texCoordinate");


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        red += 0.01f;
        if (red > 1) {
            red = 0f;
        }
        GLES20.glClearColor(red, 0.0f, 0.0f, 1.0f);

        basicShader.enable();

        int mTextureUniformHandle = basicShader.getUniformLocation("u_Texture");

        int mTextureCoordinateHandle = basicShader.getVertexAttribute("a_TexCoordinate");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        int texture = TextureLoader.Instance().getTexture(Images.indexAirplaneSmall);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);


        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 4 * 4, quadBuffer);
        GLES20.glVertexAttribPointer(1, 4, GLES20.GL_FLOAT, false, 2 * 4, texCoordsBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glEnableVertexAttribArray(1);

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
        basicShader.disable();
    }
}
