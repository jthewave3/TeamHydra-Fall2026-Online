package model; // all the game data classes live in this package

import java.io.File; // the data file
import java.io.FileNotFoundException; // thrown when the file is missing
import java.io.InputStream; // the data file inside the model package
import java.util.ArrayList; // list of rows
import java.util.Scanner; // reads the file line by line

public class DataFile { // reads the pipe separated data files that sit next to the model classes
    private static String FOLDER = "src" + File.separator + "model"; // fallback if the files weren't copied next to the classes

    public static ArrayList<String[]> read(String fileName) { // returns one array of fields per data line
        ArrayList<String[]> rows = new ArrayList<String[]>(); // all the rows
        try {
            InputStream stream = DataFile.class.getResourceAsStream(fileName); // look in the model package first
            Scanner in; // reads the lines
            if (stream != null) { // found it
                in = new Scanner(stream, "UTF-8"); // read from the package
            } else {
                in = new Scanner(new File(FOLDER, fileName), "UTF-8"); // read from the source folder
            }
            while (in.hasNextLine()) { // go through every line
                String line = in.nextLine().trim(); // remove extra spaces
                if (line.isEmpty() || line.startsWith("#")) { // skip blank lines and comments
                    continue; // next line
                }
                String[] fields = line.split("\\|", -1); // fields are separated by |
                for (int i = 0; i < fields.length; i++) { // clean up every field
                    fields[i] = fields[i].trim().replace("\\n", "\n"); // \n in the file becomes a new line
                }
                rows.add(fields); // save the row
            }
            in.close(); // done with the file
        } catch (FileNotFoundException e) { // missing file
            throw new IllegalStateException("Could not find data file: " + FOLDER + File.separator + fileName); // the game can't run without it
        }
        return rows; // send the rows back
    }
}
