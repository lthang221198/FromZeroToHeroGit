package ImageMover;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageMover {

    public static void main(String[] args) {
        // Specify the source and target directories
        String sourceDirectory = "D:\\DownloadsImages";
        String targetDirectory = "D:\\AnhForNhatAnh";
        long sizeThreshold = 100 * 1024; // 100 KB in bytes

        try {
            List<File> images = new ArrayList<>();
            findImages(new File(sourceDirectory), images);

            Map<String, File> movedImages = new HashMap<>();
            for (File image : images) {
                if (image.length() > sizeThreshold) {
                    String baseName = getBaseName(image.getName());
                    if (!movedImages.containsKey(baseName)) {
                        moveFile(image, new File(targetDirectory));
                        movedImages.put(baseName, image);
                    }
                }
            }

            // Resize images in the target directory
            File[] movedFiles = new File(targetDirectory).listFiles();
            if (movedFiles != null) {
                for (File file : movedFiles) {
                    resizeImage(file, 2048, 2048);
                }
            }

            System.out.println("Images moved and resized successfully.");
        } catch (IOException e) {
            System.err.println("Error moving or resizing images: " + e.getMessage());
        }
    }

    private static void findImages(File directory, List<File> images) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findImages(file, images);
                } else if (isImageFile(file)) {
                    images.add(file);
                }
            }
        }
    }

    private static boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "bmp", "gif", "tiff" };
        String fileName = file.getName().toLowerCase();
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private static void moveFile(File sourceFile, File targetDirectory) throws IOException {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        Path sourcePath = sourceFile.toPath();
        Path targetPath = Paths.get(targetDirectory.getPath(), sourceFile.getName());
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName;
        }
        String name = fileName.substring(0, dotIndex);
        int lastDashIndex = name.lastIndexOf('-');
        if (lastDashIndex == -1 || !name.substring(lastDashIndex + 1).matches("\\d+x\\d+")) {
            return name;
        }
        return name.substring(0, lastDashIndex);
    }

    private static void resizeImage(File file, int targetWidth, int targetHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        if (originalImage == null) {
            System.err.println("Skipping file (not a valid image): " + file.getName());
            return;
        }
        int imageType = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, imageType);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        boolean result = ImageIO.write(resizedImage, getFileExtension(file.getName()), file);
        if (result) {
            System.out.println("Resized image: " + file.getName());
        } else {
            System.err.println("Failed to resize image: " + file.getName());
        }
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
