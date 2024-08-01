import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class RenameJPGFiles {

    public static void main(String[] args) {
        // Specify the folder path
        String folderPath = "D:\\GIRLS"; // Change this to your folder path
        File folder = new File(folderPath);

        // Check if the folder exists
        if (!folder.exists()) {
            System.out.println("The specified folder does not exist.");
            return;
        }

        // Get all jpg files in the folder
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("No .jpg files found in the specified folder.");
            return;
        }

        // Rename each file
        for (File file : listOfFiles) {
            String newFileName = generateRandomFileName() + ".jpg";
            File newFile = new File(folderPath, newFileName);

            // Ensure the new file name does not already exist
            while (newFile.exists()) {
                newFileName = generateRandomFileName() + ".jpg";
                newFile = new File(folderPath, newFileName);
            }

            // Rename the file
            try {
                Files.move(file.toPath(), newFile.toPath());
                System.out.println("Renamed: " + file.getName() + " to " + newFileName);
            } catch (IOException e) {
                System.err.println("Failed to rename: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    // Method to generate a random 4-character file name
    private static String generateRandomFileName() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder fileName = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            fileName.append(characters.charAt(random.nextInt(characters.length())));
        }

        return fileName.toString();
    }
}
