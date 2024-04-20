package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UI extends JFrame {
    private JPanel catalogPanel = new JPanel();
    private JList filesList = new JList<>();
    private JScrollPane filesScroll = new JScrollPane(filesList);
    private JPanel buttonsPanel = new JPanel();
    private Button addButton = new Button("Создать папку");
    private Button backButton = new Button("Назад");
    private Button deleteButton = new Button("Удалить");
    private Button renameButton = new Button("Переименовать");
    private List<String> dirsCache = new ArrayList<>();

    public UI() {
        super("Проводник");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        catalogPanel.setLayout(new BorderLayout(5,5));
        catalogPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        buttonsPanel.setLayout(new GridLayout(1,4,5,5));
    }
}
