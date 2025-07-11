package com.extract.ExtraactAllData.model;
//
//import lombok.Data;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Data
//public class ExtractedData {
//    private String fileNo;
//    private String location;
//    private String mainPersonName;
//    private LocalDate recordDate;
//    private FamilyDetails familyDetails;
//    private LocationDetails locationDetails;
//    private PersonalDetails personalDetails;
//    private RitualDetails ritualDetails;
//
//    @Data
//    public static class FamilyDetails {
//        private String pitajiKaNaam;
//        private String dadajiKaNaam;
//        private List<FamilyMember> familyMembers;
//    }
//
//    @Data
//    public static class FamilyMember {
//        private String name;
//        private String rishta;
//
//
//        public FamilyMember() {}
//
//        public FamilyMember(String name, String rishta) {
//            this.name = name;
//            this.rishta = rishta;
//        }
//    }
//
//    @Data
//    public static class LocationDetails {
//        private String jila;
//        private String tahsil;
//    }
//
//    @Data
//    public static class PersonalDetails {
//        private String jati;
//        private String upjati;
//        private String ling;
//    }
//
//    @Data
//    public static class RitualDetails {
//        private String anusthan_ka_naam;
//        private String kiska_anusthan;
//    }
//}
//



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class ExtractedData {
    private String imageNo;
    private String pandaName;
    private String bahiName; // Assuming Bahi Name and Bahi Number are combined or separate as needed
    private String folioNo;
    private String dataPosition; // Which paragraph it is
    private String district;
    private String tehsil;
    private String station;
    private String postOffice;
    private String cityVillage;
    private String fromWhichPlace;
    private String caste;
    private String subcaste;
    private String individualID; // ID for each person within a paragraph
    private String givenName;
    private String surname;
    private String relation;
    private String gender;
    private String familyID; // Left blank as per instructions
    private String ritualName;
    private String whoseRitual1;
    private String whoseRitual2;
    private String contactNo1;
    private String contactNo2;
    private String flagsAndException; // Non-understandable stuff
    private String additionalInforma; // Date is often here, or other notes
    private String dateOfRitual;
}