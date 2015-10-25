package fi.hut.cs.drumbeat.jython;

/**
 * Hello world!
 *
 */
public class App {
	EmployeeType et;

	public static void main(String[] args) {
		try {
		
			JythonFactory jf = JythonFactory.getInstance();
			EmployeeType eType = (EmployeeType) jf
					.getJythonObject("fi.hut.cs.drumbeat.jython.EmployeeType",
							"C:/DRUM/!github/drumbeat/drumbeat-ifc2ld/drumbeat-jython/Employee.py");

			System.out.println("Employee Name: " + eType.getEmployeeFirst()
					+ " " + eType.getEmployeeLast());
			System.out.println("Employee ID: " + eType.getEmployeeId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
