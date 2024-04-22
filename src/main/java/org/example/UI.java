package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UI extends JFrame {
    private JPanel catalogPanel = new JPanel();
    private JList filesList = new JList<>();
    private JScrollPane filesScroll = new JScrollPane(filesList);
    private JPanel buttonsPanel = new JPanel();
    private JButton addFolder = new JButton("Создать папку");
    private JButton backButton = new JButton("Назад");
    private JButton deleteButton = new JButton("Удалить");
    private JButton renameButton = new JButton("Переименовать");
    private List<String> dirsCache = new ArrayList<>(); // каждый элемент является частью пути

    public UI() {
        super("Проводник");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // завершение программы путем нажатия на крестик
        setResizable(true); // возможность менять размер окна
        catalogPanel.setLayout(new BorderLayout(5, 5)); // настройка расположения элементов интерфейса
        catalogPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // граница окна
        buttonsPanel.setLayout(new GridLayout(1, 4, 5, 5));
        JDialog createNewDirDialog = new JDialog(UI.this, "Создание папки", true); // открывает
        // отдельное окно
        // true значит, что нельзя закрыть большое окно пока не закрыто маленькое
        JPanel createNewDirPanel = new JPanel();
        createNewDirDialog.add(createNewDirPanel);
        File[] discs = File.listRoots(); // массив всех корневых каталогов (дисков)
        filesScroll.setPreferredSize(new Dimension(400, 500)); // минимальные размеры скрол-панели
        filesList.setListData(discs); // отображение количества дисков в панели
        filesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // возможность
        // выбрать несколько значений

        filesList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) { // если кликнули
                if (event.getClickCount() == 2) {
                    DefaultListModel model = new DefaultListModel<>(); // создаем модель, в которую сможем динамически
                    // добавлять по каждому элементу
                    String selectedObject = filesList.getSelectedValue().toString();
                    String fullPath = toFullPath(dirsCache); // склейка полного пути
                    File selectedFile;
                    if (dirsCache.size() > 1) { // если мы находимся не в начале пути
                        selectedFile = new File(fullPath, selectedObject); // то создаем файл, указывая его путь и имя
                    } else { // если мы находимся на уровне дисков, создаем файл с указанием только пути
                        selectedFile = new File(fullPath + selectedObject);
                    }

                    if (selectedFile.isDirectory()) { // если выбранный файл является директорией
                        String[] rootStr = selectedFile.list(); // закидываем в массив все, что находится в директории
                        for (String str : rootStr) {
                            File checkObject = new File(selectedFile.getPath(), str); // проходимся по элементам директории
                            if (!checkObject.isHidden()) { // если элемент не скрыт
                                if (checkObject.isDirectory()) { // если элемент - это директория
                                    model.addElement(str); // то добавляем этот элемент в нашу модель
                                } else {
                                    model.addElement("файл-" + str); // так делаем если это файл
                                }
                            }
                        }
                    }

                    dirsCache.add(selectedObject); // добавляем новый каталог, в который мы зашли
                    filesList.setModel(model); // на лист добавляем модель с отобранными элементами
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });

        backButton.addActionListener(actionEvent -> {
            if (dirsCache.size() > 1) {
                dirsCache.remove(dirsCache.size() - 1); // удаляем из кэша последний элемент
                String backDir = toFullPath(dirsCache);
                String[] objects = new File(backDir).list();
                DefaultListModel backRootModel = new DefaultListModel<>();

                for (String str : objects) {
                    File checkFile = new File(backDir, str);
                    if (!checkFile.isHidden()) {
                        if (checkFile.isDirectory()) {
                            backRootModel.addElement(str);
                        } else {
                            backRootModel.addElement("файл-" + str);
                        }
                    }
                }
                filesList.setModel(backRootModel);
            } else {
                dirsCache.removeAll(dirsCache);
                filesList.setListData(discs);
            }
        });

        addFolder.addActionListener(event -> {
            if (!dirsCache.isEmpty()) {
                String currentPath;
                File newFolder;
                CreateNewFolderJDialog newFolderJDialog = new CreateNewFolderJDialog(UI.this);

                if (newFolderJDialog.getReady()) {
                    currentPath = toFullPath(dirsCache);
                    newFolder = new File(currentPath, newFolderJDialog.getNewName());
                    if (!newFolder.exists()) {
                        newFolder.mkdir();

                        File updateDir = new File(currentPath);
                        String[] updateMas = updateDir.list();
                        DefaultListModel updateModel = new DefaultListModel<>();
                        for (String str : updateMas) {
                            File check = new File(updateDir.getPath(), str);
                            if (!check.isHidden()) {
                                if (check.isDirectory()) {
                                    updateModel.addElement(str);
                                } else {
                                    updateModel.addElement("файл-" + str);
                                }
                            }
                        }
                        filesList.setModel(updateModel);
                    }
                }
            }
        });

        deleteButton.addActionListener(event -> {
            String selectedObject = filesList.getSelectedValue().toString();
            String currentPath = toFullPath(dirsCache);
            if (!selectedObject.isEmpty()) {

                deleteDir(new File(currentPath, selectedObject));

                File updateDir = new File(currentPath);
                String[] updateMas = updateDir.list();
                DefaultListModel updateModel = new DefaultListModel<>();

                for (String str : updateMas) {
                    File check = new File(updateDir.getPath(), str);
                    if (!check.isHidden()) {
                        if (check.isDirectory()) {
                            updateModel.addElement(str);
                        } else {
                            updateModel.addElement("файл-" + str);
                        }
                    }
                }
                filesList.setModel(updateModel);
            }
        });

        renameButton.addActionListener(e -> {
            if (!dirsCache.isEmpty() && filesList.getSelectedValue() != null) {
                String selectedObject = filesList.getSelectedValue().toString();
                String currentPath = toFullPath(dirsCache);

                RenameJDialog renameJDialog = new RenameJDialog(UI.this);
                if (renameJDialog.getReady()) {
                    File renameFile = new File(currentPath, selectedObject);
                    renameFile.renameTo(new File(currentPath, renameJDialog.getNewName()));

                    File updateDir = new File(currentPath);
                    String[] updateMas = updateDir.list();
                    DefaultListModel updateModel = new DefaultListModel<>();

                    for (String str : updateMas) {
                        File check = new File(updateDir.getPath(), str);
                        if (!check.isHidden()) {
                            if (check.isDirectory()) {
                                updateModel.addElement(str);
                            } else {
                                updateModel.addElement("файл-" + str);
                            }
                        }
                    }
                    filesList.setModel(updateModel);
                }
            }
        });

        buttonsPanel.add(backButton);
        buttonsPanel.add(addFolder);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(renameButton);
        catalogPanel.setLayout(new BorderLayout());
        catalogPanel.add(filesScroll, BorderLayout.CENTER);
        catalogPanel.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add(catalogPanel);// добавляем нашу панель
        setSize(600,600);
        //pack();
        setLocationRelativeTo(null); // при открытии появится посередине
        setVisible(true);

    }

    private String toFullPath(List<String> file) { // склеиваем путь
        StringBuilder listPart = new StringBuilder();
        for (String str : file) {
            listPart.append(str);
        }
        return listPart.toString();
    }

    public void deleteDir(File file) {
        File[] objects = file.listFiles();
        if (objects != null) {
            for (File f : objects) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
