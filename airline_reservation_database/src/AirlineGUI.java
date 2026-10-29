import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class AirlineGUI extends JFrame {

    private Connection conn;
    private JTable tableResults;

    // ComboBox-uri
    private JComboBox<String> comboTables;
    private JComboBox<String> comboQueries;
    private JComboBox<String> comboProceduri;

    // TextField pentru input funcții/proceduri
    private JTextField inputField;

    // Map query-uri
    private Map<String, String> queryMap = new LinkedHashMap<>();
    // Relative to the project root (the working directory when the app is started from the IDE)
    private static final String SCRIPT_PATH = "sql/airline_reservation.sql";

    public AirlineGUI() {
        setTitle("Airline Database GUI");
        setSize(1300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Conectare DB
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare conectare DB: " + e.getMessage());
            System.exit(1);
        }

        // Citește query-urile din script
        loadQueriesFromScript();

        // Tab-uri separate
        JTabbedPane tabs = new JTabbedPane();

        // ==================== Tab Tabele ====================
        JPanel panelTabele = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        comboTables = new JComboBox<>(new String[]{"pilot", "zbor", "pasager", "rezervare", "detaliu_rezervare"});
        JButton btnShowTable = new JButton("Afișează tabel");
        panelTabele.add(new JLabel("Selectează tabel:"));
        panelTabele.add(comboTables);
        panelTabele.add(btnShowTable);
        tabs.addTab("Tabele", panelTabele);

        // ==================== Tab Query-uri ====================
        JPanel panelQuery = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        comboQueries = new JComboBox<>(queryMap.keySet().toArray(new String[0]));
        JButton btnRunQuery = new JButton("Execută query");
        panelQuery.add(new JLabel("Selectează query:"));
        panelQuery.add(comboQueries);
        panelQuery.add(btnRunQuery);
        tabs.addTab("Query-uri", panelQuery);

        // ==================== Tab Funcții/Proceduri ====================
        JPanel panelProcFunc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        comboProceduri = new JComboBox<>(new String[]{"marcheaza_zbor", "calc_reduceri"});
        inputField = new JTextField(8);
        JButton btnRunProcFunc = new JButton("Execută funcție/procedură");
        panelProcFunc.add(new JLabel("Selectează funcție/procedură:"));
        panelProcFunc.add(comboProceduri);
        panelProcFunc.add(new JLabel("Input ID:"));
        panelProcFunc.add(inputField);
        panelProcFunc.add(btnRunProcFunc);
        tabs.addTab("Funcții/Proceduri", panelProcFunc);

        add(tabs, BorderLayout.NORTH);

        // JTable central
        tableResults = new JTable();
        JScrollPane scroll = new JScrollPane(tableResults);
        add(scroll, BorderLayout.CENTER);

        // ==================== LISTENERS ====================
        btnShowTable.addActionListener(e -> showTable());
        btnRunQuery.addActionListener(e -> runQuery());
        btnRunProcFunc.addActionListener(e -> executaProceduraSauFunctie());

        setVisible(true);
    }

    // ==================== Citire query-uri din script ====================
    private void loadQueriesFromScript() {
        try (BufferedReader br = new BufferedReader(new FileReader(SCRIPT_PATH))) {
            String line;
            String currentName = null;
            StringBuilder currentQuery = new StringBuilder();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--Query:")) {
                    currentName = line.replace("--Query:", "").trim();
                    currentQuery = new StringBuilder();
                } else if (currentName != null) {
                    if (!line.startsWith("--")) {
                        currentQuery.append(line).append(" ");
                    }
                    if (line.endsWith(";")) {
                        queryMap.put(currentName, currentQuery.toString());
                        currentName = null;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Eroare citire script SQL: " + e.getMessage());
        }
    }

    // ==================== Afisare tabel ====================
    private void showTable() {
        String table = (String) comboTables.getSelectedItem();
        if (table == null) return;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {
            tableResults.setModel(buildTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare afișare tabel: " + e.getMessage());
        }
    }

    // ==================== Executare query-uri ====================
    private void runQuery() {
        String key = (String) comboQueries.getSelectedItem();
        String sql = queryMap.get(key);
        if (sql == null) return;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            tableResults.setModel(buildTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare query: " + e.getMessage());
        }
    }

    // ==================== Executare Funcții / Proceduri ====================
    private void executaProceduraSauFunctie() {
        String selectie = (String) comboProceduri.getSelectedItem();
        String input = inputField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introdu un ID în câmpul Input!");
            return;
        }

        try {
            int id = Integer.parseInt(input);

            if ("marcheaza_zbor".equals(selectie)) {
                // FUNCTION PostgreSQL → folosim SELECT
                String sql = "SELECT marcheaza_zbor(?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.execute();
                    JOptionPane.showMessageDialog(this, "Zbor marcat ca indisponibil!");
                }

            } else if ("calc_reduceri".equals(selectie)) {
                // FUNCTION PostgreSQL care returnează valoare → folosim SELECT
                String sql = "SELECT calc_reduceri(?) AS total";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            double totalReduceri = rs.getDouble("total");
                            JOptionPane.showMessageDialog(this,
                                    "Total reduceri pentru rezervarea " + id + ": " + totalReduceri + " RON");
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Nu s-au găsit date pentru această rezervare!");
                        }
                    }
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID-ul trebuie să fie numeric!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }




    // ==================== JTable Model ====================
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Vector<String> columns = new Vector<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columns.add(meta.getColumnName(i));
        }
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }
        return new DefaultTableModel(data, columns);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AirlineGUI::new);
    }
}
