import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import javax.imageio.ImageIO;

import static com.drew.metadata.exif.ExifSubIFDDirectory.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        File photoFolder = new File("C:\\Users\\vikto\\OneDrive\\Obrázky\\SONY CAMERA\\Červenec\\Rumunsko\\bukurest");

        try {
            for (File file : Objects.requireNonNull(photoFolder.listFiles())) {
                BufferedImage image = ImageIO.read(file);
                if (image == null){
                    System.out.println("Skipping " + file.getName());
                    continue;
                }
                processPhoto(file);
            }
        } catch (ImageProcessingException | IOException e) {
            System.out.println("No photos to process, insert photos to process");
        }
    }

    private static void processPhoto(File photo) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(photo);
        String focalLength = null;String fNumber = null;String iso = null;String exposureTime = null;

        for (Directory dir : metadata.getDirectories()) {
            if (focalLength == null) focalLength = dir.getDescription(TAG_FOCAL_LENGTH);
            if (fNumber == null) fNumber = dir.getDescription(TAG_FNUMBER);
            if (iso == null) iso = dir.getDescription(TAG_ISO_EQUIVALENT);
            if (exposureTime == null) exposureTime = dir.getDescription(TAG_EXPOSURE_TIME);
        }
        exposureTime = transferExposureTime(exposureTime);

        System.out.println(photo.getName() + " - " + focalLength + " | " + fNumber + " | ISO-" + iso + " | " + exposureTime);
    }
    // Method that transfers Exposure time from decimal to fractional version (0,02 sec -> 1/50 sec)
    private static String transferExposureTime(String exposureTime) {
        if (exposureTime == null) return null;
        if (exposureTime.contains("/")) return exposureTime;
        double time = Double.parseDouble(exposureTime.replace(" sec", ""));
        if (time >= 1){
            return time + " sec";
        }
        int result = 1;

        while (time < 1) {
            time = time * 10;
            result = result * 10;
        }
        if (time == 1){
            return "1/" + result + " sec";
        }
        if (time % 2 == 0) {
            while (time != 1) {
                time = time / 2;
                result = result / 2;
            }
            return "1/" + result + " sec";
        }
        if (time % 3 == 0) {
            while (time != 1) {
                time = time / 3;
                result = result / 3;
            }
            return "1/" + result + " sec";
        }
        if (time % 5 == 0){
            while (time != 1) {
                time = time / 5;
                result = result / 5;
            }
            return "1/" + result + " sec";
        }
        return "Error occurred while transferring exposure time!";
    }
}