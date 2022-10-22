package net.codejava.controllers;

import net.codejava.entity.User;
import net.codejava.product.Product;
import net.codejava.product.ProductRepository;
import net.codejava.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class ProductController
{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProductRepository productRepo;

    @RequestMapping("/list")
    public String v (Model model, Principal principal)
    {
        User userFDSA = userRepo.getUserByUsername(principal.getName());
        model.addAttribute("user",userFDSA);
        model.addAttribute("listProducts", productRepo.findAll());
        return "products";
    }
    @RequestMapping("/new")
    public String v2(Model model)
    {
        Product product = new Product();
        model.addAttribute("product",product);
        return "new_product";
    }
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String v4(@ModelAttribute("product") Product product)
    {
        productRepo.save(product);
        return "redirect:/list";
    }
    @RequestMapping("/edit/{id}")
    public ModelAndView v5(@PathVariable(name = "id") Long id)
    {
        ModelAndView mav = new ModelAndView("edit_product");
        Product product = productRepo.getOne(id);
        mav.addObject("product",product);
        return mav;
    }
    @RequestMapping("/delete/{id}")
    public String v7(@PathVariable(name = "id") Long id)
    {
        productRepo.deleteById(id);
        return "redirect:/list";
    }

}
