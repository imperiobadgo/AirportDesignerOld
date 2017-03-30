package com.pukekogames.airportdesigner.Helper;

import java.io.*;

/**
 * Created by Marko Rapka on 16.11.2015.
 */
public class Serializing {
    private static Serializing ourInstance = new Serializing();

    public static Serializing getInstance() {
        return ourInstance;
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

}
