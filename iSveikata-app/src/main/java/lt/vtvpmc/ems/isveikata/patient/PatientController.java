package lt.vtvpmc.ems.isveikata.patient;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lt.vtvpmc.ems.isveikata.medical_record.MedicalRecord;

@RestController
@RequestMapping(value = "/api/patient")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientController {

	@Autowired
	private PatientService patientService;

	private PatientService getPatientService() {
		return patientService;
	}

	private void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	// PATIENT: /api/patient
	//
	// GET:
	// 1. “/” → return List<Patient> (DOCTOR, ADMIN)
	// 2. “/{patient_id}/" → return active Patient
	// 3. “/{patient_id}/record” → return List<Record>
	// 4. “/{patient_id}/record/{record_id}” → return Record with appointmet with
	// doctor

	// 5. “/{patient_id}/recipe" → return List<Recipe> // kolkas nereikia

	// PUT:
	// 6. “/{patient_id}/password” → update Patient password

	/**
	 * Gets all active patients
	 * URL: /api/patient
	 *
	 * @return list of all patient
	 */
	@GetMapping("/withoutdoctor")
	private List<Patient> getPatientList() {
		return getPatientService().getPatientList();
	}

	/**
	 * Gets all active and not bind with doctor patients
	 * URL: /api/patient
	 *
	 * @return all active and not bind with doctor patients
	 */
	@GetMapping("/")
	private List<Patient> getPatientListWithoutDoctor() {
		return getPatientService().getPatientListWithoutDoctor();
	}

	
	/**
	 * Gets patient by patientId
	 * @param patientId
	 * @return patient by patientId
	 */
	@GetMapping("/{patientId}")
	private Patient getPatientById(@PathVariable Long patientId) {
		return patientService.getPatient(patientId);
	}

	/**
	 * Gets all records
	 * URL: api/{patientId}/record
	 * @param patientId
	 * @return list of all patient
	 */
	@GetMapping("/{patientId}/record")
	private List<MedicalRecord> getRecordList(@PathVariable("patientId") Long patientId) {
		return patientService.getPatientRecordList(patientId);
	}

	/**
	 * Gets record with appointmet and with doctor by record id
	 * URL: "/record/{record_id}"
	 * @param id
	 * @return record with appointmet and with doctor by record id
	 */
	@GetMapping("/record/{record_id}")
	private MedicalRecord getPatientRecordById(@PathVariable("record_id") Long id) {
		return patientService.getPatientRecordById(id);
	}

	/**
	 * Change patient password in data base. URL: /{patient_id}/password
	 * 
	 * @param patient
	 *            
	 * @param patientId
	 */
	@PutMapping("/{patientId}/password")
	private void update(@RequestBody final Patient patient, @PathVariable final Long patientId) {
		patientService.updatePatientPassword(patient, patientId);
	}

}
