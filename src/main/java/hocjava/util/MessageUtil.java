package hocjava.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class MessageUtil {
	private static final String FLASH_KEY = "flashMessages";
	
	@SuppressWarnings("unchecked")
	private static Map<String, List<String>> getMessages(Object container) {
		Map<String, List<String>> flashMessages = null;
        if (container instanceof RedirectAttributes ra) {
        	flashMessages = (Map<String, List<String>>) ra.getFlashAttributes().get(FLASH_KEY);
        } else if (container instanceof Model model) {
        	flashMessages = (Map<String, List<String>>) model.getAttribute(FLASH_KEY);
        }
        return flashMessages;
	}	
    /*
     * Add message
     */
	private static void addMessage(Object container, String type, String message) {
		
		// Không xử lý với container khác Model hoặc RedirectAttributes
		if (!(container instanceof Model || container instanceof RedirectAttributes)) {
			return;
		}
		
        String bootstrapType = "error".equalsIgnoreCase(type) ? "danger" : type.toLowerCase();
        
        var flashMessages = getMessages(container);
        if (flashMessages == null) flashMessages = new HashMap<>();
        
        flashMessages.computeIfAbsent(bootstrapType, k -> new ArrayList<>()).add(message);

        if (container instanceof RedirectAttributes ra) {
        	ra.addFlashAttribute(FLASH_KEY, flashMessages);
        } else if (container instanceof Model model) {
        	model.addAttribute(FLASH_KEY, flashMessages);
        }
    }

    // CÁC PUBLIC METHODS
    public static void success(Object container, String message) { addMessage(container, "success", message); }
    public static void error(Object container, String message)   { addMessage(container, "danger", message); }
    public static void warning(Object container, String message) { addMessage(container, "warning", message); }
    public static void info(Object container, String message)    { addMessage(container, "info", message); }

    public static void error(Object container, BindingResult result) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(err -> addMessage(container, "error", err.getDefaultMessage()));
        }
    }
    
    public static void error(Object container, Object error) {
    	if (error instanceof BindingResult result) {
    		error(container, result);
    	}
    	else {
    		error(container, error.toString());
    	}
    }
}
