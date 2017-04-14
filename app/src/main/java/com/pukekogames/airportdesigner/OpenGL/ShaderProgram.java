package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;
import android.opengl.GLES30;

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
}
