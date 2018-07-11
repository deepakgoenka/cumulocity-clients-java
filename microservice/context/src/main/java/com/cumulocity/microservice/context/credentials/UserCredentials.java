package com.cumulocity.microservice.context.credentials;

public class UserCredentials implements Credentials {
    private String tenant;
    private String username;
    private String password;
    private String identifier;
    private String tfaToken;
    private String appKey;

    @java.beans.ConstructorProperties({"tenant", "username", "password", "identifier", "tfaToken", "appKey"})
    public UserCredentials(String tenant, String username, String password, String identifier, String tfaToken, String appKey) {
        this.tenant = tenant;
        this.username = username;
        this.password = password;
        this.identifier = identifier;
        this.tfaToken = tfaToken;
        this.appKey = appKey;
    }

    public UserCredentials() {
    }

    public static UserCredentialsBuilder builder() {
        return new UserCredentialsBuilder();
    }

    public String getTenant() {
        return this.tenant;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getTfaToken() {
        return this.tfaToken;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setTfaToken(String tfaToken) {
        this.tfaToken = tfaToken;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof UserCredentials)) return false;
        final UserCredentials other = (UserCredentials) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$tenant = this.getTenant();
        final Object other$tenant = other.getTenant();
        if (this$tenant == null ? other$tenant != null : !this$tenant.equals(other$tenant)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
        final Object this$identifier = this.getIdentifier();
        final Object other$identifier = other.getIdentifier();
        if (this$identifier == null ? other$identifier != null : !this$identifier.equals(other$identifier))
            return false;
        final Object this$tfaToken = this.getTfaToken();
        final Object other$tfaToken = other.getTfaToken();
        if (this$tfaToken == null ? other$tfaToken != null : !this$tfaToken.equals(other$tfaToken)) return false;
        final Object this$appKey = this.getAppKey();
        final Object other$appKey = other.getAppKey();
        if (this$appKey == null ? other$appKey != null : !this$appKey.equals(other$appKey)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $tenant = this.getTenant();
        result = result * PRIME + ($tenant == null ? 43 : $tenant.hashCode());
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $identifier = this.getIdentifier();
        result = result * PRIME + ($identifier == null ? 43 : $identifier.hashCode());
        final Object $tfaToken = this.getTfaToken();
        result = result * PRIME + ($tfaToken == null ? 43 : $tfaToken.hashCode());
        final Object $appKey = this.getAppKey();
        result = result * PRIME + ($appKey == null ? 43 : $appKey.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserCredentials;
    }

    public UserCredentials withTenant(String tenant) {
        return this.tenant == tenant ? this : new UserCredentials(tenant, this.username, this.password, this.identifier, this.tfaToken, this.appKey);
    }

    public UserCredentials withUsername(String username) {
        return this.username == username ? this : new UserCredentials(this.tenant, username, this.password, this.identifier, this.tfaToken, this.appKey);
    }

    public UserCredentials withPassword(String password) {
        return this.password == password ? this : new UserCredentials(this.tenant, this.username, password, this.identifier, this.tfaToken, this.appKey);
    }

    public UserCredentials withIdentifier(String identifier) {
        return this.identifier == identifier ? this : new UserCredentials(this.tenant, this.username, this.password, identifier, this.tfaToken, this.appKey);
    }

    public UserCredentials withTfaToken(String tfaToken) {
        return this.tfaToken == tfaToken ? this : new UserCredentials(this.tenant, this.username, this.password, this.identifier, tfaToken, this.appKey);
    }

    public UserCredentials withAppKey(String appKey) {
        return this.appKey == appKey ? this : new UserCredentials(this.tenant, this.username, this.password, this.identifier, this.tfaToken, appKey);
    }

    public String toString() {
        return "UserCredentials(tenant=" + this.getTenant() + ", username=" + this.getUsername() + ")";
    }

    public static class UserCredentialsBuilder {
        private String tenant;
        private String username;
        private String password;
        private String identifier;
        private String tfaToken;
        private String appKey;

        UserCredentialsBuilder() {
        }

        public UserCredentials.UserCredentialsBuilder tenant(String tenant) {
            this.tenant = tenant;
            return this;
        }

        public UserCredentials.UserCredentialsBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserCredentials.UserCredentialsBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserCredentials.UserCredentialsBuilder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public UserCredentials.UserCredentialsBuilder tfaToken(String tfaToken) {
            this.tfaToken = tfaToken;
            return this;
        }

        public UserCredentials.UserCredentialsBuilder appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public UserCredentials build() {
            return new UserCredentials(tenant, username, password, identifier, tfaToken, appKey);
        }

        public String toString() {
            return "UserCredentials.UserCredentialsBuilder(tenant=" + this.tenant + ", username=" + this.username + ", password=" + this.password + ", identifier=" + this.identifier + ", tfaToken=" + this.tfaToken + ", appKey=" + this.appKey + ")";
        }
    }
}