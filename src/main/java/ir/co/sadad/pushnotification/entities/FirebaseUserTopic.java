package ir.co.sadad.pushnotification.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * a table between firebase_user and topic tables
 */
@Entity
@Table(name = "FIREBASE_USER_TOPIC"/*, uniqueConstraints = {@UniqueConstraint(columnNames = {"FIREBASE_ID", "TOPIC_ID"}, name = "UKFIREBASE_USER_TOPIC")}*/)
@Getter
@Setter
public class FirebaseUserTopic extends AbstractEntity {

    @ManyToOne
//    @JoinColumns ({
//            @JoinColumn(name="emp_number", referencedColumnName = "emp_no"),
//            @JoinColumn(name="title", referencedColumnName = "title"),
//            @JoinColumn(name="from_date", referencedColumnName = "from_date")
//    })
    @JoinColumn(name = "FIREBASE_ID", referencedColumnName = "ID", nullable = false, foreignKey = @ForeignKey(name = "FKFIREBASE_USER_TOPIC_TO_FIREBASE_USER"))
    @NotNull
    private FirebaseUser firebaseUser;

    @ManyToOne//(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "TOPIC_ID", referencedColumnName = "ID", nullable = false, foreignKey = @ForeignKey(name = "FKFIREBASE_USER_TOPIC_TO_TOPIC"))
    @NotNull
    private Topic topic;
}
