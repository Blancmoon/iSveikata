package lt.vtvpmc.ems.isveikata.mappers;

import java.util.List;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import lt.vtvpmc.ems.isveikata.medical_record.MedicalRecord;
import lt.vtvpmc.ems.isveikata.medical_record.MedicalRecordDto;

@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {
	String delimiter = " ";

	@Mappings({ 
		@Mapping(source = "appointment.date", target = "appointmentDate"),
		@Mapping(source = "appointment.duration", target = "appoitmentDuration"),
		@Mapping(source = "appointment.description", target = "appointmentDescription"),
		@Mapping(source = "icd.icdCode", target = "icdCode"),
		@Mapping(source = "icd.title", target = "icdDescription"),
		@Mapping(target = "doctorFullName", source="doctor.lastName")
		})
	MedicalRecordDto medicalRecordToDto(MedicalRecord medicalRecord);

	@InheritConfiguration
	List<MedicalRecordDto> medicalRecordsToDto(List<MedicalRecord> medicalRecords);

}