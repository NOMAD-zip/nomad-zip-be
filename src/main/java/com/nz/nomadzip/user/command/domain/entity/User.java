package com.nz.nomadzip.user.command.domain.entity;

import com.nz.nomadzip.common.dto.StatusType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;


    @Enumerated(EnumType.STRING)
    private StatusType isDeleted;
}
