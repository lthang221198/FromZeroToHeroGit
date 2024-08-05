package ImageFormatName;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class ImageFormatNameApp {
    private JFrame frame;
    private JTextField directoryField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageFormatNameApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Image Renamer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel directoryLabel = new JLabel("Directory Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(directoryLabel, gbc);

        directoryField = new JTextField("D:\\Test");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(directoryField, gbc);

        JButton browseButton = new JButton("Browse");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        frame.add(browseButton, gbc);

        JButton renameButton = new JButton("Rename");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(renameButton, gbc);

        browseButton.addActionListener(e -> chooseDirectory());
        renameButton.addActionListener(e -> renameImages());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void chooseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void renameImages() {
        String directoryPath = directoryField.getText();
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            JOptionPane.showMessageDialog(frame, "Invalid directory path.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            JOptionPane.showMessageDialog(frame, "No files found in the directory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Arrays.sort(files);
        int imageCount = files.length;

        if (imageCount == 32) {
            renameImages(files, new String[]{"s", "c", "t", "d"}, 8);
        } else if (imageCount == 40) {
            renameImages(files, new String[]{"s", "c", "t", "d", "h"}, 8);
        } else {
            JOptionPane.showMessageDialog(frame, "Expected 32 or 40 images. Found: " + imageCount, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renameImages(File[] files, String[] prefixes, int maxNumber) {
        int index = 0;
        for (String prefix : prefixes) {
            for (int i = 1; i <= maxNumber; i++) {
                if (index >= files.length) return;
                File file = files[index++];
                String fileExtension = getFileExtension(file);
                String newName = String.format("%s%d%s", prefix, i, fileExtension);
                renameFile(file, newName);
            }
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);
    }

    private void renameFile(File oldFile, String newFileName) {
        File newFile = new File(oldFile.getParent(), newFileName);
        if (oldFile.renameTo(newFile)) {
            System.out.println("Renamed " + oldFile.getName() + " to " + newFileName);
        } else {
            System.err.println("Failed to rename " + oldFile.getName());
        }
    }
}
