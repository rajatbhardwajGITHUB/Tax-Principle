    package com.example.Usermangement.Bean;

    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;

    @Entity
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String email;
        private String password;
        private String role;
        private String phoneNumber;
        private String authenticationToken;
        private String refreshToken;
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public String getPhoneNumber() {
            return phoneNumber;
        }
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        public String getAuthenticationToken() {
            return authenticationToken;
        }
        public void setAuthenticationToken(String authenticationToken) {
            this.authenticationToken = authenticationToken;
        }
        public String getRefreshToken() {
            return refreshToken;
        }
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
        
        

        
    }
