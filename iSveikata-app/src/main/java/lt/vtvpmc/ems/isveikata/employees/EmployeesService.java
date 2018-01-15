package lt.vtvpmc.ems.isveikata.employees;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lt.vtvpmc.ems.isveikata.patient.JpaPatientRepository;
import lt.vtvpmc.ems.isveikata.patient.Patient;

@Service
public class EmployeesService {

	@Autowired
	private JpaEmployeesRepository employeesRepository;
	@Autowired
	private JpaPatientRepository patientRepository;

	public void addEmployee(Employee employee) {
		employeesRepository.save(employee);
	}

	public void bindDoctroToPatient(String docId, String patId) {
		Patient patient = patientRepository.findOne(patId);
		Doctor doctor = (Doctor) employeesRepository.findByUserName(docId);
		if (doctor instanceof Doctor) {
			patient.setDoctor(doctor);
		}
	}

	public List<Doctor> getDoctorsList() {
		List<Employee> allEmployees = employeesRepository.findAll();
		List<Doctor> allDoctors = new ArrayList<Doctor>();
		for (Employee ae : allEmployees) {
			if (ae instanceof Doctor) {
				allDoctors.add((Doctor) ae);
			}
		}
		return allDoctors;
	}
}
