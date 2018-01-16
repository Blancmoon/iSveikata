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
	// 2. “/{patient_id}/" → return Patient
	// 3. “/{patient_id}/record” → return List<Record>
	// 4. “/{patient_id}/record/{record_id}” → return Record with appointmet with
	// doctor

	// 5. “/{patient_id}/recipe" → return List<Recipe> // kolkas nereikia

	// PUT:
	// 6. “/{patient_id}/password” → update Patient password

	// 1 -> List<Patient> (DOCTOR, ADMIN)
	@GetMapping("/")
	private List<Patient> getPatientList() {
		return getPatientService().getPatientList();
	}

	// 2 → return Patient
	@GetMapping("/{patientId}")
	private Patient getPatientById(@PathVariable Long patientId) {
		return patientService.getPatient(patientId);
	}

	// 3 → return List<Record>"
	@GetMapping("/{patientId}/record")
	private List<MedicalRecord> getRecordList(@PathVariable("patientId") Long patientId) {
		return patientService.getPatientRecordList(patientId);
	}

	// 4“/{patient_id}/record/{record_id}” → return Record with appointmet with
	// doctor
	@GetMapping("/record/{record_id}")
	private MedicalRecord getPatientRecordById(@PathVariable("record_id") Long id) {
		return patientService.getPatientRecordById(id);
	}

	// 6. “/{patient_id}/password” → update Patient password
	@PutMapping("/{patientId}/password")
	private void update(@RequestBody final Patient patient, @PathVariable final Long patientId) {
		patientService.updatePatientPassword(patient, patientId);
	}

}