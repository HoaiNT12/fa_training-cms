package fa.training.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profile")
@Where(clause = "is_deleted = FALSE")
public class Profile extends Auditable {

    @Id
    private Long id;

	private String fullName;

    private String address;

    private String phoneNumber;

    private String email;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
