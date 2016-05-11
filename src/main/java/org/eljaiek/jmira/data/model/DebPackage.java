
package org.eljaiek.jmira.data.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author eduardo.eljaiek
 */
public class DebPackage implements Serializable {   
    
    private String name;
    
    private String version;
    
    private String description;
    
    private String relativeUrl;
    
    private long length;
    
    private String localUrl;
    
    private String remoteUrl;
    
    private String checksum;

    public DebPackage() {
    }

    public DebPackage(String name, String version, String description, String relativeUrl, long length, String localUrl, String remoteUrl, String checksum) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.relativeUrl = relativeUrl;
        this.length = length;
        this.localUrl = localUrl;
        this.remoteUrl = remoteUrl;
        this.checksum = checksum;
    }    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }   

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }   

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final DebPackage other = (DebPackage) obj;
        return Objects.equals(this.name, other.name);
    }
    
    
}
