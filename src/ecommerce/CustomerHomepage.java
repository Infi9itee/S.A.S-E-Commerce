
package ecommerce;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CustomerHomepage extends javax.swing.JFrame {
    private boolean couponCodeApplied = false;
    String dataFolderPath = "data/";
   public CustomerHomepage() {
         initComponents();
        setTitle("Customer Homepage");
        updateProductListFromFile(); // Populate the table with product details
        populateCategoryComboBox();
        // Add a MouseListener to jTable2
        jTable2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Check for double-click
                    int selectedRow = jTable2.getSelectedRow();
                    if (selectedRow != -1) { // Ensure a row is selected
                        String productName = (String) jTable2.getValueAt(selectedRow, 1);
                        showQuantityInputDialog(productName);
                    }
                }
            }
        });
        // Add a MouseListener to jTable3
    jTable3.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) { // Check for double-click
                int selectedRow = jTable3.getSelectedRow();
                if (selectedRow != -1) { // Ensure a row is selected
                    String productName = (String) jTable3.getValueAt(selectedRow, 1);
                    showQuantityInputDialogForCart(productName);
                }
            }
        }
    });
    }
   private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {
    if (evt.getClickCount() == 2) { // Check for double-click
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow != -1) {
            String productName = (String) jTable2.getValueAt(selectedRow, 2);
            String priceStr = (String) jTable2.getValueAt(selectedRow, 0);
            double price = Double.parseDouble(priceStr);

            String quantityStr = JOptionPane.showInputDialog("Enter quantity:");
            if (quantityStr != null) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    // Add the product to the cart
                    addToCart(productName, quantity, price);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.");
                }
            }
        }
    }
}
   private void addToCart(String productName, int quantity, double price) {
        DefaultTableModel cartModel = (DefaultTableModel) jTable3.getModel();
        Object[] row = { productName, quantity, price };
        cartModel.addRow(row);

        // Update the quantity in jTable2
        int rowCount = jTable2.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String productInTable = (String) jTable2.getValueAt(i, 1);
            if (productInTable.equals(productName)) {
                int currentQuantity = Integer.parseInt((String) jTable2.getValueAt(i, 3));
                int newQuantity = currentQuantity - quantity;
                jTable2.setValueAt(Integer.toString(newQuantity), i, 3);
                break;
            }
        }
        updateTotalPrice(); 
    }
   private void updateTotalPrice() {
    double totalPrice = calculateTotalPrice();
    jLabel2.setText("Total: " + totalPrice);
}
   private void generateTextReport(String customerName, String inquiries) {
       try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(dataFolderPath + "product_list.txt", true));

        // Add customer name to the report
        writer.write("Customer Name: " + customerName);
        writer.newLine();

        // Add product details to the report
        DefaultTableModel cartModel = (DefaultTableModel) jTable3.getModel();
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String productName = (String) cartModel.getValueAt(i, 1);
            String category = (String) cartModel.getValueAt(i, 2);
            int quantity = (int) cartModel.getValueAt(i, 3);
            double price = (double) cartModel.getValueAt(i, 4);

            String productInfo = productName + " (" + category + "): " + quantity + " x " + price + " ৳";
            writer.write(productInfo);
            writer.newLine(); // Move to the next line for the next product
        }

        // Add the total price to the report
        double totalPrice = calculateTotalPrice();
        writer.write("Total Price: " + totalPrice + " ৳");
        writer.newLine(); // Move to the next line

        writer.close();

        if (inquiries != null && !inquiries.isEmpty()) {
            // Store inquiries in a separate file
            BufferedWriter inquiriesWriter = new BufferedWriter(new FileWriter(dataFolderPath + "inquiries.txt", true));
            inquiriesWriter.write("Customer Name: " + customerName);
            inquiriesWriter.newLine();
            inquiriesWriter.write("Inquiries: " + inquiries);
            inquiriesWriter.newLine();
            inquiriesWriter.close();
        }

        JOptionPane.showMessageDialog(this, "Text report saved successfully!", "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error generating text report.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

   public void updateProductListFromFile() {
      DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
    model.setRowCount(0); // Clear the existing rows in the table

    try {
        // Use a relative path to the "data" folder within your project
        FileReader fr = new FileReader("data/product.txt");
        BufferedReader br = new BufferedReader(fr);

        String line;
        while ((line = br.readLine()) != null) {
            // Split the line into product details using a delimiter (e.g., comma)
            String[] details = line.split(",");

            if (details.length == 5) { // Updated to check for 5 fields
                // Add the product to the table with the correct columns
                model.addRow(new Object[]{details[1], details[2], details[3], details[4], details[0]});
            }
        }

        // Close the BufferedReader
        br.close();
    } catch (IOException e) {
        e.printStackTrace();
        // Handle the exception appropriately
    }
}
   private void showQuantityInputDialog(String productName) {
          JTextField quantityField = new JTextField();
    Object[] message = {
            "Enter quantity for " + productName + ":",
            quantityField
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Enter Quantity", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        try {
            int quantityToAdd = Integer.parseInt(quantityField.getText());
            if (quantityToAdd > 0) {
                // Get the product details from jTable2
                int selectedRow = jTable2.getSelectedRow();
                if (selectedRow != -1) {
                    String productId = (String) jTable2.getValueAt(selectedRow, 0);
                    String productCategory = (String) jTable2.getValueAt(selectedRow, 2);
                    double productPrice = Double.parseDouble((String) jTable2.getValueAt(selectedRow, 4));

                    // Check if there's enough quantity in jTable2
                    int currentQuantityInTable = Integer.parseInt((String) jTable2.getValueAt(selectedRow, 3));
                    if (quantityToAdd <= currentQuantityInTable) {
                        // Update the quantity in jTable2
                        int newQuantityInTable = currentQuantityInTable - quantityToAdd;
                        jTable2.setValueAt(Integer.toString(newQuantityInTable), selectedRow, 3);

                        // Add the product to jTable3 (the cart)
                        DefaultTableModel cartModel = (DefaultTableModel) jTable3.getModel();
                        Object[] cartRow = {productId, productName, productCategory, quantityToAdd, productPrice};
                        cartModel.addRow(cartRow);

                        // Update the total price
                        updateTotalPrice();
                    } else {
                        JOptionPane.showMessageDialog(null, "Not enough quantity in stock.", "Insufficient Stock", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
        }
    }
    }
   private void showQuantityInputDialogForCart(String productName) {
       
    JTextField quantityField = new JTextField();
    Object[] message = {
            "Enter quantity to put back for " + productName + ":",
            quantityField
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Enter Quantity", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        try {
            int quantityToAdd = Integer.parseInt(quantityField.getText());
            if (quantityToAdd > 0) {
                int selectedRow = jTable3.getSelectedRow();
                if (selectedRow != -1) {
                    int currentQuantityInCart = (int) jTable3.getValueAt(selectedRow, 3);
                    if (quantityToAdd <= currentQuantityInCart) {
                        // Find the row in jTable2 corresponding to the selected product
                        for (int i = 0; i < jTable2.getRowCount(); i++) {
                            String productInTable = (String) jTable2.getValueAt(i, 1);
                            if (productInTable.equals(productName)) {
                                int currentQuantityInTable = Integer.parseInt((String) jTable2.getValueAt(i, 3));
                                int newQuantityInTable = currentQuantityInTable + quantityToAdd;
                                jTable2.setValueAt(Integer.toString(newQuantityInTable), i, 3);
                                break;
                            }
                        }

                        // Update the quantity in jTable3 (the cart)
                        jTable3.setValueAt(currentQuantityInCart - quantityToAdd, selectedRow, 3);

                        // If the cart quantity becomes 0, remove the row
                        if (currentQuantityInCart - quantityToAdd == 0) {
                            DefaultTableModel cartModel = (DefaultTableModel) jTable3.getModel();
                            cartModel.removeRow(selectedRow);
                        }

                        // Update the total price
                        updateTotalPrice();
                    } else {
                        JOptionPane.showMessageDialog(null, "Not enough quantity in the cart.", "Insufficient Quantity", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
   private double calculateTotalPrice() {
    DefaultTableModel cartModel = (DefaultTableModel) jTable3.getModel();
    double total = 0.0;

    for (int i = 0; i < cartModel.getRowCount(); i++) {
        double price = (double) cartModel.getValueAt(i, 4); // Assuming the price is in column 4
        int quantity = (int) cartModel.getValueAt(i, 3); // Assuming the quantity is in column 3
        total += price * quantity;
    }

    return total;
}
   private boolean checkPromoCode(String promoCode) {
     try {
        // Use a relative path to the "data" folder within your project
        FileReader fr = new FileReader("data/promos.txt");
        BufferedReader br = new BufferedReader(fr);

        String line;
        while ((line = br.readLine()) != null) {
            if (line.equalsIgnoreCase(promoCode)) {
                br.close();
                return true; // Valid promo code
            }
        }

        br.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

    return false; // Invalid promo code or file not found
}
   private void checkoutAndStoreInquiries() {
     // Display a message to thank the user for shopping
    JOptionPane.showMessageDialog(this, "Thanks for shopping with us!");

    // Ask for the customer's name or identifier using an input dialog
    String customerName = JOptionPane.showInputDialog(this, "Please enter your name or identifier:");

    // Check if the customer provided a name or identifier
    if (customerName != null && !customerName.isEmpty()) {
        // Ask for inquiries using an input dialog
        String inquiries = JOptionPane.showInputDialog(this, "Do you have any inquiries?");
        
        // Generate the text report regardless of whether inquiries are provided
        generateTextReport(customerName, inquiries);
    } else {
        JOptionPane.showMessageDialog(this, "Please enter your name or identifier to save inquiries.", "No Name/Identifier Provided", JOptionPane.WARNING_MESSAGE);
    }
}
   private void populateCategoryComboBox() {
          DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
    HashSet<String> uniqueCategories = new HashSet<>();

    // Iterate through jTable2 and add unique categories to the set
    for (int i = 0; i < model.getRowCount(); i++) {
        String category = (String) model.getValueAt(i, 2); // Assuming category is in column 2
        uniqueCategories.add(category);
    }

    // Clear the existing items in jComboBox1
    jComboBox1.removeAllItems();

    // Add the default option as the first item
    jComboBox1.addItem("Select Category");

    // Add the unique categories to jComboBox1
    for (String category : uniqueCategories) {
        jComboBox1.addItem(category);
    }

    // Add an ActionListener to the JComboBox to filter jTable2 when a category is selected
    jComboBox1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedCategory = (String) jComboBox1.getSelectedItem();

            if (selectedCategory != null && !selectedCategory.equals("Select Category")) {
                // Create a TableRowSorter to filter jTable2 based on the selected category
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) jTable2.getModel());
                jTable2.setRowSorter(sorter);

                // Create a RowFilter to match the selected category (case-insensitive)
                RowFilter<DefaultTableModel, Object> categoryFilter = RowFilter.regexFilter("(?i)" + selectedCategory, 2); // Assuming category is in column 2

                // Set the RowFilter to the TableRowSorter
                sorter.setRowFilter(categoryFilter);
            } else {
                // If no category is selected or "Select Category" is selected, remove any existing filter
                jTable2.setRowSorter(null);
            }
        }
    });
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Name", "Category", "Quantity", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton5.setForeground(new java.awt.Color(255, 51, 0));
        jButton5.setText("Find");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));

        jButton3.setBackground(new java.awt.Color(102, 102, 102));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/rsz_nicepng_gray-circle-png_1366211 (1).png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setText("Checkout");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Cupon Code");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton1.setForeground(new java.awt.Color(255, 0, 51));
        jButton1.setText("Apply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(0, 102, 102));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Total: 0.0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .addComponent(jTextField2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(19, 19, 19)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Name", "Category", "Quantity", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable3);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 36, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DashBoard DashBoardFrame = new DashBoard();
        DashBoardFrame.setVisible(true);
        DashBoardFrame.pack();
        DashBoardFrame.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String searchCategory = jTextField1.getText().trim().toLowerCase();

    DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    jTable2.setRowSorter(sorter);

    // Filter based on the selected category (case-insensitive)
    if (!searchCategory.isEmpty()) {
        RowFilter<DefaultTableModel, Object> categoryFilter = RowFilter.regexFilter("(?i)" + searchCategory, 1); // 2 is the column index for the "Category" column
        sorter.setRowFilter(categoryFilter);
    } else {
        // If the search category is empty, remove the filter
        sorter.setRowFilter(null);
    }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
         DefaultTableModel cartModel = (DefaultTableModel) jTable3.getModel();
    
    // Check if the cart is empty
    if (cartModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Your cart is empty. Please add items to your cart before checking out.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
        return; // Exit the method if the cart is empty
    }
    
    // Ask the customer if they have any inquiries
    int inquiriesChoice = JOptionPane.showConfirmDialog(this, "Do you have any inquiries?", "Inquiries", JOptionPane.YES_NO_OPTION);
    
    if (inquiriesChoice == JOptionPane.YES_OPTION) {
        checkoutAndStoreInquiries();
    } else {
        // Proceed with the checkout process here if the customer has no inquiries
        JOptionPane.showMessageDialog(this, "Thanks for shopping with us!");
        
        // Clear the cart after checkout
        cartModel.setRowCount(0);
        
        // You can add any additional checkout-related actions here
    }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       if (couponCodeApplied) {
        JOptionPane.showMessageDialog(this, "A coupon code has already been applied.", "Error", JOptionPane.ERROR_MESSAGE);
        return; // Do not process a new coupon code
    }

    // Get the entered promo code
    String promoCode = jTextField2.getText().trim();

    if (promoCode.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a valid promo code.", "Invalid Promo Code", JOptionPane.ERROR_MESSAGE);
        return; // No promo code entered, so exit the method
    }

    // Check if the entered promo code is valid
    if (!checkPromoCode(promoCode)) {
        JOptionPane.showMessageDialog(this, "Invalid promo code. Please enter a valid code.", "Invalid Promo Code", JOptionPane.ERROR_MESSAGE);
        return; // Invalid promo code, so exit the method
    }

    // Generate a random discount between 5% and 20%
    double randomDiscount = 5 + Math.random() * 15; // Random number between 5 and 20

    // Calculate the total price with the discount applied
    double totalPrice = calculateTotalPrice();
    double discountAmount = totalPrice * (randomDiscount / 100.0);
    double discountedPrice = totalPrice - discountAmount;

    // Format the discounted price and discount amount to two decimal places
    String formattedDiscountedPrice = String.format("%.2f ৳", discountedPrice);
    String formattedDiscountAmount = String.format("%.2f", discountAmount);

    // Update the jLabel2 text with the formatted discounted price
    jLabel2.setText("Total: " + formattedDiscountedPrice);

    // Show a pop-up dialog displaying the discount amount with two decimal places
    JOptionPane.showMessageDialog(this, "Discount applied: ৳" + formattedDiscountAmount, "Discount", JOptionPane.INFORMATION_MESSAGE);

    couponCodeApplied = true; // Mark that a coupon code has been applied
    jButton1.setEnabled(false); // Disable the "Apply" button
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // Handle the action when an item in jComboBox1 is selected
        String selectedCategory = (String) jComboBox1.getSelectedItem();
        if (selectedCategory != null) {
            // You can filter jTable2 based on the selected category here if needed
        }
    
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CustomerHomepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerHomepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerHomepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerHomepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerHomepage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
