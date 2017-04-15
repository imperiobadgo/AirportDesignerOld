package com.pukekogames.airportdesigner.OpenGL;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Marko Rapka on 14.04.2017.
 */
public class ShaderProgram {

    private int shaderProgram;

    public ShaderProgram() {
        this.shaderProgram = GLES20.glCreateProgram();
    }

    public void attachShader(Shader shader) {
        GLES20.glAttachShader(this.shaderProgram, shader.id);
    }

    public void link() {
        GLES20.glLinkProgram(this.shaderProgram);
    }

    public void enable() {
        GLES20.glUseProgram(this.shaderProgram);
    }

    public void disable() {
        GLES20.glUseProgram(0);
    }

    public String getInfoLog(){
        return GLES20.glGetProgramInfoLog(shaderProgram);
    }

    public void bindAttribLocation(int index, String name){
        GLES20.glBindAttribLocation(shaderProgram, index, name);
    }

    public int getUniformLocation(String variable) {
        return GLES20.glGetUniformLocation(this.shaderProgram, variable);
    }

    public int getVertexAttribute(String variable) {
        return GLES20.glGetAttribLocation(this.shaderProgram, variable);
    }

    public static ShaderProgram createBasicShader(String vertexShaderResource, String fragmentShaderResource) {
        ShaderProgram shaderProgram = new ShaderProgram();
        Shader vertexShader = new Shader(vertexShaderResource, GLES20.GL_VERTEX_SHADER);
        Shader fragmentShader = new Shader(fragmentShaderResource, GLES20.GL_FRAGMENT_SHADER);
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.link();
        vertexShader.delete();
        fragmentShader.delete();
        return shaderProgram;
    }

    public static String readTextFileFromRawResource(final Context context,
                                                     final int resourceId)
    {
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }
}
