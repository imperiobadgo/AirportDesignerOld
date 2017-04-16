package com.pukekogames.airportdesigner.OpenGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.pukekogames.airportdesigner.Objects.Images;
import com.pukekogames.airportdesigner.R;

/**
 * Created by Marko Rapka on 14.04.2017.
 */
public class TextureLoader {

    private static TextureLoader ourInstance = new TextureLoader();
    private TextureLoader(){}

    public static TextureLoader Instance() {
        return ourInstance;
    }

    private Texture[] textures = new Texture[100];

    public void loadTextures(Context c){

        textures[Images.indexAirplaneSmall] = loadTexture(c, R.drawable.airplane_small);
        textures[Images.indexAirplaneCessna] = loadTexture(c, R.drawable.airplane_cessna);
        textures[Images.indexAirplaneA320] = loadTexture(c, R.drawable.airplane_a320);
        textures[Images.indexAirplane777] = loadTexture(c, R.drawable.airplane_777);


        textures[Images.indexParkGate] = loadTexture(c, R.drawable.parkgate);
        textures[Images.indexStreet] = loadTexture(c, R.drawable.street);
        textures[Images.indexRunwayEnd] = loadTexture(c, R.drawable.runway_end);
        textures[Images.indexRunwayMiddle] = loadTexture(c, R.drawable.runway_middle);
        textures[Images.indexTaxiway] = loadTexture(c, R.drawable.taxiway);
    }

    public Texture getTexture(int id){
        if (id < 0 || id > textures.length) return null;
        return textures[id];
    }

    private static Texture loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];
        Texture texture = null;

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            texture = new Texture(textureHandle[0], bitmap.getWidth(), bitmap.getHeight());

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();

        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return texture;
    }
}
