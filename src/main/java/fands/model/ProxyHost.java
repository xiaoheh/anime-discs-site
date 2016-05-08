package fands.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "proxy_host")
public class ProxyHost {

    private String host;
    private int port;
    private int error;
    private int right;
    private Date date;

    public ProxyHost() {
    }

    public ProxyHost(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Column(length = 20, nullable = false)
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Column
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Column
    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    @Column
    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    @Transient
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyHost proxyHost = (ProxyHost) o;
        return new EqualsBuilder()
                .append(port, proxyHost.port)
                .append(host, proxyHost.host)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(host)
                .append(port)
                .toHashCode();
    }

}
