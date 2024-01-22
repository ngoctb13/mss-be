package vn.edu.fpt.be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.Status;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;
    @Column(name = "store_name")
    private String storeName;
    @Column(name = "address")
    private String address;
    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;
    @Column(name = "status", columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
}
