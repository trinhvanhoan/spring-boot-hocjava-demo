package hocjava.security;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import hocjava.entity.User;
import hocjava.repository.UserRepository;

@ControllerAdvice
public class GlobalDataAdvice {
	@Autowired 
	private UserRepository userRepository;

    @ModelAttribute("userFullName")
    public String getFullName(Principal principal) {
        if (principal == null) return "Khách";
        
        return userRepository.findByUsername(principal.getName())
                             .map(User::getFullName)
                             .orElse("Unknown");
    }
}
