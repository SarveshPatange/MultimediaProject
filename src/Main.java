import org.opencv.core.Core;

public class Main {
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Player AVPlayer = new Player(args[2]);
	    AVPlayer.loadAudio(args[1]);
	    AVPlayer.loadVideo(args[0]);
	    AVPlayer.initialize();





        /*
        PlayerComponents AVPlayer = new PlayerComponents("/home/sailesh/Documents/summarized.rgb",  "/home/sailesh/Documents/summarized.wav");
		AVPlayer.loadAudio();
		AVPlayer.loadVideo();
		AVPlayer.initialize();*/

		//VideoSearch search = new VideoSearch(args[0],args[2]);
    }

}
