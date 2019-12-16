package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Model model, @RequestParam String name, @RequestParam String address) {
        signupRepository.save(new Signup(name, address));
        return "redirect:/signups";
    }
    
    @RequestMapping(value = "/signups", method = RequestMethod.GET)
    public String loadSignups(Model model) {
        model.addAttribute("signups", signupRepository.findAll());
        return "done";
    }
    
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String loadAdmin(Model model) {
        model.addAttribute("signups", signupRepository.findAll());
        model.addAttribute("admin", true);
        return "done";
    }
    
    // this line requires user to have admin rights
    // @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/signups/{id}", method = RequestMethod.DELETE)
    public String remove(@PathVariable Long id) {
        signupRepository.delete(id);
        return "redirect:/admin";
    }

}
