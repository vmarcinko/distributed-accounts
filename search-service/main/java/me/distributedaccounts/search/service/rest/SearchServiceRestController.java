package me.distributedaccounts.search.service.rest;

import me.distributedaccounts.search.service.AccountSearchService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(consumes = {"text/*", "application/*"}, produces = {"application/json"})
public class SearchServiceRestController {
    private AccountSearchService accountSearchService;

    @RequestMapping(value = "/accounts/search/{description}", method = RequestMethod.GET)
    public List<Map<String, Object>> find(@PathVariable String description) {
        List<Map<String, Object>> accounts = accountSearchService.findByDescription(description);
        return convertAccountIdsToIds(accounts);
    }

    private List<Map<String, Object>> convertAccountIdsToIds(List<Map<String, Object>> original) {
        for (Map<String, Object> indexedAccount : original) {
            Object accountId = indexedAccount.remove("accountId");
            indexedAccount.put("id", accountId);
        }
        return original;
    }

    public void setAccountSearchService(AccountSearchService accountSearchService) {
        this.accountSearchService = accountSearchService;
    }
}
