package lt.vtvpmc.ems.isveikata.patient;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lt.vtvpmc.ems.isveikata.medical_record.MedicalRecord;
import org.hibernate.validator.constraints.Length;

import lt.vtvpmc.ems.isveikata.employees.Doctor;


@Entity
public class Patient {

	@Id
	@Length(min = 11, max = 11)
	@NotNull
	@Column (unique = true)	
	private String personalCode; 
	
	private Date birthDate;
	@NotNull
	private String firstName;
	@NotNull
	private String  lastName;
	@NotNull
	private String  password;
	
	@ManyToOne
	private Doctor doctor;

	@OneToMany(mappedBy = "patient")
	private List<MedicalRecord> medicalRecords;
	
	
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public String getPersonalCode() {
		return personalCode;
	}
	public void setPersonalCode(String personalCode) {
		this.personalCode = personalCode;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	
	
	

	
}
