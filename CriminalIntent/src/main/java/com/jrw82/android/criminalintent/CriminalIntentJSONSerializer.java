package com.jrw82.android.criminalintent;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by johnsonrw82 on 4/28/2015.
 */
public class CriminalIntentJSONSerializer {
    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context c, String filename) {
        this.mContext = c;
        this.mFilename = filename;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        // build array in JSON
        JSONArray array = new JSONArray();
        for (Crime c : crimes) {
            array.put(c.toJSON());
        }

        // write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }
        finally {
            if ( writer != null ) {
                writer.close();
            }
        }
    }

    public ArrayList<Crime> loadCrimes() throws JSONException, IOException {
        ArrayList<Crime> crimes = new ArrayList<>();
        BufferedReader reader = null;
        try {
            // open the file and read from it
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while ( (line = reader.readLine()) != null ) {
                // line breaks omitted and not relevant
                jsonString.append(line);
            }
            // parse JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++ ) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        }
        catch (FileNotFoundException ex ) {
            // ignore, it will happen when no file is found
        }
        finally {
            if ( reader != null ) {
                reader.close();
            }
        }

        return crimes;
    }
}
