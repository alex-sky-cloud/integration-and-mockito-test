package boot.junit5.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;


@Entity
public class Todo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;
    private String note;
    private String owner;

    private boolean finished;

    public Todo() {
    }

    private Todo(Builder builder) {
        setId(builder.id);
        setTitle(builder.title);
        setNote(builder.note);
        setOwner(builder.owner);
        setFinished(builder.finished);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Todo copy) {
        Builder builder = new Builder();
        builder.id = copy.getId();
        builder.title = copy.getTitle();
        builder.note = copy.getNote();
        builder.owner = copy.getOwner();
        builder.finished = copy.getFinished();
        return builder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return finished == todo.finished &&
                Objects.equals(id, todo.id) &&
                Objects.equals(title, todo.title) &&
                Objects.equals(note, todo.note) &&
                Objects.equals(owner, todo.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, note, owner, finished);
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", owner='" + owner + '\'' +
                ", finished=" + finished +
                '}';
    }


    public static final class Builder {
        private Long id;
        private @NotBlank String title;
        private String note;
        private String owner;
        private boolean finished;

        private Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder title(@NotBlank String val) {
            title = val;
            return this;
        }

        public Builder note(String val) {
            note = val;
            return this;
        }

        public Builder owner(String val) {
            owner = val;
            return this;
        }

        public Builder finished(boolean val) {
            finished = val;
            return this;
        }

        public Todo build() {
            return new Todo(this);
        }
    }
}
