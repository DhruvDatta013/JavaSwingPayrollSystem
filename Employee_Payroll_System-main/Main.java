import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Main {
    // UI Components
    private static JFrame frame;
    private static JTextField nameField, idField, salaryField, hoursField;
    private static JComboBox<String> typeCombo;
    private static JTextArea displayArea;
    private static PayRollSystem payrollSystem;

    // Constants
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 800;
    private static final int SECTION_PADDING = 15;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font DISPLAY_FONT = new Font("Monospaced", Font.PLAIN, 15);
    
    // Colors
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BUTTON_BG = new Color(220, 220, 220);  // Slightly darker gray for buttons
    private static final Color FIELD_BG = Color.WHITE;  // White background for text fields
    private static final int CORNER_RADIUS = 10;  // For rounded corners
    private static final int PADDING = 10;  // Reduced padding
    private static final int SPLASH_DURATION = 2000;  // 2 seconds for splash screen

    // Add this as a class field at the top of Main.java
    private static ArrayList<Employee> employeeList = new ArrayList<>();

    // Add file path constant
    private static final String DATA_FILE = "employees.dat";

    public static void main(String[] args) {
        loadEmployeeData();  // Load saved data
        SwingUtilities.invokeLater(() -> showSplashScreen());
    }

    private static void showSplashScreen() {
        JWindow splashScreen = new JWindow();
        try {
            // Load and scale the image
            ImageIcon originalIcon = new ImageIcon("on.jpg");
            Image scaledImage = originalIcon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            JLabel splashLabel = new JLabel(scaledIcon);
            splashLabel.setBackground(BG_COLOR);
            splashLabel.setOpaque(true);
            splashScreen.add(splashLabel);
            splashScreen.pack();
            splashScreen.setLocationRelativeTo(null);
            splashScreen.setVisible(true);
            
            // Use a single timer and ensure it's disposed properly
                Timer timer = new Timer(SPLASH_DURATION, e -> {
                    splashScreen.dispose();
                    if (frame == null) {  // Only create GUI if not already created
                        createAndShowGUI();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } catch (Exception e) {
                splashScreen.dispose();
                if (frame == null) {  // Only create GUI if not already created
                    createAndShowGUI();
                }
            }
        }

    private static void createAndShowGUI() { 
        setupLookAndFeel();
        payrollSystem = new PayRollSystem();
        
        frame = new JFrame("Payroll Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        frame.setSize(WIDTH, HEIGHT);
        
        // Add window listener to save data before closing
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveEmployeeData();
                System.exit(0);
            }
        });
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Add components with proper spacing
        gbc.gridy = 0;
        mainPanel.add(createTitleLabel(), gbc);

        gbc.gridy = 1;
        mainPanel.add(createInputPanel(), gbc);

        gbc.gridy = 2;
        mainPanel.add(createButtonPanel(), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(createDisplayPanel(), gbc);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createTitleLabel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Payroll Management System", JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_COLOR);
        
        // Add underline
        JPanel underline = new JPanel();
        underline.setBackground(TEXT_COLOR);
        underline.setPreferredSize(new Dimension(400, 2));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        headerPanel.add(title, gbc);
        
        gbc.gridy = 1;
        headerPanel.add(underline, gbc);
        
        return headerPanel;
    }

    private static JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                "Employee Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                HEADER_FONT,
                TEXT_COLOR
            ),
            BorderFactory.createEmptyBorder(SECTION_PADDING, SECTION_PADDING, SECTION_PADDING, SECTION_PADDING)
        ));

        // Create two columns for better organization
        JPanel leftPanel = createGroupPanel("Personal Details");
        JPanel rightPanel = createGroupPanel("Employment Details");

        // Add fields to left panel
        addLabelAndField(leftPanel, "Name:", nameField = createField(20));
        addLabelAndField(leftPanel, "ID:", idField = createField(10));

        // Add fields to right panel
        addLabelAndField(rightPanel, "Type:", typeCombo = createStyledComboBox());
        addLabelAndField(rightPanel, "Salary/Rate:", salaryField = createField(15));
        addLabelAndField(rightPanel, "Hours:", hoursField = createField(10));

        // Add panels to main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 10, 0, 10);

        gbc.gridx = 0;
        mainPanel.add(leftPanel, gbc);
        
        gbc.gridx = 1;
        mainPanel.add(rightPanel, gbc);

        return mainPanel;
    }

    private static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));  // Changed to 4 columns
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Actions",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADER_FONT,
            TEXT_COLOR
        ));

        // Add all four buttons
        panel.add(createButton("Add Employee", e -> addEmployee()));
        panel.add(createButton("Remove Employee", e -> removeEmployee()));
        panel.add(createButton("Display All", e -> displayEmployees()));  // Added back
        panel.add(createButton("Calculate Total", e -> calculateTotal()));

        return panel;
    }

    private static JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                "Employee Records",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                HEADER_FONT,
                TEXT_COLOR
            ),
            BorderFactory.createEmptyBorder(SECTION_PADDING, SECTION_PADDING, SECTION_PADDING, SECTION_PADDING)
        ));

        // Create display area with increased size
        displayArea = new JTextArea(15, 65);
        displayArea.setFont(DISPLAY_FONT);
        displayArea.setForeground(TEXT_COLOR);
        displayArea.setBackground(FIELD_BG);
        displayArea.setEditable(false);
        displayArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);

        return panel;
    }

    // Helper Methods
    private static JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(TEXT_COLOR),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LABEL_FONT,
            TEXT_COLOR
        ));
        return panel;
    }

    private static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private static JTextField createField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(LABEL_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(FIELD_BG);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        return field;
    }

    private static JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(BG_COLOR);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.addActionListener(action);
        return button;
    }

    private static void styleComboBox(JComboBox<String> combo) {
        combo.setFont(LABEL_FONT);
        combo.setForeground(TEXT_COLOR);
        combo.setBackground(BG_COLOR);
        combo.setPreferredSize(new Dimension(200, 35));
        combo.setBorder(new LineBorder(TEXT_COLOR, 1));
        
        UIManager.put("ComboBox.selectionBackground", TEXT_COLOR);
        UIManager.put("ComboBox.selectionForeground", BG_COLOR);
    }

    // Action Methods
    private static void addEmployee() {
        try {
            String name = nameField.getText().trim();
            String idText = idField.getText().replace(",", "").trim();
            String salaryText = salaryField.getText().replace(",", "").trim();
            
            // Validate inputs
            if (name.isEmpty() || idText.isEmpty() || salaryText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all required fields");
                return;
            }

            int id = Integer.parseInt(idText);
            double salary = Double.parseDouble(salaryText);
            
            // Validate positive numbers
            if (id <= 0) {
                JOptionPane.showMessageDialog(frame, "ID must be a positive number");
                return;
            }
            
            if (salary <= 0) {
                JOptionPane.showMessageDialog(frame, "Salary/Rate must be a positive number");
                return;
            }

            // For part-time employees, validate hours
            if (typeCombo.getSelectedItem().equals("Part Time")) {
                String hoursText = hoursField.getText().replace(",", "").trim();
                if (hoursText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter hours for part-time employee");
                    return;
                }
                int hours = Integer.parseInt(hoursText);
                if (hours <= 0) {
                    JOptionPane.showMessageDialog(frame, "Hours must be a positive number");
                    return;
                }
                if (hours > 168) { // Maximum hours in a week
                    JOptionPane.showMessageDialog(frame, "Hours cannot exceed 168 per week");
                    return;
                }
            }

            // Check duplicate ID
            if (employeeList.stream().anyMatch(emp -> emp.getId() == id)) {
                JOptionPane.showMessageDialog(frame, "Employee ID already exists!");
                return;
            }

            Employee emp;
            boolean isFullTime = typeCombo.getSelectedItem().equals("Full Time");
            
            if (isFullTime) {
                emp = new FullTimeEmployee(name, id, salary);
            } else {
                int hours = Integer.parseInt(hoursField.getText().trim());
                emp = new PartTimeEmployee(name, id, hours, salary);
            }

            employeeList.add(emp);
            clearFields();
            displayEmployees();
            saveEmployeeData();
            JOptionPane.showMessageDialog(frame, "Employee added successfully!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values");
        }
    }

    private static void removeEmployee() {
        try {
            String idText = JOptionPane.showInputDialog(frame, "Enter Employee ID to remove:");
            if (idText != null && !idText.trim().isEmpty()) {
                int id = Integer.parseInt(idText.replace(",", "").trim());
                
                boolean removed = employeeList.removeIf(emp -> emp.getId() == id);
                
                if (removed) {
                    saveEmployeeData();  // Save after removing
                    JOptionPane.showMessageDialog(frame, "Employee removed successfully!");
                    displayEmployees();
                } else {
                    JOptionPane.showMessageDialog(frame, "Employee ID not found!");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid ID number");
        }
    }

    private static void displayEmployees() {
        StringBuilder sb = new StringBuilder()
            .append("\n EMPLOYEE RECORDS\n")
            .append("================================================================\n")
            .append(String.format("%-10s %-20s %-15s %15s\n", "ID", "Name", "Type", "Amount"))
            .append("----------------------------------------------------------------\n");
        
        if (employeeList.isEmpty()) {
            sb.append("\n No employees found in the system.\n");
        } else {
            employeeList.forEach(emp -> sb.append(String.format("%-10d %-20s %-15s $%,14.2f\n",
                emp.getId(),
                emp.getName(),
                emp instanceof FullTimeEmployee ? "Full Time" : "Part Time",
                emp.calculateSalary())));
            
            sb.append("----------------------------------------------------------------\n")
              .append(String.format("%46s $%,14.2f\n", "Total Payroll:", calculateTotalPayroll()));
        }
        
        displayArea.setText(sb.toString());
        displayArea.setCaretPosition(0);
    }

    private static void calculateTotal() {
        double total = calculateTotalPayroll();
        String formattedTotal = String.format("Total Payroll: $%,.2f", total);
        JOptionPane.showMessageDialog(frame, formattedTotal);
    }

    private static void clearFields() {
        nameField.setText("");
        idField.setText("");
        salaryField.setText("");
        hoursField.setText("");
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.put("Panel.background", BG_COLOR);
            UIManager.put("OptionPane.background", BG_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
            UIManager.put("TextField.background", FIELD_BG);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("TextArea.background", FIELD_BG);
            UIManager.put("TextArea.foreground", TEXT_COLOR);
            UIManager.put("ComboBox.background", FIELD_BG);
            UIManager.put("ComboBox.foreground", TEXT_COLOR);
            UIManager.put("Label.foreground", TEXT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        return label;
    }

    // Helper method to create grouped panels
    private static JPanel createGroupPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEXT_COLOR),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADER_FONT,
            TEXT_COLOR
        ));
        return panel;
    }

    // Helper method to add label and field with consistent spacing
    private static void addLabelAndField(JPanel panel, String labelText, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panel.add(createStyledLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private static JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"Full Time", "Part Time"});
        combo.setFont(LABEL_FONT);
        combo.setForeground(TEXT_COLOR);
        combo.setBackground(FIELD_BG);
        combo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        combo.addActionListener(e -> typeCombo_Changed());
        return combo;
    }

    // Add this method to handle field visibility
    private static void typeCombo_Changed() {
        boolean isPartTime = typeCombo.getSelectedItem().equals("Part Time");
        hoursField.setEnabled(isPartTime);
        hoursField.setBackground(isPartTime ? FIELD_BG : Color.LIGHT_GRAY);
        if (!isPartTime) {
            hoursField.setText("");
        }
        // Update label text based on type
        salaryField.setToolTipText(isPartTime ? "Enter hourly rate" : "Enter annual salary");
    }

    // Add this method to initialize the hours field state
    private static void initializeComponents() {
        typeCombo_Changed(); // Set initial state of hours field
        
        // Add document listeners to format numbers with commas
        idField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formatNumberField(idField);
            }
        });
        
        salaryField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formatNumberField(salaryField);
            }
        });
    }

    // Add this helper method to format numbers with commas
    private static void formatNumberField(JTextField field) {
        String text = field.getText().replaceAll("[^\\d.]", "");
        if (!text.isEmpty()) {
            try {
                double number = Double.parseDouble(text);
                field.setText(String.format("%,.2f", number));
            } catch (NumberFormatException e) {
                // Ignore formatting if not a valid number
            }
        }
    }

    // Add this method to calculate total payroll
    private static double calculateTotalPayroll() {
        return employeeList.stream()
                          .mapToDouble(Employee::calculateSalary)
                          .sum();
    }

    // Add these methods for data persistence
    private static void saveEmployeeData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(employeeList);
            System.out.println("Employee data saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving employee data: " + e.getMessage());
        }
    }

    private static void loadEmployeeData() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            employeeList = (ArrayList<Employee>) ois.readObject();
            System.out.println("Employee data loaded successfully");
        } catch (FileNotFoundException e) {
            employeeList = new ArrayList<>();  // Create new list if file doesn't exist
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading employee data: " + e.getMessage());
            employeeList = new ArrayList<>();
        }
    }

    // Add this method to validate input as user types
    private static void addInputValidation() {
        // Validate salary/rate field
        salaryField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = salaryField.getText().replace(",", "").trim();
                if (!text.isEmpty()) {
                    try {
                        double value = Double.parseDouble(text);
                        if (value < 0) {
                            salaryField.setForeground(Color.RED);
                        } else {
                            salaryField.setForeground(TEXT_COLOR);
                        }
                    } catch (NumberFormatException e) {
                        salaryField.setForeground(Color.RED);
                    }
                }
            }
        });

        // Validate hours field
        hoursField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = hoursField.getText().replace(",", "").trim();
                if (!text.isEmpty()) {
                    try {
                        int value = Integer.parseInt(text);
                        if (value < 0 || value > 168) {
                            hoursField.setForeground(Color.RED);
                        } else {
                            hoursField.setForeground(TEXT_COLOR);
                        }
                    } catch (NumberFormatException e) {
                        hoursField.setForeground(Color.RED);
                    }
                }
            }
        });
    }
}