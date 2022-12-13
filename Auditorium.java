package laba9;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Auditorium extends JPanel implements ActionListener {
    private final int width_window = 600;
    private final int delta_size_dialog = 20;
    private static JFrame mainFrame = null;

    private static final String dbURL = "jdbc:derby://localhost:1527/DB ";
    private static final String user_name = "artur";
    private static final String user_password = "artur";
    private static Connection conn = null;
    private static ResultSet rs = null;
    private static Statement stmt = null;
    private static String SQL = null;
    
    private JPanel panelControl, panelShowAuditorium, panelShowManager;
    private JButton buttonShow;
    private JButton buttonCreate;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private JButton buttonFillTable;
    private JButton buttonLeksOrder;
    private JButton buttonFunction1;
    private JButton buttonFunction2;
    private static DefaultTableModel tableShowModelAuditorium, tableShowModelManager;
    private JTable tableShowAuditorium, tableShowManager;
    private String[][] DataSetAuditorium = 
    { {"7", "333", "Компьютерная", "60", "Смирнов А.А."},
        {"7", "419", "Лекционная", "150", "Иванова С.С"},
        {"2", "111", "Преподавательская", "100", "Петров Х.Х."} };
    private String[][] DataSetManager = 
    { {"Преподаватель", "89999999999", "40", "Смирнов А.А."},
            {"Заведующий", "81234567890", "50", "Иванова С.С"},
            {"Лаборант", "8000000000", "24", "Петров Х.Х."} };

    public Auditorium() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        //Создание панели "Управление".
        panelControl = new JPanel();
        panelControl.setPreferredSize(new Dimension(width_window, 100));
        panelControl.setBorder(BorderFactory.createTitledBorder("\"Управление\""));
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ 10 пикселей
        panelControl.setLayout(new FlowLayout());
        buttonShow = new JButton("Просмотреть");
        buttonShow.addActionListener(this);
        buttonCreate = new JButton("Создать");
        buttonCreate.addActionListener(this);
        buttonEdit = new JButton("Редактировать");
        buttonEdit.addActionListener(this);
        buttonDelete = new JButton("Удалить");
        buttonDelete.addActionListener(this);
        buttonFillTable = new JButton("Сброс и заполнение");
        buttonFillTable.addActionListener(this);
        buttonLeksOrder = new JButton("Вывести в лексикограф. порядке");
        buttonLeksOrder.addActionListener(this);
        buttonFunction1 = new JButton("Функция 1");
        buttonFunction1.addActionListener(this);
        buttonFunction2 = new JButton("Функция 2");
        buttonFunction2.addActionListener(this);
        panelControl.add(buttonShow);
        panelControl.add(buttonCreate);
        panelControl.add(buttonEdit);
        panelControl.add(buttonDelete);
        panelControl.add(buttonFillTable);
        panelControl.add(buttonLeksOrder);
        panelControl.add(buttonFunction1);
        panelControl.add(buttonFunction2);
        add(panelControl);

        //Создание панели "Список контактов".
        panelShowAuditorium = new JPanel();
        panelShowAuditorium.setPreferredSize(new Dimension(width_window, 200));
        panelShowAuditorium.setLayout(new BoxLayout(panelShowAuditorium, BoxLayout.Y_AXIS));
        panelShowAuditorium.setBorder(BorderFactory.createTitledBorder("\"Список аудиторий\""));
        
        panelShowManager = new JPanel();
        panelShowManager.setPreferredSize(new Dimension(width_window, 200));
        panelShowManager.setLayout(new BoxLayout(panelShowManager, BoxLayout.Y_AXIS));
        panelShowManager.setBorder(BorderFactory.createTitledBorder("\"Список ответсвенных\""));
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ сверху вниз на 10 пикселей

        tableShowModelAuditorium = new DefaultTableModel(new Object[]{"Номер здания", "Номер аудитории", "Ответственный"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        tableShowAuditorium = new JTable();
        tableShowAuditorium.setModel(tableShowModelAuditorium);
        panelShowAuditorium.add(new JScrollPane(tableShowAuditorium));
        add(panelShowAuditorium);
        
        tableShowModelManager = new DefaultTableModel(new Object[]{"ФИО", "Должность", "Номер телефона"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        tableShowManager = new JTable();
        tableShowManager.setModel(tableShowModelManager);
        panelShowManager.add(new JScrollPane(tableShowManager));
        add(panelShowManager);

        try {
            conn = DriverManager.getConnection(dbURL, user_name, user_password);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Учебные аудитории");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame = frame;
        JComponent componentPanelAddressBook = new Auditorium();
        frame.setLocation(300, 200);
        frame.setContentPane(componentPanelAddressBook);
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                findByString("");
            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int dataToSize = 11;
        String[] dataTo = new String[dataToSize];
        for (int i = 0; i < dataToSize; i++) {
            dataTo[i] = "";
        }

        if ("Создать".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame, "Новая аудитория...", JDialog.DEFAULT_MODALITY_TYPE);

            PanelContact panelContact = new PanelContact(command, dataTo);
            dialogContact.setBounds(
            delta_size_dialog, delta_size_dialog,
            panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
            panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.setLocation(1000, 200);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);
            findByString("");
        }

        try {
            if (("Редактировать".equals(command) || "Просмотреть".equals(command))
            && rs != null) {
                rs.first();
                do {
                    String title = "";
                    if (tableShowAuditorium.getSelectedRow() > -1 && rs.getString("MANAGER").
                    equals(tableShowModelAuditorium.getValueAt(tableShowAuditorium.getSelectedRow(), 2).toString()) &&
                    rs.getString("AUDITORIUM_NUMBER").
                    equals(tableShowModelAuditorium.getValueAt(tableShowAuditorium.getSelectedRow(), 1).toString()) && 
                    rs.getString("MANAGER").equals(rs.getString("FIO"))){
                        dataTo[0] = rs.getString("ID_AUDITORIUM");
                        dataTo[1] = rs.getString("AUDITORIUM_BUILDING");
                        dataTo[2] = rs.getString("AUDITORIUM_NUMBER");
                        dataTo[3] = rs.getString("AUDITORIUM_NAME");
                        dataTo[4] = rs.getString("AUDITORIUM_SQUARE");
                        dataTo[5] = rs.getString("MANAGER");
                        if ("Редактировать".equals(command)) {
                            title = "Изменить аудиторию...";
                            command = "Редактировать аудиторию";
                        }
                        if ("Просмотреть".equals(command)) {
                            title = "Просмотреть аудиторию...";
                        }
                        JDialog dialogContact = new JDialog(mainFrame, title, JDialog.DEFAULT_MODALITY_TYPE);
                        PanelContact panelContact = new PanelContact(command, dataTo);
                        dialogContact.setBounds(
                        delta_size_dialog, delta_size_dialog,
                        panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
                        panelContact.getContactPanelHeight() + delta_size_dialog);
                        dialogContact.setLocation(1000, 200);
                        dialogContact.add(panelContact);
                        dialogContact.setVisible(true);
                        findByString("");
                        break;
                    }
                        
                    if (tableShowManager.getSelectedRow() > -1 && rs.getString("FIO").
                    equals(tableShowModelManager.getValueAt(tableShowManager.getSelectedRow(), 0).toString()) &&
                    rs.getString("MANAGER").equals(rs.getString("FIO"))){
                        dataTo[5] = rs.getString("MANAGER");
                        dataTo[6] = rs.getString("MANAGER_ID");
                        dataTo[7] = rs.getString("FIO");
                        dataTo[8] = rs.getString("MANAGER_POSITION");
                        dataTo[9] = rs.getString("MANAGER_PHONENUMBER");
                        dataTo[10] = rs.getString("MANAGER_AGE");
                        if ("Редактировать".equals(command)) {
                            title = "Изменить менеджера...";
                            command = "Редактировать менеджера";
                        }
                        if ("Просмотреть".equals(command)) {
                            title = "Просмотреть менеджера...";
                        }
                        JDialog dialogContact = new JDialog(mainFrame, title, JDialog.DEFAULT_MODALITY_TYPE);
                        PanelContact panelContact = new PanelContact(command, dataTo);
                        dialogContact.setBounds(
                        delta_size_dialog, delta_size_dialog,
                        panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
                        panelContact.getContactPanelHeight() + delta_size_dialog);
                        dialogContact.setLocation(1000, 200);
                        dialogContact.add(panelContact);
                        dialogContact.setVisible(true);
                        findByString("");
                        break;
                    }
                    
                    
                } while (rs.next());
            }
        } catch (SQLException err1) {
            System.out.println(err1.getMessage());
        }

        try {
            if ("Удалить".equals(command) && rs != null &&
            (tableShowAuditorium.getSelectedRow() > -1 || tableShowManager.getSelectedRow() > -1)) {
                rs.first();
                do {
                    if (tableShowAuditorium.getSelectedRow() > -1 && rs.getString("FIO").
                    equals(tableShowModelAuditorium.getValueAt(tableShowAuditorium.getSelectedRow(), 2).toString()) &&
                    rs.getString("AUDITORIUM_NUMBER").
                    equals(tableShowModelAuditorium.getValueAt(tableShowAuditorium.getSelectedRow(), 1).toString())){
                        
                        String SQL_UpdateAuditoriums = "DELETE FROM AUDITORIUM WHERE MANAGER = '"
                        + rs.getString("FIO") + "'" + " AND AUDITORIUM_NUMBER = " + rs.getString("AUDITORIUM_NUMBER");
                        PreparedStatement updateAuditorium = null;
                        conn.setAutoCommit(false);
                        updateAuditorium = conn.prepareStatement(SQL_UpdateAuditoriums);
                        int executeUpdate = updateAuditorium.executeUpdate();
                        conn.commit();

                        if (updateAuditorium != null) updateAuditorium.close();
                        conn.setAutoCommit(true);
                        findByString("");
                        break;
                    }
                    
                    if (tableShowManager.getSelectedRow() > -1 && rs.getString("FIO").
                    equals(tableShowModelManager.getValueAt(tableShowManager.getSelectedRow(), 0).toString())){
                        
                        String SQL_UpdateAuditoriums = "DELETE FROM MANAGER WHERE FIO = '"
                        + rs.getString("FIO") + "'";
                        PreparedStatement updateAuditorium = null;
                        conn.setAutoCommit(false);
                        updateAuditorium = conn.prepareStatement(SQL_UpdateAuditoriums);
                        int executeUpdate = updateAuditorium.executeUpdate();
                        conn.commit();

                        if (updateAuditorium != null) updateAuditorium.close();
                        conn.setAutoCommit(true);
                        findByString("");
                        break;
                    }
                } while (rs.next());
            }
        } catch (SQLException err1) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this,
                "Транзакция на удаление не выполнена.\n"
                + "Смотрите сообщения в консоли.");
                System.out.println(err1.getMessage());
            } catch (SQLException err2) {
                System.out.println(err2.getMessage());
            }
        }
        
        try {
            if ("Сброс и заполнение".equals(command)) {
                String SQL_DeleteAllManager = "DELETE FROM MANAGER";
                String SQL_DeleteAllAuditorium = "DELETE FROM AUDITORIUM";
                PreparedStatement deleteManagers = null;
                PreparedStatement deleteAuditoriums = null;
                conn.setAutoCommit(false);
                deleteManagers = conn.prepareStatement(SQL_DeleteAllManager);
                deleteAuditoriums = conn.prepareStatement(SQL_DeleteAllAuditorium);
                int executeUpdate = deleteManagers.executeUpdate();
                executeUpdate = deleteAuditoriums.executeUpdate();
                conn.commit();
                
                String SQL_InsertData = "";
                for (int i = 0; i < DataSetAuditorium.length; i++){
                    SQL_InsertData = 
                        "INSERT INTO AUDITORIUM (ID_AUDITORIUM, AUDITORIUM_BUILDING, AUDITORIUM_NUMBER,"
                        + "AUDITORIUM_NAME, AUDITORIUM_SQUARE, MANAGER) "
                        + "VALUES (" + (i + 1) + ", "
                        + Integer.parseInt(DataSetAuditorium[i][0]) + ", "
                        + Integer.parseInt(DataSetAuditorium[i][1]) + ", "
                        + "'" + DataSetAuditorium[i][2] + "', "
                        + Integer.parseInt(DataSetAuditorium[i][3]) + ", "
                        + "'" + DataSetAuditorium[i][4] + "')";
                    
                    PreparedStatement createAuditorium = null;
                    conn.setAutoCommit(false);
                    createAuditorium = conn.prepareStatement(SQL_InsertData);
                    executeUpdate = createAuditorium.executeUpdate();
                    conn.commit();
                }
                
                for (int i = 0; i < DataSetAuditorium.length; i++){
                    SQL_InsertData = 
                        "INSERT INTO MANAGER (MANAGER_ID, MANAGER_POSITION, MANAGER_PHONENUMBER, MANAGER_AGE, FIO) "
                        + "VALUES (" + (i + 1) + ", "
                        + "'" + DataSetManager[i][0] + "', "
                        + "'" + DataSetManager[i][1] + "', "
                        + Integer.parseInt(DataSetManager[i][2]) + ", "
                        + "'" + DataSetManager[i][3] + "')";
                    
                    PreparedStatement createManager = null;
                    conn.setAutoCommit(false);
                    createManager = conn.prepareStatement(SQL_InsertData);
                    executeUpdate = createManager.executeUpdate();
                    conn.commit();
                }
                
                if (deleteManagers != null) deleteManagers.close();
                if (deleteAuditoriums != null) deleteAuditoriums.close();
                conn.setAutoCommit(true);
                findByString("");
            }
        } catch (SQLException err1) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this,
                "Транзакция на удаление не выполнена.\n"
                + "Смотрите сообщения в консоли.");
                System.out.println(err1.getMessage());
            } catch (SQLException err2) {
                System.out.println(err2.getMessage());
            }
        }
        
        try {
            if ("Вывести в лексикограф. порядке".equals(command)) {
                String SQL_LeksSelect = "SELECT * FROM AUDITORIUM ORDER BY AUDITORIUM_BUILDING";
                rs = stmt.executeQuery(SQL_LeksSelect);
                while(rs.next()) {
                    System.out.println(rs.getString("AUDITORIUM_BUILDING"));
                }
            }
        } catch (SQLException err) {
                System.out.println(err.getMessage());
        }
        
        try {
            rs.first();
            Boolean flag = false;
            String message = "Список аудиторий в здании №";
            do{
                if ("Функция 1".equals(command) && rs != null && tableShowAuditorium.getSelectedRow() > -1){
                    if (rs.getString("AUDITORIUM_BUILDING").
                    equals(tableShowModelAuditorium.getValueAt(tableShowAuditorium.getSelectedRow(), 0).toString())){
                        flag = true;
                        String date = rs.getString("AUDITORIUM_BUILDING");
                        String SQL_Select = "SELECT AUDITORIUM_NUMBER FROM AUDITORIUM WHERE "
                                + "AUDITORIUM.AUDITORIUM_BUILDING = " + date;
                        ResultSet result = stmt.executeQuery(SQL_Select);
                        
                        message += date;
                        while(result.next()){
                            message += "\n";
                            message += result.getString("AUDITORIUM_NUMBER");
                        }
                        findByString("");
                        break;
                    }
                }
            }
            while(rs.next());
            if (flag) JOptionPane.showMessageDialog(this, message);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
        
        try {
            rs.first();
            Boolean flag = false;
            String message = "Общая площадь аудиторий, закреплённых за ";
            do{
                if ("Функция 2".equals(command) && rs != null && tableShowManager.getSelectedRow() > -1) {
                    if (rs.getString("FIO").
                    equals(tableShowModelManager.getValueAt(tableShowManager.getSelectedRow(), 0).toString())){
                        flag = true;
                        String date = rs.getString("FIO");
                        String SQL_Select = "SELECT SUM(AUDITORIUM_SQUARE) AS SUMMA FROM AUDITORIUM WHERE "
                                + "MANAGER = '" + rs.getString("FIO") + "'";
                        ResultSet result = stmt.executeQuery(SQL_Select);
                        message += date;
                        while(result.next()){
                            message += " = " + result.getString("SUMMA");
                        }
                        findByString("");
                        break;
                    }
                }
            }
            while(rs.next());
            if (flag) JOptionPane.showMessageDialog(this, message);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }
    
    
    private static void findByString(String textFind) {
        try {
            while(tableShowModelAuditorium.getRowCount() > 0) {
                tableShowModelAuditorium.removeRow(0);
            }
            SQL =
            "SELECT AUDITORIUM.*, MANAGER.* "
            + "FROM AUDITORIUM, MANAGER "
            + "WHERE AUDITORIUM.MANAGER = MANAGER.FIO "
            + "AND MANAGER.FIO LIKE '"
            + textFind + "%'";
            rs = stmt.executeQuery(SQL);
            while(rs.next()) {
                String building_number = rs.getString("AUDITORIUM_BUILDING");
                String auditorium_number = rs.getString("AUDITORIUM_NUMBER");
                String fio = rs.getString("MANAGER");
                tableShowModelAuditorium.addRow(new Object[]{building_number, auditorium_number, fio});
            }
            
            while(tableShowModelManager.getRowCount() > 0) {
                tableShowModelManager.removeRow(0);
            }
            SQL =
            "SELECT * FROM MANAGER ";
            rs = stmt.executeQuery(SQL);
            while(rs.next()) {
                String fio = rs.getString("FIO");
                String position = rs.getString("MANAGER_POSITION");
                String phonenumber = rs.getString("MANAGER_PHONENUMBER");
                
                tableShowModelManager.addRow(new Object[]{fio, position, phonenumber});
            }
            SQL =
             "SELECT AUDITORIUM.*, MANAGER.* "
            + "FROM AUDITORIUM, MANAGER ";
            rs = stmt.executeQuery(SQL);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    class PanelContact extends JPanel implements ActionListener {
        private final int width_window = 600;//Кратно трём
        private final int height_window = 400;
        private final int height_button_panel = 40;
        private final int height_gap = 3;
        private final String mode;
        private final String dataTo[];

        private JPanel panelUp, panelLabel, panelText, panelButton;
        private JComboBox CB_Manager;
        private JButton buttonAuditorium;
        private JButton buttonManager;
        
        private JTextField txtBuildingNumber;
        private JTextField txtAuditoriumNumber;
        private JTextField txtAuditoriumName;
        private JTextField txtAuditoriumSquare;
        private JTextField txtFIO;
        private JTextField txtPosition;
        private JTextField txtPhoneNumber;
        private JTextField txtAge;
        
        public PanelContact(String mode, String[] dataTo) {
            super();
            this.mode = mode;
            this.dataTo = dataTo;
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setPreferredSize(new Dimension(width_window, height_window));
            Object[] managers = new ArrayList<String>(getManagers()).toArray();
            
            panelUp = new JPanel();//Панель для размещения панелей
            panelLabel = new JPanel();
            panelText = new JPanel();
            panelButton = new JPanel();
            
            CB_Manager = new JComboBox(managers);
            txtBuildingNumber = new JTextField(dataTo[1]);
            txtAuditoriumNumber = new JTextField(dataTo[2]);
            txtAuditoriumName = new JTextField(dataTo[3]);
            txtAuditoriumSquare = new JTextField(dataTo[4]);
            
            txtFIO = new JTextField(dataTo[7]);
            txtPosition = new JTextField(dataTo[8]);
            txtPhoneNumber = new JTextField(dataTo[9]);
            txtAge = new JTextField(dataTo[10]);
            
            buttonAuditorium = new JButton("Аудитория");
            buttonAuditorium.addActionListener(this);
            buttonManager = new JButton("Менеджер");
            buttonManager.addActionListener(this);
            
            panelUp.setPreferredSize(new Dimension(width_window, height_window - height_button_panel - height_gap));
            panelUp.setBorder(BorderFactory.createBevelBorder(1));
            add(panelUp);
            panelUp.setLayout(new BoxLayout(panelUp, BoxLayout.LINE_AXIS));
            
            
            panelLabel.setPreferredSize(new Dimension(width_window/3, height_window - height_button_panel - height_gap));
            panelLabel.setBorder(BorderFactory.createBevelBorder(1));
            panelLabel.setLayout(new GridLayout(10,1));
            panelLabel.add(new JLabel("Учебное здание"));
            panelLabel.add(new JLabel("Номер аудитории"));
            panelLabel.add(new JLabel("Наименование аудитории"));
            panelLabel.add(new JLabel("Площадь аудитории"));
            panelLabel.add(new JLabel("Ответственный"));
            panelLabel.add(new JLabel(""));
            
            panelLabel.add(new JLabel("ФИО ответсвтенного"));
            panelLabel.add(new JLabel("Должность ответственного"));
            panelLabel.add(new JLabel("Номер телефона ответственного"));
            panelLabel.add(new JLabel("Возраст ответственного"));
            
            panelText.setPreferredSize(new Dimension(2*width_window/3, height_window - height_button_panel - height_gap));
            panelText.setBorder(BorderFactory.createBevelBorder(1));
            panelText.setLayout(new GridLayout(10,1));
            panelText.add(txtBuildingNumber);
            panelText.add(txtAuditoriumNumber);
            panelText.add(txtAuditoriumName);
            panelText.add(txtAuditoriumSquare);
            panelText.add(CB_Manager);
            panelText.add(new JLabel(""));
            
            panelText.add(txtFIO);
            panelText.add(txtPosition);
            panelText.add(txtPhoneNumber);
            panelText.add(txtAge);
            
            panelUp.add(panelLabel);
            panelUp.add(panelText);
            add(Box.createRigidArea(new Dimension(0, height_gap))); // Отступ 10 пикселей
            
            panelButton.setPreferredSize(new Dimension(width_window, height_button_panel));
            panelButton.setBorder(BorderFactory.createBevelBorder(1));
            add(panelButton);
            panelButton.setLayout(new FlowLayout());
            panelButton.add(buttonAuditorium);
            panelButton.add(buttonManager);

            if ("Просмотреть".equals(mode)) {
                CB_Manager.setSelectedIndex(getManagerNumber(dataTo[5]));
                buttonAuditorium.setEnabled(false);
                buttonManager.setEnabled(false);
                txtBuildingNumber.setEditable(false);
                txtAuditoriumNumber.setEditable(false);
                txtAuditoriumName.setEditable(false);
                txtAuditoriumSquare.setEditable(false);
                txtFIO.setEditable(false);
                txtPosition.setEditable(false);
                txtPhoneNumber.setEditable(false);
                txtAge.setEditable(false);
                CB_Manager.setEditable(false);
            }
            if ("Редактировать менеджера".equals(mode)) {
                buttonAuditorium.setEnabled(false);
                txtBuildingNumber.setEditable(false);
                txtAuditoriumNumber.setEditable(false);
                txtAuditoriumName.setEditable(false);
                txtAuditoriumSquare.setEditable(false);
                CB_Manager.setEditable(false);
            }
            if ("Редактировать аудиторию".equals(mode)) {
                CB_Manager.setSelectedIndex(getManagerNumber(dataTo[5]));
                buttonManager.setEnabled(false);
                txtFIO.setEditable(false);
                txtPosition.setEditable(false);
                txtPhoneNumber.setEditable(false);
                txtAge.setEditable(false);
            }
        }
        public int getContactPanelWidth(){
            return width_window;
        }
        public int getContactPanelHeight(){
            return height_window;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);
            
            if ("Аудитория".equals(command) || "Менеджер".equals(command)) {
                String SQL_Update_TBL = null;
                if ("Создать".equals(mode)) {
                    int r[] = findMaxID(); //Переписать на поиск первого свободного ID
                    if ("Менеджер".equals(command)){
                        SQL_Update_TBL =
                        "INSERT INTO MANAGER (MANAGER_ID, MANAGER_POSITION, MANAGER_PHONENUMBER, MANAGER_AGE, FIO) "
                        + "VALUES (" + (r[0] + 1) + ", "
                        + "'" + txtPosition.getText() + "', "
                        + "'" + txtPhoneNumber.getText() + "', "
                        + txtAge.getText() + ", "
                        + "'" + txtFIO.getText() + "')";
                    }
                    if ("Аудитория".equals(command)){
                        SQL_Update_TBL =
                        "INSERT INTO AUDITORIUM (ID_AUDITORIUM, AUDITORIUM_BUILDING, AUDITORIUM_NUMBER,"
                            + "AUDITORIUM_NAME, AUDITORIUM_SQUARE, MANAGER) "
                            + "VALUES (" + (r[1] + 1) + ", "
                            + txtBuildingNumber.getText() + ", "
                            + txtAuditoriumNumber.getText() + ", "
                            + "'" + txtAuditoriumName.getText() + "', "
                            + txtAuditoriumSquare.getText() + ", "
                            + "'" + CB_Manager.getSelectedItem() + "')";
                    }
                    findByString("");
                }
                
                if ("Редактировать аудиторию".equals(mode) || "Редактировать менеджера".equals(mode)){
                    if ("Менеджер".equals(command)){
                        SQL_Update_TBL =
                        "UPDATE MANAGER SET "
                        + "MANAGER_POSITION = '" + txtPosition.getText() + "', "
                        + "MANAGER_PHONENUMBER = '" + txtPhoneNumber.getText() + "', "
                        + "MANAGER_AGE = " + txtAge.getText() + ", "
                        + "FIO = '" + txtFIO.getText() + "' "
                        + "WHERE MANAGER_ID = " + dataTo[6] + "";
                    }
                
                    if ("Аудитория".equals(command)){
                        SQL_Update_TBL =
                        "UPDATE AUDITORIUM SET "
                        + "AUDITORIUM_BUILDING = " + txtBuildingNumber.getText() + ", "
                        + "AUDITORIUM_NUMBER = " + txtAuditoriumNumber.getText() + ", "
                        + "AUDITORIUM_NAME = '" + txtAuditoriumName.getText() + "', "
                        + "AUDITORIUM_SQUARE = " + txtAuditoriumSquare.getText() + ", "
                        + "MANAGER = '" + CB_Manager.getSelectedItem() + "' "
                        + "WHERE ID_AUDITORIUM = " + dataTo[0] + "";
                    }
                }
                try {
                    PreparedStatement create = null;
                    conn.setAutoCommit(false);
                    create = conn.prepareStatement(SQL_Update_TBL);
                    int executeUpdate = create.executeUpdate();
                    
                    conn.commit();
                    if (create != null) create.close();
                    conn.setAutoCommit(true);
                } catch (SQLException err1) {
                    try {
                        conn.rollback();
                        conn.setAutoCommit(true);
                        JOptionPane.showMessageDialog(this, "Транзакция на создание контакта не выполнена.\n"
                        + "Смотрите сообщения в консоли.");
                        System.out.println(err1.getMessage());
                    } catch (SQLException err2) {
                        System.out.println(err2.getMessage());
                    }
                }
                findByString("");
                dialog.dispose();
            }
        }
        
        private int[] findMaxID() {
            int[] r = {0, 0};
            try {
                SQL =
                "SELECT ID_AUDITORIUM, MANAGER_ID "
                + "FROM AUDITORIUM, MANAGER ";
                rs = stmt.executeQuery(SQL);
                int max_MANAGER_ID = r[0], max_ID_AUDITORIUM = r[1];
                int tmp;
                while(rs.next()) {
                    tmp = new Integer(rs.getString("MANAGER_ID"));
                    if (tmp > max_MANAGER_ID) max_MANAGER_ID = tmp;
                    tmp = new Integer(rs.getString("ID_AUDITORIUM"));
                    if (tmp > max_ID_AUDITORIUM) max_ID_AUDITORIUM = tmp;
                    r[0] = max_MANAGER_ID;
                    r[1] = max_ID_AUDITORIUM;
                }
            } catch (SQLException err) {
                System.out.println(err.getMessage());
            }
            return r;
        }
        
        private ArrayList<String> getManagers() {
            ArrayList<String> managers = new ArrayList<String>();
            try {
                SQL =
                "SELECT FIO FROM MANAGER ORDER BY MANAGER_ID";
                rs = stmt.executeQuery(SQL);
                while(rs.next()) {
                    managers.add(rs.getString("FIO"));
                }
            } catch (SQLException err) {
                System.out.println(err.getMessage());
            }
            return managers;
        }
        
        private int getManagerNumber(String manager){
            int number = 0;
            try {
                SQL =
                "SELECT MANAGER_ID FROM MANAGER "
                + "WHERE FIO = '" + manager + "'";
                rs = stmt.executeQuery(SQL);
                rs.next();
                number = Integer.parseInt(rs.getString("MANAGER_ID"));
            } catch (SQLException err) {
                System.out.println(err.getMessage());
            }
            return number-1;
        }
    }
}