import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import java.awt.*;

class Student {
    String name;
    Map<String, ArrayList<Integer>> courses;

    public Student(String name) {
        this.name = name;
        this.courses = new HashMap<>();
    }

    public void addGrade(String course, int grade) {
        courses.putIfAbsent(course, new ArrayList<>());
        courses.get(course).add(grade);
    }

    public double calculateAverage(String course) {
        ArrayList<Integer> grades = courses.get(course);
        if (grades == null || grades.isEmpty()) return 0.0;

        int sum = 0;
        for (int grade : grades) {
            sum += grade;
        }
        return sum / (double) grades.size();
    }

    public String convertToLetterGrade(double average) {
        if (average >= 90) return "A";
        else if (average >= 80) return "B";
        else if (average >= 70) return "C";
        else if (average >= 60) return "D";
        else return "F";
    }

    @Override
    public String toString() {
        return name;
    }
}

public class GroupProject {

    // Data
    private static final java.util.List<Student> students = new ArrayList<>();

    // Predefined courses for the dropdown
    private static final String[] COURSES = {
            "Math", "Science", "English", "History", "Computer Science", "Other..."
    };

    // GUI components
    private JFrame frame;
    private JTextArea outputArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GroupProject app = new GroupProject();
            app.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Student Grade Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null); // center the window

        frame.setLayout(new BorderLayout());

        // ========= LEFT: BUTTON PANEL =========
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addStudentBtn = new JButton("Add Student");
        JButton addGradeBtn = new JButton("Add Grade");
        JButton calcAverageBtn = new JButton("Calculate Average");
        JButton displayAllBtn = new JButton("Display All Students");
        JButton exitBtn = new JButton("Exit");

        buttonPanel.add(addStudentBtn);
        buttonPanel.add(addGradeBtn);
        buttonPanel.add(calcAverageBtn);
        buttonPanel.add(displayAllBtn);
        buttonPanel.add(exitBtn);

        frame.add(buttonPanel, BorderLayout.WEST);

        // ========= RIGHT: OUTPUT AREA =========
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        frame.add(scrollPane, BorderLayout.CENTER);

        // ========= BUTTON ACTIONS =========
        addStudentBtn.addActionListener(e -> addStudent());
        addGradeBtn.addActionListener(e -> addGrade());
        calcAverageBtn.addActionListener(e -> calculateAverage());
        displayAllBtn.addActionListener(e -> displayAllStudents());
        exitBtn.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void addStudent() {
        String name = JOptionPane.showInputDialog(frame, "Enter student name:");
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        students.add(new Student(name.trim()));
        JOptionPane.showMessageDialog(frame, "Student added: " + name);
        outputArea.append("Added student: " + name + "\n");
    }

    private void addGrade() {
        Student student = chooseStudent();
        if (student == null) return;

        String course = chooseCourse("Select course for grade:");
        if (course == null || course.trim().isEmpty()) {
            return;
        }

        String gradeStr = JOptionPane.showInputDialog(frame, "Enter grade (0-100):");
        if (gradeStr == null) return;

        try {
            int grade = Integer.parseInt(gradeStr.trim());
            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(frame, "Grade must be between 0 and 100.");
                return;
            }
            student.addGrade(course.trim(), grade);
            JOptionPane.showMessageDialog(frame,
                    "Grade added for " + student.name + " in " + course + ": " + grade);
            outputArea.append("Grade " + grade + " added for " + student.name
                    + " in " + course + "\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid number for grade.");
        }
    }

    private void calculateAverage() {
        Student student = chooseStudent();
        if (student == null) return;

        String course = chooseCourse("Select course to calculate average:");
        if (course == null || course.trim().isEmpty()) {
            return;
        }

        double avg = student.calculateAverage(course.trim());
        String letter = student.convertToLetterGrade(avg);

        String message = String.format(
                "Average for %s in %s: %.2f (%s)",
                student.name, course, avg, letter
        );
        JOptionPane.showMessageDialog(frame, message);
        outputArea.append(message + "\n");
    }

    private void displayAllStudents() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No students found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %-15s %-15s%n", "Student Name", "Course", "Average (Letter)"));
        sb.append("----------------------------------------------------------------\n");

        for (Student s : students) {
            if (s.courses.isEmpty()) {
                sb.append(String.format("%-20s %-15s %-15s%n", s.name, "No courses", "-"));
            } else {
                for (String course : s.courses.keySet()) {
                    double avg = s.calculateAverage(course);
                    String letter = s.convertToLetterGrade(avg);
                    sb.append(String.format("%-20s %-15s %-15s%n",
                            s.name, course, String.format("%.2f (%s)", avg, letter)));
                }
            }
        }

        outputArea.setText(sb.toString());
    }

    // ========= HELPERS =========

    private Student chooseStudent() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No students available. Add a student first.");
            return null;
        }

        String[] names = students.stream().map(s -> s.name).toArray(String[]::new);

        String selectedName = (String) JOptionPane.showInputDialog(
                frame,
                "Select a student:",
                "Choose Student",
                JOptionPane.PLAIN_MESSAGE,
                null,
                names,
                names[0]
        );

        if (selectedName == null) {
            return null;
        }

        for (Student s : students) {
            if (s.name.equals(selectedName)) {
                return s;
            }
        }
        return null;
    }

    // New: course dropdown with "Other..."
    private String chooseCourse(String message) {
        String selectedCourse = (String) JOptionPane.showInputDialog(
                frame,
                message,
                "Choose Course",
                JOptionPane.PLAIN_MESSAGE,
                null,
                COURSES,
                COURSES[0]
        );

        if (selectedCourse == null) {
            return null; // user cancelled
        }

        if ("Other...".equals(selectedCourse)) {
            String customCourse = JOptionPane.showInputDialog(frame, "Enter course name:");
            if (customCourse == null || customCourse.trim().isEmpty()) {
                return null;
            }
            return customCourse.trim();
        }

        return selectedCourse;
    }
}
