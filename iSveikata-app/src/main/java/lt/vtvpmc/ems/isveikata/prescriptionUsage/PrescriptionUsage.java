package lt.vtvpmc.ems.isveikata.prescriptionUsage;


import lombok.Data;
import lt.vtvpmc.ems.isveikata.employees.Druggist;
import lt.vtvpmc.ems.isveikata.prescription.Prescription;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Data
public class PrescriptionUsage {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private Prescription prescription;

    @ManyToOne
    private Druggist druggist;

    @Type(type = "date")
    private Date usageDate;

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public void setDruggist(Druggist druggist) {
        this.druggist = druggist;
    }
}