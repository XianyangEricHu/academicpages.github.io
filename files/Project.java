/* 
Laboratory Inventory System
Junhyuk Lee and Eric Hu

Things to Note:
1) The fields for AddProductGUI do not clear unless you succesfully add a product
2) A product with a duplicate name cannot be added
3) The minimum stock of a product is 10, and the minimum price is 1. An error will be thrown if the input does not fit these criteria. Same applies of UpdateProductDetailsGUI.
4) Input formats should be valid. Otherwise, errors will be thrown.
5) For ease of sale, the selection and the quantity field for ManageStockGUI will not be cleared even after a successful sale. 
6) Once you sell a product that drops its stock below the threshold (10), a popup option will show up that gives you the option to replenish the stock to 100. 
7) If you do not replenish the stock and try to sell quanities of produc that exceeds the current stock, error will be thrown.
8) The program will create an "inventory.txt" file, which will be used as a database that contain all the relevant data for products and sales. Clear the text file in order to reset all the data. 

Repository link: https://github.com/Teniwoha/Research-Lab-Inventory-Management-System
*/
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

class Product{
    private String name;
    private int stock;
    private double price;

    public Product(String name, int stock, double price){
            this.name = name;
            this.stock = stock;
            this.price = price;
    }

    public String getName(){
        return name;
    }
    
    public int getStock(){
        return stock;
    }

    public double getPrice(){
        return price;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public void setStock(int stock){
        this.stock = stock;
    }

    public void setPrice(double price){
        this.price = price;
    }
    public String toString(){
        return name + "," + stock + "," + price;
    }

    public static Product fromString(String productString){
        String[] parts = productString.split(",");
        if (parts.length != 3){
            throw new IllegalArgumentException("Invalid product string format");
        }
        String name = parts[0];
        int stock = Integer.parseInt(parts[1]);
        double price = Double.parseDouble(parts[2]);
        return new Product(name, stock, price);
    }
}

class Sale{
    private String name;
    private String date;
    private int stock;
    private double price;
    private double profit;
    private double total_profit;
    public Sale(String date, String name, int stock, double price){
            this.date = date;
            this.name = name;
            this.stock = stock;
            this.price = price;
            this.profit = stock * price;
            InventoryManager.setTotalProfit(stock * price);
            this.total_profit = InventoryManager.getTotalProfit();
    }

    public String getName(){
        return name;
    }

    public String getDate(){
        return date;
    }

    public int getStock(){
        return stock;
    }

    public double getPrice(){
        return price;
    }
    
    public double getProfit(){
        return profit;
    }

    public double getTotalProfit(){
        return total_profit;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setStock(int stock){
        this.stock = stock;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public void setTotalProfit(double total_profit){
        this.total_profit = total_profit;
    }
    public String toString(){
        return date + "," + name + "," + stock + "," + price + "," + profit + "," + total_profit;
    }

    public static Sale fromString(String saleString){
        String[] parts = saleString.split(",");
        if (parts.length != 6){
            throw new IllegalArgumentException("Invalid sale string format");
        }
        String date = parts[0];
        String name = parts[1];
        int stock = Integer.parseInt(parts[2]);
        double price = Double.parseDouble(parts[3]);
        return new Sale(date, name, stock, price);
    }
}

class InventoryManager{
    private static List<Product> inventory;
    private static List<Sale> sales;
    private static double total_profit = 0;

    public InventoryManager(){
        inventory = new ArrayList<>();
        sales = new ArrayList<>();
        try {
            inventory.clear();
            sales.clear();
            loadData("inventory.txt");
            System.out.println("Files loaded!\n");
            if (sales.size() > 0){
                setTotalProfit(sales.get(sales.size() - 1).getTotalProfit());
            }
        }
        catch (IOException error){
            System.err.println("Error loading data: " + error.getMessage());
        }
    }

    public static List<Product> getInventory(){
        return inventory;
    }

    public static List<Sale> getSales(){
        return sales;
    }

    public static double getTotalProfit(){
        return total_profit;
    }

    public static void setTotalProfit(double sale_profit){
        total_profit += sale_profit;
    }

    public static void addProduct(Product product){
        inventory.add(product);
        try {
            saveData("inventory.txt");
            System.out.println("product saved in file!\n");
        }
        catch (IOException error){
            System.err.println("Error saving data: " + error.getMessage());
        }
    }
    public static void addSale(Sale sale){
        sales.add(sale);
        try {
            saveData("inventory.txt");
            System.out.println("sale saved in file!\n");
        }
        catch (IOException error){
            System.err.println("Error saving data: " + error.getMessage());
        }
    }

    public static void saveData(String filename) throws IOException{
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, false))){
            for (Product product : inventory){
                out.println("Product:" + product.toString());
            }
            for (Sale sale : sales){
                out.println("Sale:" + sale.toString());
            }
            out.println("TotalProfit:" + total_profit);
        }
    }

    public static void loadData(String filename) throws IOException{
        inventory.clear();
        sales.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = br.readLine()) != null){
                if (line.startsWith("Product:")){
                    Product product = Product.fromString(line.substring(8));
                    inventory.add(product);
                } 
                else if (line.startsWith("Sale:")){
                    Sale sale = Sale.fromString(line.substring(5));
                    sales.add(sale);
                }
                else if (line.startsWith("TotalProfit:")){
                    total_profit = Double.parseDouble(line.substring(12));
                }
            }
        }
    }
}

class AddProductGUI{
    private static JFrame jf;
    private JTextField nameField;
    private JTextField stockField;
    private JTextField priceField;
    private JButton addButton;
    private JButton backButton;

    public AddProductGUI(){
        jf = new JFrame();
        jf.setTitle("Add new product");
        jf.setBounds(100, 100, 500, 300);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().setLayout(new FlowLayout());

        JLabel nameLabel = new JLabel("Name:");
        JLabel stockLabel = new JLabel("Stock:");
        JLabel priceLabel = new JLabel("Price:");

        nameField = new JTextField(10);
        stockField = new JTextField(10);
        priceField = new JTextField(10);

        addButton = new JButton("Add");
        backButton = new JButton("Back");

        jf.add(nameLabel);
        jf.add(nameField);
        jf.add(stockLabel);
        jf.add(stockField);
        jf.add(priceLabel);
        jf.add(priceField);
        jf.add(addButton);
        jf.add(backButton);

        addButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Boolean exists = false;
                String inputName = nameField.getText();
                try {
                    int inputStock = Integer.parseInt(stockField.getText());
                    Double inputPrice = Double.parseDouble(priceField.getText());
                    if (inputName.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Error: Missing or invalid input format.", "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Invalid input format!");
                    }
                    else if (inputStock < 10){
                        JOptionPane.showMessageDialog(null, "Error: stock cannot be less than 10.", "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Invalid value!");
                    }
                    else if (inputPrice <= 0){
                        JOptionPane.showMessageDialog(null, "Error: price cannot be less than 1.", "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Invalid value!");
                    }
                    else{
                        for (Product product : InventoryManager.getInventory()){
                            if (product.getName().equals(inputName)){
                                exists = true;
                            }
                        }
                        if (exists){
                            JOptionPane.showMessageDialog(null, "Error: Product already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            Product product = new Product(inputName, inputStock, inputPrice);
                            InventoryManager.addProduct(product);
                            JOptionPane.showMessageDialog(null, "Product successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            System.out.println("Product successfully added!\nName: " + inputName + "\nStock: " + inputStock + "\nPrice: " + inputPrice);
                            nameField.setText("");
                            stockField.setText("");
                            priceField.setText("");
                        }
                    }
                }
                catch(NumberFormatException error){
                    JOptionPane.showMessageDialog(null, "Error: Missing or invalid input format.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Invalid input format!");
                }
            }
        });

        backButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                jf.setVisible(false);
                nameField.setText("");
                stockField.setText("");
                priceField.setText("");
                MainGUI.setVisible();
            }
        });  
    }
    public static void setVisible(){
        jf.setVisible(true);
    }
}

class UpdateProductDetailsGUI{
    private static JFrame jf;
    private static DefaultTableModel tm;
    
    private JTextField searchField;
    private JButton updateButton, backButton;
    private JTable table;
    private List<Product> filteredProducts; 

    public UpdateProductDetailsGUI(){
        this.filteredProducts = new ArrayList<>(InventoryManager.getInventory());
        jf = new JFrame();
        jf.setTitle("Product Manager");
        jf.setSize(800, 600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(new BorderLayout());

        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ filter(); }
            public void removeUpdate(DocumentEvent e){ filter(); }
            public void changedUpdate(DocumentEvent e){ filter(); }
        });
        jf.add(searchField, BorderLayout.NORTH);

        tm = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return true; 
            }
        };
        tm.addColumn("Name");
        tm.addColumn("Stock");
        tm.addColumn("Price");
        
        table = new JTable(tm);
        JScrollPane scrollPane = new JScrollPane(table);
        jf.add(scrollPane, BorderLayout.CENTER);

        updateButton = new JButton("Update");
        backButton = new JButton("Back");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);
        jf.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener((ActionEvent e) -> {
            if (table.isEditing()){
                table.getCellEditor().stopCellEditing();
            }
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0){
                JOptionPane.showMessageDialog(null, "Error: product not selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                String inputName = String.valueOf(tm.getValueAt(selectedRow, 0));
                System.out.println("changed name: " + inputName);
                try {
                    int inputStock = Integer.parseInt(String.valueOf(tm.getValueAt(selectedRow, 1)));
                    Double inputPrice = Double.parseDouble(String.valueOf(tm.getValueAt(selectedRow, 2)));
                    System.out.println("Hi " + inputStock + " " + inputPrice);
                    if (inputName.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Error: Missing or invalid input format.", "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Invalid name format!");
                    }
                    else if (inputStock < 10){
                        JOptionPane.showMessageDialog(null, "Error: stock cannot be less than 10.", "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Invalid value!");
                    }
                    else if (inputPrice < 1){
                        JOptionPane.showMessageDialog(null, "Error: price cannot be less than 1.", "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Invalid value!");
                    }
                    else{
                        if (!searchField.getText().trim().isEmpty()){
                            if (selectedRow >= 0){
                                Product selectedProduct = filteredProducts.get(selectedRow);
                                for (Product product : InventoryManager.getInventory()){
                                    if (product.getName().equals(selectedProduct.getName())){
                                        product.setName(inputName);
                                        product.setStock(inputStock);
                                        product.setPrice(inputPrice);
                                    }
                                }
                                try {
                                    InventoryManager.saveData("inventory.txt");
                                    System.out.println("product updated in file!\n");
                                }
                                catch (IOException error){
                                    System.err.println("Error saving data: " + error.getMessage());
                                }
                            }
                        }
                        else{
                            if (selectedRow >= 0){
                                Product selectedProduct = InventoryManager.getInventory().get(selectedRow);
                                for (Product product : InventoryManager.getInventory()){
                                    if (product.getName().equals(selectedProduct.getName())){
                                        product.setName(inputName);
                                        product.setStock(inputStock);
                                        product.setPrice(inputPrice);
                                    }
                                }
                                try {
                                    InventoryManager.saveData("inventory.txt");
                                    System.out.println("product updated in file!\n");
                                }
                                catch (IOException error){
                                    System.err.println("Error saving data: " + error.getMessage());
                                }
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Product successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("Product successfully updated!\nName: " + inputName + "\nStock: " + inputStock + "\nPrice: " + inputPrice);
                        searchField.setText("");
                        updateFullTable();
                    }
                }
                catch(NumberFormatException error){
                    JOptionPane.showMessageDialog(null, "Error: Missing or invalid input format.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Invalid input format!!!");
                    System.out.println("Updated Values: " + inputName);
                    updateFullTable();
                }
                updateFullTable();
            }
        });

        backButton.addActionListener((ActionEvent e) -> {
            jf.setVisible(false);
            if (table.isEditing()){
                table.getCellEditor().cancelCellEditing();
            }
            updateFullTable();
            MainGUI.setVisible();
        });
    }

    private void updateSearchTable(){
        tm.setRowCount(0);
        for (Product product : filteredProducts){
            tm.addRow(new Object[]{product.getName(), product.getStock(), product.getPrice()});
        }
    }

    private void filter(){
        String searchText = searchField.getText().toLowerCase();
        filteredProducts = InventoryManager.getInventory().stream()
            .filter(product -> product.getName().toLowerCase().contains(searchText))
            .collect(Collectors.toList());
        updateSearchTable();
    }

    public static void updateFullTable(){
        tm.setRowCount(0);
        for (Product product : InventoryManager.getInventory()){
            tm.addRow(new Object[]{product.getName(), product.getStock(), product.getPrice()});
        }
    }

    public static void setVisible(){
        jf.setVisible(true);
    }
}

class ManageStockGUI{
    private static JFrame jf;
    private static DefaultTableModel tm;
    
    private JTextField searchField;
    private JTextField quantityField;
    private JButton sellButton, backButton;
    private JTable table;
    private List<Product> filteredProducts; 
    private int selectedRow;

    public ManageStockGUI(){
        this.filteredProducts = new ArrayList<>(InventoryManager.getInventory());
        jf = new JFrame();
        jf.setTitle("Product Manager");
        jf.setSize(800, 600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(new BorderLayout());

        searchField = new JTextField(20);
        quantityField = new JTextField(5);
        searchField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ filter(); }
            public void removeUpdate(DocumentEvent e){ filter(); }
            public void changedUpdate(DocumentEvent e){ filter(); }
        });
        jf.add(searchField, BorderLayout.NORTH);

        tm = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tm.addColumn("Name");
        tm.addColumn("Stock");
        tm.addColumn("Price");

        table = new JTable(tm);
        JScrollPane scrollPane = new JScrollPane(table);
        jf.add(scrollPane, BorderLayout.CENTER);

        sellButton = new JButton("Sell");
        backButton = new JButton("Back");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sellButton);
        buttonPanel.add(new JLabel("Quantity:"));
        buttonPanel.add(quantityField);
        buttonPanel.add(backButton);
        jf.add(buttonPanel, BorderLayout.SOUTH);

        sellButton.addActionListener((ActionEvent e) -> {
            selectedRow = table.getSelectedRow();
            if (selectedRow < 0){
                JOptionPane.showMessageDialog(null, "Error: product not selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                Product selectedProduct = InventoryManager.getInventory().get(selectedRow);
                if (!searchField.getText().trim().isEmpty()){
                    selectedProduct = filteredProducts.get(selectedRow);
                }
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                    if (quantity > selectedProduct.getStock()){
                        JOptionPane.showMessageDialog(null, "Error: Value exceeds stock.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (quantity == 0){
                        JOptionPane.showMessageDialog(null, "Error: Value cannot be 0.", "Error", JOptionPane.ERROR_MESSAGE);  
                    }
                    else{
                        for (Product product : InventoryManager.getInventory()){
                            if (product.getName().equals(selectedProduct.getName())){
                                product.setStock(selectedProduct.getStock() - quantity);
                            }
                        }
                        String successMessage = "Success! " + quantity + " " + selectedProduct.getName() + " has been sold.";
                        JOptionPane.showMessageDialog(null, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                        LocalDateTime now = LocalDateTime.now();
                        String date = dtf.format(now);
                        System.out.println(date);
                        Sale sale = new Sale(date, selectedProduct.getName(), quantity, selectedProduct.getPrice());
                        InventoryManager.addSale(sale);
                        if (selectedProduct.getStock() < 10){
                            int result = JOptionPane.showConfirmDialog(jf, "The stock of " + selectedProduct.getName() + " is running low. Do you want to restock 100?", "Choose an option", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION){
                                for (Product product : InventoryManager.getInventory()){
                                    if (product.getName().equals(selectedProduct.getName())){
                                        product.setStock(100);
                                    } 
                                }
                                try {
                                    InventoryManager.saveData("inventory.txt");
                                    System.out.println("product updated in file!\n");
                                }
                                catch (IOException error){
                                    System.err.println("Error saving data: " + error.getMessage());
                                }
                            }
                        }
                        updateFullTable();
                        table.setRowSelectionInterval(selectedRow, selectedRow);
                        System.out.println("Success! " + selectedProduct.getStock() + " " + selectedProduct.getName() + " remaning!");
                    }

                }
                catch(NumberFormatException error){
                    JOptionPane.showMessageDialog(null, "Error: Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Invalid input format!!!");
                    updateFullTable();
                }
            }
        });

        backButton.addActionListener((ActionEvent e) -> {
            jf.setVisible(false);
            quantityField.setText("");
            searchField.setText("");
            updateFullTable();
            MainGUI.setVisible();
        });
    }

    private void updateSearchTable(){
        tm.setRowCount(0);
        for (Product product : filteredProducts){
            tm.addRow(new Object[]{product.getName(), product.getStock(), product.getPrice()});
        }
    }

    private void filter(){
        String searchText = searchField.getText().toLowerCase();
        filteredProducts = InventoryManager.getInventory().stream()
            .filter(product -> product.getName().toLowerCase().contains(searchText))
            .collect(Collectors.toList());
        updateSearchTable();
    }

    public static void updateFullTable(){
        tm.setRowCount(0);
        for (Product product : InventoryManager.getInventory()){
            tm.addRow(new Object[]{product.getName(), product.getStock(), product.getPrice()});
        }
    }

    public static void setVisible(){
        jf.setVisible(true);
    }
}

class GenerateSalesReportGUI{
    private static JFrame jf;
    private static DefaultTableModel tm;
    private JTable salesTable;
    private JButton backButton;

    public GenerateSalesReportGUI(){
        jf = new JFrame();
        jf.setTitle("Sales Report");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(800, 600);
        jf.setLocationRelativeTo(null);

        tm = new DefaultTableModel();
        salesTable = new JTable(tm);

        tm.addColumn("Date");
        tm.addColumn("Name");
        tm.addColumn("Price");
        tm.addColumn("Quantity");
        tm.addColumn("Profit");
        tm.addColumn("Total Profit");

        JScrollPane scrollPane = new JScrollPane(salesTable);
        jf.getContentPane().add(scrollPane, BorderLayout.CENTER);
        backButton = new JButton("Back");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        jf.add(buttonPanel, BorderLayout.SOUTH);

        displaySales();

        backButton.addActionListener((ActionEvent e) -> {
            jf.setVisible(false);
            MainGUI.setVisible();
        });
    }

    public static void displaySales(){
        tm.setRowCount(0);

        for (Sale sale : InventoryManager.getSales()){
            String date = sale.getDate();
            String name = sale.getName();
            double price = sale.getPrice();
            double profit = sale.getProfit();
            double total_profit = sale.getTotalProfit();
            int quantity = sale.getStock();

            tm.addRow(new Object[]{date, name, price, quantity, profit, total_profit});
        }
    }

    public static void setVisible(){
        jf.setVisible(true);
    }
}

class MainGUI{
    private static JFrame jf;
    private JButton btnAddProduct;
    private JButton btnUpdateProduct;
    private JButton btnManageStock;
    private JButton btnGenerateReport;

    public MainGUI(){
        jf = new JFrame();
        jf.setTitle("Research Lab Inventory Management System");
        jf.setBounds(100, 100, 500, 300);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().setLayout(new FlowLayout());

        btnAddProduct = new JButton("Add new product");
        btnUpdateProduct = new JButton("Update product details");
        btnManageStock = new JButton("Manage stock levels");
        btnGenerateReport = new JButton("Generate sales report");

        jf.getContentPane().add(btnAddProduct);
        jf.getContentPane().add(btnUpdateProduct);
        jf.getContentPane().add(btnManageStock);
        jf.getContentPane().add(btnGenerateReport);

        btnAddProduct.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jf.setVisible(false);
                AddProductGUI.setVisible();
                System.out.println("Add new product button clicked");
            }
        });
        btnUpdateProduct.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jf.setVisible(false);
                UpdateProductDetailsGUI.updateFullTable();
                UpdateProductDetailsGUI.setVisible();
                System.out.println("Update product details button clicked");
            }
        });

        btnManageStock.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jf.setVisible(false);
                ManageStockGUI.updateFullTable();
                ManageStockGUI.setVisible();
                System.out.println("Manage stock levels button clicked");
            }
        });

        btnGenerateReport.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jf.setVisible(false);
                GenerateSalesReportGUI.displaySales();
                GenerateSalesReportGUI.setVisible();
                System.out.println("Generate sales report button clicked");
            }
        });
        setVisible();
    }
    public static void setVisible(){
        jf.setVisible(true);
    }
}

class Main{
    public static void main(String[] args){
        InventoryManager IM = new InventoryManager();
        MainGUI maingui = new MainGUI();
        AddProductGUI addproductgui = new AddProductGUI();
        UpdateProductDetailsGUI updateproductdetailsgui = new UpdateProductDetailsGUI();
        ManageStockGUI managestockgui = new ManageStockGUI();
        GenerateSalesReportGUI generatesalesreportgui = new GenerateSalesReportGUI();
    }
}
