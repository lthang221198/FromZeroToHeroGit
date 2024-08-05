package ImageMover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageMoverApp {
    private JFrame frame;
    private JTextField sourceField;
    private JTextField targetField;
    private JProgressBar progressBar;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageMoverApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Image Mover and Resizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel sourceLabel = new JLabel("Source Directory:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(sourceLabel, gbc);

        sourceField = new JTextField("D:\\DownloadsImages");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(sourceField, gbc);

        JButton sourceBrowseButton = new JButton("Browse");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        frame.add(sourceBrowseButton, gbc);

        JLabel targetLabel = new JLabel("Target Directory:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(targetLabel, gbc);

        targetField = new JTextField("D:\\AnhForNhatAnh");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(targetField, gbc);

        JButton targetBrowseButton = new JButton("Browse");
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(targetBrowseButton, gbc);

        JButton startButton = new JButton("Start");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(startButton, gbc);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        frame.add(progressBar, gbc);

        sourceBrowseButton.addActionListener(e -> chooseDirectory(sourceField));
        targetBrowseButton.addActionListener(e -> chooseDirectory(targetField));
        startButton.addActionListener(e -> new Task().execute());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void chooseDirectory(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            textField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private class Task extends SwingWorker<Void, Integer> {
        @Override
        protected Void doInBackground() throws Exception {
            File sourceDir = new File(sourceField.getText());
            File targetDir = new File(targetField.getText());

            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            File[] files = sourceDir.listFiles((dir, name) -> {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg") || lowercaseName.endsWith(".png");
            });

            if (files != null) {
                int totalFiles = files.length;
                int count = 0;

                for (File file : files) {
                    File destFile = new File(targetDir, file.getName());
                    if (file.length() > 100 * 1024) { // Move files larger than 100KB
                        moveAndResizeImage(file, destFile);
                    }

                    count++;
                    int progress = (int) ((count / (double) totalFiles) * 100);
                    publish(progress);
                }
            }

            return null;
        }

        @Override
        protected void process(java.util.List<Integer> chunks) {
            for (int progress : chunks) {
                progressBar.setValue(progress);
            }
        }

        @Override
        protected void done() {
            JOptionPane.showMessageDialog(frame, "Image moving and resizing completed!");
            progressBar.setValue(100);
        }
    }

    private void moveAndResizeImage(File source, File dest) throws IOException {
        BufferedImage originalImage = ImageIO.read(source);
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

        BufferedImage resizedImage = new BufferedImage(2048, 2048, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 2048, 2048, null);
        g.dispose();

        String formatName = dest.getName().substring(dest.getName().lastIndexOf(".") + 1);
        ImageIO.write(resizedImage, formatName, dest);
    }
}
