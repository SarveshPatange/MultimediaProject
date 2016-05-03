package Writer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

/**
 * Created by sailesh on 5/2/16.
 */
public class WAVWriter {

    private int BUFFER ;
    private int AUDIOSAMPLES_PER_VIDEOFRAME = 1600;
    AudioInputStream audStream;
    InputStream ipStream;
    String inFile;

    public WAVWriter(String Filename){
        this.inFile = Filename;

        try {
            ipStream = new FileInputStream(inFile);
            audStream  = AudioSystem.getAudioInputStream(new BufferedInputStream(ipStream));
            this.BUFFER = AUDIOSAMPLES_PER_VIDEOFRAME * audStream.getFormat().getFrameSize();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genAudio(boolean[] frames, File outFile){

        try {

            int framesGen=0;
            for(int i = 0; i < frames.length;i++){
                if(frames[i])
                    framesGen++;
            }

            byte[] byteArray = new byte[BUFFER*framesGen];
            int offset = 0;

            for(int i = 0; i < frames.length;i++){

                if(frames[i]){
                    audStream.read(byteArray,offset,BUFFER);
                    offset+=BUFFER;
                }
                else {
                    audStream.skip(BUFFER);
                }
            }

            InputStream is = new ByteArrayInputStream(byteArray);
            AudioInputStream outStream = new AudioInputStream(is, audStream.getFormat(), BUFFER * framesGen);
            AudioSystem.write(outStream, AudioFileFormat.Type.WAVE, outFile);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
