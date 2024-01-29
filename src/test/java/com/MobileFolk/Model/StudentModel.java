package com.MobileFolk.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentModel {
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String mobile;
    private List<String> subject;
    private List<String> hobbies;
    private String picture;
    private String currentAddress;
}
