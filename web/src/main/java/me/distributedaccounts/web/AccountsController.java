package me.distributedaccounts.web;

import me.distributedaccounts.mgmt.client.AccountManagementClient;
import me.distributedaccounts.search.client.AccountSearchClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AccountsController {

    private AccountManagementClient accountManagementClient;
    private AccountSearchClient accountSearchClient;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap model) {
        System.out.println("### TU SAM!!!");
        return "index";
    }

    public void setAccountManagementClient(AccountManagementClient accountManagementClient) {
        this.accountManagementClient = accountManagementClient;
    }

    public void setAccountSearchClient(AccountSearchClient accountSearchClient) {
        this.accountSearchClient = accountSearchClient;
    }
}
