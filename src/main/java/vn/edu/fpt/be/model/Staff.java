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
@Table(name = "staff")
public class Staff extends Model{
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
