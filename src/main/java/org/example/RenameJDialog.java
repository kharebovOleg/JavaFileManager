package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RenameJDialog extends JDialog {
    private JTextField nameOfNewFolder = new JTextField();
    private JButton okButton = new JButton("Переименовать");
    private JButton cancelButton = new JButton("Отмена");
    private String newFolderName;
    private JLabel nameFolderWait = new JLabel("Новое имя: ");
    private boolean ready = false;

    public RenameJDialog(JFrame jFrame) {
        super(jFrame, "Переименовать", true);
        setLayout(new GridLayout(2, 2, 5, 5));
        setSize(400, 200);

        okButton.addActionListener(event -> {
            newFolderName = nameOfNewFolder.getText();
            setVisible(false);
            ready = true;
        });

        cancelButton.addActionListener(actionEvent -> {
            setVisible(false);
            ready = false;
        });

        getContentPane().add(nameFolderWait);
        getContentPane().add(nameOfNewFolder);
        getContentPane().add(okButton);
        getContentPane().add(cancelButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getNewName() {
        return newFolderName;
    }

    public boolean getReady() {
        return ready;
    }
}
