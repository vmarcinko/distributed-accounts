<#import "spring.ftl" as spring />

<html>
<body>
<h1>Account</h1>

<#if infoMessage??>
    <table style="background-color: #54e6ff" cellpadding="5">
        <tr>
            <td>${infoMessage}</td>
        </tr>
    </table>
</#if>
<#if errorMessage??>
    <table style="background-color: #cd7c6d" cellpadding="5">
        <tr>
            <td>${infoMessage}</td>
        </tr>
    </table>
</#if>

<ul>
    <li><strong>ID</strong>: ${account.id}</li>
    <li><strong>Balance</strong>: ${account.balance}</li>
    <li><strong>Description</strong>: ${account.description}</li>
</ul>

<h2>Depositing money</h2>
<form action="<@spring.url relativeUrl='/accounts/${account.id}/depositMoney' />" method="post">
    Amount: <input type="text" name="amount"/>
    <p/>
    <input type="submit" value="Deposit"/>
</form>

<h2>Withdrawing money</h2>
<form action="<@spring.url relativeUrl='/accounts/${account.id}/withdrawMoney' />" method="post">
    Amount: <input type="text" name="amount"/>
    <p/>
    <input type="submit" value="Withdraw"/>
</form>

<h2>Closing</h2>
<form action="<@spring.url relativeUrl='/accounts/${account.id}/close' />" method="post">
    <input type="submit" value="Close"/>
</form>

</body>
</html>