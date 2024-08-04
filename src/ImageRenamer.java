import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageRenamer {
    public static void main(String[] args) {
        // Specify the directory containing the images
        String directoryPath = "D:\\Test";
        File directory = new File(directoryPath);

        // Check if the directory exists and is a directory
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory path.");
            return;
        }

        // List all files in the directory
        File[] files = directory.listFiles();
        if (files == null) {
            System.err.println("No files found in the directory.");
            return;
        }

        // Sort files to ensure consistent ordering
        Arrays.sort(files);

        int imageCount = files.length;

        if (imageCount == 32) {
            renameImages(files, new String[]{"s", "c", "t", "d"}, 8);
        } else if (imageCount == 40) {
            renameImages(files, new String[]{"s", "c", "t", "d", "h"}, 8);
        } else {
            System.out.println("Expected 32 or 40 images. Found: " + imageCount);
        }
    }

    private static void renameImages(File[] files, String[] prefixes, int maxNumber) {
        int index = 0;
        for (String prefix : prefixes) {
            for (int i = 1; i <= maxNumber; i++) {
                if (index >= files.length) return; // Exit if no more files
                File file = files[index++];
                String fileExtension = getFileExtension(file);
                String newName = String.format("%s%d%s", prefix, i, fileExtension);
                renameFile(file, newName);
            }
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);
    }

    private static void renameFile(File oldFile, String newFileName) {
        File newFile = new File(oldFile.getParent(), newFileName);
        if (oldFile.renameTo(newFile)) {
            System.out.println("Renamed " + oldFile.getName() + " to " + newFileName);
        } else {
            System.err.println("Failed to rename " + oldFile.getName());
        }
    }
}
