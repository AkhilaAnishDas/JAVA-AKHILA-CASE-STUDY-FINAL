import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BillKaunBharega extends JFrame {

    static final String APP_NAME = "Bill Kaun Bharega?";
    static final String TAGLINE = "Pehle Khaao, Phir Socho.";

    static final Path DATA_DIR = Paths.get("data");
    static final Path MENU_FILE = DATA_DIR.resolve("menu_data.txt");
    static final Path BILL_DIR = Paths.get("bills");

    static final BigDecimal DEFAULT_TAX = new BigDecimal("5.00");
    static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static final List<MenuItemData> menuItems = new ArrayList<>();
    static final List<Order> orders = new ArrayList<>();

    static int nextOrderNumber = 1001;

    JTabbedPane tabs;

    JTextField customerField;
    JTextField discountField;
    JTextField taxField;

    JComboBox<String> categoryBox;
    JComboBox<String> itemBox;
    JSpinner quantitySpinner;

    JTable cartTable;
    DefaultTableModel cartModel;

    JLabel subtotalLabel;
    JLabel discountLabel;
    JLabel taxLabel;
    JLabel totalLabel;

    JComboBox<String> statusBox;

    JTable orderTable;
    DefaultTableModel orderModel;

    JTextField searchOrderField;
    JComboBox<String> searchStatusBox;

    JTable menuTable;
    DefaultTableModel menuModel;

    JLabel summaryOrdersLabel;
    JLabel summaryRevenueLabel;
    JLabel summarySubtotalLabel;
    JLabel summaryDiscountLabel;
    JLabel summaryTaxLabel;
    JLabel summaryItemsLabel;
    JLabel summaryAverageLabel;

    JTextArea summaryArea;

    Order editingOrder = null;

    public BillKaunBharega() {
        setTitle(APP_NAME);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        initializeFolders();

        orders.clear();
        editingOrder = null;

        loadMenu();

        if (menuItems.isEmpty()) {
            createDefaultMenu();
            saveMenu();
        }

        buildUI();
        refreshAll();
    }

    static void initializeFolders() {
        try {
            Files.createDirectories(DATA_DIR);
            Files.createDirectories(BILL_DIR);
        } catch (IOException e) {
            showErrorStatic("Unable to create application folders.");
        }
    }

    static void createDefaultMenu() {

        addDefault("Starters", "Manchow Soup", "270");
        addDefault("Starters", "Paneer Tikka", "220");
        addDefault("Starters", "Medu Vada", "80");
        addDefault("Starters", "Chilli Paneer", "230");
        addDefault("Starters", "Chicken Tikka", "240");
        addDefault("Starters", "Veg Spring Roll", "140");
        addDefault("Starters", "Hara Bhara Kebab", "180");
        addDefault("Starters", "Aloo Tikki", "110");
        addDefault("Starters", "Crispy Corn", "170");
        addDefault("Starters", "Veg Seekh Kebab", "190");

        addDefault("Main Course", "Butter Chicken", "280");
        addDefault("Main Course", "Chicken Tikka Masala", "290");
        addDefault("Main Course", "Shahi Paneer", "270");
        addDefault("Main Course", "Dal Makhani", "170");
        addDefault("Main Course", "Chole Masala", "150");
        addDefault("Main Course", "Rajma Masala", "150");
        addDefault("Main Course", "Paneer Butter Masala", "260");
        addDefault("Main Course", "Kadai Paneer", "280");
        addDefault("Main Course", "Veg Kolhapuri", "240");
        addDefault("Main Course", "Malai Kofta", "260");

        addDefault("Rice & Biryani", "Chicken Biryani", "260");
        addDefault("Rice & Biryani", "Veg Fried Rice", "180");
        addDefault("Rice & Biryani", "Veg Biryani", "200");
        addDefault("Rice & Biryani", "Jeera Rice", "130");
        addDefault("Rice & Biryani", "Veg Pulao", "150");
        addDefault("Rice & Biryani", "Dal Rice", "140");
        addDefault("Rice & Biryani", "Paneer Fried Rice", "210");
        addDefault("Rice & Biryani", "Schezwan Fried Rice", "200");
        addDefault("Rice & Biryani", "Veg Hakka Noodles", "190");
        addDefault("Rice & Biryani", "Paneer Biryani", "230");

        addDefault("Breads", "Naan", "50");
        addDefault("Breads", "Butter Naan", "65");
        addDefault("Breads", "Roti", "25");
        addDefault("Breads", "Tandoori Roti", "35");
        addDefault("Breads", "Paratha", "70");
        addDefault("Breads", "Garlic Naan", "80");
        addDefault("Breads", "Cheese Naan", "120");
        addDefault("Breads", "Missi Roti", "60");
        addDefault("Breads", "Laccha Paratha", "85");
        addDefault("Breads", "Stuffed Aloo Paratha", "110");

        addDefault("Beverages", "Masala Chai", "40");
        addDefault("Beverages", "Mango Lassi", "90");
        addDefault("Beverages", "Sweet Lassi", "80");
        addDefault("Beverages", "Nimbu Pani", "50");
        addDefault("Beverages", "Milkshake", "120");
        addDefault("Beverages", "Fruit Juice", "100");
        addDefault("Beverages", "Cold Coffee", "110");
        addDefault("Beverages", "Fresh Lime Soda", "70");
        addDefault("Beverages", "Rose Milk", "90");
        addDefault("Beverages", "Masala Buttermilk", "60");

        addDefault("Desserts", "Gulab Jamun", "110");
        addDefault("Desserts", "Rasmalai", "90");
        addDefault("Desserts", "Jalebi", "80");
        addDefault("Desserts", "Kulfi", "100");
        addDefault("Desserts", "Ice Cream", "90");
        addDefault("Desserts", "Pastry", "100");
        addDefault("Desserts", "Gajar Halwa", "120");
        addDefault("Desserts", "Kheer", "100");
        addDefault("Desserts", "Brownie", "140");
        addDefault("Desserts", "Fruit Custard", "110");
    }

    static void addDefault(String category, String name, String price) {
        menuItems.add(
                new MenuItemData(
                        generateMenuId(category),
                        category,
                        name,
                        new BigDecimal(price)
                )
        );
    }

    static String categoryCode(String category) {
        switch (category) {
            case "Starters":
                return "ST";
            case "Main Course":
                return "MC";
            case "Rice & Biryani":
                return "RB";
            case "Breads":
                return "BR";
            case "Beverages":
                return "BV";
            case "Desserts":
                return "DS";
            default:
                String letters = category.replaceAll("[^A-Za-z]", "").toUpperCase();
                if (letters.length() >= 2) {
                    return letters.substring(0, 2);
                }
                if (letters.length() == 1) {
                    return letters + "X";
                }
                return "OT";
        }
    }

    static String generateMenuId(String category) {
        String normalizedCategory = canonicalCategory(category);
        String code = categoryCode(normalizedCategory);
        int next = 1;

        for (MenuItemData item : menuItems) {
            String itemCategory = canonicalCategory(item.category);

            if (itemCategory.equals(normalizedCategory)) {
                String prefix = "ITEM-";
                String suffix = "-" + code;

                if (item.id != null
                        && item.id.startsWith(prefix)
                        && item.id.endsWith(suffix)) {

                    String numberPart =
                            item.id.substring(
                                    prefix.length(),
                                    item.id.length() - suffix.length()
                            );

                    try {
                        int number = Integer.parseInt(numberPart);
                        next = Math.max(next, number + 1);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        return String.format("ITEM-%02d-%s", next, code);
    }

    static boolean normalizeMenuIds() {
        List<String> categoryOrder = Arrays.asList(
                "Starters",
                "Main Course",
                "Rice & Biryani",
                "Breads",
                "Beverages",
                "Desserts"
        );

        List<String> defaultItemOrder = Arrays.asList(
                "Manchow Soup", "Paneer Tikka", "Medu Vada", "Chilli Paneer",
                "Chicken Tikka", "Veg Spring Roll", "Hara Bhara Kebab", "Aloo Tikki",
                "Crispy Corn", "Veg Seekh Kebab",
                "Butter Chicken", "Chicken Tikka Masala", "Shahi Paneer", "Dal Makhani",
                "Chole Masala", "Rajma Masala", "Paneer Butter Masala", "Kadai Paneer",
                "Veg Kolhapuri", "Malai Kofta",
                "Chicken Biryani", "Veg Fried Rice", "Veg Biryani", "Jeera Rice",
                "Veg Pulao", "Dal Rice", "Paneer Fried Rice", "Schezwan Fried Rice",
                "Veg Hakka Noodles", "Paneer Biryani",
                "Naan", "Butter Naan", "Roti", "Tandoori Roti", "Paratha", "Garlic Naan",
                "Cheese Naan", "Missi Roti", "Laccha Paratha", "Stuffed Aloo Paratha",
                "Masala Chai", "Mango Lassi", "Sweet Lassi", "Nimbu Pani", "Milkshake",
                "Fruit Juice", "Cold Coffee", "Fresh Lime Soda", "Rose Milk", "Masala Buttermilk",
                "Gulab Jamun", "Rasmalai", "Jalebi", "Kulfi", "Ice Cream", "Pastry",
                "Gajar Halwa", "Kheer", "Brownie", "Fruit Custard"
        );

        Map<String, Integer> categoryRanks = new HashMap<>();
        for (int i = 0; i < categoryOrder.size(); i++) {
            categoryRanks.put(categoryOrder.get(i), i);
        }

        Map<String, Integer> itemRanks = new HashMap<>();
        for (int i = 0; i < defaultItemOrder.size(); i++) {
            itemRanks.put(defaultItemOrder.get(i), i);
        }

        menuItems.sort(
                Comparator.comparingInt((MenuItemData m) ->
                                categoryRanks.getOrDefault(m.category, 999))
                        .thenComparingInt(m ->
                                itemRanks.getOrDefault(m.name, 999))
                        .thenComparing(m -> m.name, String.CASE_INSENSITIVE_ORDER)
        );

        Map<String, Integer> counters = new LinkedHashMap<>();
        boolean changed = false;

        for (MenuItemData item : menuItems) {
            String code = categoryCode(item.category);
            int next = counters.getOrDefault(item.category, 0) + 1;
            counters.put(item.category, next);

            String expectedId = String.format("ITEM-%02d-%s", next, code);

            if (!expectedId.equals(item.id)) {
                item.id = expectedId;
                changed = true;
            }
        }

        return changed;
    }

    void buildUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 245, 250));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(18, 24, 18, 24));
        header.setBackground(new Color(55, 18, 74));

        JLabel title = new JLabel(APP_NAME);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel tagline = new JLabel(TAGLINE);
        tagline.setForeground(new Color(245, 220, 240));
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 15));

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.add(title);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(tagline);

        header.add(headerText, BorderLayout.WEST);

        JLabel date = new JLabel(
                "Today: " + LocalDate.now().format(DATE_FORMAT)
        );
        date.setForeground(Color.WHITE);
        date.setFont(new Font("SansSerif", Font.BOLD, 14));

        header.add(date, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("New Order", createOrderPanel());
        tabs.addTab("Orders", createOrdersPanel());
        tabs.addTab("Menu Management", createMenuPanel());
        tabs.addTab("Sales Summary", createSummaryPanel());

        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);
    }

    JPanel createOrderPanel() {

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(new EmptyBorder(18, 18, 18, 18));
        main.setBackground(Color.WHITE);

        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(
                BorderFactory.createTitledBorder("Customer & Bill")
        );
        customerPanel.setBackground(Color.WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 8, 7, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        customerField = new JTextField(20);
        discountField = new JTextField("0", 8);
        taxField = new JTextField(DEFAULT_TAX.toPlainString(), 8);

        g.gridx = 0;
        g.gridy = 0;
        customerPanel.add(new JLabel("Customer Name:"), g);

        g.gridx = 1;
        customerPanel.add(customerField, g);

        g.gridx = 2;
        customerPanel.add(new JLabel("Discount %:"), g);

        g.gridx = 3;
        customerPanel.add(discountField, g);

        g.gridx = 4;
        customerPanel.add(new JLabel("Tax %:"), g);

        g.gridx = 5;
        customerPanel.add(taxField, g);

        JPanel itemPanel = new JPanel(new GridBagLayout());
        itemPanel.setBorder(
                BorderFactory.createTitledBorder("Add Items")
        );
        itemPanel.setBackground(Color.WHITE);

        categoryBox = new JComboBox<>();
        itemBox = new JComboBox<>();
        quantitySpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, 999, 1)
        );

        populateCategories();

        categoryBox.addActionListener(e -> populateItems());

        JButton addButton = createButton("Add Item");
        addButton.addActionListener(e -> addItemToCart());

        g = new GridBagConstraints();
        g.insets = new Insets(7, 8, 7, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.gridy = 0;
        itemPanel.add(new JLabel("Category:"), g);

        g.gridx = 1;
        itemPanel.add(categoryBox, g);

        g.gridx = 2;
        itemPanel.add(new JLabel("Item:"), g);

        g.gridx = 3;
        itemPanel.add(itemBox, g);

        g.gridx = 4;
        itemPanel.add(new JLabel("Quantity:"), g);

        g.gridx = 5;
        itemPanel.add(quantitySpinner, g);

        g.gridx = 6;
        itemPanel.add(addButton, g);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);
        top.add(customerPanel, BorderLayout.NORTH);
        top.add(itemPanel, BorderLayout.CENTER);

        main.add(top, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(
                new Object[]{
                        "Item ID",
                        "Category",
                        "Item",
                        "Price",
                        "Quantity",
                        "Total"
                }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(28);
        cartTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        cartTable.getColumnModel().getColumn(0).setMinWidth(0);
        cartTable.getColumnModel().getColumn(0).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(
                BorderFactory.createTitledBorder("Current Order")
        );

        main.add(cartScroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(15, 10));
        bottom.setOpaque(false);

        JPanel totals = new JPanel(new GridLayout(4, 2, 8, 8));
        totals.setBorder(
                BorderFactory.createTitledBorder("Bill Calculation")
        );
        totals.setBackground(Color.WHITE);

        subtotalLabel = new JLabel("₹0.00");
        discountLabel = new JLabel("₹0.00");
        taxLabel = new JLabel("₹0.00");
        totalLabel = new JLabel("₹0.00");

        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        totals.add(new JLabel("Subtotal:"));
        totals.add(subtotalLabel);
        totals.add(new JLabel("Discount:"));
        totals.add(discountLabel);
        totals.add(new JLabel("Tax:"));
        totals.add(taxLabel);
        totals.add(new JLabel("Grand Total:"));
        totals.add(totalLabel);

        bottom.add(totals, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton removeButton = createButton("Remove Item");
        removeButton.addActionListener(e -> removeSelectedItem());

        JButton clearButton = createButton("Clear Order");
        clearButton.addActionListener(e -> clearOrderForm());

        JButton saveButton = createButton("Save Order");
        saveButton.addActionListener(e -> saveOrder());

        JButton pdfButton = createButton("Generate PDF Bill");
        pdfButton.addActionListener(e -> generatePDFForCurrentOrder());

        statusBox = new JComboBox<>(
                new String[]{
                        "Pending",
                        "Preparing",
                        "Ready",
                        "Completed",
                        "Cancelled"
                }
        );

        actions.add(new JLabel("Status:"));
        actions.add(statusBox);
        actions.add(removeButton);
        actions.add(clearButton);
        actions.add(saveButton);
        actions.add(pdfButton);

        bottom.add(actions, BorderLayout.SOUTH);

        main.add(bottom, BorderLayout.SOUTH);

        return main;
    }

    JPanel createOrdersPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setBackground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        searchOrderField = new JTextField(22);

        searchStatusBox = new JComboBox<>(
                new String[]{
                        "All",
                        "Pending",
                        "Preparing",
                        "Ready",
                        "Completed",
                        "Cancelled"
                }
        );

        JButton searchButton = createButton("Search");
        searchButton.addActionListener(e -> refreshOrderTable());

        JButton resetButton = createButton("Reset");
        resetButton.addActionListener(e -> {
            searchOrderField.setText("");
            searchStatusBox.setSelectedItem("All");
            refreshOrderTable();
        });

        JButton modifyButton = createButton("Modify Selected");
        modifyButton.addActionListener(e -> modifySelectedOrder());

        JButton statusButton = createButton("Update Status");
        statusButton.addActionListener(e -> updateSelectedStatus());

        JButton pdfButton = createButton("Generate PDF");
        pdfButton.addActionListener(e -> generateSelectedPDF());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchOrderField);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(searchStatusBox);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        searchPanel.add(modifyButton);
        searchPanel.add(statusButton);
        searchPanel.add(pdfButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        orderModel = new DefaultTableModel(
                new Object[]{
                        "Order ID",
                        "Date",
                        "Customer",
                        "Items",
                        "Subtotal",
                        "Discount",
                        "Tax",
                        "Grand Total",
                        "Status"
                }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(28);
        orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        return panel;
    }

    JPanel createMenuPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setBackground(Color.WHITE);

        menuModel = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Category",
                        "Item",
                        "Price"
                }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        menuTable = new JTable(menuModel);
        menuTable.setRowHeight(28);
        menuTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        panel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setBackground(Color.WHITE);

        JButton add = createButton("Add Menu Item");
        JButton edit = createButton("Edit Selected");
        JButton delete = createButton("Delete Selected");
        JButton save = createButton("Save Menu");

        add.addActionListener(e -> addMenuItem());
        edit.addActionListener(e -> editMenuItem());
        delete.addActionListener(e -> deleteMenuItem());
        save.addActionListener(e -> {
            saveMenu();
            showMessage("Menu saved successfully.");
        });

        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);
        buttons.add(save);

        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    JPanel createSummaryPanel() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setBackground(Color.WHITE);

        JPanel cards = new JPanel(new GridLayout(2, 4, 12, 12));
        cards.setBackground(Color.WHITE);

        summaryOrdersLabel = createSummaryValue();
        summaryRevenueLabel = createSummaryValue();
        summarySubtotalLabel = createSummaryValue();
        summaryDiscountLabel = createSummaryValue();
        summaryTaxLabel = createSummaryValue();
        summaryItemsLabel = createSummaryValue();
        summaryAverageLabel = createSummaryValue();

        cards.add(createSummaryCard("Orders", summaryOrdersLabel));
        cards.add(createSummaryCard("Revenue", summaryRevenueLabel));
        cards.add(createSummaryCard("Subtotal", summarySubtotalLabel));
        cards.add(createSummaryCard("Discount", summaryDiscountLabel));
        cards.add(createSummaryCard("Tax", summaryTaxLabel));
        cards.add(createSummaryCard("Items Sold", summaryItemsLabel));
        cards.add(createSummaryCard("Average Order", summaryAverageLabel));

        JButton refresh = createButton("Refresh Summary");
        refresh.addActionListener(e -> refreshSummary());

        JButton export = createButton("Export Summary");
        export.addActionListener(e -> exportSummary());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(cards, BorderLayout.CENTER);

        JPanel summaryButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryButtons.setOpaque(false);
        summaryButtons.add(refresh);
        summaryButtons.add(export);

        top.add(summaryButtons, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);

        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        summaryArea.setMargin(new Insets(12, 12, 12, 12));

        panel.add(
                new JScrollPane(summaryArea),
                BorderLayout.CENTER
        );

        return panel;
    }

    JLabel createSummaryValue() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 21));
        return label;
    }

    JPanel createSummaryCard(String title, JLabel value) {

        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(249, 246, 250));
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 210, 225)
                        ),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 13));

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        return button;
    }

    void populateCategories() {

        categoryBox.removeAllItems();

        List<String> categoryOrder = Arrays.asList(
                "Starters",
                "Main Course",
                "Rice & Biryani",
                "Breads",
                "Beverages",
                "Desserts"
        );

        categoryOrder.stream()
                .filter(category -> menuItems.stream().anyMatch(m -> m.category.equals(category)))
                .forEach(categoryBox::addItem);

        menuItems.stream()
                .map(m -> m.category)
                .filter(category -> !categoryOrder.contains(category))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEach(categoryBox::addItem);

        if (categoryBox.getItemCount() > 0) {
            categoryBox.setSelectedIndex(0);
            populateItems();
        }
    }

    void populateItems() {

        itemBox.removeAllItems();

        String category = (String) categoryBox.getSelectedItem();

        if (category == null) {
            return;
        }

        menuItems.stream()
                .filter(m -> m.category.equals(category))
                .sorted(Comparator.comparingInt((MenuItemData m) -> menuItemNumber(m.id))
                        .thenComparing(m -> m.name, String.CASE_INSENSITIVE_ORDER))
                .forEach(m -> itemBox.addItem(m.name));
    }

    void addItemToCart() {

        try {

            String category = (String) categoryBox.getSelectedItem();
            String itemName = (String) itemBox.getSelectedItem();

            if (category == null || itemName == null) {
                throw new ValidationException("Please select an item.");
            }

            int quantity = (Integer) quantitySpinner.getValue();

            if (quantity <= 0) {
                throw new ValidationException(
                        "Quantity must be greater than zero."
                );
            }

            MenuItemData item = menuItems.stream()
                    .filter(m ->
                            m.category.equals(category)
                                    && m.name.equals(itemName)
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new ValidationException(
                                    "Selected menu item was not found."
                            )
                    );

            int existingRow = findCartRow(item.id);

            if (existingRow >= 0) {

                int oldQuantity =
                        Integer.parseInt(
                                cartModel.getValueAt(
                                        existingRow, 4
                                ).toString()
                        );

                int newQuantity = oldQuantity + quantity;

                cartModel.setValueAt(
                        newQuantity,
                        existingRow,
                        4
                );

                BigDecimal total =
                        item.price.multiply(
                                BigDecimal.valueOf(newQuantity)
                        );

                cartModel.setValueAt(
                        money(total),
                        existingRow,
                        5
                );

            } else {

                BigDecimal total =
                        item.price.multiply(
                                BigDecimal.valueOf(quantity)
                        );

                cartModel.addRow(
                        new Object[]{
                                item.id,
                                item.category,
                                item.name,
                                money(item.price),
                                quantity,
                                money(total)
                        }
                );
            }

            updateTotals();

        } catch (ValidationException e) {
            showError(e.getMessage());
        }
    }

    int findCartRow(String itemId) {

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (cartModel.getValueAt(i, 0)
                    .toString()
                    .equals(itemId)) {
                return i;
            }
        }

        return -1;
    }

    void removeSelectedItem() {

        int row = cartTable.getSelectedRow();

        if (row < 0) {
            showError("Select an item to remove.");
            return;
        }

        cartModel.removeRow(row);
        updateTotals();
    }

    void updateTotals() {

        BigDecimal subtotal = calculateCartSubtotal();

        BigDecimal discountPercent =
                parsePercentage(discountField.getText());

        BigDecimal taxPercent =
                parsePercentage(taxField.getText());

        BigDecimal discount =
                subtotal
                        .multiply(discountPercent)
                        .divide(
                                new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal taxable =
                subtotal.subtract(discount);

        BigDecimal tax =
                taxable
                        .multiply(taxPercent)
                        .divide(
                                new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal total =
                taxable.add(tax);

        subtotalLabel.setText("₹" + money(subtotal));
        discountLabel.setText("₹" + money(discount));
        taxLabel.setText("₹" + money(tax));
        totalLabel.setText("₹" + money(total));
    }

    BigDecimal calculateCartSubtotal() {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < cartModel.getRowCount(); i++) {

            BigDecimal total =
                    new BigDecimal(
                            cartModel.getValueAt(i, 5)
                                    .toString()
                    );

            subtotal = subtotal.add(total);
        }

        return subtotal;
    }

    BigDecimal parsePercentage(String text) {

        try {

            BigDecimal value =
                    new BigDecimal(text.trim());

            if (value.compareTo(BigDecimal.ZERO) < 0
                    || value.compareTo(new BigDecimal("100")) > 0) {
                return BigDecimal.ZERO;
            }

            return value;

        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    void saveOrder() {

        try {

            String customer =
                    customerField.getText().trim();

            if (customer.isEmpty()) {
                throw new ValidationException(
                        "Customer name is required."
                );
            }

            if (cartModel.getRowCount() == 0) {
                throw new ValidationException(
                        "Add at least one item to the order."
                );
            }

            BigDecimal discountPercent =
                    new BigDecimal(
                            discountField.getText().trim()
                    );

            BigDecimal taxPercent =
                    new BigDecimal(
                            taxField.getText().trim()
                    );

            validatePercentage(discountPercent);
            validatePercentage(taxPercent);

            String status =
                    statusBox.getSelectedItem().toString();

            Order savedOrder;

            if (editingOrder != null) {

                if (editingOrder.status.equals("Completed")
                        || editingOrder.status.equals("Cancelled")) {

                    throw new ValidationException(
                            "Completed or cancelled orders cannot be modified."
                    );
                }

                editingOrder.customerName = customer;
                editingOrder.discountPercent = discountPercent;
                editingOrder.taxPercent = taxPercent;
                editingOrder.status = status;
                editingOrder.items = readCartItems();

                editingOrder.recalculate();
                savedOrder = editingOrder;

                showMessage(
                        "Order " + editingOrder.orderId
                                + " modified successfully."
                );

            } else {

                Order order =
                        new Order(
                                "ORD-" + nextOrderNumber++,
                                customer,
                                LocalDate.now(),
                                readCartItems(),
                                discountPercent,
                                taxPercent,
                                status
                        );

                order.recalculate();
                orders.add(order);
                savedOrder = order;

                showMessage(
                        "Order " + order.orderId
                                + " saved successfully."
                );
            }

            editingOrder = savedOrder;

            refreshAll();

            showMessage(
                    "Order " + savedOrder.orderId +
                            " is ready. You can now generate the PDF bill."
            );

        } catch (NumberFormatException e) {

            showError(
                    "Discount and tax must contain valid numbers."
            );

        } catch (ValidationException e) {
            showError(e.getMessage());
        }
    }

    void validatePercentage(BigDecimal value)
            throws ValidationException {

        if (value.compareTo(BigDecimal.ZERO) < 0
                || value.compareTo(new BigDecimal("100")) > 0) {

            throw new ValidationException(
                    "Percentage must be between 0 and 100."
            );
        }
    }

    List<OrderItem> readCartItems() {

        List<OrderItem> items = new ArrayList<>();

        for (int i = 0; i < cartModel.getRowCount(); i++) {

            String itemId =
                    cartModel.getValueAt(i, 0).toString();

            String category =
                    cartModel.getValueAt(i, 1).toString();

            String name =
                    cartModel.getValueAt(i, 2).toString();

            BigDecimal price =
                    new BigDecimal(
                            cartModel.getValueAt(i, 3).toString()
                    );

            int quantity =
                    Integer.parseInt(
                            cartModel.getValueAt(i, 4).toString()
                    );

            items.add(
                    new OrderItem(
                            itemId,
                            category,
                            name,
                            price,
                            quantity
                    )
            );
        }

        return items;
    }

    void clearOrderForm() {

        customerField.setText("");
        discountField.setText("0");
        taxField.setText(DEFAULT_TAX.toPlainString());
        statusBox.setSelectedItem("Pending");

        cartModel.setRowCount(0);

        subtotalLabel.setText("₹0.00");
        discountLabel.setText("₹0.00");
        taxLabel.setText("₹0.00");
        totalLabel.setText("₹0.00");

        editingOrder = null;
    }

    void refreshOrderTable() {

        orderModel.setRowCount(0);

        String search =
                searchOrderField == null
                        ? ""
                        : searchOrderField.getText()
                        .trim()
                        .toLowerCase();

        String selectedStatus =
                searchStatusBox == null
                        ? "All"
                        : searchStatusBox
                        .getSelectedItem()
                        .toString();

        orders.stream()
                .filter(o -> {

                    if (selectedStatus.equals("All")) {
                        return true;
                    }

                    return o.status.equals(selectedStatus);
                })
                .filter(o -> {

                    if (search.isEmpty()) {
                        return true;
                    }

                    boolean orderMatch =
                            o.orderId.toLowerCase()
                                    .contains(search)
                                    || o.customerName
                                    .toLowerCase()
                                    .contains(search);

                    boolean itemMatch =
                            o.items.stream()
                                    .anyMatch(item ->
                                            item.name
                                                    .toLowerCase()
                                                    .contains(search)
                                    );

                    return orderMatch || itemMatch;
                })
                .forEach(o ->
                        orderModel.addRow(
                                new Object[]{
                                        o.orderId,
                                        o.date.format(DATE_FORMAT),
                                        o.customerName,
                                        o.items.stream()
                                                .map(i -> i.name + " x" + i.quantity)
                                                .collect(Collectors.joining(", ")),
                                        money(o.subtotal),
                                        money(o.discountAmount),
                                        money(o.taxAmount),
                                        money(o.grandTotal),
                                        o.status
                                }
                        )
                );
    }

    void modifySelectedOrder() {

        int row = orderTable.getSelectedRow();

        if (row < 0) {
            showError("Select an order to modify.");
            return;
        }

        String orderId =
                orderModel.getValueAt(row, 0).toString();

        Order order =
                orders.stream()
                        .filter(o -> o.orderId.equals(orderId))
                        .findFirst()
                        .orElse(null);

        if (order == null) {
            showError("Order not found.");
            return;
        }

        if (order.status.equals("Completed")
                || order.status.equals("Cancelled")) {

            showError(
                    "Completed or cancelled orders cannot be modified."
            );

            return;
        }

        editingOrder = order;

        customerField.setText(order.customerName);
        discountField.setText(
                order.discountPercent.toPlainString()
        );
        taxField.setText(
                order.taxPercent.toPlainString()
        );

        statusBox.setSelectedItem(order.status);

        cartModel.setRowCount(0);

        for (OrderItem item : order.items) {

            cartModel.addRow(
                    new Object[]{
                            item.itemId,
                            item.category,
                            item.name,
                            money(item.price),
                            item.quantity,
                            money(item.lineTotal())
                    }
            );
        }

        updateTotals();

        tabs.setSelectedIndex(0);
    }

    void updateSelectedStatus() {

        int row = orderTable.getSelectedRow();

        if (row < 0) {
            showError("Select an order.");
            return;
        }

        String orderId =
                orderModel.getValueAt(row, 0).toString();

        Order order =
                orders.stream()
                        .filter(o -> o.orderId.equals(orderId))
                        .findFirst()
                        .orElse(null);

        if (order == null) {
            showError("Order not found.");
            return;
        }

        if (order.status.equals("Completed")
                || order.status.equals("Cancelled")) {

            showError(
                    "This order is already closed and cannot change status."
            );

            return;
        }

        String[] options = {
                "Pending",
                "Preparing",
                "Ready",
                "Completed",
                "Cancelled"
        };

        String newStatus =
                (String) JOptionPane.showInputDialog(
                        this,
                        "Select new status:",
                        "Update Order Status",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        order.status
                );

        if (newStatus != null) {

            order.status = newStatus;

            refreshAll();

            showMessage(
                    "Status updated for " + order.orderId
            );
        }
    }

    void generatePDFForCurrentOrder() {

        if (editingOrder != null) {

            try {

                editingOrder.items = readCartItems();
                editingOrder.customerName =
                        customerField.getText().trim();

                editingOrder.discountPercent =
                        new BigDecimal(
                                discountField.getText().trim()
                        );

                editingOrder.taxPercent =
                        new BigDecimal(
                                taxField.getText().trim()
                        );

                editingOrder.status =
                        statusBox.getSelectedItem().toString();

                editingOrder.recalculate();

                generatePDF(editingOrder);

            } catch (Exception e) {
                showError("Unable to generate bill: " + e.getMessage());
            }

            return;
        }

        if (cartModel.getRowCount() == 0) {
            showError(
                    "Add items to the order before generating a PDF."
            );
            return;
        }

        try {

            String customer =
                    customerField.getText().trim();

            if (customer.isEmpty()) {
                throw new ValidationException(
                        "Customer name is required."
                );
            }

            BigDecimal discount =
                    new BigDecimal(
                            discountField.getText().trim()
                    );

            BigDecimal tax =
                    new BigDecimal(
                            taxField.getText().trim()
                    );

            validatePercentage(discount);
            validatePercentage(tax);

            Order temporary =
                    new Order(
                            "DRAFT-" + nextOrderNumber,
                            customer,
                            LocalDate.now(),
                            readCartItems(),
                            discount,
                            tax,
                            statusBox.getSelectedItem().toString()
                    );

            temporary.recalculate();

            generatePDF(temporary);

        } catch (Exception e) {
            showError("Unable to generate bill: " + e.getMessage());
        }
    }

    void generateSelectedPDF() {

        int row = orderTable.getSelectedRow();

        if (row < 0) {
            showError("Select an order first.");
            return;
        }

        String orderId =
                orderModel.getValueAt(row, 0).toString();

        Order order =
                orders.stream()
                        .filter(o -> o.orderId.equals(orderId))
                        .findFirst()
                        .orElse(null);

        if (order == null) {
            showError("Order not found.");
            return;
        }

        generatePDF(order);
    }

    void generatePDF(Order order) {

        try {

            Files.createDirectories(BILL_DIR);

            Path file =
                    BILL_DIR.resolve(
                            order.orderId + "_" +
                                    safeFileName(order.customerName) +
                                    ".pdf"
                    );

            SimplePDF pdf = new SimplePDF();

            pdf.addText("BILL KAUN BHAREGA?", 36, 780, 20, true);
            pdf.addText(
                    "Pehle Khaao, Phir Socho.",
                    36,
                    756,
                    11,
                    false
            );

            pdf.addText(
                    "Order ID: " + order.orderId,
                    36,
                    725,
                    11,
                    false
            );

            pdf.addText(
                    "Date: " +
                            order.date.format(DATE_FORMAT),
                    36,
                    708,
                    11,
                    false
            );

            pdf.addText(
                    "Customer: " +
                            order.customerName,
                    36,
                    691,
                    11,
                    true
            );

            pdf.addText(
                    "Status: " +
                            order.status,
                    36,
                    674,
                    11,
                    false
            );

            int y = 640;

            pdf.addText("ITEM", 36, y, 10, true);
            pdf.addText("QTY", 320, y, 10, true);
            pdf.addText("PRICE", 370, y, 10, true);
            pdf.addText("TOTAL", 455, y, 10, true);

            y -= 20;

            for (OrderItem item : order.items) {

                pdf.addText(
                        item.name,
                        36,
                        y,
                        10,
                        false
                );

                pdf.addText(
                        String.valueOf(item.quantity),
                        320,
                        y,
                        10,
                        false
                );

                pdf.addText(
                        "Rs. " + money(item.price),
                        370,
                        y,
                        10,
                        false
                );

                pdf.addText(
                        "Rs. " + money(item.lineTotal()),
                        455,
                        y,
                        10,
                        false
                );

                y -= 18;
            }

            y -= 15;

            pdf.addText(
                    "Subtotal:",
                    350,
                    y,
                    10,
                    false
            );

            pdf.addText(
                    "Rs. " + money(order.subtotal),
                    455,
                    y,
                    10,
                    false
            );

            y -= 18;

            pdf.addText(
                    "Discount (" +
                            order.discountPercent.toPlainString() +
                            "%):",
                    350,
                    y,
                    10,
                    false
            );

            pdf.addText(
                    "Rs. " + money(order.discountAmount),
                    455,
                    y,
                    10,
                    false
            );

            y -= 18;

            pdf.addText(
                    "Tax (" +
                            order.taxPercent.toPlainString() +
                            "%):",
                    350,
                    y,
                    10,
                    false
            );

            pdf.addText(
                    "Rs. " + money(order.taxAmount),
                    455,
                    y,
                    10,
                    false
            );

            y -= 25;

            pdf.addText(
                    "GRAND TOTAL:",
                    350,
                    y,
                    12,
                    true
            );

            pdf.addText(
                    "Rs. " + money(order.grandTotal),
                    455,
                    y,
                    12,
                    true
            );

            y -= 40;

            pdf.addText(
                    "Thank you for dining with us!",
                    36,
                    y,
                    10,
                    false
            );

            pdf.save(file);

            showMessage(
                    "PDF bill generated successfully.\n\n" +
                            "Open it from the bills folder in VS Code:\n\n" +
                            file.toAbsolutePath()
            );

        } catch (Exception e) {
            showError(
                    "PDF generation failed: " +
                            e.getMessage()
            );
        }
    }

    void addMenuItem() {

        JTextField nameField = new JTextField();
        JComboBox<String> categoryBox =
                new JComboBox<>(new String[]{
                        "Starters",
                        "Main Course",
                        "Rice & Biryani",
                        "Breads",
                        "Beverages",
                        "Desserts"
                });
        JTextField priceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));

        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryBox);

        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Menu Item",
                        JOptionPane.OK_CANCEL_OPTION
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String name = nameField.getText().trim();
            String category = canonicalCategory(categoryBox.getSelectedItem().toString());

            if (name.isEmpty() || category.isEmpty()) {
                throw new ValidationException(
                        "Item name and category are required."
                );
            }

            BigDecimal price =
                    new BigDecimal(
                            priceField.getText().trim()
                    );

            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException(
                        "Price must be greater than zero."
                );
            }

            menuItems.add(
                    new MenuItemData(
                            generateMenuId(category),
                            category,
                            name,
                            price
                    )
            );

            saveMenu();
            refreshAll();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    void editMenuItem() {

        int row = menuTable.getSelectedRow();

        if (row < 0) {
            showError("Select a menu item.");
            return;
        }

        String id =
                menuModel.getValueAt(row, 0).toString();

        MenuItemData item =
                menuItems.stream()
                        .filter(m -> m.id.equals(id))
                        .findFirst()
                        .orElse(null);

        if (item == null) {
            showError("Menu item not found.");
            return;
        }

        JTextField nameField =
                new JTextField(item.name);

        JTextField categoryField =
                new JTextField(item.category);

        JTextField priceField =
                new JTextField(item.price.toPlainString());

        JPanel panel =
                new JPanel(new GridLayout(3, 2, 8, 8));

        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Edit Menu Item",
                        JOptionPane.OK_CANCEL_OPTION
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String name =
                    nameField.getText().trim();

            String category =
                    canonicalCategory(categoryField.getText().trim());

            BigDecimal price =
                    new BigDecimal(
                            priceField.getText().trim()
                    );

            if (name.isEmpty()
                    || category.isEmpty()
                    || price.compareTo(BigDecimal.ZERO) <= 0) {

                throw new ValidationException(
                        "Enter valid item name, category and price."
                );
            }

            item.name = name;
            item.category = category;
            item.price = price;

            saveMenu();
            refreshAll();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    void deleteMenuItem() {

        int row = menuTable.getSelectedRow();

        if (row < 0) {
            showError("Select a menu item.");
            return;
        }

        String id =
                menuModel.getValueAt(row, 0).toString();

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete this menu item?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm == JOptionPane.YES_OPTION) {

            menuItems.removeIf(
                    item -> item.id.equals(id)
            );

            saveMenu();
            refreshAll();
        }
    }

    static int menuItemNumber(String id) {
        if (id == null) {
            return Integer.MAX_VALUE;
        }

        try {
            int start = id.indexOf("ITEM-") + 5;
            int end = id.indexOf('-', start);
            if (start >= 5 && end > start) {
                return Integer.parseInt(id.substring(start, end));
            }
        } catch (Exception ignored) {
        }

        return Integer.MAX_VALUE;
    }

    void refreshMenuTable() {

        menuModel.setRowCount(0);

        List<String> categoryOrder = Arrays.asList(
                "Starters",
                "Main Course",
                "Rice & Biryani",
                "Breads",
                "Beverages",
                "Desserts"
        );

        Map<String, Integer> categoryRanks = new HashMap<>();
        for (int i = 0; i < categoryOrder.size(); i++) {
            categoryRanks.put(categoryOrder.get(i), i);
        }

        menuItems.stream()
                .sorted(
                        Comparator.comparingInt(
                                (MenuItemData m) ->
                                        categoryRanks.getOrDefault(
                                                canonicalCategory(m.category),
                                                999
                                        )
                        )
                        .thenComparingInt(
                                m -> menuItemNumber(m.id)
                        )
                        .thenComparing(
                                m -> m.name,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .forEach(m ->
                        menuModel.addRow(
                                new Object[]{
                                        m.id,
                                        canonicalCategory(m.category),
                                        m.name,
                                        money(m.price)
                                }
                        )
                );
    }

    void refreshSummary() {

        List<Order> validOrders =
                orders.stream()
                        .filter(o ->
                                !o.status.equals("Cancelled"))
                        .collect(Collectors.toList());

        BigDecimal revenue =
                validOrders.stream()
                        .map(o -> o.grandTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal subtotal =
                validOrders.stream()
                        .map(o -> o.subtotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal discounts =
                validOrders.stream()
                        .map(o -> o.discountAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal taxes =
                validOrders.stream()
                        .map(o -> o.taxAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        int itemsSold =
                validOrders.stream()
                        .flatMap(o -> o.items.stream())
                        .mapToInt(i -> i.quantity)
                        .sum();

        BigDecimal average =
                validOrders.isEmpty()
                        ? BigDecimal.ZERO
                        : revenue.divide(
                        BigDecimal.valueOf(
                                validOrders.size()
                        ),
                        2,
                        RoundingMode.HALF_UP
                );

        summaryOrdersLabel.setText(
                String.valueOf(validOrders.size())
        );

        summaryRevenueLabel.setText(
                "₹" + money(revenue)
        );

        summarySubtotalLabel.setText(
                "₹" + money(subtotal)
        );

        summaryDiscountLabel.setText(
                "₹" + money(discounts)
        );

        summaryTaxLabel.setText(
                "₹" + money(taxes)
        );

        summaryItemsLabel.setText(
                String.valueOf(itemsSold)
        );

        summaryAverageLabel.setText(
                "₹" + money(average)
        );

        buildDetailedSummary(validOrders);
    }

    void buildDetailedSummary(List<Order> validOrders) {

        StringBuilder text =
                new StringBuilder();

        text.append(
                "SALES SUMMARY — CURRENT SESSION\n"
        );

        text.append(
                "Date: "
                        + LocalDate.now().format(DATE_FORMAT)
                        + "\n"
        );

        text.append(
                "============================================================\n\n"
        );

        if (validOrders.isEmpty()) {

            text.append(
                    "No completed sales data is available in this session.\n"
            );

            summaryArea.setText(text.toString());
            return;
        }

        Map<String, Integer> itemQuantities =
                validOrders.stream()
                        .flatMap(o -> o.items.stream())
                        .collect(
                                Collectors.groupingBy(
                                        i -> i.name,
                                        Collectors.summingInt(
                                                i -> i.quantity
                                        )
                                )
                        );

        Map<String, BigDecimal> itemRevenue =
                validOrders.stream()
                        .flatMap(o -> o.items.stream())
                        .collect(
                                Collectors.groupingBy(
                                        i -> i.name,
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                OrderItem::lineTotal,
                                                BigDecimal::add
                                        )
                                )
                        );

        Map<String, Integer> categoryQuantities =
                validOrders.stream()
                        .flatMap(o -> o.items.stream())
                        .collect(
                                Collectors.groupingBy(
                                        i -> i.category,
                                        Collectors.summingInt(
                                                i -> i.quantity
                                        )
                                )
                        );

        Map<String, BigDecimal> categoryRevenue =
                validOrders.stream()
                        .flatMap(o -> o.items.stream())
                        .collect(
                                Collectors.groupingBy(
                                        i -> i.category,
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                OrderItem::lineTotal,
                                                BigDecimal::add
                                        )
                                )
                        );

        Optional<Map.Entry<String, Integer>> topItem =
                itemQuantities.entrySet()
                        .stream()
                        .max(
                                Map.Entry.comparingByValue()
                        );

        Optional<Map.Entry<String, BigDecimal>> topRevenueItem =
                itemRevenue.entrySet()
                        .stream()
                        .max(
                                Map.Entry.comparingByValue()
                        );

        text.append("TOP-SELLING ITEM\n");
        text.append("------------------------------------------------------------\n");

        if (topItem.isPresent()) {
            text.append(
                    topItem.get().getKey()
                            + " — "
                            + topItem.get().getValue()
                            + " units\n"
            );
        }

        text.append("\nTOP ITEM BY SALES VALUE\n");
        text.append("------------------------------------------------------------\n");

        if (topRevenueItem.isPresent()) {
            text.append(
                    topRevenueItem.get().getKey()
                            + " — ₹"
                            + money(topRevenueItem.get().getValue())
                            + "\n"
            );
        }

        text.append("\nITEM-WISE SALES\n");
        text.append("------------------------------------------------------------\n");

        itemQuantities.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Integer>
                                comparingByValue()
                                .reversed()
                )
                .forEach(entry -> {

                    String item =
                            entry.getKey();

                    BigDecimal sales =
                            itemRevenue.get(item);

                    text.append(
                            String.format(
                                    "%-30s %5d units   ₹%10s%n",
                                    item,
                                    entry.getValue(),
                                    money(sales)
                            )
                    );
                });

        text.append("\nCATEGORY-WISE SALES\n");
        text.append("------------------------------------------------------------\n");

        categoryQuantities.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Integer>
                                comparingByValue()
                                .reversed()
                )
                .forEach(entry -> {

                    String category =
                            entry.getKey();

                    text.append(
                            String.format(
                                    "%-30s %5d units   ₹%10s%n",
                                    category,
                                    entry.getValue(),
                                    money(
                                            categoryRevenue
                                                    .get(category)
                                    )
                            )
                    );
                });

        text.append("\nORDER STATUS BREAKDOWN\n");
        text.append("------------------------------------------------------------\n");

        Map<String, Long> statusCounts =
                orders.stream()
                        .collect(
                                Collectors.groupingBy(
                                        o -> o.status,
                                        Collectors.counting()
                                )
                        );

        statusCounts.forEach(
                (status, count) ->
                        text.append(
                                String.format(
                                        "%-20s %d%n",
                                        status,
                                        count
                                )
                        )
        );

        text.append("\nDAILY PERFORMANCE\n");
        text.append("------------------------------------------------------------\n");

        Map<LocalDate, BigDecimal> dailyRevenue =
                validOrders.stream()
                        .collect(
                                Collectors.groupingBy(
                                        o -> o.date,
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                o -> o.grandTotal,
                                                BigDecimal::add
                                        )
                                )
                        );

        dailyRevenue.entrySet()
                .stream()
                .sorted(
                        Map.Entry.comparingByKey()
                )
                .forEach(entry ->
                        text.append(
                                entry.getKey()
                                        .format(DATE_FORMAT)
                                        + " — ₹"
                                        + money(entry.getValue())
                                        + "\n"
                        )
                );

        text.append(
                "\n============================================================\n"
        );

        summaryArea.setText(text.toString());
    }

    void exportSummary() {

        try {

            Files.createDirectories(DATA_DIR);

            Path file =
                    DATA_DIR.resolve(
                            "sales_summary_" +
                                    LocalDate.now() +
                                    ".txt"
                    );

            Files.writeString(
                    file,
                    summaryArea.getText(),
                    StandardCharsets.UTF_8
            );

            showMessage(
                    "Sales summary exported to:\n"
                            + file.toAbsolutePath()
            );

        } catch (IOException e) {
            showError(
                    "Unable to export sales summary."
            );
        }
    }

    void refreshAll() {

        if (menuModel != null) {
            refreshMenuTable();
        }

        if (categoryBox != null) {
            populateCategories();
        }

        if (orderModel != null) {
            refreshOrderTable();
        }

        if (summaryArea != null) {
            refreshSummary();
        }

        updateTotals();
    }

    void migrateToExpandedMenu() {

        List<MenuItemData> existing = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (MenuItemData item : menuItems) {
            item.category = canonicalCategory(item.category);
            String key = item.category.toLowerCase() + "|" + item.name.trim().toLowerCase();
            if (!seen.contains(key)) {
                item.name = item.name.trim();
                existing.add(item);
                seen.add(key);
            }
        }

        menuItems.clear();
        menuItems.addAll(existing);

        addMissingDefault("Starters", "Manchow Soup", "270");
        addMissingDefault("Starters", "Paneer Tikka", "220");
        addMissingDefault("Starters", "Medu Vada", "80");
        addMissingDefault("Starters", "Chilli Paneer", "230");
        addMissingDefault("Starters", "Chicken Tikka", "240");
        addMissingDefault("Starters", "Veg Spring Roll", "140");
        addMissingDefault("Starters", "Hara Bhara Kebab", "180");
        addMissingDefault("Starters", "Aloo Tikki", "110");
        addMissingDefault("Starters", "Crispy Corn", "170");
        addMissingDefault("Starters", "Veg Seekh Kebab", "190");

        addMissingDefault("Main Course", "Butter Chicken", "280");
        addMissingDefault("Main Course", "Chicken Tikka Masala", "290");
        addMissingDefault("Main Course", "Shahi Paneer", "270");
        addMissingDefault("Main Course", "Dal Makhani", "170");
        addMissingDefault("Main Course", "Chole Masala", "150");
        addMissingDefault("Main Course", "Rajma Masala", "150");
        addMissingDefault("Main Course", "Paneer Butter Masala", "260");
        addMissingDefault("Main Course", "Kadai Paneer", "280");
        addMissingDefault("Main Course", "Veg Kolhapuri", "240");
        addMissingDefault("Main Course", "Malai Kofta", "260");

        addMissingDefault("Rice & Biryani", "Chicken Biryani", "260");
        addMissingDefault("Rice & Biryani", "Veg Fried Rice", "180");
        addMissingDefault("Rice & Biryani", "Veg Biryani", "200");
        addMissingDefault("Rice & Biryani", "Jeera Rice", "130");
        addMissingDefault("Rice & Biryani", "Veg Pulao", "150");
        addMissingDefault("Rice & Biryani", "Dal Rice", "140");
        addMissingDefault("Rice & Biryani", "Paneer Fried Rice", "210");
        addMissingDefault("Rice & Biryani", "Schezwan Fried Rice", "200");
        addMissingDefault("Rice & Biryani", "Veg Hakka Noodles", "190");
        addMissingDefault("Rice & Biryani", "Paneer Biryani", "230");

        addMissingDefault("Breads", "Naan", "50");
        addMissingDefault("Breads", "Butter Naan", "65");
        addMissingDefault("Breads", "Roti", "25");
        addMissingDefault("Breads", "Tandoori Roti", "35");
        addMissingDefault("Breads", "Paratha", "70");
        addMissingDefault("Breads", "Garlic Naan", "80");
        addMissingDefault("Breads", "Cheese Naan", "120");
        addMissingDefault("Breads", "Missi Roti", "60");
        addMissingDefault("Breads", "Laccha Paratha", "85");
        addMissingDefault("Breads", "Stuffed Aloo Paratha", "110");

        addMissingDefault("Beverages", "Masala Chai", "40");
        addMissingDefault("Beverages", "Mango Lassi", "90");
        addMissingDefault("Beverages", "Sweet Lassi", "80");
        addMissingDefault("Beverages", "Nimbu Pani", "50");
        addMissingDefault("Beverages", "Milkshake", "120");
        addMissingDefault("Beverages", "Fruit Juice", "100");
        addMissingDefault("Beverages", "Cold Coffee", "110");
        addMissingDefault("Beverages", "Fresh Lime Soda", "70");
        addMissingDefault("Beverages", "Rose Milk", "90");
        addMissingDefault("Beverages", "Masala Buttermilk", "60");

        addMissingDefault("Desserts", "Gulab Jamun", "110");
        addMissingDefault("Desserts", "Rasmalai", "90");
        addMissingDefault("Desserts", "Jalebi", "80");
        addMissingDefault("Desserts", "Kulfi", "100");
        addMissingDefault("Desserts", "Ice Cream", "90");
        addMissingDefault("Desserts", "Pastry", "100");
        addMissingDefault("Desserts", "Gajar Halwa", "120");
        addMissingDefault("Desserts", "Kheer", "100");
        addMissingDefault("Desserts", "Brownie", "140");
        addMissingDefault("Desserts", "Fruit Custard", "110");
    }

    static String canonicalCategory(String category) {
        if (category == null) {
            return "Starters";
        }

        String value = category.trim();

        if (value.equalsIgnoreCase("Starter") ||
                value.equalsIgnoreCase("Starters") ||
                value.equalsIgnoreCase("Starters / Appetizers")) {
            return "Starters";
        }
        if (value.equalsIgnoreCase("Main Course")) {
            return "Main Course";
        }
        if (value.equalsIgnoreCase("Rice & Biryani")) {
            return "Rice & Biryani";
        }
        if (value.equalsIgnoreCase("Breads")) {
            return "Breads";
        }
        if (value.equalsIgnoreCase("Beverages")) {
            return "Beverages";
        }
        if (value.equalsIgnoreCase("Dessert") || value.equalsIgnoreCase("Desserts")) {
            return "Desserts";
        }

        return value;
    }

    void addMissingDefault(String category, String name, String price) {
        boolean exists = menuItems.stream().anyMatch(item ->
                item.category.equalsIgnoreCase(category) &&
                item.name.equalsIgnoreCase(name)
        );

        if (!exists) {
            menuItems.add(
                    new MenuItemData(
                            "",
                            category,
                            name,
                            new BigDecimal(price)
                    )
            );
        }
    }

    static void addMigrationItem(List<MenuItemData> target, String category, String name, String price) {
        target.add(new MenuItemData("", category, name, new BigDecimal(price)));
    }

    void loadMenu() {

        if (!Files.exists(MENU_FILE)) {
            return;
        }

        try {

            List<String> lines =
                    Files.readAllLines(
                            MENU_FILE,
                            StandardCharsets.UTF_8
                    );

            menuItems.clear();

            for (String line : lines) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts =
                        line.split("\\|", -1);

                if (parts.length != 4) {
                    continue;
                }

                String category = canonicalCategory(parts[1]);

                menuItems.add(
                        new MenuItemData(
                                parts[0],
                                category,
                                parts[2],
                                new BigDecimal(parts[3])
                        )
                );
            }

            migrateToExpandedMenu();

            if (normalizeMenuIds()) {
                saveMenu();
            } else {
                saveMenu();
            }

        } catch (Exception e) {

            menuItems.clear();

            showErrorStatic(
                    "Menu file could not be loaded. Default menu will be restored."
            );
        }
    }

    void saveMenu() {

        try {

            normalizeMenuIds();

            Files.createDirectories(DATA_DIR);

            List<String> lines =
                    menuItems.stream()
                            .map(m ->
                                    m.id + "|"
                                            + m.category + "|"
                                            + m.name + "|"
                                            + m.price.toPlainString()
                            )
                            .collect(Collectors.toList());

            Files.write(
                    MENU_FILE,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {

            showError(
                    "Unable to save menu data."
            );
        }
    }

    static String money(BigDecimal value) {

        return value
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .toPlainString();
    }

    static String safeFileName(String text) {

        return text
                .replaceAll(
                        "[^a-zA-Z0-9._-]",
                        "_"
                );
    }

    void showMessage(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                APP_NAME,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                APP_NAME,
                JOptionPane.ERROR_MESSAGE
        );
    }

    static void showErrorStatic(String message) {

        JOptionPane.showMessageDialog(
                null,
                message,
                APP_NAME,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            new BillKaunBharega().setVisible(true);
        });
    }

    static class MenuItemData {

        String id;
        String category;
        String name;
        BigDecimal price;

        MenuItemData(
                String id,
                String category,
                String name,
                BigDecimal price
        ) {
            this.id = id;
            this.category = category;
            this.name = name;
            this.price = price;
        }
    }

    static class OrderItem {

        String itemId;
        String category;
        String name;
        BigDecimal price;
        int quantity;

        OrderItem(
                String itemId,
                String category,
                String name,
                BigDecimal price,
                int quantity
        ) {
            this.itemId = itemId;
            this.category = category;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        BigDecimal lineTotal() {

            return price.multiply(
                    BigDecimal.valueOf(quantity)
            );
        }
    }

    static class Order {

        String orderId;
        String customerName;
        LocalDate date;
        List<OrderItem> items;
        BigDecimal discountPercent;
        BigDecimal taxPercent;
        BigDecimal subtotal;
        BigDecimal discountAmount;
        BigDecimal taxAmount;
        BigDecimal grandTotal;
        String status;

        Order(
                String orderId,
                String customerName,
                LocalDate date,
                List<OrderItem> items,
                BigDecimal discountPercent,
                BigDecimal taxPercent,
                String status
        ) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.date = date;
            this.items = items;
            this.discountPercent = discountPercent;
            this.taxPercent = taxPercent;
            this.status = status;
        }

        void recalculate() {

            subtotal =
                    items.stream()
                            .map(OrderItem::lineTotal)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            discountAmount =
                    subtotal
                            .multiply(discountPercent)
                            .divide(
                                    new BigDecimal("100"),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            BigDecimal taxableAmount =
                    subtotal.subtract(
                            discountAmount
                    );

            taxAmount =
                    taxableAmount
                            .multiply(taxPercent)
                            .divide(
                                    new BigDecimal("100"),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            grandTotal =
                    taxableAmount.add(
                            taxAmount
                    );
        }
    }

    static class ValidationException
            extends Exception {

        ValidationException(String message) {
            super(message);
        }
    }

    static class SimplePDF {

        StringBuilder content =
                new StringBuilder();

        SimplePDF() {

            content.append(
                    "BT\n"
            );
        }

        void addText(
                String text,
                int x,
                int y,
                int size,
                boolean bold
        ) {

            String font =
                    bold
                            ? "/F2"
                            : "/F1";

            content.append(
                    font
                            + " "
                            + size
                            + " Tf\n"
            );

            content.append(
                    "1 0 0 1 "
                            + x
                            + " "
                            + y
                            + " Tm\n"
            );

            content.append(
                    "("
                            + escape(text)
                            + ") Tj\n"
            );
        }

        String escape(String text) {

            return text
                    .replace("\\", "\\\\")
                    .replace("(", "\\(")
                    .replace(")", "\\)");
        }

        void save(Path path)
                throws IOException {

            content.append("ET\n");

            byte[] stream =
                    content.toString()
                            .getBytes(
                                    StandardCharsets.ISO_8859_1
                            );

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            List<Integer> offsets =
                    new ArrayList<>();

            write(
                    output,
                    "%PDF-1.4\n"
            );

            offsets.add(output.size());

            write(
                    output,
                    "1 0 obj\n"
                            + "<< /Type /Catalog /Pages 2 0 R >>\n"
                            + "endobj\n"
            );

            offsets.add(output.size());

            write(
                    output,
                    "2 0 obj\n"
                            + "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n"
                            + "endobj\n"
            );

            offsets.add(output.size());

            write(
                    output,
                    "3 0 obj\n"
                            + "<< /Type /Page /Parent 2 0 R "
                            + "/MediaBox [0 0 595 842] "
                            + "/Resources << /Font << "
                            + "/F1 4 0 R "
                            + "/F2 5 0 R "
                            + ">> >> "
                            + "/Contents 6 0 R >>\n"
                            + "endobj\n"
            );

            offsets.add(output.size());

            write(
                    output,
                    "4 0 obj\n"
                            + "<< /Type /Font /Subtype /Type1 "
                            + "/BaseFont /Helvetica >>\n"
                            + "endobj\n"
            );

            offsets.add(output.size());

            write(
                    output,
                    "5 0 obj\n"
                            + "<< /Type /Font /Subtype /Type1 "
                            + "/BaseFont /Helvetica-Bold >>\n"
                            + "endobj\n"
            );

            offsets.add(output.size());

            write(
                    output,
                    "6 0 obj\n"
                            + "<< /Length "
                            + stream.length
                            + " >>\n"
                            + "stream\n"
            );

            output.write(stream);

            write(
                    output,
                    "endstream\n"
                            + "endobj\n"
            );

            int xref =
                    output.size();

            write(
                    output,
                    "xref\n"
                            + "0 7\n"
                            + "0000000000 65535 f \n"
            );

            for (Integer offset : offsets) {

                write(
                        output,
                        String.format(
                                "%010d 00000 n \n",
                                offset
                        )
                );
            }

            write(
                    output,
                    "trailer\n"
                            + "<< /Size 7 /Root 1 0 R >>\n"
                            + "startxref\n"
                            + xref
                            + "\n"
                            + "%%EOF\n"
            );

            Files.write(
                    path,
                    output.toByteArray()
            );
        }

        void write(
                ByteArrayOutputStream output,
                String text
        ) throws IOException {

            output.write(
                    text.getBytes(
                            StandardCharsets.ISO_8859_1
                    )
            );
        }
    }
}