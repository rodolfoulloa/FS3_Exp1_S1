package cl.duoc.biblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA Libro (tabla LIBROS en Oracle Autonomous Database).
 * Mantiene los atributos de la semana 1: id, titulo, autor, anioPublicacion y genero.
 *
 * Patrón BUILDER: la clase interna {@link Builder} construye el libro paso a paso:
 * {@code Libro.builder().titulo("Rayuela").autor("Julio Cortázar").anioPublicacion(1963).genero("Novela").build()}.
 */
@Entity
@Table(name = "LIBROS")
public class Libro implements Identificable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITULO", nullable = false, length = 200)
    private String titulo;

    @Column(name = "AUTOR", nullable = false, length = 150)
    private String autor;

    @Column(name = "ANIO_PUBLICACION", nullable = false)
    private Integer anioPublicacion;

    @Column(name = "GENERO", nullable = false, length = 80)
    private String genero;

    /** Constructor vacío requerido por JPA. */
    protected Libro() {
    }

    private Libro(Builder builder) {
        this.id = builder.id;
        this.titulo = builder.titulo;
        this.autor = builder.autor;
        this.anioPublicacion = builder.anioPublicacion;
        this.genero = builder.genero;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    /** Patrón Builder: construye un {@link Libro} paso a paso. */
    public static final class Builder {
        private Long id;
        private String titulo;
        private String autor;
        private Integer anioPublicacion;
        private String genero;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder titulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public Builder autor(String autor) {
            this.autor = autor;
            return this;
        }

        public Builder anioPublicacion(Integer anioPublicacion) {
            this.anioPublicacion = anioPublicacion;
            return this;
        }

        public Builder genero(String genero) {
            this.genero = genero;
            return this;
        }

        public Libro build() {
            return new Libro(this);
        }
    }
}
