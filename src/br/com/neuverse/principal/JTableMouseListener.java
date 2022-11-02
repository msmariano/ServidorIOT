package br.com.neuverse.principal;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JTableMouseListener extends MouseAdapter {
    private final JTable table;

    public JTableMouseListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        int row = e.getY() / table.getRowHeight();
        System.out.println("Col :" + column + "row:" + row);

        if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
            Object value = table.getValueAt(row, column);
            if(value!=null){
            //System.out.println("Value :" + value.getClass().getName());
            if (value instanceof JButton) {
                ((JButton) value).doClick();
            }
            else if (value instanceof JList){
                 ((JList) value).doLayout();
            }
        }
            

        }
    }

}
