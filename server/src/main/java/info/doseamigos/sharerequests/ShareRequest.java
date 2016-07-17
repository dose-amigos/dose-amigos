package info.doseamigos.sharerequests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import info.doseamigos.amigousers.AmigoUser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a share amigo request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareRequest {

    private Long id;
    private AmigoUser sharedAmigo;
    private String targetUserEmail;
    private Boolean accepted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AmigoUser getSharedAmigo() {
        return sharedAmigo;
    }

    public void setSharedAmigo(AmigoUser sharedAmigo) {
        this.sharedAmigo = sharedAmigo;
    }

    public String getTargetUserEmail() {
        return targetUserEmail;
    }

    public void setTargetUserEmail(String targetUserEmail) {
        this.targetUserEmail = targetUserEmail;
    }

    public Boolean isApproved() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
