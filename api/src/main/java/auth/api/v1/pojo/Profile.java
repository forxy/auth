package auth.api.v1.pojo;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import common.pojo.SimpleJacksonDateDeserializer;
import common.pojo.SimpleJacksonDateSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Profile extends User implements Serializable {

    private static final long serialVersionUID = -1828924339216439884L;

    private Date birthDate;

    private Set<String> telephones;

    private Address address;

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    public Date getBirthDate() {
        return birthDate;
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }

    public Set<String> getTelephones() {
        return telephones;
    }

    public void setTelephones(Set<String> telephones) {
        this.telephones = telephones;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;
        if (!super.equals(o)) return false;

        Profile profile = (Profile) o;

        if (address != null ? !address.equals(profile.address) : profile.address != null) return false;
        if (birthDate != null ? !birthDate.equals(profile.birthDate) : profile.birthDate != null) return false;
        if (telephones != null ? !telephones.equals(profile.telephones) : profile.telephones != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (telephones != null ? telephones.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "birthDate=" + birthDate +
                ", telephones=" + telephones +
                ", address=" + address +
                '}';
    }
}
