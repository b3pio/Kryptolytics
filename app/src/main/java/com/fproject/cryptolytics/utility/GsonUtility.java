package com.fproject.cryptolytics.utility;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Contains helper functions for writing/reading {@link JSONObject} to/from disk.
 *
 * @author lSzathmary
 */
public class GsonUtility {
    private final static  String TAG = "[GsonUtility]";

    /**
     * Saves a {@link JSONObject} to file.
     */
    public static void toFile(Context context, JSONObject jsonObject, String fileName){
        File file = null;

        try {
            file = new File(context.getFilesDir(), fileName);
            file.createNewFile();

            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonObject.toString());
            writer.close();
        }
        catch (IOException exception) {
            Log.d(TAG, " toFile(): "  + exception.toString());
        }

    }

    /**
     * Loads a {@link JSONObject} from a file/
     */
    public static JSONObject fromFile(Context context, String fileName) {
        String jsonString = null;

        File file = new File(context.getFilesDir(), fileName);
        jsonString = stringFromFile(file);

        return stringToJsonObject(jsonString);
    }

    /**
     * Reads a {@link String} from a text file.
     */
    private static String stringFromFile(File file) {
        StringBuilder stringBuilder =  new StringBuilder();

        try {

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            reader.close();

        } catch (IOException exception) {
            Log.d(TAG, "stringFromFile(): "  + exception.toString());
        }

        return  stringBuilder.toString();
    }

    /**
     * Creates a {@link JSONObject} based on the specified {@link String}.
     */
    private static JSONObject stringToJsonObject(String jsonString) {
        JSONObject jsonObject = null;

        try {

            jsonObject = new JSONObject(jsonString);

        } catch (JSONException exception) {
            Log.d(TAG, "stringToJsonObject(): "  + exception.toString());
        }

        return  jsonObject;
    }
}
