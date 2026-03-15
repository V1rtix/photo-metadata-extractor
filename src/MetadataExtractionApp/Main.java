package MetadataExtractionApp;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File photoFolder = new File("C:\\Users\\vikto\\Desktop\\metadata-convert");

        MetadataExtraction.photosToProcess(photoFolder);
    }
}