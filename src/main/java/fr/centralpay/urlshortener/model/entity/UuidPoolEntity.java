package fr.centralpay.urlshortener.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "uuid_pool")
@Getter
@Setter
public class UuidPoolEntity {

    @Id
    @Column(name = "random_string")
    private String randomString;
}
