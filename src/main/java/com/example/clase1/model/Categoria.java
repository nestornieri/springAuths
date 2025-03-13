package com.example.clase1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "nombre")
    private String nomCategoria;

    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Producto> productos;

    @JsonIgnore
    public Categoria(){

    }

    public Categoria(Integer idCategoria, String descripcion, String nomCategoria, List<Producto> productos) {
        this.idCategoria = idCategoria;
        this.descripcion = descripcion;
        this.nomCategoria = nomCategoria;
        this.productos = productos;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNomCategoria() {
        return nomCategoria;
    }

    public void setNomCategoria(String nomCategoria) {
        this.nomCategoria = nomCategoria;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", descripcion='" + descripcion + '\'' +
                ", nomCategoria='" + nomCategoria + '\'' +
                ", productos=" + productos +
                '}';
    }
}