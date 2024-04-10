package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "debt_record")
public class DebtRecord extends Model {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @Column(name = "debt_amount")
    private Double debtAmount;
    @Column(name = "record_date", columnDefinition = "DATETIME(0)")
    private LocalDateTime recordDate;
    @Column(name = "note")
    private String note;
}
