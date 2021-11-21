package ua.nure.mykhnevych.task3.Utils;

import java.io.*;
import java.util.Base64;

public final class SerializationHelper {

    public static Object deserializeObject(String string) throws Exception {
        byte [] data = Base64.getDecoder().decode(string);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    public static String serializeObject(Object obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(bos);
            os.writeObject(obj);
            os.close();
            String serialized = Base64.getEncoder().encodeToString(bos.toByteArray());
            return serialized;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
