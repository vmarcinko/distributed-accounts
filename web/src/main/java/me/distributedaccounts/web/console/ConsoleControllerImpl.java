package me.distributedaccounts.web.console;

import me.distributedaccounts.mgmt.client.AccountManagementClient;
import me.distributedaccounts.mgmt.client.InsufficientMoneyException;
import me.distributedaccounts.search.client.AccountSearchClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConsoleControllerImpl implements ConsoleController {
    private AccountManagementClient accountManagementClient;
    private AccountSearchClient accountSearchClient;

    @Override
    public void readAndProcessConsoleInputs() throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            printPrompt();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equalsIgnoreCase("exit")) {
                    break;

                } else if (!line.trim().equals("")) {
                    String requestOutput = executeRequest(line);
                    System.out.println(">> " + requestOutput);
                }
                printPrompt();
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void printPrompt() {
        System.out.print("Enter command ('help' for instructions): ");
    }

    private String executeRequest(String line) {
        String[] parts = line.split("\\s+");

        switch (parts[0]) {
            case "find":
                return executeFind(parts);
            case "findByDescription":
                return executeFindByDescription(parts);
            case "open":
                return executeOpen(parts);
            case "close":
                return executeClose(parts);
            case "withdrawMoney":
                return executeWithdrawMoney(parts);
            case "depositMoney":
                return executeDepositMoney(parts);
            default:
                return "OK";
        }
    }

    private String executeOpen(String[] parts) {
        UUID uuid = accountManagementClient.openAccount(parts[1]);
        return "Account created: " + uuid;
    }

    private String executeClose(String[] parts) {
        UUID uuid = UUID.fromString(parts[1]);
        accountManagementClient.closeAccount(uuid);
        return "OK";
    }

    private String executeDepositMoney(String[] parts) {
        UUID uuid = UUID.fromString(parts[1]);
        float amount = Float.parseFloat(parts[2]);
        accountManagementClient.depositMoney(uuid, amount);
        return "OK";
    }

    private String executeWithdrawMoney(String[] parts) {
        UUID uuid = UUID.fromString(parts[1]);
        float amount = Float.parseFloat(parts[2]);
        try {
            accountManagementClient.withdrawMoney(uuid, amount);
            return "OK";
        } catch (InsufficientMoneyException e) {
            return "ERROR: Insufficient funds";
        }
    }

    private String executeFind(String[] parts) {
        UUID accountId = UUID.fromString(parts[1]);
        Map<String, Object> accountJsonMap = accountManagementClient.find(accountId);
        return "Account found: " + accountJsonMap.toString();
    }

    private String executeFindByDescription(String[] parts) {
        String description = parts[1];
        List<Map<String,Object>> accounts = accountSearchClient.findByDescription(description);
        return "Accounts found: " + accounts;
    }

    public void setAccountManagementClient(AccountManagementClient accountManagementClient) {
        this.accountManagementClient = accountManagementClient;
    }

    public void setAccountSearchClient(AccountSearchClient accountSearchClient) {
        this.accountSearchClient = accountSearchClient;
    }
}
