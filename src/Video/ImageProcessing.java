package Video;

import Config.Constants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;


public class ImageProcessing {
    private InputStream videoInputStream;
    private int totalFrames;
    private int width;
    private int height;
    private int frameLength;
    private ArrayList<Integer> keyFrames;

    private double THRESHOLD = Constants.IMAGE_SUMMARY_THRESHOLD_PERCENT * Constants.MAX_HISTOGRAM_DIFFERENCE;
    private Histogram hist;
    public static int keyFrameSize;

    public ImageProcessing(String fileName, int totalFrames, int width, int height) {
        try {
            videoInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.totalFrames = totalFrames;
        this.width = width;
        this.height = height;
        this.frameLength = width * height * 3;
        this.keyFrames = new ArrayList<>();
        this.hist = new Histogram();
    }

    public ArrayList<Integer> generateKeyFrames() {


        List<Mat> referenceHist = null;
        List<Mat> currentHist;
        for (int i = 0; i < totalFrames; i++) {

            byte[] bytes = new byte[frameLength];
            int offset = 0;
            int numRead;
            try {
                while (offset < bytes.length && (numRead = videoInputStream.read(bytes, offset, bytes.length - offset)) >= 0) {
                    offset += numRead;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (referenceHist == null) {
                referenceHist = hist.getHistogram(bytes, width, height);
                keyFrames.add(i);
                continue;
            }
            else {
                currentHist = hist.getHistogram(bytes, width, height);
            }

            if (hist.getDifference(currentHist, referenceHist) >= THRESHOLD) {
                System.out.println("Threshold crossed.");
                referenceHist = currentHist;
                keyFrames.add(i);
            }
        }

        keyFrameSize = keyFrames.size();
        return keyFrames;
    }
}
