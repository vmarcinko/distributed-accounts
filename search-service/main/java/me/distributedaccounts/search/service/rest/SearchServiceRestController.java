package me.distributedaccounts.search.service.rest;

import me.distributedaccounts.search.service.AccountDescriptionIndex;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(consumes = {"text/*", "application/*"}, produces = {"application/json"})
public class SearchServiceRestController {
    private AccountDescriptionIndex accountDescriptionIndex;

    @RequestMapping(value = "/accounts/search/{description}", method = RequestMethod.GET)
    public List<Map<String, Object>> find(@PathVariable String description) {
        List<Map<String,Object>> accounts = accountDescriptionIndex.findByDescription(description);
        return accounts;
    }

    public void setAccountDescriptionIndex(AccountDescriptionIndex accountDescriptionIndex) {
        this.accountDescriptionIndex = accountDescriptionIndex;
    }
}
