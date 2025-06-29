import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentManagementSystem extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<Student> studentList = new ArrayList<>();
    private static final String FILE_NAME = "students.dat";

    public StudentManagementSystem() {
        // 初始化界面
        setTitle("学生成绩管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadData(); // 加载数据

        // 创建表格模型
        String[] columnNames = {"学号", "姓名", "班级", "高等数学", "大学英语", "计算机导论", "体育"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        // 创建操作按钮
        JButton addButton = new JButton("添加学生");
        JButton editButton = new JButton("修改信息");
        JButton deleteButton = new JButton("删除学生");
        JButton searchButton = new JButton("查询学生");
        JButton failButton = new JButton("不及格名单");
        JButton saveButton = new JButton("保存数据");
        searchField = new JTextField(20);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JLabel("学号/姓名:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(failButton);
        buttonPanel.add(saveButton);

        // 添加组件到主窗口
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 按钮事件处理
        addButton.addActionListener(e -> addStudent());
        editButton.addActionListener(e -> editStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudent());
        failButton.addActionListener(e -> showFailList());
        saveButton.addActionListener(e -> saveData());

        // 初始刷新表格
        refreshTable();
    }

    // 刷新表格数据
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student student : studentList) {
            Object[] rowData = {
                    student.getId(),
                    student.getName(),
                    student.getClassName(),
                    student.getMath(),
                    student.getEnglish(),
                    student.getComputer(),
                    student.getPe()
            };
            tableModel.addRow(rowData);
        }
    }

    // 添加学生
    private void addStudent() {
        JPanel panel = new JPanel(new GridLayout(7, 2));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField classField = new JTextField();
        JTextField mathField = new JTextField();
        JTextField englishField = new JTextField();
        JTextField computerField = new JTextField();
        JTextField peField = new JTextField();

        panel.add(new JLabel("学号:"));
        panel.add(idField);
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("班级:"));
        panel.add(classField);
        panel.add(new JLabel("高等数学:"));
        panel.add(mathField);
        panel.add(new JLabel("大学英语:"));
        panel.add(englishField);
        panel.add(new JLabel("计算机导论:"));
        panel.add(computerField);
        panel.add(new JLabel("体育:"));
        panel.add(peField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "添加学生信息", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Student student = new Student(
                        idField.getText(),
                        nameField.getText(),
                        classField.getText(),
                        Integer.parseInt(mathField.getText()),
                        Integer.parseInt(englishField.getText()),
                        Integer.parseInt(computerField.getText()),
                        Integer.parseInt(peField.getText())
                );
                studentList.add(student);
                refreshTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "成绩必须为数字!", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 修改学生信息
    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择学生!", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student student = studentList.get(selectedRow);
        JPanel panel = new JPanel(new GridLayout(7, 2));
        JTextField idField = new JTextField(student.getId());
        JTextField nameField = new JTextField(student.getName());
        JTextField classField = new JTextField(student.getClassName());
        JTextField mathField = new JTextField(String.valueOf(student.getMath()));
        JTextField englishField = new JTextField(String.valueOf(student.getEnglish()));
        JTextField computerField = new JTextField(String.valueOf(student.getComputer()));
        JTextField peField = new JTextField(String.valueOf(student.getPe()));

        // 学号不可编辑
        idField.setEditable(false);

        panel.add(new JLabel("学号:"));
        panel.add(idField);
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("班级:"));
        panel.add(classField);
        panel.add(new JLabel("高等数学:"));
        panel.add(mathField);
        panel.add(new JLabel("大学英语:"));
        panel.add(englishField);
        panel.add(new JLabel("计算机导论:"));
        panel.add(computerField);
        panel.add(new JLabel("体育:"));
        panel.add(peField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "修改学生信息", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                student.setName(nameField.getText());
                student.setClassName(classField.getText());
                student.setMath(Integer.parseInt(mathField.getText()));
                student.setEnglish(Integer.parseInt(englishField.getText()));
                student.setComputer(Integer.parseInt(computerField.getText()));
                student.setPe(Integer.parseInt(peField.getText()));
                refreshTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "成绩必须为数字!", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 删除学生
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择学生!", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, "确定要删除该学生吗?", "确认删除", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            studentList.remove(selectedRow);
            refreshTable();
        }
    }

    // 查询学生
    private void searchStudent() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        List<Student> resultList = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getId().toLowerCase().contains(keyword) || 
                student.getName().toLowerCase().contains(keyword)) {
                resultList.add(student);
            }
        }

        tableModel.setRowCount(0);
        for (Student student : resultList) {
            Object[] rowData = {
                    student.getId(),
                    student.getName(),
                    student.getClassName(),
                    student.getMath(),
                    student.getEnglish(),
                    student.getComputer(),
                    student.getPe()
            };
            tableModel.addRow(rowData);
        }
    }

    // 显示不及格名单
    private void showFailList() {
        String[] courses = {"高等数学", "大学英语", "计算机导论", "体育"};
        String course = (String) JOptionPane.showInputDialog(
                this, "请选择课程:", "不及格名单查询",
                JOptionPane.PLAIN_MESSAGE, null, courses, courses[0]);

        if (course == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append(course).append("不及格学生名单:\n\n");
        
        for (Student student : studentList) {
            int score = -1;
            switch (course) {
                case "高等数学": score = student.getMath(); break;
                case "大学英语": score = student.getEnglish(); break;
                case "计算机导论": score = student.getComputer(); break;
                case "体育": score = student.getPe(); break;
            }
            
            if (score >= 0 && score < 60) {
                sb.append(String.format("学号: %s  姓名: %s  班级: %s  成绩: %d\n", 
                        student.getId(), student.getName(), 
                        student.getClassName(), score));
            }
        }
        
        JTextArea textArea = new JTextArea(sb.toString(), 15, 40);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                course + "不及格名单", JOptionPane.INFORMATION_MESSAGE);
    }

    // 保存数据到文件
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(studentList);
            JOptionPane.showMessageDialog(this, "数据保存成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 从文件加载数据
    @SuppressWarnings("unchecked")  
    private void loadData() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                studentList = (List<Student>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "加载数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new StudentManagementSystem().setVisible(true);
        });
    }
}

class Student implements Serializable {
    private String id;
    private String name;
    private String className;
    private int math;
    private int english;
    private int computer;
    private int pe;

    public Student(String id, String name, String className, int math, int english, int computer, int pe) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.math = math;
        this.english = english;
        this.computer = computer;
        this.pe = pe;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public int getMath() { return math; }
    public void setMath(int math) { this.math = math; }
    public int getEnglish() { return english; }
    public void setEnglish(int english) { this.english = english; }
    public int getComputer() { return computer; }
    public void setComputer(int computer) { this.computer = computer; }
    public int getPe() { return pe; }
    public void setPe(int pe) { this.pe = pe; }
}