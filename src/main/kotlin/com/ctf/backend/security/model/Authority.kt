package com.ctf.backend.security.model

import com.ctf.backend.util.USER_ROLE
import org.springframework.security.core.GrantedAuthority

enum class Authority (val authority: GrantedAuthority) {
    USER(GrantedAuthority {USER_ROLE})
}