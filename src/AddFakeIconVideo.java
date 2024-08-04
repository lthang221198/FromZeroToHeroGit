import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AddFakeIconVideo {
    public static void main(String[] args) {
        try {
            // Load the base image and the play button image
            BufferedImage baseImage = ImageIO.read(new File("input_image.jpg"));
            BufferedImage playButton = ImageIO.read(new File("play_button.png"));

            // Calculate the position to center the play button on the base image
            int x = (baseImage.getWidth() - playButton.getWidth()) / 2;
            int y = (baseImage.getHeight() - playButton.getHeight()) / 2;

            // Create a new image with the same dimensions as the base image
            BufferedImage combined = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Draw the base image and the play button on the new image
            Graphics g = combined.getGraphics();
            g.drawImage(baseImage, 0, 0, null);
            g.drawImage(playButton, x, y, null);

            // Save the new image as a PNG file
            ImageIO.write(combined, "PNG", new File("output_image.png"));
            System.out.println("Play button added to the image successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
