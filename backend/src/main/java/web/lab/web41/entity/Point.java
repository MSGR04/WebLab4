package web.lab.web41.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "points")
@Getter
@Setter
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(-3)
    @Max(3)
    private Double x;

    @NotNull
    @Min(-3)
    @Max(3)
    private Double y;

    @NotNull
    @Min(1)
    @Max(3)
    private Double r;

    @NotNull
    private Boolean hit;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
