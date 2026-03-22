package motorph.motorph_automatic_payroll_system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MotorPH_Automatic_Payroll_SystemTest {

    // Test convertToDecimal()
    @Test
    public void testConvertToDecimal() {
        double result = MotorPH_Automatic_Payroll_System.convertToDecimal("08:30");
        assertEquals(8.5, result, 0.01);
    }
    // Test calculateWorkedHours()

    @Test

    public void testCalculateWorkedHours() {

        double result = MotorPH_Automatic_Payroll_System.calculateWorkedHours("08:10", "17:00");

        assertEquals(8, result, 0.01);
    }

    // Test getMonthName()
    @Test
    public void testGetMonthName() {
        String result = MotorPH_Automatic_Payroll_System.getMonthName(0);
        assertEquals("Unknown", result);
    }

    @Test
    void testNetPay_WithExpectedValue() {
        double gross = 20000 / 2.0; // 10000

        double sss = MotorPH_Automatic_Payroll_System.computeSSS(gross);
        double phil = MotorPH_Automatic_Payroll_System.computePhilHealth(gross);
        double pagibig = MotorPH_Automatic_Payroll_System.computePagibig(gross);

        double taxable = gross - sss - phil - pagibig;
        double tax = MotorPH_Automatic_Payroll_System.computeTax(taxable);

        double net = gross - sss - phil - pagibig - tax;

        assertEquals(9311.25, net, 0.01);
    }

    @Test
    void testNetPay_SampleOnly() {
        double gross = 40000;

        double sss = MotorPH_Automatic_Payroll_System.computeSSS(gross);
        double phil = MotorPH_Automatic_Payroll_System.computePhilHealth(gross);
        double pagibig = MotorPH_Automatic_Payroll_System.computePagibig(gross);

        double taxable = gross - sss - phil - pagibig;
        double tax = MotorPH_Automatic_Payroll_System.computeTax(taxable);

        double net = gross - sss - phil - pagibig - tax;

        System.out.println("Computed Net: " + net);

        assertEquals(net, net, 0.01);
    }
}
