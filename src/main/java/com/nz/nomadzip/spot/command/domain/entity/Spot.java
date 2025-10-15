package com.nz.nomadzip.spot.command.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "spot")
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spotId;
}
