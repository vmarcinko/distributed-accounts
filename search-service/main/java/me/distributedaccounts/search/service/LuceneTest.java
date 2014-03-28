package me.distributedaccounts.search.service;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LuceneTest {

    public static void main(String[] args) throws Exception {
        AccountDescriptionIndex accountDescriptionIndex = constructAccountDescriptionIndex();
        accountDescriptionIndex.addAccountDescription("broj1", "Jako mali account!");
        accountDescriptionIndex.addAccountDescription("broj2", "Jako veliki account!");

        AccountSearchService accountSearchService = (AccountSearchService) accountDescriptionIndex;

        List<Map<String, Object>> accounts = accountSearchService.findByDescription("jako");
        System.out.println("accounts = " + accounts);

        System.out.println("-------------------");
        accounts = accountSearchService.findByDescription("mali");
        System.out.println("accounts = " + accounts);

        System.out.println("-------------------");
        accountDescriptionIndex.removeAccountDescription("broj2");
        accounts = accountSearchService.findByDescription("jako");
        System.out.println("accounts = " + accounts);

        destroy(accountDescriptionIndex);
    }

    private static void destroy(AccountDescriptionIndex accountDescriptionIndex) throws Exception {
        ((AccountSearchServiceImpl) accountDescriptionIndex).destroy();
    }

    private static AccountDescriptionIndex constructAccountDescriptionIndex() throws Exception {
        AccountSearchServiceImpl index = new AccountSearchServiceImpl();
        index.setIndexFile(new File("C:/temp/searchDistAccountsIndex"));
        index.afterPropertiesSet();
        return index;
    }
}
