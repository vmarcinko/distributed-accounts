package me.distributedaccounts.web;

import me.distributedaccounts.mgmt.client.AccountManagementClient;
import me.distributedaccounts.mgmt.client.InsufficientMoneyException;
import me.distributedaccounts.search.client.AccountSearchClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class AccountsController {
    private AccountManagementClient accountManagementClient;
    private AccountSearchClient accountSearchClient;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(@RequestParam(required = false) String descriptionQueryValue, ModelMap model) {
        List<Map<String, Object>> accounts;
        if (descriptionQueryValue == null) {
            accounts = accountManagementClient.findAll();
        } else {
            accounts =  accountSearchClient.findByDescription(descriptionQueryValue);
            model.addAttribute("infoMessage", "Found " + accounts.size() + " accounts for description text: " + descriptionQueryValue);
        }
        model.addAttribute("accounts", accounts);
        return "index";
    }

    @RequestMapping(value = "/accounts/{accountId}", method = RequestMethod.GET)
    public String account(@PathVariable String accountId, ModelMap model) {
        Map<String, Object> account = accountManagementClient.find(UUID.fromString(accountId));
        model.put("account", account);
        return "account";
    }

    @RequestMapping(value = "/open", method = RequestMethod.POST)
    public String open(@RequestParam String description, RedirectAttributes redirectAttributes, ModelMap model) {
        UUID accountId = accountManagementClient.openAccount(description);
        redirectAttributes.addFlashAttribute("infoMessage", "Account opened under ID: " + accountId);
        return "redirect:/index";
    }

    @RequestMapping(value = "/accounts/{accountId}/close", method = RequestMethod.POST)
    public String depositMoney(@PathVariable String accountId, RedirectAttributes redirectAttributes, ModelMap model) {
        accountManagementClient.closeAccount(UUID.fromString(accountId));
        redirectAttributes.addFlashAttribute("infoMessage", "Account closed: " + accountId);
        return "redirect:/index";
    }

    @RequestMapping(value = "/accounts/{accountId}/depositMoney", method = RequestMethod.POST)
    public String depositMoney(@PathVariable String accountId, @RequestParam Float amount, RedirectAttributes redirectAttributes, ModelMap model) {
        accountManagementClient.depositMoney(UUID.fromString(accountId), amount);
        redirectAttributes.addFlashAttribute("infoMessage", "Money deposited: " + amount);
        return "redirect:/accounts/" + accountId;
    }

    @RequestMapping(value = "/accounts/{accountId}/withdrawMoney", method = RequestMethod.POST)
    public String withdrawMoney(@PathVariable String accountId, @RequestParam Float amount, RedirectAttributes redirectAttributes, ModelMap model) {
        try {
            accountManagementClient.withdrawMoney(UUID.fromString(accountId), amount);
            redirectAttributes.addFlashAttribute("infoMessage", "Money withdrawn: " + amount);
        } catch (InsufficientMoneyException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not enough money!");
        }
        return "redirect:/accounts/" + accountId;
    }

    public void setAccountManagementClient(AccountManagementClient accountManagementClient) {
        this.accountManagementClient = accountManagementClient;
    }

    public void setAccountSearchClient(AccountSearchClient accountSearchClient) {
        this.accountSearchClient = accountSearchClient;
    }
}
