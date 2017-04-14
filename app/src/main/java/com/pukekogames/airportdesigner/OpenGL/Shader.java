package com.pukekogames.airportdesigner.OpenGL;

import android.opengl.GLES20;

/**
 * Created by Marko Rapka on 14.04.2017.
 */
public class Shader {

    private String code;
    int id;
    private int type;

    public Shader(String code, int type) {
        this.code = code;
        this.type = type;
        compile();
    }

    private void compile() {
        id = GLES20.glCreateShader(type);
        GLES20.glShaderSource(id, code);
        GLES20.glCompileShader(id);
    }

    public void delete() {
        GLES20.glDeleteShader(id);
    }

    public String getInfoLog(){
        return GLES20.glGetShaderInfoLog(id);
    }
}
