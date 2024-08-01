import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class RenameFilesGUI extends JFrame {
    private JTextField folderPathField;
    private JTextField fileTypesField;
    private JTextField numCharactersField;
    private JButton browseButton;
    private JButton renameButton;

    public RenameFilesGUI() {
        setTitle("Rename Files");
        setSize(600, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        folderPathField = new JTextField("D:\\GIRLS");
        fileTypesField = new JTextField("jpg,png");
        numCharactersField = new JTextField("4");
        browseButton = new JButton("Browse...");
        renameButton = new JButton("Rename Files");

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BorderLayout());
        pathPanel.add(new JLabel("Folder Path: "), BorderLayout.WEST);
        pathPanel.add(folderPathField, BorderLayout.CENTER);
        pathPanel.add(browseButton, BorderLayout.EAST);

        JPanel fileTypesPanel = new JPanel();
        fileTypesPanel.setLayout(new BorderLayout());
        fileTypesPanel.add(new JLabel("File Types (comma-separated, e.g., jpg,png): "), BorderLayout.WEST);
        fileTypesPanel.add(fileTypesField, BorderLayout.CENTER);

        JPanel numCharactersPanel = new JPanel();
        numCharactersPanel.setLayout(new BorderLayout());
        numCharactersPanel.add(new JLabel("Number of Characters for New Names: "), BorderLayout.WEST);
        numCharactersPanel.add(numCharactersField, BorderLayout.CENTER);

        add(pathPanel);
        add(fileTypesPanel);
        add(numCharactersPanel);
        add(renameButton);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(RenameFilesGUI.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File folder = fileChooser.getSelectedFile();
                    folderPathField.setText(folder.getAbsolutePath());
                }
            }
        });

        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String folderPath = folderPathField.getText();
                String fileTypes = fileTypesField.getText();
                int numCharacters;
                try {
                    numCharacters = Integer.parseInt(numCharactersField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(RenameFilesGUI.this, "Number of characters must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                renameFiles(folderPath, fileTypes, numCharacters);
            }
        });
    }

    private void renameFiles(String folderPath, String fileTypes, int numCharacters) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            JOptionPane.showMessageDialog(this, "The specified folder does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] types = fileTypes.split(",");
        for (String type : types) {
            File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith("." + type.trim().toLowerCase()));
            if (listOfFiles == null || listOfFiles.length == 0) {
                JOptionPane.showMessageDialog(this, "No ." + type + " files found in the specified folder.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            for (File file : listOfFiles) {
                String newFileName = generateRandomFileName(numCharacters) + "." + type.trim().toLowerCase();
                File newFile = new File(folderPath, newFileName);

                while (newFile.exists()) {
                    newFileName = generateRandomFileName(numCharacters) + "." + type.trim().toLowerCase();
                    newFile = new File(folderPath, newFileName);
                }

                try {
                    Files.move(file.toPath(), newFile.toPath());
                    System.out.println("Renamed: " + file.getName() + " to " + newFileName);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Failed to rename: " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Files renamed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private String generateRandomFileName(int numCharacters) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder fileName = new StringBuilder(numCharacters);

        for (int i = 0; i < numCharacters; i++) {
            fileName.append(characters.charAt(random.nextInt(characters.length())));
        }

        return fileName.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RenameFilesGUI().setVisible(true);
            }
        });
    }
}
