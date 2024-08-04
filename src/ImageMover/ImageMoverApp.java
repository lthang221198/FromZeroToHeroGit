package ImageMover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class ImageMoverApp extends JFrame {

    private JTextField sourceField;
    private JTextField targetField;
    private JButton browseSourceButton;
    private JButton browseTargetButton;

    public ImageMoverApp() {
        setTitle("Image Mover");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create UI components
        sourceField = new JTextField("D:\\DownloadsImages", 30);
        targetField = new JTextField("D:\\AnhForNhatAnh", 30);
        browseSourceButton = new JButton("Browse");
        browseTargetButton = new JButton("Browse");
        JButton startButton = new JButton("Start");

        // Set preferred size for browse buttons
        browseSourceButton.setPreferredSize(new Dimension(100, 25));
        browseTargetButton.setPreferredSize(new Dimension(100, 25));

        // Create layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Source Directory:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(sourceField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(browseSourceButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Target Directory:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(targetField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(browseTargetButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(new JLabel(""), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panel.add(startButton, gbc);

        add(panel);

        // Add button listeners
        browseSourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory(sourceField);
            }
        });

        browseTargetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirectory(targetField);
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceDirectory = sourceField.getText();
                String targetDirectory = targetField.getText();
                try {
                    moveAndResizeImages(sourceDirectory, targetDirectory);
                    JOptionPane.showMessageDialog(null, "Images moved and resized successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void chooseDirectory(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = chooser.getSelectedFile();
            textField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void moveAndResizeImages(String sourceDirectory, String targetDirectory) throws IOException {
        File sourceDir = new File(sourceDirectory);
        File targetDir = new File(targetDirectory);

        // Move images
        List<File> images = new ArrayList<>();
        findImages(sourceDir, images);

        Map<String, File> movedImages = new HashMap<>();
        for (File image : images) {
            if (image.length() > 100 * 1024) { // 100 KB
                String baseName = getBaseName(image.getName());
                if (!movedImages.containsKey(baseName)) {
                    moveFile(image, targetDir);
                    movedImages.put(baseName, image);
                }
            }
        }

        // Resize images
        File[] movedFiles = targetDir.listFiles();
        if (movedFiles != null) {
            for (File file : movedFiles) {
                resizeImage(file, 2048, 2048);
            }
        }
    }

    private void findImages(File directory, List<File> images) {
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

    private boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "bmp", "gif", "tiff" };
        String fileName = file.getName().toLowerCase();
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private void moveFile(File sourceFile, File targetDirectory) throws IOException {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        Path sourcePath = sourceFile.toPath();
        Path targetPath = Paths.get(targetDirectory.getPath(), sourceFile.getName());
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private String getBaseName(String fileName) {
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

    private void resizeImage(File file, int targetWidth, int targetHeight) throws IOException {
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

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageMoverApp().setVisible(true);
        });
    }
}
