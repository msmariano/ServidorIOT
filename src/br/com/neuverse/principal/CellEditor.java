package br.com.neuverse.principal;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import br.com.neuverse.enumerador.Status;

/**
 * A custom editor for cells in the Country column.
 * 
 * @author www.codejava.net
 *
 */
public class CellEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

    private Object obj;

    @Override
    public Object getCellEditorValue() {
        return this.obj;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (column == 4 || column == 5 || column == 6) {
            JTableButtonModel modelo = (JTableButtonModel) table.getModel();
            this.obj = modelo.getValueAt(row, column);
            if (!(this.obj instanceof JComboBox)) {
                JComboBox<Status> cb = new JComboBox<Status>();
                cb.addActionListener(this);
                for (Status st : Status.values()) {
                    cb.addItem(st);
                }
                modelo.insereCelula(cb, row, column);
                modelo.fireTableDataChanged();
                this.obj = cb;
                return (Component) cb;
            }
            if (this.obj instanceof Component) {
                return (Component) this.obj;
            }
        } else if (column == 0 ||column == 1 || column == 2 || column == 3) {
            JTableButtonModel modelo = (JTableButtonModel) table.getModel();
            this.obj = modelo.getValueAt(row, column);
            if (!(this.obj instanceof JTextField)) {
                JTextField tf = new JTextField("1");
                this.obj = tf;
                modelo.insereCelula(tf, row, column);
                modelo.fireTableDataChanged();
                return (Component) tf;
            }
            if (this.obj instanceof Component) {
                return (Component) this.obj;
            }

        }
        this.obj = null;

        return null;

    }

    @Override
    public void actionPerformed(ActionEvent event) {

    }

}