import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


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
}

// Main program
public class GroupProject {
    static ArrayList<Student> students = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Student Grade Management System ===");
            System.out.println("1. Add Student");
            System.out.println("2. Add Grade");
            System.out.println("3. Calculate Average");
            System.out.println("4. Display All Students");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    addGrade();
                    break;
                case 3:
                    calculateAverage();
                    break;
                case 4:
                    displayAllStudents();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

   
    static void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        students.add(new Student(name));
        System.out.println("Student added.");
    }

   
    static void addGrade() {
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
        int grade = scanner.nextInt();
        scanner.nextLine();

        student.addGrade(course, grade);
        System.out.println("Grade added.");
    }

    
    static void calculateAverage() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        Student student = findStudentByName(name);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter course name: ");
        String course = scanner.nextLine();

        double average = student.calculateAverage(course);
        String letter = student.convertToLetterGrade(average);

        System.out.printf("Average for %s in %s: %.2f (%s)%n",
                name, course, average, letter);
    }

    
    static void displayAllStudents() {
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

   
    static Student findStudentByName(String name) {
        for (Student student : students) {
            if (student.name.equalsIgnoreCase(name)) {
                return student;
            }
        }
        return null;
    }
}
