package Summary;

import Writer.*;

import Video.*;
import AudioAnalyser.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sarvesh on 4/30/16.
 */
public class Condenser {

    private String IMAGE_FILE;
    private String AUDIO_FILE;

    private String OUTPUT_FILE;

    private static final int  TOTAL_FRAMES = 4500;

    public Condenser(String imageFile, String audioFile)
    {
        this.IMAGE_FILE = imageFile;
        this.AUDIO_FILE = audioFile;
    }

    public void condense(String outFile)
    {
        this.OUTPUT_FILE = outFile;

        boolean[] framesRequired = new boolean[TOTAL_FRAMES];

        ImageProcessing condensedImage = new ImageProcessing(IMAGE_FILE, TOTAL_FRAMES, 480, 270);
        ArrayList<Integer> keyImageFrames = condensedImage.generateKeyFrames();

        for (int frame: keyImageFrames)
        {
            framesRequired[frame] = true;
        }

        WAVSummarize condensedAudio = new WAVSummarize(AUDIO_FILE);
        ArrayList<Integer> keyAudioFrames = null;
        try {
            keyAudioFrames = condensedAudio.processAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < keyAudioFrames.size(); i++)
        {
            int frameTime = keyAudioFrames.get(i);
            framesRequired[(int)Math.floor(frameTime * 7.5)] = true;
        }

        int frameIndex = 16;

        //Always consider the first 15 frames i.e the first second
        for (int i = 0; i < frameIndex; i++)
        {
            framesRequired[i] = true;
        }

        //If Frame has been identified in either Image or AudioAnalyser processing keep the frames around it.
        while( frameIndex < framesRequired.length)
        {
            if(!framesRequired[frameIndex])
            {
                //If frame is not a key frame check next frame
                frameIndex += 1;
            }
            else
            {
                for(int j = frameIndex; j <= frameIndex + 15 && j <framesRequired.length; j++)
                {
                    framesRequired[j] = true;
                }
                frameIndex += 16;
            }

            FrameWriter writeImage = new FrameWriter(IMAGE_FILE);
            writeImage.writeFrames(framesRequired, new File("condensed.rgb"));

            WAVWriter writeAudio = new WAVWriter(AUDIO_FILE);
            writeAudio.genAudio(framesRequired, new File("condensed.wav"));
        }
    }

}
