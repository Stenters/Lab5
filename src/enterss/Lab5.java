/*
 * CS2852 – 031
 * Spring 2017
 * Lab 5 - Guitar Synth
 * Name: Stuart Enters
 * Created: 4/5/2018
 */

package enterss;

import edu.msoe.taylor.audio.Note;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Driver program for Data Structures lab assignment
 * @author t a y l o r@msoe.edu
 * @version 2018.03.26
 */
public class Lab5 extends Application {

    /**
     * The start of the application
     * @param primaryStage the stage to display to
     */
    @Override
    public void start(Stage primaryStage) {
        ArrayList<Note> notes = new ArrayList<>();
        Scanner fileIn = null;
        try (Scanner input = new Scanner(System.in)){
            int sampleRate = -1;
            float decayRate = -1;
            System.out.println("Please input a sampling rate (8000 - 48000, default 8000): ");
            if (input.hasNextInt()){
                sampleRate = input.nextInt();
                System.out.println(sampleRate);
            }
            System.out.println("Please input a decay rate (0 - 1, default .99): ");
            if (input.hasNextFloat()){
                decayRate = input.nextFloat();
                System.out.println(decayRate);
            }
            Guitar guitar;
            if (sampleRate != -1 && decayRate != -1){
                guitar = new Guitar(sampleRate, decayRate);
            } else {
                guitar = new Guitar();
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose a file to load");
            chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File file = chooser.showOpenDialog(new Stage());

            fileIn = new Scanner(file);
            while (fileIn.hasNextLine()){
                String line = fileIn.nextLine();
                if (!line.equals(" ")){
                    if (line.split(" ").length < 2){
                        System.err.println("Illegal line found: " + line);
                    } else {
                        notes.add(parseNote(line.split(" ")));
                    }
                }
            }

            for (Note n : notes) {
                guitar.addNote(n);
            }

            guitar.play();
        } catch (IOException | LineUnavailableException e) {
            System.err.println("Error with choosing files: " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.err.println("Error with Parameters: " + e.getMessage());
        } finally {
            if(fileIn != null) {
                fileIn.close();
            }
        }
    }

    /**
     * Program that reads in notes from a text file and plays a song
     * using the Guitar class to generate the sounds which are then
     * played by a SimpleAudio object.
     * @param args Ignored
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Returns a new Note initialized to the value represented by the specified String
     * @param line Description of a note with scientific pitch followed by duration in milliseconds.
     * @return Note represented by the String passed in.  Returns null if it is unable to parse
     * the note data correctly.
     */
    private static Note parseNote(String[] line) {
        return new Note(line[0],
                Float.parseFloat(line[1]));
    }
}
