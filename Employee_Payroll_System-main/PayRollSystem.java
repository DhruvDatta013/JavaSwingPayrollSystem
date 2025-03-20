import java.util.ArrayList;
import java.util.List;

public class PayRollSystem {
    private List<Employee> employees;
    
    public PayRollSystem() {
        employees = new ArrayList<>();
    }
    
    public void addEmployee(Employee emp) {
        // Validate employee data
        if (emp == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (emp.getName() == null || emp.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be empty");
        }
        if (emp.getId() <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (emp.calculateSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        
        // Check for duplicate ID
        for (Employee existing : employees) {
            if (existing.getId() == emp.getId()) {
                throw new IllegalArgumentException("Employee ID " + emp.getId() + " already exists!");
            }
        }
        
        // Add employee and print confirmation
        employees.add(emp);
        System.out.println("Added employee: " + emp.getName() + " (ID: " + emp.getId() + ")");
    }
    
    public void removeEmployee(int id) {
        boolean removed = employees.removeIf(emp -> emp.getId() == id);
        if (!removed) {
            throw new IllegalArgumentException("Employee ID " + id + " not found!");
        }
        System.out.println("Removed employee with ID: " + id);
    }
    
    public List<Employee> getEmployeeList() {
        return new ArrayList<>(employees);
    }
    
    public double calculateTotalPayroll() {
        double total = employees.stream()
                               .mapToDouble(Employee::calculateSalary)
                               .sum();
        if (total < 0) {
            System.err.println("Warning: Negative total payroll detected!");
            return 0.0;
        }
        System.out.println("Calculated total payroll: " + String.format("$%,.2f", total));
        return total;
    }
} 