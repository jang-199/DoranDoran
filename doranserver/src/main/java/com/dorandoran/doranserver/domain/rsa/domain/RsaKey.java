package com.dorandoran.doranserver.domain.rsa.domain;

import jakarta.persistence.*;
import lombok.*;

import java.security.PrivateKey;
import java.security.PublicKey;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsaKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long rsaId;

    @Column(length = 3000)
    private String PRIVATE_KEY;

    @Column(length = 3000)
    private String PUBLIC_KEY;
}
