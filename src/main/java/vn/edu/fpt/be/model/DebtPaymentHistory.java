package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.RecordType;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "debt_payment_history")
public class DebtPaymentHistory extends Model{
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RecordType type;
    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "record_date", columnDefinition = "DATETIME(0)")
    private LocalDateTime recordDate;
    @Column(name = "note")
    private String note;
}
