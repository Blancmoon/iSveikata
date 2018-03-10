package lt.vtvpmc.ems.isveikata.medical_record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lt.vtvpmc.ems.isveikata.IsveikataApplication;
import lt.vtvpmc.ems.isveikata.appointment.Appointment;
import lt.vtvpmc.ems.isveikata.appointment.JpaAppointmentRepository;
import lt.vtvpmc.ems.isveikata.employees.Doctor;
import lt.vtvpmc.ems.isveikata.employees.JpaEmployeesRepository;
import lt.vtvpmc.ems.isveikata.icd.Icd;
import lt.vtvpmc.ems.isveikata.icd.JpaIcdRepository;
import lt.vtvpmc.ems.isveikata.mappers.MedicalRecordMapper;
import lt.vtvpmc.ems.isveikata.patient.JpaPatientRepository;

@Service
@Transactional
public class MedicalRecordService {

	@Autowired
	private JpaMedicalRecordRepository jpaMedicalRecordRepository;

	@Autowired
	private JpaAppointmentRepository jpaAppointmentRepository;

	@Autowired
	private JpaEmployeesRepository<Doctor> jpaEmployeesRepository;

	@Autowired
	private JpaPatientRepository jpaPatientRepository;

	@Autowired
	private JpaIcdRepository jpaIcdRepository;

	@Autowired
	private MedicalRecordMapper mapper;

	private String getUserName() {
		User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return loggedUser.getUsername();
	}

	private String getUserRole() {
		User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return loggedUser.getAuthorities().toString();
	}

	@PreAuthorize("hasRole('Doctor')")
	public void createNewRecord(Map<String, Object> map) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			Icd icd = jpaIcdRepository.findOne(mapper.convertValue(map.get("icdCode"), String.class));
			MedicalRecord medicalRecord = mapper.convertValue(map.get("medicalRecord"), MedicalRecord.class);
			Appointment appointment = mapper.convertValue(map.get("appointment"), Appointment.class);
			if (!medicalRecord.isRepetitive())
				icd.setCounter(icd.getCounter() + 1);
			medicalRecord.setIcd(icd);
			medicalRecord.setAppointment(appointment);
			medicalRecord.setDoctor((Doctor) jpaEmployeesRepository
					.findByUserName(mapper.convertValue(map.get("userName"), String.class)));
			medicalRecord
					.setPatient(jpaPatientRepository.findOne(mapper.convertValue(map.get("patientId"), String.class)));
			jpaMedicalRecordRepository.save(medicalRecord);
			jpaAppointmentRepository.save(appointment);
			IsveikataApplication.loggMsg(Level.INFO, getUserName(), getUserRole(), "created new medical record");
		} catch (Exception e) {
			IsveikataApplication.loggMsg(Level.WARNING, "public", "[public]",
					"Error creating new record " + map.toString() + "\r\n" + e.getMessage());
		}

	}

	@PreAuthorize("hasRole('Doctor') OR hasRole('Patient')")
	public MedicalRecordDto getMedicalRecord(Long medicalRecordId) {
		try {
			IsveikataApplication.loggMsg(Level.FINE, getUserName(), getUserRole(), "fetching medical record");
			return mapper.medicalRecordToDto(jpaMedicalRecordRepository.findOne(medicalRecordId));
		} catch (Exception e) {
			IsveikataApplication.loggMsg(Level.WARNING, getUserName(), getUserRole(),
					"Error fetching medical record id:" + medicalRecordId + "\r\n" + e.getMessage());
			return null;
		}
	}

	@PreAuthorize("hasRole('Doctor')")
	public List<Object> getDoctorWorkDaysStatistic(String userName, String dateFrom, String dateTill) {
		try {
			Long doctorId = jpaEmployeesRepository.findByUserName(userName).getId();
			IsveikataApplication.loggMsg(Level.FINE, getUserName(), getUserRole(),
					"fetching doctor working days statistics");
			return jpaMedicalRecordRepository.getDoctorWorkDaysStatistic(doctorId, dateFrom, dateTill);
		} catch (Exception e) {
			IsveikataApplication.loggMsg(Level.WARNING, getUserName(), getUserRole(),
					"Error fetching doctor working days statistic  from " + dateFrom + " to " + dateTill + "\r\n" + e.getMessage());
			return null;
		}

	}

	public List<Map<String, Object>> publicTlkStatistics() {
		try {
			List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
			List<Icd> list = jpaIcdRepository.findAllByOrderByCounterDesc(new PageRequest(0, 10));
			Integer total = jpaMedicalRecordRepository.getTotalNonRepetitiveMedicalRecordCount();
			for (Icd icd : list) {
				final Map<String, Object> objectMap = new HashMap<String, Object>();
				objectMap.put("title", icd.getTitle());
				objectMap.put("icdCode", icd.getIcdCode());
				objectMap.put("totalProc", (long) icd.getCounter() * (double) 100 / total);
				objectMap.put("totalCount", icd.getCounter());
				newList.add(objectMap);
				IsveikataApplication.loggMsg(Level.INFO, "public", "[public]", "fetching tlk statistics");
			}
			return newList;
		} catch (Exception e) {
			IsveikataApplication.loggMsg(Level.WARNING, "public", "[public]",
					"Error fetching public tlk satatistics " + e.getMessage());
			return null;
		}

	}
}
