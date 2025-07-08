package com.extract.ExtraactAllData.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ExtractedData {
    private String fileNo;
    private String location;
    private String mainPersonName;
    private LocalDate recordDate;
    private FamilyDetails familyDetails;
    private LocationDetails locationDetails;
    private PersonalDetails personalDetails;
    private RitualDetails ritualDetails;

    @Data
    public static class FamilyDetails {
        private String pitajiKaNaam;
        private String dadajiKaNaam;
        private List<FamilyMember> familyMembers;
    }

    @Data
    public static class FamilyMember {
        private String name;
        private String rishta;


        public FamilyMember() {}

        public FamilyMember(String name, String rishta) {
            this.name = name;
            this.rishta = rishta;
        }
    }

    @Data
    public static class LocationDetails {
        private String jila;
        private String tahsil;
    }

    @Data
    public static class PersonalDetails {
        private String jati;
        private String upjati;
        private String ling;
    }

    @Data
    public static class RitualDetails {
        private String anusthan_ka_naam;
        private LocalDate anusthanKiTithi1;
        private LocalDate anusthanKiTithi2;
        private String kiska_anusthan;
    }
}

