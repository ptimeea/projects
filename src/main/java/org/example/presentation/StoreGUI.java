package org.example.presentation;

import org.example.businessLogic.ClientBLL;
import org.example.businessLogic.ProductBLL;
import org.example.businessLogic.OrdresBLL;
import org.example.model.Bill;
import org.example.model.CartItem;
import org.example.model.Client;
import org.example.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Presentation class for managing the GUI.
 */

public class StoreGUI {
    private JTabbedPane bills;
    private JPanel panel1;
    private JPanel clients;
    private JPanel products;
    private JButton btnShowClients;
    private JButton btnAddClient;
    private JButton btnEditClient;
    private JButton btnDeleteClient;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JTextField textFieldPhone;
    private JTable tableClients;
    private JButton btnShowProducts;
    private JButton btnAddProduct;
    private JButton btnEditProduct;
    private JButton btnDeleteProduct;
    private JTextField textFieldProductName;
    private JTextField textFieldPrice;
    private JTextField textFieldDescription;
    private JTextField textFieldStock;
    private JTable tableProducts;
    private JComboBox comboBoxClients;
    private JComboBox comboBoxProducts;
    private JTextField textFieldQuantity;
    private JButton btnAddCart;
    private JTable tableCart;
    private JLabel labelTotal;
    private JButton finalizeOrderButton;
    private JTable tableBills;
    private JPanel orders;
    private JButton btnShowBills;

    private ClientBLL clientBLL;
    private ProductBLL productBLL;
    private OrdresBLL ordersBLL = new OrdresBLL();


    public StoreGUI() {
        this.clientBLL = new ClientBLL();
        this.productBLL = new ProductBLL();

        btnShowClients.addActionListener(e -> refreshTableClients());
        btnShowProducts.addActionListener(e -> refreshTableProducts());
        btnAddClient.addActionListener(e -> {
            try {
                String name = textFieldName.getText().trim();
                String email = textFieldEmail.getText().trim();
                String phone = textFieldPhone.getText().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    throw new IllegalArgumentException("All fields are required!");
                }

                Client newClient = new Client(0, name, email, phone);
                clientBLL.insertClient(newClient);

                JOptionPane.showMessageDialog(clients, "Client added!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(clients, "The client could not be added: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEditClient.addActionListener(e -> {
            try {
                int selectedRow = tableClients.getSelectedRow();
                if (selectedRow == -indexValildator(selectedRow)) {
                    throw new IllegalStateException("Select a client from the table!");
                }
                int id = (int) tableClients.getValueAt(selectedRow, 0);
                String name = textFieldName.getText().trim();
                String email = textFieldEmail.getText().trim();
                String phone = textFieldPhone.getText().trim();

                Client updatedClient = new Client(id, name, email, phone);
                clientBLL.updateClient(updatedClient);

                JOptionPane.showMessageDialog(clients, "Client data updated!");
                populateOrderComboBoxes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(clients, "Error at editing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDeleteClient.addActionListener(e -> {
            try {
                int selectedRow = tableClients.getSelectedRow();
                if (selectedRow == -1) {
                    throw new IllegalStateException("Select a  client from the table!");
                }

                int id = (int) tableClients.getValueAt(selectedRow, 0);
                clientBLL.deleteClient(id);
                JOptionPane.showMessageDialog(clients, "Client deleted!");
                populateOrderComboBoxes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(clients, "Error at deleting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tableClients.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableClients.getSelectedRow();
                if (selectedRow != -1) {
                    textFieldName.setText(tableClients.getValueAt(selectedRow, 1).toString());
                    textFieldEmail.setText(tableClients.getValueAt(selectedRow, 2).toString());
                    textFieldPhone.setText(tableClients.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        btnAddProduct.addActionListener(e -> {
            try {
                String name = textFieldProductName.getText().trim();
                String priceStr = textFieldPrice.getText().trim();
                String description = textFieldDescription.getText().trim();
                String stockStr = textFieldStock.getText().trim();

                if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                    throw new IllegalArgumentException("All fields are required!");
                }

                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);

                Product newProduct = new Product(0, name, price, description, stock);
                productBLL.insertProduct(newProduct);

                JOptionPane.showMessageDialog(products, "Product added!");
                textFieldProductName.setText("");
                textFieldPrice.setText("");
                textFieldDescription.setText("");
                textFieldStock.setText("");

                refreshTableProducts();
                populateOrderComboBoxes();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(products, "Invalid Data", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(products, "Product could not be added: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnEditProduct.addActionListener(e -> {
            try {
                int selectedRow = tableProducts.getSelectedRow();
                if (selectedRow == -1) {
                    throw new IllegalStateException("Select a product from the table!");
                }

                int id = (int) tableProducts.getValueAt(selectedRow, 0);
                String name = textFieldProductName.getText().trim();
                double price = Double.parseDouble(textFieldPrice.getText().trim());
                String description = textFieldDescription.getText().trim();
                int stock = Integer.parseInt(textFieldStock.getText().trim());

                Product updatedProduct = new Product(id, name, price, description, stock);
                productBLL.updateProduct(updatedProduct);

                JOptionPane.showMessageDialog(panel1, "Product data updated!");
                populateOrderComboBoxes();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel1, "Data is not valid!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, "Error at modifing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDeleteProduct.addActionListener(e -> {
            try {
                int selectedRow = tableProducts.getSelectedRow();
                if (selectedRow == -1) {
                    throw new IllegalStateException("Select a product from the table!");
                }

                int id = (int) tableProducts.getValueAt(selectedRow, 0);
                String name = tableProducts.getValueAt(selectedRow, 1).toString();

                productBLL.deleteProduct(id);
                JOptionPane.showMessageDialog(panel1, "Product deleted!");
                populateOrderComboBoxes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, "Error at deleting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tableProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableProducts.getSelectedRow();
                if (selectedRow != -1) {

                    textFieldProductName.setText(tableProducts.getValueAt(selectedRow, 1).toString());
                    textFieldPrice.setText(tableProducts.getValueAt(selectedRow, 2).toString());

                    Object descValue = tableProducts.getValueAt(selectedRow, 3);
                    textFieldDescription.setText(descValue != null ? descValue.toString() : "");

                    textFieldStock.setText(tableProducts.getValueAt(selectedRow, 4).toString());
                }
            }
        });
        String[] cartHeaders = {"PRODUCT ID", "PRODUCT NAME", "PRICE", "QUANTITY", "SUBTOTAL"};
        DefaultTableModel cartModel = new DefaultTableModel(cartHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            } // Citire doar
        };
        tableCart.setModel(cartModel);
        btnAddCart.addActionListener(e -> {
            try {
                String selectedClientStr = (String) comboBoxClients.getSelectedItem();
                if (selectedClientStr == null) throw new IllegalArgumentException("Select a client!");
                int clientId = Integer.parseInt(selectedClientStr.split(" - ")[0]);

                String selectedProductStr = (String) comboBoxProducts.getSelectedItem();
                if (selectedProductStr == null) throw new IllegalArgumentException("Select a product!");
                int productId = Integer.parseInt(selectedProductStr.split(" - ")[0]);

                int desiredQty = Integer.parseInt(textFieldQuantity.getText().trim());

                Product product = productBLL.findProductById(productId);
                if (product == null) throw new IllegalArgumentException("The product is not found!");
                ordersBLL.addProductToCart(clientId, product, desiredQty);
                refreshCartTableForSelectedClient();
                textFieldQuantity.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(products, "Invalid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(products, ex.getMessage(), "Insuficient Stock", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(products, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        comboBoxClients.addActionListener(e -> {
            refreshCartTableForSelectedClient();
        });
        bills.addChangeListener(e -> {
            if (bills.getSelectedIndex() == 2) {
                populateOrderComboBoxes();
            }
        });
        finalizeOrderButton.addActionListener(e -> {
            try {
                String selectedClientStr = (String) comboBoxClients.getSelectedItem();
                if (selectedClientStr == null) {
                    throw new IllegalArgumentException("Select a client!");
                }
                int clientId = Integer.parseInt(selectedClientStr.split(" - ")[0]);
                String clientName = selectedClientStr.split(" - ")[1];

                Client currentClient = new Client();
                currentClient.setId(clientId);
                currentClient.setName(clientName);

                ordersBLL.finalizeOrder(currentClient);

                JOptionPane.showMessageDialog(panel1, "Order finalized successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                DefaultTableModel model = (DefaultTableModel) tableCart.getModel();
                model.setRowCount(0);
                updateCartTotal(model);

                refreshTableProducts();
                populateOrderComboBoxes();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(panel1, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, "Error finalizing order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnShowBills.addActionListener(e -> refreshTableBills());
        bills.addChangeListener(e -> {
            int selectedIndex = bills.getSelectedIndex();
            if (selectedIndex == 2) {
                populateOrderComboBoxes();
            } else if (selectedIndex == 3) {
                refreshTableBills();
            }
        });
    }

    private void updateCartTotal(DefaultTableModel model) {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object value = model.getValueAt(i, 4);
            if (value != null) {
                total += Double.parseDouble(value.toString());
            }
        }
        labelTotal.setText("Total: " + total + " Lei");
    }

    private void refreshTableClients() {
        try {
            List<Client> clientList = clientBLL.findAllClients();
            DefaultTableModel model = TableGenerator.createTableFromData(clientList);
            tableClients.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(clients, "Data could not be loaded! " + ex.getMessage());
        }
    }

    private void refreshTableProducts() {
        try {
            List<Product> productList = productBLL.findAllProducts();
            DefaultTableModel model = TableGenerator.createTableFromData(productList);
            tableProducts.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(products, "Data could not be loaded! " + ex.getMessage());
        }
    }

    private int indexValildator(int index) {
        if (index == -1)
            return 0;
        return 1;
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public void populateOrderComboBoxes() {
        try {
            comboBoxClients.removeAllItems();
            comboBoxProducts.removeAllItems();

            List<Client> allClients = clientBLL.findAllClients();
            List<Product> allProducts = productBLL.findAllProducts();

            for (Client c : allClients) {
                comboBoxClients.addItem(c.getId() + " - " + c.getName());
            }

            for (Product p : allProducts) {
                comboBoxProducts.addItem(p.getId() + " - " + p.getName() + " (Stock: " + p.getStock() + ")");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel1, "Error for loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void refreshCartTableForSelectedClient() {
        try {
            String selectedClientStr = (String) comboBoxClients.getSelectedItem();
            if (selectedClientStr == null) return;

            int clientId = Integer.parseInt(selectedClientStr.split(" - ")[0]);

            DefaultTableModel model = (DefaultTableModel) tableCart.getModel();
            model.setRowCount(0);

            List<CartItem> cart = ordersBLL.getCartForClient(clientId);

            for (CartItem item : cart) {
                double subtotal = item.getProduct().getPrice() * item.getQuantity();
                model.addRow(new Object[]{
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        subtotal
                });
            }

            updateCartTotal(model);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void refreshTableBills() {
        try {
            List<Bill> billList = ordersBLL.findAllBills();
            DefaultTableModel model = TableGenerator.createTableFromData(billList);
            tableBills.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel1, "Bills data could not be loaded! " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        bills = new JTabbedPane();
        panel1.add(bills, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        clients = new JPanel();
        clients.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        bills.addTab("Clients", clients);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 5, new Insets(0, 0, 0, 0), -1, -1));
        clients.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnShowClients = new JButton();
        btnShowClients.setText("Show Clients");
        panel2.add(btnShowClients, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnAddClient = new JButton();
        btnAddClient.setText("Add Client");
        panel2.add(btnAddClient, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnEditClient = new JButton();
        btnEditClient.setText("Edit Client");
        panel2.add(btnEditClient, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDeleteClient = new JButton();
        btnDeleteClient.setText("Delete Client");
        panel2.add(btnDeleteClient, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Name:");
        panel2.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Email:");
        panel2.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Phone Number:");
        panel2.add(label3, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldName = new JTextField();
        panel2.add(textFieldName, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textFieldEmail = new JTextField();
        panel2.add(textFieldEmail, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textFieldPhone = new JTextField();
        panel2.add(textFieldPhone, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableClients = new JTable();
        scrollPane1.setViewportView(tableClients);
        products = new JPanel();
        products.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
        bills.addTab("Products", products);
        btnShowProducts = new JButton();
        btnShowProducts.setText("Show Products");
        products.add(btnShowProducts, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        products.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnAddProduct = new JButton();
        btnAddProduct.setText("Add Product");
        products.add(btnAddProduct, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnEditProduct = new JButton();
        btnEditProduct.setText("Edit Product");
        products.add(btnEditProduct, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDeleteProduct = new JButton();
        btnDeleteProduct.setText("Delete Product");
        products.add(btnDeleteProduct, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Name:");
        products.add(label4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Price:");
        products.add(label5, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Description:");
        products.add(label6, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Stock:");
        products.add(label7, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldProductName = new JTextField();
        products.add(textFieldProductName, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textFieldPrice = new JTextField();
        products.add(textFieldPrice, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textFieldDescription = new JTextField();
        products.add(textFieldDescription, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textFieldStock = new JTextField();
        products.add(textFieldStock, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        products.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableProducts = new JTable();
        scrollPane2.setViewportView(tableProducts);
        orders = new JPanel();
        orders.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
        bills.addTab("Orders", orders);
        final JLabel label8 = new JLabel();
        label8.setText("Select a client:");
        orders.add(label8, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        orders.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        comboBoxClients = new JComboBox();
        orders.add(comboBoxClients, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Select a product:");
        orders.add(label9, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxProducts = new JComboBox();
        orders.add(comboBoxProducts, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Quantity:");
        orders.add(label10, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldQuantity = new JTextField();
        orders.add(textFieldQuantity, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        btnAddCart = new JButton();
        btnAddCart.setText("Add to Cart");
        orders.add(btnAddCart, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 2, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        orders.add(scrollPane3, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableCart = new JTable();
        scrollPane3.setViewportView(tableCart);
        finalizeOrderButton = new JButton();
        finalizeOrderButton.setText("Finalize Order");
        orders.add(finalizeOrderButton, new com.intellij.uiDesigner.core.GridConstraints(4, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelTotal = new JLabel();
        labelTotal.setText("Total: 0 Lei");
        orders.add(labelTotal, new com.intellij.uiDesigner.core.GridConstraints(3, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        bills.addTab("Bills", panel3);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel3.add(scrollPane4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableBills = new JTable();
        scrollPane4.setViewportView(tableBills);
        btnShowBills = new JButton();
        btnShowBills.setText("Show Bills");
        panel3.add(btnShowBills, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
