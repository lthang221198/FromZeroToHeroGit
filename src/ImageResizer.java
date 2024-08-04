import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageResizer {

    private static final int TARGET_WIDTH = 2048;
    private static final int TARGET_HEIGHT = 2048;
   // private static final String DIRECTORY_PATH = "C:\\Users\\Huy Hoang\\Downloads\\ae24140f-b452-44ab-b0a8-5b3d6cbda35a"; // Set your path here
    private static final String DIRECTORY_PATH = "D:\\NewImages";
    private static final float JPEG_QUALITY = 0.3f; // Lower quality to reduce file size
    private static final long SIZE_THRESHOLD = 100 * 1024; // 100 KB in bytes

    public static void main(String[] args) {
        File folder = new File(DIRECTORY_PATH);
        if (!folder.isDirectory()) {
            System.out.println("Provided path is not a directory.");
            System.exit(1);
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
        if (files == null) {
            System.out.println("No images found in the directory.");
            System.exit(1);
        }

        for (File file : files) {
            if (file.length() <= SIZE_THRESHOLD) {
                // Delete the file if it is smaller than or equal to 100 KB
                if (file.delete()) {
                    System.out.println("Deleted file: " + file.getName());
                } else {
                    System.err.println("Failed to delete file: " + file.getName());
                }
                continue;
            }

            try {
                BufferedImage originalImage = ImageIO.read(file);
                if (originalImage == null) {
                    System.err.println("Unable to read image: " + file.getName());
                    continue;
                }
                BufferedImage resizedImage = resizeImage(originalImage, TARGET_WIDTH, TARGET_HEIGHT);

                // Convert PNG to JPEG if the original file is PNG
                if (file.getName().toLowerCase().endsWith(".png")) {
                    convertPNGToJPEG(resizedImage, file);
                } else {
                    writeJPEG(resizedImage, file);
                }

                System.out.println("Processed image saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error processing file: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    private static void writeJPEG(BufferedImage image, File file) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG writers found.");
        }
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(JPEG_QUALITY);

        writer.setOutput(ImageIO.createImageOutputStream(file));
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        writer.dispose();
    }

    private static void convertPNGToJPEG(BufferedImage image, File file) throws IOException {
        // Create a new file for the JPEG version
        File jpegFile = new File(file.getParent(), "converted_" + file.getName().replace(".png", ".jpg"));
        writeJPEG(image, jpegFile);
        // Delete the original PNG file
        if (file.delete()) {
            System.out.println("Deleted original PNG file: " + file.getName());
        } else {
            System.err.println("Failed to delete original PNG file: " + file.getName());
        }
    }
}
