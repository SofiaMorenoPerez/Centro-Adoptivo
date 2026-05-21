package co.edu.unbosque.centroadoptivo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String usuarioEjecutor;

 
    private String accion;

  
    @Column(length = 500)
    private String descripcion;

  
    private LocalDateTime fecha;


    private boolean exitoso;

    public Auditoria() {}

    public Auditoria(String usuarioEjecutor, String accion, String descripcion, boolean exitoso) {
        this.usuarioEjecutor = usuarioEjecutor;
        this.accion = accion;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
        this.exitoso = exitoso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuarioEjecutor() { return usuarioEjecutor; }
    public void setUsuarioEjecutor(String usuarioEjecutor) { this.usuarioEjecutor = usuarioEjecutor; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isExitoso() { return exitoso; }
    public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
}