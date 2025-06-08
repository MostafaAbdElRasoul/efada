package com.efada.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.efada.security.EfadaSecurityUser;

public class EfadaAuditorAware implements AuditorAware<Long> {

	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth!=null && auth.getPrincipal() instanceof EfadaSecurityUser) {
			EfadaSecurityUser user = (EfadaSecurityUser) auth.getPrincipal();
			return Optional.of(user.getId());
		}
		return Optional.of(-1L);
	}
}
