package de.vptr.midas.api.rest.entity;

import java.util.Collections;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user_accounts")
public class UserAccountEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    public String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    public List<UserAccountMetaEntity> userAccountMetas;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL)
    public List<UserPaymentEntity> outgoingPayments;

    @OneToMany(mappedBy = "targetAccount", cascade = CascadeType.ALL)
    public List<UserPaymentEntity> incomingPayments;

    // Helper method to find account by name
    public static UserAccountEntity findByName(final String name) {
        return find("name", name).firstResult();
    }

    // Helper method to get users associated with this account
    public List<UserEntity> getAssociatedUsers() {
        if (this.userAccountMetas == null) {
            return Collections.emptyList();
        }
        return this.userAccountMetas.stream()
                .map(meta -> meta.user)
                .toList();
    }
}
