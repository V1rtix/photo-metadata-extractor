import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ImageProcessingException, IOException {

        String photosPath = "photos";
        File photoFolder = new File(photosPath);

        for (File file : photoFolder.listFiles()) {
            if (file != null) {
                processPhoto(file);
            } else {
                System.out.println("No photos to process");
            }
        }

        File photo = new File("photos/test.jpg/");

    }

    private static void processPhoto(File photo) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(photo);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);

        String name = descriptor.getFileSourceDescription();
        String focalLength = descriptor.getFocalLengthDescription();
        String fNumber = descriptor.getFNumberDescription();
        String iso = descriptor.getIsoEquivalentDescription();
        String exposure = descriptor.getExposureTimeDescription();

        System.out.println(photo.getName() + " - " + focalLength + " | " + fNumber + " | ISO-" + iso + " | " + exposure);
    }
}