package org.example.presentation;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Generates a table using reflexion technique.
 */

public class TableGenerator {
    public static <T> DefaultTableModel createTableFromData(List<T> data) {
        if (data == null || data.isEmpty()) {
            return new DefaultTableModel(new Object[]{"Messsage"}, 0);
        }
        Class<?> type = data.get(0).getClass();
        Field[] fields = type.getDeclaredFields();

        String[] columnNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            columnNames[i] = fields[i].getName().toUpperCase();
        }

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (T item : data) {
            Object[] rowData = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true);
                    rowData[i] = fields[i].get(item);
                } catch (IllegalAccessException e) {
                    rowData[i] = "";
                }
            }
            model.addRow(rowData);
        }

        return model;
    }
}
