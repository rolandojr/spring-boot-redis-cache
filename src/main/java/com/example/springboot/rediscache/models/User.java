package com.example.springboot.rediscache.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;


@Getter
@Setter
@Table("USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 6758222469555393885L;

    @Id
    private Long id;
    private String name;
    private String email;

}
