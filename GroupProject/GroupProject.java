import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;
import java.io.*; // <-- added for saving to file

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

    // ===== SHARED DATA (used by both console and GUI) =====
    private static final ArrayList<Student> students = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    // Predefined courses for the dropdown (GUI)
    private static final String[] COURSES = {
            "Math", "Science", "English", "History", "Computer Science", "Other..."
    };

    // ===== GUI FIELDS =====
    private JFrame frame;
    private JTextArea outputArea;

    // ===== MAIN: choose mode (GUI by default) =====
    public static void main(String[] args) {
        // If you run with argument "console", use the text-based menu
        if (args.length > 0 && args[0].equalsIgnoreCase("console")) {
            runConsoleMenu();
        } else {
            // Otherwise, start the GUI
            SwingUtilities.invokeLater(() -> {
                GroupProject app = new GroupProject();
                app.createAndShowGUI();
            });
        }
    }

    // ===================== CONSOLE VERSION =====================

    private static void runConsoleMenu() {
        while (true) {
            System.out.println("\n=== Student Grade Management System (Console) ===");
            System.out.println("1. Add Student");
            System.out.println("2. Add Grade");
            System.out.println("3. Calculate Average");
            System.out.println("4. Display All Students");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                // If invalid input, clear scanner and continue
                scanner.nextLine();
                System.out.println("Invalid input. Try again.");
                continue;
            }
            scanner.nextLine(); // consume leftover newline

            switch (choice) {
                case 1:
                    addStudentConsole();
                    break;
                case 2:
                    addGradeConsole();
                    break;
                case 3:
                    calculateAverageConsole();
                    break;
                case 4:
                    displayAllStudentsConsole();
                    break;
                case 5:
                    System.out.println("Exiting console mode...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addStudentConsole() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        students.add(new Student(name.trim()));
        System.out.println("Student added.");
    }

    private static void addGradeConsole() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        Student student = findStudentByName(name);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter course name: ");
        String course = scanner.nextLine();

        System.out.print("Enter grade: ");
        int grade;
        try {
            grade = scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid number for grade.");
            return;
        }
        scanner.nextLine(); // consume newline

        student.addGrade(course.trim(), grade);
        System.out.println("Grade added.");
    }

    private static void calculateAverageConsole() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        Student student = findStudentByName(name);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter course name: ");
        String course = scanner.nextLine();

        double average = student.calculateAverage(course.trim());
        String letter = student.convertToLetterGrade(average);

        System.out.printf("Average for %s in %s: %.2f (%s)%n",
                name, course, average, letter);
    }

    private static void displayAllStudentsConsole() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("+----------------------+-----------------+-----------+");
        System.out.printf("| %-20s | %-15s | %-9s |%n", "Student Name", "Course", "Average");
        System.out.println("+----------------------+-----------------+-----------+");

        for (Student s : students) {
            if (s.courses.isEmpty()) {
                System.out.printf("| %-20s | %-15s | %-9s |%n", s.name, "No courses", "-");
            } else {
                for (String course : s.courses.keySet()) {
                    double avg = s.calculateAverage(course);
                    String letter = s.convertToLetterGrade(avg);
                    System.out.printf("| %-20s | %-15s | %-9s |%n",
                            s.name, course, String.format("%.2f (%s)", avg, letter));
                }
            }
        }

        System.out.println("+----------------------+-----------------+-----------+");
    }

    private static Student findStudentByName(String name) {
        for (Student student : students) {
            if (student.name.equalsIgnoreCase(name)) {
                return student;
            }
        }
        return null;
    }

    // ===================== GUI VERSION =====================

    private void createAndShowGUI() {
        frame = new JFrame("Student Grade Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null); // center

        frame.setLayout(new BorderLayout());

        // LEFT: BUTTON PANEL
        JPanel buttonPanel = new JPanel();
        // now 8 buttons total
        buttonPanel.setLayout(new GridLayout(8, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addStudentBtn = new JButton("Add Student");
        JButton addGradeBtn = new JButton("Add Grade");
        JButton calcAverageBtn = new JButton("Calculate Average");
        JButton displayAllBtn = new JButton("Display All Students");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn = new JButton("Save to File");
        JButton exitBtn = new JButton("Exit");

        buttonPanel.add(addStudentBtn);
        buttonPanel.add(addGradeBtn);
        buttonPanel.add(calcAverageBtn);
        buttonPanel.add(displayAllBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(exitBtn);

        frame.add(buttonPanel, BorderLayout.WEST);

        // RIGHT: OUTPUT AREA
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        frame.add(scrollPane, BorderLayout.CENTER);

        // BUTTON ACTIONS
        addStudentBtn.addActionListener(e -> addStudent());
        addGradeBtn.addActionListener(e -> addGrade());
        calcAverageBtn.addActionListener(e -> calculateAverage());
        displayAllBtn.addActionListener(e -> displayAllStudents());
        editBtn.addActionListener(e -> editStudentOrGrade());
        deleteBtn.addActionListener(e -> deleteStudentOrGrade());
        saveBtn.addActionListener(e -> saveToFile());
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
            outputArea.setText("");
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

    // ===== NEW: SAVE / EDIT / DELETE =====

    // Save all students & grades to students.txt
    private void saveToFile() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No students to save.");
            return;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter("students.txt"))) {
            for (Student s : students) {
                out.println("Student: " + s.name);
                if (s.courses.isEmpty()) {
                    out.println("  No courses/grades.");
                } else {
                    for (Map.Entry<String, ArrayList<Integer>> entry : s.courses.entrySet()) {
                        String course = entry.getKey();
                        ArrayList<Integer> grades = entry.getValue();

                        out.print("  " + course + ": ");
                        for (int i = 0; i < grades.size(); i++) {
                            out.print(grades.get(i));
                            if (i < grades.size() - 1) {
                                out.print(", ");
                            }
                        }
                        double avg = s.calculateAverage(course);
                        String letter = s.convertToLetterGrade(avg);
                        out.printf(" | Average: %.2f (%s)%n", avg, letter);
                    }
                }
                out.println();
            }
            JOptionPane.showMessageDialog(frame, "Students saved to students.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
        }
    }

    // Edit student name or a specific grade
    private void editStudentOrGrade() {
        Student student = chooseStudent();
        if (student == null) return;

        String[] options = {"Rename Student", "Edit Grade", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "What would you like to edit?",
                "Edit",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) { // Rename student
            String newName = JOptionPane.showInputDialog(frame, "Enter new name:", student.name);
            if (newName != null && !newName.trim().isEmpty()) {
                String oldName = student.name;
                student.name = newName.trim();
                JOptionPane.showMessageDialog(frame, "Renamed " + oldName + " to " + student.name);
                outputArea.append("Renamed " + oldName + " to " + student.name + "\n");
            }
        } else if (choice == 1) { // Edit grade
            editGrade(student);
        }
        // Cancel -> do nothing
    }

    private void editGrade(Student student) {
        if (student.courses.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "This student has no courses/grades.");
            return;
        }

        String[] courseNames = student.courses.keySet().toArray(new String[0]);
        String course = (String) JOptionPane.showInputDialog(
                frame,
                "Choose course to edit:",
                "Edit Grade",
                JOptionPane.PLAIN_MESSAGE,
                null,
                courseNames,
                courseNames[0]
        );
        if (course == null) return;

        ArrayList<Integer> grades = student.courses.get(course);
        if (grades == null || grades.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No grades in this course.");
            return;
        }

        StringBuilder sb = new StringBuilder("Current grades for " + course + ":\n");
        for (int i = 0; i < grades.size(); i++) {
            sb.append(i).append(": ").append(grades.get(i)).append("\n");
        }

        String indexStr = JOptionPane.showInputDialog(
                frame,
                sb.toString() + "\nEnter index of grade to edit (0-" + (grades.size() - 1) + "):"
        );
        if (indexStr == null) return;

        try {
            int index = Integer.parseInt(indexStr.trim());
            if (index < 0 || index >= grades.size()) {
                JOptionPane.showMessageDialog(frame, "Index out of range.");
                return;
            }

            String newGradeStr = JOptionPane.showInputDialog(frame, "Enter new grade (0-100):");
            if (newGradeStr == null) return;

            int newGrade = Integer.parseInt(newGradeStr.trim());
            if (newGrade < 0 || newGrade > 100) {
                JOptionPane.showMessageDialog(frame, "Grade must be between 0 and 100.");
                return;
            }

            int oldGrade = grades.get(index);
            grades.set(index, newGrade);
            JOptionPane.showMessageDialog(
                    frame,
                    "Changed grade " + oldGrade + " to " + newGrade +
                            " for " + student.name + " in " + course
            );
            outputArea.append("Edited grade for " + student.name + " in " + course +
                    ": " + oldGrade + " -> " + newGrade + "\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid number.");
        }
    }

    // Delete student, course, or a single grade
    private void deleteStudentOrGrade() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No students to delete.");
            return;
        }

        Student student = chooseStudent();
        if (student == null) return;

        String[] options = {"Delete Student", "Delete Course", "Delete Grade", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "What would you like to delete?",
                "Delete",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) { // Delete student
            students.remove(student);
            JOptionPane.showMessageDialog(frame, "Deleted student: " + student.name);
            outputArea.append("Deleted student: " + student.name + "\n");
        } else if (choice == 1) { // Delete course
            if (student.courses.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "This student has no courses.");
                return;
            }
            String[] courseNames = student.courses.keySet().toArray(new String[0]);
            String course = (String) JOptionPane.showInputDialog(
                    frame,
                    "Choose course to delete:",
                    "Delete Course",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    courseNames,
                    courseNames[0]
            );
            if (course != null) {
                student.courses.remove(course);
                JOptionPane.showMessageDialog(frame, "Deleted course " + course +
                        " for " + student.name);
                outputArea.append("Deleted course " + course + " for " + student.name + "\n");
            }
        } else if (choice == 2) { // Delete grade
            if (student.courses.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "This student has no courses.");
                return;
            }

            String[] courseNames = student.courses.keySet().toArray(new String[0]);
            String course = (String) JOptionPane.showInputDialog(
                    frame,
                    "Choose course:",
                    "Delete Grade",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    courseNames,
                    courseNames[0]
            );
            if (course == null) return;

            ArrayList<Integer> grades = student.courses.get(course);
            if (grades == null || grades.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No grades in this course.");
                return;
            }

            StringBuilder sb = new StringBuilder("Current grades for " + course + ":\n");
            for (int i = 0; i < grades.size(); i++) {
                sb.append(i).append(": ").append(grades.get(i)).append("\n");
            }

            String indexStr = JOptionPane.showInputDialog(
                    frame,
                    sb.toString() + "\nEnter index of grade to delete (0-" + (grades.size() - 1) + "):"
            );
            if (indexStr == null) return;

            try {
                int index = Integer.parseInt(indexStr.trim());
                if (index < 0 || index >= grades.size()) {
                    JOptionPane.showMessageDialog(frame, "Index out of range.");
                    return;
                }
                int removed = grades.remove(index);
                JOptionPane.showMessageDialog(frame, "Deleted grade " + removed +
                        " from " + course + " for " + student.name);
                outputArea.append("Deleted grade " + removed + " from " + course +
                        " for " + student.name + "\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number.");
            }
        }
        // Cancel -> do nothing
    }

    // ===== GUI HELPERS =====

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

    // Course dropdown with "Other..."
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
