package ImageFormatName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ImageFormatNameApp {
    private JFrame frame;
    private JTextField directoryField;
    private JRadioButton option32;
    private JRadioButton option40;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageFormatNameApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Image Format and Rename");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
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

        option32 = new JRadioButton("32 Images");
        option40 = new JRadioButton("40 Images");
        ButtonGroup group = new ButtonGroup();
        group.add(option32);
        group.add(option40);
        option32.setSelected(true); // Default selection

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(option32, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(option40, gbc);

        JButton renameButton = new JButton("Start");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(renameButton, gbc);

        browseButton.addActionListener(e -> chooseDirectory());
        renameButton.addActionListener(e -> new Task().execute());

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

    private class Task extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            String directoryPath = directoryField.getText();
            File directory = new File(directoryPath);

            if (!directory.exists() || !directory.isDirectory()) {
                JOptionPane.showMessageDialog(frame, "Invalid directory path.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            File[] files = directory.listFiles();
            if (files == null) {
                JOptionPane.showMessageDialog(frame, "No files found in the directory.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            int imageCount = files.length;
            int targetCount = option32.isSelected() ? 32 : option40.isSelected() ? 40 : 0;

            if (imageCount < targetCount) {
                fillWithRandomImages(directory, targetCount - imageCount);
            }

            files = directory.listFiles();
            if (files != null) {
                Arrays.sort(files);
                if (files.length == 32) {
                    renameImages(files, new String[]{"s", "c", "t", "d"}, 8);
                } else if (files.length == 40) {
                    renameImages(files, new String[]{"s", "c", "t", "d", "h"}, 8);
                } else {
                    JOptionPane.showMessageDialog(frame, "Expected 32 or 40 images. Found: " + files.length, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(frame, "Image processing completed!");
        }
    }

    private void fillWithRandomImages(File directory, int count) throws IOException {
        File sourceDir = new File("D:\\GIRLS");
        File[] sourceFiles = sourceDir.listFiles((dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg") || lowercaseName.endsWith(".png");
        });

        if (sourceFiles == null || sourceFiles.length == 0) {
            JOptionPane.showMessageDialog(frame, "No images found in the source directory (D:\\GIRLS).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<File> fileList = Arrays.asList(sourceFiles);
        Collections.shuffle(fileList, new Random());

        for (int i = 0; i < count; i++) {
            File sourceFile = fileList.get(i % fileList.size());
            File destFile = new File(directory, sourceFile.getName());
            Files.copy(sourceFile.toPath(), destFile.toPath());
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
