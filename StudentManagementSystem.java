import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentManagementSystem extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private static final String DB_URL = "jdbc:sqlite:students.db";

    public StudentManagementSystem() {
        setTitle("学生成绩管理系统");
        setSize(1230, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置全局字体
        setUIFont(new Font("微软雅黑", Font.PLAIN, 15));

        // 设置主面板背景色
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(12, 12));

        // ====== 新增：大标题 ======
        JLabel titleLabel = new JLabel("学生成绩管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(18, 0, 8, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // =========================

        // 创建表格模型
        String[] columnNames = {"学号", "姓名", "班级", "高等数学", "大学英语", "计算机导论", "体育"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(28);
        studentTable.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        studentTable.setSelectionBackground(new Color(220, 235, 245));
        studentTable.setSelectionForeground(Color.BLACK);

        // ====== 新增：表格排序功能 ======
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(sorter);
        // ==============================

        // 表头美化
        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 16));
        header.setBackground(new Color(230, 230, 230));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // 表格居中
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        scrollPane.getViewport().setBackground(new Color(250, 250, 250));

        // 创建操作按钮
        JButton addButton = createButton("添加学生");
        JButton editButton = createButton("修改信息");
        JButton deleteButton = createButton("删除学生");
        JButton searchButton = createButton("查询学生");
        JButton failButton = createButton("不及格名单");
        JButton refreshButton = createButton("刷新数据");
        JButton resetOrderButton = createButton("恢复顺序");
        searchField = new JTextField(16);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        searchField.setPreferredSize(new Dimension(120, 32));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(new JLabel("学号/姓名/班级:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(failButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(resetOrderButton);

        // ====== 新增：标题和按钮面板垂直组合 ======
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(new Color(245, 245, 245));
        northPanel.add(titleLabel);
        northPanel.add(buttonPanel);
        // ======================================

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 按钮事件
        addButton.addActionListener(e -> addStudent());
        editButton.addActionListener(e -> editStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudent());
        failButton.addActionListener(e -> showFailList());
        refreshButton.addActionListener(e -> refreshTable());
        resetOrderButton.addActionListener(e -> sorter.setSortKeys(null));

        // 初始化数据库
        createTableIfNotExists();

        // 初始刷新表格
        refreshTable();
    }

    // 设置全局字体
    public static void setUIFont(Font f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    // 创建美观按钮
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btn.setPreferredSize(new Dimension(100, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(235, 235, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    // 创建表
    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS students (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT," +
                    "className TEXT," +
                    "math INTEGER," +
                    "english INTEGER," +
                    "computer INTEGER," +
                    "pe INTEGER)";
            stmt.execute(sql);
        } catch (SQLException e) {
            showError("数据库初始化失败: " + e.getMessage());
        }
    }

    // 刷新表格
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Student> studentList = getAllStudents();
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

    // 获取所有学生
    private List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("className"),
                        rs.getInt("math"),
                        rs.getInt("english"),
                        rs.getInt("computer"),
                        rs.getInt("pe")
                ));
            }
        } catch (SQLException e) {
            showError("读取数据失败: " + e.getMessage());
        }
        return list;
    }

    // 检查学号是否已存在
    private boolean isStudentIdExists(String id) {
        String sql = "SELECT COUNT(*) FROM students WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            showError("检查学号失败: " + e.getMessage());
        }
        return false;
    }

    // 验证成绩是否在0-100之间
    private boolean isValidGrade(int grade) {
        return grade >= 0 && grade <= 100;
    }

    // 添加学生
    private void addStudent() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 8, 8));
        panel.setBackground(new Color(250, 250, 250));
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

        setDialogFont(panel);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "添加学生信息",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // 验证学号是否为空
                String id = idField.getText().trim();
                if (id.isEmpty()) {
                    showError("学号不能为空!");
                    return;
                }

                // 验证学号是否已存在
                if (isStudentIdExists(id)) {
                    showError("学号已存在，请使用其他学号!");
                    return;
                }

                // 验证姓名是否为空
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showError("姓名不能为空!");
                    return;
                }

                // 验证班级是否为空
                String className = classField.getText().trim();
                if (className.isEmpty()) {
                    showError("班级不能为空!");
                    return;
                }

                // 验证成绩
                int math = Integer.parseInt(mathField.getText());
                int english = Integer.parseInt(englishField.getText());
                int computer = Integer.parseInt(computerField.getText());
                int pe = Integer.parseInt(peField.getText());

                if (!isValidGrade(math) || !isValidGrade(english) ||
                        !isValidGrade(computer) || !isValidGrade(pe)) {
                    showError("成绩必须在0-100之间!");
                    return;
                }

                Student student = new Student(id, name, className, math, english, computer, pe);
                insertStudent(student);
                refreshTable();
                JOptionPane.showMessageDialog(this, "学生添加成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                showError("成绩必须为数字!");
            }
        }
    }

    // 插入学生
    private void insertStudent(Student student) {
        String sql = "INSERT INTO students (id, name, className, math, english, computer, pe) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getClassName());
            pstmt.setInt(4, student.getMath());
            pstmt.setInt(5, student.getEnglish());
            pstmt.setInt(6, student.getComputer());
            pstmt.setInt(7, student.getPe());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showError("添加学生失败: " + e.getMessage());
        }
    }

    // 修改学生
    private void editStudent() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow == -1) {
            showError("请先选择学生!");
            return;
        }
        int modelRow = studentTable.convertRowIndexToModel(viewRow);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        Student student = getStudentById(id);
        if (student == null) {
            showError("未找到该学生!");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(7, 2, 8, 8));
        panel.setBackground(new Color(250, 250, 250));
        JTextField idField = new JTextField(student.getId());
        JTextField nameField = new JTextField(student.getName());
        JTextField classField = new JTextField(student.getClassName());
        JTextField mathField = new JTextField(String.valueOf(student.getMath()));
        JTextField englishField = new JTextField(String.valueOf(student.getEnglish()));
        JTextField computerField = new JTextField(String.valueOf(student.getComputer()));
        JTextField peField = new JTextField(String.valueOf(student.getPe()));

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

        setDialogFont(panel);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "修改学生信息",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // 验证姓名是否为空
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    showError("姓名不能为空!");
                    return;
                }

                // 验证班级是否为空
                String className = classField.getText().trim();
                if (className.isEmpty()) {
                    showError("班级不能为空!");
                    return;
                }

                // 验证成绩
                int math = Integer.parseInt(mathField.getText());
                int english = Integer.parseInt(englishField.getText());
                int computer = Integer.parseInt(computerField.getText());
                int pe = Integer.parseInt(peField.getText());

                if (!isValidGrade(math) || !isValidGrade(english) ||
                        !isValidGrade(computer) || !isValidGrade(pe)) {
                    showError("成绩必须在0-100之间!");
                    return;
                }

                student.setName(name);
                student.setClassName(className);
                student.setMath(math);
                student.setEnglish(english);
                student.setComputer(computer);
                student.setPe(pe);
                updateStudent(student);
                refreshTable();
                JOptionPane.showMessageDialog(this, "学生信息修改成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                showError("成绩必须为数字!");
            }
        }
    }

    // 获取单个学生
    private Student getStudentById(String id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("className"),
                        rs.getInt("math"),
                        rs.getInt("english"),
                        rs.getInt("computer"),
                        rs.getInt("pe")
                );
            }
        } catch (SQLException e) {
            showError("查询学生失败: " + e.getMessage());
        }
        return null;
    }

    // 更新学生
    private void updateStudent(Student student) {
        String sql = "UPDATE students SET name=?, className=?, math=?, english=?, computer=?, pe=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getClassName());
            pstmt.setInt(3, student.getMath());
            pstmt.setInt(4, student.getEnglish());
            pstmt.setInt(5, student.getComputer());
            pstmt.setInt(6, student.getPe());
            pstmt.setString(7, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showError("更新学生失败: " + e.getMessage());
        }
    }

    // 删除学生
    private void deleteStudent() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow == -1) {
            showError("请先选择学生!");
            return;
        }
        int modelRow = studentTable.convertRowIndexToModel(viewRow);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        String name = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this, "确定要删除学生 " + name + " (学号: " + id + ") 吗?", "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM students WHERE id=?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
                refreshTable();
                JOptionPane.showMessageDialog(this, "学生删除成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                showError("删除学生失败: " + e.getMessage());
            }
        }
    }

    // 查询学生
    private void searchStudent() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM students WHERE lower(id) LIKE ? OR lower(name) LIKE ? OR lower(className) LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] rowData = {
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("className"),
                        rs.getInt("math"),
                        rs.getInt("english"),
                        rs.getInt("computer"),
                        rs.getInt("pe")
                };
                tableModel.addRow(rowData);
            }

            // 显示查询结果数量
            int rowCount = tableModel.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(this, "未找到匹配的学生信息", "查询结果", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "找到 " + rowCount + " 条匹配的学生信息", "查询结果", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            showError("查询失败: " + e.getMessage());
        }
    }

    // 显示不及格名单
    private void showFailList() {
        String[] courses = {"高等数学", "大学英语", "计算机导论", "体育"};
        String[] dbFields = {"math", "english", "computer", "pe"};
        String course = (String) JOptionPane.showInputDialog(
                this, "请选择课程:", "不及格名单查询",
                JOptionPane.PLAIN_MESSAGE, null, courses, courses[0]);
        if (course == null) return;

        int idx = -1;
        for (int i = 0; i < courses.length; i++) {
            if (courses[i].equals(course)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return;

        StringBuilder sb = new StringBuilder();
        sb.append(course).append("不及格学生名单:\n\n");

        String sql = "SELECT * FROM students WHERE " + dbFields[idx] + " < 60";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean hasFailStudents = false;
            while (rs.next()) {
                hasFailStudents = true;
                sb.append(String.format("学号: %s  姓名: %s  班级: %s  成绩: %d\n",
                        rs.getString("id"), rs.getString("name"),
                        rs.getString("className"), rs.getInt(dbFields[idx])));
            }

            if (!hasFailStudents) {
                sb.append("该课程没有不及格的学生！");
            }
        } catch (SQLException e) {
            showError("查询不及格名单失败: " + e.getMessage());
        }

        JTextArea textArea = new JTextArea(sb.toString(), 15, 40);
        textArea.setEditable(false);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                course + "不及格名单", JOptionPane.INFORMATION_MESSAGE);
    }

    // 统一弹窗字体
    private void setDialogFont(JComponent comp) {
        Font font = new Font("微软雅黑", Font.PLAIN, 15);
        for (Component c : comp.getComponents()) {
            c.setFont(font);
            if (c instanceof JComponent) {
                setDialogFont((JComponent) c);
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE);
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

class Student {
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