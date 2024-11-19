package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// Основной класс программы
public class SalesManagerApp {
    private JFrame frame;
    private List<Client> clients = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Sale> sales = new ArrayList<>();
    private List<Supplier> suppliers = new ArrayList<>();
    private List<Warehouse> warehouses = new ArrayList<>();

    private DefaultComboBoxModel<Client> clientModel = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<Product> productModel = new DefaultComboBoxModel<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalesManagerApp::new);
    }

    public SalesManagerApp() {
        frame = new JFrame("АРМ менеджера по продажам");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Клиенты", createClientsPanel());
        tabbedPane.addTab("Товары", createProductsPanel());
        tabbedPane.addTab("Сделки", createSalesPanel());
        tabbedPane.addTab("Поставщики", createSuppliersPanel());
        tabbedPane.addTab("Склады", createWarehousesPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Вкладка "Клиенты"
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Имя", "Email"}, 0);
        JTable table = new JTable(model);

        JPanel formPanel = new JPanel(new GridLayout(1, 4));
        JTextField nameField = new JTextField();
        nameField.setForeground(Color.GRAY);
        nameField.setText("ФИО");
        JTextField emailField = new JTextField();
        emailField.setForeground(Color.GRAY);
        emailField.setText("Эл.Почта");
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Изменить");

        // Панель для фильтров и поиска
        JPanel filterPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton filterButton = new JButton("Фильтровать");

        filterPanel.add(new JLabel("Поиск:"));
        filterPanel.add(searchField);

        // Кнопки для добавления, редактирования и удаления



        formPanel.add(nameField);
        formPanel.add(emailField);
        formPanel.add(addButton);
        formPanel.add(editButton);
        JButton deleteButton = new JButton("Удалить");
        formPanel.add(deleteButton);

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirmation = JOptionPane.showConfirmDialog(panel, "Вы уверены, что хотите удалить клиента?", "Удалить", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    clients.remove(selectedRow);
                    model.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Выберите клиента для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Поиск по имени или email
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                // Фильтрация клиентов по имени или email
                List<Client> filteredClients = clients.stream()
                        .filter(client -> client.getName().toLowerCase().contains(searchText) || client.getEmail().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());

                model.setRowCount(0); // Очищаем таблицу
                for (Client client : filteredClients) {
                    model.addRow(new Object[]{client.getName(), client.getEmail()});
                }
            }
        });



        filterButton.addActionListener(e -> {
            // Для фильтрации по другим параметрам (например, статусу)
            String searchText = searchField.getText().toLowerCase();
            List<Client> filteredClients = clients.stream()
                    .filter(client -> client.getName().toLowerCase().contains(searchText) || client.getEmail().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            model.setRowCount(0); // Очищаем таблицу
            for (Client client : filteredClients) {
                model.addRow(new Object[]{client.getName(), client.getEmail()});
            }
        });

        // Когда поле получает фокус, убираем placeholder
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("ФИО")) {
                    nameField.setText(""); // Очищаем текст при фокусе
                    nameField.setForeground(Color.BLACK); // Цвет текста
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText("ФИО"); // Восстанавливаем placeholder, если текст пустой
                    nameField.setForeground(Color.GRAY); // Цвет для подсказки
                }

            }
        });

        // Когда поле получает фокус, убираем placeholder
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {

                if (emailField.getText().equals("Эл.Почта")) {
                    emailField.setText(""); // Очищаем текст при фокусе
                    emailField.setForeground(Color.BLACK); // Цвет текста
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (emailField.getText().isEmpty()) {
                    emailField.setText("Эл.Почта"); // Восстанавливаем placeholder, если текст пустой
                    emailField.setForeground(Color.GRAY); // Цвет для подсказки
                }
            }
        });


        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            if (!name.isEmpty() && !email.isEmpty()  && !nameField.getText().equals("ФИО")) {
                Client newClient = new Client(name, email);
                clients.add(newClient);
                model.addRow(new Object[]{name, email});
                clientModel.addElement(newClient); // Обновление выпадающего списка
                nameField.setText("");
                emailField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String newName = nameField.getText();
                String newEmail = emailField.getText();
                if (!newName.isEmpty() && !newEmail.isEmpty()) {
                    Client client = clients.get(selectedRow);
                    client.setName(newName);
                    client.setEmail(newEmail);
                    model.setValueAt(newName, selectedRow, 0);
                    model.setValueAt(newEmail, selectedRow, 1);
                    clientModel.removeElementAt(selectedRow);
                    clientModel.insertElementAt(client, selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Выберите клиента для изменения!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(filterPanel, BorderLayout.NORTH); // Панель для поиска и фильтра
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Вкладка "Товары"
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Название", "Цена", "Категория", "Остаток"}, 0);
        JTable table = new JTable(model);

        JPanel formPanel = new JPanel(new GridLayout(1, 5));
        JTextField nameField = new JTextField();
        nameField.setForeground(Color.GRAY);
        nameField.setText("Название");
        JTextField priceField = new JTextField();
        priceField.setForeground(Color.GRAY);
        priceField.setText("Цена");
        JTextField quantityField = new JTextField();
        quantityField.setForeground(Color.GRAY);
        quantityField.setText("Количество");
        JComboBox<Category> categoryBox = new JComboBox<>(Category.values());
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Изменить");


        // Панель для поиска и фильтра
        JPanel filterPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JComboBox<Category> filterCategory = new JComboBox<>(Category.values());
        JButton filterButton = new JButton("Фильтровать");

        filterPanel.add(new JLabel("Поиск:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Категория:"));
        filterPanel.add(filterCategory);
        filterPanel.add(filterButton);


        // Поиск по названию товара
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                List<Product> filteredProducts = products.stream()
                        .filter(product -> product.getName().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());

                model.setRowCount(0); // Очищаем таблицу
                for (Product product : filteredProducts) {
                    model.addRow(new Object[]{product.getName(), product.getPrice(), product.getCategory()});
                }
            }
        });

        // Фильтрация по категории
        filterButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            Category selectedCategory = (Category) filterCategory.getSelectedItem();
            List<Product> filteredProducts = products.stream()
                    .filter(product -> (product.getCategory() == selectedCategory || selectedCategory == null)
                            && product.getName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            model.setRowCount(0); // Очищаем таблицу
            for (Product product : filteredProducts) {
                model.addRow(new Object[]{product.getName(), product.getPrice(), product.getCategory()});
            }
        });



        filterButton.addActionListener(e -> {
            // Для фильтрации по другим параметрам (например, статусу)
            String searchText = searchField.getText().toLowerCase();
            List<Product> filteredProducts = products.stream()
                    .filter(client -> client.getName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            model.setRowCount(0); // Очищаем таблицу
            for (Product product : filteredProducts) {
                model.addRow(new Object[]{product.getName()});
            }
        });

        formPanel.add(nameField);
        formPanel.add(priceField);
        formPanel.add(quantityField);

        formPanel.add(categoryBox);
        formPanel.add(addButton);
        formPanel.add(editButton);
        JButton deleteButton = new JButton("Удалить");
        formPanel.add(deleteButton);

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirmation = JOptionPane.showConfirmDialog(panel, "Вы уверены, что хотите удалить товар?", "Удалить", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    products.remove(selectedRow);
                    model.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Выберите товар для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Когда поле получает фокус, убираем placeholder
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("Название")) {
                    nameField.setText(""); // Очищаем текст при фокусе
                    nameField.setForeground(Color.BLACK); // Цвет текста
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText("Название"); // Восстанавливаем placeholder, если текст пустой
                    nameField.setForeground(Color.GRAY); // Цвет для подсказки
                }

            }
        });
        // Когда поле получает фокус, убираем placeholder
        priceField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {

                if (priceField.getText().equals("Цена")) {
                    priceField.setText(""); // Очищаем текст при фокусе
                    priceField.setForeground(Color.BLACK); // Цвет текста
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (priceField.getText().isEmpty()) {
                    priceField.setText("Цена"); // Восстанавливаем placeholder, если текст пустой
                    priceField.setForeground(Color.GRAY); // Цвет для подсказки
                }
            }
        });

        // Когда поле получает фокус, убираем placeholder
        quantityField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {

                if (quantityField.getText().equals("Количество")) {
                    quantityField.setText(""); // Очищаем текст при фокусе
                    quantityField.setForeground(Color.BLACK); // Цвет текста
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (quantityField.getText().isEmpty()) {
                    quantityField.setText("Количество"); // Восстанавливаем placeholder, если текст пустой
                    quantityField.setForeground(Color.GRAY); // Цвет для подсказки
                }
            }
        });

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            try {
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                Category category = (Category) categoryBox.getSelectedItem();
                Product newProduct = new Product(name, price, category,quantity);
                products.add(newProduct);
                model.addRow(new Object[]{name, price, category, quantity});
                productModel.addElement(newProduct); // Обновление выпадающего списка
                nameField.setText("");
                priceField.setText("");
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите корректную цену!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1  && !nameField.getText().equals("Название")) {
                String newName = nameField.getText();
                try {
                    double newPrice = Double.parseDouble(priceField.getText());
                    Category newCategory = (Category) categoryBox.getSelectedItem();
                    Product product = products.get(selectedRow);
                    product.setName(newName);
                    product.setPrice(newPrice);
                    product.setCategory(newCategory);
                    model.setValueAt(newName, selectedRow, 0);
                    model.setValueAt(newPrice, selectedRow, 1);
                    model.setValueAt(newCategory, selectedRow, 2);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Введите корректную цену!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Выберите товар для изменения!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(filterPanel, BorderLayout.NORTH); // Панель для поиска и фильтра
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Вкладка "Сделки"
    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Клиент", "Товар", "Количество", "Сумма", "Статус"}, 0);
        JTable table = new JTable(model);

        JPanel formPanel = new JPanel(new GridLayout(1, 5));
        JComboBox<Client> clientBox = new JComboBox<>(clientModel);
        JComboBox<Product> productBox = new JComboBox<>(productModel);
        JComboBox<STATUS> statusBox = new JComboBox<>(STATUS.values());
        JTextField quantityField = new JTextField();

        JButton editStatusButton = new JButton("Стадия");
        quantityField.setForeground(Color.GRAY);
        quantityField.setText("Количество");
        JButton addButton = new JButton("Добавить");

        formPanel.add(clientBox);
        formPanel.add(productBox);
        formPanel.add(quantityField);
        formPanel.add(statusBox);
        formPanel.add(addButton);

        formPanel.add(editStatusButton);
        JButton deleteButton = new JButton("Удалить");
        formPanel.add(deleteButton);

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirmation = JOptionPane.showConfirmDialog(panel, "Вы уверены, что хотите удалить сделку?", "Удалить", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    sales.remove(selectedRow);
                    model.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Выберите сделку для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Когда поле получает фокус, убираем placeholder
        quantityField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (quantityField.getText().equals("Количество")) {
                    quantityField.setText(""); // Очищаем текст при фокусе
                    quantityField.setForeground(Color.BLACK); // Цвет текста
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (quantityField.getText().isEmpty()) {
                    quantityField.setText("Количество"); // Восстанавливаем placeholder, если текст пустой
                    quantityField.setForeground(Color.GRAY); // Цвет для подсказки
                }

            }
        });

        // Смена категории у выбранного продукта
        editStatusButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                Product selectedProduct = products.get(selectedRow);
                STATUS newStatus = (STATUS) JOptionPane.showInputDialog(panel,
                        "Cмена стадии:",
                        "Выберите стадию",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        STATUS.values(),
                        selectedProduct.getCategory());

                if (newStatus != null) {
                    selectedProduct.setStatus(newStatus);
                    model.setValueAt(newStatus, selectedRow, 4);  // Обновляем категорию в таблице
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Выберите сделку для смены стадии!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        addButton.addActionListener(e -> {
            Client client = (Client) clientBox.getSelectedItem();
            Product product = (Product) productBox.getSelectedItem();
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                STATUS status = (STATUS) statusBox.getSelectedItem();
                if (client != null && product != null) {
                    Sale sale = new Sale(client, product, quantity);
                    sales.add(sale);
                    model.addRow(new Object[]{
                            client.getName(),
                            product.getName(),
                            quantity,
                            sale.getTotalPrice(),
                            status.toString()
                    });
                    quantityField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите корректное количество!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }


    // Вкладка "Поставщики"
    private JPanel createSuppliersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Название", "Контакт"}, 0);
        JTable table = new JTable(model);

        JPanel formPanel = new JPanel(new GridLayout(1, 3));
        JTextField nameField = new JTextField();
        nameField.setForeground(Color.GRAY);
        nameField.setText("Название");
        JTextField contactField = new JTextField();
        contactField.setForeground(Color.GRAY);
        contactField.setText("Контакт");
        JButton addButton = new JButton("Добавить");

        formPanel.add(nameField);
        formPanel.add(contactField);
        formPanel.add(addButton);
        JButton deleteButton = new JButton("Удалить");
        formPanel.add(deleteButton);

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirmation = JOptionPane.showConfirmDialog(panel, "Вы уверены, что хотите удалить поставщика?", "Удалить", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    suppliers.remove(selectedRow);
                    model.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Выберите поставщика для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("Название")) {
                    nameField.setText(""); // Очищаем текст при фокусе
                    nameField.setForeground(Color.BLACK); // Цвет текста
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText("Название"); // Восстанавливаем placeholder, если текст пустой
                    nameField.setForeground(Color.GRAY); // Цвет для подсказки
                }

            }
        });
        contactField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (contactField.getText().equals("Контакт")) {
                    contactField.setText(""); // Очищаем текст при фокусе
                    contactField.setForeground(Color.BLACK); // Цвет текста
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (contactField.getText().isEmpty()) {
                    contactField.setText("Контакт"); // Восстанавливаем placeholder, если текст пустой
                    contactField.setForeground(Color.GRAY); // Цвет для подсказки
                }

            }
        });


        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String contact = contactField.getText();
            if (!name.isEmpty() && !contact.isEmpty()  && !nameField.getText().equals("Название")) {
                Supplier supplier = new Supplier(name, contact);
                suppliers.add(supplier);
                model.addRow(new Object[]{name, contact});
                nameField.setText("");
                contactField.setText("");
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Вкладка "Склады"
    private JPanel createWarehousesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Название", "Адрес"}, 0);
        JTable table = new JTable(model);

        JPanel formPanel = new JPanel(new GridLayout(1, 3));
        JTextField nameField = new JTextField();
        nameField.setForeground(Color.GRAY);
        nameField.setText("Название");
        JTextField addressField = new JTextField();
        addressField.setForeground(Color.GRAY);
        addressField.setText("Адрес");
        JButton addButton = new JButton("Добавить");

        formPanel.add(nameField);
        formPanel.add(addressField);
        formPanel.add(addButton);
        JButton deleteButton = new JButton("Удалить");
        formPanel.add(deleteButton);

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int confirmation = JOptionPane.showConfirmDialog(panel, "Вы уверены, что хотите удалить склад?", "Удалить", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    warehouses.remove(selectedRow);
                    model.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Выберите склад для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("Название")) {
                    nameField.setText(""); // Очищаем текст при фокусе
                    nameField.setForeground(Color.BLACK); // Цвет текста
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText("Название"); // Восстанавливаем placeholder, если текст пустой
                    nameField.setForeground(Color.GRAY); // Цвет для подсказки
                }

            }
        });

        addressField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (addressField.getText().equals("Адрес")) {
                    addressField.setText(""); // Очищаем текст при фокусе
                    addressField.setForeground(Color.BLACK); // Цвет текста
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (addressField.getText().isEmpty()) {
                    addressField.setText("Адрес"); // Восстанавливаем placeholder, если текст пустой
                    addressField.setForeground(Color.GRAY); // Цвет для подсказки
                }

            }
        });

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String address = addressField.getText();
            if (!name.isEmpty() && !address.isEmpty() && !nameField.getText().equals("Название")) {
                Warehouse warehouse = new Warehouse(name, address);
                warehouses.add(warehouse);
                model.addRow(new Object[]{name, address});
                nameField.setText("");
                addressField.setText("");
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Классы для товаров, клиентов и других объектов
    class Client {
        private String name;
        private String email;

        public Client(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        // Переопределение метода toString для вывода имени и email
        @Override
        public String toString() {
            return name + " (" + email + ")";
        }
    }


    class Product {
        private String name;
        private double price;
        private Category category;
        private int quantity;  // Количество оставшегося товара
        private STATUS status;

        public Product(String name, double price, Category category, int quantity) {
            this.name = name;
            this.price = price;
            this.category = category;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public void setStatus(STATUS status) {
            this.status = status;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        // Переопределение метода toString для вывода названия и цены товара
        @Override
        public String toString() {
            return name + " - " + price + " руб.";
        }
    }


    enum Category {
        ELECTRONICS, CLOTHING, FOOD
    }

    enum STATUS {
        INPROGRESS ("В РАБОТЕ"), RUINED ("ПРОВАЛЕНА"),SUCCESS ("УСПЕШНА");

        private String statusName;
        STATUS(String statusName) {
            this.statusName = statusName;
        }
        @Override
        public String toString() {
            return statusName;
        }

    }

    class Sale {
        private Client client;
        private Product product;
        private int quantity;

        public Sale(Client client, Product product, int quantity) {
            this.client = client;
            this.product = product;
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return product.getPrice() * quantity;
        }
    }

    class Supplier {
        private String name;
        private String contact;

        public Supplier(String name, String contact) {
            this.name = name;
            this.contact = contact;
        }

        public String getName() {
            return name;
        }

        public String getContact() {
            return contact;
        }

        // Переопределение метода toString для вывода имени и контакта поставщика
        @Override
        public String toString() {
            return name + " (" + contact + ")";
        }
    }

    public class DataManager {

        private static final String FILE_NAME = "products.dat"; // Файл для сохранения данных

        // Сохранение списка продуктов в файл
        public static void saveProducts(List<Product> products) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                oos.writeObject(products);
                System.out.println("Данные сохранены.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Ошибка при сохранении данных.");
            }
        }

        
    }


    class Warehouse {
        private String name;
        private String address;

        public Warehouse(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }
    }
}
