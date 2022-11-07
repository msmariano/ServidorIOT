package br.com.neuverse.principal;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import br.com.neuverse.enumerador.Status;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JTableButtonModel extends AbstractTableModel implements TableCellEditor, ActionListener {
    private List<Object[]> rows = new ArrayList<>();

    public List<Object[]> getRows() {
        return rows;
    }

    Integer inicial = 1;

    public JTableButtonModel() {
        //newRecord();
    }

    private String[] columns = { "ID", "PinIN", "PinOUT", "Nick", "Funcao", "Status", "Tecla", "" };

    public String getColumnName(int column) {
        return columns[column];
    }
    public void newRecordData(String nick,Status stbF,Status stbS,Status stbt,String id,String in,String out ) {
        JButton jb = new JButton("Ok");
        jb.addActionListener(this);
        JComboBox<Status> jcbF = new JComboBox<Status>(Status.values());
        jcbF.setSelectedItem(stbF);
        JComboBox<Status> jcbS = new JComboBox<Status>(Status.values());
        jcbS.setSelectedItem(stbS);
        JComboBox<Status> jcbT = new JComboBox<Status>(Status.values());
        jcbT.setSelectedItem(stbt);

        Object[] rowl = { new JTextField(id), 
                new JTextField(in), 
                new JTextField(out), 
                new JTextField(nick),
                jcbF,
                jcbS, 
                jcbT, 
                jb };
        rows.add(rowl);
        inicial++;
    }
  
    public void newRecord() {
        JButton jb = new JButton("Ok");
        jb.addActionListener(this);
        JComboBox<Status> jcbF = new JComboBox<Status>(Status.values());
        jcbF.setSelectedItem(Status.KEY);
        JComboBox<Status> jcbS = new JComboBox<Status>(Status.values());
        jcbS.setSelectedItem(Status.OFF);
        JComboBox<Status> jcbT = new JComboBox<Status>(Status.values());
        jcbT.setSelectedItem(Status.LOW);

        Object[] rowl = { new JTextField("1"), 
                new JTextField("1"), 
                new JTextField("1"), 
                new JTextField("BT"+inicial),
                jcbF,
                jcbS, 
                jcbT, 
                jb };
        rows.add(rowl);
        inicial++;
    }

    public void add(Object[] rowl) {
        rows.add(rowl);
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public void setValueAt(Object obj, int row, int column) {
        Object rowAl[] = rows.get(row);
        rowAl[column] = obj;
    }

    public void insereCelula(Object obj, int row, int column) {
        Object rowAl[] = rows.get(row);
        rowAl[column] = obj;
    }

    public Object getValueAt(int row, int column) {
        Object rowl[] = rows.get(row);
        return rowl[column];// rows[row][column];
    }

    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void cancelCellEditing() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (column == 1) {
            JComboBox<Status> cb = new JComboBox<Status>();
            cb.addActionListener(this);

            for (Status st : Status.values()) {
                cb.addItem(st);
            }

            return cb;
        }

        return null;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Clicked !");

    }   
}