package com.jjos.backendapirest.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "region")
@Data
public class Region  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "no puede estar vacio.")
    @Size(min = 4 , max = 50)
    @Column(nullable = false, length = 50)
    private String nombre;
}
