package com.abarreyapp;

import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilidades de búsqueda sobre TableModel para separar la lógica de la UI.
 */
public class SearchUtils {
    /**
     * Devuelve el conjunto de índices de fila (modelo) que contienen el texto (insensible a mayúsculas).
     */
    public static Set<Integer> findMatchingModelRows(DefaultTableModel model, String text) {
        Set<Integer> res = new HashSet<>();
        if (text == null) return res;
        String q = text.trim().toLowerCase();
        if (q.isEmpty()) return res;
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount() - 1; j++) {
                Object v = model.getValueAt(i, j);
                if (v != null && v.toString().toLowerCase().contains(q)) {
                    res.add(i);
                    break;
                }
            }
        }
        return res;
    }
}
