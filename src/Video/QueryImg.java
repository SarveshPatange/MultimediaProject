package Video;

import Bucket.Bucket;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.opencv.core.Core.absdiff;
import static org.opencv.core.Core.sumElems;

/**
 * Created by garrydmello on 4/30/16.
 */
public class QueryImg {

    private static int CHANNELS = 3;
    static int WIDTH = 1280;
    static int HEIGHT = 720;
    double histVal;
    List<Mat> imgHist;

    private static BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(480, 270, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 480, 270, null);
        g.dispose();

        return resizedImage;
    }

    public double getDifference(List<Mat> histogramA, List<Mat> histogramB) {
        double value = 0.0;
        for (int i = 0; i < CHANNELS; i++) {
            Mat diff = new Mat();
            absdiff(histogramA.get(i), histogramB.get(i), diff);
            value += sumElems(diff).val[0];
        }
        return value / 3.0;
    }

    public void genHistImg(File ipFile) {

        try {

            InputStream is = new FileInputStream(ipFile);
            File output = new File("/Users/garrydmello/IdeaProjects/CS567Project/src/ImageProcessing/output.png");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Resize Image
            BufferedImage bi;
            BufferedImage resized;

            //bi = ImageIO.read(is);

            //FileWriter out
            byte[] bytes = new byte[(int)ipFile.length()];
            is.read(bytes, 0, Math.round(ipFile.length()));

            System.out.println("ipFile length"+ipFile.length());

            bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

            int ind = 0;
            for(int y = 0; y < HEIGHT; y++) {
                for(int x = 0; x < WIDTH; x++) {

                    byte r = bytes[ind];
                    byte g = bytes[ind+(WIDTH*HEIGHT)];
                    byte b = bytes[ind+(WIDTH*HEIGHT*2)];

                    int pix = (0xff000000) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    bi.setRGB(x,y,pix);
                    ind++;
                }
            }
             resized = resizeImage(bi,BufferedImage.TYPE_INT_RGB);

             ImageIO.write(resized,"rgb",baos);
             ImageIO.write(resized,"png",output);


             //bytes =((DataBufferByte)resized.getRaster().getDataBuffer()).getData();
             bytes = baos.toByteArray();


            Histogram hist = new Histogram();
            imgHist = hist.getHistogram(bytes, 480, 270);


            Mat chanR = imgHist.get(0);
            Mat chanG = imgHist.get(1);
            Mat chanB = imgHist.get(2);

            double totalR = 0.0;
            double totalG = 0.0;
            double totalB = 0.0;


            double[] tempR;
            double[] tempG;
            double[] tempB;


            for (int j = 0; j < chanR.rows(); j++) {

                tempR = chanR.get(j, 0);
                totalR += (tempR[0] * (j+1));

                tempG = chanG.get(j, 0);
                totalG += (tempG[0] * (j+1));

                tempB = chanB.get(j, 0);
                totalB += (tempB[0] * (j+1));


            }

            histVal = ((totalR + totalG + totalB) / (256 * 3));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchImg(String fileName,ImgIndex img){


          try {
              File ipFile = new File(fileName);
              genHistImg(ipFile);

              ArrayList<Bucket> clusters = img.getClusters();
              histVal = Math.round(histVal);

              ArrayList<List<Mat>> histList = img.getHistList();

              int bucket_id = (int)Math.floor((histVal - img.minHist)/(img.rangeCluster+1));
              System.out.println("histVal"+histVal);
              System.out.println("Bucket Number"+bucket_id);

              Bucket buck = clusters.get(bucket_id);

              double minDiffHist = Double.MAX_VALUE;
              int frameSearch = 0;
              ArrayList<Double[]> bucketList = buck.getBucketList();

              for(int i = 0; i < bucketList.size();i++){
                  Double[] drop = bucketList.get(i);
                  int frame = (int)Math.round(drop[0]);

                  List<Mat> frameHist = histList.get(frame);

                  double histDiff = getDifference(frameHist,imgHist);

                  if(histDiff < minDiffHist){
                      frameSearch = frame;
                      minDiffHist = histDiff;

                  }


              }

              System.out.println("Frame No : "+frameSearch);


          }catch(Exception e){

              e.printStackTrace();

        }


    }


    public static void main(String[] args){


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ImgIndex img = new ImgIndex(args[0]);

        img.genHistList();
        img.genClusters();
        //img.genFiles();


        QueryImg query = new QueryImg();
        query.searchImg(args[0],img);


    }
}