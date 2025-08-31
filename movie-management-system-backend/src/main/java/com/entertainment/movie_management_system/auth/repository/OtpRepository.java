// The OtpRepository should be an interface, not a class, and should extend JpaRepository.
// Also, the method signature should be inside the interface.

package com.entertainment.movie_management_system.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.entertainment.movie_management_system.auth.entity.OtpTable;

public interface OtpRepository extends JpaRepository<OtpTable, Long> {
    OtpTable findByEmail(String email); // Find OTP details by email from OtpTable
}
