package de.vptr.midas.api.rest.entity;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "user_accounts")
public class UserAccount extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    public String name;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    public List<UserAccountMeta> userAccountMetas;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL)
    public List<UserPayment> outgoingPayments;

    @OneToMany(mappedBy = "targetAccount", cascade = CascadeType.ALL)
    public List<UserPayment> incomingPayments;

    // Helper method to find account by name
    public static UserAccount findByName(final String name) {
        return find("name", name).firstResult();
    }

    // Helper method to get users associated with this account
    public List<User> getAssociatedUsers() {
        return this.userAccountMetas.stream()
                .map(meta -> meta.user)
                .toList();
    }
}
