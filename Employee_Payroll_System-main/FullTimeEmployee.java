public class FullTimeEmployee extends Employee {
    private static final long serialVersionUID = 1L;
    private double salary;
    
    public FullTimeEmployee(String name, int id, double salary) {
        super(name, id);
        this.salary = salary;
    }
    
    @Override
    public double calculateSalary() {
        return salary;
    }
} 