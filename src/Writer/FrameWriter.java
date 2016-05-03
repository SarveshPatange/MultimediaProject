package MediaWriter;

import java.io.*;

/**
 * Created by sarvesh on 4/30/16.
 */
public class FrameWriter {

    private String inputFile;
    private int width;
    private int height;

    public FrameWriter(String fileName)
    {
        this.inputFile = fileName;
    }

    public void writeFrames(boolean[] frames, File outputFile)
    {
        this.width = 480;
        this.height = 270;

        File file = new File(inputFile);
        int totalFrames = (int)file.length() / (480 * 270 * 3);

        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(outputFile);

            for (int i = 0; i < frames.length; i++)
            {

                byte[] byte_array = new byte[width * height * 3];
                int offset = 0;
                int numberRead;
                while(offset < byte_array.length && (numberRead = in.read(byte_array, offset, byte_array.length - offset)) >= 0)
                {
                    offset += numberRead;
                }
                if(frames[i])
                {
                    out.write(byte_array);
                }
            }
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
