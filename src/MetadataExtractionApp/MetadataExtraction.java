package MetadataExtractionApp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.File;
import java.io.IOException;

import static com.drew.metadata.exif.ExifDirectoryBase.*;

public class MetadataExtraction {

    public static String[] processPhoto(File photo) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(photo);
        String focalLength = null;
        String fNumber = null;
        String iso = null;
        String exposureTime = null;

        for (Directory dir : metadata.getDirectories()) {
            if (focalLength == null) focalLength = dir.getDescription(TAG_FOCAL_LENGTH);
            if (fNumber == null) fNumber = dir.getDescription(TAG_FNUMBER);
            if (iso == null) iso = dir.getDescription(TAG_ISO_EQUIVALENT);
            if (exposureTime == null) exposureTime = dir.getDescription(TAG_EXPOSURE_TIME);
        }
        exposureTime = transferExposureTime(exposureTime);

        // Ošetření prázdných hodnot
        focalLength = (focalLength != null) ? focalLength : "N/A";
        fNumber = (fNumber != null) ? fNumber : "N/A";
        iso = (iso != null) ? "ISO-" + iso : "N/A";
        exposureTime = (exposureTime != null) ? exposureTime : "N/A";

        return new String[]{focalLength, fNumber, iso, exposureTime};
    }

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