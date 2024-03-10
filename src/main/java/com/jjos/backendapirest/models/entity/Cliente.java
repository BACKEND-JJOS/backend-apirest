package com.jjos.backendapirest.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cliente")
@Data
public class Cliente implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "no puede estar vacio.")
    @Size(min = 4 , max = 50)
    @Column(nullable = false, length = 50)
    private String nombre;
    @NotEmpty(message = "no puede estar vacio.")
    @Size(min = 4 , max = 50)
    @Column(nullable = false, length = 50)
    private String apellido;

    @NotEmpty(message = "no puede estar vacio.")
    @Email
    @Column(nullable = false , length = 120, unique = true)
    private String email;

    @NotNull(message = "no puede estar vacio")
    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt;

    private String foto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", foreignKey = @ForeignKey(name = "fk_cliente_region"))
    @JsonIgnoreProperties({"hibernateLazyInitializer" , "handler"})
    private Region region;

}
