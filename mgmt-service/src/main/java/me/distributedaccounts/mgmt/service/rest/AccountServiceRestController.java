package me.distributedaccounts.mgmt.service.rest;

import me.distributedaccounts.mgmt.service.Account;
import me.distributedaccounts.mgmt.service.AccountManagementService;
import me.distributedaccounts.mgmt.service.InsufficientFundsException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(consumes = {"text/*", "application/*"}, produces = {"application/json"})
public class AccountServiceRestController {
    private AccountManagementService accountManagementService;

    @RequestMapping(value = "/accounts/{accountId}", method = RequestMethod.GET)
    public Map<String, Object> find(@PathVariable String accountId) {
        Account account = accountManagementService.find(UUID.fromString(accountId));
        return account.toJacksonMap();
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public List<Map<String, Object>> findAll() {
        List<Account> accounts = accountManagementService.findAll();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Account account : accounts) {
            list.add(account.toJacksonMap());
        }
        return list;
    }

    @RequestMapping(value = "/accounts/open", method = RequestMethod.POST)
    public Map<String, Object> open(@RequestBody Map<String, Object> requestBody) {
        UUID accountId = UUID.fromString((String) requestBody.get("id"));
        String description = (String) requestBody.get("description");
        accountManagementService.open(accountId, description);

        return Collections.singletonMap("accountId", (Object) accountId.toString());
    }

    @RequestMapping(value = "/accounts/{accountId}/close", method = RequestMethod.POST)
    public void close(@PathVariable String accountId) {
        accountManagementService.close(UUID.fromString(accountId));
    }

    @RequestMapping(value = "/accounts/{accountId}/deposit", method = RequestMethod.POST)
    public void depositMoney(@PathVariable String accountId, @RequestBody Map<String, Object> requestBody) {
        float amount = Float.parseFloat(requestBody.get("amount").toString());
        accountManagementService.depositMoney(UUID.fromString(accountId), amount);
    }

    @RequestMapping(value = "/accounts/{accountId}/withdraw", method = RequestMethod.POST)
    public Map<String, Object> withdrawMoney(@PathVariable String accountId, @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> response = new HashMap<String, Object>();

        float amount = Float.parseFloat(requestBody.get("amount").toString());
        try {
            accountManagementService.withdrawMoney(UUID.fromString(accountId), amount);
        } catch (InsufficientFundsException e) {
            response.put("errorCode", "insufficient-funds");
        }
        return response;
    }

    public void setAccountManagementService(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }
}