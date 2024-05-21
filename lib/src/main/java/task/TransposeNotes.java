package task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TransposeNotes {
    private static final int MIN_OCTAVE = -3;
    private static final int MAX_OCTAVE = 5;
    private static final int NOTES_COUNT_IN_OCTAVE = 12;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar transpose-notes.jar <inputFile> <semitone> <outputFile>");
            System.exit(1);
        }

        String inputFile = args[0];
        int semitone = Integer.parseInt(args[1]);
        String outputFile = args[2];

        try {
            List<int[]> notes = readNotesFromFile(inputFile);
            List<int[]> transposedNotes = transposeNotes(notes, semitone);
            writeNotesToFile(transposedNotes, outputFile);
        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static List<int[]> readNotesFromFile(String inputFile) throws IOException {
        Gson gson = new Gson();
        Type noteListType = new TypeToken<List<int[]>>() {}.getType();
        FileReader reader = new FileReader(inputFile);
        List<int[]> notes = gson.fromJson(reader, noteListType);
        reader.close();
        return notes;
    }

    private static List<int[]> transposeNotes(List<int[]> notes, int semitone) {
        for (int[] note : notes) {
            int presentOctave = note[0];
            int presentNoteNumber = note[1];
            int finalSemitones = presentOctave * NOTES_COUNT_IN_OCTAVE + presentNoteNumber + semitone;

            int newOctave = finalSemitones / NOTES_COUNT_IN_OCTAVE;
            int newNoteNumber = finalSemitones % NOTES_COUNT_IN_OCTAVE;

            if (newNoteNumber <= 0) {
                newOctave--;
                newNoteNumber += NOTES_COUNT_IN_OCTAVE;
            }

            if (newOctave < MIN_OCTAVE || newOctave > MAX_OCTAVE) {
                throw new IllegalArgumentException("Transposed note out of keyboard range");
            }

            note[0] = newOctave;
            note[1] = newNoteNumber;
        }
        return notes;
    }

    private static void writeNotesToFile(List<int[]> notes, String outputFile) throws IOException {
        Gson gson = new Gson();
        FileWriter writer = new FileWriter(outputFile);
        gson.toJson(notes, writer);
        writer.close();
    }
}
