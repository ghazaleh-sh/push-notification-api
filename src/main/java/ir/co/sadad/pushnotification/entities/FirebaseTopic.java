package ir.co.sadad.pushnotification.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * @author g.shahrokhabadi
 * <p>
 * stores topics of messages
 */
@Entity
@Table(name = "FIREBASE_TOPIC")
@Getter
@Setter
public class FirebaseTopic extends AbstractEntity {

    @Column(name = "TOPIC_NAME", length = 100)
    private String topicName;

//    @OneToMany(mappedBy = "FIREBASE_USER_TOPIC")
//    private Set<FirebaseUserTopic> firebaseUserTopic;
}
