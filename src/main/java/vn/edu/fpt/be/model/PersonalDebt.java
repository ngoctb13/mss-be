package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "personal_debt")
public class PersonalDebt extends Model{
    @Column(name = "creditor_name", nullable = false)
    private String creditorName;
    @Column(name = "creditor_phone")
    private String creditorPhone;
    @Column(name = "creditor_address")
    private String creditorAddress;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "note")
    private String note;
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
