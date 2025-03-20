public class PartTimeEmployee extends Employee {
    private static final long serialVersionUID = 1L;
    private int hours;
    private double rate;
    
    public PartTimeEmployee(String name, int id, int hours, double rate) {
        super(name, id);
        this.hours = hours;
        this.rate = rate;
    }
    
    @Override
    public double calculateSalary() {
        return hours * rate;
    }
} 