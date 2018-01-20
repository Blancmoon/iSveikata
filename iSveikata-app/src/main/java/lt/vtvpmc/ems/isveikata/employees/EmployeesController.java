package lt.vtvpmc.ems.isveikata.employees;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import lt.vtvpmc.ems.isveikata.icd.IcdService;
import lt.vtvpmc.ems.isveikata.icd.InternationalClassificationOfDiseases;
import lt.vtvpmc.ems.isveikata.icd.JpaIcdRepository;
import lt.vtvpmc.ems.isveikata.specialization.Specialization;
import lt.vtvpmc.ems.isveikata.specialization.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lt.vtvpmc.ems.isveikata.employees.DTO.RecordAppointment;
import lt.vtvpmc.ems.isveikata.medical_record.MedicalRecordService;
import lt.vtvpmc.ems.isveikata.patient.Patient;
import lt.vtvpmc.ems.isveikata.patient.PatientService;

/**
 * The Class EmployeesController.
 */
@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeesController {

	/** The employees service. */
	@Autowired
	private EmployeesService employeesService;

	/** The medical record service. */
	@Autowired
	private MedicalRecordService medicalRecordService;

	/** The patient service. */
	@Autowired
	private PatientService patientService;

	@Autowired
	private IcdService icdService;

	@Autowired
	private SpecializationService specializationService;

	/**
	 * Insert user. Insert new user into data base with unique userName.
	 * Return response if userName is not unique. URL: /api/admin/new/user
	 *
	 * @param user
	 *            the new user info from UI
	 */
	@PostMapping("/admin/new/user")
	private <T extends Employee> ResponseEntity<String> insertUserValid(@RequestBody Employee employee) {
		if (employeesService.validateAddNewUser(employee)) {
			employeesService.addEmployee(employee);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Sukurtas naujas vartotojas"); 
		} else {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body("Vartotojas su tokiu prisijungimo ID jau egzistuoja");
		}
	}
	
	
	/**
	 * Insert patient. Insert new patient into data base with unique patientId.
	 * Return response if patientId is not unique. URL: /api/admin/new/patient
	 *
	 * @param patient
	 *            the new patient info from UI
	 */
	@PostMapping("/admin/new/patient")
	private ResponseEntity<String> insertPatientValid(@RequestBody Patient patient) {
		if (patientService.validateAddNewPatient(patient)) {
			patientService.addNewPatient(patient);
			return ResponseEntity.status(HttpStatus.CREATED).body("Sukurtas naujas pacientas");
		} else {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body("Pacientas su tokiu asmens kodu jau egzistuoja");
		}
	}

//	Validuoti kad naujai priskiriami pacientai gydytojui neturi priskirto paciento ir kad gydytojui nera priskirtas tas pacientas.
	/**
	 * Binding. Adds new bind between doctor and patient with validation is patient not bind to doctor. 
	 * URL: /api/admin/new/bind/{userName}/to/{patient_id}
	 * 
	 * @param userName
	 *            the doctor id
	 * @param patId
	 *            the patient id
	 */
	@PostMapping("/admin/new/bind/{userName}/to/{patient_id}/valid")
	private ResponseEntity<String> bindingValid(@PathVariable("userName") String userName, @PathVariable("patient_id") Long patId) {
		employeesService.bindDoctroToPatient(userName, patId);
		if (employeesService.validateBindDoctroToPatient(userName, patId)) {
			employeesService.bindDoctroToPatient(userName, patId);
			return ResponseEntity.status(HttpStatus.CREATED).body("Pacientas priskirtas daktarui");
		} else {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body("Pacientas jau buvo priskirtas daktarui anksciau, arba bandote priskirti pacienta ne daktarui");
		}
	}
	

	/**
	 * Creates the record. Creates appointment record, using data from request body.
	 * In body additional must be specified doctro ID, patient ID and appoitment
	 * duration time.
	 * 
	 * URL: /api/doctor/new/record
	 *
	 * @param recordAppointment
	 *            the record from UI
	 */
	@PostMapping("/doctor/new/record")
	@ResponseStatus(HttpStatus.CREATED)
	private void createRecord(@RequestBody RecordAppointment recordAppointment) {
		medicalRecordService.createNewRecord(recordAppointment);
	}

	/**
	 * Gets all active doctors URL: /api/doctor
	 *
	 * @return list of all doctors
	 */
	@GetMapping("/doctor")
	private List<Doctor> getAllDoctors() {
		return employeesService.getDoctorsList();
	}

	/**
	 * Gets all active patient from given doctor URL: /api/doctor/{userName}
	 *
	 * @param userName
	 * @return list of all patient of current doctor
	 */
	@GetMapping("/doctor/{userName}/patient")
	private List<Patient> getAllDoctorPatient(@PathVariable String userName) {
		return employeesService.getDoctorPatientList(userName);
	}

	/**
	 * Change employee password in data base. URL: /{userName}/password
	 * 
	 * @param fields
	 * 
	 * @param userName
	 */
	@PutMapping("/{userName}/password")
	private void update(@RequestBody final Map<String, String> fields, @PathVariable final String userName) {
		employeesService.updateUserPassword(fields.get("password"), userName);
	}

	/**
	 * Login. URL: /user/login
	 *
	 * @param fields
	 */
	@PostMapping("/user/login")
	@ResponseBody
	private ResponseEntity<String> update(@RequestBody final Map<String, String> fields)
			throws NoSuchAlgorithmException {
		if (employeesService.userLogin(fields.get("userName"), fields.get("password"))) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(getUserType(fields.get("userName")).toLowerCase());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Vartotojas nerastas, patikrinkit prisijungimo duomenis");
		}
	}
	private String getUserType(String userName) {
		return employeesService.getType(userName);
	}

	/**
	 * Gets all active and not bind with doctor patients URL: /api/doctor/notbind
	 *
	 * @return all active and not bind with doctor patients
	 */
	@GetMapping("/doctor/notbind")
	private List<Patient> getPatientListWithoutDoctor() {
		return patientService.getPatientListWithoutDoctor();
	}

	/**
	 *	Creates new ICD
	 *
	 *	@param icd
	 */
	@PostMapping("/icd")
	private void createIcd(@RequestBody InternationalClassificationOfDiseases icd) {
		icdService.createIcd(icd);
	}

	/**
	 *	Gets all icd
	 *
	 * @return all icd
	 */
	@GetMapping("/icd")
	private List<InternationalClassificationOfDiseases> getAllIcd() {
		return icdService.getAllIcd();
	}

	/**
	 *	Gets specific idc by it's id
	 *
	 * @param icdCode
	 * @return icd title by given icdCode
	 */
	@GetMapping("/icd/{icdCode}")
	private String getIcdTitle(@PathVariable final String icdCode) {
		return icdService.getIcdTitle(icdCode);
	}

	/**
	 *	Creates new Specializacion
	 *
	 * @param specialization
	 */
	@PostMapping("/specialization")
	private void createIcd(@RequestBody Specialization specialization) {
		specializationService.createSpecialization(specialization);
	}

	/**
	 *	Gets all specialization
	 *
	 * @return all specialization
	 */
	@GetMapping("/specialization")
	private List<Specialization> getAllSpecialization() {
		return specializationService.getAllSpecialization();
	}




}
