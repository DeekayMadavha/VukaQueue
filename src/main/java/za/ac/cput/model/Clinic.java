package za.ac.cput.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "clinics")
public class Clinic {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "clinic_id", updatable = false, nullable = false)
    private String clinicId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String suburb;

    @Column(nullable = false)
    private String address;

    private String phone;

    @Column(nullable = false)
    private int maxDailyCapacity;

    protected Clinic() {}

    private Clinic(Builder builder) {
        this.clinicId = builder.clinicId;
        this.name = builder.name;
        this.province = builder.province;
        this.city = builder.city;
        this.suburb = builder.suburb;
        this.address = builder.address;
        this.phone = builder.phone;
        this.maxDailyCapacity = builder.maxDailyCapacity;
    }

    public String getClinicId() { return clinicId; }
    public String getName() { return name; }
    public String getProvince() { return province; }
    public String getCity() { return city; }
    public String getSuburb() { return suburb; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public int getMaxDailyCapacity() { return maxDailyCapacity; }

    public static class Builder {
        private String clinicId;
        private String name;
        private String province;
        private String city;
        private String suburb;
        private String address;
        private String phone;
        private int maxDailyCapacity;

        public Builder setClinicId(String clinicId) { this.clinicId = clinicId; return this; }
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setProvince(String province) { this.province = province; return this; }
        public Builder setCity(String city) { this.city = city; return this; }
        public Builder setSuburb(String suburb) { this.suburb = suburb; return this; }
        public Builder setAddress(String address) { this.address = address; return this; }
        public Builder setPhone(String phone) { this.phone = phone; return this; }
        public Builder setMaxDailyCapacity(int maxDailyCapacity) { this.maxDailyCapacity = maxDailyCapacity; return this; }

        public Builder copy(Clinic c) {
            this.clinicId = c.clinicId;
            this.name = c.name;
            this.province = c.province;
            this.city = c.city;
            this.suburb = c.suburb;
            this.address = c.address;
            this.phone = c.phone;
            this.maxDailyCapacity = c.maxDailyCapacity;
            return this;
        }

        public Clinic build() { return new Clinic(this); }
    }
}