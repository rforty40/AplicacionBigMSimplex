
package controlador;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author FORTY
 */
public abstract class EditorCeldas extends JLabel implements TableCellRenderer {

    int Row, Columns;

    public void setRow(int Row) {
        this.Row = Row;
    }

    public void setColumns(int Columns) {
        this.Columns = Columns;
    }

    public EditorCeldas() {
        setOpaque(true); // Permite que se vea el color en la celda del JLabel
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if ((row == Row) && (column == Columns)) {
            setBackground(Color.BLACK); // Una condicion arbitraria solo para pintar el JLabel que esta en la celda.
            // setForeground(new Color(0,153,153));
            setFont(new  Font("Kameron", Font.BOLD, 15));
            setText(String.valueOf(value)); // Se agrega el valor que viene por defecto en la celda
        }
        if ((row != Row) && (column == Columns)) {
            setBackground(new Color(0,153,153)); // Una condicion arbitraria solo para pintar el JLabel que esta en la celda.
            setText(String.valueOf(value)); // Se agrega el valor que viene por defecto en la celda
        }

        return this;
    }

}
