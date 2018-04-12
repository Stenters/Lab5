/*
 * CS2852 – 031
 * Spring 2017
 * Lab 5 - Guitar Synth
 * Name: Stuart Enters
 * Created: 4/5/2018
 */

package enterss;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;

import javax.sound.sampled.LineUnavailableException;

import edu.msoe.taylor.audio.Note;
import edu.msoe.taylor.audio.SimpleAudio;

/**
 * The Guitar class generates guitar sounds based on user input.
 * 
 * In order to use this class correctly, one must create a Guitar
 * object, add all of the desired notes to the object, and then
 * call the play() method.  The play() method will generate a
 * list of samples for all of the notes to be played (by calling
 * an internal method (jaffeSmith())) and then send them to the
 * audio output stream.
 * @author t a y l o r@msoe.edu
 * @version 2018.03.26_2.3
 */
public class Guitar {
    /** 
     * Default sample rate in Hz 
     */
    private static final int DEFAULT_SAMPLE_RATE = 8000;
    
    /** 
     * Maximum sample rate in Hz
     */
    private static final int MAX_SAMPLE_RATE = 48000;
    
    /** 
     * Default decay rate
     */
    private static final float DEFAULT_DECAY_RATE = 0.99f;
    private static final int UINT_MILLI_CONVERSION_FACTOR = 1000;

    /**
     * Queue of notes 
     */
    private Queue<Note> notes = new LinkedList<>();
    
    /**
     *  Sample rate in samples per second 
     */
    private int sampleRate;
    
    /** 
     * Decay rate 
     */
    private float decayRate;


    /**
     * Constructs a new Guitar object with the default sample rate
     * and decay rate.
     */
    public Guitar() {
        this.decayRate = DEFAULT_DECAY_RATE;
        this.sampleRate = DEFAULT_SAMPLE_RATE;
    }
    
    /**
     * Constructs a new Guitar object with the specified parameters.
     * If an invalid sampleRate or decayRate is specified, the
     * default value will be used and an error message is sent to
     * System.err.
     * @param sampleRate sample rate (between 8000 Hz and 48000 Hz)
     * @param decayRate decay rate (between 0.0f and 1.0f)
     */
    public Guitar(int sampleRate, float decayRate) {
        if (sampleRate < DEFAULT_SAMPLE_RATE || sampleRate > MAX_SAMPLE_RATE){
            throw new IllegalArgumentException("Illegal value for Sample Rate");
        } else if (decayRate <= 0 || decayRate > 1){
            throw new IllegalArgumentException("Illegal value for Decay Rate");
        }

        this.decayRate = decayRate;
        this.sampleRate = sampleRate;
    }
        
    /**
     * Adds the specified note to this Guitar.
     * @param note Note to be added.
     */
    public void addNote(Note note) {
        notes.add(note);
    }
    
    /**
     * Generates the audio samples for the notes listed in the
     * current Guitar object by calling the jaffeSmith algorithm and
     * sends the samples to the speakers.
     * @throws LineUnavailableException If audio line is unavailable.
     * @throws IOException If any other input/output problem is encountered.
     */
    public void play() throws LineUnavailableException, IOException {
        SimpleAudio audio = new SimpleAudio(sampleRate);
        List<Float> samples = jaffeSmith();
        audio.play(samples);
        System.out.println("Done");
    }

    /**
     * Uses the Jaffe-Smith algorithm to generate the audio samples.
     * <br />Implementation note:<br />
     * Use Jaffe-Smith algorithm described on the assignment page
     * to generate a sequence of samples for each note in the list
     * of notes.
     * 
     * @return List of samples comprising the pluck sound(s).
     */
    private List<Float> jaffeSmith() {
        List<Float> samples = new LinkedList<>();

        for (Note n : notes) {
            float samplesPerPeriod = sampleRate / n.getFrequency();
            float numberOfSamples = sampleRate * (n.getDuration() / UINT_MILLI_CONVERSION_FACTOR);
            float previousSample = 0;
            Queue<Float> periodSamples = new LinkedList<>();

            for (int i = 0; i < samplesPerPeriod; i++){
                periodSamples.offer((float)(Math.random() * 2) - 1);
            }

            for (int i = 0; i < numberOfSamples; i++){
                float val = periodSamples.remove();
                float calcVal = decayRate * ((previousSample + val) / 2);
                periodSamples.offer(calcVal);
                samples.add(calcVal);
                previousSample = val;
            }
        }
        return samples;
    }

    /**
     * Returns an array containing all the notes in this Guitar.
     * OPTIONAL
     * @return An array of Notes in the Guitar object.
     */
    public Note[] getNotes() {
        return notes.toArray(new Note[notes.size()]);
    }
    
    /**
     * Creates a new file and writes to that file.
     * OPTIONAL
     * @param file File to write to.
     * @throws IOException If any other input/output problem is encountered.
     */
    public void write(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)){
            for (Note n: notes){
                writer.write(n.toString() + "\n");
            }

        }
    }
}
